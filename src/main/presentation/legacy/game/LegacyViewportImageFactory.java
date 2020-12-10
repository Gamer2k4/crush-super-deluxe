package main.presentation.legacy.game;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import main.data.Data;
import main.data.entities.Arena;
import main.data.entities.Player;
import main.data.entities.Race;
import main.presentation.common.Logger;
import main.presentation.common.image.ImageBuffer;
import main.presentation.common.image.ImageType;
import main.presentation.common.image.ImageUtils;
import main.presentation.game.HighlightIcon;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.legacy.game.sprites.LegacySpriteMovementManager;
import main.presentation.legacy.game.sprites.MovingSprite;
import main.presentation.legacy.game.sprites.PlayerSpriteType;
import main.presentation.legacy.game.sprites.SpriteFactory;
import main.presentation.legacy.game.sprites.TileSpriteType;

public class LegacyViewportImageFactory extends LegacyUiImageFactory
{
	private static LegacyViewportImageFactory instance = null;
	
	private List<HighlightIcon> highlightsToDisplay;
	private List<Point> moveTrack;

	private Map<Player, String> playerState;
	private Map<Player, String> playerFacing;
	
	private LegacySpriteMovementManager spriteMovementManager;

	private LegacyViewportImageFactory()
	{
		playersInGame = new ArrayList<Player>();
		highlightsToDisplay = new ArrayList<HighlightIcon>();
		moveTrack = new ArrayList<Point>();
		playerState = new HashMap<Player, String>();
		playerFacing = new HashMap<Player, String>();
		spriteMovementManager = LegacySpriteMovementManager.getInstance();
	}

	public static LegacyViewportImageFactory getInstance()
	{
		if (instance == null)
			instance = new LegacyViewportImageFactory();

		return instance;
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
	
	public void updatePlayerFacing(Player player, String direction)
	{
		playerFacing.put(player, direction);
	}
	
	public String getPlayerFacing(Player player)
	{
		return playerFacing.get(player);
	}
	
	public void updatePlayerState(Player player, String state)
	{
		playerState.put(player, state);
	}
		
	@Override
	public BufferedImage generateImage(Data gameData, Player gameCurrentPlayer)
	{
		return generateImage(gameData, gameCurrentPlayer, new Rectangle(LegacyUiConstants.MAP_IMAGE_WIDTH, LegacyUiConstants.MAP_IMAGE_HEIGHT));
	}
	
	//TODO: make use of the viewport argument
	public BufferedImage generateImage(Data gameData, Player gameCurrentPlayer, Rectangle viewport)
	{
		data = gameData;
		currentPlayer = gameCurrentPlayer;
		playersInGame.clear();
		
		Arena arena = gameData.getArena();
		ImageBuffer canvas = new ImageBuffer(getArenaBaseImage(arena));
		
		addMapSpritesAndScanPlayers(canvas);
		addCursor(canvas);
		addBall(canvas);
		addPlayersThreadSafe(canvas);
		addHighlightsThreadSafe(canvas);
		
		BufferedImage fullMap = canvas.getCompositeImage();
		
		//TODO: update this to add only the requested layers
		//for now, print all the sprites (rather than just the ones within a certain viewport, and just the ones on a certain layer);
		
		try {
			return fullMap.getSubimage(viewport.x, viewport.y, viewport.width, viewport.height);
		} catch (RasterFormatException rfe)
		{
			Logger.warn("Invalid viewport rectangle: " + viewport);
			return fullMap;
		}
	}

	private void addMapSpritesAndScanPlayers(ImageBuffer canvas)
	{
		Arena arena = data.getArena();
		if (arena == null)
			return;
		
		for (int i = 0; i < 30; i++)
		{
			for (int j = 0; j < 30; j++)
			{
				int tileSheetIndex = convertLegacyTileValueToTileSheetIndex(arena.getLegacyTile(i, j));
				Player player = data.getPlayerAtLocation(i, j);
				
				if (tileSheetIndex != -1)
					canvas.addLayer(getX(j), getY(i), getTileSprite(tileSheetIndex)); //i and j are swapped, since i is row (Y axis), while j is column (X axis)
				
				if (player != null)
					playersInGame.add(player);
			}
		}
	}

	private void addCursor(ImageBuffer canvas)
	{
		Point cursorLocation = data.getLocationOfPlayer(currentPlayer);
		
		if (cursorLocation != null)
			canvas.addLayer(getX(cursorLocation.y), getY(cursorLocation.x), getTileSprite(TileSpriteType.CURSOR)); //swapping from row, column to x, y
	}
	
	private void addBall(ImageBuffer canvas)
	{
		Point ballLocation = data.getBallLocation();
		
		if (data.getBallCarrier() != null)
		{
			ballLocation = data.getLocationOfPlayer(data.getBallCarrier());
			canvas.addLayer(getX(ballLocation.y), getY(ballLocation.x), getTileSprite(TileSpriteType.BALL_HIGHLIGHT)); //swapping from row, column to x, y
		}
		else if (ballLocation.x > -1 && ballLocation.y > -1)
		{
			canvas.addLayer(getX(ballLocation.y), getY(ballLocation.x), getTileSprite(TileSpriteType.BALL)); //swapping from row, column to x, y
		}
	}
	
	private void addPlayersThreadSafe(ImageBuffer canvas)
	{
		Iterator<Player> iter = null;
		
		do {
			try {
				iter = playersInGame.iterator();
		
				while (iter.hasNext()) {
					addPlayerToMapImage(canvas, iter.next());
				}
			} catch (ConcurrentModificationException cme)
			{
				iter = null;
			}
		} while (iter == null);
	}
	
	private void addPlayerToMapImage(ImageBuffer canvas, Player player)
	{
		Point playerLocation = data.getLocationOfPlayer(player);
//		MovingSprite movingSprite = spriteMovementManager.getMovingSprite(player);
		
		Logger.info("Adding player to map image at point: " + playerLocation);
		
		//TODO: null pointer exception when adding injured player
		canvas.addLayer(getX(playerLocation.y), getY(playerLocation.x), getPlayerSprite(player)); //swapping from row, column to x, y
		
		if (player.status == Player.STS_STUN)
			canvas.addLayer(getX(playerLocation.y), getY(playerLocation.x), getTileSprite(TileSpriteType.STUN_STARS));
	}
	
	private void addHighlightsThreadSafe(ImageBuffer canvas)
	{
		Iterator<HighlightIcon> iter = null;
		int currentMoveLocationIndex = 0;
		
		do {
			try {
				iter = highlightsToDisplay.iterator();
		
				while (iter.hasNext()) {
					currentMoveLocationIndex = addHighlightToMapImage(canvas, iter.next(), currentMoveLocationIndex);
				}
			} catch (ConcurrentModificationException cme)
			{
				iter = null;
			}
		} while (iter == null);
	}
	
	private int addHighlightToMapImage(ImageBuffer canvas, HighlightIcon icon, int currentMoveLocationIndex)
	{
		TileSpriteType highlightSprite = TileSpriteType.UNDEFINED_TYPE;
		
		//TODO: determine how to find the origin, determine which point is the destination,
		//      and make the arrow directions based on the next point, not the last one (somehow)
		if (icon.isInvalidMoveTarget())
		{
			highlightSprite = getMoveHighlightSprite("RED", currentMoveLocationIndex, icon.x, icon.y);
			currentMoveLocationIndex++;
		}
		else if (icon.isValidMoveTarget())
		{
			highlightSprite = getMoveHighlightSprite("GREEN", currentMoveLocationIndex, icon.x, icon.y);
			currentMoveLocationIndex++;
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
		
		canvas.addLayer(getX(icon.y), getY(icon.x), getTileSprite(highlightSprite));
		return currentMoveLocationIndex;
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

	private int convertLegacyTileValueToTileSheetIndex(int legacyTileValue)
	{
		if (legacyTileValue == 00 || legacyTileValue == 40)
			return -1;
		
		return legacyTileValue - 1;
	}
	
	private BufferedImage getTileSprite(int tileIndex)
	{
		return SpriteFactory.getInstance().getTileSprite(TileSpriteType.getTileSpriteType(tileIndex)).getCurrentFrame();
	}
	
	private BufferedImage getTileSprite(TileSpriteType tileType)
	{
		return SpriteFactory.getInstance().getTileSprite(tileType).getCurrentFrame();
	}
	
	private BufferedImage getPlayerSprite(Player player)
	{
		int teamIndex = data.getTeamIndexOfPlayer(player);
		
		String state = playerState.get(player);
		String facing = playerFacing.get(player);
		
		if (state == null && facing == null)
		{
			Logger.warn("Incomplete player definition to generate sprite! State: " + state + ", Facing: " + facing + ", Player: " + player);
			return ImageUtils.createBlankBufferedImage(new Dimension(1, 1));
		}
		
		String spriteType = state.toUpperCase() + "_" + facing.toUpperCase();
		return getPlayerSprite(player, PlayerSpriteType.valueOf(spriteType), teamIndex);
	}
	
	private BufferedImage getPlayerSprite(Player player, PlayerSpriteType spriteType, int teamIndex)
	{
		try {
			return SpriteFactory.getInstance().getPlayerSprite(player.getRace(), spriteType, teamIndex).getCurrentFrame();
		} catch (Exception e)
		{
			return ImageUtils.createBlankBufferedImage(new Dimension(1, 1));
		}
	}
	
	private BufferedImage getArenaBaseImage(Arena arena)
	{
		if (arena == null)
			return new BufferedImage(1, 1, 1);
		
		switch (arena.getIndex())
		{
		case 0:
			return imageFactory.getImage(ImageType.MAP_A1);
		case 1:
			return imageFactory.getImage(ImageType.MAP_A2);
		case 2:
			return imageFactory.getImage(ImageType.MAP_A3);
		case 3:
			return imageFactory.getImage(ImageType.MAP_A4);
		case 4:
			return imageFactory.getImage(ImageType.MAP_B1);
		case 5:
			return imageFactory.getImage(ImageType.MAP_B2);
		case 6:
			return imageFactory.getImage(ImageType.MAP_B3);
		case 7:
			return imageFactory.getImage(ImageType.MAP_B4);
		case 8:
			return imageFactory.getImage(ImageType.MAP_C1);
		case 9:
			return imageFactory.getImage(ImageType.MAP_C2);
		case 10:
			return imageFactory.getImage(ImageType.MAP_C3);
		case 11:
			return imageFactory.getImage(ImageType.MAP_C4);
		case 12:
			return imageFactory.getImage(ImageType.MAP_D1);
		case 13:
			return imageFactory.getImage(ImageType.MAP_D2);
		case 14:
			return imageFactory.getImage(ImageType.MAP_D3);
		case 15:
			return imageFactory.getImage(ImageType.MAP_D4);
		case 16:
			return imageFactory.getImage(ImageType.MAP_E1);
		case 17:
			return imageFactory.getImage(ImageType.MAP_E2);
		case 18:
			return imageFactory.getImage(ImageType.MAP_E3);
		case 19:
			return imageFactory.getImage(ImageType.MAP_E4);
		}
		
		throw new IllegalArgumentException("No map image defined for arena " + arena.getIndex());
	}
	
	private int getX(int x)
	{
		return 36 + 36 * x;
	}
	
	private int getY(int y)
	{
		return 30 + 30 * y;
	}
}
