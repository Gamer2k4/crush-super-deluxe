package main.presentation.screens.teamselect;

import com.badlogic.gdx.graphics.Texture;

import main.data.entities.Team;
import main.presentation.TeamColorsManager;

public class TeamEntry
{
	private Team team;
	
	private int wins;
	private int losses;
	private int ties;
	
	public TeamEntry()
	{
		team = new Team();
		wins = 0;
		losses = 0;
		ties = 0;
	}
	
	public Team getTeam()
	{
		return team;
	}
	
	public void setTeam(Team team)
	{
		this.team = team;
	}
	
	public int getWins()
	{
		return wins;
	}
	
	public void addWin()
	{
		wins += 1;
	}
	
	public int getLosses()
	{
		return losses;
	}
	
	public void addLoss()
	{
		losses += 1;
	}
	
	public int getTies()
	{
		return ties;
	}
	
	public void addTie()
	{
		ties += 1;
	}
	
	public String getCoachName()
	{
		if (team == null)
			return "UNDEFINED";
		
		return team.coachName;
	}
	
	public String getTeamName()
	{
		if (team == null)
			return "UNDEFINED";
		
		return team.teamName;
	}
	
	public Texture getHelmetImage()
	{
		return TeamColorsManager.getInstance().getHelmetImage(team);
	}
	
	public boolean isWinner(int winRequirement)
	{
		if (winRequirement == 0)
			return false;
		
		return (wins >= winRequirement);
	}
}
