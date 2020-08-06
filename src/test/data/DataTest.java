package test.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import main.data.Data;
import main.data.DataImpl;
import main.data.Event;
import main.data.entities.Arena;
import main.data.entities.Player;
import main.data.entities.Race;
import main.data.entities.Stats;
import main.data.entities.Team;
import main.data.factory.PlayerFactory;

import org.junit.Before;
import org.junit.Test;

public class DataTest
{
	Data dataImpl;
	
	private Method updatePlayerLocationMethod;

	@Before
	public void setUp() throws NoSuchMethodException, SecurityException
	{
		dataImpl = new DataImpl();
		
		updatePlayerLocationMethod = DataImpl.class.getDeclaredMethod("updatePlayerLocation", Player.class, Point.class);
		updatePlayerLocationMethod.setAccessible(true);
	}

	@Test
	public void clone_newData_fieldsNullOrEmpty()
	{
		Data clonedDataImpl = dataImpl.clone();

		assertClonedDataImpl(dataImpl, clonedDataImpl);
	}

	@Test
	public void newGame_incompleteTeams_exceptionThrown()
	{
		List<Team> incompleteTeams = new ArrayList<Team>();

		try
		{
			dataImpl.newGame(incompleteTeams);
			fail();
		} catch (IllegalArgumentException e)
		{
			assertEquals("There must be three teams in a game!", e.getMessage());
		}
	}

	@Test
	public void newGame_nullTeams_exceptionThrown()
	{
		List<Team> nullTeams = new ArrayList<Team>();
		nullTeams.add(null);
		nullTeams.add(null);
		nullTeams.add(null);

		try
		{
			dataImpl.newGame(nullTeams);
			fail();
		} catch (IllegalArgumentException e)
		{
			assertEquals("There must be three teams in a game!", e.getMessage());
		}
	}

	@Test
	public void getTeamOfPlayer_validPlayer_teamReturned()
	{
		List<Team> teams = createOnePlayerTeam();
		dataImpl.newGame(teams, Arena.ARENA_MAELSTROM);
		Player player = dataImpl.getPlayer(0);
		
		int playerTeamIndex = dataImpl.getTeamIndexOfPlayer(player);
		Team playerTeam = dataImpl.getTeamOfPlayer(player);

		assertEquals(0, playerTeamIndex);
		assertEquals(teams.get(0), playerTeam);
	}

	@Test
	public void getPlayerAtLocation_validCoord_coordReturned() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		dataImpl.newGame(createOnePlayerTeam(), Arena.ARENA_SAVANNA);
		Player player = dataImpl.getPlayer(0);
		updatePlayerLocation(player, new Point(1, 1));
		Player comparePlayer = dataImpl.getPlayerAtLocation(new Point(1, 1));

		assertEquals(comparePlayer, player);
		assertEquals(comparePlayer.hashCode(), player.hashCode());
	}

	@Test
	public void getPlayerAtLocation_nullPoint_nullReturned()
	{
		Player player = dataImpl.getPlayerAtLocation(null);
		assertNull(player);
	}

	@Test
	public void getPlayerAtLocation_noPlayerAtPoint_nullReturned()
	{
		Player player = dataImpl.getPlayerAtLocation(1, 1);
		assertNull(player);
	}

	@Test
	public void getPlayerAtLocation_outsideOfBounds_nullReturned()
	{
		assertNull(dataImpl.getPlayerAtLocation(-1, -1));
		assertNull(dataImpl.getPlayerAtLocation(30, 30));
	}

	@Test
	public void getStatsOfPlayer_newGame_emptyStats()
	{
		dataImpl.newGame(createOnePlayerTeam(), Arena.ARENA_CRISSICK);
		Player player = dataImpl.getPlayer(0);

		Stats stats = dataImpl.getStatsOfPlayer(player);

		for (int i = 0; i < Stats.TOTAL_STATS; i++)
		{
			assertEquals(0, stats.getStat(i));
		}
	}

	@Test
	public void processWarpEvent()
	{
		dataImpl.newGame(createOnePlayerTeam(), Arena.ARENA_CRISSICK);
		dataImpl.processEvent(Event.teleport(0, -1, 1));

		Player player = dataImpl.getPlayer(0);
		Point playerCoords = dataImpl.getLocationOfPlayer(player);

		assertEquals(1, playerCoords.x);
		assertEquals(18, playerCoords.y);
		assertEquals(Player.STS_OKAY, player.status);
	}

	private List<Team> createOnePlayerTeam()
	{
		Player player = PlayerFactory.createPlayerWithRandomName(Race.DRAGORAN);
		Team team = new Team();
		team.addPlayer(player);
		List<Team> singleTeam = new ArrayList<Team>();
		singleTeam.add(team);
		singleTeam.add(new Team());
		singleTeam.add(new Team());

		return singleTeam;
	}

	private void assertClonedDataImpl(Data original, Data clone)
	{
		assertEquals(original.getBallLocation(), clone.getBallLocation());
		assertEquals(original.getBallCarrier(), clone.getBallCarrier());
		assertEquals(original.getCurrentTeam(), clone.getCurrentTeam());
		assertEquals(original.getWinningTeamIndex(), clone.getWinningTeamIndex());
	}

	private void updatePlayerLocation(Player player, Point point)
	{
		try
		{
			updatePlayerLocationMethod.invoke(dataImpl, player, point);
		} catch (Exception e)
		{
			System.out.println("Exception thrown when updating player location: " + e.getMessage());
		}
	}
}
