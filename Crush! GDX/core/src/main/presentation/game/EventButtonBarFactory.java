package main.presentation.game;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.data.Data;
import main.data.entities.Player;
import main.presentation.ImageType;
import main.presentation.legacy.common.LegacyUiConstants;

public class EventButtonBarFactory
{
	private Data data;
	
	private GameText[][] playerNames = new GameText[3][9];
	private GameText[] teamNames = new GameText[3];
	private GameText arenaName;
	
	private Map<ImageType, StaticImage> overlays = new HashMap<ImageType, StaticImage>();
	private Map<Action, StaticImage> depressedButtons = new HashMap<Action, StaticImage>();
	
	public static final int MINIMAP_X_START = 569;
	public static final int MINIMAP_Y_START = 7;
	public static final int MAPNAME_Y_START = 70;
	
	public static final int PLAYER_NAME_X_START = 21;
	public static final int PLAYER_NAME_Y_START = 325;
	public static final int TEAM_NAME_X_START = 396;	//426
	public static final int TEAM_NAME_Y_START = 326;
	public static final int TEAM_NAME_MAX_WIDTH = 118;
	
	public static final int PLAYER_ATTRIBUTE_X_START = 9;
	public static final int PLAYER_ATTRIBUTE_Y_START = 10;
	public static final int PLAYER_STATUS_X_START = 10;
	public static final int PLAYER_STATUS_Y_START = 58;
	
	private EventButtonBarFactory()
	{
		overlays.put(ImageType.GAME_OVERLAY_SELECTEDPLAYER, new StaticImage(ImageType.GAME_OVERLAY_SELECTEDPLAYER, new Point(0, 0)));
		overlays.put(ImageType.GAME_OVERLAY_CURRENTTEAM, new StaticImage(ImageType.GAME_OVERLAY_CURRENTTEAM, new Point(0, 0)));
		
		createDepressedButtons();
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

	public void beginGame(Data clientData)
	{
		data = clientData;
		generateNames();
		//create minimap image
	}

	private void generateNames()
	{
		arenaName = GameText.small2(new Point(MINIMAP_X_START - 9, MAPNAME_Y_START), LegacyUiConstants.COLOR_LEGACY_BLUE, data.getArena().getName());
		
		for (int i = 0; i < 3; i++)
		{
			String teamName = data.getTeam(i).teamName;
			teamNames[i] = GameText.small2(new Point(getStringStartX(TEAM_NAME_X_START, TEAM_NAME_MAX_WIDTH, teamName), TEAM_NAME_Y_START), LegacyUiConstants.COLOR_LEGACY_WHITE, teamName);
			
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
	
	private int getStringStartX(int targetX, int maxWidth, String text)
	{
		int stringLength = getStringPixelLength(text);
		
		if (stringLength > maxWidth)
			return targetX;
		
		int lengthDif = maxWidth - getStringPixelLength(text);
		
		return targetX + (lengthDif / 2);
	}
	
	private int getStringPixelLength(String text)
	{
		int length = 0;
		
		for (int i = 0; i < text.length(); i++)
		{
			if (text.charAt(i) == ' ')
				length += 3;
			else
				length += 6;
		}
		
		return length;
	}

	public List<GameText> getPlayerAndTeamName(Player currentPlayer)
	{
		List<GameText> names = new ArrayList<GameText>();
		
		if (currentPlayer == null)
			return names;
		
		int teamIndex = data.getCurrentTeam();
		int playerIndex = getPlayerIndex(currentPlayer);
		
		names.add(teamNames[teamIndex]);
		
		if (playerIndex >= 0)	//I think this can happen if the team has no available players
			names.add(playerNames[teamIndex][playerIndex]);
		
		return names;
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
		selectedPlayerIndicator.setPosition(new Point(12 + (28 * getPlayerIndex(currentPlayer)), 8));
		images.add(selectedPlayerIndicator);
		
		StaticImage currentTeamIndicator = overlays.get(ImageType.GAME_OVERLAY_CURRENTTEAM);
		currentTeamIndicator.setPosition(new Point(535, 49 - (10 * data.getCurrentTeam())));
		images.add(currentTeamIndicator);
		
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
			else if (status == Player.STS_STUN)
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
	
	private int getPlayerIndex(Player currentPlayer)
	{
		int teamIndex = data.getCurrentTeam();
		int playerIndex = data.getIndexOfPlayer(currentPlayer);
		return playerIndex - teamIndex * 9;
	}
}
