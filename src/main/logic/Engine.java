package main.logic;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import main.data.Data;
import main.data.Event;
import main.data.entities.Field;
import main.data.entities.Player;
/**
 * class Engine
 * @author Jared
 * 
 * This is the game engine.  At its core, all it does is receive a single game event,
 * manipulate the game data accordingly, and generate additional events that should follow
 * the initial one.
 * 
 * A temporary copy of the data set is made any time events need to be stacked, so that they
 * can execute virtually in the background.
 * 
 */
public class Engine
{
	private Data gameData;
	private Data localData;
	
	public Engine(Data dataSource)
	{
		gameData = dataSource;
		localData = new Data();
	}
	
	public Queue<Event> generateEvents(Event theCommand)
	{
		localData = gameData.clone();
		return generateEventsAndPersistData(theCommand);
	}
	
	public Queue<Event> generateEventsAndPersistData(Event theCommand)
	{
		Queue<Event> toRet = new LinkedList<Event>();
		
		Field curArena = gameData.getArena();
		
		//if an empty event is sent in, return an empty list
		if (theCommand == null) return toRet;
		
		//This is the start of the turn; that is, whatever happens when a new team
		//is selected but before the player is given control.  Event order: 
		// Terror
		// Booster Belt
		// Repulsor
		// Player recovery
		// Vortex
		// Ball movement
		// Next teleport in
		if (theCommand.getType() == Event.EVENT_TURN)
		{
			toRet.offer(theCommand);
			
			int turnUser = theCommand.flags[0];
			int startingIndex = turnUser * 9;
			
			boolean teamHasPlayer = false;
			
			//make every player on this team recover
			for (int i = startingIndex; i < startingIndex + 9; i++)
			{
				//we should probably check if the player is even on the field at this point.
				Player p = gameData.getPlayer(i);
				
				if (p == null)
					continue;
				
				if (p.getStatus() == Player.STS_DECK)
					teamHasPlayer = true;
				
				if (p.getStatus() == Player.STS_OKAY || p.getStatus() == Player.STS_DOWN || p.getStatus() == Player.STS_STUN)
				{
					toRet.offer(Event.recover(i));
					teamHasPlayer = true;
				}
			}
			
			//move the ball if it's loose (ball moves twice at the start of a turn)
			if (gameData.getBallLocation().x > -1 && gameData.getBallLocation().y > -1)
			{
				Event firstMove = moveBall(); 
				
				toRet.offer(firstMove);
				localData.processEvent(firstMove);
				toRet.offer(moveBall());
			}
			
			Queue<Event> warpResults = generateEventsAndPersistData(warpInNextPlayer(turnUser));
			
			for (Event e : warpResults)
			{
				toRet.offer(e);
			}
			
			System.out.println("ENGINE TURN EVENT: turnUser is " + turnUser + " and teamHasPlayer is " + teamHasPlayer);
			
			//if no active players, increment the turn user
			if (!teamHasPlayer)
			{
				/* FIX THIS; it's skipping wrong
				if (turnUser == 2)
					turnUser = 0;
				else
					turnUser++;

				toRet.offer(Event.updateTurnPlayer(turnUser));
				*/
			}
		}
		else if (theCommand.getType() == Event.EVENT_MOVE)
		{
			Player p = gameData.getPlayer(theCommand.flags[0]);
			Point origin = (Point) gameData.getLocationOfPlayer(p).clone();
			Point destination = new Point(theCommand.flags[2], theCommand.flags[3]);
			
			int curAP = p.currentAP;
			
			List<Point> path = createPath(p, origin, destination);

			for (Point pnt : path)
			{
				if (curAP < 10)
					break;
				
				Event e = new Event(Event.EVENT_MOVE);
				e.flags[0] = theCommand.flags[0];
				e.flags[2] = pnt.x;
				e.flags[3] = pnt.y;
				
				//decrease the copy of the AP; don't actually touch the player
				curAP -= 10;
				
				toRet.offer(e);
				
				//CHECK IF THE BALL IS HERE AND ACT ACCORDINGLY
				Point ballLoc = gameData.getBallLocation();
				//System.out.println("Ball is at " + ballLoc);
				
				if (ballLoc.x == pnt.x && ballLoc.y == pnt.y)
				{
					int hands = p.getAttributeWithModifiers(Player.ATT_HD);
					if (!p.hasSkill[Player.SKILL_SCOOP])
					{
						curAP -= 10;
						if (curAP >= 0) toRet.offer(e);	//treat it as moving again if the player has the AP for it (autofail otherwise)
					}
					
					if (curAP < 0)	//we should only get here if the player was pulled onto the ball without Scoop
					{
						curAP = 0;
						hands = 0;	//if a player has Scoop, the AP when getting here should be 0 or higher, so the hands remain normal
					}

					if (skillCheck(hands))
					{
						toRet.offer(Event.getBall(theCommand.flags[0], 1));
					}
					else
					{
						toRet.offer(Event.getBall(theCommand.flags[0], 0));		//indicated that this was a failed attempt
						toRet.offer(moveBall());								//ball runs away if the player misses it
					}
				}
				
				//CHECK IF THIS IS A PORTAL AND ACT ACCORDINGLY
				int potentialPortal = -1;
				
				//assumes exactly 8 portals
				for (int i = 0; i < 8; i++)
				{
					Point prtl = curArena.getPortal(i);
					
					if (prtl.x == pnt.x && prtl.y == pnt.y)
					{
						potentialPortal = i;
						break;
					}
				}
				
				if (potentialPortal > -1)
				{
					Event teleEvent = new Event(Event.EVENT_TELE);
					teleEvent.flags[0] = e.flags[0];
					teleEvent.flags[2] = potentialPortal;
					teleEvent.flags[3] = Randint(0, 7);
					
					Queue<Event> teleResults = generateEventsAndPersistData(teleEvent);
					
					for (Event e2 : teleResults)
					{
						toRet.offer(e2);
					}
					break;	//stop going through move events
				}
				
				//CHECK IF THIS IS A BALL BIN PAD AND ACT ACCORDINGLY
				int binIndex = curArena.getPadIndex(pnt.x, pnt.y);
				
				if (curArena.getBinStatus(binIndex) == Field.STATE_UNTRIED)	//untried ball bin
				{
					int isCorrect = Randint(1, curArena.getUntriedBinCount());
					
					if (isCorrect == 1 || (isCorrect == 2 && p.hasSkill[Player.SKILL_INTUITION]))
					{
						toRet.offer(Event.tryBallBin(theCommand.flags[0], binIndex, 1));
					}
					else
					{
						toRet.offer(Event.tryBallBin(theCommand.flags[0], binIndex, 0));
						//offer another combat event (stunning) once I get to that point 
					}
				}
				
				//CHECK IF THIS IS A GOAL TILE AND ACT ACCORDINGLY
				if (checkForVictory())
				{
					System.out.println("SOMEHOW checkForVictory() returned true.");
					
					int currentTeam = localData.getCurrentTeam();
					toRet.offer(Event.victory(currentTeam));
				}
			}
		}
		else if (theCommand.getType() == Event.EVENT_TELE)
		{
			Player p = gameData.getPlayer(theCommand.flags[0]);
			int tele1 = theCommand.flags[2];
			int tele2 = theCommand.flags[3];
			
			toRet.offer(theCommand);				//right now, just relay this through (only one player anyway)

			//blob logic
			if (tele1 == tele2)
			{
				toRet.offer(Event.eject(theCommand.flags[0], 0, Event.EJECT_BLOB, 0, 0, 0, 0));
				
				if (gameData.getBallCarrier() == p)
					toRet.offer(teleportBall());
			}
			//displacement logic (only checks if player didn't land on own teleporter)
			else
			{
				// 1) Check if there's a player in the target teleporter.
				Point targetPortal = curArena.getPortal(tele2);
				Player toTele = localData.getPlayerAtLocation(targetPortal);
				
				localData.processEvent(theCommand);		//the player in our instance is no longer on his origin teleporter
				
				if (toTele != null)
				{					
					// 2) If so, generate a teleport event for him.
					Event teleEvent = new Event(Event.EVENT_TELE);
					teleEvent.flags[0] = localData.getIndexOfPlayer(toTele);
					teleEvent.flags[2] = theCommand.flags[3];
					teleEvent.flags[3] = Randint(0, 7);
					
					System.out.println("DISPLACEMENT! Player " + theCommand.flags[0] + " is displacing Player " + teleEvent.flags[0]);
					
					// 3) Push the new teleport event after the current one.
					Queue<Event> teleResults = generateEventsAndPersistData(teleEvent);
					
					for (Event e2 : teleResults)
					{
						toRet.offer(e2);
					}
				}
			}
		}
		else if (theCommand.getType() == Event.EVENT_HANDOFF)
		{
			if (theCommand.flags[0] == theCommand.flags[1])		//the player hurled the ball
			{
				Event e = Event.handoff(theCommand.flags[0], theCommand.flags[1], Event.HANDOFF_HURL);
				toRet.offer(e);
				toRet.offer(teleportBall());
			}
			else
			{
				//interesting that we don't actually seem to know where the ball is coming from, since
				//the presentation layer handles valid/invalid targets
				
				boolean ballReceived = (5 <= Randint(1, 100));		//no hands check; 5% chance of failure
				
				Event e = Event.handoff(theCommand.flags[0], theCommand.flags[1], Event.HANDOFF_PASS);
				
				if (ballReceived)
				{
					toRet.offer(e);
				}
				else
				{
					toRet.offer(e);
					toRet.offer(dropBall());
					toRet.offer(moveBall());
				}
			}
		}
		else if (theCommand.getType() == Event.EVENT_CHECK)
		{
			Player attacker = gameData.getPlayer(theCommand.flags[0]);
			Player defender = gameData.getPlayer(theCommand.flags[1]);
			
			int atk_check = attacker.getAttributeWithModifiers(Player.ATT_CH) + Randint(1, 100);
			int def_check = defender.getAttributeWithModifiers(Player.ATT_CH) + Randint(1, 100);
			//TODO: allow for assists
			
			int result = atk_check - def_check;
			boolean dodge = skillCheck(defender.getAttributeWithModifiers(Player.ATT_DA));
			
			System.out.println("ENGINE - CHECK: Attacker rolls " + atk_check + " and defender rolls " + def_check + " for a total of " + result + ".");

			if (gameData.getBallCarrier() == attacker)
				System.out.println("ENGINE - CHECK: Ball carrier is attacking.");
			
			if (gameData.getBallCarrier() == defender)
				System.out.println("ENGINE - CHECK: Ball carrier is being attacked.");
			
			//dodge, but only if the attacker wouldn't have fallen down
			if (dodge && result > -20)
			{
				System.out.println("...but the defender dodged the attack.");
				toRet.offer(Event.check(theCommand.flags[0], theCommand.flags[1], Event.CHECK_DODGE));
			}
			else if (result < -20)
			{
				toRet.offer(Event.check(theCommand.flags[0], theCommand.flags[1], Event.CHECK_CRITFAIL));
				//calculate injury (reverse it)
				
				if (gameData.getBallCarrier() == attacker)
				{
					toRet.offer(dropBall());
					toRet.offer(moveBall());
				}
			}
			else if (result >= -20 && result < -6)
			{
				toRet.offer(Event.check(theCommand.flags[0], theCommand.flags[1], Event.CHECK_FAIL));
			}
			else if (result >= -6 && result <= 5)
			{
				toRet.offer(Event.check(theCommand.flags[0], theCommand.flags[1], Event.CHECK_DOUBLEFALL));
				//calculate injury twice, one normal and one reversed
				//defender's injury is resolved first
				
				if (gameData.getBallCarrier() == attacker || gameData.getBallCarrier() == defender)
				{
					toRet.offer(dropBall());
					toRet.offer(moveBall());
				}
			}
			else if (result > 5 && result <= 20)
			{
				toRet.offer(Event.check(theCommand.flags[0], theCommand.flags[1], Event.CHECK_PUSH));
			}
			else if (result > 20 && result <= 40)
			{
				toRet.offer(Event.check(theCommand.flags[0], theCommand.flags[1], Event.CHECK_FALL));
				//calculate injury
				
				if (gameData.getBallCarrier() == defender)
				{
					toRet.offer(dropBall());
					toRet.offer(moveBall());
				}
			}
			else if (result > 40)
			{
				toRet.offer(Event.check(theCommand.flags[0], theCommand.flags[1], Event.CHECK_PUSHFALL));
				//calculate injury
				
				if (gameData.getBallCarrier() == defender)
				{
					toRet.offer(dropBall());
					toRet.offer(moveBall());
				}
			}
		}
		
		return toRet;
	}
	
	//ball moves in a random direction
	private Event moveBall()
	{
		System.out.println("ENGINE MOVING BALL");
		int playerCount = 0;
		Point ballLoc = localData.getBallLocation();
		
		int curX = ballLoc.x;
		int curY = ballLoc.y;
		
		int newX = -1;
		int newY = -1;
		
		//get a count of the players around the ball carrier
		for (int i = -1; i <= 1; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				if (i == 0 && j == 0)
					continue;
				
				if (gameData.getPlayerAtLocation(curX + i, curY + j) != null)	//SHOULD BE localData EVENTUALLY, BUT FINE FOR NOW
				{
					playerCount++;
				}
			}
		}
		
		if (playerCount > 7)	//ball is surrounded
			return teleportBall();
		
		boolean playerHere = true;
		
		System.out.println("\t moveBall 1");
		
		do
		{
			System.out.println("\t\t inner loop 1");
			
			newX = Randint(-1, 1) + curX;
			newY = Randint(-1, 1) + curY;
			
			System.out.println("ENGINE - MOVING BALL: testing motion from (" + curX + ", " + curY + ") to (" + newX + ", " + newY + ")");
			
			playerHere = gameData.getArena().isObstructed(newX, newY);		//AGAIN, CORRECT EVENTUALLY
			
			System.out.println("\t\t inner loop 2");
			
			//standing still doesn't count
			if (newX == curX && newY == curY)
				playerHere = true;
			
			System.out.println("\t\t inner loop 3");
			
			if (gameData.getPlayerAtLocation(new Point(newX, newY)) != null)	//SAME GOES
				playerHere = true;
			
			System.out.println("\t\t inner loop 4");
			
		} while (playerHere);
		
		System.out.println("\tm oveBall 1");
		
		return Event.moveBall(newX, newY);
	}
	
	//logic for when a player gets knocked down or doesn't receive a handoff.
	//this only puts the ball back on the map; a moveBall event should probably be called after
	private Event dropBall()
	{
		System.out.println("ENGINE DROPPING BALL");
		
		Player carrier = localData.getBallCarrier();
		Point coords = localData.getLocationOfPlayer(carrier);
		
		System.out.println("Drop coordinates are (" + coords.x + ", " + coords.y + ").");
		
		Event toRet = Event.moveBall(coords.x, coords.y); 
		
		localData.processEvent(toRet);		//make sure the engine knows the ball has coordinates again	
		
		return toRet;
	}
	
	//also used for hurling the ball for now
	private Event teleportBall()
	{
		int newX = -1;
		int newY = -1;
		
		boolean playerHere = true;
		
		do
		{
			newX = Randint(1, 28);
			newY = Randint(1, 28);
			playerHere = gameData.getArena().isObstructed(newX, newY);
			
			if (gameData.getPlayerAtLocation(new Point(newX, newY)) != null)
				playerHere = true;
			
		} while (playerHere);
		
		return Event.moveBall(newX, newY);
	}
	
	private List<Point> createPath(Player plyr, Point origin, Point destination)
	{
		List<Point> toRet = new ArrayList<Point>();
		
		while (!origin.equals(destination))
		{
			if (origin.x < destination.x) origin.x++;
			if (origin.y < destination.y) origin.y++;
			if (origin.x > destination.x) origin.x--;
			if (origin.y > destination.y) origin.y--;
			
			int tile = gameData.getArena().getTile(origin.x, origin.y);
			
			if (tile == Field.WALL_TILE || tile == Field.BIN_TILE)
				return toRet;
			
			if (gameData.getPlayerAtLocation(origin) != null)
				return toRet;
			
			System.out.println("Engine - " + plyr + " to " + origin);
			
			toRet.add((Point)origin.clone());
		}
		
		return toRet;
	}
	
	private Event warpInNextPlayer(int user)
	{
		int begin = 9 * user;
		int pIndex = -1;
		Player nextPlayer = null;
				
		for (int i = begin; i < begin + 9; i++)
		{
			Player p = gameData.getPlayer(i);
			
			if (p == null) continue;
			if (p.getStatus() == Player.STS_DECK)
			{
				nextPlayer = p;
				pIndex = i;
				break;
			}
		}
		
		if (pIndex == -1) return null;	//if there simply aren't any more players on deck, ignore this
		
		//REF LOGIC
		
		nextPlayer.status = Player.STS_OKAY;
		
		Event toRet = new Event(Event.EVENT_TELE);
		
		toRet.flags[0] = pIndex;
		toRet.flags[2] = -1;
		toRet.flags[3] = Randint(0, 7);
		
		return toRet;
	}
	
	public static int Randint(int lower, int upper)
	{
		Random r = new Random();
		
		return r.nextInt(upper + 1 - lower) + lower;
	}
	
	private boolean skillCheck(int skillAmt)
	{
		System.out.println("Skill Check with value " + skillAmt);
		
		if (skillAmt < Randint(5, 95))
			return false;
		
		System.out.println("Check passed!");
		
		return true;
	}
	
	//called after ball pickups, handoffs, moves, and (eventually) strips
	/** TODO: Seems that right now, blobbing causes victory. */
	private boolean checkForVictory()
	{
		System.out.println("ENGINE VICTORY CHECK");
		
		Player ballCarrier = localData.getBallCarrier();
		
		if (ballCarrier == null)
			return false;
		
		Point coords = localData.getLocationOfPlayer(ballCarrier);
		int playerTile = localData.getArena().getTile(coords);
		
		System.out.println("ENGINE VICTORY CHECK: tile is " + playerTile + " at point (" + coords.x + ", " + coords.y + ").");
		
		//check if the carrier's tile is a goal tile
		if (playerTile == Field.GOAL_TILE)
			return false;//true;
		
		return false;
	}
}
