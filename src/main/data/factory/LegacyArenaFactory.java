package main.data.factory;

import java.util.ArrayList;
import java.util.List;

import main.data.entities.Arena;
import main.data.load.LegacyMapLoader;
import main.presentation.common.Logger;

public class LegacyArenaFactory extends ArenaFactory
{
	private static List<Arena> arenas = new ArrayList<Arena>();
	private static LegacyArenaFactory instance = null;

	protected LegacyArenaFactory()
	{
		if (arenas.size() == 0)
			loadArenas();
	}

	public static LegacyArenaFactory getInstance()
	{
		if (instance == null)
			instance = new LegacyArenaFactory();

		return instance;
	}

	@Override
	public Arena generateArena(int arenaNumber)
	{
		try {
			return arenas.get(arenaNumber).clone();
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new IndexOutOfBoundsException("Invalid arena number specified: " + arenaNumber);
		}
	}
	
	private void loadArenas()
	{
		Logger.output("Loading map data...\n");
		
		arenas = LegacyMapLoader.loadLegacyMaps();
		
		Logger.output("Done!\n");
	}
}
