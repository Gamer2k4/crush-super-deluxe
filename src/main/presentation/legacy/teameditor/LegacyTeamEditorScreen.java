package main.presentation.legacy.teameditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import main.data.entities.Team;
import main.presentation.common.Logger;
import main.presentation.common.TeamEditor;
import main.presentation.common.image.ImageBuffer;
import main.presentation.common.image.ImageType;
import main.presentation.common.image.ImageUtils;
import main.presentation.legacy.common.AbstractLegacyImageBasedScreenPanel;
import main.presentation.legacy.common.FontType;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.teameditor.common.EditorValue;
import main.presentation.teameditor.common.TeamUpdater;

public class LegacyTeamEditorScreen extends AbstractLegacyImageBasedScreenPanel implements TeamEditor
{
	private static final long serialVersionUID = 1476475348480823916L;
	
	private static final Point coordsButtonBack = new Point(585, 341);
	private static final Point coordsButtonDone = new Point(585, 362);
	private static final Point coordsButtonSettings = new Point(85, 340);
	private static final Point coordsButtonEquipment = new Point(170, 340);
	private static final Point coordsButtonRosters = new Point(255, 340);
	private static final Point coordsButtonStats = new Point(340, 340);
	private static final Point coordsButtonDraft = new Point(85, 361);
	private static final Point coordsButtonDocbot = new Point(170, 361);
	private static final Point coordsButtonTrainer = new Point(255, 361);
	private static final Point coordsButtonSchedule = new Point(340, 361);
	
	private DecimalFormat threeDigitFormatter = new DecimalFormat("000");
	
	private Map<ImageType, Boolean> screenSelected = new HashMap<ImageType, Boolean>();

	private ImageType currentEditorScreen;
	private boolean detailedRoster;
	
	private LegacyTeamEditorSettingsScreen settingsScreen;
	private LegacyTeamEditorGeneralRoster generalRoster;
	
	protected TeamUpdater teamUpdater;
	protected int currentPlayerIndex = 0;
	
	private Team originalTeam;	//the team as it was before any changes are made (the one reverted to if clicking "Back" instead of "Done")

	public LegacyTeamEditorScreen(ActionListener listener)
	{
		super(imageFactory.getImage(ImageType.BG_BG4), getCompositeFormImage(ImageType.SCREEN_TEAM_EDITOR_START, ImageType.SCREEN_TEAM_EDITOR_ROSTER_GENERAL));
		setActionListener(listener);

		addClickZone(new Rectangle(coordsButtonBack, buttonDimSmallStats), ScreenCommand.CANCEL);
		addClickZone(new Rectangle(coordsButtonDone, buttonDimSmallStats), ScreenCommand.EXIT);
		
		addClickZone(new Rectangle(coordsButtonSettings, buttonDimLarge), ScreenCommand.SETTINGS_VIEW);
		addClickZone(new Rectangle(coordsButtonEquipment, buttonDimLarge), ScreenCommand.ACQUIRE_VIEW);
		addClickZone(new Rectangle(coordsButtonRosters, buttonDimLarge), ScreenCommand.TOGGLE_ROSTER_VIEW);
		addClickZone(new Rectangle(coordsButtonStats, buttonDimLarge), ScreenCommand.STATS_VIEW);
		addClickZone(new Rectangle(coordsButtonDraft, buttonDimLarge), ScreenCommand.DRAFT_VIEW);
		addClickZone(new Rectangle(coordsButtonDocbot, buttonDimLarge), ScreenCommand.DOCBOT_VIEW);
		addClickZone(new Rectangle(coordsButtonTrainer, buttonDimLarge), ScreenCommand.POWER_VIEW);
		addClickZone(new Rectangle(coordsButtonSchedule, buttonDimLarge), ScreenCommand.SCHEDULE_VIEW);
		
		screenSelected.put(ImageType.SCREEN_TEAM_EDITOR_START, true);
		screenSelected.put(ImageType.SCREEN_TEAM_EDITOR_SETTINGS, false);
		screenSelected.put(ImageType.SCREEN_TEAM_EDITOR_ACQUIRE, false);
		screenSelected.put(ImageType.SCREEN_TEAM_EDITOR_DRAFT, false);
		screenSelected.put(ImageType.SCREEN_TEAM_EDITOR_DOCBOT, false);
		screenSelected.put(ImageType.SCREEN_TEAM_EDITOR_PSYCHE, false);
		
		//TODO: Below was an okay thought, but the object-oriented approach I'm using now is probably better.  Delete the comment once it's completely irrelevant.
		//To add the click mappings for individual screens, just paint them, copy the screen section over to an image, black it out, then paint the next ones,
		//saving those as well.  Then, when switching screens, also switch the click maps.  

		currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_START;
		detailedRoster = false;

		teamUpdater = new TeamUpdater();
		
		settingsScreen = new LegacyTeamEditorSettingsScreen(this);
		generalRoster = new LegacyTeamEditorGeneralRoster(this);
	}

	private static BufferedImage getCompositeFormImage(ImageType leftSide, ImageType rightSide)
	{
		ImageBuffer.setBaseImage(ImageUtils.createBlankBufferedImage(defaultlegacyPanelDims, new Color(0, 0, 0, 0)));

		if (leftSide != null)
			ImageBuffer.addLayer(0, 0, imageFactory.getImage(leftSide));

		if (rightSide != null)
			ImageBuffer.addLayer(383, 10, imageFactory.getImage(rightSide));

		return ImageBuffer.getCompositeImage();
	}

	private String formatIntValue(EditorValue value)
	{
		int text = teamUpdater.getIntValue(value);

		switch (value)
		{
		// three digit values will flow through
		case TOTAL_COST:
		case TREASURY:
			return threeDigitFormatter.format(text);
		}

		return "NO VALUE";
	}

	@Override
	protected void paintImages(Graphics2D graphics)
	{
		if (detailedRoster);
		else
			generalRoster.paintElements(graphics);
		
		switch (currentEditorScreen)
		{
		case SCREEN_TEAM_EDITOR_START:
			return;
		case SCREEN_TEAM_EDITOR_SETTINGS:
			settingsScreen.paintElements(graphics);
//		case SCREEN_EQUIP_ACQUIRE:
//			return ImageType.SCREEN_TEAM_EDITOR_ACQUIRE;
//		case SCREEN_EQUIP_OUTFIT:
//			return ImageType.SCREEN_TEAM_EDITOR_OUTFIT;
//		case SCREEN_DRAFT:
//			return ImageType.SCREEN_TEAM_EDITOR_DRAFT;
//		case SCREEN_DOCBOT:
//			return ImageType.SCREEN_TEAM_EDITOR_DOCBOT;
//		case SCREEN_TRAINER_POWER:
//			return ImageType.SCREEN_TEAM_EDITOR_POWER;
//		case SCREEN_TRAINER_AGILITY:
//			return ImageType.SCREEN_TEAM_EDITOR_AGILITY;
//		case SCREEN_TRAINER_PSYCHE:
//			return ImageType.SCREEN_TEAM_EDITOR_PSYCHE;
		}
	}

	@Override
	protected void paintText(Graphics2D graphics)
	{
		paintTextElement(graphics, 516, 345, formatIntValue(EditorValue.TOTAL_COST), FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GOLD);
		paintTextElement(graphics, 516, 365, formatIntValue(EditorValue.TREASURY), FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GOLD);
	}

	@Override
	protected void paintButtonShading(Graphics2D graphics)
	{
		//clunky, but works
		if (screenSelected.get(ImageType.SCREEN_TEAM_EDITOR_SETTINGS))
			graphics.drawImage(imageFactory.getImage(ImageType.BUTTON_LARGE_CLICKED), coordsButtonSettings.x, coordsButtonSettings.y, null);
		else
			graphics.drawImage(imageFactory.getImage(ImageType.BUTTON_LARGE_NORMAL), coordsButtonSettings.x, coordsButtonSettings.y, null);
	}

	@Override
	public void paintComponent(Graphics g)
	{
		screenBaseImage = getCompositeFormImage(getLeftScreen(), getRightScreen());
		super.paintComponent(g);
	}

	private ImageType getLeftScreen()
	{
		return currentEditorScreen;
	}
	
	private ImageType getRightScreen()
	{
		if (detailedRoster)
			return ImageType.SCREEN_TEAM_EDITOR_ROSTER_DETAILED;
		
		return ImageType.SCREEN_TEAM_EDITOR_ROSTER_GENERAL;
	}

	@Override
	protected void handleCommand(ScreenCommand command)
	{
		Logger.debug("Command received for team editor screen: " + command);
		
		if (command.isEditorViewChange())
			screenSelected.put(currentEditorScreen, false);
		
		if (ScreenCommand.SETTINGS_VIEW.equals(command))
			currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_SETTINGS;
		else if (ScreenCommand.ACQUIRE_VIEW.equals(command))
			currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_ACQUIRE;
		else if (ScreenCommand.TOGGLE_ROSTER_VIEW.equals(command))
			detailedRoster = !detailedRoster;
		else if (ScreenCommand.DRAFT_VIEW.equals(command))
			currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_DRAFT;
		else if (ScreenCommand.DOCBOT_VIEW.equals(command))
			currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_DOCBOT;
		else if (ScreenCommand.POWER_VIEW.equals(command))
			currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_POWER;
		else if (ScreenCommand.EXIT.equals(command) || ScreenCommand.CANCEL.equals(command))
		{
			resetScreen();
			fireAction(command);
		}
		else
			fireAction(command);
		
		if (command.isEditorViewChange())
		{
			screenSelected.put(currentEditorScreen, true);
			refreshEnabledButtons();
		}

		repaint();
		
		//TODO: for button clicks, hold the "click" image as long as the mouse is held down (perhaps move this to the parent class)
		//for screen changes (except roster toggling), hold the click image as long as the screen is selected
	}

	private void refreshEnabledButtons()
	{
		if (screenSelected.get(ImageType.SCREEN_TEAM_EDITOR_SETTINGS))
			settingsScreen.enableButtons();
		else
			settingsScreen.disableButtons();
		
		//TODO: more screens as I add them
	}

	@Override
	public void resetScreen()
	{
		currentPlayerIndex = 0;
		currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_START;
		
		for (ImageType key : screenSelected.keySet())
		{
			screenSelected.put(key, false);
		}
		
		refreshEnabledButtons();
	}

	@Override
	public void setTeam(Team team)
	{
		if (team == null)
			teamUpdater.loadTeam(new Team());
		else
			teamUpdater.loadTeam(team);

		originalTeam = teamUpdater.getTeam().clone();

		//refreshTeam();	//TODO
	}


	@Override
	public Team getTeam()
	{
		return teamUpdater.getTeam();
	}

	@Override
	public void setLoadEnabled(boolean isEnabled)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setBudget(int budget)
	{
		// TODO Auto-generated method stub

	}
}
