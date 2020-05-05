package test.logic;

import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Queue;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import main.data.Data;
import main.data.Event;
import main.data.entities.Arena;
import main.data.entities.Player;
import main.data.factory.PlayerFactory;
import main.data.factory.SimpleArenaFactory;
import main.logic.Engine;
import main.logic.LegacyEngineImpl;
import main.logic.RandomGenerator;
import main.logic.RandomGeneratorSingletonImpl;

public class LegacyEngineTest
{
	private IMocksControl mocksControl;

	private Data mockData;
	private Data mockLocalData;
	private RandomGenerator mockRandomGenerator;
	
	private Arena arenaSavanna;
	private Arena arenaDarksun;
	
	private Engine engine;
	
	private static final int RANDOM_LOWER = 5;
	private static final int RANDOM_UPPER = 95;
	
	private static final int RANDOMNESS = 100;
	
	private static final int TEAM1_INDEX = 0;
	private static final int TEAM2_INDEX = 1;

	@Before
	public void setUp() throws NoSuchMethodException, SecurityException
	{
		mocksControl = createControl();
		
		mockData = mocksControl.createMock(Data.class);
		mockLocalData = mocksControl.createMock(Data.class);
		mockRandomGenerator = mocksControl.createMock(RandomGenerator.class);
		
		setRandomGenerator();
		arenaSavanna = SimpleArenaFactory.getInstance().generateArena(Arena.ARENA_SAVANNA);
		arenaDarksun = SimpleArenaFactory.getInstance().generateArena(Arena.ARENA_DARKSUN);
		
		engine = new LegacyEngineImpl(mockData);
	}

	@Test
	public void getAssistBonus_noAdjecentPlayers_noBonus()
	{
		Player team1attacker = PlayerFactory.createPlayerWithDefinedName(Player.RACE_HUMAN, "Player1");
		Player team2defender = PlayerFactory.createPlayerWithDefinedName(Player.RACE_GRONK, "Player2");
		
		expect(mockData.clone()).andReturn(mockLocalData);
		assistExpects(mockLocalData, team1attacker, team2defender, 0, 14, 14, team1attacker, null, null, null, team2defender, null, null, null, null);
		
		mocksControl.replay();
		
		int bonus = engine.getAssistBonus(team1attacker, team2defender);
		assertEquals(0, bonus);
		
		mocksControl.verify();
	}

	@Test
	public void getAssistBonus_oneAdjecentAlly_bonusOfTen()
	{
		Player team1attacker = createPlayer(Player.RACE_HUMAN, "Team 1 Attacker", Player.STS_OKAY);
		Player team2defender = createPlayer(Player.RACE_GRONK, "Team 2 Defender", Player.STS_OKAY);
		Player team1ally = createPlayer(Player.RACE_HUMAN, "Team 1 Ally", Player.STS_OKAY);
		
		expect(mockData.clone()).andReturn(mockLocalData);
		assistExpects(mockLocalData, team1attacker, team2defender, 0, 14, 14, team1attacker, null, null, null, team2defender, null, null, team1ally, null);
		
		expect(mockLocalData.getTeamIndexOfPlayer(team1ally)).andReturn(TEAM1_INDEX);
		
		mocksControl.replay();
		
		int bonus = engine.getAssistBonus(team1attacker, team2defender);
		assertEquals(10, bonus);
		
		mocksControl.verify();
	}

	@Test
	public void getAssistBonus_oneStunnedAllyOneAllyWithGuard_bonusOfFifteen()
	{
		Player team1attacker = createPlayer(Player.RACE_HUMAN, "Team 1 Attacker", Player.STS_OKAY);
		Player team2defender = createPlayer(Player.RACE_GRONK, "Team 2 Defender", Player.STS_OKAY);
		Player team1stunnedAlly = createPlayer(Player.RACE_HUMAN, "Team 1 Stunned Ally", Player.STS_STUN);
		Player team1guardAlly = createPlayer(Player.RACE_HUMAN, "Team 1 Guard Ally", Player.STS_OKAY);
		
		team1guardAlly.gainSkill(Player.SKILL_GUARD);
		
		expect(mockData.clone()).andReturn(mockLocalData);
		assistExpects(mockLocalData, team1attacker, team2defender, 0, 14, 14, team1attacker, team1guardAlly, null, null, team2defender, null, null, team1stunnedAlly, null);
		
		expect(mockLocalData.getTeamIndexOfPlayer(team1guardAlly)).andReturn(TEAM1_INDEX);
		
		mocksControl.replay();
		
		int bonus = engine.getAssistBonus(team1attacker, team2defender);
		assertEquals(15, bonus);
		
		mocksControl.verify();
	}

	@Test
	public void getAssistBonus_oneAdjecentEnemy_noBonus() // bonuses are additive, not subtractive
	{
		Player team1attacker = createPlayer(Player.RACE_HUMAN, "Team 1 Attacker", Player.STS_OKAY);
		Player team2defender = createPlayer(Player.RACE_GRONK, "Team 2 Defender", Player.STS_OKAY);
		Player team2ally = createPlayer(Player.RACE_HUMAN, "Team 2 Ally", Player.STS_OKAY);
		
		expect(mockData.clone()).andReturn(mockLocalData);
		assistExpects(mockLocalData, team1attacker, team2defender, 0, 14, 14, team1attacker, null, null, null, team2defender, null, null, team2ally, null);
		
		expect(mockLocalData.getTeamIndexOfPlayer(team2ally)).andReturn(TEAM2_INDEX);
		
		mocksControl.replay();
		
		int bonus = engine.getAssistBonus(team1attacker, team2defender);
		assertEquals(0, bonus);
		
		mocksControl.verify();
	}

	@Test
	public void getAssistBonus_defenderHasTactics_noBonus()	//tactics negates assist bonuses
	{
		Player team1attacker = createPlayer(Player.RACE_HUMAN, "Team 1 Attacker", Player.STS_OKAY);
		Player team2defender = createPlayer(Player.RACE_GRONK, "Team 2 Defender", Player.STS_OKAY);
		
		team2defender.gainSkill(Player.SKILL_TACTICS);
		
		expect(mockData.clone()).andReturn(mockLocalData);
		
		mocksControl.replay();
		
		int bonus = engine.getAssistBonus(team1attacker, team2defender);
		assertEquals(0, bonus);
		
		mocksControl.verify();
	}
	
	@Test
	public void skillCheck_differentValues_appropriateResultsReturned() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Method skillCheck = engine.getClass().getDeclaredMethod("skillCheck", int.class);
		skillCheck.setAccessible(true);
		
		expect(mockRandomGenerator.getRandomInt(5, 95)).andReturn(25).times(3);
		
		mocksControl.replay();
		
		boolean result = (Boolean) skillCheck.invoke(engine, 35);
		assertTrue(result);
		
		result = (Boolean) skillCheck.invoke(engine, 25);
		assertTrue(result);
		
		result = (Boolean) skillCheck.invoke(engine, 15);
		assertFalse(result);
		
		mocksControl.verify();
	}
	
	@Test
	public void generateEvents_jumpEventOverElectricTiles_tilesAvoided()
	{
		Player player = createPlayer(Player.RACE_HUMAN, "Player", Player.STS_OKAY);
		
		int playerIndex = 0;
		
		int playerOriginX = 4;
		int playerOriginY = 8;
		int playerTargetX = 4;
		int playerTargetY = 10;
		
		Event jumpEvent1 = Event.move(playerIndex, playerTargetX, playerTargetY - 1, false, true, false);
		Event jumpEvent2 = Event.move(playerIndex, playerTargetX, playerTargetY, false, true, false);
		
		expect(mockData.clone()).andReturn(mockLocalData);
		expect(mockLocalData.getArena()).andReturn(arenaDarksun);
		
		//get the player specified in the move event and his starting location
		expect(mockLocalData.getPlayer(playerIndex)).andReturn(player);
		expect(mockLocalData.getLocationOfPlayer(player)).andReturn(new Point(playerOriginX, playerOriginY));
		
		//create a path for the movement, confirming there's no player in the target tiles
		expect(mockLocalData.getArena()).andReturn(arenaDarksun);
		expect(mockLocalData.getPlayerAtLocation(new Point(playerTargetX, playerTargetY - 1))).andReturn(null);
		expect(mockLocalData.getArena()).andReturn(arenaDarksun);
		expect(mockLocalData.getPlayerAtLocation(new Point(playerTargetX, playerTargetY))).andReturn(null);

		// persist the first move event to local data and check for ball pickup
		mockLocalData.processEvent(jumpEvent1);
		expectLastCall();
		expect(mockLocalData.getBallLocation()).andReturn(new Point(-1, -1));
		
		// persist the second move event to local data and check for ball pickup
		mockLocalData.processEvent(jumpEvent2);
		expectLastCall();
		expect(mockLocalData.getBallLocation()).andReturn(new Point(-1, -1));
		
		// make a jump skill check (and pass it)
		expect(mockRandomGenerator.getRandomInt(RANDOM_LOWER, RANDOM_UPPER)).andReturn(50);
		
		// get the moving player's team index to pass into the reflex check method
		expect(mockLocalData.getTeamIndexOfPlayer(player)).andReturn(TEAM1_INDEX);
		
		//for every tile around the player, make sure the player hasn't moved
		expect(mockLocalData.getLocationOfPlayer(player)).andReturn(new Point(playerTargetX, playerTargetY)).times(9);
		
		// check surrounding tiles for a player to reflex check
		expect(mockLocalData.getPlayerAtLocation(3, 9)).andReturn(null);
		expect(mockLocalData.getPlayerAtLocation(4, 9)).andReturn(null);
		expect(mockLocalData.getPlayerAtLocation(5, 9)).andReturn(null);
		expect(mockLocalData.getPlayerAtLocation(3, 10)).andReturn(null);
		expect(mockLocalData.getPlayerAtLocation(4, 10)).andReturn(player);
		expect(mockLocalData.getTeamIndexOfPlayer(player)).andReturn(TEAM1_INDEX); // there's a player here, so find his team

		expect(mockLocalData.getPlayerAtLocation(5, 10)).andReturn(null);
		expect(mockLocalData.getPlayerAtLocation(3, 11)).andReturn(null);
		expect(mockLocalData.getPlayerAtLocation(4, 11)).andReturn(null);
		expect(mockLocalData.getPlayerAtLocation(5, 11)).andReturn(null);
		
		// player location check for ball bin
		expect(mockLocalData.getLocationOfPlayer(player)).andReturn(new Point(playerTargetX, playerTargetY));
		
		//victory check after all is said and done
		expect(mockLocalData.getBallCarrier()).andReturn(null);
		
		mocksControl.replay();
		
		Queue<Event> events = engine.generateEvents(jumpEvent2);
		assertEquals(2, events.size());
		
		mocksControl.verify();
	}
	
	@Test
	public void generateEvents_moveEventWithReactionCheckAndKnockdown_properEventsGenerated()
	{
		Player movingPlayer = createPlayer(Player.RACE_HUMAN, "Player1", Player.STS_OKAY);
		Player reflexCheckingPlayer = createPlayer(Player.RACE_GRONK, "Player2", Player.STS_OKAY);
		
		int player1Index = 0;
		int player2Index = 9;
		
		int player1OriginX = 14;
		int player1OriginY = 14;
		int player1TargetX = 15;
		int player1TargetY = 15;
		int player2X = 16;
		int player2Y = 16;
		
		Event moveEvent = Event.move(player1Index, player1TargetX, player1TargetY, false, false, false);
		
		expect(mockData.clone()).andReturn(mockLocalData);
		expect(mockLocalData.getArena()).andReturn(arenaSavanna);
		
		//get the player specified in the move event and his starting location
		expect(mockLocalData.getPlayer(player1Index)).andReturn(movingPlayer);
		expect(mockLocalData.getLocationOfPlayer(movingPlayer)).andReturn(new Point(player1OriginX, player1OriginY));
		
		//create a path for the movement, confirming there's no player in the target tile
		expect(mockLocalData.getArena()).andReturn(arenaSavanna);
		expect(mockLocalData.getPlayerAtLocation(new Point(player1TargetX, player1TargetY))).andReturn(null);
		
		//persist the move event to local data
		mockLocalData.processEvent(moveEvent);
		expectLastCall();
		
		//check for ball pickup
		expect(mockLocalData.getBallLocation()).andReturn(new Point(-1, -1));
		
		//get the moving player's team index to pass into the reflex check method
		expect(mockLocalData.getTeamIndexOfPlayer(movingPlayer)).andReturn(TEAM1_INDEX);
		
		//for every tile around the player, make sure the player hasn't moved
		expect(mockLocalData.getLocationOfPlayer(movingPlayer)).andReturn(new Point(player1TargetX, player1TargetY)).times(9);
		
		//check surrounding tiles for a player to reflex check
		expect(mockLocalData.getPlayerAtLocation(14, 14)).andReturn(null);
		expect(mockLocalData.getPlayerAtLocation(15, 14)).andReturn(null);
		expect(mockLocalData.getPlayerAtLocation(16, 14)).andReturn(null);
		expect(mockLocalData.getPlayerAtLocation(14, 15)).andReturn(null);
		expect(mockLocalData.getPlayerAtLocation(15, 15)).andReturn(movingPlayer);
		expect(mockLocalData.getTeamIndexOfPlayer(movingPlayer)).andReturn(TEAM1_INDEX);	//there's a player here, so find his team
		
		expect(mockLocalData.getPlayerAtLocation(16, 15)).andReturn(null);
		expect(mockLocalData.getPlayerAtLocation(14, 16)).andReturn(null);
		expect(mockLocalData.getPlayerAtLocation(15, 16)).andReturn(null);
		expect(mockLocalData.getPlayerAtLocation(player2X, player2Y)).andReturn(reflexCheckingPlayer);
		expect(mockLocalData.getTeamIndexOfPlayer(reflexCheckingPlayer)).andReturn(TEAM2_INDEX);	//there's a player here, so find his team
		
		//gronks have terrible reflexes, but we'll give him this one
		expect(mockRandomGenerator.getRandomInt(RANDOM_LOWER, RANDOM_UPPER)).andReturn(15);
		
		//find the indexes of the players involved in the check
		expect(mockLocalData.getIndexOfPlayer(movingPlayer)).andReturn(player1Index);
		expect(mockLocalData.getIndexOfPlayer(reflexCheckingPlayer)).andReturn(player2Index);
		
		//get players involved in the check
		expect(mockLocalData.getPlayer(player1Index)).andReturn(movingPlayer);
		expect(mockLocalData.getPlayer(player2Index)).andReturn(reflexCheckingPlayer);
		
		//attacker checking skill
		assistExpects(mockLocalData, reflexCheckingPlayer, movingPlayer, TEAM2_INDEX, player1TargetX, player1TargetY, null, null, null, null, movingPlayer, null, null, null, reflexCheckingPlayer);
		expect(mockRandomGenerator.getRandomInt(1, RANDOMNESS)).andReturn(15);	//attacker CH: 60 + 15 = 75
		
		//defender checking skill
		assistExpects(mockLocalData, movingPlayer, reflexCheckingPlayer, TEAM1_INDEX, player2X, player2Y, movingPlayer, null, null, null, reflexCheckingPlayer, null, null, null, null);
		expect(mockRandomGenerator.getRandomInt(1, RANDOMNESS)).andReturn(0);	//defender CH: 50 + 0 = 0
		
		//ST and TG checks - we just want a knockdown here, so pump up the TG for both
		expect(mockRandomGenerator.getRandomInt(1, RANDOMNESS)).andReturn(10);	//attacker ST: 70 + 10 = 80
		expect(mockRandomGenerator.getRandomInt(1, RANDOMNESS)).andReturn(10);	//defender ST: 50 + 10 = 60
		expect(mockRandomGenerator.getRandomInt(1, RANDOMNESS)).andReturn(40);	//attacker TG: 60 + 40 = 100
		expect(mockRandomGenerator.getRandomInt(1, RANDOMNESS)).andReturn(50);	//defender TG: 50 + 50 = 100
		
		//dodge check - failed
		expect(mockRandomGenerator.getRandomInt(RANDOM_LOWER, RANDOM_UPPER)).andReturn(RANDOM_UPPER);
		
		//checking to see if either player has the ball
		expect(mockLocalData.getBallCarrier()).andReturn(null).times(2);
		
		//persist the check and knockdown events to local data
		mockLocalData.processEvent(Event.check(player2Index, player1Index, Event.CHECK_FALL, true));
		expectLastCall();
		
		mockLocalData.processEvent(Event.setStatus(player1Index, Player.STS_DOWN));
		expectLastCall();
		
		//injury calculation - it's a knockdown, so also check if he had the ball
		expect(mockLocalData.getIndexOfPlayer(movingPlayer)).andReturn(player1Index);
		expect(mockLocalData.getBallCarrier()).andReturn(null);
		
		//check if the player is at a ball bin (at the end of the move event)
		expect(mockLocalData.getLocationOfPlayer(movingPlayer)).andReturn(new Point(player1TargetX, player1TargetY));
		
		//victory check after all is said and done
		expect(mockLocalData.getBallCarrier()).andReturn(null);
		
		mocksControl.replay();
		
		Queue<Event> events = engine.generateEvents(moveEvent);
		assertEquals(3, events.size());
		
		Event event1 = events.poll();
		Event event2 = events.poll();
		Event event3 = events.poll();
		
		assertEquals(moveEvent, event1);
		
		assertEquals(Event.EVENT_CHECK, event2.getType());
		assertEquals(player2Index, event2.flags[0]);
		assertEquals(player1Index, event2.flags[1]);
		assertEquals(Event.CHECK_FALL, event2.flags[2]);
		assertEquals(1, event2.flags[3]);	//check was a reflex check
		assertEquals(0, event2.flags[4]);
		assertEquals(0, event2.flags[5]);
		assertEquals(0, event2.flags[6]);
		assertEquals(0, event2.flags[7]);
		
		assertEquals(Event.EVENT_STS, event3.getType());
		assertEquals(player1Index, event3.flags[0]);
		assertEquals(0, event3.flags[1]);
		assertEquals(Player.STS_DOWN, event3.flags[2]);
		assertEquals(0, event3.flags[3]);
		assertEquals(0, event3.flags[4]);
		assertEquals(0, event3.flags[5]);
		assertEquals(0, event3.flags[6]);
		assertEquals(0, event3.flags[7]);
		
		mocksControl.verify();
	}
	
	private void setRandomGenerator()
	{
		try
		{
			Field generator = RandomGeneratorSingletonImpl.class.getDeclaredField("instance");
			generator.setAccessible(true);
			generator.set(RandomGeneratorSingletonImpl.class, mockRandomGenerator);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private Player createPlayer(int race, String name, int status)
	{
		Player player = PlayerFactory.createPlayerWithDefinedName(race, name);
		player.status = status;
		return player;
	}
	
	//123
	//456
	//789
	private void assistExpects(Data data, Player attackingPlayer, Player defendingPlayer, int attackingPlayerTeamIndex, int centerX, int centerY, Player p1, Player p2, Player p3, Player p4, Player p5, Player p6, Player p7, Player p8, Player p9)
	{
		expect(data.getTeamIndexOfPlayer(attackingPlayer)).andReturn(attackingPlayerTeamIndex);
		expect(data.getLocationOfPlayer(defendingPlayer)).andReturn(new Point(centerX, centerY));
		
		expect(data.getPlayerAtLocation(centerX - 1, centerY - 1)).andReturn(p1);
		expect(data.getPlayerAtLocation(centerX, centerY - 1)).andReturn(p2);
		expect(data.getPlayerAtLocation(centerX + 1, centerY - 1)).andReturn(p3);
		expect(data.getPlayerAtLocation(centerX - 1, centerY)).andReturn(p4);
		expect(data.getPlayerAtLocation(centerX, centerY)).andReturn(p5);
		expect(data.getPlayerAtLocation(centerX + 1, centerY)).andReturn(p6);
		expect(data.getPlayerAtLocation(centerX - 1, centerY + 1)).andReturn(p7);
		expect(data.getPlayerAtLocation(centerX, centerY + 1)).andReturn(p8);
		expect(data.getPlayerAtLocation(centerX + 1, centerY + 1)).andReturn(p9);
	}
}
