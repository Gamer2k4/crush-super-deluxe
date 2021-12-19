package main.presentation.screens.teamselect;

import java.util.ArrayList;
import java.util.List;

public class TeamRecordTracker
{
	public static final int RESULT_WIN = 0;
	public static final int RESULT_LOSS = 1;
	public static final int RESULT_TIE = 2;
	
	private List<int[]> teamRecords;
	
	public TeamRecordTracker()
	{
		teamRecords = new ArrayList<int[]>();
	}
	
	public void addTeam()
	{
		int[] winLossTie = {0, 0, 0};
		teamRecords.add(winLossTie);
	}
	
	public int[] getRecord(int teamIndex)
	{
		if (teamIndex < 0 || teamIndex >= teamRecords.size())
		{
			throw new IllegalArgumentException("Invalid team index for getting record: " + teamIndex);
		}
		
		return teamRecords.get(teamIndex);
	}
	
	public void addResultToTeam(int teamIndex, int resultType)
	{
		if (teamIndex < 0 || teamIndex >= teamRecords.size())
		{
			throw new IllegalArgumentException("Invalid team index for updating record: " + teamIndex);
		}
		
		int[] record = teamRecords.get(teamIndex);
		
		record[resultType]++;
		
		teamRecords.set(teamIndex, record);
	}
	
	public int getLeadingTeamIndex()
	{
		int topScore = -1;
		int leadingTeamIndex = -1;
		
		for (int i = 0; i < teamRecords.size(); i++)
		{
			int[] record = teamRecords.get(i);
			if (record[RESULT_WIN] > topScore)
			{
				topScore = record[RESULT_WIN];
				leadingTeamIndex = i;
			}
		}
		
		return leadingTeamIndex;
	}
	
	public int getTopScore()
	{
		return teamRecords.get(getLeadingTeamIndex())[RESULT_WIN];
	}
	
	//returns true if there is at least one team and that team has at least one win, loss, or tie
	public boolean seasonStarted()
	{
		if (teamRecords.size() > 0)
		{
			int[] record = teamRecords.get(0);
			if ((record[RESULT_WIN] + record[RESULT_LOSS] + record[RESULT_TIE]) > 0)
				return true;
		}
		
		return false;
	}
	
	public void resetRecords()
	{
		int size = teamRecords.size();
		teamRecords.clear();
		
		for (int i = 0; i < size; i++)
			addTeam();
	}
}
