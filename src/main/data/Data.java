package main.data;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.data.entities.Arena;
import main.data.entities.Equipment;
import main.data.entities.Player;
import main.data.entities.Stats;
import main.data.entities.Team;

public class Data
{
	protected List<Team> teams;
	
	protected List<Player> allPlayers;
	protected Player[][] playerLocs = new Player[30][30];

	protected Point ball; // location of the ball; (-1, -1) if someone is carrying it
	protected Player ballCarrier;
	protected Arena arena;

	protected int currentTeam;
	private int gameWinner;		//keeps track if the teams have been saved once the game has concluded
	
	public static final int TIE_GAME = -1;
	public static final int GAME_IN_PROGRESS = -2;
	public static final int TEAM_SIZE = 9;	//TODO: replace all instances of 9 with this

	@Override
	public Data clone()
	{
		// clone the easy to clone data
		Data toRet = new Data();
		toRet.ball = new Point(ball.x, ball.y);
		toRet.arena = arena.clone();
		toRet.currentTeam = currentTeam;
		toRet.gameWinner = gameWinner;

		// go through each player, clone it, and record its location
		for (int i = 0; i < 27; i++)
		{
			Player p = allPlayers.get(i);

			// no player on the team
			if (p == null)
			{
				toRet.allPlayers.add(null);
				continue;
			}

			Player newPlayer = p.clone();
			Point oldLoc = pointOfPlayer.get(p);

			// the player is in play (as opposed to on deck, blobbed, whatever)
			if (oldLoc != null)
			{
				Point newLoc = new Point(oldLoc.x, oldLoc.y);
				toRet.updatePlayerLocation(newPlayer, newLoc);
			}

			// record the team of the player based on its placement in the list
			toRet.teamOfPlayer.put(newPlayer, (int) (i / 9));

			// make this player the ball carrier if necessary
			if (p == ballCarrier)
				toRet.ballCarrier = newPlayer;

			// copy over the player's stats
			Stats newStats = statsOfPlayer.get(p).clone();
			toRet.statsOfPlayer.put(newPlayer, newStats);

			// finally, add it to the list
			toRet.allPlayers.add(newPlayer);
		}
		
		//clone the team, but copy in the players we're currently working with
		for (int i = 0; i < 3; i++)
		{
			Team newTeam = teams.get(i).clone();
			
			int startingIndex = i * 9;
			for (int j = startingIndex; j < startingIndex + 9; j++)
			{
				newTeam.setPlayer(j - startingIndex, toRet.allPlayers.get(j));
			}
			
			toRet.teams.add(newTeam);
		}

		return toRet;
	}

	public Data()
	{
		teams = new ArrayList<Team>();
		allPlayers = new ArrayList<Player>();

		pointOfPlayer = new HashMap<Player, Point>();
		teamOfPlayer = new HashMap<Player, Integer>();
		statsOfPlayer = new HashMap<Player, Stats>();

		for (int i = 0; i < 30; i++)
		{
			for (int j = 0; j < 30; j++)
			{
				playerLocs[i][j] = null;
			}
		}

		ball = new Point(-1, -1);
		ballCarrier = null;
		arena = null;
		currentTeam = 0;
	}
	
	public void newGame(List<Team> allThreeTeams)
	{
		newGame(allThreeTeams, null);
	}
	
	public void newGame(List<Team> allThreeTeams, Integer fieldNum)
	{
		for (int i = 0; i < 3; i++)
		{
			Team curTeam = allThreeTeams.get(i);

			if (curTeam == null)
				throw new IllegalArgumentException("There must be three teams in a game!");
			
			teams.add(curTeam);

			for (int j = 0; j < 9; j++) // no matter how many players are in the team, only take the first 9
			{
				Player p = curTeam.getPlayer(j);

				if (p != null)
				{
					Player pClone = p.clone();

					teamOfPlayer.put(pClone, i);
					statsOfPlayer.put(pClone, new Stats());
					allPlayers.add(pClone);
				} else
				{
					allPlayers.add(null); // when accessing the players, it's important that the correct team's players are in the correct spot
				}
			}
		}

		currentTeam = 0;
		
		int fieldIndex = allThreeTeams.get(0).homeField;	// home field advantage
		
		if (fieldNum != null)								// but there is no such thing in the playoffs 
			fieldIndex = fieldNum;
		
		createMap(fieldIndex);
		
		gameWinner = GAME_IN_PROGRESS;
	}
	
	private void endGame(int winningTeam)
	{
		//save the stats for everyone
		for (Player p : allPlayers)
		{
			if (p != null)
				p.addXP(statsOfPlayer.get(p));
		}
		
		//save the players to their teams (stats, injuries, and all)
		for (int i = 0; i < 3; i++)
		{
			Team team = teams.get(i);
			
			int startingIndex = i * 9;
			for (int j = startingIndex; j < startingIndex + 9; j++)
			{
				Player p = allPlayers.get(j);
				
				if (p.status == Player.STS_DEAD)	//remove the player from the team if he's dead
				{
					//put the dead player's gear back in the team inventory
					for (int k = 0; k < 4; k++)
					{
						int itemIndex = p.unequipItem(k);
						if (itemIndex != Equipment.EQUIP_NONE)
							team.unassignedGear.add(Integer.valueOf(itemIndex));
					}
					
					p = null;						//TODO: if halls of fame exist, keep track of the player still
				}
				
				if (p != null)
					p.recoverInjuries(1);
				
				team.setPlayer(j - startingIndex, p);
			}
		}
		
		gameWinner = winningTeam;
	}

	public void processEvent(Event theEvent)
	{
		// dumbly execute events as they come in; the Engine should've sanitized them so nothing illegal can happen
		// only do one at a time, since by this point everything is resolved and distinct

		if (theEvent.getType() == Event.EVENT_TURN)
		{
			// TODO: Set the AP of all players on the current team to 0.
			currentTeam = theEvent.flags[0];
		} else if (theEvent.getType() == Event.EVENT_VICTORY)
		{
			int winningTeam = theEvent.flags[0];
			setWinningStats(winningTeam);
			endGame(winningTeam);
		} else if (theEvent.getType() == Event.EVENT_RECVR)
		{
			Player p = getPlayer(theEvent.flags[0]);

			if (p != null)
			{
				if (p.status == Player.STS_OKAY)
				{
					p.currentAP = p.getAttributeWithModifiers(Player.ATT_AP);
				} else if (p.status == Player.STS_STUN)
				{
					p.status = Player.STS_DOWN;
				} else if (p.status == Player.STS_DOWN)
				{
					p.status = Player.STS_OKAY;

					if (p.race == Player.RACE_DRAGORAN) // TODO: racial abilities are actually skills
						p.currentAP = p.getAttributeWithModifiers(Player.ATT_AP) - 10;
					else
						p.currentAP = p.getAttributeWithModifiers(Player.ATT_AP) / 2;

					// round AP up
					if (p.currentAP % 10 == 5)
						p.currentAP += 5;
				}
			}
		} else if (theEvent.getType() == Event.EVENT_STS)
		{
			// this ONLY changes a player's status (used for LATE, EGO, and basic stunning)
			// attributes are left untouched (if they need to be altered, another event should be sent
			Player p = getPlayer(theEvent.flags[0]);

			if (p != null)
			{
				p.status = theEvent.flags[2];
				p.currentAP = 0; // TODO I don't like this, but it's easiest. This also makes Juggernaut simple.
			}
		} else if (theEvent.getType() == Event.EVENT_MOVE)
		{
			// again, this is sanitized, so each move event should be from one legal tile to one legal adjacent tile
			// in other words, we expect these to each take 10 AP, and we expect the player to have at least 10 AP to begin with

			Player p = getPlayer(theEvent.flags[0]);
			Point destination = new Point(theEvent.flags[2], theEvent.flags[3]);
			boolean isSliding = (theEvent.flags[4] == 1);
			boolean isJumping = (theEvent.flags[5] == 1);

			// slides don't occur on the player's turn, so they don't consume AP
			if (!isSliding)
				p.currentAP -= 10;

			// if this is a jump, we don't want to blindly wipe out someone the player is jumping over
			if (isJumping)
				updatePlayerLocationForJump(p, destination);
			else
				updatePlayerLocation(p, destination);

			// because a jump is treated as two move events, it automatically reduces the AP by 20. For non-Curmians,
			// it should cumulatively be reduced an additional 10AP (5AP per move event).

			if (isJumping && p.getRace() != Player.RACE_CURMIAN) // TODO: racial abilities are actually skills
				p.currentAP -= 5;
			
			if (p == ballCarrier)
				statsOfPlayer.get(p).rush(1);
		} else if (theEvent.getType() == Event.EVENT_TELE)
		{
			Player p = getPlayer(theEvent.flags[0]);
			int tele1 = theEvent.flags[2];
			int tele2 = theEvent.flags[3];

			Point newLoc = getArena().getPortal(tele2);

			if (tele1 != -1)
			{
				// see if there's a player that we displaced
				Point oldLoc = getLocationOfPlayer(p);
				Player oldPlayer = getPlayerAtLocation(oldLoc);

				clearPlayerLocation(p);

				// If we just nulled a spot where a player should exist (by way of a forced teleport), put that player back.
				// Note that the pointOfPlayer map should be unaffected.
				if (p != oldPlayer)
				{
					setPlayerAtLocation(oldLoc, oldPlayer);
				}

				// XJS bots have gyro stablizers
				// TODO: racial abilities are actually skills
				if (p.race != Player.RACE_XJS9000)
					p.currentAP = 0;
			} else
			{
				p.status = Player.STS_OKAY;
				p.currentAP = p.getAttributeWithModifiers(Player.ATT_AP);
			}

			if (tele1 != tele2)
			{
				updatePlayerLocation(p, newLoc);
			}

			// And this is all we need to do. It's really as simple as that: if another teleport forced this one, don't clear out the old space.
			// We're not concerned about future teleports, because they'll handle it the same way. It's the logic layer that builds the stack,
			// not the data layer.
		} else if (theEvent.getType() == Event.EVENT_BIN)
		{
			System.out.println("BIN EVENT");

			Player p = getPlayer(theEvent.flags[0]);
			int binIndex = theEvent.flags[2];
			int result = theEvent.flags[3];

			p.currentAP = 0;

			arena.setBinStatus(binIndex, result + 1);

			if (result == 1) // success, so make all of them failed
			{
				arena.ballFound(binIndex);
				ballCarrier = p;
				statsOfPlayer.get(p).getBall();
			}

			statsOfPlayer.get(p).tryPad();
		} else if (theEvent.getType() == Event.EVENT_BALLMOVE)
		{
			System.out.println("DATA MOVING BALL");
			ball.x = theEvent.flags[2];
			ball.y = theEvent.flags[3];
			ballCarrier = null;
		} else if (theEvent.getType() == Event.EVENT_GETBALL)
		{
			Player p = getPlayer(theEvent.flags[0]);

			// check if player missed the ball
			if (theEvent.flags[2] == 0)
			{
				p.currentAP = 0;
			} else
			{
				ball.x = -1;
				ball.y = -1;

				ballCarrier = p;
				statsOfPlayer.get(p).getBall();
			}
		} else if (theEvent.getType() == Event.EVENT_HANDOFF)
		{
			Player p = getPlayer(theEvent.flags[0]);

			// handoffs cost 10AP, hurling costs 20
			if (theEvent.flags[2] == Event.HANDOFF_HURL)
				p.currentAP -= 20;
			else
				p.currentAP -= 10;

			ballCarrier = getPlayer(theEvent.flags[1]); // we're going to get a BALLMOVE event after this if it was a hurl, so this doesn't hurt
			statsOfPlayer.get(ballCarrier).getBall();
		} else if (theEvent.getType() == Event.EVENT_CHECK)
		{
			Player attacker = getPlayer(theEvent.flags[0]);
			Player defender = getPlayer(theEvent.flags[1]);
			int result = theEvent.flags[2];

			// stats variables
			boolean success = false;
			boolean sack = false;

			// deduct the proper amount of AP
			if (theEvent.flags[3] == 1) // if this is a reflex check
				attacker.currentAP -= 0; // technically unnecessary, but it makes sense this way
			else if (attacker.hasSkill(Player.SKILL_CHARGE))
				attacker.currentAP -= 10;
			else
				attacker.currentAP -= 20;

			if (result == Event.CHECK_CRITFAIL)
			{
				attacker.currentAP = 0;
				attacker.status = Player.STS_DOWN;
			} else if (result == Event.CHECK_FAIL || result == Event.CHECK_DODGE)
			{
				// nothing happens
			} else if (result == Event.CHECK_DOUBLEFALL)
			{
				attacker.currentAP = 0;
				attacker.status = Player.STS_DOWN;
				defender.currentAP = 0;
				defender.status = Player.STS_DOWN;
				//TODO: determine if this can count as a sack
			} else if (result == Event.CHECK_PUSH)
			{
				// do nothing (the push will be a move action generated by the engine)
				success = true;
			} else if (result == Event.CHECK_FALL || result == Event.CHECK_PUSHFALL)
			{
				success = true;
				defender.currentAP = 0;
				defender.status = Player.STS_DOWN;
				
				if (defender == ballCarrier)
					sack = true;
			}
			
			statsOfPlayer.get(attacker).check(success, sack);
		} else if (theEvent.getType() == Event.EVENT_EJECT)
		{
			Player p = getPlayer(theEvent.flags[0]);
			Player attacker = getPlayer(theEvent.flags[1]);

			//stat variables
			boolean injury = false;
			boolean kill = false;
			
			// get the player off the field
			clearPlayerLocation(p);

			// set player status
			if (theEvent.flags[2] == Event.EJECT_REF)
			{
				p.status = Player.STS_OUT;
			} else if (theEvent.flags[2] == Event.EJECT_BLOB)
			{
				p.status = Player.STS_BLOB;
			} else if (theEvent.flags[2] == Event.EJECT_TRIVIAL)
			{
				p.status = Player.STS_HURT;
				p.setInjuryType(Player.INJURY_TRIVIAL);
				injury = true;
			} else if (theEvent.flags[2] == Event.EJECT_SERIOUS)
			{
				p.status = Player.STS_HURT;
				p.setInjuryType(Player.INJURY_CRIPPLING);
				injury = true;

				if (theEvent.flags[4] == 0 && theEvent.flags[6] == 0)
					p.setInjuryType(Player.INJURY_MINOR);
			} else if (theEvent.flags[2] == Event.EJECT_DEATH)
			{
				p.status = Player.STS_DEAD;
				kill = true;
			}
			
			System.out.println("Data - eject event: " + theEvent);

			// damage the player's attributes if there was an injury
			p.applyInjury(theEvent.flags[3], theEvent.flags[4]);
			p.applyInjury(theEvent.flags[5], theEvent.flags[6]);
			p.setWeeksOut(theEvent.flags[7] + 1);		//add 1 because we're subtracting one for a week passed once the game is done
			
			if (attacker != null)
			{
				if (injury)
					statsOfPlayer.get(attacker).injure();
				if (kill)
					statsOfPlayer.get(attacker).kill();
			}

			// TODO kill the player if he dies (might be as simple as a boolean on the Player object, so hall of fames and such can still exist
			// this might actually just be done by setting p.status equal to STS_DEAD (which is already done)
		}
	}
	
	//note that (as in the original game), this gives everyone the points, whether they made it in or not
	private void setWinningStats(int winningTeam)
	{
		int startingIndex = winningTeam * 9;
		
		for (int i = startingIndex; i < startingIndex + 9; i++)
		{
			Player p = allPlayers.get(i);
			
			if (p == ballCarrier)
				statsOfPlayer.get(p).score();
			else if (p != null)
				statsOfPlayer.get(p).teamWon();
		}
	}

	private String serializeAllData()
	{
		return "";
	}

	private void createMap(int mapNum)
	{
		arena = Arena.generateArena(mapNum);
	}

	protected Map<Player, Point> pointOfPlayer;
	protected Map<Player, Integer> teamOfPlayer;
	protected Map<Player, Stats> statsOfPlayer;

	private void clearPlayerLocation(Player plyr)
	{
		Point p = getLocationOfPlayer(plyr);

		setPlayerAtLocation(p, null);
		pointOfPlayer.remove(plyr);
	}

	private void updatePlayerLocation(Player plyr, Point pnt)
	{
		Point p = getLocationOfPlayer(plyr);

		clearPlayerLocation(plyr);

		if (p == null)
			p = new Point();

		p.x = pnt.x;
		p.y = pnt.y;

		setPlayerAtLocation(p, plyr);
		setLocationOfPlayer(plyr, p);
	}

	private void updatePlayerLocationForJump(Player plyr, Point pnt)
	{
		Point p = getLocationOfPlayer(plyr);

		Player playerHere = getPlayerAtLocation(p);
		Player playerThere = getPlayerAtLocation(pnt);

		// only remove the player at this tile if it's the jumping player; otherwise assume the jumping player is "in the air"
		if (plyr == playerHere)
			setPlayerAtLocation(p, null);

		pointOfPlayer.remove(plyr);

		if (p == null)
			p = new Point();

		p.x = pnt.x;
		p.y = pnt.y;

		// only stick the player back on the map if the space is clear; otherwise assume the jumping player is "in the air"
		if (playerThere == null)
			setPlayerAtLocation(p, plyr);

		setLocationOfPlayer(plyr, p);
	}
	
	public Stats getStatsOfPlayer(Player p)
	{
		return statsOfPlayer.get(p);
	}

	public Point getLocationOfPlayer(Player p)
	{
		return pointOfPlayer.get(p);
	}

	public Player getPlayerAtLocation(Point p)
	{
		if (p == null)
			return null;
		return getPlayerAtLocation(p.x, p.y);
	}

	public Player getPlayerAtLocation(int x, int y)
	{
		// out of bounds obviously has no players
		if (x < 0 || y < 0 || x > 29 || y > 29)
			return null;

		return playerLocs[x][y];
	}

	public int getTeamOfPlayer(Player p)
	{
		return teamOfPlayer.get(p);
	}

	public int getIndexOfPlayer(Player p)
	{
		int index = 0;

		for (Player toCheck : allPlayers)
		{
			if (p == toCheck)
				return index;

			index++;
		}

		return -1;
	}

	public Point setLocationOfPlayer(Player p, Point pnt)
	{
		return pointOfPlayer.put(p, pnt);
	}

	public void setPlayerAtLocation(Point p, Player plyr)
	{
		if (p == null)
			return;

		playerLocs[p.x][p.y] = plyr;
	}

	public int setTeamOfPlayer(Player p, int team)
	{
		return teamOfPlayer.put(p, team);
	}

	public int getCurrentTeam()
	{
		return currentTeam;
	}

	public void setCurrentTeam(int curTeam)
	{
		currentTeam = curTeam;
	}

	public Arena getArena()
	{
		return arena;
	}

	public Player getPlayer(int index)
	{
		try
		{
			return allPlayers.get(index);
		} catch (IndexOutOfBoundsException e)
		{
			return null;
		}
	}

	public Player getBallCarrier()
	{
		return ballCarrier;
	}

	public Point getBallLocation()
	{
		return ball;
	}

	public boolean isStateBallNotFound()
	{
		return arena.isBallFound();
	}

	public boolean isStateBallLoose()
	{
		return !isStateBallNotFound() && (ballCarrier == null);
	}

	public boolean isStateOwnTeamHasBall()
	{
		if (ballCarrier == null)
			return false;

		return (currentTeam == getTeamOfPlayer(ballCarrier));
	}

	public boolean isStateOpponentHasBall()
	{
		if (ballCarrier == null)
			return false;

		return (currentTeam != getTeamOfPlayer(ballCarrier));
	}
	
	public boolean isGameDone()
	{
		return gameWinner != GAME_IN_PROGRESS;
	}
	
	public List<Team> getTeams()
	{
		return teams;
	}

	// DEBUG
	public void printMap()
	{
		for (int i = 0; i < 30; i++)
		{
			for (int j = 0; j < 30; j++)
			{
				if (playerLocs[i][j] == null)
					System.out.print("0, ");
				else
					System.out.print(playerLocs[i][j].name.substring(0, 1) + ", ");
			}

			System.out.println();
		}
	}
}