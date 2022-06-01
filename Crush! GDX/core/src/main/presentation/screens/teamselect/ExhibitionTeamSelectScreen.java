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
import main.presentation.ImageType;
import main.presentation.common.ScreenCommand;
import main.presentation.game.FontType;
import main.presentation.game.GameText;
import main.presentation.legacy.common.LegacyUiConstants;

public class ExhibitionTeamSelectScreen extends AbstractTeamSelectScreen
{
	private List<ImageButton> buttons;
	
	public ExhibitionTeamSelectScreen(Game sourceGame, ActionListener eventListener)
	{
		super(sourceGame, eventListener, 3);
		
		buttons = new ArrayList<ImageButton>();
		buttons.add(addButton(37, 585, 41, false, ScreenCommand.MAIN_SCREEN));
		buttons.add(addButton(72, 486, 21, false, ScreenCommand.EXHIBITION_PREGAME));
		
		reset();
	}
	
	@Override
	public void reset()
	{
		clearTeams();
		super.reset();
	}

	@Override
	public List<Team> getTeamsForNextGame()
	{
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
		
		super.reset();	//should refresh the helmet images
		//TODO: THE ABOVE LINE CLEARS OUT THE BUTTONS!
		//		figure out a way to reset the screen without clearing the teams
		
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
	protected ImageType getSelectScreenImageType()
	{
		return ImageType.SCREEN_EXHIBITION_TEAM_SELECT;
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
		//TODO: for now do nothing
//		ScreenCommand command = ScreenCommand.fromActionEvent(e);
	}

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
		}
		
		//TODO: record that the match has concluded, show a victory screen immediately, and show one every time the player tries to click "start game"
	}

	@Override
	public void updateTeam(int index, Team team)
	{
		teams[index].setTeam(team);
	}

	@Override
	protected List<ImageButton> getButtons()
	{
		return buttons;
	}
}
