package main.presentation.legacy.teameditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import main.data.entities.Team;
import main.presentation.common.Logger;
import main.presentation.common.TeamEditor;
import main.presentation.common.image.ImageBuffer;
import main.presentation.common.image.ImageType;
import main.presentation.common.image.ImageUtils;
import main.presentation.legacy.common.AbstractLegacyImageBasedScreenPanel;
import main.presentation.legacy.common.FontType;
import main.presentation.legacy.common.LegacyUiConstants;
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
	private Map<ImageType, LegacyTeamEditorScreenDecorator> screenMappings = new HashMap<ImageType, LegacyTeamEditorScreenDecorator>();

	private ImageType currentEditorScreen;
	private boolean showingDetailedRoster;
	private boolean poolDrafting;
	private boolean detailedPool;

	private LegacyTeamEditorGeneralRoster generalRoster;
	private LegacyTeamEditorDetailedRoster detailedRoster;
	
	protected TeamUpdater teamUpdater;
	protected int currentPlayerIndex = 0;
	
	private Team originalTeam;	//the team as it was before any changes are made (the one reverted to if clicking "Back" instead of "Done")
	private int maxBudget = 900;

	public LegacyTeamEditorScreen(ActionListener listener)
	{
		super(imageFactory.getImage(ImageType.BG_BG4), getCompositeFromImages(ImageType.SCREEN_TEAM_EDITOR_START, ImageType.SCREEN_TEAM_EDITOR_ROSTER_GENERAL));
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
		screenSelected.put(ImageType.SCREEN_TEAM_EDITOR_OUTFIT, false);
		screenSelected.put(ImageType.SCREEN_TEAM_EDITOR_DRAFT, false);
		screenSelected.put(ImageType.SCREEN_TEAM_EDITOR_DOCBOT, false);
		screenSelected.put(ImageType.SCREEN_TEAM_EDITOR_POWER, false);
		screenSelected.put(ImageType.SCREEN_TEAM_EDITOR_AGILITY, false);
		screenSelected.put(ImageType.SCREEN_TEAM_EDITOR_PSYCHE, false);
		screenSelected.put(ImageType.SCREEN_TEAM_EDITOR_POOL_DRAFT_DETAILED, false);
		screenSelected.put(ImageType.SCREEN_TEAM_EDITOR_POOL_DRAFT_GENERAL, false);
		
		teamUpdater = new TeamUpdater();
		
		screenMappings.put(ImageType.SCREEN_TEAM_EDITOR_SETTINGS, new LegacyTeamEditorSettingsScreen(this));
		screenMappings.put(ImageType.SCREEN_TEAM_EDITOR_DRAFT, new LegacyTeamEditorStockDraftScreen(this));
		
		//TODO: Below was an okay thought, but the object-oriented approach I'm using now is probably better.  Delete the comment once it's completely irrelevant.
		//To add the click mappings for individual screens, just paint them, copy the screen section over to an image, black it out, then paint the next ones,
		//saving those as well.  Then, when switching screens, also switch the click maps.  

		currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_START;
		showingDetailedRoster = false;
		poolDrafting = false;
		detailedPool = false;
		
		generalRoster = new LegacyTeamEditorGeneralRoster(this);
		detailedRoster = new LegacyTeamEditorDetailedRoster(this);
	}
	
	public void beginPoolDraft()
	{
		poolDrafting = true;
	}

	private static BufferedImage getCompositeFromImages(ImageType leftSide, ImageType rightSide)
	{
		ImageBuffer.setBaseImage(ImageUtils.createBlankBufferedImage(defaultlegacyPanelDims, new Color(0, 0, 0, 0)));

		if (leftSide != null)
			ImageBuffer.addLayer(0, 0, imageFactory.getImage(leftSide));

		if (rightSide != null)
			ImageBuffer.addLayer(383, 10, imageFactory.getImage(rightSide));

		return ImageBuffer.getCompositeImage();
	}

	@Override
	protected void paintImages(Graphics2D graphics)
	{
		if (showingDetailedRoster)
			detailedRoster.paintElements(graphics);
		else
			generalRoster.paintElements(graphics);
		
		LegacyTeamEditorScreenDecorator screenToPaint = screenMappings.get(currentEditorScreen);
		
		if (screenToPaint == null)
			return;
		
		screenToPaint.paintElements(graphics);
	}

	@Override
	protected void paintText(Graphics2D graphics)
	{
		int totalCost = teamUpdater.getTeam().getValue();
		int treasury = getBudget() - totalCost;
		Color treasuryColor = LegacyUiConstants.COLOR_LEGACY_GOLD;
		
		if (treasury < 0)
		{
			treasury = -1 * treasury;
			treasuryColor = LegacyUiConstants.COLOR_LEGACY_RED;
		}
		
		
		paintTextElement(graphics, 516, 345, threeDigitFormatter.format(totalCost), FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GOLD);
		paintTextElement(graphics, 516, 365, threeDigitFormatter.format(treasury), FontType.FONT_SMALL, treasuryColor);
	}

	@Override
	protected void paintButtonShading(Graphics2D graphics)
	{
		//clunky, but works; eventually make this a map if it seems better
		
		if (screenSelected.get(ImageType.SCREEN_TEAM_EDITOR_SETTINGS))
			graphics.drawImage(imageFactory.getImage(ImageType.BUTTON_LARGE_CLICKED), coordsButtonSettings.x, coordsButtonSettings.y, null);
		else
			graphics.drawImage(imageFactory.getImage(ImageType.BUTTON_LARGE_NORMAL), coordsButtonSettings.x, coordsButtonSettings.y, null);
		
		if (screenSelected.get(ImageType.SCREEN_TEAM_EDITOR_DRAFT))
			graphics.drawImage(imageFactory.getImage(ImageType.BUTTON_LARGE_CLICKED), coordsButtonDraft.x, coordsButtonDraft.y, null);
		else
			graphics.drawImage(imageFactory.getImage(ImageType.BUTTON_LARGE_NORMAL), coordsButtonDraft.x, coordsButtonDraft.y, null);
		
		if (screenSelected.get(ImageType.SCREEN_TEAM_EDITOR_ACQUIRE) || screenSelected.get(ImageType.SCREEN_TEAM_EDITOR_OUTFIT))
			graphics.drawImage(imageFactory.getImage(ImageType.BUTTON_LARGE_CLICKED), coordsButtonEquipment.x, coordsButtonEquipment.y, null);
		else
			graphics.drawImage(imageFactory.getImage(ImageType.BUTTON_LARGE_NORMAL), coordsButtonEquipment.x, coordsButtonEquipment.y, null);
		
		if (screenSelected.get(ImageType.SCREEN_TEAM_EDITOR_POWER) || screenSelected.get(ImageType.SCREEN_TEAM_EDITOR_AGILITY) || screenSelected.get(ImageType.SCREEN_TEAM_EDITOR_PSYCHE))
			graphics.drawImage(imageFactory.getImage(ImageType.BUTTON_LARGE_CLICKED), coordsButtonTrainer.x, coordsButtonTrainer.y, null);
		else
			graphics.drawImage(imageFactory.getImage(ImageType.BUTTON_LARGE_NORMAL), coordsButtonTrainer.x, coordsButtonTrainer.y, null);
		
		if (screenSelected.get(ImageType.SCREEN_TEAM_EDITOR_DOCBOT))
			graphics.drawImage(imageFactory.getImage(ImageType.BUTTON_LARGE_CLICKED), coordsButtonDocbot.x, coordsButtonDocbot.y, null);
		else
			graphics.drawImage(imageFactory.getImage(ImageType.BUTTON_LARGE_NORMAL), coordsButtonDocbot.x, coordsButtonDocbot.y, null);
	}

	@Override
	public void paintComponent(Graphics g)
	{
		screenBaseImage = getCompositeFromImages(getLeftScreen(), getRightScreen());
		super.paintComponent(g);
	}

	private ImageType getLeftScreen()
	{
		return currentEditorScreen;
	}
	
	private ImageType getRightScreen()
	{
		if (showingDetailedRoster)
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
			showingDetailedRoster = !showingDetailedRoster;
		else if (ScreenCommand.DRAFT_VIEW.equals(command))
			currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_DRAFT;
		else if (ScreenCommand.DOCBOT_VIEW.equals(command))
			currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_DOCBOT;
		else if (ScreenCommand.POWER_VIEW.equals(command))
			currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_POWER;
		else if (ScreenCommand.CANCEL.equals(command))
			confirmDiscardChanges();
		else if (ScreenCommand.EXIT.equals(command))
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
	
	protected void keyAction(ActionEvent keyAction)
	{
		//can't rename in detailed roster, so no need to fire the event to it
		generalRoster.keyAction(keyAction);
		
		//note that this should really only apply to the settings screen, but this covers all of them, just in case
		LegacyTeamEditorScreenDecorator currentScreen = screenMappings.get(currentEditorScreen);
		currentScreen.keyAction(keyAction);
	}

	private void refreshEnabledButtons()
	{
		for (ImageType screenTypeToRefresh : screenMappings.keySet())
		{
			LegacyTeamEditorScreenDecorator screenToRefresh = screenMappings.get(screenTypeToRefresh);
			
			if (screenTypeToRefresh == currentEditorScreen)
				screenToRefresh.enableButtons();
			else
				screenToRefresh.disableButtons();
		}
		
		if (showingDetailedRoster)
		{
			detailedRoster.enableButtons();
			generalRoster.disableButtons();
		}
		else
		{
			detailedRoster.disableButtons();
			generalRoster.enableButtons();
		}
	}

	@Override
	public void resetScreen()
	{
		currentPlayerIndex = 0;
		currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_START;
		
		for (ImageType key : screenSelected.keySet())
		{
			screenSelected.put(key, false);
			
			LegacyTeamEditorScreenDecorator screenToReset = screenMappings.get(key);
			if (screenToReset != null)
				screenToReset.resetScreen();
		}
		
		generalRoster.resetScreen();
		
		refreshEnabledButtons();
	}
	
	private void confirmDiscardChanges()
	{
		int response = JOptionPane.showConfirmDialog(this, "Your changes will be discarded.  Continue?", "Really Cancel?", JOptionPane.YES_NO_OPTION);
		
		if (response == JOptionPane.NO_OPTION)
			return;
		
		teamUpdater.loadTeam(originalTeam);
		resetScreen();
		fireAction(ScreenCommand.EXIT);
	}

	@Override
	public void setTeam(Team team)
	{
		if (team == null)
			teamUpdater.loadTeam(new Team());
		else
			teamUpdater.loadTeam(team);

		originalTeam = teamUpdater.getTeam().clone();
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
		maxBudget = budget;
	}
	
	@Override
	public int getBudget()
	{
		return maxBudget;
	}
	
	public void previousPlayer()
	{
		if (currentPlayerIndex > 0)
			currentPlayerIndex--;
	}
	
	public void nextPlayer()
	{
		if (currentPlayerIndex < Team.MAX_TEAM_SIZE - 1)
			currentPlayerIndex++;
	}
}
