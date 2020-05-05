package main.presentation.teamchoicescreen;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import main.data.DataImpl;
import main.data.entities.Team;
import main.presentation.common.AbstractScreenPanel;
import main.presentation.startupscreen.TeamRecordTracker;

public abstract class AbstractTeamSelectionScreen extends AbstractScreenPanel
{
	private static final long serialVersionUID = -5549725459860836257L;
	
	protected List<GamePlayerSlotHelmetPanel> playerSlots;
	protected ActionListener externalListener;
	protected TeamRecordTracker teamRecordTracker;
	
	protected boolean readyToStart = false;
	
	public static final String ACTION_TEAM_SELECT_BACK = "teamBack_";
	
	protected AbstractTeamSelectionScreen(Dimension dimension, ActionListener listener)
	{
		super(dimension);
		externalListener = listener;
		playerSlots = new ArrayList<GamePlayerSlotHelmetPanel>();
		teamRecordTracker = new TeamRecordTracker();
	}
	
	protected abstract void checkGameStartConditions();
	protected abstract void addPlayerSlotToPanel(int index, GamePlayerSlotHelmetPanel playerSlot);
	public abstract int getSeasonWinner();
	public abstract int getBudget();
	public abstract int getGoal();		//TODO: might only be necessary for exhibition games
	public abstract String getScreenTag();
	public abstract void disableControls();
	public abstract void enableControls();
	
	public boolean readyToStart()
	{
		checkGameStartConditions();

		return readyToStart;
	}
	
	protected void createPlayerSlot(int index)
	{
		GamePlayerSlotHelmetPanel playerSlot = new GamePlayerSlotHelmetPanel(index, this, externalListener);
		addPlayerSlotToPanel(index, playerSlot);
		playerSlots.add(playerSlot);
		teamRecordTracker.addTeam();
	}
	
	public void updatePlayerSlot(int index, Team team)
	{
		if (index < 0 || index >= playerSlots.size())
		{
			System.out.println("Updating player slot - index " + index + " is out of bounds.");
			return;
		}
		
		playerSlots.get(index).setTeam(team);
	}
	
	public void updateTeamRecords(int winningTeamIndex)
	{
		//TODO: eventually, account for the fact that multiple sets of teams might be playing, like in a tournament mode.
		//possibly have an abstract method that gets the index offset
		
		for (int i = 0; i < 3; i++)
		{
			GamePlayerSlotHelmetPanel playerSlot = playerSlots.get(i);	//plus offset when I get to it - note that assuming three teams should be fine until
																		//we get into the expansion, where different amounts of teams could play
			if (winningTeamIndex == DataImpl.TIE_GAME)
			{
				teamRecordTracker.addResultToTeam(i, TeamRecordTracker.RESULT_TIE);
			}
			else if (winningTeamIndex == i)
			{
				teamRecordTracker.addResultToTeam(i, TeamRecordTracker.RESULT_WIN);
			}
			else
			{
				teamRecordTracker.addResultToTeam(i, TeamRecordTracker.RESULT_LOSS);
			}
			
			playerSlot.updateRecord(teamRecordTracker.getRecord(i));
		}
	}
	
	public Team getTeamForPlayerSlot(int index)
	{
		if (index < 0 || index >= playerSlots.size())
		{
			System.out.println("Getting player slot - index " + index + " is out of bounds.");
			return null;
		}
		
		return playerSlots.get(index).getTeam();
	}
	
	public List<Team> getTeams()
	{
		List<Team> teams = new ArrayList<Team>();
		
		for (GamePlayerSlotHelmetPanel playerSlot : playerSlots)
		{
			teams.add(playerSlot.getTeam());
		}
		
		return teams;
	}
	
	public void setTeams(List<Team> newTeams)
	{
		for (int i = 0; i < newTeams.size(); i++)
		{
			Team team = newTeams.get(i);
			playerSlots.get(i).setTeam(team);
		}
	}
	
	public boolean isSeasonStarted()
	{
		return teamRecordTracker.seasonStarted();
	}
}
