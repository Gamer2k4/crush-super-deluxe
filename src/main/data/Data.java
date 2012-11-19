package main.data;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.data.entities.Field;
import main.data.entities.Player;
import main.data.entities.Stats;
import main.data.entities.Team;

public class Data
{
	protected List<Player> allPlayers;
	protected Player[][] playerLocs = new Player[30][30];
	
	protected Point ball;			//location of the ball; (-1, -1) if someone is carrying it
	protected Player ballCarrier;
	protected Field arena;
	
	protected int currentTeam;
	
	public Data clone()
	{
		//clone the easy to clone data
		Data toRet = new Data();
		toRet.ball = new Point(ball.x, ball.y);
		toRet.arena = arena.clone();
		toRet.currentTeam = currentTeam;
		
		//go through each player, clone it, and record its location
		for (int i = 0; i < 27; i++)
		{
			Player p = allPlayers.get(i);
			
			//no player on the team
			if (p == null)
			{
				toRet.allPlayers.add(null);
				continue;
			}
			
			Player newPlayer = p.clone();
			Point oldLoc = pointOfPlayer.get(p);
			
			//the player is in play (as opposed to on deck, blobbed, whatever)
			if (oldLoc != null)
			{
				Point newLoc = new Point(oldLoc.x, oldLoc.y);
				toRet.updatePlayerLocation(newPlayer, newLoc);
			}
			
			//record the team of the player based on its placement in the list
			toRet.teamOfPlayer.put(newPlayer, (int)(i / 9));
			
			//make this player the ball carrier if necessary
			if (p == ballCarrier)
				toRet.ballCarrier = newPlayer;
			
			//finally, add it to the list
			toRet.allPlayers.add(newPlayer);
		}
		
		return toRet;
	}
	
	public Data()
	{
		allPlayers = new ArrayList<Player>();
		
		pointOfPlayer = new HashMap<Player, Point>();
		teamOfPlayer = new HashMap<Player, Integer>();
		
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
	
	public void newGame(List<Team> allThreeTeams, int fieldNum)
	{	
		for (int i = 0; i < 3; i++)
		{
			Team curTeam = allThreeTeams.get(i); 
			
			if (curTeam == null) continue;
			
			for (int j = 0; j < 9; j++)		//no matter how many players are in the team, only take the first 9
			{
				Player p = curTeam.getPlayer(j);
				
				if (p != null)
				{
					Player pClone = p.clone();
					
					teamOfPlayer.put(pClone, i);
					allPlayers.add(pClone);
				}
				else
				{
					allPlayers.add(null);	//when accessing the players, it's important that the correct team's players are in the correct spot
				}
			}
		}
		
		currentTeam = 0;
		
		try
		{
			createMap(fieldNum);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		//note that none of this is random, so the same parameters should create the same game
	}
	
	public void processEvent(Event theEvent)
	{
		//dumbly execute events as they come in; the Engine should've sanitized them so nothing illegal can happen
		//only do one at a time, since by this point everything is resolved and distinct
		
		if (theEvent.getType() == Event.EVENT_TURN)
		{
			currentTeam = theEvent.flags[0];
		}
		else if (theEvent.getType() == Event.EVENT_RECVR)
		{	
			Player p = getPlayer(theEvent.flags[0]);
			
			if (p != null)
			{
				//System.out.println(p.status);
				
				if (p.status == Player.STS_OKAY)
				{
					p.currentAP = p.getAttributeWithModifiers(Player.ATT_AP);
				}
				else if (p.status == Player.STS_STUN)
				{
					p.status = Player.STS_DOWN;
				}
				else if (p.status == Player.STS_DOWN)
				{
					p.status = Player.STS_OKAY;
					p.currentAP = p.getAttributeWithModifiers(Player.ATT_AP) / 2;
				}
			}
		}
		else if (theEvent.getType() == Event.EVENT_MOVE)
		{
			//again, this is sanitized, so each move event should be from one legal tile to one legal adjacent tile
			//in other words, we expect these to each take 10 AP, and we expect the player to have at least 10 AP to begin with
			
			Player p = getPlayer(theEvent.flags[0]);
			Point destination = new Point(theEvent.flags[2], theEvent.flags[3]);
			
			p.currentAP -= 10;
			updatePlayerLocation(p, destination);
		}
		else if (theEvent.getType() == Event.EVENT_TELE)
		{
			Player p = getPlayer(theEvent.flags[0]);
			int tele1 = theEvent.flags[2];
			int tele2 = theEvent.flags[3];
			
			Point newLoc = getArena().getPortal(tele2);

			if (tele1 != -1)
			{
				//see if there's a player that we displaced
				Point oldLoc = getLocationOfPlayer(p);
				Player oldPlayer = getPlayerAtLocation(oldLoc);
				
				clearPlayerLocation(p);
				
				//If we just nulled a spot where a player should exist (by way of a forced teleport), put that player back.
				//Note that the pointOfPlayer map should be unaffected.
				if (p != oldPlayer)
				{
					setPlayerAtLocation(oldLoc, oldPlayer);
				}
				
				//XJS bots have gyro stablizers
				if (p.race != Player.RACE_XJS9000)
					p.currentAP = 0;
			}
			else
			{
				p.status = Player.STS_OKAY;
			}
			
			if (tele1 != tele2)
			{
				updatePlayerLocation(p, newLoc);
			}
			
			//And this is all we need to do.  It's really as simple as that: if another teleport forced this one, don't clear out the old space.
			//We're not concerned about future teleports, because they'll handle it the same way.  It's the logic layer that builds the stack,
			//not the data layer.
		}
		else if (theEvent.getType() == Event.EVENT_BIN)
		{
			System.out.println("BIN EVENT");
			
			Player p = getPlayer(theEvent.flags[0]);
			int binIndex = theEvent.flags[2];
			int result = theEvent.flags[3];
			
			p.currentAP = 0;
			
			arena.setBinStatus(binIndex, result + 1);
			
			if (result == 1)	//success, so make all of them failed
			{
				arena.ballFound(binIndex);
				ballCarrier = p;
			}
			else
			{
				p.status = Player.STS_DOWN;		//for now, just knock the player down
			}
		}
		else if (theEvent.getType() == Event.EVENT_BALLMOVE)
		{
			System.out.println("DATA MOVING BALL");
			ball.x = theEvent.flags[2];
			ball.y = theEvent.flags[3];
			ballCarrier = null;
		}
		else if (theEvent.getType() == Event.EVENT_GETBALL)
		{
			Player p = getPlayer(theEvent.flags[0]);
			
			//check if player missed the ball
			if (theEvent.flags[2] == 0)
			{
				p.currentAP = 0;
			}
			else
			{
				ball.x = -1;
				ball.y = -1;
				
				ballCarrier = p;
			}
		}
		else if (theEvent.getType() == Event.EVENT_HANDOFF)
		{
			Player p = getPlayer(theEvent.flags[0]);
			
			//handoffs cost 10AP, hurling costs 20
			if (theEvent.flags[2] == Event.HANDOFF_HURL)
				p.currentAP -= 20;
			else
				p.currentAP -= 10;
			
			ballCarrier = getPlayer(theEvent.flags[1]);		//we're going to get a BALLMOVE event after this if it was a hurl, so this doesn't hurt
		}
		else if (theEvent.getType() == Event.EVENT_CHECK)
		{
			Player attacker = getPlayer(theEvent.flags[0]);
			Player defender = getPlayer(theEvent.flags[1]);
			int result = theEvent.flags[2];
			
			//deduct the proper amount of AP
			if (attacker.hasSkill[Player.SKILL_CHARGE])
				attacker.currentAP -= 10;
			else
				attacker.currentAP -= 20;
			
			if (result == Event.CHECK_CRITFAIL)
			{
				attacker.currentAP = 0;
				attacker.status = Player.STS_DOWN;
			}
			else if (result == Event.CHECK_FAIL || result == Event.CHECK_DODGE)
			{
				//nothing happens
			}
			else if (result == Event.CHECK_DOUBLEFALL)
			{
				attacker.currentAP = 0;
				attacker.status = Player.STS_DOWN;
				defender.currentAP = 0;
				defender.status = Player.STS_DOWN;
			}
			else if (result == Event.CHECK_PUSH)
			{
				pushPlayer(attacker, defender);
			}
			else if (result == Event.CHECK_FALL)
			{
				defender.currentAP = 0;
				defender.status = Player.STS_DOWN;
			}
			else if (result == Event.CHECK_PUSHFALL)
			{
				pushPlayer(attacker, defender);
				defender.currentAP = 0;
				defender.status = Player.STS_DOWN;
			}
		}
		else if (theEvent.getType() == Event.EVENT_EJECT)
		{
			Player p = getPlayer(theEvent.flags[0]);
			
			//adjust stats later, but this is sufficient for now
			if (theEvent.flags[2] == Event.EJECT_REF)
			{
				p.status = Player.STS_OUT;
			}
			else if (theEvent.flags[2] == Event.EJECT_BLOB)
			{
				p.status = Player.STS_BLOB;
			}
			else if (theEvent.flags[2] == Event.EJECT_TRIVIAL)
			{
				p.status = Player.STS_HURT;
			}
			else if (theEvent.flags[2] == Event.EJECT_SERIOUS)
			{
				p.status = Player.STS_HURT;
			}
			else if (theEvent.flags[2] == Event.EJECT_DEATH)
			{
				p.status = Player.STS_DEAD;
			}
		}
	}
	
	private String serializeAllData()
	{
		return "";
	}
	
	private void createMap(int mapNum) throws Exception
	{
		List<String> mapStrings = new ArrayList<String>();
		
		mapStrings.add("000000000000000000000000000000022221111005111111500111122220022221111004111111400111122220022221111111111111111111122220022221111111111111111111122220011111111111111111111111111110011111111111111111111111111110011111111001111111100111111110011111111003111111300111111110000111100000011110000001111000000111100000011110000001111000054111113001111111100311111450011111111001111111100111111110011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110011111111001111111100111111110054111113001111111100311111450000111100000011110000001111000000111100000011110000001111000011111111003111111300111111110011111111001111111100111111110011111111111111111111111111110011111111111111111111111111110022221111111111111111111122220022221111111111111111111122220022221111004111111400111122220022221111005111111500111122220000000000000000000000000000000");
		
		if (mapNum < 0)
		{
			arena = new Field();
		}
		else
		{
			arena = new Field(mapStrings.get(mapNum));
		}
	}
	
	protected Map<Player, Point> pointOfPlayer;
	protected Map<Player, Integer> teamOfPlayer;
	
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
	
	private void pushPlayer(Player pusher, Player pushee)
	{
		//TODO: FILL THIS OUT
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
		//out of bounds obviously has no players
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
		if (p == null) return;
		
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
	
	public Field getArena()
	{
		return arena;
	}
	
	public Player getPlayer(int index)
	{
		try
		{
			return allPlayers.get(index);
		}
		catch (IndexOutOfBoundsException e)
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
	
	//DEBUG
	public void printMap()
	{
		for (int i = 0; i < 30; i++)
		{
			for (int j = 0; j < 30; j++)
			{
				if (playerLocs[i][j] == null)
					System.out.print("0, ");
				else 
					System.out.print(playerLocs[i][j].name.substring(0,1) + ", ");
			}
			
			System.out.println();
		}
	}
}
