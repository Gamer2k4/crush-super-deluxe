package main.presentation.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import main.data.Data;
import main.data.entities.Arena;
import main.data.entities.Player;
import main.presentation.common.Logger;
import main.presentation.game.sprite.CrushArenaImageManager;
import main.presentation.game.sprite.CrushBallSprite;
import main.presentation.game.sprite.CrushPlayerSprite;
import main.presentation.game.sprite.CrushSprite;
import main.presentation.game.sprite.CrushTile;
import main.presentation.game.sprite.Facing;
import main.presentation.game.sprite.TileSpriteType;

public class EventTextureFactory
{
	private static EventTextureFactory instance = null;
	
	public static final Point OFFSCREEN_COORDS = new Point(-10, -10);
	
	private Data data;
	
	private Map<Point, CrushSprite> tileSpritesAtArenaLocation = new HashMap<Point, CrushSprite>();
	private Map<Point, CrushPlayerSprite> playerSpritesAtArenaLocation = new HashMap<Point, CrushPlayerSprite>();
	private Map<Player, CrushPlayerSprite> playerSpritesByPlayer = new HashMap<Player, CrushPlayerSprite>();
	private List<CrushSprite> elevatedSprites = new ArrayList<CrushSprite>();
	
	private List<HighlightIcon> highlightsToDisplay = new ArrayList<HighlightIcon>();
	private List<Point> moveTrack = new ArrayList<Point>();
	
	private CrushSprite cursorSprite = CrushTile.createTile(OFFSCREEN_COORDS, TileSpriteType.CURSOR);
	private CrushSprite ballHighlight = CrushTile.createTile(OFFSCREEN_COORDS, TileSpriteType.BALL_HIGHLIGHT);
	private CrushBallSprite ballOnField = new CrushBallSprite();

	private EventTextureFactory() {}
	
	public static EventTextureFactory getInstance()
	{
		if (instance == null)
			instance = new EventTextureFactory();

		return instance;
	}

	public void beginGame(Data clientData)
	{
		data = clientData;
		CrushTile.setHomeTeam(data.getTeam(0));
		refreshTileSprites();
		generatePlayerSprites();
	}
	
	public void endGame()
	{
		tileSpritesAtArenaLocation.clear();
		playerSpritesAtArenaLocation.clear();
		playerSpritesByPlayer.clear();
		elevatedSprites.clear();
		highlightsToDisplay.clear();
		moveTrack.clear();
	}

	public void setMoveTrack(List<Point> movePossibilities)
	{
		clearMoveTrack();
		moveTrack.addAll(movePossibilities);
	}
	
	public void clearMoveTrack()
	{
		moveTrack.clear();
	}
	
	public void setHighlights(List<HighlightIcon> highlights)
	{
		clearHighlights();
		highlightsToDisplay.addAll(highlights);
	}
	
	public void clearHighlights()
	{
		highlightsToDisplay.clear();
	}
	
	public List<CrushSprite> getArenaSprite()
	{
		List<CrushSprite> arenaSprites = new ArrayList<CrushSprite>();
		
//		CrushSprite arenaSprite = CrushArenaImageManager.getInstance().createArena(data.getArena());		//saves loading time by not updating field colors
		CrushSprite arenaSprite = CrushArenaImageManager.getInstance().getArenaForHomeTeam(data.getArena().getIndex(), data.getTeam(0));
		
		if (arenaSprite != null)
			arenaSprites.add(arenaSprite);
		
		return arenaSprites;
	}
	
	public void refreshTileSprites()
	{
		tileSpritesAtArenaLocation.clear();
		
		if (data == null)
			return;
		
		Arena arena = data.getArena();
		if (arena == null)
			return;
		
		for (int i = -1; i < 31; i++)
		{
			for (int j = -1; j < 31; j++)
			{
				int tileSheetIndex = -1;
				
				if (i == -1 || j == -1 || i == Arena.ARENA_DIMENSIONS || j == Arena.ARENA_DIMENSIONS)
					tileSheetIndex = getBorderTile(i, j);
				else
					tileSheetIndex = convertLegacyTileValueToTileSheetIndex(arena.getLegacyTile(i, j));

				if (tileSheetIndex == -1)
					continue;

				CrushTile tileSprite = CrushTile.createTile(OFFSCREEN_COORDS, tileSheetIndex);
				Point coords = new Point(i, j);
				tileSprite.setArenaPosition(coords);
				tileSpritesAtArenaLocation.put(coords, tileSprite);
			}
		}	
	}

	@Deprecated
	public List<CrushSprite> getTileSprites_notConcurrentSafe()
	{
		List<CrushSprite> tileSpriteList = new ArrayList<CrushSprite>();
		
		for (CrushSprite sprite : tileSpritesAtArenaLocation.values())
		{
			tileSpriteList.add(sprite);
		}
		
		return tileSpriteList;
	}
	
	public List<CrushSprite> getTileSprites()
	{	
		List<CrushSprite> tileSpriteList = new ArrayList<CrushSprite>();
		
		Iterator<CrushSprite> iter = null;
		
		do {
			try {
				iter = tileSpritesAtArenaLocation.values().iterator();
		
				while (iter.hasNext()) {
					tileSpriteList.add(iter.next());
				}
			} catch (ConcurrentModificationException cme)
			{
				iter = null;
			}
		} while (iter == null);
		
		return tileSpriteList;
	}

	public List<CrushSprite> getBallSprite()
	{
		List<CrushSprite> ballSprite = new ArrayList<CrushSprite>();
		
		Point ballArenaLocation = data.getBallLocation();
		
		//if it's not on the field, it just never gets added to the list to return
		//TODO:	it's not this simple for when the BALL is scurrying around, but this is good for now
		if (ballArenaLocation.x != -1 && ballArenaLocation.y != -1)
		{
			ballOnField.setArenaPosition(ballArenaLocation);
			ballSprite.add(ballOnField);
		}
		
		return ballSprite;
	}
	
	public List<CrushSprite> getCursorSprite(Player currentPlayer)
	{
		List<CrushSprite> cursorSpriteList = new ArrayList<CrushSprite>();
		
		CrushPlayerSprite currentSprite = playerSpritesByPlayer.get(currentPlayer);
		
		if (currentSprite == null)
			return cursorSpriteList;
		
		Point cursorLocation = new Point(currentSprite.getX(), currentSprite.getY());
		
		cursorSprite.setCoords(cursorLocation);
		cursorSpriteList.add(cursorSprite);
		
		return cursorSpriteList;
	}
	
	public List<CrushSprite> getBallCarrierIndicator()
	{
		List<CrushSprite> ballSprite = new ArrayList<CrushSprite>();
		
		Player ballCarrier = data.getBallCarrier();
		
		if (ballCarrier == null)
			return ballSprite;
		
		Point ballCarrierCoords = getSpriteCoordsOfPlayer(ballCarrier);
		CrushPlayerSprite ballCarrierSprite = getPlayerSpriteAtCoords(ballCarrierCoords);
		
		if (ballCarrierSprite == null)
		{
			Logger.warn("No sprite available to mark with ball indicator!");
			return ballSprite;
		}
		
		Point ballCoords = new Point(ballCarrierSprite.getX(), ballCarrierSprite.getY());
		ballHighlight.setCoords(ballCoords);
		ballSprite.add(ballHighlight);
			
		return ballSprite; 
	}

	public void generatePlayerSprites()
	{
		List<Player> players = data.getAllPlayers();
		
		for (Player player : players)
		{
			//I think this can happen if a team is incomplete when a game begins, which is fine - probably not even a warning, but it's fine for now
			if (player == null)
			{
				Logger.warn("Null player passed in when generating sprites, skipping to next player.");
				continue;
			}
			
			CrushPlayerSprite sprite = new CrushPlayerSprite(data.getTeamOfPlayer(player), player.getRace());
			sprite.setArenaPosition(OFFSCREEN_COORDS);
			playerSpritesAtArenaLocation.put(OFFSCREEN_COORDS, sprite);
			playerSpritesByPlayer.put(player, sprite);
		}
	}
	
	@Deprecated
	public List<CrushSprite> getPlayerSprites_notConcurrentSafe()
	{
		List<CrushSprite> playerSpriteList = new ArrayList<CrushSprite>();
		
		for (Point coords : playerSpritesAtArenaLocation.keySet())
		{
			if (coords.x < 0 || coords.y < 0 || coords.x >= 30 || coords.y >= 30)
				continue;
			
			CrushPlayerSprite playerSprite = playerSpritesAtArenaLocation.get(coords);
			
			if (playerSprite != null)
				playerSpriteList.add(playerSprite);
		}
		
		return playerSpriteList;
	}
	
	public List<CrushSprite> getPlayerSprites()
	{
		List<CrushSprite> playerSpriteList = new ArrayList<CrushSprite>();
		
		Iterator<Point> iter = null;
		
		do {
			try {
				iter = playerSpritesAtArenaLocation.keySet().iterator();
		
				while (iter.hasNext()) {
					Point coords = iter.next();
					
					if (coords.x < 0 || coords.y < 0 || coords.x >= 30 || coords.y >= 30)
						continue;
					
					CrushPlayerSprite playerSprite = playerSpritesAtArenaLocation.get(coords);
					
					if (playerSprite != null)
						playerSpriteList.add(playerSprite);
				}
			} catch (ConcurrentModificationException cme)
			{
				iter = null;
			}
		} while (iter == null);
		
		return playerSpriteList;
	}

	public List<CrushSprite> getTileHighlightSprites()
	{
		List<CrushSprite> highlightList = new ArrayList<CrushSprite>();
		
		int currentMoveLocationIndex = 0;
		
		for (HighlightIcon icon : highlightsToDisplay)
		{
			currentMoveLocationIndex = addHighlightToMapImage(highlightList, icon, currentMoveLocationIndex);
		}
		
		return highlightList;
	}
	
	private int addHighlightToMapImage(List<CrushSprite> highlightList, HighlightIcon icon, int currentMoveLocationIndex)
	{
		TileSpriteType highlightSprite = TileSpriteType.UNDEFINED_TYPE;
		int index = currentMoveLocationIndex;
		
		//TODO: determine how to find the origin, determine which point is the destination,
		//      and make the arrow directions based on the next point, not the last one (somehow)
		if (icon.isInvalidMoveTarget())
		{
			highlightSprite = getMoveHighlightSprite("RED", currentMoveLocationIndex, icon.x, icon.y);
			index++;
		}
		else if (icon.isValidMoveTarget())
		{
			highlightSprite = getMoveHighlightSprite("GREEN", currentMoveLocationIndex, icon.x, icon.y);
			index++;
		}
		else if (icon.isBadCheckTarget())
			highlightSprite = TileSpriteType.CHECK_RED;
		else if (icon.isEvenCheckTarget())
			highlightSprite = TileSpriteType.CHECK_YELLOW;
		else if (icon.isGoodCheckTarget())
			highlightSprite = TileSpriteType.CHECK_GREEN;
		else if (icon.isHandoffTarget())
			highlightSprite = TileSpriteType.HANDOFF;
		else if (icon.isJumpTarget())
			highlightSprite = TileSpriteType.JUMP;
		
		CrushTile highlightTile = CrushTile.createTile(OFFSCREEN_COORDS, highlightSprite);
		highlightTile.setArenaPosition(icon.x, icon.y);
		highlightList.add(highlightTile);
		
		return index;
	}
	
	private TileSpriteType getMoveHighlightSprite(String color, int currentMoveLocationIndex, int x, int y)
	{
		//TODO: this should only happen if the move track is cleared before the highlights, which seems to be happening - no big deal now, but
		//		there are occasionally flashes of slithgas as a result
		if (moveTrack.isEmpty())
			return TileSpriteType.UNDEFINED_TYPE;
		
		if (currentMoveLocationIndex == moveTrack.size() - 1)
			return TileSpriteType.valueOf("WALK_TARGET_" + color);
		
		Point nextMove;
		
		try {
			nextMove = moveTrack.get(currentMoveLocationIndex + 1);
		} catch (IndexOutOfBoundsException ioobe)
		{
			Logger.error("Index out of bounds exception when drawing move path!");
			return TileSpriteType.UNDEFINED_TYPE;
		}
		
		String direction = "";
		
		if (x > nextMove.x) direction = direction + "N";
		if (x < nextMove.x) direction = direction + "S";
		if (y > nextMove.y) direction = direction + "W";
		if (y < nextMove.y) direction = direction + "E";
		
		return TileSpriteType.valueOf("WALK_" + direction + "_" + color);
	}
	
	public List<CrushSprite> getHoveringSprites()
	{
		//TODO: this is stun stars, vortex/repulsor, terror, backfire, etc.
		
		List<CrushSprite> hoveringSprites = new ArrayList<CrushSprite>();
		
		for (Player player : data.getAllPlayers())
		{
			//I think this can happen if a team is incomplete when a game begins, which is fine - probably not even a warning, but it's fine for now
			if (player == null)
			{
//				Logger.warn("Null player passed in when generating hovering sprites, skipping to next player.");
				continue;
			}
			
			if (player.status == Player.STS_STUN_DOWN || player.status == Player.STS_STUN_SIT)
			{
				CrushTile stunStars = CrushTile.createTile(OFFSCREEN_COORDS, TileSpriteType.STUN_STARS);
				Point coords = data.getLocationOfPlayer(player);
				
				if (coords == null)
				{
					Logger.warn("getHoveringSprites() - Player " + player.name + " has no location.");
					continue;
				}
				
				stunStars.setArenaPosition(coords);
				hoveringSprites.add(stunStars);
			}
		}
		
		return hoveringSprites;
	}

	public List<CrushSprite> getElevatedSprites()
	{
		//TODO: this will be cleared before the other sprites are generated and filled as they are
		//		it will include jumping players, checking players (so they always overlap the defender) thrown balls, and cameras
		return elevatedSprites;
	}
	
	@Deprecated
	public Point getSpriteCoordsOfPlayer_notConcurrentSafe(Player player)
	{
		if (player == null)
		{
			Logger.warn("Null player passed in for EventTextureFactory.getSpriteCoordsOfPlayer(); returning " + OFFSCREEN_COORDS);
			return OFFSCREEN_COORDS;
		}
		
		CrushPlayerSprite playerSprite = playerSpritesByPlayer.get(player);
		
		if (playerSprite == null)
			throw new IllegalArgumentException("Could not get sprite arena coordinates for player [" + player + "]; no sprite was found");
		
		//can throw concurrentModificationException
		for (Point key : playerSpritesAtArenaLocation.keySet())
		{
			CrushPlayerSprite spriteToCheck = playerSpritesAtArenaLocation.get(key);
			
			if (spriteToCheck == playerSprite)	//yes, we want actual object equality here, not just identical values
				return key;
		}
		
		return OFFSCREEN_COORDS;
	}
	
	public Point getSpriteCoordsOfPlayer(Player player)
	{
		if (player == null)
		{
			Logger.warn("Null player passed in for EventTextureFactory.getSpriteCoordsOfPlayer(); returning " + OFFSCREEN_COORDS);
			return OFFSCREEN_COORDS;
		}
		
		CrushPlayerSprite playerSprite = playerSpritesByPlayer.get(player);
		
		if (playerSprite == null)
		{
			Logger.warn("Could not get sprite arena coordinates for player [" + player + "]; no sprite was found. Returning " + OFFSCREEN_COORDS);
			return OFFSCREEN_COORDS;
		}
		
		Iterator<Point> iter = null;
		
		do {
			try {
				iter = playerSpritesAtArenaLocation.keySet().iterator();
		
				while (iter.hasNext()) {
					Point key = iter.next();
					CrushPlayerSprite spriteToCheck = playerSpritesAtArenaLocation.get(key);
					
					if (spriteToCheck == playerSprite)	//yes, we want actual object equality here, not just identical values
						return key;
				}
			} catch (ConcurrentModificationException cme)
			{
				iter = null;
			}
		} while (iter == null);
		
		return OFFSCREEN_COORDS;
	}
	
	public CrushPlayerSprite getPlayerSpriteAtCoords(int row, int col)
	{
		return getPlayerSpriteAtCoords(new Point(row, col));
	}
	
	public CrushPlayerSprite getPlayerSpriteAtCoords(Point arenaCoords)
	{
		if (arenaCoords == null)
			return null;
		
		return playerSpritesAtArenaLocation.get(arenaCoords);
	}
	
	public CrushPlayerSprite getPlayerSprite(Player player)
	{
		return playerSpritesByPlayer.get(player);
	}
	
	public void updatePlayerSpriteCoords(CrushPlayerSprite player, Point newArenaCoords)
	{
		for (Player key : playerSpritesByPlayer.keySet())
		{
			CrushPlayerSprite spriteToCheck = playerSpritesByPlayer.get(key);
			
			if (spriteToCheck == player)	//yes, we want actual object equality here, not just identical values
			{
				updatePlayerSpriteCoords(key, newArenaCoords);
				return;
			}
		}
	}
	
	public void updatePlayerSpriteCoords(Player player, Point newArenaCoords)
	{
		if (player == null)
		{
			Logger.warn("Null player passed in for EventTextureFactory.updatePlayerSpriteCoords()");
			return;
		}
		
		CrushPlayerSprite playerSprite = playerSpritesByPlayer.get(player);
		
		if (playerSprite == null)
		{
			Logger.warn("Could not update sprite arena coordinates for player [" + player + "]; no sprite was found");
			return;
		}
		
		removePlayerSpriteFromCoordsMap(playerSprite);
		playerSprite.setArenaPosition(newArenaCoords);
		playerSpritesAtArenaLocation.put(newArenaCoords, playerSprite);
	}
	
	public void elevateSprite(CrushPlayerSprite sprite)
	{
		if (elevatedSprites.contains(sprite))
			return;
		
		removePlayerSpriteFromCoordsMap(sprite);
		elevatedSprites.add(sprite);
	}
	
	public void lowerSprite(CrushPlayerSprite sprite)
	{
		if (!elevatedSprites.contains(sprite))
			return;
		
		elevatedSprites.remove(sprite);
		playerSpritesAtArenaLocation.put(sprite.getArenaPosition(), sprite);
	}
	
	@Deprecated
	private boolean removePlayerSpriteFromCoordsMap_notConcurrentSafe(CrushPlayerSprite spriteToRemove)
	{
		Point locationOfSpriteToRemove = null;
		
		//the player sprite may not be in the coordinations map if it's offscreen; however, it if is there, remove it so it can be re-added.
		for (Point key : playerSpritesAtArenaLocation.keySet())
		{
			CrushPlayerSprite spriteToCheck = playerSpritesAtArenaLocation.get(key);
			
			if (spriteToCheck == spriteToRemove)	//yes, we want actual object equality here, not just identical values
			{
				locationOfSpriteToRemove = key;
				break;
			}
		}
		
		//done here to avoid concurrent modification exception
		if (locationOfSpriteToRemove == null)
			return false;
		
		playerSpritesAtArenaLocation.remove(locationOfSpriteToRemove);
		return true;
	}
	
	private boolean removePlayerSpriteFromCoordsMap(CrushPlayerSprite spriteToRemove)
	{
		Point locationOfSpriteToRemove = null;
		Iterator<Point> iter = null;
		
		//the player sprite may not be in the coordinations map if it's offscreen; however, it if is there, remove it so it can be re-added.
		do {
			try {
				iter = playerSpritesAtArenaLocation.keySet().iterator();
				
				while (iter.hasNext()) {
					Point key = iter.next();
					
					CrushPlayerSprite spriteToCheck = playerSpritesAtArenaLocation.get(key);
					
					if (spriteToCheck == spriteToRemove)	//yes, we want actual object equality here, not just identical values
					{
						locationOfSpriteToRemove = key;
						break;
					}
				}
			} catch (ConcurrentModificationException cme)
			{
				iter = null;
			}
		} while (iter == null);
		
		if (locationOfSpriteToRemove == null)
			return false;
		
		playerSpritesAtArenaLocation.remove(locationOfSpriteToRemove);
		return true;
	}	
	
	public Facing getBinSpriteFacing(Point coords)
	{
		int tileSheetIndex = convertLegacyTileValueToTileSheetIndex(data.getArena().getLegacyTile(coords.x, coords.y));
		String tileSpriteTypeName = TileSpriteType.getTileSpriteType(tileSheetIndex).name();
		
		if (tileSpriteTypeName.startsWith("BIN_"))
			return Facing.valueOf(tileSpriteTypeName.substring(4, 5));
		
		return null;
	}
	
	public void refreshPlayerSpriteCoords(CrushPlayerSprite player)
	{
		updatePlayerSpriteCoords(player, player.getArenaPosition());
	}
	
	private int convertLegacyTileValueToTileSheetIndex(int legacyTileValue)
	{
		//													  red NW arrow			   red SW arrow			    red W arrow
		if (legacyTileValue == 00 || legacyTileValue == 40 || legacyTileValue == 20 || legacyTileValue == 26 || legacyTileValue == 27)
			return -1;
		
		return legacyTileValue - 1;
	}
	
	//TODO: note that these are untinted, so they'll look off for green arenas
	private int getBorderTile(int row, int col)
	{
		if (row == -1 && col == -1)
			return TileSpriteType.BORDER_NW.getIndex();
		if (row == 30 && col == -1)
			return TileSpriteType.BORDER_SW.getIndex();
		if (row == -1 && col == 30)
			return TileSpriteType.BORDER_NE.getIndex();
		if (row == 30 && col == 30)
			return TileSpriteType.BORDER_SE.getIndex();

		if (row == -1)
			return TileSpriteType.BORDER_N.getIndex();
		if (row == 30)
			return TileSpriteType.BORDER_S.getIndex();
		if (col == -1)
			return TileSpriteType.BORDER_W.getIndex();
		if (col == 30)
			return TileSpriteType.BORDER_E.getIndex();
		
		return TileSpriteType.UNDEFINED_TYPE.getIndex();
	}
}
