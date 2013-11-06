package main.data.save;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.data.entities.Player;
import main.data.entities.Stats;
import main.data.entities.Team;

public class EntityMap
{
	private static Map<String, Team> knownTeams = new HashMap<String, Team>();
	private static Map<String, Player> knownPlayers = new HashMap<String, Player>();
	private static Map<String, Stats> knownStats = new HashMap<String, Stats>();
	
	private static Map<String, String> entityComplexIdFinder = new HashMap<String, String>();
	private static Map<String, String> entitySimpleIdFinder = new HashMap<String, String>();
	
	public static String put(String key, Object value)
	{
		//System.out.println("EntityMap - Putting object with key " + key);

		if (value == null)
		{
			System.out.println("EntityMap - Warning! Object is null!");
		}
		
		String entityType = key.toUpperCase().substring(0, 1);
		String simpleKey = "";
		
		if (entityType.equals("T"))
		{
			knownTeams.put(key, (Team)value);
			simpleKey = entityType + String.valueOf(knownTeams.size());
		}
		if (entityType.equals("P"))
		{
			knownPlayers.put(key, (Player)value);
			simpleKey = entityType + String.valueOf(knownPlayers.size());
		}
		if (entityType.equals("S"))
		{
			knownStats.put(key, (Stats)value);
			simpleKey = entityType + String.valueOf(knownStats.size());
		}
			
		entityComplexIdFinder.put(simpleKey, key);
		entitySimpleIdFinder.put(key, simpleKey);
			
		return simpleKey;
	}
	
	public static Team getTeam(String key)
	{
		return knownTeams.get(key);
	}
	
	public static Player getPlayer(String key)
	{
		return knownPlayers.get(key);
	}
	
	public static Stats getStats(String key)
	{
		return knownStats.get(key);
	}
	
	public static List<String> getTeamKeys()
	{
		List<String> toRet = new ArrayList<String>();
		toRet.addAll(knownTeams.keySet());
		return toRet;
	}
	
	public static List<String> getPlayerKeys()
	{
		List<String> toRet = new ArrayList<String>();
		toRet.addAll(knownPlayers.keySet());
		return toRet;
	}
	
	public static List<String> getStatKeys()
	{
		List<String> toRet = new ArrayList<String>();
		toRet.addAll(knownStats.keySet());
		return toRet;
	}
	
	public static String getSimpleKey(String complexKey)
	{
		return entitySimpleIdFinder.get(complexKey);
	}
	
	public static String getComplexKey(String simpleKey)
	{
		return entityComplexIdFinder.get(simpleKey);
	}
	
	public static void clearMappings()
	{
		knownTeams.clear();
		knownPlayers.clear();
		knownStats.clear();
		
		entitySimpleIdFinder.clear();
		entityComplexIdFinder.clear();
	}
}
