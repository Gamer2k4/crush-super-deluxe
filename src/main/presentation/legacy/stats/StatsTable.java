package main.presentation.legacy.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import main.data.entities.Player;
import main.data.entities.Team;

public class StatsTable
{
	private List<StatsTableRow> table;

	public StatsTable()
	{
		table = new ArrayList<StatsTableRow>();
	}

	public void addPlayer(Player player, Team team, int statsType)
	{
		table.add(new StatsTableRow(player, team, statsType));
	}
	
	public void addTeam(Team team, int statsType)
	{
		table.add(new StatsTableRow(team, statsType));
	}
	
	public List<String[]> getTopPlayers(int playerCount, int statToSortOn, boolean includeZeros, int ... columnsToInclude)
	{
		sort(statToSortOn);
		List<String[]> topPlayers = new ArrayList<String[]>();
		
		for (int i = 0; i < playerCount && i < table.size(); i++)
		{
			if (!includeZeros && Double.valueOf(table.get(i).getStat(statToSortOn)) == 0)	//not a number or a zero; either way, we don't want it
				continue;
			
			String[] statRow = new String[columnsToInclude.length];
			
			for (int j = 0; j < columnsToInclude.length; j++)
			{
				statRow[j] = table.get(i).getStat(columnsToInclude[j]);
			}
			
			topPlayers.add(statRow);
		}
		
		return topPlayers;
	}
	
	public List<Player> getTopPlayers(int playerCount, int statToSortOn)
	{
		sort(statToSortOn);
		List<Player> topPlayers = new ArrayList<Player>();
		
		for (int i = 0; i < playerCount && i < table.size(); i++)
		{
			topPlayers.add(table.get(i).getPlayer());
		}
		
		return topPlayers;
	}
	
	private void sort(int statToSortOn)
	{
		StatsTableRow.setSortKey(statToSortOn);
		Collections.sort(table);
		Collections.reverse(table);
	}
}
