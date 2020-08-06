package test.data;

import static org.junit.Assert.assertEquals;
import main.data.Event;

import org.junit.Before;
import org.junit.Test;

public class EventTest {

	Event event;
	String toStringVal;
	int type;
	
	@Before
	public void setUp()
	{
		event = null;
		toStringVal = "";
		type = -1;
	}
	
	@Test
	public void testUpdateTurnEvent()
	{
		event = Event.updateTurnPlayer(1);
		toStringVal = "EVENT_TURN, 1, 0, 0, 0, 0, 0, 0, 0";
		type = Event.EVENT_TURN;
		
		assertEquals(1, event.flags[0]);
		assertEquals(0, event.flags[1]);
		assertEquals(0, event.flags[2]);
		assertEquals(0, event.flags[3]);
		assertEquals(0, event.flags[4]);
		assertEquals(0, event.flags[5]);
		assertEquals(0, event.flags[6]);
		
		assertEquals(toStringVal, event.toString());
		assertEquals(type, event.getType());
	}
	
	@Test
	public void testMoveEvent_TrueArgs()
	{
		event = Event.move(3, 22, 16, true, true, true);
		toStringVal = "EVENT_MOVE, 3, 0, 22, 16, 1, 1, 1, 0";
		type = Event.EVENT_MOVE;
		
		assertEquals(3, event.flags[0]);
		assertEquals(0, event.flags[1]);
		assertEquals(22, event.flags[2]);
		assertEquals(16, event.flags[3]);
		assertEquals(1, event.flags[4]);
		assertEquals(1, event.flags[5]);
		assertEquals(1, event.flags[6]);
		
		assertEquals(toStringVal, event.toString());
		assertEquals(type, event.getType());
	}
	
	@Test
	public void testMoveEvent_FalseArgs()
	{
		event = Event.move(2, 11, 4, false, false, false);
		toStringVal = "EVENT_MOVE, 2, 0, 11, 4, 0, 0, 0, 0";
		type = Event.EVENT_MOVE;
		
		assertEquals(2, event.flags[0]);
		assertEquals(0, event.flags[1]);
		assertEquals(11, event.flags[2]);
		assertEquals(4, event.flags[3]);
		assertEquals(0, event.flags[4]);
		assertEquals(0, event.flags[5]);
		assertEquals(0, event.flags[6]);
		
		assertEquals(toStringVal, event.toString());
		assertEquals(type, event.getType());
	}
	
	@Test
	public void testTeleportEvent()
	{
		event = Event.teleport(2, 5, 8);
		toStringVal = "EVENT_TELE, 2, 0, 5, 8, 0, 0, 0, 0";
		type = Event.EVENT_TELE;
		
		assertEquals(2, event.flags[0]);
		assertEquals(0, event.flags[1]);
		assertEquals(5, event.flags[2]);
		assertEquals(8, event.flags[3]);
		assertEquals(0, event.flags[4]);
		assertEquals(0, event.flags[5]);
		assertEquals(0, event.flags[6]);
		
		assertEquals(toStringVal, event.toString());
		assertEquals(type, event.getType());
	}
}
