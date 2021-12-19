package main.presentation.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.data.Data;
import main.data.Event;
import main.data.entities.Arena;
import main.data.entities.Player;
import main.presentation.common.Logger;
import main.presentation.game.sprite.CrushArena;
import main.presentation.game.sprite.CrushPlayerSprite;
import main.presentation.game.sprite.CrushSprite;
import main.presentation.game.sprite.CrushTile;
import main.presentation.game.sprite.TileSpriteType;

public class EventTextureFactory
{
	private static EventTextureFactory instance = null;
	
	public static final Point OFFSCREEN_COORDS = new Point(-10, -10);
	
	private Data data;
	
	private Map<Point, CrushSprite> tileSprites = new HashMap<Point, CrushSprite>();
	private Map<Point, CrushPlayerSprite> playerSprites = new HashMap<Point, CrushPlayerSprite>();
	private Map<Player, CrushPlayerSprite> playerSpriteMap = new HashMap<Player, CrushPlayerSprite>();
	private List<CrushSprite> elevatedSprites = new ArrayList<CrushSprite>();
	
	private List<HighlightIcon> highlightsToDisplay = new ArrayList<HighlightIcon>();
	private List<Point> moveTrack = new ArrayList<Point>();
	
	private CrushSprite cursorSprite = CrushTile.createTile(OFFSCREEN_COORDS, TileSpriteType.CURSOR);

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
		generateTileSprites();
		generatePlayerSprites();
	}
	
	//TODO: I wonder if I can pop up ejection alerts from here as well?
	//		This is for sliding camera textures, not static ones, but maybe I can call another factory for them.
	public List<CrushPlayerSprite> animateEvent(Event event)
	{
		Player player = data.getPlayer(event.flags[0]);
		CrushPlayerSprite playerSprite = null;
		
		List<CrushPlayerSprite> activeSprites = new ArrayList<CrushPlayerSprite>();
		
		if (player != null)
			playerSprite = playerSpriteMap.get(player);
		
		if (event.getType() == Event.EVENT_MOVE)
		{
			playerSprite.walk(event);
			activeSprites.add(playerSprite);
			//TODO: somehow we need to update the sprite location map once this concludes, because i think they're still
			//		treated as their origin location (so they can be teleported away after moving from the portal)
			//TODO: also, the cursor needs to follow the player as he moves
//			updatePlayerSpriteCoords(playerSprite, new Point(event.flags[2], event.flags[3]));
		}
		
		return activeSprites;
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
		
		CrushSprite arenaSprite = CrushArena.createArena(data.getArena());
		
		if (arenaSprite != null)
			arenaSprites.add(arenaSprite);
		
		return arenaSprites;
	}
	
	public void generateTileSprites()
	{
		Arena arena = data.getArena();
		if (arena == null)
			return;
		
		for (int i = 0; i < 30; i++)
		{
			for (int j = 0; j < 30; j++)
			{
				int tileSheetIndex = convertLegacyTileValueToTileSheetIndex(arena.getLegacyTile(i, j));
				
				if (tileSheetIndex != -1)
				{
					CrushTile tileSprite = CrushTile.createTile(OFFSCREEN_COORDS, tileSheetIndex);
					Point coords = new Point(i, j);
					tileSprite.setArenaPosition(coords);
					tileSprites.put(coords, tileSprite);
				}
			}
		}	
	}
	
	public List<CrushSprite> getTileSprites()
	{
		List<CrushSprite> tileSpriteList = new ArrayList<CrushSprite>();
		
		for (CrushSprite sprite : tileSprites.values())
		{
			tileSpriteList.add(sprite);
		}
		
		return tileSpriteList;
	}
	
	public List<CrushSprite> getCursorSprite(Player currentPlayer)
	{
		List<CrushSprite> cursorSpriteList = new ArrayList<CrushSprite>();
		
		if (currentPlayer == null)
			return cursorSpriteList;
		
		CrushPlayerSprite currentSprite = playerSpriteMap.get(currentPlayer); 
		Point cursorLocation = new Point(currentSprite.getX(), currentSprite.getY());
		
		cursorSprite.setCoords(cursorLocation);
		cursorSpriteList.add(cursorSprite);
		
		return cursorSpriteList;
	}
	
	public void generatePlayerSprites()
	{
		List<Player> players = data.getAllPlayers();
		
		for (Player player : players)
		{
			CrushPlayerSprite sprite = new CrushPlayerSprite(data.getTeamOfPlayer(player), player.getRace());
			sprite.setArenaPosition(OFFSCREEN_COORDS);
			playerSprites.put(OFFSCREEN_COORDS, sprite);
			playerSpriteMap.put(player, sprite);
		}
	}
	
	public List<CrushSprite> getPlayerSprites()
	{
		List<CrushSprite> playerSpriteList = new ArrayList<CrushSprite>();
		
		for (Point coords : playerSprites.keySet())
		{
			if (coords.x < 0 || coords.y < 0 || coords.x >= 30 || coords.y >= 30)
				continue;
			
			CrushPlayerSprite playerSprite = playerSprites.get(coords);
			
			if (playerSprite != null)
				playerSpriteList.add(playerSprite);
		}
		
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
	
	//TODO: this might be managed by GdxGui and its activeOverlayAnimations list
	public List<CrushSprite> getHoveringSprites()
	{
		List<CrushSprite> hoveringSprites = new ArrayList<CrushSprite>();
		
		//TODO: this is stun stars, vortex/repulsor, terror, backfire, etc.
		
		return hoveringSprites;
	}

	public List<CrushSprite> getElevatedSprites()
	{
		//TODO: this will be cleared before the other sprites are generated and filled as they are
		//		it will include jumping players, thrown balls, and cameras
		return elevatedSprites;
	}
	
	public Point getSpriteCoordsOfPlayer(Player player)
	{
		if (player == null)
		{
			Logger.warn("Null player passed in for EventTextureFactory.getSpriteCoordsOfPlayer(); returning " + OFFSCREEN_COORDS);
			return OFFSCREEN_COORDS;
		}
		
		CrushPlayerSprite playerSprite = playerSpriteMap.get(player);
		
		if (playerSprite == null)
			throw new IllegalArgumentException("Could not get sprite arena coordinates for player [" + player + "]; no sprite was found");
		
		for (Point key : playerSprites.keySet())
		{
			CrushPlayerSprite spriteToCheck = playerSprites.get(key);
			
			if (spriteToCheck == playerSprite)	//yes, we want actual object equality here, not just identical values
				return key;
		}
		
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
		
		return playerSprites.get(arenaCoords);
	}
	
	public void updatePlayerSpriteCoords(CrushPlayerSprite player, Point newArenaCoords)
	{
		for (Player key : playerSpriteMap.keySet())
		{
			CrushPlayerSprite spriteToCheck = playerSpriteMap.get(key);
			
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
		
		CrushPlayerSprite playerSprite = playerSpriteMap.get(player);
		
		if (playerSprite == null)
			throw new IllegalArgumentException("Could not update sprite arena coordinates for player [" + player + "]; no sprite was found");
		
		//the player sprite may not be in the coordinations map if it's offscreen; however, it if is there, remove it so it can be re-added.
		for (Point key : playerSprites.keySet())
		{
			CrushPlayerSprite spriteToCheck = playerSprites.get(key);
			
			if (spriteToCheck == playerSprite)	//yes, we want actual object equality here, not just identical values
			{
				playerSprites.remove(key);
				break;
			}
		}
		
		playerSprite.setArenaPosition(newArenaCoords);
		playerSprites.put(newArenaCoords, playerSprite);
	}
	
	public void refreshPlayerSpriteCoords(CrushPlayerSprite player)
	{
		updatePlayerSpriteCoords(player, player.getArenaPosition());
	}
	
	private int convertLegacyTileValueToTileSheetIndex(int legacyTileValue)
	{
		if (legacyTileValue == 00 || legacyTileValue == 40)
			return -1;
		
		return legacyTileValue - 1;
	}
}
