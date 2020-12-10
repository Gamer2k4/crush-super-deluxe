package main.presentation.legacy.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import main.data.Data;
import main.data.entities.Arena;
import main.data.entities.Player;
import main.presentation.common.Logger;
import main.presentation.common.image.ArenaImageGenerator;
import main.presentation.common.image.ImageBuffer;
import main.presentation.common.image.ImageType;
import main.presentation.common.image.ImageUtils;
import main.presentation.game.Action;
import main.presentation.legacy.common.FontType;
import main.presentation.legacy.common.LegacyPlayerTextFactory;
import main.presentation.legacy.common.LegacyTextElement;
import main.presentation.legacy.common.LegacyUiConstants;

public class LegacyButtonBarImageFactory extends LegacyUiImageFactory
{
	private static final int ARENA_SIDE_LENGTH = 30;
	private static final int TILE_SIZE = 2;

	private static LegacyButtonBarImageFactory instance = null;
	
	private Action currentAction = Action.ACTION_NONE;

//	private BufferedImage minimapBaseImage = null;
	private BufferedImage arenaName = null;
	private BufferedImage[] teamNames = {null, null, null};
	
	private LegacyButtonBarImageFactory()
	{
		playersInGame = new ArrayList<Player>();
	}

	public static LegacyButtonBarImageFactory getInstance()
	{
		if (instance == null)
			instance = new LegacyButtonBarImageFactory();

		return instance;
	}
	
	public void setCurrentAction(Action action)
	{
		this.currentAction = action;
	}

	@Override
	public BufferedImage generateImage(Data gameData, Player gameCurrentPlayer)
	{
		//TODO: probably should be a method in dataImpl for isGameStarted() that returns true once all important attributes are set, but this is fine for now
		if (gameData.getArena() == null)
			return ImageUtils.createBlankBufferedImage(new Dimension(1, 1));
		
		data = gameData;
		currentPlayer = gameCurrentPlayer;
		generateNames();

		BufferedImage playerStatuses = generatePlayerStatuses();
		BufferedImage playerAttributes = generateCurrentPlayerAttributes();
		BufferedImage minimap = generateMinimap();

		// be sure to generate all the sub-images before this part, since they'll each use the image buffer
		ImageBuffer canvas = new ImageBuffer(imageFactory.getImage(ImageType.GAME_BUTTONBAR));
		depressActionButtons(canvas);

		// add active player information
		canvas.addLayer(LegacyUiConstants.PLAYER_ATTRIBUTE_X_START, LegacyUiConstants.PLAYER_ATTRIBUTE_Y_START, playerAttributes);
		canvas.addLayer(LegacyUiConstants.PLAYER_STATUS_X_START, LegacyUiConstants.PLAYER_STATUS_Y_START, playerStatuses);

		// add a grey border around the map, as in the original game
		canvas.addLayer(LegacyUiConstants.MINIMAP_X_START - 2, LegacyUiConstants.MINIMAP_Y_START - 2,
				ImageUtils.createBlankBufferedImage(new Dimension(64, 64), LegacyUiConstants.COLOR_LEGACY_BLUE_GREY));
		if (minimap != null)
		{
			canvas.addLayer(LegacyUiConstants.MINIMAP_X_START, LegacyUiConstants.MINIMAP_Y_START, minimap);
			canvas.addLayer(LegacyUiConstants.MINIMAP_X_START - 9, LegacyUiConstants.MAPNAME_Y_START, arenaName);
			//the map name isn't always centered true to the original game, but it is centered correctly
		}
		
		canvas.addLayer(LegacyUiConstants.TEAM_NAME_X_START, LegacyUiConstants.TEAM_NAME_Y_START, teamNames[data.getCurrentTeam()]);

		addTeamBanners(canvas);
		addSelectedTeamAndPlayerIndicators(canvas);
		addYardLineOrPads(canvas);

		return canvas.getCompositeImage();
	}

	//longest team name and map name is 13 characters (jackal's lair)
	private void generateNames()
	{
		if (arenaName == null)
			arenaName = ImageUtils.padImage(fontFactory.generateString(data.getArena().getName(), LegacyUiConstants.COLOR_LEGACY_BLUE), new Dimension(78, 5));

		for (int i = 0; i < 3; i++)
		{
			if (teamNames[i] == null)
				teamNames[i] = ImageUtils.padImage(fontFactory.generateString(data.getTeam(i).teamName, LegacyUiConstants.COLOR_LEGACY_WHITE), new Dimension(118, 5));
		}
	}

	//TODO: this should be handled by the LegacyGamePlayScreen class
	private void depressActionButtons(ImageBuffer canvas)
	{
		switch (currentAction)
		{
		case ACTION_CHECK:
			canvas.addLayer(290, 7, imageFactory.getImage(ImageType.DEPRESSED_CHECK_BUTTON));
			break;
		case ACTION_HANDOFF:
			canvas.addLayer(281, 27, imageFactory.getImage(ImageType.DEPRESSED_HANDOFF_BUTTON));
			break;
		case ACTION_JUMP:
			canvas.addLayer(342, 7, imageFactory.getImage(ImageType.DEPRESSED_JUMP_BUTTON));
			break;
		case ACTION_MOVE:
			canvas.addLayer(261, 7, imageFactory.getImage(ImageType.DEPRESSED_MOVE_BUTTON));
			break;
		}
	}

	private BufferedImage generatePlayerStatuses()
	{
		ImageBuffer canvas = new ImageBuffer(ImageUtils.createBlankBufferedImage(new Dimension(244, 8), LegacyUiConstants.COLOR_LEGACY_TRANSPARENT));

		int startingIndex = data.getCurrentTeam() * 9;

		for (int i = 0; i < 9; i++)
		{
			Player p = data.getPlayer(startingIndex + i);

			if (p != null)
			{
				int status = p.status;
				int currentAP = p.currentAP;

				if (currentAP >= 100) // TODO: handle this properly (reference a Ritty curmian in-game to confirm)
					currentAP = 99;

				BufferedImage statusImage = ImageUtils.createBlankBufferedImage(new Dimension(1, 1),
						LegacyUiConstants.COLOR_LEGACY_TRANSPARENT);

				if (status == Player.STS_OKAY || status == Player.STS_DOWN)
				{
					statusImage = fontFactory.generateString(String.valueOf(currentAP), LegacyUiConstants.COLOR_LEGACY_GREEN,
							FontType.FONT_SMALL2);
					canvas.addLayer(28 * i + 3, 1, statusImage);
					continue;
				} else if (status == Player.STS_LATE)
					statusImage = imageFactory.getImage(ImageType.GAME_MASK_LATESTATUS);
				else if (status == Player.STS_STUN)
					statusImage = imageFactory.getImage(ImageType.GAME_MASK_STUNSTATUS);
				else if (status == Player.STS_HURT)
					statusImage = imageFactory.getImage(ImageType.GAME_MASK_HURTSTATUS);
				else if (status == Player.STS_DEAD)
					statusImage = imageFactory.getImage(ImageType.GAME_MASK_DEADSTATUS);
				else if (status == Player.STS_BLOB)
					statusImage = imageFactory.getImage(ImageType.GAME_MASK_BLOBSTATUS);
				else if (status == Player.STS_DECK)
					statusImage = imageFactory.getImage(ImageType.GAME_MASK_DECKSTATUS);

				canvas.addLayer(28 * i, 0, statusImage);
			}
		}

		return canvas.getCompositeImage();
	}
	
	private BufferedImage generateCurrentPlayerAttributes()
	{
		if (currentPlayer == null)
			return ImageUtils.createBlankBufferedImage(new Dimension(1, 1), LegacyUiConstants.COLOR_LEGACY_TRANSPARENT);
		
		LegacyPlayerTextFactory.setPlayer(currentPlayer);
		
		ImageBuffer canvas = new ImageBuffer(ImageUtils.createBlankBufferedImage(new Dimension(209, 23), LegacyUiConstants.COLOR_LEGACY_TRANSPARENT));
		
		int playerNumber = data.getNumberOfPlayer(currentPlayer);
		canvas.addLayer(1, 0, fontFactory.generateString(String.valueOf(playerNumber), LegacyUiConstants.COLOR_LEGACY_WHITE, FontType.FONT_SMALL));
		canvas.addLayer(15, 0, fontFactory.generateString(currentPlayer.name, LegacyUiConstants.COLOR_LEGACY_WHITE));
		
		LegacyTextElement apText = LegacyPlayerTextFactory.getColoredAttributeWithModifiers(Player.ATT_AP, LegacyUiConstants.COLOR_LEGACY_BLUE, LegacyUiConstants.COLOR_LEGACY_GOLD);
		canvas.addLayer(7, 15, fontFactory.generateString(apText));
		canvas.addLayer(50, 13, fontFactory.generateString(String.valueOf(currentPlayer.currentAP), LegacyUiConstants.COLOR_LEGACY_GREEN, FontType.FONT_BIG));
		
		for (int i = 1; i < 8; i++)
		{
			LegacyTextElement text = LegacyPlayerTextFactory.getColoredAttributeWithModifiers(i, LegacyUiConstants.COLOR_LEGACY_BLUE, LegacyUiConstants.COLOR_LEGACY_GOLD);
			canvas.addLayer(65 + (19 * i), 17, fontFactory.generateString(text));
		}
		
		return canvas.getCompositeImage();
	}

	private BufferedImage generateMinimap()
	{
		BufferedImage minimapBaseImage = generateMinimapBaseImage();
		
//		if (minimapBaseImage == null)
//			generateMinimapBaseImage();

		if (minimapBaseImage == null) // TODO: I really don't like this code repetition, but it will work for now
			return null;

		ImageBuffer canvas = new ImageBuffer(minimapBaseImage);

		darkenPadsAndGoals(canvas);
		addPlayerMinimapPoints(canvas);
		return canvas.getCompositeImage();
	}

	private BufferedImage generateMinimapBaseImage()
	{
		Arena arena = data.getArena();

		if (arena == null)
		{
			Logger.warn("LegacyButtonBarImageFactory - Minimap cannot be generated; no arena defined.");
			return null;
		}

		return ArenaImageGenerator.getArenaImage(arena, ARENA_SIDE_LENGTH, TILE_SIZE);
	}

	private void addPlayerMinimapPoints(ImageBuffer canvas)
	{
		BufferedImage playerPointsImage = ImageUtils.createBlankBufferedImage(new Dimension(ARENA_SIDE_LENGTH * TILE_SIZE, ARENA_SIDE_LENGTH * TILE_SIZE));
		populatePlayersInGame();
		Iterator<Player> iter = null;
		
		do {
			try {
				iter = playersInGame.iterator();
		
				while (iter.hasNext()) {
					addPlayerToMapImage(playerPointsImage, iter.next());
				}
			} catch (ConcurrentModificationException cme)
			{
				iter = null;
			}
		} while (iter == null);
		
		canvas.addLayer(0, 0, playerPointsImage);
	}
	
	private void addPlayerToMapImage(BufferedImage playerPointsImage, Player player)
	{
		Color pointColor = data.getTeamOfPlayer(player).teamColors[0];	// TODO: see if the ball carrier is a different color
		Point location = data.getLocationOfPlayer(player);
		
		//TODO: this happens if a player gets blobbed; probably happens at other times
		if (location == null) {
			Logger.warn("addPlayerMinimapPoints() - Player " + player.name + " has no location.");
			return;
		}
		
		ArenaImageGenerator.drawTile(playerPointsImage.createGraphics(), location.y, location.x, pointColor, TILE_SIZE);
	}
	
	private void darkenPadsAndGoals(ImageBuffer canvas)
	{
		BufferedImage darkenedTilesOverlay = ImageUtils.createBlankBufferedImage(new Dimension(ARENA_SIDE_LENGTH * TILE_SIZE, ARENA_SIDE_LENGTH * TILE_SIZE));
		
		List<Point> failedPadLocations = data.getArena().getDimPadLocations();
		
		for (Point padCoords : failedPadLocations)
		{
			ArenaImageGenerator.drawTile(darkenedTilesOverlay.createGraphics(), padCoords.y, padCoords.x, ArenaImageGenerator.FLOOR_COLOR, TILE_SIZE);
		}
		
		List<Point> dimGoalLocations = data.getArena().getDimGoalLocations();
		
		for (Point padCoords : dimGoalLocations)
		{
			ArenaImageGenerator.drawTile(darkenedTilesOverlay.createGraphics(), padCoords.y, padCoords.x, ArenaImageGenerator.DIM_GOAL_COLOR, TILE_SIZE);
		}
		
		canvas.addLayer(0, 0, darkenedTilesOverlay);
	}

	private void addTeamBanners(ImageBuffer canvas)
	{
		if (data.getTeams().isEmpty())
			return;
		
		Color bannerColors[][] = new Color[3][2];

		for (int i = 0; i < data.getTeams().size(); i++)
		{
			for (int j = 0; j < 2; j++)
			{
				bannerColors[i][j] = data.getTeam(i).teamColors[j];
			}
		}

		int currentTeamIndex = data.getCurrentTeam();

		// we have three banner source images, but the color replacer assumes only orange and blue as replacement colors
		// color replacer deep copies the images, so we can just reference them here
		BufferedImage teamBanner = imageFactory.getImage(ImageType.GAME_OVERLAY_TEAM1BANNER);
		BufferedImage currentTeamBanner = imageFactory.getImage(ImageType.GAME_OVERLAY_CURRENTTEAMBANNER);

		BufferedImage team1Banner = colorReplacer.setColors(teamBanner, bannerColors[0][0], bannerColors[0][1],
				LegacyUiConstants.COLOR_LEGACY_TRANSPARENT);
		BufferedImage team2Banner = colorReplacer.setColors(teamBanner, bannerColors[1][0], bannerColors[1][1],
				LegacyUiConstants.COLOR_LEGACY_TRANSPARENT);
		BufferedImage team3Banner = colorReplacer.setColors(teamBanner, bannerColors[2][0], bannerColors[2][1],
				LegacyUiConstants.COLOR_LEGACY_TRANSPARENT);
		currentTeamBanner = colorReplacer.setColors(currentTeamBanner, bannerColors[currentTeamIndex][0], bannerColors[currentTeamIndex][1],
				LegacyUiConstants.COLOR_LEGACY_TRANSPARENT);

		canvas.addLayer(541, 23, team1Banner);
		canvas.addLayer(541, 33, team2Banner);
		canvas.addLayer(541, 43, team3Banner);
		canvas.addLayer(398, 4, currentTeamBanner);
	}

	private void addSelectedTeamAndPlayerIndicators(ImageBuffer canvas)
	{
		int currentTeamIndex = data.getCurrentTeam();
		int currentPlayerIndex = data.getIndexOfPlayer(currentPlayer) - (9 * currentTeamIndex);

		canvas.addLayer(12 + (28 * currentPlayerIndex), 68, imageFactory.getImage(ImageType.GAME_OVERLAY_SELECTEDPLAYER));
		canvas.addLayer(535, 24 + (10 * currentTeamIndex), imageFactory.getImage(ImageType.GAME_OVERLAY_CURRENTTEAM));
	}
	
	private void addYardLineOrPads(ImageBuffer canvas)
	{
		Arena arena = data.getArena();
		BufferedImage text = null;
		
		if (!arena.isBallFound())
		{
			text = ImageUtils.padImage(fontFactory.generateString(String.valueOf(arena.getUntriedBinCount()), LegacyUiConstants.COLOR_LEGACY_BLUE), new Dimension(15, 5));
			canvas.addLayer(489, 63, imageFactory.getImage(ImageType.GAME_OVERLAY_PADSLEFT));
		} else
		{
			Point goalLine = arena.getGoalLine();
			Point ballLocation = data.getBallLocation();
			
			if (ballLocation.x == -1 && ballLocation.y == -1)
				ballLocation = data.getLocationOfPlayer(data.getBallCarrier());
			
			try {
				int xDist = Math.abs(ballLocation.x - goalLine.x);
				int yDist = Math.abs(ballLocation.y - goalLine.y);
				int dist = Math.max(xDist, yDist);
				text = ImageUtils.padImage(fontFactory.generateString(String.valueOf(dist), LegacyUiConstants.COLOR_LEGACY_BLUE), new Dimension(15, 5));
			} catch (NullPointerException npe)	//could happen if data hasn't quite updated by the time we're here
			{
				return;
			}
		}
			
		canvas.addLayer(494, 53, text);
	}

	private void populatePlayersInGame()
	{
		playersInGame.clear();
		List<Player> players = data.getAllPlayers();

		for (Player player : players)
		{
			if (player.isInGame())
			{
				playersInGame.add(player);
			}
		}
	}
}
