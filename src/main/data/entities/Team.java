package main.data.entities;

import java.util.ArrayList;
import java.util.List;

public class Team
{
	public Team(String serialString)
	{
		//
	}
	
	public Team()
	{
		players = new ArrayList<Player>();
		unassignedGear = new ArrayList<Equipment>();
		teamName = "Test Team";
		coachName = "Jay Beezie";
		money = 900;
		
		for (int i = 0; i < 4; i++)
		{
			docbot[i] = false;
		}
	}
	
	private List<Player> players;
	public String teamName;
	public String coachName;
	public int money;
	public boolean[] docbot = new boolean[4];
	public List<Equipment> unassignedGear;
	
	//return sum of money and player value
	public int getValue()
	{
		return 0;
	}
	
	public void addPlayer(Player p)
	{
		players.add(p);
	}
	
	public Player getPlayer(int index)
	{
		Player p = null;
		
		try
		{
			p = players.get(index);
		}
		catch (IndexOutOfBoundsException e)
		{
			//
		}
		
		return p;
	}
	
	public String serialize()
	{
		return coachName;
	}
}
