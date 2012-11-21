package main.data;

import java.io.Serializable;

public class Event implements Serializable
{
	public static final int EVENT_TURN = 0;
	public static final int EVENT_MOVE = 1;
	public static final int EVENT_TELE = 2;
	public static final int EVENT_STS = 3;
	public static final int EVENT_RECVR = 4;
	public static final int EVENT_BIN = 5;
	public static final int EVENT_BALLMOVE = 6;
	public static final int EVENT_GETBALL = 7;
	public static final int EVENT_HANDOFF = 8;
	public static final int EVENT_VICTORY = 9;
	public static final int EVENT_CHECK = 10;
	public static final int EVENT_EJECT = 11;

	public static final int HANDOFF_FALL = -1;
	public static final int HANDOFF_PASS = 0;
	public static final int HANDOFF_HURL = 1;
	
	public static final int CHECK_CRITFAIL = -1;
	public static final int CHECK_DODGE = 0;
	public static final int CHECK_FAIL = 1;
	public static final int CHECK_DOUBLEFALL = 2;
	public static final int CHECK_PUSH = 3;
	public static final int CHECK_FALL = 4;
	public static final int CHECK_PUSHFALL = 5;
	
	public static final int EJECT_REF = 0;
	public static final int EJECT_BLOB = 1;
	public static final int EJECT_TRIVIAL = 2;
	public static final int EJECT_SERIOUS = 3;
	public static final int EJECT_DEATH = 4;
	
	private int type;
	private final int TOTAL_FLAGS = 7;
	
	public int[] flags = new int[TOTAL_FLAGS];
	
	public Event(int evt_type)
	{
		type = evt_type;
		
		for (int i = 0; i < TOTAL_FLAGS; i++)
		{
			flags[i] = 0;
		}
	}
	
	public String toString()
	{
		String toRet = "UNDEFINED EVENT - TYPE " + type;
		
		if (type == 0)
			toRet = "EVENT_TURN";
		else if (type == 1)
			toRet = "EVENT_MOVE";
		else if (type == 2)
			toRet = "EVENT_TELE";
		else if (type == 3)
			toRet = "EVENT_STS";
		else if (type == 4)
			toRet = "EVENT_RECVR";
		else if (type == 5)
			toRet = "EVENT_BIN";
		else if (type == 6)
			toRet = "EVENT_BALLMOVE";
		else if (type == 7)
			toRet = "EVENT_GETBALL";
		else if (type == 8)
			toRet = "EVENT_HANDOFF";
		else if (type == 9)
			toRet = "EVENT_VICTORY";
		else if (type == 10)
			toRet = "EVENT_CHECK";
		else if (type == 11)
			toRet = "EVENT_EJECT";
		
		
		for (int i = 0; i < TOTAL_FLAGS; i++)
		{
			toRet = toRet + ", " + flags[i];
		}
		
		return toRet;
	}
	
	public int getType()
	{
		return type;
	}
	
	public static Event updateTurnPlayer(int player)
	{
		Event e = new Event(EVENT_TURN);
		e.flags[0] = player;
		
		return e;
	}
	
	public static Event move(int player, int targetX, int targetY, boolean slide, boolean jump)
	{
		Event e = new Event(EVENT_MOVE);
		e.flags[0] = player;
		e.flags[2] = targetX;
		e.flags[3] = targetY;
		e.flags[4] = 0;
		e.flags[5] = 0;
		
		if (slide)
			e.flags[4] = 1;
		
		if (jump)
			e.flags[5] = 1;
		
		return e;
	}
	
	public static Event setStatus(int player, int newStatus)
	{
		Event e = new Event(EVENT_STS);
		e.flags[0] = player;
		e.flags[2] = newStatus;
		
		return e;
	}
	
	//this tells the data layer to restore AP, reduce stun severity, etc.
	public static Event recover(int player)
	{
		Event e = new Event(EVENT_RECVR);
		e.flags[0] = player;
		
		return e;
	}
	
	public static Event tryBallBin(int player, int binIndex, int result)
	{
		Event e = new Event(EVENT_BIN);
		e.flags[0] = player;
		e.flags[2] = binIndex;
		e.flags[3] = result;
		
		return e;
	}
	
	public static Event moveBall(int targetX, int targetY)
	{
		Event e = new Event(EVENT_BALLMOVE);
		e.flags[2] = targetX;
		e.flags[3] = targetY;
		
		return e;
	}
	
	public static Event getBall(int player, int success)
	{
		Event e = new Event(EVENT_GETBALL);
		e.flags[0] = player;
		e.flags[2] = success;
		
		return e;
	}
	
	public static Event handoff(int p1, int p2, int hurl)
	{
		Event e = new Event(EVENT_HANDOFF);
		e.flags[0] = p1;
		e.flags[1] = p2;
		e.flags[2] = hurl;
		
		return e;
	}
	
	public static Event victory(int team)
	{
		Event e = new Event(EVENT_VICTORY);
		e.flags[0] = team;
		
		return e;
	}
	
	public static Event check(int p1, int p2, int result, boolean reflex)
	{
		Event e = new Event(EVENT_CHECK);
		e.flags[0] = p1;
		e.flags[1] = p2;
		e.flags[2] = result;
		e.flags[3] = 0;
		
		if (reflex)
			e.flags[3] = 1;
		
		return e;
	}
	
	public static Event eject(int player, int weeksOut, int type, int stat1, int penalty1, int stat2, int penalty2)
	{
		Event e = new Event(EVENT_EJECT);
		e.flags[0] = player;
		e.flags[1] = weeksOut;
		e.flags[2] = type;
		e.flags[3] = stat1;
		e.flags[4] = penalty1;
		e.flags[5] = stat2;
		e.flags[6] = penalty2;
		
		return e;
	}
}

/**
 * Key
 * ---
 * EVENT_TURN
 * 0 - current turn
 * 
 * EVENT_MOVE:
 * 0 - p1 index
 * 2 - target x
 * 3 - target y
 * 4 - slide boolean
 * 5 - jump boolean
 * 
 * EVENT_TELE
 * 0 - p1 index
 * 2 - initial portal
 * 3 - final portal
 * 
 * EVENT_STS
 * 0 - p1 index
 * 2 - the new status to give the player
 * 
 * EVENT_RECVR
 * 0 - p1 index
 * 
 * EVENT_BIN
 * 0 - p1 index
 * 2 - bin index
 * 3 - bin result (1 if success, 0 if fail)
 * 
 * EVENT_BALLMOVE:
 * 2 - target x
 * 3 - target y
 * 
 * EVENT_GETBALL
 * 0 - p1 index
 * 2 - get result (1 if success, 0 if fail)
 * 
 * EVENT_HANDOFF
 * 0 - p1 index
 * 1 - p2 index
 * 2 - hurl boolean (1 if hurling, 0 if normal handoff, -1 if failed handoff; used in data layer)
 * assumed successful because otherwise it wouldn't be passed through to data layer
 * 
 * EVENT_VICTORY
 * 0 - index of winning team
 * 
 * EVENT_CHECK
 * 0 - p1 index (-1 if the field, such as falling damage or getting shocked)
 * 1 - p2 index
 * 2 - result (-1 - attacker falls, 0 - dodged, 1 - nothing, 2 - both fall, 3 - defender pushed, 4 - defender falls, 5 - defender pushed and falls)
 * 3 - reflex boolean (1 means the player doesn't need or use AP)
 * 
 * EVENT_EJECT
 * 0 - p1 index
 * 1 - weeks on bench
 * 2 - ejection type (0 for equipment, 1 for blob, 2 for trivial injury, 3 for non-trivial injury, 4 for death)
 * 3 - first stat index
 * 4 - first stat penalty
 * 5 - second stat index
 * 6 - second stat penalty
 */