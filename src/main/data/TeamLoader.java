package main.data;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import main.data.entities.Player;
import main.data.entities.Team;
import main.data.save.EntityMap;
import main.data.save.SaveHandler;

public class TeamLoader
{
	private TeamLoader(){};
	
	public static Team loadTeamFromFile(File path)
	{
		SaveHandler saveHandler = createHandlerFromPath(path);
		
		try
		{
			saveHandler.unzipSaveDir();
		} catch (IOException e)
		{
			System.out.println("Data - Error occured while uncompressing save directory!");
			return null;
		}
		
		EntityMap.clearMappings();
		List<String> entityLines;
		
		//load players
		entityLines = saveHandler.loadPlayer();
		for (String saveString : entityLines)
		{
			loadAndMapPlayerFromSaveString(saveString);
		}
		
		//load team
		entityLines = saveHandler.loadTeam();
		String saveString = entityLines.get(0);		//there should only be one team saved
		
		Team loadedTeam = loadAndMapTeamFromSaveString(saveString);
		
		saveHandler.deleteSaveDir();
		EntityMap.clearMappings();
		
		return loadedTeam; 
	}
	
	public static boolean saveTeamToFile(Team team, File path)
	{
		SaveHandler saveHandler = createHandlerFromPath(path);
		
		saveHandler.createSaveDir();
		List<String> keys;
		
		EntityMap.clearMappings();
		
		//save team
		saveHandler.saveTeam(team);
		
		//save players
		keys = EntityMap.getPlayerKeys();
		
		for (String key : keys)
		{
			Player player = EntityMap.getPlayer(key);
			saveHandler.savePlayer(player);
		}
		
		//zip the individual files into a single save file
		try
		{
			saveHandler.zipSaveDir();
		} catch (IOException e)
		{
			System.out.println("Data - Error occured while compressing save directory!");
			return false;
		}
		
		saveHandler.deleteSaveDir();
		EntityMap.clearMappings();
		
		return true;
	}
	
	private static SaveHandler createHandlerFromPath(File path)
	{
		String fileName = path.getName();
		
		int index = fileName.indexOf(".");
		
		if (index > -1)
			fileName = fileName.substring(0, index);
		
		return new SaveHandler(path.getParent() + "\\", fileName, SaveHandler.TYPE_DEFAULT_TEAM);
	}
	
	public static Player loadAndMapPlayerFromSaveString(String saveString)
	{
		Player player = null;
		
		try
		{
			player = Player.createEmptyPlayer();
			String key = player.loadFromText(saveString);
			
			EntityMap.put(key, player);
		} catch (ParseException e)
		{
			System.out.println("TeamLoader - Exception while loading player: " + e.getMessage());
		}
		
		return player;
	}
	
	public static Team loadAndMapTeamFromSaveString(String saveString)
	{
		Team team = null;
		
		try
		{
			team = new Team();
			String key = team.loadFromText(saveString);
			EntityMap.put(key, team);
		} catch (ParseException e)
		{
			System.out.println("TeamLoader - Exception while loading team: " + e.getMessage());
		}
		
		return team;
	}
}
