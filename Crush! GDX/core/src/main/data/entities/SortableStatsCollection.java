package main.data.entities;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortableStatsCollection
{
	protected int indexToSortOn = 0;
	
	private List<SortableStatsEntry> statCollection;
	
	public SortableStatsCollection()
	{
		statCollection = new ArrayList<SortableStatsEntry>();
	}
	
	public void clear()
	{
		indexToSortOn = 0;
		statCollection.clear();
	}
	
	public void addPlayerEntry(Team team, int playerIndex)
	{
		//TODO: allow different stats to be added - that is, season instead of career
		SortableStatsEntry entry = null;
		Player player = team.getPlayer(playerIndex);
				
		if (team != null && player != null)
			entry = new SortableStatsEntry(team.teamName, player.name, player.getRace().name(), team.teamColors[0], player.getCareerStats(), playerIndex);
		else
			entry = new SortableStatsEntry(null, null, null, Color.BLACK, new Stats(), 99);
		
		statCollection.add(entry);
	}
	
	public boolean hasPlayerEntry(int entryIndex)
	{
		String playerName = getPlayerName(entryIndex);
		
		return (playerName != null);
	}
	
	public String getTeamName(int entryIndex)
	{
		return statCollection.get(entryIndex).getTeamName();
	}

	public String getPlayerName(int entryIndex)
	{
		return statCollection.get(entryIndex).getPlayerName();
	}

	public String getPlayerRace(int entryIndex)
	{
		return statCollection.get(entryIndex).getPlayerRace();
	}

	public Color getColor(int entryIndex)
	{
		return statCollection.get(entryIndex).getColor();
	}

	public int getStat(int entryIndex, int statIndex)
	{
		return statCollection.get(entryIndex).getStat(statIndex);
	}
	
	public void sortStats(int statIndex)
	{
		indexToSortOn = statIndex;
		Collections.sort(statCollection);
	}
	
	private class SortableStatsEntry implements Comparable<SortableStatsEntry>
	{
		protected SortableStatsEntry(String teamName, String playerName, String playerRace, Color color, Stats stats, int playerIndex)
		{
			this.teamName = teamName;
			this.playerName = playerName;
			this.playerRace = playerRace;
			this.color = new Color(color.getRGB());
			this.stats = stats.clone();
			this.playerIndex = playerIndex;
		}
		
		private String teamName;
		private String playerName;
		private String playerRace;
		private Color color;
		private Stats stats;
		private int playerIndex;
		
		public String getTeamName()
		{
			return teamName;
		}

		public String getPlayerName()
		{
			return playerName;
		}

		public String getPlayerRace()
		{
			return playerRace;
		}

		public Color getColor()
		{
			return color;
		}

		public int getStat(int index)
		{
			return stats.getStat(index);
		}

		@Override
		public int compareTo(SortableStatsEntry entry)
		{
			//non-null entries always come before null entries
			if (teamName != null && entry.teamName == null)
				return -1;
			else if (teamName == null && entry.teamName == null)
				return 0;
			else if (teamName == null && entry.teamName != null)
				return 1;
			
			int myValue = stats.getStat(indexToSortOn);
			int otherValue = entry.stats.getStat(indexToSortOn);
			
			//higher numbers come first
			if (myValue > otherValue)
				return -1;
			if (myValue < otherValue)
				return 1;
			
			//if the stats are the same, order by player's spot in the lineup
			if (playerIndex < entry.playerIndex)
				return -1;
			if (playerIndex > entry.playerIndex)
				return 1;
			
			return 0;
		}
	}
}
