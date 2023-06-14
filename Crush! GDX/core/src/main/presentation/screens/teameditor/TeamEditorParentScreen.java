package main.presentation.screens.teameditor;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import main.data.entities.Team;
import main.data.factory.CpuTeamFactory;
import main.logic.ai.coach.Coach;
import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.common.Logger;
import main.presentation.common.ScreenCommand;
import main.presentation.game.GameText;
import main.presentation.game.sprite.CrushSprite;
import main.presentation.screens.GameScreenManager;
import main.presentation.screens.ScreenType;
import main.presentation.screens.popupready.YesNoPopupScreen;
import main.presentation.screens.teameditor.utilities.TeamUpdater;
import main.presentation.screens.teamselect.AbstractTeamSelectScreen;

public class TeamEditorParentScreen extends YesNoPopupScreen implements TeamEditor
{
	private ScreenType originScreen = null;
	private int teamIndex = -1;
	private boolean teamsLockedForEditing = false;
	private boolean showingKeepTeamPopup = false;
	private boolean showingDiscardChangesPopup = false;
	
	private List<ImageButton> buttons = new ArrayList<ImageButton>();
	private ImageButton backButton = addButton(37, 585, 353, false, ScreenCommand.EXIT_TEAM_EDITOR_BACK);
	private ImageButton doneButton = addButton(37, 585, 374, false, ScreenCommand.EXIT_TEAM_EDITOR_DONE);
	private ImageButton settingsButton = addButton(72, 85, 353, false, ScreenCommand.SETTINGS_VIEW);
	private ImageButton equipmentButton = addButton(72, 170, 353, false, ScreenCommand.OUTFIT_VIEW);
	private ImageButton rosterToggleButton = addButton(72, 255, 353, false, ScreenCommand.TOGGLE_ROSTER_VIEW);
	private ImageButton statsButton = addButton(72, 340, 353, false, ScreenCommand.STATS_VIEW);
	private ImageButton draftButton = addButton(72, 85, 374, false, ScreenCommand.DRAFT_VIEW);
	private ImageButton docbotButton = addButton(72, 170, 374, false, ScreenCommand.DOCBOT_VIEW);
	private ImageButton trainerButton = addButton(72, 255, 374, false, ScreenCommand.POWER_VIEW);
	
	private Map<ImageType, AbstractTeamEditorSubScreen> screenMappings = new HashMap<ImageType, AbstractTeamEditorSubScreen>();
	
	private ImageType currentEditorScreen;
	private boolean showingDetailedRoster;
	
	private TeamEditorGeneralRoster generalRoster;
	private TeamEditorDetailedRoster detailedRoster;
	
	private TeamUpdater teamUpdater;
	
	private Team originalTeam;	//the team as it was before any changes are made (the one reverted to if clicking "Back" instead of "Done")
	
	public TeamEditorParentScreen(Game sourceGame, ActionListener eventListener)
	{
		super(sourceGame, eventListener);
		
		teamUpdater = new TeamUpdater();
		
		screenMappings.put(ImageType.SCREEN_TEAM_EDITOR_START, new TeamEditorStartScreen());
		screenMappings.put(ImageType.SCREEN_TEAM_EDITOR_SETTINGS, new TeamEditorSettingsScreen(this));
		screenMappings.put(ImageType.SCREEN_TEAM_EDITOR_ACQUIRE, new TeamEditorAcquireScreen(this));
		screenMappings.put(ImageType.SCREEN_TEAM_EDITOR_OUTFIT, new TeamEditorOutfitScreen(this));
		screenMappings.put(ImageType.SCREEN_TEAM_EDITOR_DRAFT, new TeamEditorStockDraftScreen(this));
		screenMappings.put(ImageType.SCREEN_TEAM_EDITOR_DOCBOT, new TeamEditorDocbotScreen(this));
		screenMappings.put(ImageType.SCREEN_TEAM_EDITOR_POWER, new TeamEditorPowerTrainerScreen(this));
		screenMappings.put(ImageType.SCREEN_TEAM_EDITOR_AGILITY, new TeamEditorAgilityTrainerScreen(this));
		screenMappings.put(ImageType.SCREEN_TEAM_EDITOR_PSYCHE, new TeamEditorPsycheTrainerScreen(this));
		
		currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_START;
		showingDetailedRoster = false;
		
		generalRoster = new TeamEditorGeneralRoster(this);
		detailedRoster = new TeamEditorDetailedRoster(this);
		
		resetBaseButtons();
		
		teamUpdater.addUpdateListener(this);
	}
	
	private void resetBaseButtons()
	{
		buttons.clear();
		buttons.add(backButton);
		buttons.add(doneButton);
		buttons.add(settingsButton);
		buttons.add(equipmentButton);
		buttons.add(rosterToggleButton);
		buttons.add(statsButton);
		buttons.add(draftButton);
		buttons.add(docbotButton);
		buttons.add(trainerButton);
	}
	
	@Override
	public void reset()
	{
		hidePopup();
		
		AbstractTeamSelectScreen originTeamSelectScreen = (AbstractTeamSelectScreen) GameScreenManager.getInstance().getScreen(getOriginScreen());
		teamsLockedForEditing = originTeamSelectScreen.areTeamsLockedForEditing();
		
		teamUpdater.setCurrentPlayerIndex(0);
		
		currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_START;
		showingDetailedRoster = false;
		
		resetBaseButtons();
		uncheckAllButtons();
		
		for (ImageType key : screenMappings.keySet())
		{
			AbstractTeamEditorSubScreen screen = screenMappings.get(key);
			screen.refreshContent();
		}
		
		generalRoster.refreshContent();
		detailedRoster.refreshContent();
		
		refreshStage();
	}
	
	private void uncheckAllButtons()
	{
		settingsButton.setChecked(false);
		equipmentButton.setChecked(false);
		draftButton.setChecked(false);
		docbotButton.setChecked(false);
		trainerButton.setChecked(false);
	}
	
	private void refreshStage()
	{
		stage.clear();
		stage.addActor(new Image(ImageFactory.getInstance().getDrawable(ImageType.BG_BG4)));
		
		AbstractTeamEditorSubScreen mainScreen = screenMappings.get(currentEditorScreen);
		AbstractTeamEditorRosterScreen rosterScreen = generalRoster;
		
		if (showingDetailedRoster)
			rosterScreen = detailedRoster;
		
		for (Actor actor : mainScreen.getActors())
		{
			stage.addActor(actor);
		}
		
		for (Actor actor : rosterScreen.getActors())
		{
			stage.addActor(actor);
		}
		
		//don't add any more buttons if there's a popup, since that's all you should be able to manipulate
		if (popupIsActive())
			return;

		for (ImageButton button : getButtons())
		{
			stage.addActor(button);
		}
		
		for (ImageButton button : mainScreen.getScreenButtons())
		{
			stage.addActor(button);
		}
		
		for (ImageButton button : rosterScreen.getScreenButtons())
		{
			stage.addActor(button);
		}
	}
	
	private void refreshScreensForTeamUpdate()
	{
		generalRoster.refreshContent();
	}
	
	@Override
	public List<GameText> getStaticText()
	{
		AbstractTeamEditorSubScreen mainScreen = screenMappings.get(currentEditorScreen);
		AbstractTeamEditorRosterScreen rosterScreen = generalRoster;
		
		if (showingDetailedRoster)
			rosterScreen = detailedRoster;
		
		List<GameText> gameTexts = new ArrayList<GameText>();
		
		gameTexts.addAll(mainScreen.getScreenTexts());
		gameTexts.addAll(rosterScreen.getScreenTexts());
		
		return gameTexts;
	}
	
	@Override
	public List<CrushSprite> getStaticSprites()
	{
		AbstractTeamEditorSubScreen mainScreen = screenMappings.get(currentEditorScreen);
		
		List<CrushSprite> staticSprites = new ArrayList<CrushSprite>();
		
		staticSprites.addAll(mainScreen.getStaticSprites());
		
		return staticSprites;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		if (!isActive)
			return;
		
		String actionCommand = event.getActionCommand();
		
		if (actionCommand.endsWith(":ENTER"))
		{
			String[] split = actionCommand.split(":");
			delegateMouseEnterEventToActiveScreens(new ActionEvent(event.getSource(), event.getID() + 1, split[0]));
			return;
		}
		
		if (actionCommand.endsWith(":EXIT"))
		{
			String[] split = actionCommand.split(":");
			delegateMouseExitEventToActiveScreens(new ActionEvent(event.getSource(), event.getID() + 1, split[0]));
			return;
		}
		
		if (actionCommand.equals(TeamUpdater.UPDATER_NEW_TEAM))
		{
			Logger.debug("Team update detected; refreshing all screens with new information.");
			refreshScreensForTeamUpdate();
			return;
		}
		
		if (actionCommand.equals(AbstractTeamEditorSubScreen.REFRESH_STAGE))
		{
			Logger.debug("Refresh stage event received in Team Editor Parent Screen.");
			refreshStage();
			return;
		}
		
		//team updater action events can't be converted to screen commands
		if (actionCommand.startsWith("UPDATER_"))
			return;
		
		ScreenCommand command = ScreenCommand.fromActionEvent(event);
		
		if (command.isEditorViewChange())
			uncheckAllButtons();
		
		Team team = teamUpdater.getTeam();
		
		switch(command)
		{
		case SETTINGS_VIEW:
			currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_SETTINGS;
			settingsButton.setChecked(true);
			break;
		case ACQUIRE_VIEW:
			currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_ACQUIRE;
			equipmentButton.setChecked(true);
			break;
		case OUTFIT_VIEW:
			currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_OUTFIT;
			equipmentButton.setChecked(true);
			break;
		case TOGGLE_ROSTER_VIEW:
			showingDetailedRoster = !showingDetailedRoster;
			break;
		case DRAFT_VIEW:
			currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_DRAFT;
			draftButton.setChecked(true);
			break;
		case DOCBOT_VIEW:
			currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_DOCBOT;
			docbotButton.setChecked(true);
			break;
		case POWER_VIEW:
			currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_POWER;
			trainerButton.setChecked(true);
			break;
		case AGILITY_VIEW:
			currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_AGILITY;
			trainerButton.setChecked(true);
			break;
		case PSYCHE_VIEW:
			currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_PSYCHE;
			trainerButton.setChecked(true);
			break;
		case STATS_VIEW:
			//handled by CrushGame
			break;
		case EXIT_TEAM_EDITOR_BACK:
			showingDiscardChangesPopup = true;
			showPopup("DISCARD CHANGES");
			break;
		case CONTROLLER_HUMAN:
			team.humanControlled = true;
			if (team.isBlankTeam() || teamsLockedForEditing)
				break;	//hop right to refreshing the stage
			
			showingKeepTeamPopup = true;
			showPopup("KEEP TEAM");
			return;
		case CONTROLLER_AI:
			team.humanControlled = false;
			if (teamsLockedForEditing)
				break;	//hop right to refreshing the stage
			
			if (team.isBlankTeam())
			{
				loadTeamForControllerChange();
				return;
			}
			
			teamUpdater.getTeam().humanControlled = false;
			showingKeepTeamPopup = true;
			showPopup("KEEP TEAM");
			return;
		case POPUP_YES:
			handlePopupYes();
			break;
		case POPUP_NO:		
			handlePopupNo();
			break;
		}
		
		if (!command.isEditorViewChange() && !command.name().startsWith("CONTROLLER_"))
			delegateCommandToActiveScreens(command);
		
		refreshStage();
	}

	private void delegateCommandToActiveScreens(ScreenCommand command)
	{
		AbstractTeamEditorSubScreen mainScreen = screenMappings.get(currentEditorScreen);
		mainScreen.handleCommand(command);
		
		if (showingDetailedRoster)
			detailedRoster.handleCommand(command);
		else
			generalRoster.handleCommand(command);
	}

	private void delegateMouseEnterEventToActiveScreens(ActionEvent event)
	{
		AbstractTeamEditorSubScreen mainScreen = screenMappings.get(currentEditorScreen);
		mainScreen.handleMouseEnterEvent(event);
		
		if (showingDetailedRoster)
			detailedRoster.handleMouseEnterEvent(event);
		else
			generalRoster.handleMouseEnterEvent(event);
	}

	private void delegateMouseExitEventToActiveScreens(ActionEvent event)
	{
		AbstractTeamEditorSubScreen mainScreen = screenMappings.get(currentEditorScreen);
		mainScreen.handleMouseExitEvent(event);
		
		if (showingDetailedRoster)
			detailedRoster.handleMouseExitEvent(event);
		else
			generalRoster.handleMouseExitEvent(event);
	}
	
	private void handlePopupYes()
	{
		hidePopup();
		if (showingDiscardChangesPopup)
		{
			discardChanges();
			showingDiscardChangesPopup = false;
			return;
		}
		else if (showingKeepTeamPopup)
		{
			showingKeepTeamPopup = false;
			return;
		}
	}
	
	private void handlePopupNo()
	{
		hidePopup();
		if (showingDiscardChangesPopup)
		{
			showingDiscardChangesPopup = false;
			return;
		}
		else if (showingKeepTeamPopup)
		{
			showingKeepTeamPopup = false;
			loadTeamForControllerChange();
		}
	}
	
	private void loadTeamForControllerChange()
	{
		boolean humanControlled = teamUpdater.getTeam().humanControlled;
		
		Coach coach = new Coach();
		Team newTeam = new Team();
		if (!humanControlled)
		{
			newTeam = CpuTeamFactory.getInstance().generateEmptyCpuTeam();
			coach.draftForTeam(newTeam, teamUpdater.getMaxBudget());
		}
		
		newTeam.humanControlled = humanControlled;
		teamUpdater.loadTeam(newTeam);
		updateAiCoach(coach);
		
		return;
	}
	
	private void updateAiCoach(Coach coach)
	{
		AbstractTeamSelectScreen teamSelectScreen = (AbstractTeamSelectScreen) GameScreenManager.getInstance().getScreen(getOriginScreen());
		teamSelectScreen.updateCoach(teamIndex, coach);
	}

	private void discardChanges()
	{
		teamUpdater.loadTeam(originalTeam);
		eventListener.actionPerformed(ScreenCommand.EXIT_TEAM_EDITOR_DONE.asActionEvent());
	}

	@Override
	public void setTeamIndex(int index)
	{
		teamIndex = index;
	}

	@Override
	public int getTeamIndex()
	{
		return teamIndex;
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
	public TeamUpdater getTeamUpdater()
	{
		return teamUpdater;
	}

	@Override
	public void setLoadEnabled(boolean isEnabled)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBudget(int budget)
	{
		teamUpdater.setMaxBudget(budget);
	}

	@Override
	public int getBudget()
	{
		return teamUpdater.getMaxBudget();
	}

	@Override
	public void setOriginScreen(ScreenType originScreen)
	{
		this.originScreen = originScreen;
	}

	@Override
	public ScreenType getOriginScreen()
	{
		return originScreen;
	}

	@Override
	public boolean areTeamsLockedForEditing()
	{
		return teamsLockedForEditing;
	}

	@Override
	protected List<ImageButton> getButtons()
	{
		List<ImageButton> allButtons = new ArrayList<ImageButton>();
		
		allButtons.addAll(buttons);
		
		return allButtons;
	}
	
	//these are only used for drag tracking at the moment
	@Override
	public void update()
	{
		AbstractTeamEditorSubScreen mainScreen = screenMappings.get(currentEditorScreen);
		
		if (!mainScreen.dragEnabled())
			return;
		
		if (Gdx.input.isTouched())
		{
			Point cursorCoords = convertMouseCoordinates(Gdx.input.getX(), Gdx.input.getY());

//			System.out.println("Mouse click processed! Coordinates are: " + cursorCoords);
			mainScreen.mouseClicked(cursorCoords);
		}
		else
		{
			mainScreen.mouseReleased();
		}
	}
	
	private Point convertMouseCoordinates(int x, int y)
	{
		int curWidth = Gdx.graphics.getWidth();
		int curHeight = Gdx.graphics.getHeight();
		
		double xProportion = LEGACY_SCREEN_DIMENSION.width / (double)curWidth;
		double yProportion = LEGACY_SCREEN_DIMENSION.height / (double)curHeight;
		
		return new Point((int)((x * xProportion) + .5), (int)((y * yProportion) + .5));
	}
}
