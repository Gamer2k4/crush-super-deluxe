package main.presentation.screens.teameditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import main.data.entities.Team;
import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.common.Logger;
import main.presentation.common.ScreenCommand;
import main.presentation.game.GameText;
import main.presentation.screens.GameScreenManager;
import main.presentation.screens.ScreenType;
import main.presentation.screens.StandardButtonScreen;
import main.presentation.screens.teameditor.utilities.TeamUpdater;
import main.presentation.screens.teamselect.AbstractTeamSelectScreen;

public class TeamEditorParentScreen extends StandardButtonScreen implements TeamEditor
{
	private ScreenType originScreen = null;
	private int teamIndex = -1;
	private boolean teamsLockedForEditing = false;
	
	private List<ImageButton> buttons = new ArrayList<ImageButton>();
	private ImageButton settingsButton = addButton(72, 85, 353, false, ScreenCommand.SETTINGS_VIEW);
	private ImageButton equipmentButton = addButton(72, 170, 353, false, ScreenCommand.ACQUIRE_VIEW);
	private ImageButton draftButton = addButton(72, 85, 374, false, ScreenCommand.DRAFT_VIEW);
	private ImageButton docbotButton = addButton(72, 170, 374, false, ScreenCommand.DOCBOT_VIEW);
	private ImageButton trainerButton = addButton(72, 255, 374, false, ScreenCommand.DOCBOT_VIEW);
	
	private Map<ImageType, AbstractTeamEditorSubScreen> screenMappings = new HashMap<ImageType, AbstractTeamEditorSubScreen>();
	
	private ImageType currentEditorScreen;
	private boolean showingDetailedRoster;
	
	private TeamEditorGeneralRoster generalRoster;
	private TeamEditorDetailedRoster detailedRoster;
	
	private TeamUpdater teamUpdater;
	
	private Team originalTeam;	//the team as it was before any changes are made (the one reverted to if clicking "Back" instead of "Done")
	private int maxBudget = 900;
	
	public TeamEditorParentScreen(Game sourceGame, ActionListener eventListener)
	{
		super(sourceGame, eventListener);
		
		teamUpdater = new TeamUpdater();
		
		screenMappings.put(ImageType.SCREEN_TEAM_EDITOR_START, new TeamEditorStartScreen());
		screenMappings.put(ImageType.SCREEN_TEAM_EDITOR_SETTINGS, new TeamEditorSettingsScreen(this));
		screenMappings.put(ImageType.SCREEN_TEAM_EDITOR_DRAFT, new TeamEditorDraftScreen(this));
		screenMappings.put(ImageType.SCREEN_TEAM_EDITOR_DOCBOT, new TeamEditorDocbotScreen(this));
		
		currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_START;
		showingDetailedRoster = false;
		
		generalRoster = new TeamEditorGeneralRoster(this);
		detailedRoster = new TeamEditorDetailedRoster(this);
		
		buttons.add(addButton(37, 585, 353, false, ScreenCommand.EXIT_TEAM_EDITOR_BACK));
		buttons.add(addButton(37, 585, 374, false, ScreenCommand.EXIT_TEAM_EDITOR_DONE));
		buttons.add(settingsButton);
		buttons.add(equipmentButton);
		buttons.add(addButton(72, 255, 353, false, ScreenCommand.TOGGLE_ROSTER_VIEW));
		buttons.add(draftButton);
		buttons.add(docbotButton);
		buttons.add(trainerButton);
		
		teamUpdater.addUpdateListener(this);
	}
	
	@Override
	public void reset()
	{
		AbstractTeamSelectScreen originTeamSelectScreen = (AbstractTeamSelectScreen) GameScreenManager.getInstance().getScreen(getOriginScreen());
		teamsLockedForEditing = originTeamSelectScreen.areTeamsLockedForEditing();
		
		teamUpdater.setCurrentPlayerIndex(0);
		
		currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_START;
		showingDetailedRoster = false;
		
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
	public void actionPerformed(ActionEvent e)
	{
		if (!isActive)
			return;
		
		if (e.getActionCommand().equals(TeamUpdater.UPDATER_NEW_TEAM))
		{
			Logger.debug("Team update detected; refreshing all screens with new information.");
			refreshScreensForTeamUpdate();
			return;
		}
		
		//team updater action events can't be converted to screen commands
		if (e.getActionCommand().startsWith("UPDATER_"))
			return;
		
		ScreenCommand command = ScreenCommand.fromActionEvent(e);
		
		if (command.isEditorViewChange())
			uncheckAllButtons();
		
		switch(command)
		{
		case SETTINGS_VIEW:
			currentEditorScreen = ImageType.SCREEN_TEAM_EDITOR_SETTINGS;
			settingsButton.setChecked(true);
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
		case EXIT_TEAM_EDITOR_BACK:
			discardChanges();
			break;
		}
		
		if (!command.isEditorViewChange())
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

	private void discardChanges()
	{
		// TODO show a confirmation popup first
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
		maxBudget = budget;
	}

	@Override
	public int getBudget()
	{
		return maxBudget;
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
		
		//TODO: reference ExhibitionTeamSelectScreen for disabled or conditional buttons
		
		return allButtons;
	}
}
