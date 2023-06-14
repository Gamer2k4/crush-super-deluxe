package main.presentation.screens.teamselect;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import main.data.DataImpl;
import main.data.entities.Team;
import main.logic.ai.coach.Coach;
import main.presentation.ImageType;
import main.presentation.common.ScreenCommand;
import main.presentation.game.FontType;
import main.presentation.game.GameText;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.screens.GameScreenManager;
import main.presentation.screens.ScreenType;
import main.presentation.screens.victory.ExhibitionVictoryScreen;

public class ExhibitionTeamSelectScreen extends AbstractTeamSelectScreen
{
	private static final int SETTINGS_Y = 366;
	
	private List<ImageButton> buttons = new ArrayList<ImageButton>();
	private ImageButton startButton = null;
	private ImageButton eventButton;
	private ImageButton victoryButton;
	
	private GameText winRequirementText;
	
	private GameText winReq1;
	private GameText winReq2;
	private GameText winReq4;
	private GameText winReqNA;
	
	private int winsNeeded = 1;
	
	public ExhibitionTeamSelectScreen(Game sourceGame, ActionListener eventListener)
	{
		super(sourceGame, eventListener, 3);
		
		eventButton = addButton(72, 486, 362, false, ScreenCommand.EXHIBITION_PREGAME);
		victoryButton = addButton(72, 486, 362, false, ScreenCommand.EXHIBITION_VICTORY);
		startButton = eventButton;
		
		buttons.add(addButton(37, 585, 342, false, ScreenCommand.MAIN_SCREEN));
		buttons.add(addClickZone(198, 369, 45, 9, ScreenCommand.CHANGE_WIN_REQUIREMENT));
		buttons.add(addClickZone(265, 369, 45, 9, ScreenCommand.CHANGE_BUDGET));
		buttons.add(addClickZone(331, 369, 61, 9, ScreenCommand.CHANGE_PACE));
		buttons.add(addClickZone(413, 369, 45, 9, ScreenCommand.CHANGE_TURNS));
		
		addHelmetClickZones(buttons);
		
		defineSettingsTexts();
		
		reset();
	}

	@Override
	public void reset()
	{
		clearTeams();
		startButton = eventButton;
		super.reset();
		
		winsNeeded = 1;
		winRequirementText = winReq1;
		eventCompleted = false;
	}

	@Override
	public List<Team> getTeamsForNextGame()
	{
		initializeBlankTeams();
		
		List<Team> rawTeams = new ArrayList<Team>();
		
		for (int i = 0; i < 3; i++)
		{
			rawTeams.add(teams[i].getTeam());
		}
		
		List<Team> teamsPlaying = getTeamsForGameStart(rawTeams);
		
		for (int i = 0; i < 3; i++)
		{
			teams[i].setTeam(teamsPlaying.get(i).clone());		//clone is important, so they don't get updated if the game is quit midway through
		}
		
		super.paintHelmetImages();	//not perfect, because it just paints them on top of the existing helmet actors, but it should be fine for now
		
		lockTeams();
		return teamsPlaying;
	}
	
	@Override
	protected List<Point> getHelmetLocations()
	{
		List<Point> locations = new ArrayList<Point>();
		locations.add(new Point(305, 228));
		locations.add(new Point(205, 124));
		locations.add(new Point(405, 124));
		return locations;
	}

	@Override
	protected List<Point> getTeamNameLocations()
	{
		List<Point> locations = new ArrayList<Point>();
		
		//the first two are purposely off-screen because I'll recreate one for each of them with either center or right alignment
		locations.add(new Point(1000, 0));
		locations.add(new Point(1000, 0));
		
		locations.add(new Point(440, 242));
		return locations;
	}

	@Override
	protected ImageType getBackgroundImageType()
	{
		return ImageType.BG_BG1;
	}

	@Override
	protected ImageType getTeamSelectScreenImageType()
	{
		return ImageType.SCREEN_EXHIBITION_TEAM_SELECT;
	}
	
	@Override
	protected void defineSettingsTexts()
	{
		super.defineSettingsTexts();
		
		winReq1 = new GameText(FontType.FONT_SMALL2, new Point(), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, "1 WIN");
		int width = winReq1.getStringPixelLength();
		winReq1.setCoords(new Point(198 + ((45 - width) / 2), SETTINGS_Y));

		winReq2 = new GameText(FontType.FONT_SMALL2, new Point(), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, "2 WINS");
		width = winReq2.getStringPixelLength();
		winReq2.setCoords(new Point(198 + ((45 - width) / 2), SETTINGS_Y));
		
		winReq4 = new GameText(FontType.FONT_SMALL2, new Point(), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, "4 WINS");
		width = winReq4.getStringPixelLength();
		winReq4.setCoords(new Point(198 + ((45 - width) / 2), SETTINGS_Y));
		
		winReqNA = new GameText(FontType.FONT_SMALL2, new Point(), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, "NA");
		width = winReqNA.getStringPixelLength();
		winReqNA.setCoords(new Point(198 + ((45 - width) / 2), SETTINGS_Y));
		
		winRequirementText = winReq1;
	}

	@Override
	protected void setBudgetTextPosition()
	{
		budget600.setCoords(new Point(279, SETTINGS_Y));
		budget900.setCoords(new Point(279, SETTINGS_Y));
	}

	@Override
	protected void setPaceTextPosition()
	{
		int width = paceRelaxed.getStringPixelLength();
		paceRelaxed.setCoords(new Point(331 + ((62 - width) / 2), SETTINGS_Y));
		
		width = paceStandard.getStringPixelLength();
		paceStandard.setCoords(new Point(331 + ((62 - width) / 2), SETTINGS_Y));
		
		width = paceFrenzied.getStringPixelLength();
		paceFrenzied.setCoords(new Point(331 + ((62 - width) / 2), SETTINGS_Y));
	}

	@Override
	protected void setTurnsTextPosition()
	{
		turns20.setCoords(new Point(430, SETTINGS_Y));
		turns25.setCoords(new Point(430, SETTINGS_Y));
		turns15.setCoords(new Point(430, SETTINGS_Y));
	}

	@Override
	protected List<GameText> getScreenTexts()
	{
		List<GameText> screenTexts = new ArrayList<GameText>();
		
		screenTexts.add(new GameText(FontType.FONT_SMALL2, new Point(285, 332), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, "Click on a helmet to edit a team."));
		screenTexts.add(new GameText(FontType.FONT_SMALL2, new Point(251, 340), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, "When finished, press start to begin the game."));
		
		getCenterAlignedTextsForTeam0(screenTexts);
		getRightAlignedTextsForTeam1(screenTexts);
		
		if (teamsLockedForEditing)
			getTeamRecords(screenTexts);
		
		screenTexts.add(winRequirementText);
		screenTexts.add(budgetText);
		screenTexts.add(paceText);
		screenTexts.add(turnsText);
		
		return screenTexts;
	}

	//TODO: this shouldn't be a performance problem, but technically I can save these GameTexts (and update them when the teams update), and abstract that
	//		throughout these screens as well
	private void getCenterAlignedTextsForTeam0(List<GameText> screenTexts)
	{
		TeamEntry team = teams[0];
		
		GameText coachName = new GameText(FontType.FONT_SMALL2, new Point(), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, "Coach " + team.getCoachName());
		int coachWidth = coachName.getStringPixelLength();
		coachName.setCoords(new Point(267 + ((107 - coachWidth) / 2), 111));
		screenTexts.add(coachName);
		
		GameText teamName = new GameText(FontType.FONT_SMALL, new Point(), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, team.getTeamName());
		int teamWidth = teamName.getStringPixelLength();
		teamName.setCoords(new Point(254 + ((134 - teamWidth) / 2), 115));
		screenTexts.add(teamName);
	}
	
	
	private void getRightAlignedTextsForTeam1(List<GameText> screenTexts)
	{
		TeamEntry team = teams[1];
		
		GameText coachName = new GameText(FontType.FONT_SMALL2, new Point(), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, "Coach " + team.getCoachName());
		int coachWidth = coachName.getStringPixelLength();
		coachName.setCoords(new Point(92 + (107 - coachWidth), 242));
		screenTexts.add(coachName);
		
		GameText teamName = new GameText(FontType.FONT_SMALL, new Point(), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, team.getTeamName());
		int teamWidth = teamName.getStringPixelLength();
		teamName.setCoords(new Point(65 + (134 - teamWidth), 246));
		screenTexts.add(teamName);
	}

	private void getTeamRecords(List<GameText> screenTexts)
	{
		Point[] textLocations = {new Point(301, 169),
								 new Point(201, 273),
								 new Point(401, 273)};
		
		for (int i = 0; i < 3; i++)
		{
			TeamEntry team = teams[i];
			
			GameText wins = new GameText(FontType.FONT_SMALL2, new Point(), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, "WINS: " + team.getWins());
			GameText losses = new GameText(FontType.FONT_SMALL2, new Point(), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, "LOSS: " + team.getLosses());
			GameText ties = new GameText(FontType.FONT_SMALL2, new Point(), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, "TIES: " + team.getTies());
			
			Point point = textLocations[i];
			
			wins.setCoords(new Point(point.x, point.y));
			losses.setCoords(new Point(point.x, point.y + 6));
			ties.setCoords(new Point(point.x, point.y + 12));
			
			screenTexts.add(wins);
			screenTexts.add(losses);
			screenTexts.add(ties);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		//if we're locked, no self-contained actions (that is, actions that apply only to this screen) should register
		if (teamsLockedForEditing)
			return;
		
		ScreenCommand command = ScreenCommand.fromActionEvent(e);
		
		switch(command)
		{
		case CHANGE_WIN_REQUIREMENT:
			changeWinRequirement();
			break;
		case CHANGE_BUDGET:
			changeBudget();
			break;
		case CHANGE_PACE:
			changePace();
			break;
		case CHANGE_TURNS:
			changeTurns();
			break;
		case EXHIBITION_PREGAME:
			checkBudgets();
			break;
		case POPUP_NO:
			hidePopup();
			refreshStage();
			break;
		}
	}

	private void checkBudgets()
	{
		List<String> teamsOverBudget = getTeamsOverBudget();
		
		if (!teamsOverBudget.isEmpty())
			showPopup(teamsOverBudget);
	}

	private void changeWinRequirement()
	{
		if (winsNeeded == 1)
		{
			winsNeeded = 2;
			winRequirementText = winReq2;
		}
		else if (winsNeeded == 2)
		{
			winsNeeded = 4;
			winRequirementText = winReq4;
		}
		else if (winsNeeded == 4)
		{
			winsNeeded = 0;
			winRequirementText = winReqNA;
		}
		else if (winsNeeded == 0)
		{
			winsNeeded = 1;
			winRequirementText = winReq1;
		}
	}

	@Override
	protected void setSimulationTextPosition() {}	//nothing to do on this screen

	@Override
	public void updateRecords(int gameWinner)
	{
		if (gameWinner == DataImpl.GAME_CANCELLED)
			return;
		
		for (int i = 0; i < 3; i++)
		{
			if (gameWinner == DataImpl.TIE_GAME)
				teams[i].addTie();
			else if (gameWinner == i)
				teams[i].addWin();
			else
				teams[i].addLoss();
			
			if (teams[i].isWinner(winsNeeded))
				updateVictoryScreen(teams[i].getTeam());
		}
	}

	private void updateVictoryScreen(Team team)
	{
		ExhibitionVictoryScreen victoryScreen = (ExhibitionVictoryScreen) GameScreenManager.getInstance().getScreen(ScreenType.EXHIBITION_VICTORY);
		
		boolean victoryResetSuccess = false;
		victoryScreen.reset();
		victoryScreen.setTeam(team);
		startButton = victoryButton;
		refreshStage();	//to get the correct start button on the screen
		eventCompleted = true;
	}

	@Override
	public void updateTeam(int index, Team team)
	{
		teams[index].setTeam(team);
		
		if (!team.humanControlled)
			teams[index].updateTeamByCoach();		//TODO: this is also triggering just upon leaving the team editor (which makes sense, since it's being "updated")
	}

	@Override
	public void updateCoach(int index, Coach coach)
	{
		teams[index].setCoach(coach);
	}

	@Override
	public ScreenType getVictoryScreenType()
	{
		return ScreenType.EXHIBITION_VICTORY;
	}

	@Override
	protected List<ImageButton> getButtons()
	{
		List<ImageButton> allButtons = new ArrayList<ImageButton>();
		
		allButtons.addAll(buttons);
		
		if (startButton != null)
			allButtons.add(startButton);
		
		if (popupIsActive())
			allButtons.addAll(getPopupButtons());
		
		return allButtons;
	}
}
