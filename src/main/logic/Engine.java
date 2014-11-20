package main.logic;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import main.data.Data;
import main.data.Event;
import main.data.entities.Equipment;
import main.data.entities.Arena;
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
		
		Arena curArena = gameData.getArena();
		
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
			boolean isJumping = false;
			Player p = gameData.getPlayer(theCommand.flags[0]);
			Point origin = (Point) gameData.getLocationOfPlayer(p).clone();
			Point destination = new Point(theCommand.flags[2], theCommand.flags[3]);
			
			int curAP = p.currentAP;
			int curTG = p.getAttributeWithModifiers(Player.ATT_TG);
			
			if (theCommand.flags[5] == 1)
				isJumping = true;
			
			List<Point> path = createPath(p, origin, destination, isJumping);
			
			System.out.println("ENGINE - MOVE EVENT: Path distance is " + path.size());

			for (Point pnt : path)
			{
				boolean slideBool = false;
				boolean jumpBool = false;
				boolean knockdownBool = false;
				
				if (theCommand.flags[4] == 1)
					slideBool = true;
				
				if (theCommand.flags[5] == 1)
					jumpBool = true;
				
				if (theCommand.flags[6] == 1)
					knockdownBool = true;
				
				if (curAP < 10 && !slideBool)
					break;
				
				Event e = Event.move(theCommand.flags[0], pnt.x, pnt.y, slideBool, jumpBool, knockdownBool);
				
				Point ballLoc = gameData.getBallLocation();
				
				//decrease the copy of the AP; don't actually touch the player
				//if the player is sliding, they can go forever
				if (!slideBool)
					curAP -= 10;
				System.out.println("ENGINE - PATH MOVEMENT: curAP is " + curAP + " and p.currentAP is " + p.currentAP);
				
				toRet.offer(e);
				
				//if we're jumping but have only moved one tile, none of the rest applies
				if (isJumping && (p.currentAP - curAP) == 10)
					continue;
				else if (isJumping)	//we're landing, so check for damage (note that failing a jump on shocks precludes you from shocks)
				{
					int curJP = p.getAttributeWithModifiers(Player.ATT_JP);
					if (!skillCheck(curJP))
					{
						curAP = 0;
						
						toRet.offer(calculateInjury(null, p, 20 + Randint(1, 100), curTG + Randint(1, 100)));		//failed jumps attack with 20 strength
						
						//can't pick up the ball if you're on your butt
						if (ballLoc.x == pnt.x && ballLoc.y == pnt.y)
						{
							toRet.offer(Event.getBall(theCommand.flags[0], 0));		//indicated that this was a failed attempt
							toRet.offer(moveBall());								//ball runs away if the player misses it
						}
						
						continue;	//since the player is knocked down, no sense in checking ball bins
					}
				}
				
				//CHECK IF THIS IS AN ELECTRICAL TILE AND ACT ACCORDINGLY
				if (curArena.getTile(pnt.x, pnt.y) == Arena.TILE_SHOCK  && !playerResistsShock(p) && !knockdownBool)
				{
					toRet.offer(calculateInjury(null, p, 60 + Randint(1, 100), curTG + Randint(1, 100)));		//shock tiles attack with 60 strength
					
					if (gameData.getBallCarrier() == p)
					{
						toRet.offer(dropBall());
						toRet.offer(moveBall());
					}
				}
				
				//CHECK IF THE BALL IS HERE AND ACT ACCORDINGLY
				if (ballLoc.x == pnt.x && ballLoc.y == pnt.y)
				{
					int hands = p.getAttributeWithModifiers(Player.ATT_HD);
					if (!p.hasSkill(Player.SKILL_SCOOP))
					{
						curAP -= 10;
						if (curAP >= 0) toRet.offer(e);	//treat it as moving again if the player has the AP for it (autofail otherwise)
					}
					
					if (curAP < 0 ||  knockdownBool)	//we should only get here if a standing player was pulled onto the ball without Scoop
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
				//even if a player was knocked down from a check, they will still teleport
				int potentialPortal = -1;
				
				//assumes exactly 8 portals
				//TODO: Crissick only has 4 portals!!
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
					Event teleEvent = Event.teleport(e.flags[0], potentialPortal, Randint(0, 7));
					toRet = addEventChainToQueue(toRet, teleEvent);
					
					break;	//stop going through move events
				}
				//REFLEX CHECKS HAPPEN BEFORE BALL BIN CHECKS BUT AFTER EVERYTHING ELSE
				Queue<Event> reflexChecks = generateReflexChecks(p, pnt, gameData.getTeamOfPlayer(p));
				toRet.addAll(reflexChecks);
				
				//CHECK IF THIS IS A BALL BIN PAD AND ACT ACCORDINGLY
				// TODO Ignore this section if the player would be knocked down from a reflex check
				//		Probably going to need a global boolean here; this would keep track of knockdown pushes into ball bins, too
				//		possibly use localData here instead
				int binIndex = curArena.getPadIndex(pnt.x, pnt.y);
				
				if (curArena.getBinStatus(binIndex) == Arena.STATE_UNTRIED && !knockdownBool)	//untried ball bin, player is standing
				{
					int isCorrect = Randint(1, curArena.getUntriedBinCount());
					
					if (isCorrect == 1 || (isCorrect == 2 && p.hasSkill(Player.SKILL_INTUITION)))
					{
						toRet.offer(Event.tryBallBin(theCommand.flags[0], binIndex, 1));
					}
					else
					{
						toRet.offer(Event.tryBallBin(theCommand.flags[0], binIndex, 0));
						
						if (!playerResistsShock(p))					//no effect if player has insulated boots
							toRet.offer(calculateInjury(null, p, 20 + Randint(1, 100), curTG + Randint(1, 100)));		//ball bins attack with 20 strength
						
						//don't need to worry about dropping the ball, since there's no way you can have the ball and be shocked by a bin
					}
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
				toRet.offer(Event.eject(theCommand.flags[0], -1, Event.EJECT_BLOB, 0, 0, 0, 0, 0));
				
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
					Event teleEvent = Event.teleport(localData.getIndexOfPlayer(toTele), theCommand.flags[3], Randint(0, 7));
					
					System.out.println("DISPLACEMENT! Player " + theCommand.flags[0] + " is displacing Player " + teleEvent.flags[0]);
					
					// 3) Push the new teleport event after the current one.
					toRet = addEventChainToQueue(toRet, teleEvent);
				}
			}
			
			//check for illegal equipment
			if (tele1 == -1)
			{
				int detectChance = p.getDetectionChance();
				
				System.out.println("ENGINE - WARPING IN: New player's detection chance is " + detectChance + ".");
				
				if (Randint(1, 100) < detectChance)	//player was detected
					toRet.offer(Event.eject(theCommand.flags[0], -1, Event.EJECT_REF, 0, 0, 0, 0, 0));
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
			toRet.addAll((resolveCombat(theCommand)));
		}
		
		//Check to see if the ball is at the goal in someone's control after this chain of events.
		if (checkForVictory())
		{
			System.out.println("SOMEHOW checkForVictory() returned true.");
			
			int currentTeam = localData.getCurrentTeam();
			toRet.offer(Event.victory(currentTeam));
		}
		
		return toRet;
	}
	
	//ball moves in a random direction
	private Event moveBall()
	{
		System.out.println("ENGINE - MOVING BALL");
		int playerCount = 0;
		Point ballLoc = localData.getBallLocation();
		
		int curX = ballLoc.x;
		int curY = ballLoc.y;
		
		int newX = -1;
		int newY = -1;
		
		if (curX < 0 || curY < 0 || curX > 29 || curY > 29)
		{
			System.out.println("ENGINE - MOVING BALL: Ball is out of bounds and cannot move.  Teleporting instead.");
			return teleportBall();
		}
		
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
				else if (gameData.getArena().isObstructed(curX + i, curY + j))	//USE localData, AS ABOVE
				{
					playerCount++;		//it's okay to treat obstructions as players; we're only doing this for a teleport check
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
		
		System.out.println("\t moveBall 1");
		
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
	
	private List<Point> createPath(Player plyr, Point origin, Point destination, boolean isJumping)
	{
		List<Point> toRet = new ArrayList<Point>();
		
		while (!origin.equals(destination))
		{
			if (origin.x < destination.x) origin.x++;
			if (origin.y < destination.y) origin.y++;
			if (origin.x > destination.x) origin.x--;
			if (origin.y > destination.y) origin.y--;
			
			int tile = gameData.getArena().getTile(origin.x, origin.y);
			
			if (tile == Arena.TILE_WALL || tile == Arena.TILE_BIN)
				return toRet;
			
			if (gameData.getPlayerAtLocation(origin) != null && !isJumping)	//block routes that go through other players, until the player is jumping
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
		
		nextPlayer.status = Player.STS_OKAY;
		
		return Event.teleport(pIndex, -1, Randint(0, 7));
	}
	
	//checks how many teammates surround a player, and returns 10 times that number
	//coord is the target location, team is the value a player needs to be to add to the bonus
	private int getAssistBonus(Player ally, Player target)
	{
		//tactics negates assist bonuses
		if (target.hasSkill(Player.SKILL_TACTICS))
			return 0;
		
		int team = gameData.getTeamOfPlayer(ally);
		Point coords = gameData.getLocationOfPlayer(target);
		
		//bonus starts at -10 because the check is going to pick up the player involved in the attack
		int toRet = -10;
		
		for (int i = -1; i <= 1; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				if (i == 0 && j == 0)
					continue;
				
				int x = coords.x + i;
				int y = coords.y + j;
				Player p = gameData.getPlayerAtLocation(x, y);
				
				//if the player is there, is standing, and is co-aligned
				if (p != null && p.getStatus() == Player.STS_OKAY && gameData.getTeamOfPlayer(p) == team)
				{
					toRet += 10;
					
					//teammates with guard help even more
					if (p.hasSkill(Player.SKILL_GUARD))
						toRet += 5;
					
					System.out.println("ENGINE - GET ASSIST: Teammate found; assist bonus is now " + toRet + ".");
				}
			}
		}
		
		return toRet;
	}
	
	//Simply returns a number based on the difference between two stats.  The values passed in
	//are assumed to be already modified.
	private Event calculateInjury(Player attacker, Player defender, int ST, int TG)
	{
		int injLevel = 0;
		int result = ST - TG;
		int pIndex = gameData.getIndexOfPlayer(defender);
		boolean isCheck = (attacker != null);	//we'll pass a null var here for shock effects (including equipment), backfire, falling damage...
												//really anything besides actual checks
		
		Event toRet = null;
		
		if (result < 20)
			injLevel = Player.INJURY_KNOCKDOWN;
		else if (result < 50)
			injLevel = Player.INJURY_STUN;
		else if (result < 60)
			injLevel = Player.INJURY_TRIVIAL;
		else if (result < 70)
			injLevel = Player.INJURY_MINOR;
		else if (result < 74)
			injLevel = Player.INJURY_CRIPPLE_10;
		else if (result < 77)
			injLevel = Player.INJURY_CRIPPLE_15;
		else if (result < 80)
			injLevel = Player.INJURY_CRIPPLE_20;
		else if (result < 90)
			injLevel = Player.INJURY_DEATH_1;
		else if (result < 100)
			injLevel = Player.INJURY_DEATH_2;
		else if (result < 110)
			injLevel = Player.INJURY_DEATH_3;
		else
			injLevel = Player.INJURY_DEATH_4;
				
		if (isCheck)
		{
			//auto-injure 16% of the time with Doomstrike (manual says 25%)
			if (attacker.hasSkill(Player.SKILL_DOOMSTRIKE) && Randint(1, 6) == 1 && injLevel < Player.INJURY_TRIVIAL)
			{
				System.out.println("ENGINE - INJURY CALC: Doomstrike activated.");
				injLevel = Player.INJURY_TRIVIAL;
			}
			
			//auto-stun 16% of the time with Fist of Iron (manual says 25%)
			if (attacker.hasSkill(Player.SKILL_FIST_OF_IRON) && Randint(1, 6) == 1 && injLevel < Player.INJURY_STUN)
			{
				System.out.println("ENGINE - INJURY CALC: Fist of Iron activated.");
				injLevel = Player.INJURY_STUN;
			}
			
			//Vicious adds to the injury type
			if (attacker.hasSkill(Player.SKILL_VICIOUS))
			{
				System.out.println("ENGINE - INJURY CALC: Vicious activated.");
				injLevel++;
			}
			
			//Resilient subtracts from the injury type
			if (defender.hasSkill(Player.SKILL_RESILIENT))
			{
				System.out.println("ENGINE - INJURY CALC: Resilient activated.");
				injLevel--;
			}
		}
		
		if (injLevel <= Player.INJURY_KNOCKDOWN && defender.hasSkill(Player.SKILL_JUGGERNAUT))	//juggernaut prevents basic knockdown
			toRet = Event.setStatus(pIndex, Player.STS_OKAY);
		else if (injLevel <= Player.INJURY_KNOCKDOWN)
			toRet = Event.setStatus(pIndex, Player.STS_DOWN);
		else if (injLevel == Player.INJURY_STUN)
			toRet = Event.setStatus(pIndex, Player.STS_STUN);
		else
			toRet = injurePlayer(defender, injLevel, attacker);
		
		System.out.println("ENGINE - INJURY CALC: Player " + defender.name + " is being attacked.  Attacker's ST is " + ST + ", Defender's TG is " + TG + ", and the injury level is " + injLevel + ".");
		
		return toRet;
	}
	
	//always returns an ejection event; only called if there actually is an injury (not just a stun or knockdown)
	// TODO Modify all of these with docbot stuff
	private Event injurePlayer(Player injuredPlayer, int injLevel, Player causingPlayer)
	{
		int pIndex = gameData.getIndexOfPlayer(injuredPlayer);
		int causeIndex = gameData.getIndexOfPlayer(causingPlayer);
		int weeksOut = Randint(1, 6);
		int type = Event.EJECT_SERIOUS;
		int stat1 = 4 + Randint(0, 3);
		int stat2 = 0;
		int[] penalty = new int[2];
		
		penalty[0] = 0;
		penalty[1] = 0;
		
		if (stat1 == 4) stat1 = Player.ATT_ST;
		if (stat1 == Player.ATT_ST) stat2 = Player.ATT_TG;	//body injury
		if (stat1 == Player.ATT_HD) stat2 = Player.ATT_CH;	//arm injury
		if (stat1 == Player.ATT_JP) stat2 = Player.ATT_AP;	//leg injury
		if (stat1 == Player.ATT_DA) stat2 = Player.ATT_RF;	//head injury
		
		if (injLevel == Player.INJURY_TRIVIAL)
		{
			type = Event.EJECT_TRIVIAL;
			weeksOut = 0;
		}
		if (injLevel == Player.INJURY_MINOR); //do nothing; weeks out are already calculated, and no stat damage is applied
		if (injLevel == Player.INJURY_CRIPPLE_10)
		{
			penalty[0] = 5;
			penalty[1] = 5;
		}
		if (injLevel == Player.INJURY_CRIPPLE_15)
		{
			penalty[0] = 5;
			penalty[1] = 5;
			
			penalty[Randint(0, 1)] += 5;
		}
		if (injLevel == Player.INJURY_CRIPPLE_20)
		{
			penalty[0] = 10;
			penalty[1] = 10;
		}
		if (injLevel >= Player.INJURY_DEATH_1)
		{
			type = Event.EJECT_DEATH;
		}
		
		return Event.eject(pIndex, causeIndex, type, stat1, penalty[0], stat2, penalty[1], weeksOut);
	}
	
	private Queue<Event> resolveCombat(Event theCommand)
	{
		Queue<Event> toRet = new LinkedList<Event>();
		
		boolean reflex = (theCommand.flags[3] == 1);
		
		if (reflex)
			System.out.println("ENGINE - CHECK: Reflex check!");
		
		System.out.println("ENGINE - CHECK: Flags var is: {" + theCommand.flags[0] + ", " + theCommand.flags[1] + ", " + theCommand.flags[2] + ", " + theCommand.flags[3] + "}");
		
		Player attacker = gameData.getPlayer(theCommand.flags[0]);
		Player defender = gameData.getPlayer(theCommand.flags[1]);
		
		System.out.println("\t\t" + attacker);
		System.out.println("\t\t" + defender);

		int atk_CH = attacker.getAttributeWithModifiers(Player.ATT_CH) + getAssistBonus(attacker, defender) + Randint(1, 100);
		int def_CH = defender.getAttributeWithModifiers(Player.ATT_CH) + getAssistBonus(defender, attacker) + Randint(1, 100);
		
		int atk_ST = attacker.getAttributeWithModifiers(Player.ATT_ST) + Randint(1, 100);
		int def_ST = defender.getAttributeWithModifiers(Player.ATT_ST) + Randint(1, 100);
		
		int atk_TG = attacker.getAttributeWithModifiers(Player.ATT_TG) + Randint(1, 100);
		int def_TG = defender.getAttributeWithModifiers(Player.ATT_TG) + Randint(1, 100);
		
		int result = atk_CH - def_CH;
		boolean dodge = skillCheck(defender.getAttributeWithModifiers(Player.ATT_DA));
		
		System.out.println("ENGINE - CHECK: Attacker rolls " + atk_CH + " and defender rolls " + def_CH + " for a total of " + result + ".");

		if (gameData.getBallCarrier() == attacker)
			System.out.println("ENGINE - CHECK: Ball carrier is attacking.");
		
		if (gameData.getBallCarrier() == defender)
			System.out.println("ENGINE - CHECK: Ball carrier is being attacked.");
		
		//dodge, but only if the attacker wouldn't have fallen down
		if (dodge && result > -20)
		{
			System.out.println("...but the defender dodged the attack.");
			toRet.offer(Event.check(theCommand.flags[0], theCommand.flags[1], Event.CHECK_DODGE, reflex));
		}
		else if (result < -20)
		{
			toRet.offer(Event.check(theCommand.flags[0], theCommand.flags[1], Event.CHECK_CRITFAIL, reflex));
			toRet.offer(calculateInjury(defender, attacker, def_ST, atk_TG));
			
			if (gameData.getBallCarrier() == attacker)
			{
				toRet.offer(dropBall());
				toRet.offer(moveBall());
			}
		}
		else if (result >= -20 && result < -6)
		{
			toRet.offer(Event.check(theCommand.flags[0], theCommand.flags[1], Event.CHECK_FAIL, reflex));
			
			//if nothing would happen but the player has repulsor gloves, push the target anyway
			if (playerHasRepulsorGauntlets(attacker) && !playerHasMagneticBoots(defender))
			{
				System.out.println("  Repulsor gauntlets have activated!");
				toRet = addEventChainToQueue(toRet, generatePushEvent(theCommand.flags[0], theCommand.flags[1], false));
			}
		}
		else if (result >= -6 && result <= 5)
		{
			toRet.offer(Event.check(theCommand.flags[0], theCommand.flags[1], Event.CHECK_DOUBLEFALL, reflex));
			toRet.offer(calculateInjury(attacker, defender, atk_ST, def_TG));
			toRet.offer(calculateInjury(defender, attacker, def_ST, atk_TG));
			
			if (gameData.getBallCarrier() == attacker || gameData.getBallCarrier() == defender)
			{
				toRet.offer(dropBall());
				toRet.offer(moveBall());
			}
		}
		else if (result > 5 && result <= 20 && playerHasMagneticBoots(defender))
		{
			toRet.offer(Event.check(theCommand.flags[0], theCommand.flags[1], Event.CHECK_FAIL, reflex));
		}
		else if (result > 5 && result <= 20 && !playerHasMagneticBoots(defender))
		{
			toRet.offer(Event.check(theCommand.flags[0], theCommand.flags[1], Event.CHECK_PUSH, reflex));
			toRet = addEventChainToQueue(toRet, generatePushEvent(theCommand.flags[0], theCommand.flags[1], false));
		}
		else if (result > 20 && result <= 40)
		{
			toRet.offer(Event.check(theCommand.flags[0], theCommand.flags[1], Event.CHECK_FALL, reflex));
			toRet.offer(calculateInjury(attacker, defender, atk_ST, def_TG));
			
			if (gameData.getBallCarrier() == defender)
			{
				toRet.offer(dropBall());
				toRet.offer(moveBall());
			}
		}
		else if (result > 40)
		{
			toRet.offer(Event.check(theCommand.flags[0], theCommand.flags[1], Event.CHECK_PUSHFALL, reflex));
			
			if (!playerHasMagneticBoots(defender))
				toRet = addEventChainToQueue(toRet, generatePushEvent(theCommand.flags[0], theCommand.flags[1], true));
			
			toRet.offer(calculateInjury(attacker, defender, atk_ST, def_TG));
			
			if (gameData.getBallCarrier() == defender)
			{
				toRet.offer(dropBall());
				toRet.offer(moveBall());
			}
		}
		
		return toRet;
	}
	
	private Event generatePushEvent(int pushingPlayerIndex, int pushedPlayerIndex, boolean isKnockdown)
	{
		Player attacker = gameData.getPlayer(pushingPlayerIndex);
		Player defender = gameData.getPlayer(pushedPlayerIndex);

		Point attackerCoords = gameData.getLocationOfPlayer(attacker);
		Point defenderCoords = gameData.getLocationOfPlayer(defender);
		
		int xChange = 0;
		int yChange = 0;
		
		if (attackerCoords.x < defenderCoords.x) xChange = 1;
		if (attackerCoords.x > defenderCoords.x) xChange = -1;
		if (attackerCoords.y < defenderCoords.y) yChange = 1;
		if (attackerCoords.y > defenderCoords.y) yChange = -1;
		
		return Event.move(pushedPlayerIndex, defenderCoords.x + xChange, defenderCoords.y + yChange, true, false, isKnockdown);
	}
	
	private Queue<Event> addEventChainToQueue(Queue<Event> returnQueue, Event event)
	{
		Queue<Event> eventResults = generateEventsAndPersistData(event);
		
		for (Event e2 : eventResults)
		{
			returnQueue.offer(e2);
		}
		
		return returnQueue;
	}

	private Queue<Event> generateReflexChecks(Player plyr, Point coords, int team)
	{
		Queue<Event> toRet = new LinkedList<Event>();
		
		for (int i = -1; i <= 1; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				Player toCheck = gameData.getPlayerAtLocation(coords.x + i, coords.y + j);
				
				//there's a standing opponent here
				if (toCheck != null && toCheck.status == Player.STS_OKAY && gameData.getTeamOfPlayer(toCheck) != team)
				{
					//don't reaction check if moving player has Awe
					if (plyr.hasSkill(Player.SKILL_AWE) && !toCheck.hasSkill(Player.SKILL_STOIC) && Randint(1, 20) < 20)
						continue;
					
					//reflex check passed
					if (skillCheck(toCheck.getAttributeWithModifiers(Player.ATT_RF)))
					{
						int p1 = gameData.getIndexOfPlayer(plyr);
						int p2 = gameData.getIndexOfPlayer(toCheck);
						
						Queue<Event> checkResult = resolveCombat(Event.check(p2, p1, -2, true));
						toRet.addAll(checkResult);
						
						//throw another reflex check if player has Combo skill
						// TODO Don't throw this second check if either player was knocked down by the first one
						//		possibly use localData here
						if (toCheck.hasSkill(Player.SKILL_COMBO))
						{
							checkResult = resolveCombat(Event.check(p2, p1, -2, true));
							toRet.addAll(checkResult);
						}
					}
				}
			}
		}
		
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
	
	private boolean playerResistsShock(Player player)
	{
		return (player.getEquipment(Equipment.EQUIP_BOOTS) == Equipment.EQUIP_INSULATED_BOOTS);
	}
	
	private boolean playerHasRepulsorGauntlets(Player player)
	{
		return (player.getEquipment(Equipment.EQUIP_GLOVES) == Equipment.EQUIP_REPULSOR_GLOVES);
	}
	
	private boolean playerHasMagneticBoots(Player player)
	{
		return (player.getEquipment(Equipment.EQUIP_BOOTS) == Equipment.EQUIP_MAGNETIC_BOOTS);
	}
	
	//called after ball pickups, handoffs, moves, and (eventually) strips
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
		if (playerTile == Arena.TILE_GOAL)
			return true;
			//return false;
		
		return false;
	}
}
