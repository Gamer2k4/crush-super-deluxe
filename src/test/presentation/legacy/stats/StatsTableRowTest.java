package test.presentation.legacy.stats;

import static org.junit.Assert.assertEquals;

import main.data.entities.Player;
import main.data.entities.Stats;
import main.data.entities.Team;
import main.data.factory.PlayerFactory;
import main.presentation.legacy.stats.StatsTableRow;

import org.junit.Before;
import org.junit.Test;


public class StatsTableRowTest
{
	Player player1;
	Player player2;
	
	StatsTableRow row1;
	StatsTableRow row2;
	
	@Before
	public void setup()
	{
		player1 = PlayerFactory.createPlayerWithDefinedName(Player.RACE_HUMAN, "Bob");
		player2 = PlayerFactory.createPlayerWithDefinedName(Player.RACE_HUMAN, "Carl");
		
		Team team = new Team();
		
		row1 = new StatsTableRow(player1, team, Stats.GAME_STATS);
		row2 = new StatsTableRow(player2, team, Stats.GAME_STATS);
	}
	
	@Test
	public void compareTo_compareOnStrings_sortedAlphabetically()
	{
		StatsTableRow.setSortKey(StatsTableRow.STATS_PLAYER_NAME);
		assertEquals(-1, row1.compareTo(row2));
		assertEquals(1, row2.compareTo(row1));
		assertEquals(0, row1.compareTo(row1));
		assertEquals(0, row2.compareTo(row2));
	}
	
	@Test
	public void compareTo_compareOnDecimalDoubles_sortedByValue()
	{
		player1.getLastGameStats().check(true, false);
		player1.getLastGameStats().check(false, false);
		
		player2.getLastGameStats().check(true, false);
		player2.getLastGameStats().check(true, false);
		player2.getLastGameStats().check(false, false);
		
		row1 = new StatsTableRow(player1, new Team(), Stats.GAME_STATS);
		row2 = new StatsTableRow(player2, new Team(), Stats.GAME_STATS);
		
		StatsTableRow.setSortKey(StatsTableRow.STATS_CHECKING_AVERAGE);
		assertEquals(-1, row1.compareTo(row2));
		assertEquals(1, row2.compareTo(row1));
		assertEquals(0, row1.compareTo(row1));
		assertEquals(0, row2.compareTo(row2));
	}
	
	@Test
	public void compareTo_compareOnDoublesOneAndZero_sortedByValue()
	{
		player1.getLastGameStats().check(false, false);
		player2.getLastGameStats().check(true, false);
		
		row1 = new StatsTableRow(player1, new Team(), Stats.GAME_STATS);
		row2 = new StatsTableRow(player2, new Team(), Stats.GAME_STATS);
		
		StatsTableRow.setSortKey(StatsTableRow.STATS_CHECKING_AVERAGE);
		assertEquals(-1, row1.compareTo(row2));
		assertEquals(1, row2.compareTo(row1));
		assertEquals(0, row1.compareTo(row1));
		assertEquals(0, row2.compareTo(row2));
	}
}
