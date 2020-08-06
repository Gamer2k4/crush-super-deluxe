package main.data;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import main.data.entities.Player;
import main.data.entities.Stats;
import main.data.entities.Team;
import main.data.factory.PlayerFactory;
import main.data.load.LegacyTeamLoader;
import main.data.save.EntityMap;
import main.data.save.EntitySerializer;
import main.data.save.SaveHandler;

public class TeamLoader
{
	private static final String DEFAULT_EXTENSION = "CSDT";
	private static final String LEGACY_EXTENSION = "TME";

	private TeamLoader()
	{
		// private to prevent instantiation
	}

	// TODO: extract this code to private method so that saving can use it too (possibly return a list of strings)
	public static Team loadTeamFromFile(File path)
	{
		String fileName = path.getName();
		String fileExtension = "";

		int index = fileName.indexOf(".");

		if (index > -1)
		{
			fileExtension = fileName.substring(index + 1);
			fileName = fileName.substring(0, index);
		}

		if (fileExtension.equalsIgnoreCase(DEFAULT_EXTENSION))
			return loadTeamFromDefaultFile(path, fileName, fileExtension);
		else if (fileExtension.equalsIgnoreCase(LEGACY_EXTENSION))
			return loadTeamFromLegacyFile(path.getParent() + "\\" + fileName + "." + fileExtension);

		throw new IllegalArgumentException("Unable to read file path: " + path.getAbsolutePath());
	}
	
	public static void saveTeamToFile(Team team, File path)
	{
		String fileName = path.getName();
		String fileExtension = "";

		int index = fileName.indexOf(".");

		if (index > -1)
		{
			fileExtension = fileName.substring(index + 1);
			fileName = fileName.substring(0, index);
		}

		if (fileExtension.equalsIgnoreCase(DEFAULT_EXTENSION))
		{
			saveTeamToDefaultFile(team, path);
			return;
		}
		else if (fileExtension.equalsIgnoreCase(LEGACY_EXTENSION))
		{
			saveTeamToLegacyFile(team, path);
			return;
		}

		throw new IllegalArgumentException("Unable to read file path: " + path.getAbsolutePath());
	}

	private static Team loadTeamFromLegacyFile(String fullPath)
	{
		return LegacyTeamLoader.loadTeam(fullPath);
	}

	private static Team loadTeamFromDefaultFile(File path, String fileName, String fileExtension)
	{
		SaveHandler saveHandler = new SaveHandler(path.getParent() + "\\", fileName, fileExtension);

		try
		{
			saveHandler.unzipSaveDir();
		} catch (IOException e)
		{
			System.out.println("TeamLoader - Error occured while uncompressing save directory!");
			return null;
		}

		EntityMap.clearMappings();
		List<String> entityLines;

		// load stats
		entityLines = saveHandler.loadStats();
		for (String saveString : entityLines)
		{
			loadAndMapStatsFromSaveString(saveString);
		}

		// load players
		entityLines = saveHandler.loadPlayer();
		for (String saveString : entityLines)
		{
			loadAndMapPlayerFromSaveString(saveString);
		}

		// load team
		entityLines = saveHandler.loadTeam();
		String saveString = entityLines.get(0); // there should only be one team saved

		Team loadedTeam = loadAndMapTeamFromSaveString(saveString);

		saveHandler.deleteSaveDir();
		EntityMap.clearMappings();

		return loadedTeam;
	}

	public static boolean saveTeamToDefaultFile(Team team, File path)
	{
		SaveHandler saveHandler = createDefaultHandlerFromPath(path);

		saveHandler.createSaveDir();
		List<String> keys;

		EntityMap.clearMappings();

		// save team
		saveHandler.saveTeam(team);

		// save players
		keys = EntityMap.getPlayerKeys();

		for (String key : keys)
		{
			Player player = EntityMap.getPlayer(key);
			saveHandler.savePlayer(player);
		}

		// save stats
		keys = EntityMap.getStatKeys();

		for (String key : keys)
		{
			Stats stats = EntityMap.getStats(key);
			saveHandler.saveStats(stats);
		}

		// zip the individual files into a single save file
		try
		{
			saveHandler.zipSaveDir();
		} catch (IOException e)
		{
			System.out.println("TeamLoader - Error occured while compressing save directory!");
			return false;
		}

		saveHandler.deleteSaveDir();
		EntityMap.clearMappings();

		return true;
	}
	
	private static void saveTeamToLegacyFile(Team team, File path)
	{
		try
		{
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(path));
			EntitySerializer.serializeTeamToStream(dos, team);
			dos.close();
		} catch (FileNotFoundException e)
		{
			System.out.println("TeamLoader - FileNotFoundException while saving to " + path.getAbsolutePath());
		} catch (IOException e)
		{
			System.out.println("TeamLoader - IOException while saving to " + path.getAbsolutePath() + "; message was " + e.getMessage());
		}
	}

	private static SaveHandler createDefaultHandlerFromPath(File path)
	{
		String fileName = path.getName();
		String fileExtension = "";
		int fileType = -1;

		int index = fileName.indexOf(".");

		if (index > -1)
		{
			fileExtension = fileName.substring(index + 1);
			fileName = fileName.substring(0, index);
		}
		return new SaveHandler(path.getParent() + "\\", fileName, DEFAULT_EXTENSION);
	}

	public static Player loadAndMapPlayerFromSaveString(String saveString)
	{
		Player player = null;

		try
		{
			player = PlayerFactory.createEmptyPlayer();
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

	public static Stats loadAndMapStatsFromSaveString(String saveString)
	{
		Stats stats = null;

		try
		{
			stats = new Stats();
			String key = stats.loadFromText(saveString);
			EntityMap.put(key, stats);
		} catch (ParseException e)
		{
			System.out.println("TeamLoader - Exception while loading stats: " + e.getMessage());
		}

		return stats;
	}
}
