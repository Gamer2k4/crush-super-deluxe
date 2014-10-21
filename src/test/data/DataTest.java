package test.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import main.data.Data;
import main.data.Event;
import main.data.entities.Arena;
import main.data.entities.Player;
import main.data.entities.Team;

import org.junit.Before;
import org.junit.Test;

public class DataTest {

	Data data;
	
	@Before
	public void setUp()
	{
		data = new Data();
	}
	
	@Test
	public void testClone()
	{
		
	}
	
	@Test
	public void getTeamOfPlayer()
	{
		data.newGame(createOnePlayerTeam(), Arena.ARENA_MAELSTROM);
		Player player = data.getPlayer(0);
		int playerTeam = data.getTeamOfPlayer(player);
		
		assertEquals(0, playerTeam);
	}
	
	@Test
	public void getPlayerAtLocation()
	{
		data.newGame(createOnePlayerTeam(), Arena.ARENA_SAVANNA);
		Player player = data.getPlayer(0);
		data.setPlayerAtLocation(new Point(1, 1), player);
		Player comparePlayer = data.getPlayerAtLocation(1, 1);
		
		assertNotNull(player);
		assertNotNull(comparePlayer);
		
		assertEquals(comparePlayer, player);
		assertEquals(comparePlayer.hashCode(), player.hashCode());
	}
	
	@Test
	public void processWarpEvent()
	{
		data.newGame(createOnePlayerTeam(), Arena.ARENA_CRISSICK);
		data.processEvent(Event.teleport(0, -1, 1));
		
		Player player = data.getPlayer(0);
		Point playerCoords = data.getLocationOfPlayer(player);
		
		assertEquals(1, playerCoords.x);
		assertEquals(16, playerCoords.y);
		assertEquals(Player.STS_OKAY, player.status);
	}
	
	private List<Team> createOnePlayerTeam()
	{
		Player player = new Player(Player.RACE_DRAGORAN);
		Team team = new Team();
		team.addPlayer(player);
		List<Team> singleTeam = new ArrayList<Team>();
		singleTeam.add(team);
		singleTeam.add(null);
		singleTeam.add(null);
		
		return singleTeam;
	}
}
