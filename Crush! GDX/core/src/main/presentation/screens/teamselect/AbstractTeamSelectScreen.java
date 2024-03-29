package main.presentation.screens.teamselect;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import main.data.entities.Pace;
import main.data.entities.Player;
import main.data.entities.Quirk;
import main.data.entities.Simulation;
import main.data.entities.Skill;
import main.data.entities.Team;
import main.data.factory.CpuTeamFactory;
import main.logic.Randomizer;
import main.logic.ai.coach.Coach;
import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.common.ScreenCommand;
import main.presentation.game.FontType;
import main.presentation.game.GameText;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.screens.ScreenType;
import main.presentation.screens.popupready.OverBudgetPopupScreen;
import main.presentation.screens.teameditor.utilities.TeamUpdater;

public abstract class AbstractTeamSelectScreen extends OverBudgetPopupScreen
{
	private int totalTeams;
	protected TeamEntry[] teams;
	protected TeamUpdater teamUpdater;
	protected boolean teamsLockedForEditing = false;	//This just means the USER can't change anything anymore, include budget and the like.  The game is still free to do whatever.
	protected int budget = 600;
	protected Pace pace = Pace.RELAXED;
	protected int turns = 20;
	protected Simulation simulation = Simulation.ABSTRACT;
	protected boolean eventCompleted = false;

	protected GameText budgetText;
	protected GameText paceText;
	protected GameText turnsText;
	protected GameText simulationText;
	
	protected GameText budget600;
	protected GameText budget900;

	protected GameText paceRelaxed;
	protected GameText paceStandard;
	protected GameText paceFrenzied;
	
	protected GameText turns20;
	protected GameText turns25;
	protected GameText turns15;
	
	protected GameText simulationAbstract;
	protected GameText simulationDetailed;
	
	protected AbstractTeamSelectScreen(Game sourceGame, ActionListener eventListener, int totalTeams)
	{
		super(sourceGame, eventListener);
		
		this.totalTeams = totalTeams;
		teamUpdater = new TeamUpdater();
		clearTeams();
	}
	
	protected void addHelmetClickZones(List<ImageButton> buttons)
	{
		Texture helmetTexture = ImageFactory.getInstance().getTexture(ImageType.EDITOR_HELMET);
		int width = helmetTexture.getWidth();
		int height = helmetTexture.getHeight();
		
		List<Point> helmetLocations = getHelmetLocations();
		
		for (int i = 0; i < helmetLocations.size(); i++)
		{
			Point origin = helmetLocations.get(i);
			buttons.add(addClickZone(origin.x, 400 - height - origin.y, width, height, ScreenCommand.editTeam(i)));
		}
	}

	protected void defineSettingsTexts()
	{
		budget600 = new GameText(FontType.FONT_SMALL2, new Point(), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, "600");
		budget900 = new GameText(FontType.FONT_SMALL2, new Point(), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, "900");
		setBudgetTextPosition();
		budgetText = budget600;
		
		paceRelaxed = new GameText(FontType.FONT_SMALL2, new Point(), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, "RELAXED");
		paceStandard = new GameText(FontType.FONT_SMALL2, new Point(), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, "STANDARD");
		paceFrenzied = new GameText(FontType.FONT_SMALL2, new Point(), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, "FRENZIED");
		setPaceTextPosition();
		paceText = paceRelaxed;
		
		turns20 = new GameText(FontType.FONT_SMALL2, new Point(), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, "20");
		turns25 = new GameText(FontType.FONT_SMALL2, new Point(), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, "25");
		turns15 = new GameText(FontType.FONT_SMALL2, new Point(), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, "15");
		setTurnsTextPosition();
		turnsText = turns20;
		
		simulationAbstract = new GameText(FontType.FONT_SMALL2, new Point(), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, "ABSTRACT");
		simulationDetailed = new GameText(FontType.FONT_SMALL2, new Point(), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, "DETAILED");
		setSimulationTextPosition();
		simulationText = simulationAbstract;
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
	public void activate()
	{
		super.activate();
		refreshStage();
	}

	@Override
	public void reset()
	{
		//league will need to default this to 900 instead
		budget = 600;
		budgetText = budget600;
		pace = Pace.RELAXED;
		paceText = paceRelaxed;
		turns = 20;
		turnsText = turns20;
		simulation = Simulation.ABSTRACT;
		simulationText = simulationAbstract;
		
		teamsLockedForEditing = false;
		
		refreshStage();
	}
	
	protected void refreshStage()
	{
		stage.clear();
		stage.addActor(new Image(ImageFactory.getInstance().getDrawable(getBackgroundImageType())));
		stage.addActor(new Image(ImageFactory.getInstance().getDrawable(getTeamSelectScreenImageType())));
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
	
	public boolean areTeamsLockedForEditing()
	{
		return teamsLockedForEditing;
	}
	
	public List<String> getTeamsOverBudget()
	{
		List<String> teamNames = new ArrayList<String>();
		
		if (teamsLockedForEditing)
			return teamNames;	//if we can't edit the teams, there's no sense in calling them out for being over budget
		
		for (int i = 0; i < totalTeams; i++)
		{
			Team team = teams[i].getTeam();
			if (team.getValue() > budget)
				teamNames.add(team.teamName);
		}
		
		return teamNames;
	}
	
	public int getBudget()
	{
		return budget;
	}
	
	public Pace getPace()
	{
		return pace;
	}
	
	public int getTurns()
	{
		return turns;
	}
	
	protected void paintHelmetImages()
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
				team = CpuTeamFactory.getInstance().generatePopulatedCpuTeam(budget);

			preparedTeams.add(team);

			if (preparedTeams.size() == 3)
				break;
		}

		while (preparedTeams.size() < 3)
			preparedTeams.add(CpuTeamFactory.getInstance().generatePopulatedCpuTeam(budget));

		return preparedTeams;
	}
	
	protected void handlePregameEffects(Team team)
	{
		//TODO:
		//Coach rearranges roster (currently done in updateTeam(), which is triggered by game end)
		//Coach allocates skill points (currently done in updateTeam(), which is triggered by game end)
		//Coach assigns equipment (currently done in updateTeam(), which is triggered by game end)
		
		for (int i = 0; i < Team.MAX_TEAM_SIZE; i++)
		{
			Player player = team.getPlayer(i);
			
			if (player == null)
				continue;
			
			if (player.hasSkill(Skill.HEALER))
				team.triggerHealChecks();
			
			if (player.hasQuirk(Quirk.EGOMANIAC) && i > 0)
			{
				player.status = Player.STS_EGO;
				continue;
			}
			
			if (player.hasQuirk(Quirk.SLACKER) && Randomizer.getRandomInt(1, 4) == 1)
			{
				player.status = Player.STS_LATE;
				continue;
			}
		}
	}

	protected void initializeBlankTeams()
	{
		for (int i = 0; i < teams.length; i++)
		{
			if (!teams[i].getTeam().isBlankTeam())
				continue;
			
			Team newTeam = CpuTeamFactory.getInstance().generateEmptyCpuTeam();
			
			Coach coach = teams[i].getCoach();
			newTeam = coach.draftForTeam(newTeam, budget);
			newTeam = coach.setLineup(newTeam);		//TODO: this shouldn't be here, but right now I have the lineup adjusted AFTER each game, instead of BEFORE (before is correct)
			teams[i].setTeam(newTeam);
		}
		
		removeDuplicateTeamColorPairs();
	}
	
	//no team can have a main color that matches another team's main color, or a trim color that matches another team's trim color
	//also, giveTeamNewRandomColors() gives distinct colors for main and trim, so a team can't have both be the same
	private void removeDuplicateTeamColorPairs()
	{
		List<Color> usedMainColors = new ArrayList<Color>();
		List<Color> usedTrimColors = new ArrayList<Color>();
		
		for (int i = 0; i < teams.length; i++)
		{
			Team team = teams[i].getTeam();
			if (team == null)
				continue;
			
			if (team.humanControlled)
				continue;
			
			while (usedMainColors.contains(team.teamColors[0]) || usedTrimColors.contains(team.teamColors[1]))
				CpuTeamFactory.getInstance().giveTeamNewRandomColors(team);
			
			usedMainColors.add(team.teamColors[0]);
			usedTrimColors.add(team.teamColors[1]);
		}
	}

	protected void changeBudget()
	{
		if (budget == 600)
		{
			budget = 900;
			budgetText = budget900;
		}
		else if (budget == 900)
		{
			budget = 600;
			budgetText = budget600;
		}
	}

	protected void changePace()
	{
		if (pace == Pace.RELAXED)
		{
			pace = Pace.STANDARD;
			paceText = paceStandard;
		}
		else if (pace == Pace.STANDARD)
		{
			pace = Pace.FRENZIED;
			paceText = paceFrenzied;
		}
		else if (pace == Pace.FRENZIED)
		{
			pace = Pace.RELAXED;
			paceText = paceRelaxed;
		}
	}

	protected void changeTurns()
	{
		if (turns == 20)
		{
			turns = 25;
			turnsText = turns25;
		}
		else if (turns == 25)
		{
			turns = 15;
			turnsText = turns15;
		}
		else if (turns == 15)
		{
			turns = 20;
			turnsText = turns20;
		}
	}

	public boolean isEventCompleted()
	{
		return eventCompleted;
	}
	
	public Team getTeam(int index)
	{
		return teams[index].getTeam();
	}
	
	public void updateTeam(int index, Team team)
	{
		teams[index].setTeam(team);
		
		if (!team.humanControlled)
			teams[index].updateTeamByCoach();		//TODO: this is also triggering just upon leaving the team editor (which makes sense, since it's being "updated")
	}

	public void updateCoach(int index, Coach coach)
	{
		teams[index].setCoach(coach);
	}
	
	public abstract List<Team> getTeamsForNextGame();
	public abstract void updateRecords(int gameWinner);
	public abstract ScreenType getVictoryScreenType();
	public abstract boolean showScheduleButton();
	
	protected abstract ImageType getBackgroundImageType();
	protected abstract ImageType getTeamSelectScreenImageType();
	protected abstract List<Point> getHelmetLocations();
	protected abstract List<Point> getTeamNameLocations();
	protected abstract List<GameText> getScreenTexts();
	
	protected abstract void setBudgetTextPosition();
	protected abstract void setPaceTextPosition();
	protected abstract void setTurnsTextPosition();
	protected abstract void setSimulationTextPosition();
}
