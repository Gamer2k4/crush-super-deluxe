package main.presentation.screens.teameditor;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import main.presentation.CursorManager;
import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.common.GdxKeyMappings;
import main.presentation.common.ScreenCommand;
import main.presentation.common.image.ImageUtils;
import main.presentation.common.image.TeamColorType;
import main.presentation.game.ArenaImageGenerator;
import main.presentation.game.GameText;
import main.presentation.game.StaticImage;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.screens.teameditor.utilities.TeamUpdater;

public class TeamEditorSettingsScreen extends AbstractTeamEditorSubScreen
{
	private ArenaImageGenerator arenaImageGenerator = ArenaImageGenerator.getInstance();
	
	private int arenaSet = 0;
	private int arenaIndexInSet = 0;
	private Image arenaImage = null;
	
	private boolean updatingPrimaryColor = true;
	
	private Point mainColorCoords = null;
	private Point trimColorCoords = null;
	
	private static final int[][] teamColors = { { 0, 9, 18 },
											  { 1, 11, 29 },
											  { 2, 12, 21 },
											  { 3, 13, 19 },
											  { 5, 14, 26 },
											  { 7, 17, 28 }};
	
	private static final String[][] arenaNames = {{ "BRIDGES", "JACKAL'S LAIR", "CRISSICK", "WHIRLWIND"},
												  {"THE VOID", "OBSERVATORY", "ABYSS", "GADEL SPYRE"},
												  {"FULCRUM", "SAVANNA", "BARROW", "MAELSTROM"},
												  {"VAULT", "NEXUS", "DARKSUN", "BADLANDS"},
												  {"LIGHTWAY", "EYES", "DARKSTAR", "SPACECOM"}};
	
	private ImageButton humanControl;
	private ImageButton cpuControl;
	private List<ImageButton> editButtons;
	
	private Image colorPalette;
	private Image textEditCursor;
	
	private String teamNameEditText = null;
	private String coachNameEditText = null;
	
	private static final int MAX_NAME_LENGTH = 13;
	
	//text input:
	//player character limit: 8
	//team/coach character limit: 13
	//all uppercase
	//many symbols are technically allowed, but lets keep it to letters and numbers for now
	//text being edited is yellow, followed by a solid white square at the end
	//hitting enter or reaching the character limit submits the change
	//backspace deletes the last character
	//escape cancels the edit and restores the original text
	//double-clicking anywhere on the text box (or anywhere on the player line) triggers the edit
	//empty player rows cannot have their names changed
	//strictly speaking, the cursor vanishes when something is being edited, meaning there's no clicking anywhere else until it's done
	
	protected TeamEditorSettingsScreen(TeamEditorParentScreen parentScreen)
	{
		super(parentScreen);
		subScreenImage = new StaticImage(ImageType.SCREEN_TEAM_EDITOR_SETTINGS, screenOrigin);
		definePaletteImages();
		defineButtons();
		refreshContent();
	}
	
	private void definePaletteImages()
	{
		Point coords = new Point(310, 107);
		
		StaticImage colorPaletteStaticImage = new StaticImage(ImageType.SETTINGS_COLOR_PALETTE, coords);
		colorPalette = colorPaletteStaticImage.getImage();
		
		textEditCursor = new Image(new TextureRegionDrawable(new TextureRegion(ImageFactory.getInstance().getTexture(ImageType.SETTINGS_COLOR_PALETTE), 24, 24, 10, 10)));
	}

	private void defineButtons()
	{
		editButtons = new ArrayList<ImageButton>();
		
		editButtons.add(parentScreen.addClickZone(42, 30, 152, 18, ScreenCommand.RENAME_TEAM));
		editButtons.add(parentScreen.addClickZone(42, 84, 152, 18, ScreenCommand.RENAME_COACH));
		
		editButtons.add(parentScreen.addClickZone(128, 151, 85, 11, ScreenCommand.ARENA_NAME_0));
		editButtons.add(parentScreen.addClickZone(128, 164, 85, 11, ScreenCommand.ARENA_NAME_1));
		editButtons.add(parentScreen.addClickZone(128, 177, 85, 11, ScreenCommand.ARENA_NAME_2));
		editButtons.add(parentScreen.addClickZone(128, 190, 85, 11, ScreenCommand.ARENA_NAME_3));
		
		editButtons.add(parentScreen.addClickZone(134, 208, 15, 35, ScreenCommand.ARENA_SET_0));
		editButtons.add(parentScreen.addClickZone(151, 208, 15, 35, ScreenCommand.ARENA_SET_1));
		editButtons.add(parentScreen.addClickZone(168, 208, 15, 35, ScreenCommand.ARENA_SET_2));
		editButtons.add(parentScreen.addClickZone(185, 208, 15, 35, ScreenCommand.ARENA_SET_3));
		editButtons.add(parentScreen.addClickZone(202, 208, 15, 35, ScreenCommand.ARENA_SET_4));
		
		editButtons.add(parentScreen.addClickZone(322, 169, 14, 14, ScreenCommand.TEAM_MAIN_COLOR));
		editButtons.add(parentScreen.addClickZone(322, 194, 14, 14, ScreenCommand.TEAM_TRIM_COLOR));

		editButtons.add(parentScreen.addClickZone(311, 221, 11, 11, ScreenCommand.TEAM_COLOR_00));
		editButtons.add(parentScreen.addClickZone(323, 221, 11, 11, ScreenCommand.TEAM_COLOR_01));
		editButtons.add(parentScreen.addClickZone(335, 221, 11, 11, ScreenCommand.TEAM_COLOR_02));
		editButtons.add(parentScreen.addClickZone(311, 233, 11, 11, ScreenCommand.TEAM_COLOR_10));
		editButtons.add(parentScreen.addClickZone(323, 233, 11, 11, ScreenCommand.TEAM_COLOR_11));
		editButtons.add(parentScreen.addClickZone(335, 233, 11, 11, ScreenCommand.TEAM_COLOR_12));
		editButtons.add(parentScreen.addClickZone(311, 245, 11, 11, ScreenCommand.TEAM_COLOR_20));
		editButtons.add(parentScreen.addClickZone(323, 245, 11, 11, ScreenCommand.TEAM_COLOR_21));
		editButtons.add(parentScreen.addClickZone(335, 245, 11, 11, ScreenCommand.TEAM_COLOR_22));
		editButtons.add(parentScreen.addClickZone(311, 257, 11, 11, ScreenCommand.TEAM_COLOR_30));
		editButtons.add(parentScreen.addClickZone(323, 257, 11, 11, ScreenCommand.TEAM_COLOR_31));
		editButtons.add(parentScreen.addClickZone(335, 257, 11, 11, ScreenCommand.TEAM_COLOR_32));
		editButtons.add(parentScreen.addClickZone(311, 269, 11, 11, ScreenCommand.TEAM_COLOR_40));
		editButtons.add(parentScreen.addClickZone(323, 269, 11, 11, ScreenCommand.TEAM_COLOR_41));
		editButtons.add(parentScreen.addClickZone(335, 269, 11, 11, ScreenCommand.TEAM_COLOR_42));
		editButtons.add(parentScreen.addClickZone(311, 281, 11, 11, ScreenCommand.TEAM_COLOR_50));
		editButtons.add(parentScreen.addClickZone(323, 281, 11, 11, ScreenCommand.TEAM_COLOR_51));
		editButtons.add(parentScreen.addClickZone(335, 281, 11, 11, ScreenCommand.TEAM_COLOR_52));
		
		humanControl = parentScreen.addClickZone(246, 86, 37, 17, ScreenCommand.CONTROLLER_HUMAN);
		cpuControl = parentScreen.addClickZone(287, 86, 37, 17, ScreenCommand.CONTROLLER_AI);
	}

	@Override
	public void handleCommand(ScreenCommand command)
	{
		super.handleCommand(command);	//needed to check for double-clicks
		
		String commandString = command.name();
		
//		System.out.println("Settings screen received command: " + commandString);
//		System.out.println("Double click is " + isDoubleClick());
		
		//delegate these two to the parent screen, since they potentially involve a popup
		if (command == ScreenCommand.CONTROLLER_HUMAN || command == ScreenCommand.CONTROLLER_AI)
			parentScreen.actionPerformed(command.asActionEvent());
				
		else if (command == ScreenCommand.TEAM_MAIN_COLOR && !updatingPrimaryColor)
			updatingPrimaryColor = true;
		else if (command == ScreenCommand.TEAM_TRIM_COLOR && updatingPrimaryColor)
			updatingPrimaryColor = false;
		else if (command.isArenaNameSelect())
		{
			int newIndex = Integer.parseInt(commandString.substring(11));
			if (newIndex != arenaIndexInSet)
			{
				arenaIndexInSet = newIndex;
				teamUpdater.setHomeField((4 * arenaSet) + arenaIndexInSet);
			}
		}
		else if (command.isArenaSetSelect())
		{
			int newSet = Integer.parseInt(commandString.substring(10));
			if (newSet != arenaSet)
			{
				arenaSet = newSet;
				teamUpdater.setHomeField((4 * arenaSet) + arenaIndexInSet);
			}
		}
		else if (command.isTeamColorSelect())
		{
			int row = Integer.parseInt(commandString.substring(11, 12));
			int column = Integer.parseInt(commandString.substring(12));

			Color newTeamColor = TeamColorType.getColor(teamColors[row][column]);
			
			if (updatingPrimaryColor)
			{
				teamUpdater.setMainColor(newTeamColor);
				mainColorCoords = new Point(row, column);
			}
			else
			{
				teamUpdater.setTrimColor(newTeamColor);
				trimColorCoords = new Point(row, column);
			}
		}
		else if (command == ScreenCommand.RENAME_TEAM && isDoubleClick() && teamNameEditText == null)
		{
			Gdx.graphics.setCursor(CursorManager.hidden());
			teamNameEditText = "";
			coachNameEditText = null;
		}
		else if (command == ScreenCommand.RENAME_COACH && isDoubleClick() && coachNameEditText == null)
		{
			Gdx.graphics.setCursor(CursorManager.hidden());
			teamNameEditText = null;
			coachNameEditText = "";
		}
	}
	
	@Override
	protected void pressKey(String key)
	{
		if (key == GdxKeyMappings.ESCAPE)
		{
			Gdx.graphics.setCursor(parentScreen.getCursor());
			teamNameEditText = null;
			coachNameEditText = null;
			refreshParentStage();
		}
		
		if (teamNameEditText == null && coachNameEditText == null)
			return;
		
		if (key == GdxKeyMappings.BACKSPACE)
		{
			if (teamNameEditText != null && teamNameEditText.length() > 0)
				teamNameEditText = teamNameEditText.substring(0, teamNameEditText.length() - 1);
			else if (coachNameEditText != null && coachNameEditText.length() > 0)
				coachNameEditText = coachNameEditText.substring(0, coachNameEditText.length() - 1);
		}
		
		if (key.length() == 1)
		{
			if (teamNameEditText != null)
				teamNameEditText = teamNameEditText + key;
			else if (coachNameEditText != null)
				coachNameEditText = coachNameEditText + key;
		}
		
		if (teamNameEditText != null)
		{
			if (teamNameEditText.length() >= MAX_NAME_LENGTH || key == GdxKeyMappings.ENTER)
			{
				Gdx.graphics.setCursor(parentScreen.getCursor());
				teamUpdater.setTeamName(teamNameEditText);
				teamNameEditText = null;
			}
		}
		
		if (coachNameEditText != null)
		{
			if (coachNameEditText.length() >= MAX_NAME_LENGTH || key == GdxKeyMappings.ENTER)
			{
				Gdx.graphics.setCursor(parentScreen.getCursor());
				teamUpdater.setTeamCoach(coachNameEditText);
				coachNameEditText = null;
			}
		}
		
		refreshParentStage();
	}
	
	public void updateArenaSet(int set)
	{
		arenaSet = set;
	}
	
	public void refreshArena()
	{
		arenaImage = new Image(arenaImageGenerator.getArenaImage(teamUpdater.getHomeField()));
		arenaImage.setPosition(52, 196);
	}

	private void refreshColorCoords()
	{
		mainColorCoords = getColorCoord(teamUpdater.getMainColor());
		trimColorCoords = getColorCoord(teamUpdater.getTrimColor());
	}
	
	private Point getColorCoord(Color color)
	{
		for (int i = 0; i < 6; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Color compareColor = TeamColorType.getColor(teamColors[i][j]);
				
				if (ImageUtils.rgbEquals(color, compareColor))
					return new Point(i, j);
			}
		}
		
		return new Point(2, 0);	//default to gold if the team color isn't in the standard list of 18 default colors
	}
	
	private void setArenaIndex()
	{
		int set = teamUpdater.getHomeField() / 4;
		arenaIndexInSet = teamUpdater.getHomeField() - (4 * set);
	}

	private void swapToCorrectArenaSet()
	{
		int set = teamUpdater.getHomeField() / 4;
		handleCommand(ScreenCommand.valueOf("ARENA_SET_" + set));
	}
	
	private void refreshTeam()
	{
		refreshColorCoords();
		setArenaIndex();		
		swapToCorrectArenaSet();
		refreshArena();
	}

	@Override
	protected void refreshContent()
	{
		updatingPrimaryColor = true;
		teamNameEditText = null;
		coachNameEditText = null;
		refreshTeam();
	}
	
	@Override
	public void actionPerformed(ActionEvent event)
	{
		if (event.getActionCommand().equals(TeamUpdater.UPDATER_NEW_TEAM))
			refreshContent();
		
		if (event.getActionCommand().equals(TeamUpdater.UPDATER_ARENA_CHANGED))
			refreshArena();
		
		if (event.getActionCommand().equals(TeamUpdater.UPDATER_COLORS_CHANGED))
			refreshColorCoords();
	}
	
	@Override
	public List<Actor> getActors()
	{
		List<Actor> actors = super.getActors();
		
		//adding these at 0 so they go behind the screen
		actors.add(0, colorPalette);
		actors.add(0, getSingleColor(new Point(323, 218), mainColorCoords));
		actors.add(0, getSingleColor(new Point(323, 192), trimColorCoords));
		
		actors.add(getArenaSetPressedButton());
		actors.add(getControllerPressedButton());
		actors.add(getHighlightDiamond());
		
		actors.add(arenaImage);
		
		if (teamNameEditText != null)
		{
			textEditCursor.setPosition(48 + (11 * teamNameEditText.length()), 355);
			actors.add(textEditCursor);
		}
		else if (coachNameEditText != null)
		{
			textEditCursor.setPosition(48 + (11 * coachNameEditText.length()), 301);
			actors.add(textEditCursor);
		}
		
		return actors;
	}

	@Override
	public List<ImageButton> getScreenButtons()
	{
		List<ImageButton> screenButtons = super.getScreenButtons();
		
		//all non-controller buttons should be disabled if the user isn't allowed to edit (due to a CPU controller or the team being locked)
		if (canUserEditTeam())
			screenButtons.addAll(editButtons);
		
		//the controller buttons still work no matter what, though there's no prompt to keep the current team if the team is locked for editing
		if (teamUpdater.getTeam().humanControlled)
			screenButtons.add(cpuControl);
		else
			screenButtons.add(humanControl);
		
		return screenButtons;
	}

	private Image getSingleColor(Point screenLocation, Point colorCoords)
	{
		int x = 12 * colorCoords.x;
		int y = 12 * colorCoords.y;
		Drawable singleColorSwatch = new TextureRegionDrawable(new TextureRegion(ImageFactory.getInstance().getTexture(ImageType.SETTINGS_COLOR_PALETTE), y, x, 11, 11));
		StaticImage swatch = new StaticImage(singleColorSwatch, screenLocation);
		return swatch.getImage();
	}

	private Actor getArenaSetPressedButton()
	{
		Point coords = new Point(134 + (17 * arenaSet), 156);
		StaticImage arenaSetPressedButton = new StaticImage(ImageType.BUTTON_15x35_CLICKED, coords);
		return arenaSetPressedButton.getImage();
	}
	
	private Image getControllerPressedButton()
	{
		Point coords = new Point(246, 297);
		if (!teamUpdater.getTeam().humanControlled)
			coords.x = 287;
		
		StaticImage controllerPressedButton = new StaticImage(ImageType.BUTTON_37x17_CLICKED, coords);
		return controllerPressedButton.getImage();
	}
	
	private Image getHighlightDiamond()
	{
		Point coords = new Point(322, 217);
		if (!updatingPrimaryColor)
			coords.y = 191;
		
		StaticImage highlightDiamond = new StaticImage(ImageType.DIAMOND_HIGHLIGHT, coords);
		return highlightDiamond.getImage();
	}
	
	@Override
	public List<GameText> getScreenTexts()
	{
		List<GameText> screenTexts = super.getScreenTexts();
		
		screenTexts.addAll(generateArenaNames());
		
		if (teamNameEditText == "") {}
		else if (teamNameEditText != null)
		{
			screenTexts.add(GameText.small(new Point(48, 25), LegacyUiConstants.COLOR_LEGACY_GOLD, teamNameEditText));
		}
		else
		{
			screenTexts.add(GameText.small(new Point(48, 25), LegacyUiConstants.COLOR_LEGACY_GREY, teamUpdater.getTeamName()));
		}
		
		if (coachNameEditText == "") {}
		else if (coachNameEditText != null)
		{
			screenTexts.add(GameText.small(new Point(48, 79), LegacyUiConstants.COLOR_LEGACY_GOLD, coachNameEditText));
		}
		else
		{
			screenTexts.add(GameText.small(new Point(48, 79), LegacyUiConstants.COLOR_LEGACY_GREY, teamUpdater.getTeamCoach()));
		}
		
		return screenTexts;
	}
	
	private List<GameText> generateArenaNames()
	{
		List<GameText> arenaNameTexts = new ArrayList<GameText>();
		
		for (int i = 0; i < 4; i++)
		{
			String arenaName = arenaNames[arenaSet][i];
			Color nameColor = LegacyUiConstants.COLOR_LEGACY_GREY;
			
			if (teamUpdater.getHomeField() == (4 * arenaSet + i))
				nameColor = LegacyUiConstants.COLOR_LEGACY_GOLD;
			
			arenaNameTexts.add(GameText.small2(new Point(142, 150 + (13 * i)), nameColor, arenaName));
		}
		
		return arenaNameTexts;
	}
	
	@Override
	public boolean keysEnabled()
	{
		return (teamNameEditText != null || coachNameEditText != null);
	}
}