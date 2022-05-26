package main.presentation.game;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import main.data.Data;
import main.data.entities.Arena;
import main.data.entities.Player;
import main.data.entities.Team;
import main.presentation.ImageType;
import main.presentation.TeamColorsManager;
import main.presentation.common.Logger;
import main.presentation.common.PlayerTextFactory;
import main.presentation.common.image.ImageUtils;
import main.presentation.legacy.common.LegacyUiConstants;

public class EventButtonBarFactory
{
	private Data data;
	
	private GameText[][] playerNames = new GameText[3][9];
	private GameText[] teamNames = new GameText[3];
	private GameText[] currentPlayerAttributes = new GameText[8];
	private GameText[] playerNumbers = new GameText[9];
	private Map<Integer, GameText> largeApText = new HashMap<Integer, GameText>();
	
	private GameText arenaName;
	
	private Point currentPlayerTeamAndIndex = new Point(-1, -1);
	
	private Map<ImageType, StaticImage> overlays = new HashMap<ImageType, StaticImage>();
	private Map<Action, StaticImage> depressedButtons = new HashMap<Action, StaticImage>();
	
	private StaticImage[][] teamBanners = new StaticImage[3][2];
	
	private boolean ballFound = false;
	
	public static final int MINIMAP_X_START = 567;
	public static final int MINIMAP_Y_START = 11;
	public static final int MAPNAME_Y_START = 385;
	public static final int MAP_NAME_MAX_WIDTH = 78;
	
	public static final int PLAYER_NAME_X_START = 24;
	public static final int PLAYER_NAME_Y_START = 325;
	public static final int TEAM_NAME_X_START = 426;
	public static final int TEAM_NAME_Y_START = 326;
	public static final int TEAM_NAME_MAX_WIDTH = 118;
	
	public static final int PLAYER_ATTRIBUTE_X_START = 9;
	public static final int PLAYER_ATTRIBUTE_Y_START = 10;
	public static final int PLAYER_STATUS_X_START = 10;
	public static final int PLAYER_STATUS_Y_START = 58;
	
	private static final int PADS_LEFT_BALL_ON_X_START = 494;
	private static final int PADS_LEFT_BALL_ON_Y_START = 368;
	private static final int PADS_LEFT_BALL_ON_MAX_WIDTH = 15;
	
	private static final int ARENA_SIDE_LENGTH = 30;
	private static final int TILE_SIZE = 2;
	
	private EventButtonBarFactory()
	{
		overlays.put(ImageType.GAME_OVERLAY_SELECTEDPLAYER, new StaticImage(ImageType.GAME_OVERLAY_SELECTEDPLAYER, new Point(0, 0)));
		overlays.put(ImageType.GAME_OVERLAY_CURRENTTEAM, new StaticImage(ImageType.GAME_OVERLAY_CURRENTTEAM, new Point(0, 0)));
		
		createDepressedButtons();
		createPlayerNumbers();
	}

	private static EventButtonBarFactory instance = null;

	public static EventButtonBarFactory getInstance()
	{
		if (instance == null)
			instance = new EventButtonBarFactory();

		return instance;
	}

	private void createDepressedButtons()
	{
		depressedButtons.put(Action.ACTION_MOVE, new StaticImage(ImageType.DEPRESSED_MOVE_BUTTON, new Point(261, 36)));
		depressedButtons.put(Action.ACTION_CHECK, new StaticImage(ImageType.DEPRESSED_CHECK_BUTTON, new Point(291, 38)));
		depressedButtons.put(Action.ACTION_JUMP, new StaticImage(ImageType.DEPRESSED_JUMP_BUTTON, new Point(342, 36)));
		depressedButtons.put(Action.ACTION_HANDOFF, new StaticImage(ImageType.DEPRESSED_HANDOFF_BUTTON, new Point(281, 18)));
		depressedButtons.put(Action.ACTION_END_TURN, new StaticImage(ImageType.DEPRESSED_END_BUTTON, new Point(369, 10)));
	}

	private void createPlayerNumbers()
	{
		for (int i = 1; i <= 9; i++)
			playerNumbers[i - 1] = new GameText(FontType.FONT_BIG, new Point(10, 320), Color.WHITE, String.valueOf(i));
	}

	public void beginGame(Data clientData)
	{
		data = clientData;
		ballFound = false;
		generateNames();
		generateTeamBanners();
	}
	
	public void setBallFound()
	{
		ballFound = true;
	}

	private void generateNames()
	{
		arenaName = GameText.small2(new Point(GameText.getStringStartX(GameText.small2, MINIMAP_X_START - 11, MAP_NAME_MAX_WIDTH, data.getArena().getName()), MAPNAME_Y_START), LegacyUiConstants.COLOR_LEGACY_BLUE, data.getArena().getName());
		
		for (int i = 0; i < 3; i++)
		{
			String teamName = data.getTeam(i).teamName;
			teamNames[i] = GameText.small2(new Point(GameText.getStringStartX(GameText.small2, TEAM_NAME_X_START, TEAM_NAME_MAX_WIDTH, teamName), TEAM_NAME_Y_START), LegacyUiConstants.COLOR_LEGACY_WHITE, teamName);
			
			for (int j = 0; j < 9; j++)
			{
				Player player = data.getTeam(i).getPlayer(j);
				if (player == null)
				{
					playerNames[i][j] = GameText.small2(new Point(-10, -10), new Color(0, 0, 0, 0), " ");
					continue;
				}
				
				playerNames[i][j] = GameText.small2(new Point(PLAYER_NAME_X_START, PLAYER_NAME_Y_START), LegacyUiConstants.COLOR_LEGACY_WHITE, player.name);
			}
		}
	}
	
	private void generateTeamBanners()
	{
		for (int i = 0; i < 3; i++)
		{
			Team team = data.getTeam(i);
			
			teamBanners[i][0] = new StaticImage(TeamColorsManager.getInstance().getSmallTeamBanner(team), new Point(541, 49 - (10 * i)));
			teamBanners[i][1] = new StaticImage(TeamColorsManager.getInstance().getLargeTeamBanner(team), new Point(398, 48));
		}
	}

	public List<GameText> getPlayerTextInfo(Player currentPlayer)
	{
		List<GameText> names = new ArrayList<GameText>();
		
		if (currentPlayer == null)
			return names;
		
		int teamIndex = data.getCurrentTeam();
		int playerIndex = data.getNumberOfPlayer(currentPlayer);
		
		names.add(playerNumbers[playerIndex - 1]);
		names.add(teamNames[teamIndex]);
		names.add(arenaName);
		names.add(getPadsLeftOrBallOnValue());
		
//		if (currentPlayer.isInGame())			//apparently the way the original game worked was to display the stats anyway
			names.add(playerNames[teamIndex][playerIndex - 1]);
		
		return names;
	}

	private GameText getPadsLeftOrBallOnValue()
	{
		Arena arena = data.getArena();
		
		if (!ballFound)
		{
			String padsLeft = String.valueOf(arena.getUntriedBinCount());
			int startX = GameText.getStringStartX(GameText.small2, PADS_LEFT_BALL_ON_X_START, PADS_LEFT_BALL_ON_MAX_WIDTH, padsLeft); 
			return GameText.small2(new Point(startX, PADS_LEFT_BALL_ON_Y_START), LegacyUiConstants.COLOR_LEGACY_BLUE, padsLeft);
		}
		
		Point goalLine = arena.getGoalFarCorner();
		Point ballLocation = data.getBallLocation();
		
		if (ballLocation.x == -1 && ballLocation.y == -1)
			ballLocation = data.getLocationOfPlayer(data.getBallCarrier());
		
		try {
			int xDist = Math.abs(ballLocation.x - goalLine.x);
			int yDist = Math.abs(ballLocation.y - goalLine.y);
			int totalDist = Math.max(xDist, yDist) - 3;	//subtracting 3 because we're calculating from the innermost corner, so all edges will be the same distance
			String ballOn = String.valueOf(Math.max(totalDist, 0));	//no negative numbers (could come from jumping into the goal)		
			int startX = GameText.getStringStartX(GameText.small2, PADS_LEFT_BALL_ON_X_START, PADS_LEFT_BALL_ON_MAX_WIDTH, ballOn);
			return GameText.small2(new Point(startX, PADS_LEFT_BALL_ON_Y_START), LegacyUiConstants.COLOR_LEGACY_BLUE, ballOn);
		} catch (NullPointerException npe)	//could happen if data hasn't quite updated by the time we're here
		{
			int startX = GameText.getStringStartX(GameText.small2, PADS_LEFT_BALL_ON_X_START, PADS_LEFT_BALL_ON_MAX_WIDTH, "0");
			return GameText.small2(new Point(startX, PADS_LEFT_BALL_ON_Y_START), LegacyUiConstants.COLOR_LEGACY_BLUE, "0");
		}
	}

	public List<StaticImage> getMinimap()
	{
		List<StaticImage> minimap = new ArrayList<StaticImage>();
		
		ArenaImageGenerator.generateArenaImage(data.getArena(), ARENA_SIDE_LENGTH, TILE_SIZE);
		darkenPadsAndGoals();
		addPlayerMinimapPoints();
		ArenaImageGenerator.prepare();
		
		Drawable minimapImage = ArenaImageGenerator.getArenaImage();
		minimap.add(new StaticImage(minimapImage, new Point(MINIMAP_X_START, MINIMAP_Y_START)));
		
		return minimap;
	}
	
	private void darkenPadsAndGoals()
	{
		List<Point> failedPadLocations = data.getArena().getDimPadLocations();
		
		for (Point padCoords : failedPadLocations)
		{
			ArenaImageGenerator.drawTile(padCoords.y + 1, padCoords.x + 1, ArenaImageGenerator.FLOOR_COLOR, TILE_SIZE);
		}
		
		List<Point> dimGoalLocations = data.getArena().getDimGoalLocations();
		
		for (Point padCoords : dimGoalLocations)
		{
			ArenaImageGenerator.drawTile(padCoords.y + 1, padCoords.x + 1, ArenaImageGenerator.DIM_GOAL_COLOR, TILE_SIZE);
		}
	}

	private void addPlayerMinimapPoints()
	{
		for (Player player : data.getAllPlayers())
		{
			if (player.isInGame())
			{
				Color pointColor = data.getTeamOfPlayer(player).teamColors[0];	// TODO: see if the ball carrier is a different color
				Point location = data.getLocationOfPlayer(player);
				
				//this happens if a player gets blobbed; probably happens at other times
				if (location == null) {
					Logger.warn("addPlayerMinimapPoints() - Player " + player.name + " has no location.");
					continue;
				}
				
				ArenaImageGenerator.drawTile(location.y + 1, location.x + 1, ImageUtils.gdxColor(pointColor), TILE_SIZE);	//coords are +1 to account for the map border
			}
		}
	}
	
	public List<StaticImage> getTeamBanners()
	{
		List<StaticImage> banners = new ArrayList<StaticImage>();
		
		for (int i = 0; i < 3; i++)
		{
			StaticImage smallBanner = teamBanners[i][0];
			
			if (smallBanner != null)
				banners.add(smallBanner);
		}
		
		StaticImage largeBanner = teamBanners[data.getCurrentTeam()][1];
		
		if (largeBanner != null)
			banners.add(largeBanner);
		
		return banners;
	}

	public List<StaticImage> getSelectedButton(Action currentAction)
	{
		List<StaticImage> button = new ArrayList<StaticImage>();
		
		button.add(depressedButtons.get(currentAction));
		
		return button;
	}

	public List<StaticImage> getSelectedTeamAndPlayerIndicators(Player currentPlayer)
	{
		List<StaticImage> images = new ArrayList<StaticImage>();
		
		StaticImage selectedPlayerIndicator = overlays.get(ImageType.GAME_OVERLAY_SELECTEDPLAYER);
		selectedPlayerIndicator.setPosition(new Point((28 * data.getNumberOfPlayer(currentPlayer)) - 16, 8));
		images.add(selectedPlayerIndicator);
		
		StaticImage currentTeamIndicator = overlays.get(ImageType.GAME_OVERLAY_CURRENTTEAM);
		currentTeamIndicator.setPosition(new Point(535, 49 - (10 * data.getCurrentTeam())));
		images.add(currentTeamIndicator);
		
		if (!ballFound)
		{
			StaticImage padsLeft = new StaticImage(ImageType.GAME_OVERLAY_PADSLEFT, new Point(489, 8));
			images.add(padsLeft);
		}
		
		return images;
	}
	
	public List<StaticImage> getPlayerStatuses()
	{
		List<StaticImage> images = new ArrayList<StaticImage>();
		
		int startingIndex = data.getCurrentTeam() * 9;

		for (int i = 0; i < 9; i++)
		{
			Player p = data.getPlayer(startingIndex + i);

			if (p == null)
				continue;
			
			int status = p.status;
			
			if (status == Player.STS_OKAY || status == Player.STS_DOWN)
				continue;
			
			ImageType statusImage = ImageType.GAME_MASK_DECKSTATUS;
			
			if (status == Player.STS_LATE)
				statusImage = ImageType.GAME_MASK_LATESTATUS;
			else if (status == Player.STS_STUN_DOWN || status == Player.STS_STUN_SIT)
				statusImage = ImageType.GAME_MASK_STUNSTATUS;
			else if (status == Player.STS_HURT)
				statusImage = ImageType.GAME_MASK_HURTSTATUS;
			else if (status == Player.STS_DEAD)
				statusImage = ImageType.GAME_MASK_DEADSTATUS;
			else if (status == Player.STS_BLOB)
				statusImage = ImageType.GAME_MASK_BLOBSTATUS; 
			
			images.add(new StaticImage(statusImage, new Point(9 + (28 * i), 15)));
		}
		
		return images;
	}
	
	public List<GameText> getPlayerAttributes(Player currentPlayer)
	{
		List<GameText> attributes = new ArrayList<GameText>();
		
		int teamIndex = data.getCurrentTeam();
		int playerIndex = data.getNumberOfPlayer(currentPlayer);
		
		if (currentPlayerTeamAndIndex.x != teamIndex || currentPlayerTeamAndIndex.y != playerIndex)
		{
			currentPlayerTeamAndIndex = new Point(teamIndex, playerIndex);
			redefineCurrentPlayerAttributeText(currentPlayer);
		}
		
		for (int i = 0; i < 8; i++)
		{
			if (currentPlayerAttributes[i] != null)
				attributes.add(currentPlayerAttributes[i]);
		}
		
		if (currentPlayer != null)
			attributes.add(getLargeApText(currentPlayer.currentAP));
		
		return attributes;
	}
	
	private void redefineCurrentPlayerAttributeText(Player currentPlayer)
	{
		PlayerTextFactory.setPlayer(currentPlayer);
//		int playerNumber = data.getNumberOfPlayer(currentPlayer);
		//TODO: display the number of the player in large font
		
		for (int i = 1; i < 8; i++)
		{
			if (currentPlayer == null)
			{
				currentPlayerAttributes[i] = null;
				continue;
			}
			
			GameText text = PlayerTextFactory.getColoredAttributeWithModifiers(i, LegacyUiConstants.COLOR_LEGACY_BLUE, LegacyUiConstants.COLOR_LEGACY_GOLD);
			text.setCoords(new Point(74 + (19 * i), 342));
			currentPlayerAttributes[i] = text;
		}
	}
	
	public List<GameText> getPlayerApStatuses(int teamIndex)
	{
		List<GameText> attributes = new ArrayList<GameText>();
		
		int startingIndex = teamIndex * 9;
		
		for (int i = 0; i < 9; i++)
		{
			Player player = data.getPlayer(startingIndex + i);
			
			if (player != null && (player.status == Player.STS_OKAY || player.status == Player.STS_DOWN))
				attributes.add(GameText.small2(new Point((28 * i) + 13, 374), LegacyUiConstants.COLOR_LEGACY_GREEN, String.valueOf(player.currentAP)));
		}
		
		return attributes;
	}
	
	private GameText getLargeApText(int apAmount)
	{
		Integer key = Integer.valueOf(apAmount);
		
		if (!largeApText.containsKey(key))
			largeApText.put(key, GameText.big(new Point(58, 333), LegacyUiConstants.COLOR_LEGACY_GREEN, String.valueOf(apAmount)));
		
		return largeApText.get(key);
	}
}
