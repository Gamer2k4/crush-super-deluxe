package main.logic;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import main.data.Data;
import main.data.Event;
import main.data.entities.Arena;
import main.data.entities.Equipment;
import main.data.entities.Player;
import main.data.entities.Skill;
import main.presentation.common.Logger;
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
public class LegacyEngineImpl implements Engine
{
	private Data gameData;
	private Data localData;
	
	private static final int RANDOMNESS = 100;
	private static final int MAX_PERCENT = 100;
	
	public LegacyEngineImpl(Data dataImplSource)
	{
		gameData = dataImplSource;
	}
	
	@Override
	public Queue<Event> generateEvents(Event theCommand)
	{
		localData = gameData.clone();
		return generateEventsAndPersistData(theCommand);
	}
	
	private Queue<Event> generateEventsAndPersistData(Event theCommand)
	{
		Queue<Event> toRet = new LinkedList<Event>();
		
		Arena curArena = localData.getArena();
		
		//if an empty event is sent in, return an empty list
		if (theCommand == null) return toRet;
		
		//This is the start of the turn; that is, whatever happens when a new team
		//is selected but before the player is given control.  Event order: 
		// Terror
		// Booster Belt
		// Repulsor
		// Player state recovery
		// Vortex
		// Ball movement
		// Next teleport in
		if (theCommand.getType() == Event.EVENT_TURN)
		{
			processAndOfferEvent(toRet, theCommand);
			
			int turnUser = theCommand.flags[0];
			int startingIndex = turnUser * 9;
			
			boolean teamHasPlayer = false;
			
			//make every player on this team recover
			for (int i = startingIndex; i < startingIndex + 9; i++)
			{
				//we should probably check if the player is even on the field at this point.
				Player player = localData.getPlayer(i);
				
				if (player == null)
					continue;
				
				if (player.getStatus() == Player.STS_DECK)
					teamHasPlayer = true;
				
				if (player.isInGame())
				{
					processAndOfferEvent(toRet, Event.recover(i));
					teamHasPlayer = true;
				}
			}
			
			//move the ball if it's loose (ball moves twice at the start of a turn)
			if (localData.getBallLocation().x > -1 && localData.getBallLocation().y > -1)
			{
				processAndOfferEvent(toRet, generateRandomBallMotionEvent());
				processAndOfferEvent(toRet, generateRandomBallMotionEvent());
			}
			
			Queue<Event> warpResults = generateEventsAndPersistData(warpInNextPlayer(turnUser));
			
			for (Event e : warpResults)
			{
				toRet.offer(e);	//all the events should already be processed locally, so simply add them to the return queue
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
			
			//TODO: I think this is the spot to generate events from the AI
		}
		else if (theCommand.getType() == Event.EVENT_MOVE)
		{
			boolean isJumping = false;
			Player activePlayer = localData.getPlayer(theCommand.flags[0]);
			Point origin = (Point) localData.getLocationOfPlayer(activePlayer).clone();
			Point destination = new Point(theCommand.flags[2], theCommand.flags[3]);
			
			int originalAP = activePlayer.currentAP;
			int curAP = originalAP;
			int curTG = activePlayer.getAttributeWithModifiers(Player.ATT_TG);
			
			if (theCommand.flags[5] == 1)
				isJumping = true;
			
			List<Point> path = createPath(activePlayer, origin, destination, isJumping);
			
			System.out.println("ENGINE - MOVE EVENT: Path distance is " + path.size());
			System.out.println("Origin is: " + origin);
			System.out.println("Destination is: " + destination);
			System.out.println("Path is: " + path);

			//TODO: this could break things with chained actions (perhaps a push check or something), since everything else uses local data
			Player originalPlayer = gameData.getPlayer(theCommand.flags[0]);
			Point lastCoords = gameData.getLocationOfPlayer(originalPlayer);
			
			for (Point playerMoveCoords : path)
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
				
				Event e = Event.move(theCommand.flags[0], getFacing(lastCoords, playerMoveCoords), playerMoveCoords.x, playerMoveCoords.y, slideBool, jumpBool, knockdownBool);
				
				processAndOfferEvent(toRet, e);		//TODO: this processing of the event decreases the player's AP, so it matches the curAP and doesn't actually skip the tile being jumped over
				//TODO: fix this possibly by using an "originalAP" variable from the player so it doesn't decrease as the player's does 
				
				Point ballLoc = localData.getBallLocation();
				
				//decrease the copy of the AP; don't actually touch the player
				//if the player is sliding, they can go forever
				if (!slideBool)
					curAP -= 10;
				System.out.println("ENGINE - PATH MOVEMENT: curAP is " + curAP + ", originalAP is " + originalAP + ", and p.currentAP is " + activePlayer.currentAP);
				
				//TODO: somehow jumping still has players hitting electrical tiles and teleporters on the way.
				//if we're jumping but have only moved one tile, none of the rest applies
				if (isJumping && (originalAP - curAP) == 10)
					continue;
				else if (isJumping)	//we're landing, so check for damage (note that failing a jump on shocks precludes you from shocks)
				{
					int curJP = activePlayer.getAttributeWithModifiers(Player.ATT_JP);
					if (!skillCheck(curJP))
					{
						curAP = 0;
						
						Event failedJumpInjuryEvent = generateCheckResultEvent(null, activePlayer, 20 + Randomizer.getRandomInt(1, RANDOMNESS), curTG + Randomizer.getRandomInt(1, RANDOMNESS));		//failed jumps attack with 20 strength (TODO: confirm this)
						processAndOfferEvent(toRet, failedJumpInjuryEvent);
						
						//can't pick up the ball if you're on your butt
						if (ballLoc.x == playerMoveCoords.x && ballLoc.y == playerMoveCoords.y)
						{
							processAndOfferEvent(toRet, Event.getBall(theCommand.flags[0], 0));		//indicated that this was a failed attempt
							processAndOfferEvent(toRet, generateRandomBallMotionEvent());			//ball runs away if the player misses it
						}
						else if (localData.getBallCarrier() == activePlayer)	//the ball wasn't here, so check if the player had it
						{
							fumbleBall(toRet);
						}
						
						continue;	//since the player is knocked down, no sense in checking ball bins
					}
				}
				
				//CHECK IF THIS IS AN ELECTRICAL TILE AND ACT ACCORDINGLY
				//TODO: when going through a list of destinations, the list keeps going even if a player is hurt
				if (curArena.getTile(playerMoveCoords.x, playerMoveCoords.y) == Arena.TILE_SHOCK  && !playerResistsShock(activePlayer) && !knockdownBool)
				{
					Event shockTileInjuryEvent = generateCheckResultEvent(null, activePlayer, 60 + Randomizer.getRandomInt(1, RANDOMNESS), curTG + Randomizer.getRandomInt(1, RANDOMNESS));		//shock tiles attack with 60 strength (TODO: confirm this)
					processAndOfferEvent(toRet, shockTileInjuryEvent);
					
					if (localData.getBallCarrier() == activePlayer)
					{
						fumbleBall(toRet);
					}
				}
				
				//CHECK IF THE BALL IS HERE AND ACT ACCORDINGLY
				if (ballLoc.x == playerMoveCoords.x && ballLoc.y == playerMoveCoords.y)
				{
					int hands = activePlayer.getAttributeWithModifiers(Player.ATT_HD);
					if (!activePlayer.hasSkill(Skill.SCOOP))
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
						processAndOfferEvent(toRet, Event.getBall(theCommand.flags[0], 1));
					}
					else
					{
						processAndOfferEvent(toRet, Event.getBall(theCommand.flags[0], 0));		//indicated that this was a failed attempt
						processAndOfferEvent(toRet, generateRandomBallMotionEvent());			//ball runs away if the player misses it
					}
				}
				
				//CHECK IF THIS IS A PORTAL AND ACT ACCORDINGLY
				//even if a player was knocked down from a check, they will still teleport
				int potentialPortal = -1;
				
				for (int i = 0; i < curArena.getPortalCount(); i++)
				{
					Point prtl = curArena.getPortal(i);
					
					if (prtl.x == playerMoveCoords.x && prtl.y == playerMoveCoords.y)
					{
						potentialPortal = i;
						break;
					}
				}
				
				if (potentialPortal > -1)
				{
					Event teleEvent = Event.teleport(e.flags[0], potentialPortal, Randomizer.getRandomInt(0, curArena.getPortalCount() - 1));
					toRet = addEventChainToQueue(toRet, teleEvent);
					
					break;	//stop going through move events
				}
				//REFLEX CHECKS HAPPEN BEFORE BALL BIN CHECKS BUT AFTER EVERYTHING ELSE
				Queue<Event> reflexChecks = generateReflexCheckEvents(activePlayer, playerMoveCoords, localData.getTeamIndexOfPlayer(activePlayer));
				toRet.addAll(reflexChecks);
				
				//CHECK IF THIS IS A BALL BIN PAD AND ACT ACCORDINGLY
				int binIndex = curArena.getPadIndex(playerMoveCoords.x, playerMoveCoords.y);
				
				Point currentPlayerLocation = localData.getLocationOfPlayer(activePlayer);
				
				//make it so the ball bin is untestable if the player isn't in a fit state to try it
				if (activePlayer.getStatus() != Player.STS_OKAY || (currentPlayerLocation.x != playerMoveCoords.x || currentPlayerLocation.y != playerMoveCoords.y))
				{
					binIndex = -1;
				}
				
				if (curArena.getBinStatus(binIndex) == Arena.STATE_UNTRIED && !knockdownBool)
				{
					int isCorrect = Randomizer.getRandomInt(1, curArena.getUntriedBinCount());
					
					if (isCorrect == 1 || (isCorrect == 2 && activePlayer.hasSkill(Skill.INTUITION)))
					{
						toRet.offer(Event.tryBallBin(theCommand.flags[0], binIndex, 1));
					}
					else
					{
						toRet.offer(Event.tryBallBin(theCommand.flags[0], binIndex, 0));
						
						if (!playerResistsShock(activePlayer))					//no effect if player has insulated boots
							toRet.offer(generateCheckResultEvent(null, activePlayer, 20 + Randomizer.getRandomInt(1, RANDOMNESS), curTG + Randomizer.getRandomInt(1, RANDOMNESS)));		//ball bins attack with 20 strength
						
						//don't need to worry about dropping the ball, since there's no way you can have the ball and be shocked by a bin
					}
				}
				
				lastCoords = playerMoveCoords;
			}
		}
		else if (theCommand.getType() == Event.EVENT_TELE)
		{
			Player p = localData.getPlayer(theCommand.flags[0]);
			int tele1 = theCommand.flags[2];
			int tele2 = theCommand.flags[3];
			
			toRet.offer(theCommand);				//right now, just relay this through (only one player anyway)

			//blob logic
			if (tele1 == tele2)
			{
				toRet.offer(Event.eject(theCommand.flags[0], -1, Event.EJECT_BLOB, 0, 0, 0, 0, 0));
				
				if (localData.getBallCarrier() == p)
					toRet.offer(generateTeleportBallEvent());
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
					Event teleEvent = Event.teleport(localData.getIndexOfPlayer(toTele), theCommand.flags[3], Randomizer.getRandomInt(0, 7));
					
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
				
				if (Randomizer.getRandomInt(1, MAX_PERCENT) < detectChance)	//player was detected
					toRet.offer(Event.eject(theCommand.flags[0], -1, Event.EJECT_REF, 0, 0, 0, 0, 0));
			}
			
		}
		else if (theCommand.getType() == Event.EVENT_HANDOFF)
		{
			if (theCommand.flags[0] == theCommand.flags[1])		//the player hurled the ball
			{
				Event e = Event.handoff(theCommand.flags[0], theCommand.flags[1], Event.HANDOFF_HURL);
				processAndOfferEvent(toRet, e);
				processAndOfferEvent(toRet, generateTeleportBallEvent());
			}
			else
			{
				//interesting that we don't actually seem to know where the ball is coming from, since
				//the presentation layer handles valid/invalid targets
				
				boolean ballReceived = (5 <= Randomizer.getRandomInt(1, MAX_PERCENT));		//no hands check; 5% chance of failure
				
				Event e = Event.handoff(theCommand.flags[0], theCommand.flags[1], Event.HANDOFF_PASS);
				
				if (ballReceived)
				{
					processAndOfferEvent(toRet, e);
				}
				else
				{
					processAndOfferEvent(toRet, e);
					fumbleBall(toRet);
				}
			}
		}
		else if (theCommand.getType() == Event.EVENT_CHECK)
		{
			toRet.addAll((resolveAndPersistCombat(theCommand)));	//these should already by processed locally in the method
		}
		
		//Check to see if the ball is at the goal in someone's control after this chain of events.
		if (checkForVictory())
		{
			System.out.println("SOMEHOW checkForVictory() returned true.");
			
			int currentTeam = localData.getCurrentTeam();
			toRet.offer(Event.victory(currentTeam));	//no need to process this one locally, since nothing can happen afterward
		}
		
		return toRet;
	}

	private void fumbleBall(Queue<Event> eventQueue)
	{
		processAndOfferEvent(eventQueue, generateDropBallEvent());
		processAndOfferEvent(eventQueue, generateRandomBallMotionEvent());
	}
	
	private void processAndOfferEvent(Queue<Event> queue, Event event)
	{
		localData.processEvent(event);
		queue.offer(event);
	}
	
	//ball moves in a random direction
	private Event generateRandomBallMotionEvent()
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
			return generateTeleportBallEvent();
		}
		
		//get a count of the players around the ball carrier
		for (int i = -1; i <= 1; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				if (i == 0 && j == 0)
					continue;
				
				if (localData.getPlayerAtLocation(curX + i, curY + j) != null)
				{
					playerCount++;
				}
				else if (localData.getArena().isObstructedForBall(curX + i, curY + j))
				{
					playerCount++;		//it's okay to treat obstructions as players; we're only doing this for a teleport check
				}
			}
		}
		
		if (playerCount > 7)	//ball is surrounded
			return generateTeleportBallEvent();
		
		boolean playerHere = true;
		
		System.out.println("\t moveBall 1");
		
		do
		{
			System.out.println("\t\t inner loop 1");
			
			newX = Randomizer.getRandomInt(-1, 1) + curX;
			newY = Randomizer.getRandomInt(-1, 1) + curY;
			
			System.out.println("ENGINE - MOVING BALL: testing motion from (" + curX + ", " + curY + ") to (" + newX + ", " + newY + ")");
			
			playerHere = localData.getArena().isObstructedForBall(newX, newY);
			
			System.out.println("\t\t inner loop 2");
			
			//standing still doesn't count
			if (newX == curX && newY == curY)
				playerHere = true;
			
			System.out.println("\t\t inner loop 3");
			
			if (localData.getPlayerAtLocation(new Point(newX, newY)) != null)
				playerHere = true;
			
			System.out.println("\t\t inner loop 4");
			
		} while (playerHere);
		
		System.out.println("\t moveBall 1");
		
		return Event.moveBall(newX, newY);
	}
	
	//logic for when a player gets knocked down or doesn't receive a handoff.
	//this only puts the ball back on the map; a moveBall event should probably be called after
	private Event generateDropBallEvent()
	{
		System.out.println("ENGINE DROPPING BALL");
		
		Player carrier = localData.getBallCarrier();
		Point coords = localData.getLocationOfPlayer(carrier);
		
		System.out.println("Drop coordinates are (" + coords.x + ", " + coords.y + ").");	//TODO: this threw a null pointer exception, though I don't know why
		
		Event toRet = Event.moveBall(coords.x, coords.y); 
		
		localData.processEvent(toRet);		//make sure the engine knows the ball has coordinates again	
		
		return toRet;
	}
	
	//also used for hurling the ball for now
	private Event generateTeleportBallEvent()
	{
		int newX = -1;
		int newY = -1;
		
		boolean playerHere = true;
		
		do
		{
			newX = Randomizer.getRandomInt(1, 28);
			newY = Randomizer.getRandomInt(1, 28);
			playerHere = localData.getArena().isObstructedForPlayer(newX, newY);
			
			if (localData.getPlayerAtLocation(new Point(newX, newY)) != null)
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
			
			int tile = localData.getArena().getTile(origin.x, origin.y);
			
			if (tile == Arena.TILE_WALL || tile == Arena.TILE_BIN)
				return toRet;
			
			if (localData.getPlayerAtLocation(origin) != null && !isJumping)	//block routes that go through other players, until the player is jumping
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
			Player p = localData.getPlayer(i);
			
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
		
		int portalCount = localData.getArena().getPortalCount();
		return Event.teleport(pIndex, -1, Randomizer.getRandomInt(0, portalCount - 1));
	}
	
	//checks how many teammates surround a player, and returns 10 times that number
	//coord is the target location, team is the value a player needs to be to add to the bonus
	@Override
	public int getAssistBonus(Player ally, Player target)
	{
		localData = gameData;	//no need to clone it; just point to the gameData, since it's not changing at this point
		return getLocalAssistBonus(ally, target);
	}
	
	private int getLocalAssistBonus(Player ally, Player target)
	{
		//tactics negates assist bonuses
		if (target.hasSkill(Skill.TACTICS))
			return 0;
		
		int team = localData.getTeamIndexOfPlayer(ally);
		Point coords = localData.getLocationOfPlayer(target);
		
		//bonus starts at 0 because the check is going to pick up the player involved in the attack
		int toRet = 0;
		
		for (int i = -1; i <= 1; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				int x = coords.x + i;
				int y = coords.y + j;
				Player p = localData.getPlayerAtLocation(x, y);
				
				//don't add the bonuses from the attacker or the defender
				if (p == ally || p == target)
					continue;
				
				//if the player is there, is standing, and is co-aligned
				if (p != null && p.getStatus() == Player.STS_OKAY && localData.getTeamIndexOfPlayer(p) == team)
				{
					toRet += 10;
					
					//teammates with guard help even more
					if (p.hasSkill(Skill.GUARD))
						toRet += 5;
					
					System.out.println("ENGINE - GET ASSIST: Teammate found; assist bonus is now " + toRet + ".");
				}
			}
		}
		
		return toRet;
	}
	
	
	
	//Simply returns a number based on the difference between two stats.  The values passed in
	//are assumed to be already modified.
	private Event generateCheckResultEvent(Player attacker, Player defender, int ST, int TG)
	{
		int injLevel = 0;
		int result = ST - TG;
		int pIndex = localData.getIndexOfPlayer(defender);
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
			if (attacker.hasSkill(Skill.DOOMSTRIKE) && Randomizer.getRandomInt(1, 6) == 1 && injLevel < Player.INJURY_TRIVIAL)
			{
				System.out.println("ENGINE - INJURY CALC: Doomstrike activated.");
				injLevel = Player.INJURY_TRIVIAL;
			}
			
			//auto-stun 16% of the time with Fist of Iron (manual says 25%)
			if (attacker.hasSkill(Skill.FIST_OF_IRON) && Randomizer.getRandomInt(1, 6) == 1 && injLevel < Player.INJURY_STUN)
			{
				System.out.println("ENGINE - INJURY CALC: Fist of Iron activated.");
				injLevel = Player.INJURY_STUN;
			}
			
			//Vicious adds to the injury type
			if (attacker.hasSkill(Skill.VICIOUS))
			{
				System.out.println("ENGINE - INJURY CALC: Vicious activated.");
				injLevel++;
			}
			
			//Resilient subtracts from the injury type
			if (defender.hasSkill(Skill.RESILIENT))
			{
				System.out.println("ENGINE - INJURY CALC: Resilient activated.");
				injLevel--;
			}
		}
		
		if (injLevel <= Player.INJURY_KNOCKDOWN && defender.hasSkill(Skill.JUGGERNAUT))	//juggernaut prevents basic knockdown
			toRet = Event.setStatus(pIndex, Player.STS_OKAY);
		else if (injLevel <= Player.INJURY_KNOCKDOWN)
			toRet = Event.setStatus(pIndex, Player.STS_DOWN);
		else if (injLevel == Player.INJURY_STUN)
			toRet = Event.setStatus(pIndex, Player.STS_STUN);
		else
			toRet = generateInjuryEvent(defender, injLevel, attacker);
		
		System.out.println("ENGINE - INJURY CALC: Player " + defender.name + " is being attacked.  Attacker's ST is " + ST + ", Defender's TG is " + TG + ", and the injury level is " + injLevel + ".");
		
		return toRet;	//we'll process this in the combat method
	}
	
	//always returns an ejection event; only called if there actually is an injury (not just a stun or knockdown)
	// TODO Modify all of these with docbot stuff
	private Event generateInjuryEvent(Player injuredPlayer, int injLevel, Player causingPlayer)
	{
		int pIndex = localData.getIndexOfPlayer(injuredPlayer);
		int causeIndex = localData.getIndexOfPlayer(causingPlayer);
		int weeksOut = Randomizer.getRandomInt(1, 6);
		int type = Event.EJECT_SERIOUS;
		int stat1 = 4 + Randomizer.getRandomInt(0, 3);
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
			
			penalty[Randomizer.getRandomInt(0, 1)] += 5;
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
	
	private Queue<Event> resolveAndPersistCombat(Event theCommand)
	{
		Queue<Event> toRet = new LinkedList<Event>();
		
		boolean reflex = (theCommand.flags[3] == 1);
		
		if (reflex)
			System.out.println("ENGINE - CHECK: Reflex check!");
		
		System.out.println("ENGINE - CHECK: Flags var is: {" + theCommand.flags[0] + ", " + theCommand.flags[1] + ", " + theCommand.flags[2] + ", " + theCommand.flags[3] + "}");
		
		Player attacker = localData.getPlayer(theCommand.flags[0]);
		Player defender = localData.getPlayer(theCommand.flags[1]);
		
		System.out.println("\t\t" + attacker);
		System.out.println("\t\t" + defender);

		int atk_CH = attacker.getAttributeWithModifiers(Player.ATT_CH) + getLocalAssistBonus(attacker, defender) + Randomizer.getRandomInt(1, RANDOMNESS);
		int def_CH = defender.getAttributeWithModifiers(Player.ATT_CH) + getLocalAssistBonus(defender, attacker) + Randomizer.getRandomInt(1, RANDOMNESS);
		
		int atk_ST = attacker.getAttributeWithModifiers(Player.ATT_ST) + Randomizer.getRandomInt(1, RANDOMNESS);
		int def_ST = defender.getAttributeWithModifiers(Player.ATT_ST) + Randomizer.getRandomInt(1, RANDOMNESS);
		
		int atk_TG = attacker.getAttributeWithModifiers(Player.ATT_TG) + Randomizer.getRandomInt(1, RANDOMNESS);
		int def_TG = defender.getAttributeWithModifiers(Player.ATT_TG) + Randomizer.getRandomInt(1, RANDOMNESS);
		
		int result = atk_CH - def_CH;
		boolean dodge = skillCheck(defender.getAttributeWithModifiers(Player.ATT_DA));
		
		System.out.println("ENGINE - CHECK: Attacker rolls " + atk_CH + " and defender rolls " + def_CH + " for a total of " + result + ".");

		if (localData.getBallCarrier() == attacker)
			System.out.println("ENGINE - CHECK: Ball carrier is attacking.");
		
		if (localData.getBallCarrier() == defender)
			System.out.println("ENGINE - CHECK: Ball carrier is being attacked.");
		
		//dodge, but only if the attacker wouldn't have fallen down
		if (dodge && result > -20)
		{
			System.out.println("...but the defender dodged the attack.");
			processAndOfferEvent(toRet, Event.check(theCommand.flags[0], theCommand.flags[1], Event.CHECK_DODGE, reflex));
		}
		else if (result < -20)
		{
			processAndOfferEvent(toRet, Event.check(theCommand.flags[0], theCommand.flags[1], Event.CHECK_CRITFAIL, reflex));
			processAndOfferEvent(toRet, generateCheckResultEvent(defender, attacker, def_ST, atk_TG));
			
			if (localData.getBallCarrier() == attacker)
			{
				fumbleBall(toRet);
			}
		}
		else if (result >= -20 && result < -6)
		{
			processAndOfferEvent(toRet, Event.check(theCommand.flags[0], theCommand.flags[1], Event.CHECK_FAIL, reflex));
			
			//if nothing would happen but the player has repulsor gloves, push the target anyway
			if (playerHasRepulsorGauntlets(attacker) && !playerHasMagneticBoots(defender))
			{
				System.out.println("  Repulsor gauntlets have activated!");
				toRet = addEventChainToQueue(toRet, generatePushEvent(theCommand.flags[0], theCommand.flags[1], false));	//TODO
			}
		}
		else if (result >= -6 && result <= 5)
		{
			processAndOfferEvent(toRet, Event.check(theCommand.flags[0], theCommand.flags[1], Event.CHECK_DOUBLEFALL, reflex));
			processAndOfferEvent(toRet, generateCheckResultEvent(attacker, defender, atk_ST, def_TG));
			processAndOfferEvent(toRet, generateCheckResultEvent(defender, attacker, def_ST, atk_TG));
			
			if (localData.getBallCarrier() == attacker || localData.getBallCarrier() == defender)
			{
				fumbleBall(toRet);
			}
		}
		else if (result > 5 && result <= 20 && playerHasMagneticBoots(defender))
		{
			processAndOfferEvent(toRet, Event.check(theCommand.flags[0], theCommand.flags[1], Event.CHECK_FAIL, reflex));
		}
		else if (result > 5 && result <= 20 && !playerHasMagneticBoots(defender))
		{
			processAndOfferEvent(toRet, Event.check(theCommand.flags[0], theCommand.flags[1], Event.CHECK_PUSH, reflex));
			toRet = addEventChainToQueue(toRet, generatePushEvent(theCommand.flags[0], theCommand.flags[1], false));	//TODO
		}
		else if (result > 20 && result <= 40)
		{
			processAndOfferEvent(toRet, Event.check(theCommand.flags[0], theCommand.flags[1], Event.CHECK_FALL, reflex));
			processAndOfferEvent(toRet, generateCheckResultEvent(attacker, defender, atk_ST, def_TG));
			
			if (localData.getBallCarrier() == defender)
			{
				fumbleBall(toRet);
			}
		}
		else if (result > 40)
		{
			processAndOfferEvent(toRet, Event.check(theCommand.flags[0], theCommand.flags[1], Event.CHECK_PUSHFALL, reflex));
			
			if (!playerHasMagneticBoots(defender))
				toRet = addEventChainToQueue(toRet, generatePushEvent(theCommand.flags[0], theCommand.flags[1], true));	//TODO
			
			processAndOfferEvent(toRet, generateCheckResultEvent(attacker, defender, atk_ST, def_TG));
			
			if (localData.getBallCarrier() == defender)
			{
				fumbleBall(toRet);
			}
		}
		
		return toRet;
	}
	
	private Event generatePushEvent(int pushingPlayerIndex, int pushedPlayerIndex, boolean isKnockdown)
	{
		Player attacker = localData.getPlayer(pushingPlayerIndex);
		Player defender = localData.getPlayer(pushedPlayerIndex);

		Point attackerCoords = localData.getLocationOfPlayer(attacker);
		Point defenderCoords = localData.getLocationOfPlayer(defender);
		
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

	private Queue<Event> generateReflexCheckEvents(Player checkedPlayer, Point coords, int team)
	{
		boolean combo = false;	//this is set to true if we're repeating a check due to a player's combo skill
		
		Queue<Event> toRet = new LinkedList<Event>();
		
		for (int i = -1; i <= 1; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				Point currentPlayerLocation = localData.getLocationOfPlayer(checkedPlayer);
				
				//if at any time in the reflex checks the player is knocked down or moved, that's the end of this (though the move may generate additional checks)
				if (checkedPlayer.getStatus() != Player.STS_OKAY || (currentPlayerLocation.x != coords.x || currentPlayerLocation.y != coords.y))
				{
					return toRet;
				}
				
				Player checkingPlayer = localData.getPlayerAtLocation(coords.x + i, coords.y + j);
				
				//there's a standing opponent here
				if (checkingPlayer != null && checkingPlayer.status == Player.STS_OKAY && localData.getTeamIndexOfPlayer(checkingPlayer) != team)
				{
					//don't reaction check if moving player has Awe
					if (checkedPlayer.hasSkill(Skill.AWE) && !checkingPlayer.hasSkill(Skill.STOIC) && Randomizer.getRandomInt(1, 20) < 20)
						continue;
					
					//reflex check passed, or the second check of a combo
					if (combo || skillCheck(checkingPlayer.getAttributeWithModifiers(Player.ATT_RF)))
					{
						int p1 = localData.getIndexOfPlayer(checkedPlayer);
						int p2 = localData.getIndexOfPlayer(checkingPlayer);
						
						Queue<Event> checkResult = resolveAndPersistCombat(Event.check(p2, p1, Event.CHECK_UNRESOLVED, true));
						toRet.addAll(checkResult);
						
						//throw another reflex check if player has Combo skill
						if (checkingPlayer.hasSkill(Skill.COMBO))
						{
							if (combo) //if we're already in a combo, get out of it and move to the next player (combo check already happened by this point)
							{
								combo = false;
							}
							else	//otherwise indicate the combo check was successful and repeat the player
							{
								combo = true;
								j--;
							}
						}
						else
						{
							combo = false;
						}
					}
				}
			}
		}
		
		return toRet;
	}
	
	private boolean skillCheck(int skillAmt)
	{
		System.out.println("Skill Check with value " + skillAmt);
		
		if (skillAmt < Randomizer.getRandomInt(5, 95))
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

	private int getFacing(Point lastCoords, Point playerMoveCoords)
	{
		Logger.warn("Engine: getting facing - Last Coords: " + lastCoords + ", Move Coords: " + playerMoveCoords);
		
		int x1 = lastCoords.x;
		int y1 = lastCoords.y;
		int x2 = playerMoveCoords.x;
		int y2 = playerMoveCoords.y;
		
		String direction = "";
		
		if (x1 > x2)
			direction = direction + "N";
		if (x1 < x2)
			direction = direction + "S";
		if (y1 > y2)
			direction = direction + "W";
		if (y1 < y2)
			direction = direction + "E";

		if ("SW".equals(direction))
			return 1;
		if ("W".equals(direction))
			return 2;
		if ("NW".equals(direction))
			return 3;
		if ("N".equals(direction))
			return 4;
		if ("NE".equals(direction))
			return 5;
		if ("E".equals(direction))
			return 6;
		if ("SE".equals(direction))
			return 7;
		
		return 0;	//also hit if it's "S"
	}
}
