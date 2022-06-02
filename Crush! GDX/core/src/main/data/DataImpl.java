package main.data;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.data.entities.Arena;
import main.data.entities.Equipment;
import main.data.entities.Pace;
import main.data.entities.Player;
import main.data.entities.Skill;
import main.data.entities.Stats;
import main.data.entities.Team;
import main.data.factory.LegacyArenaFactory;
import main.data.factory.SimpleArenaFactory;
import main.presentation.common.GameSettings;
import main.presentation.common.Logger;
import main.presentation.game.PresentationMode;

public class DataImpl implements Data
{
	protected List<Team> teams;
	
	protected List<Player> allPlayers;
	protected Player[][] playerLocs = new Player[30][30];

	protected Point ball; // location of the ball; (-1, -1) if someone is carrying it
	protected Player ballCarrier;
	protected Arena arena;

	protected int currentTeam;
	private int turnsRemaining;
	private int gameWinner;
	private Pace pace;
	
	public static final int TIE_GAME = -1;
	public static final int GAME_IN_PROGRESS = -2;
	public static final int GAME_CANCELLED = -3;
	public static final int TEAM_SIZE = 9;
	
	public static final int AP_MOVE_COST = 10;
	public static final int AP_PICKUP_COST = 10;
	public static final int AP_HANDOFF_COST = 10;
	public static final int AP_HURL_COST = 20;
	public static final int AP_JUMP_COST = 30;
	public static final int AP_HIGH_JUMP_COST = 20;
	public static final int AP_CHECK_COST = 20;
	public static final int AP_CHARGE_COST = 10;
	public static final int AP_POPUP_COST = 10;
	
	private boolean gameActive = false;

	@Override
	public Data clone()
	{
		// clone the easy to clone data
		DataImpl toRet = new DataImpl(name + "[clone]");
		toRet.ball = new Point(ball.x, ball.y);
		toRet.currentTeam = currentTeam;
		toRet.turnsRemaining = turnsRemaining;
		toRet.gameWinner = gameWinner;
		toRet.pace = pace;
		
		if (arena != null)
			toRet.arena = arena.clone();

		// go through each player, clone it, and record its location
		for (int i = 0; i < allPlayers.size(); i++)
		{
			Player player = allPlayers.get(i);

			// no player on the team
			if (player == null)
			{
				toRet.allPlayers.add(null);
				continue;
			}

			Player newPlayer = player.clone();
			Point oldLoc = pointOfPlayer.get(player);

			// the player is in play (as opposed to on deck, blobbed, whatever)
			if (oldLoc != null)
			{
				Point newLoc = new Point(oldLoc.x, oldLoc.y);
				toRet.updatePlayerLocation(newPlayer, newLoc);
			}

			// record the team of the player based on its placement in the list
			toRet.teamOfPlayer.put(newPlayer, (int) (i / TEAM_SIZE));

			// make this player the ball carrier if necessary
			if (player == ballCarrier)
				toRet.ballCarrier = newPlayer;

			// copy over the player's stats
			Stats newStats = statsOfPlayer.get(player).clone();
			toRet.statsOfPlayer.put(newPlayer, newStats);

			// finally, add it to the list
			toRet.allPlayers.add(newPlayer);
		}
		
		//clone the team, but copy in the players we're currently working with
		for (int i = 0; i < teams.size(); i++)
		{
			Team newTeam = teams.get(i).clone();
			
			int startingIndex = i * TEAM_SIZE;
			for (int j = startingIndex; j < startingIndex + TEAM_SIZE; j++)
			{
				newTeam.setPlayer(j - startingIndex, toRet.allPlayers.get(j));
			}
			
			toRet.teams.add(newTeam);
		}

		return toRet;
	}

	String name;
	
	public DataImpl()
	{
		this("unnamed");
	}
	
	public DataImpl(String dataName)
	{
		name = dataName;
		
		teams = new ArrayList<Team>();
		allPlayers = new ArrayList<Player>();

		pointOfPlayer = new HashMap<Player, Point>();
		teamOfPlayer = new HashMap<Player, Integer>();
		statsOfPlayer = new HashMap<Player, Stats>();

		clearAllData();
	}
	
	private void clearAllData()
	{
		teams.clear();
		allPlayers.clear();
		
		pointOfPlayer.clear();
		teamOfPlayer.clear();
		statsOfPlayer.clear();
		
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
		turnsRemaining = 20;
		gameWinner = GAME_IN_PROGRESS;
		pace = Pace.RELAXED;
	}
	
	@Override
	public void newGame(List<Team> allThreeTeams)
	{
		newGame(allThreeTeams, -1);
	}
	
	@Override
	public void newGame(List<Team> allThreeTeams, int fieldNum)
	{
		newGame(allThreeTeams, fieldNum, Pace.RELAXED, 20);
	}
	
	@Override
	public void newGame(List<Team> allThreeTeams, int fieldNum, Pace gamePace, int turns)
	{
		if (allThreeTeams.size() != 3)
			throw new IllegalArgumentException("There must be three teams in a game!");	//no more; no less.  Empty teams are okay; missing teams are not.
		
		clearAllData();
		
		for (int i = 0; i < 3; i++)
		{
			Team curTeam = allThreeTeams.get(i);

			if (curTeam == null)
				throw new IllegalArgumentException("There must be three teams in a game!");
			
			curTeam.clearLastGameStats();
			teams.add(curTeam);

			for (int j = 0; j < TEAM_SIZE; j++)		//only bring in players within the defined team size, even if they have more available
			{
				Player player = curTeam.getPlayer(j);
				
				if (player != null)
				{
					Player playerClone = putPlayerOnDeck(player.clone());
					
					teamOfPlayer.put(playerClone, i);
					statsOfPlayer.put(playerClone, new Stats());
					allPlayers.add(playerClone);
					
				} else
				{
					allPlayers.add(null); // when accessing the players, it's important that the correct team's players are in the correct spot
				}
			}
		}

		currentTeam = 0;
		
		int fieldIndex = allThreeTeams.get(0).homeField;	// home field advantage
		
		if (fieldNum != -1)								// but there is no such thing in the playoffs 
			fieldIndex = fieldNum;
		
		createMap(fieldIndex);
		
		turnsRemaining = turns;
		pace = gamePace;
		gameWinner = GAME_IN_PROGRESS;
		gameActive = true;
	}
	
	private Player putPlayerOnDeck(Player player)
	{
		if (player == null)
			return null;
		
		//TODO: technically, this should be updated at the end of the game, not the beginning
		if (player.getStatus() == Player.STS_OKAY || player.getStatus() == Player.STS_DOWN
				|| player.getStatus() == Player.STS_STUN_DOWN || player.getStatus() == Player.STS_STUN_SIT
				|| player.getWeeksOut() <= 0)
			player.status = Player.STS_DECK;
		else
			player.recoverInjuries(1);	//okay to do it here, because if the game is cancelled, it all gets undone anyway
		
		return player;
	}

	@Override
	public void endGame(int winningTeam)
	{
		gameActive = false;
		
		//save the stats for everyone
		for (Player player : allPlayers)
		{
			if (player != null)
			{
				Stats stats = statsOfPlayer.get(player);
				stats.setStat(Stats.STATS_HIGHEST_RATING, stats.getXP());
				player.addXP(stats);
				player.incrementGamesPlayed();
			}
		}
		
		//save the players to their teams (stats, injuries, and all)
		for (int i = 0; i < 3; i++)
		{
			Team team = teams.get(i);
			
			int startingIndex = i * TEAM_SIZE;
			for (int j = startingIndex; j < startingIndex + TEAM_SIZE; j++)
			{
				Player player = allPlayers.get(j);

				if (player == null)
					continue;
				
				team.getLastGameStats().updateWithResults(player.getLastGameStats());	//even if the player is dead, his stats should still add to the team's
				
				if (player.status == Player.STS_DEAD)	//remove the player from the team if he's dead
				{
					//put the dead player's gear back in the team inventory
					for (int k = 0; k < 4; k++)
					{
						int itemIndex = player.unequipItem(k);
						if (itemIndex != Equipment.EQUIP_NONE)
							team.getEquipment().add(Integer.valueOf(itemIndex));
					}
					
					player = null;						//TODO: if halls of fame exist, keep track of the player still
				}
				else if (player.status == Player.STS_LATE ||
						 player.status == Player.STS_OKAY || 
						 player.status == Player.STS_DOWN ||
						 player.status == Player.STS_STUN_DOWN || 
						 player.status == Player.STS_STUN_SIT || 
						 player.status == Player.STS_BLOB || 
						 player.status == Player.STS_OUT)
					player.status = Player.STS_DECK;	//note that doing this here DOES clear out the statuses when the victory screen is being displayed
				
				team.setPlayer(j - startingIndex, player);
			}
		}
		
		gameWinner = winningTeam;
	}
	
	@Override
	public int getNextStateForRecoveringPlayer(Player player)
	{
		if (player == null)
			throw new IllegalArgumentException("Failed retrieval of next state for recovering player; player cannot be null.");
		
		if (player.status == Player.STS_STUN_DOWN)
			return Player.STS_STUN_SIT;
		if (player.status == Player.STS_DOWN || player.status == Player.STS_STUN_SIT)
			return Player.STS_OKAY;
			
		return player.status;
	}

	@Override
	public void processEvent(Event theEvent)
	{
		if (theEvent == null)
		{
			Logger.warn("Null event received by Data layer.");
			return;
		}
		
		// dumbly execute events as they come in; the Engine should've sanitized them so nothing illegal can happen
		// only do one at a time, since by this point everything is resolved and distinct
		
		Logger.debug("\t\tData " + name + " received event: " + theEvent);

		if (theEvent.getType() == Event.EVENT_TURN)
		{
			processNewTurn(theEvent.flags[0]);
		} else if (theEvent.getType() == Event.EVENT_VICTORY)
		{
			int winningTeam = theEvent.flags[0];
			setGameResultStats(winningTeam);
			endGame(winningTeam);
		} else if (theEvent.getType() == Event.EVENT_RECVR)
		{
			Player player = getPlayer(theEvent.flags[0]);

			if (player != null)
			{
				if (player.status == Player.STS_OKAY)
				{
					player.currentAP = player.getAttributeWithModifiers(Player.ATT_AP);
				} else if (player.status == Player.STS_DOWN || player.status == Player.STS_STUN_SIT)
				{
					if (player.hasSkill(Skill.POP_UP))
						player.currentAP = player.getAttributeWithModifiers(Player.ATT_AP) - AP_POPUP_COST;
					else
						player.currentAP = halfAttribute(player.getAttributeWithModifiers(Player.ATT_AP));
				}
				
				player.status = getNextStateForRecoveringPlayer(player);
			}
		} else if (theEvent.getType() == Event.EVENT_STS)
		{
			// this ONLY changes a player's status (used for LATE, EGO, and basic stunning)
			// attributes are left untouched (if they need to be altered, another event should be sent
			Player player = getPlayer(theEvent.flags[0]);

			if (player != null)
			{
				player.status = theEvent.flags[2];
				player.currentAP = 0; // TODO I don't like this, but it's easiest. This also makes Juggernaut simple.
			}
		} else if (theEvent.getType() == Event.EVENT_MOVE)
		{
			// again, this is sanitized, so each move event should be from one legal tile to one legal adjacent tile
			// in other words, we expect these to each take 10 AP, and we expect the player to have at least 10 AP to begin with

			Player player = getPlayer(theEvent.flags[0]);
			Point destination = new Point(theEvent.flags[2], theEvent.flags[3]);
			boolean isSliding = (theEvent.flags[4] == 1);
			boolean isJumping = (theEvent.flags[5] == 1);

			// slides don't occur on the player's turn, so they don't consume AP
			if (!isSliding)
				player.currentAP -= AP_MOVE_COST;

			// if this is a jump, we don't want to blindly wipe out someone the player is jumping over
			if (isJumping)
				updatePlayerLocationForJump(player, destination);
			else
				updatePlayerLocation(player, destination);

			// because a jump is treated as two move events, it automatically reduces the AP by 20. For non-Curmians,
			// it should cumulatively be reduced an additional 10AP (5AP per move event).
			int highJumpApModifier = (AP_JUMP_COST - AP_HIGH_JUMP_COST) / 2;
			
			if (isJumping && !player.hasSkill(Skill.HIGH_JUMP))
				player.currentAP -= highJumpApModifier;
			
			if (player == ballCarrier)
				statsOfPlayer.get(player).rush(1);
		} else if (theEvent.getType() == Event.EVENT_TELE)
		{
			processTeleport(theEvent);
		} else if (theEvent.getType() == Event.EVENT_BIN)
		{
			Player player = getPlayer(theEvent.flags[0]);
			int binIndex = theEvent.flags[2];
			int result = theEvent.flags[3];

			player.currentAP = 0;

			arena.setBinStatus(binIndex, result + 1);

			if (result == 1) // success, so make all of them failed
			{
				arena.ballFound(binIndex);
				ballCarrier = player;
				statsOfPlayer.get(player).getBall();
			}

			statsOfPlayer.get(player).tryPad();
		} else if (theEvent.getType() == Event.EVENT_BALLMOVE)
		{
			Logger.debug("DATA MOVING BALL");
			ball.x = theEvent.flags[2];
			ball.y = theEvent.flags[3];
			
			if (ballCarrier != null)
				statsOfPlayer.get(ballCarrier).fumbleBall();
			
			ballCarrier = null;
		} else if (theEvent.getType() == Event.EVENT_GETBALL)
		{
			Player player = getPlayer(theEvent.flags[0]);

			// check if player missed the ball
			if (theEvent.flags[2] == 0)
			{
				player.currentAP = 0;
			} else
			{
				ball.x = -1;
				ball.y = -1;

				ballCarrier = player;
				statsOfPlayer.get(player).getBall();
				
				//TODO: the additional AP cost to get the ball is treated by the engine as a duplicate move
			}
		} else if (theEvent.getType() == Event.EVENT_HANDOFF)
		{
			Player player = getPlayer(theEvent.flags[0]);

			// handoffs cost 10AP, hurling costs 20
			if (theEvent.flags[2] == Event.HANDOFF_HURL)
				player.currentAP -= AP_HURL_COST;
			else
				player.currentAP -= AP_HANDOFF_COST;
			
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
			else if (attacker.hasSkill(Skill.CHARGE))
				attacker.currentAP -= AP_CHARGE_COST;
			else
				attacker.currentAP -= AP_CHECK_COST;

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
				{
					statsOfPlayer.get(ballCarrier).getSacked();
					sack = true;
				}
			}
			
			statsOfPlayer.get(attacker).check(success, sack);
		} else if (theEvent.getType() == Event.EVENT_EJECT)
		{
			Player player = getPlayer(theEvent.flags[0]);
			Player attacker = getPlayer(theEvent.flags[1]);

			//stat variables
			boolean injury = false;
			boolean kill = false;
			
			// get the player off the field
			clearPlayerLocation(player);

			// set player status
			if (theEvent.flags[2] == Event.EJECT_REF)
			{
				player.status = Player.STS_OUT;
				statsOfPlayer.get(player).eject();
			} else if (theEvent.flags[2] == Event.EJECT_BLOB)
			{
				player.status = Player.STS_BLOB;
				statsOfPlayer.get(player).mutate();
			} else if (theEvent.flags[2] == Event.EJECT_TRIVIAL)
			{
				player.status = Player.STS_HURT;
				player.setInjuryType(Player.INJURY_TRIVIAL);
				injury = true;
			} else if (theEvent.flags[2] == Event.EJECT_SERIOUS)
			{
				player.status = Player.STS_HURT;
				player.setInjuryType(Player.INJURY_CRIPPLING);
				injury = true;

				if (theEvent.flags[4] == 0 && theEvent.flags[6] == 0)
					player.setInjuryType(Player.INJURY_MINOR);
			} else if (theEvent.flags[2] == Event.EJECT_DEATH)
			{
				player.status = Player.STS_DEAD;
				statsOfPlayer.get(player).getKilled();
				kill = true;
			}
			
			if (injury)		//getting killed is already handled in EJECT_DEATH
				statsOfPlayer.get(player).getInjured();
			
			Logger.debug("Data - eject event: " + theEvent);

			// damage the player's attributes if there was an injury
			player.applyInjury(theEvent.flags[3], theEvent.flags[4]);
			player.applyInjury(theEvent.flags[5], theEvent.flags[6]);
			player.setWeeksOut(theEvent.flags[7] + 1);		//add 1 because we're subtracting one for a week passed once the game is done
			
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
	
	private int halfAttribute(int attribute)
	{
		return ((attribute + 10) / 20) * 10;
	}
	
	private void processNewTurn(int newTurnPlayerIndex)
	{
		if (newTurnPlayerIndex == 0)
			turnsRemaining--;
		
		//clear the AP of everyone on the team whose turn is ending
		int startingIndex = currentTeam * TEAM_SIZE;
		for (int j = startingIndex; j < startingIndex + TEAM_SIZE; j++)
		{
			Player player = allPlayers.get(j);

			if (player == null)
				continue;
			
			player.currentAP = 0;
		}
		
		currentTeam = newTurnPlayerIndex;
		
		if (getTeamIndexOfPlayer(ballCarrier) == currentTeam)
		{
			teams.get(currentTeam).getLastGameStats().controlBall();
		}
	}

	private void processTeleport(Event theEvent)
	{
		Player player = getPlayer(theEvent.flags[0]);
		int tele1 = theEvent.flags[2];
		int tele2 = theEvent.flags[3];

		Point newLoc = getArena().getPortal(tele2);

		if (tele1 != -1)
		{
			// see if there's a player that we displaced
			Point oldLoc = getLocationOfPlayer(player);
			Player oldPlayer = getPlayerAtLocation(oldLoc);

			clearPlayerLocation(player);

			// If we just nulled a spot where a player should exist (by way of a forced teleport), put that player back.
			// Note that the pointOfPlayer map should be unaffected.
			if (player != oldPlayer)
			{
				setPlayerAtLocation(oldLoc, oldPlayer);
			}

			// XJS bots have gyro stablizers
			if (!player.hasSkill(Skill.GYRO_STABILIZER))
				player.currentAP = 0;
		} else
		{
			player.status = Player.STS_OKAY;
			player.currentAP = player.getAttributeWithModifiers(Player.ATT_AP);
			statsOfPlayer.get(player).enterGame();
		}

		if (tele1 != tele2)
		{
			updatePlayerLocation(player, newLoc);
		}

		// And this is all we need to do. It's really as simple as that: if another teleport forced this one, don't clear out the old space.
		// We're not concerned about future teleports, because they'll handle it the same way. It's the logic layer that builds the stack,
		// not the data layer.
	}

	//note that (as in the original game), this gives everyone the points, whether they were ejected or not
	private void setGameResultStats(int winningTeam)
	{
		for (int i = 0; i < 3; i++)
		{
			int startingIndex = i * TEAM_SIZE;
			
			for (int j = startingIndex; j < startingIndex + TEAM_SIZE; j++)
			{
				Player p = allPlayers.get(i);
				
				if (p == null)
					continue;
				
				//if it's not a tie, the player with the ball when the game ended must have scored the goal
				if (p == ballCarrier && winningTeam != TIE_GAME)
					statsOfPlayer.get(p).score();
				
				if (winningTeam == TIE_GAME)
					statsOfPlayer.get(p).teamTied();
				else if (i == winningTeam)
					statsOfPlayer.get(p).teamWon();
				else
					statsOfPlayer.get(p).teamLost();
			}
		}
	}

	private String serializeAllData()
	{
		return "";
	}

	private void createMap(int mapNum)
	{
		if (GameSettings.getPresentationMode() == PresentationMode.LEGACY)
			arena = LegacyArenaFactory.getInstance().generateArena(mapNum);
		else
			arena = SimpleArenaFactory.getInstance().generateArena(mapNum);
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
	
	@Override
	public boolean playersAreOpponents(Player player1, Player player2)
	{
		return getTeamIndexOfPlayer(player1) != getTeamIndexOfPlayer(player2);
	}
	
	@Override
	public Stats getStatsOfPlayer(Player p)
	{
		return statsOfPlayer.get(p);
	}

	@Override
	public Point getLocationOfPlayer(Player p)
	{
		return pointOfPlayer.get(p);
	}

	@Override
	public Player getPlayerAtLocation(Point p)
	{
		if (p == null)
			return null;
		return getPlayerAtLocation(p.x, p.y);
	}

	@Override
	public Player getPlayerAtLocation(int x, int y)
	{
		// out of bounds obviously has no players
		if (x < 0 || y < 0 || x > 29 || y > 29)
			return null;

		return playerLocs[x][y];
	}

	@Override
	public int getTeamIndexOfPlayer(Player p)
	{
		if (p == null)
			return -1;
		
		return teamOfPlayer.get(p);
	}

	@Override
	public Team getTeamOfPlayer(Player p)
	{
		return getTeam(getTeamIndexOfPlayer(p));
	}

	@Override
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

	@Override
	public int getNumberOfPlayer(Player player)
	{
		int playerIndex = getIndexOfPlayer(player) + 1;
		int teamIndex = getTeamIndexOfPlayer(player);
		
		return playerIndex - (9 * teamIndex);
	}

	private Point setLocationOfPlayer(Player p, Point pnt)
	{
		return pointOfPlayer.put(p, pnt);
	}

	private void setPlayerAtLocation(Point p, Player plyr)
	{
		if (p == null)
			return;

		playerLocs[p.x][p.y] = plyr;
	}

	private int setTeamOfPlayer(Player p, int team)
	{
		return teamOfPlayer.put(p, team);
	}

	@Override
	public int getCurrentTeam()
	{
		return currentTeam;
	}
	
	@Override
	public boolean isCurrentTeamHumanControlled()
	{
		Team team = teams.get(currentTeam);
		return team.humanControlled;
	}
	
	@Override
	public int getTurnsRemaining()
	{
		return turnsRemaining;
	}

	@Override
	public Arena getArena()
	{
		return arena;
	}

	@Override
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

	@Override
	public List<Player> getAllPlayers()
	{
		return allPlayers;
	}
	
	@Override
	public Team getTeam(int index)
	{
		try
		{
			return teams.get(index);
		} catch (IndexOutOfBoundsException e)
		{
			return null;
		}
	}

	@Override
	public Player getBallCarrier()
	{
		return ballCarrier;
	}

	@Override
	public Point getBallLocation()
	{
		return ball;
	}

	@Override
	public boolean isStateBallNotFound()
	{
		return arena.isBallFound();
	}

	@Override
	public boolean isStateBallLoose()
	{
		return !isStateBallNotFound() && (ballCarrier == null);
	}

	@Override
	public boolean isStateOwnTeamHasBall()
	{
		if (ballCarrier == null)
			return false;

		return (currentTeam == getTeamIndexOfPlayer(ballCarrier));
	}

	@Override
	public boolean isStateOpponentHasBall()
	{
		if (ballCarrier == null)
			return false;

		return (currentTeam != getTeamIndexOfPlayer(ballCarrier));
	}
	
	@Override
	public boolean isGameDone()
	{
		return gameWinner != GAME_IN_PROGRESS;
	}
	
	@Override
	public List<Team> getTeams()
	{
		return teams;
	}
	
	@Override
	public int getWinningTeamIndex()
	{
		return gameWinner;
	}
	
	@Override
	public int getTurnBallFound()
	{
		return 1;	//TODO: record this
	}
	
	@Override
	public int getTurnBallScored()
	{
		return 2;	//TODO: record this
	}

	@Override
	public boolean isGameActive()
	{
		return gameActive;
	}

	// TODO: DEBUG
	@Override
	public void printMap()
	{
		for (int i = 0; i < 30; i++)
		{
			for (int j = 0; j < 30; j++)
			{
				if (playerLocs[i][j] == null)
					Logger.output("0, ");
				else
					Logger.output(playerLocs[i][j].name.substring(0, 1) + ", ");
			}

			Logger.output("\n");
		}
	}
	
	@Override
	public String toString()
	{
		return "Data[" + name + "]";
	}
}
