package main.presentation.screens;

import java.util.ArrayList;
import java.util.List;

import main.data.entities.Arena;
import main.data.entities.Pace;
import main.data.entities.Team;
import main.data.factory.SimpleArenaFactory;

public class EventDetails
{
	private static Arena arena = null;
	private static int arenaIndex = Arena.ARENA_SAVANNA;
	private static Pace pace = Pace.RELAXED;
	private static int turns = 25; 
	
	private static List<Team> gameTeams = new ArrayList<Team>();
	
	public static void setTeams(List<Team> teams)
	{
		if (teams.size() != 3)
			throw new IllegalArgumentException("There must be exactly three teams in a Crush! event.");
		
		gameTeams.clear();
			gameTeams.addAll(teams);
			
		arenaIndex = teams.get(0).homeField;
	}
	
	public static void setArena(int newArenaIndex)
	{
		arena = SimpleArenaFactory.getInstance().generateArena(newArenaIndex);
		arenaIndex = newArenaIndex;
	}
	
	public static void setPace(Pace newPace)
	{
		pace = newPace;
	}
	
	public static void setTurns(int newTurns)
	{
		turns = newTurns;
	}
	
	public static int getArenaIndex()
	{
		return arenaIndex;
	}
	
	public static Arena getArena()
	{
		return arena;
	}
	
	public static Pace getPace()
	{
		return pace;
	}
	
	public static int getTurns()
	{
		return turns;
	}
	
	public static List<Team> getTeams()
	{
		return gameTeams;
	}
}
