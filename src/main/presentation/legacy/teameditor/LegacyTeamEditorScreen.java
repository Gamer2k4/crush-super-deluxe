package main.presentation.legacy.teameditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import main.data.entities.Team;
import main.presentation.common.Logger;
import main.presentation.common.TeamEditor;
import main.presentation.common.image.ImageType;
import main.presentation.legacy.common.FontType;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.legacy.framework.AbstractLegacyScreen;
import main.presentation.legacy.framework.ClickableRegion;
import main.presentation.legacy.framework.GuiCommand;
import main.presentation.legacy.framework.KeyCommand;
import main.presentation.legacy.framework.ScreenCommand;
import main.presentation.teameditor.common.TeamUpdater;

public class LegacyTeamEditorScreen extends AbstractLegacyScreen implements TeamEditor
{
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
	private Map<ImageType, ClickableRegion> buttonHighlighted;
	private Map<ImageType, AbstractLegacyTeamEditorSubScreen> screenMappings = new HashMap<ImageType, AbstractLegacyTeamEditorSubScreen>();

	private ImageType currentEditorScreen;
	private boolean showingDetailedRoster;
	private boolean poolDrafting;
	private boolean detailedPool;

	private LegacyTeamEditorGeneralRoster generalRoster;
	private LegacyTeamEditorDetailedRoster detailedRoster;
	
	protected TeamUpdater teamUpdater;
	
	private Team originalTeam;	//the team as it was before any changes are made (the one reverted to if clicking "Back" instead of "Done")
	private int maxBudget = 900;

	public LegacyTeamEditorScreen(ActionListener listener)
	{
		super(listener, ImageType.BG_BG4);
		
//		super(imageFactory.getImage(ImageType.BG_BG4), getCompositeFromImages(ImageType.SCREEN_TEAM_EDITOR_START, ImageType.SCREEN_TEAM_EDITOR_ROSTER_GENERAL));		
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
		
		screenMappings.put(ImageType.SCREEN_TEAM_EDITOR_SETTINGS, new LegacyTeamEditorSettingsScreen(teamUpdater, listener));
		screenMappings.put(ImageType.SCREEN_TEAM_EDITOR_DRAFT, new LegacyTeamEditorStockDraftScreen(teamUpdater, listener, maxBudget));
		
		//TODO: Below was an okay thought, but the object-oriented approach I'm using now is probably better.  Delete the comment once it's completely irrelevant.
		//To add the click mappings for individual screens, just paint them, copy the screen section over to an image, black it out, then paint the next ones,
		//saving those as well.  Then, when switching screens, also switch the click maps.  

		currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_START;
		showingDetailedRoster = false;
		poolDrafting = false;
		detailedPool = false;
		
		generalRoster = new LegacyTeamEditorGeneralRoster(teamUpdater, listener);
		detailedRoster = new LegacyTeamEditorDetailedRoster(teamUpdater, listener);
		refreshEnabledButtons();
	}
	
	public void beginPoolDraft()
	{
		poolDrafting = true;
	}

	@Override
	protected void handleCommand(ScreenCommand command)
	{
		Logger.output("Command received for team editor screen: " + command);
		
		if (command.isEditorViewChange())
			screenSelected.put(currentEditorScreen, false);
//		
		if (ScreenCommand.SETTINGS_VIEW.equals(command))
			currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_SETTINGS;
//		else if (ScreenCommand.ACQUIRE_VIEW.equals(command))
//			currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_ACQUIRE;
		else if (ScreenCommand.TOGGLE_ROSTER_VIEW.equals(command))
			showingDetailedRoster = !showingDetailedRoster;
		else if (ScreenCommand.DRAFT_VIEW.equals(command))
			currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_DRAFT;
//		else if (ScreenCommand.DOCBOT_VIEW.equals(command))
//			currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_DOCBOT;
//		else if (ScreenCommand.POWER_VIEW.equals(command))
//			currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_POWER;
		if (ScreenCommand.EXIT_TEAM_EDITOR_BACK.equals(command))
			confirmDiscardChanges();
		else if (ScreenCommand.EXIT_TEAM_EDITOR_DONE.equals(command))
		{
			resetScreen();
			fireAction(command);
		}
		else
		{
			AbstractLegacyTeamEditorSubScreen screenToAct = screenMappings.get(currentEditorScreen);
			if (screenToAct != null)
				screenToAct.receiveScreenCommand(command);
			
			//active/inactive should handle this
			generalRoster.receiveScreenCommand(command);
			detailedRoster.receiveScreenCommand(command);
		}
		
		if (command.isEditorViewChange())
		{
			screenSelected.put(currentEditorScreen, true);
			refreshEnabledButtons();
		}

		updateScreenImage();
	}
	
	@Override
	public void receiveGuiCommand(GuiCommand command)
	{
		super.receiveGuiCommand(command);
		
		if (showingDetailedRoster)
			detailedRoster.receiveGuiCommand(command);
		else
			generalRoster.receiveGuiCommand(command);
		
		AbstractLegacyTeamEditorSubScreen currentScreen = screenMappings.get(currentEditorScreen);
		
		if (currentScreen != null)
			currentScreen.receiveGuiCommand(command);
		
		updateScreenImage();
	}

	@Override
	protected void handleKeyCommand(KeyCommand command)
	{
		//can't rename in detailed roster, so no need to fire the event to it
		generalRoster.receiveKeyCommand(command);
		
		//note that this should really only apply to the settings screen, but this covers all of them, just in case
		AbstractLegacyTeamEditorSubScreen currentScreen = screenMappings.get(currentEditorScreen);
		currentScreen.receiveKeyCommand(command);
		
		updateScreenImage();
	}

	private void refreshEnabledButtons()
	{
		for (ImageType screenTypeToRefresh : screenMappings.keySet())
		{
			AbstractLegacyTeamEditorSubScreen screenToRefresh = screenMappings.get(screenTypeToRefresh);
			
			if (screenTypeToRefresh == currentEditorScreen)
				screenToRefresh.activate();
			else
				screenToRefresh.deactivate();
		}
		
		if (showingDetailedRoster)
		{
			detailedRoster.activate();
			generalRoster.deactivate();
		}
		else
		{
			detailedRoster.deactivate();
			generalRoster.activate();
		}
	}

	@Override
	public void resetScreen()
	{
		teamUpdater.setCurrentPlayerIndex(0);
		currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_START;
		showingDetailedRoster = false;
		
		for (ImageType key : screenSelected.keySet())
		{
			screenSelected.put(key, false);
			
			AbstractLegacyTeamEditorSubScreen screenToReset = screenMappings.get(key);
			if (screenToReset != null)
				screenToReset.resetScreen();
		}
		
		generalRoster.resetScreen();
		
		refreshEnabledButtons();
	}
	
	private void confirmDiscardChanges()
	{
		int response = JOptionPane.showConfirmDialog(null, "Your changes will be discarded.  Continue?", "Really Cancel?", JOptionPane.YES_NO_OPTION);
		
		if (response == JOptionPane.NO_OPTION)
			return;
		
		teamUpdater.loadTeam(originalTeam);
		resetScreen();
		fireAction(ScreenCommand.EXIT_TEAM_EDITOR_DONE);
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
		
		LegacyTeamEditorStockDraftScreen draftScreen = (LegacyTeamEditorStockDraftScreen) screenMappings.get(ImageType.SCREEN_TEAM_EDITOR_DRAFT);
		draftScreen.setBudget(budget);
	}
	
	@Override
	public int getBudget()
	{
		return maxBudget;
	}

	@Override
	protected void defineClickableRegions()
	{
		createClickZone(new Rectangle(coordsButtonBack, buttonDimSmall2), ClickableRegion.smallButton2(coordsButtonBack, ScreenCommand.EXIT_TEAM_EDITOR_BACK));
		createClickZone(new Rectangle(coordsButtonDone, buttonDimSmall2), ClickableRegion.smallButton2(coordsButtonDone, ScreenCommand.EXIT_TEAM_EDITOR_DONE));
		
		createClickZone(new Rectangle(coordsButtonSettings, buttonDimLarge), createAndMapRegion(ImageType.SCREEN_TEAM_EDITOR_SETTINGS, ClickableRegion.largeButton(coordsButtonSettings, ScreenCommand.SETTINGS_VIEW)));
		createClickZone(new Rectangle(coordsButtonEquipment, buttonDimLarge), createAndMapRegion(ImageType.SCREEN_TEAM_EDITOR_ACQUIRE, ClickableRegion.largeButton(coordsButtonEquipment, ScreenCommand.ACQUIRE_VIEW)));
		createClickZone(new Rectangle(coordsButtonRosters, buttonDimLarge), ClickableRegion.largeButton(coordsButtonRosters, ScreenCommand.TOGGLE_ROSTER_VIEW));
		createClickZone(new Rectangle(coordsButtonStats, buttonDimLarge), ClickableRegion.largeButton(coordsButtonStats, ScreenCommand.STATS_VIEW));
		createClickZone(new Rectangle(coordsButtonDraft, buttonDimLarge), createAndMapRegion(ImageType.SCREEN_TEAM_EDITOR_DRAFT, ClickableRegion.largeButton(coordsButtonDraft, ScreenCommand.DRAFT_VIEW)));
		createClickZone(new Rectangle(coordsButtonDocbot, buttonDimLarge), createAndMapRegion(ImageType.SCREEN_TEAM_EDITOR_DOCBOT, ClickableRegion.largeButton(coordsButtonDocbot, ScreenCommand.DOCBOT_VIEW)));
		createClickZone(new Rectangle(coordsButtonTrainer, buttonDimLarge), createAndMapRegion(ImageType.SCREEN_TEAM_EDITOR_POWER, ClickableRegion.largeButton(coordsButtonTrainer, ScreenCommand.POWER_VIEW)));
		createClickZone(new Rectangle(coordsButtonSchedule, buttonDimLarge), ClickableRegion.largeButton(coordsButtonSchedule, ScreenCommand.SCHEDULE_VIEW));
	}

	private ClickableRegion createAndMapRegion(ImageType screenType, ClickableRegion region)
	{
		//i don't like this, but i think it's necessary because otherwise it doesn't get defined before reaching this, even if i do the "new" in the definition	
		if (buttonHighlighted == null)
			buttonHighlighted = new HashMap<ImageType, ClickableRegion>();
		
		buttonHighlighted.put(screenType, region);
		return region;
	}

	@Override
	protected void paintComponent(Graphics2D graphics)
	{
		drawSubScreens(graphics);
		paintText(graphics);
	}

	private void drawSubScreens(Graphics2D graphics)
	{
		if (showingDetailedRoster)
		{
			detailedRoster.updateScreenImage();
			graphics.drawImage(detailedRoster.getScreenImage(), 383, 10, null);
		}
		else
		{
			generalRoster.updateScreenImage();
			graphics.drawImage(generalRoster.getScreenImage(), 383, 10, null);
		}
		
		AbstractLegacyTeamEditorSubScreen screenToPaint = screenMappings.get(currentEditorScreen);
		
		if (screenToPaint != null)
		{
			screenToPaint.updateScreenImage();
			graphics.drawImage(screenToPaint.getScreenImage(), 0, 0, null);
		}
		else
		{
			graphics.drawImage(imageFactory.getImage(ImageType.SCREEN_TEAM_EDITOR_START), 0, 0, null);
		}
	}

	private void paintText(Graphics2D graphics)
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
	protected Set<ClickableRegion> getAlwaysHighlightedRegions()
	{
		Set<ClickableRegion> highlightedRegions = new HashSet<ClickableRegion>();
		
		ClickableRegion region = buttonHighlighted.get(currentEditorScreen);
		
		if (region != null)
			highlightedRegions.add(region);
		
		return highlightedRegions;
	}
}
