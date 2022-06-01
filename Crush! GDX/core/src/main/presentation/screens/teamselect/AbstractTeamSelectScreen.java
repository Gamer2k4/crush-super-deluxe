package main.presentation.screens.teamselect;

import java.awt.Point;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import main.data.entities.Team;
import main.data.factory.CpuTeamFactory;
import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.game.FontType;
import main.presentation.game.GameText;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.screens.StandardButtonScreen;
import main.presentation.teameditor.common.TeamUpdater;

public abstract class AbstractTeamSelectScreen extends StandardButtonScreen
{
	private int totalTeams;
	protected TeamEntry[] teams;
	protected TeamUpdater teamUpdater;
	protected boolean teamsLockedForEditing = false;	//This just means the USER can't change anything anymore, include budget and the like.  The game is still free to do whatever.
	protected int budget = 900;
	
	protected AbstractTeamSelectScreen(Game sourceGame, ActionListener eventListener, int totalTeams)
	{
		super(sourceGame, eventListener);
		
		this.totalTeams = totalTeams;
		teamUpdater = new TeamUpdater();
		clearTeams();
	}
	
	protected void clearTeams()
	{
		teams = new TeamEntry[totalTeams];
		
		for (int i = 0; i < totalTeams; i++)
		{
			teams[i] = new TeamEntry();
		}
	}

	@Override
	public void reset()
	{
		teamsLockedForEditing = false;
		stage.clear();
		stage.addActor(new Image(ImageFactory.getInstance().getDrawable(getBackgroundImageType())));
		stage.addActor(new Image(ImageFactory.getInstance().getDrawable(getSelectScreenImageType())));
		paintHelmetImages();
		
		for (ImageButton button : getButtons())
		{
			stage.addActor(button);
		}
	}
	
	public void lockTeams()
	{
		teamsLockedForEditing = true;
	}
	
	public void unlockTeams()
	{
		teamsLockedForEditing = false;
	}
	
	public int getBudget()
	{
		return budget;
	}
	
	private void paintHelmetImages()
	{
		List<Point> helmetLocations = getHelmetLocations();
		
		for (int i = 0; i < helmetLocations.size(); i++)
		{
			Point coords = helmetLocations.get(i);
			Image helmetImage = new Image(teams[i].getHelmetImage());
			helmetImage.setPosition(coords.x, coords.y);
			stage.addActor(helmetImage);
		}
	}
	
	@Override
	public List<GameText> getStaticText()
	{
		List<GameText> gameTexts = new ArrayList<GameText>();
		
		gameTexts.addAll(getScreenTexts());
		gameTexts.addAll(getTeamAndCoachNames());
		
		return gameTexts;
	}

	private List<GameText> getTeamAndCoachNames()
	{
		List<GameText> nameTexts = new ArrayList<GameText>();
		List<Point> nameLocations = getTeamNameLocations();
		
		for (int i = 0; i < nameLocations.size(); i++)
		{
			TeamEntry team = teams[i];
			Point coachCoords = nameLocations.get(i);
			Point teamCoords = new Point(coachCoords.x, coachCoords.y + 4);
			nameTexts.add(new GameText(FontType.FONT_SMALL2, coachCoords, LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, "Coach " + team.getCoachName()));
			nameTexts.add(new GameText(FontType.FONT_SMALL, teamCoords, LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, team.getTeamName()));
		}
		
		return nameTexts;
	}

	protected List<Team> getTeamsForGameStart(List<Team> rawTeams)
	{
		List<Team> preparedTeams = new ArrayList<Team>();

		for (Team team : rawTeams)
		{
			if (team.isBlankTeam())
				team = CpuTeamFactory.generatePopulatedCpuTeam(budget);

			preparedTeams.add(team);

			if (preparedTeams.size() == 3)
				break;
		}

		while (preparedTeams.size() < 3)
			preparedTeams.add(CpuTeamFactory.generatePopulatedCpuTeam(budget));

		return preparedTeams;
	}
	
	public abstract List<Team> getTeamsForNextGame();
	public abstract void updateRecords(int gameWinner);
	public abstract void updateTeam(int index, Team team);
	
	protected abstract ImageType getBackgroundImageType();
	protected abstract ImageType getSelectScreenImageType();
	protected abstract List<Point> getHelmetLocations();
	protected abstract List<Point> getTeamNameLocations();
	protected abstract List<GameText> getScreenTexts();
}
