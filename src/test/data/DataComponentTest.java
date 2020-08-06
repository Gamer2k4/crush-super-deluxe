package test.data;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import main.data.Data;
import main.data.DataImpl;
import main.data.Event;
import main.data.entities.Arena;
import main.data.entities.Player;
import main.data.entities.Race;
import main.data.entities.Team;
import main.data.factory.PlayerFactory;

import org.junit.Before;
import org.junit.Test;

public class DataComponentTest
{
	private static final int CURMIAN_MAX_AP = 80;
	private static final int HUMAN_MAX_AP = 60;
	private static final int AP_MOVE_COST = 10;
	
	Data dataImpl;

	@Before
	public void setUp()
	{
		dataImpl = new DataImpl();
	}
	
	@Test
	public void scenario1()	//TODO: get a better method name
	{
		int CURMIAN1_INDEX = 0;
		int XJS1_INDEX = 1;
		int HUMAN1_INDEX = 9;
		int HUMAN2_INDEX = 10;
		
		List<Team> teams = new ArrayList<Team>();
		teams.add(createTeamWithCurmianAndXjs());
		teams.add(createTeamWithTwoHumans());
		teams.add(new Team());
		
		dataImpl.newGame(teams, Arena.ARENA_SAVANNA);

		Player curmian1 = dataImpl.getPlayer(CURMIAN1_INDEX);
		Player xjs1 = dataImpl.getPlayer(XJS1_INDEX);
		Player human1 = dataImpl.getPlayer(HUMAN1_INDEX);
		Player human2 = dataImpl.getPlayer(HUMAN2_INDEX);

		assertEquals(curmian1, teams.get(0).getPlayer(0));
		assertEquals(xjs1, teams.get(0).getPlayer(1));
		assertEquals(human1, teams.get(1).getPlayer(0));
		assertEquals(human2, teams.get(1).getPlayer(1));
		
		//turn 1, player 0
		dataImpl.processEvent(Event.updateTurnPlayer(0));
		
		assertEquals(0, dataImpl.getCurrentTeam());
		
		//curmian warps in
		dataImpl.processEvent(Event.teleport(CURMIAN1_INDEX, -1, 0));
		
		assertEquals(new Point(8, 8), dataImpl.getLocationOfPlayer(curmian1));
		assertEquals(CURMIAN_MAX_AP, curmian1.currentAP);
		
		//turn 1, player 1
		dataImpl.processEvent(Event.updateTurnPlayer(1));
		
		assertEquals(0, curmian1.currentAP);	//AP should be zeroed for all players on the team after their turn ends
		assertEquals(1, dataImpl.getCurrentTeam());
		
		//displace the curmian by warping human into the same portal
		dataImpl.processEvent(Event.teleport(HUMAN1_INDEX, -1, 0));
		dataImpl.processEvent(Event.teleport(CURMIAN1_INDEX, 0, 1));
		
		assertEquals(new Point(8, 21), dataImpl.getLocationOfPlayer(curmian1));
		assertEquals(new Point(8, 8), dataImpl.getLocationOfPlayer(human1));
		assertEquals(HUMAN_MAX_AP, human1.currentAP);
		
		//human moves off portal
		dataImpl.processEvent(Event.move(HUMAN1_INDEX, 7, 7, false, false, false));

		assertEquals(new Point(7, 7), dataImpl.getLocationOfPlayer(human1));
		assertEquals(HUMAN_MAX_AP - AP_MOVE_COST, human1.currentAP);
		
	}
	
//	private Team createTeamWithTwoCurmians()
//	{
//		Team team = new Team();
//		team.addPlayer(PlayerFactory.createPlayerWithDefinedName(Player.RACE_CURMIAN, "curmian1"));
//		team.addPlayer(PlayerFactory.createPlayerWithDefinedName(Player.RACE_CURMIAN, "curmian2"));
//		return team;
//	}
	
	private Team createTeamWithCurmianAndXjs()
	{
		Team team = new Team();
		team.addPlayer(PlayerFactory.createPlayerWithDefinedName(Race.CURMIAN, "curmian1"));
		team.addPlayer(PlayerFactory.createPlayerWithDefinedName(Race.XJS9000, "xjs1"));
		return team;
	}
	
	private Team createTeamWithTwoHumans()
	{
		Team team = new Team();
		team.addPlayer(PlayerFactory.createPlayerWithDefinedName(Race.HUMAN, "human1"));
		team.addPlayer(PlayerFactory.createPlayerWithDefinedName(Race.HUMAN, "human2"));
		return team;
	}
}
