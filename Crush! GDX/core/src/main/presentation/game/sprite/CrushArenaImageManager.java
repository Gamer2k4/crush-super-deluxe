package main.presentation.game.sprite;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.data.entities.Arena;
import main.data.entities.Team;
import main.presentation.ColorPairKey;
import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.common.Logger;
import main.presentation.common.image.ColorReplacer;

public class CrushArenaImageManager
{
	private static final int MAX_CACHED_ARENAS = 24;	//enough for a full league plus a tournament plus an exhibition (12 + 9 + 3)

	private static Map<ColorPairKey, Map<Integer, Texture>> teamArenas = new HashMap<ColorPairKey, Map<Integer, Texture>>();
	
	private static ImageFactory imageFactory = ImageFactory.getInstance();
	
	private static CrushArenaImageManager instance = null;
	
	private CrushArenaImageManager() {}
	
	public static CrushArenaImageManager getInstance()
	{
		if (instance == null)
			instance = new CrushArenaImageManager();
		
		return instance;
	}

	public CrushArena createArena(Arena arena)
	{
		if (arena == null)
			return null;
		
		return createArena(arena.getIndex());
	}
	
	public CrushArena getArenaForHomeTeam(int index, Team team)
	{
		if (index == Arena.ARENA_JACKALS_LAIR || index == Arena.ARENA_CRISSICK || index == Arena.ARENA_ABYSS || index == Arena.ARENA_SAVANNA
				|| index == Arena.ARENA_VAULT || index == Arena.ARENA_DARKSUN || index == Arena.ARENA_BADLANDS || index == Arena.ARENA_LIGHTWAY)
			return createArena(index);
		
		return getTeamArena(index, team);
	}

	private CrushArena getTeamArena(int index, Team team)
	{
		ColorPairKey key = new ColorPairKey(team);
		
		if (!teamArenas.containsKey(key))
			teamArenas.put(key, new HashMap<Integer, Texture>());
		
		Map<Integer, Texture> arenasForTeam = teamArenas.get(key);
		Integer arenaIndex = new Integer(index);
		
		if (!arenasForTeam.containsKey(arenaIndex))
		{
			if (teamArenas.size() > MAX_CACHED_ARENAS)
				clearTeamArenaCache(key);
			
			arenasForTeam.put(arenaIndex, updateArenaImage(index, team));
		}
		
		Texture arenaTexture = arenasForTeam.get(arenaIndex);
		
		if (arenaTexture == null)
			return null;
		
		TextureRegion region = new TextureRegion(arenaTexture);
		return new CrushArena(region);
	}

	private void clearTeamArenaCache(ColorPairKey key)
	{
		Logger.error("Team arena cache has more than the maximum entries of " + MAX_CACHED_ARENAS +"; clearing team arena cache.");
		dispose();
		teamArenas.put(key, new HashMap<Integer, Texture>());
	}

	private Texture updateArenaImage(int index, Team team)
	{
		Texture arenaTexture = getArenaTexture(index);
		
		//otherwise will throw a null pointer exception when the game is closed
		if (arenaTexture == null)
			return null;
		
		return ColorReplacer.getInstance().setColors(arenaTexture, team.teamColors[0], team.teamColors[1], new Color(0, 0, 0, 0));
	}

	public CrushArena createArena(int index)
	{
		Texture arenaTexture = getArenaTexture(index);
		
		//otherwise will throw a null pointer exception when the game is closed
		if (arenaTexture == null)
			return null;
		
		TextureRegion region = new TextureRegion(arenaTexture);
		return new CrushArena(region);
	}
		
	public Texture getArenaTexture(int index)
	{
		Texture arenaTexture;
		
		switch (index)
		{
		case 0:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_A1);
			break;
		case 1:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_A2);
			break;
		case 2:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_A3);
			break;
		case 3:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_A4);
			break;
		case 4:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_B1);
			break;
		case 5:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_B2);
			break;
		case 6:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_B3);
			break;
		case 7:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_B4);
			break;
		case 8:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_C1);
			break;
		case 9:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_C2);
			break;
		case 10:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_C3);
			break;
		case 11:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_C4);
			break;
		case 12:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_D1);
			break;
		case 13:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_D2);
			break;
		case 14:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_D3);
			break;
		case 15:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_D4);
			break;
		case 16:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_E1);
			break;
		case 17:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_E2);
			break;
		case 18:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_E3);
			break;
		case 19:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_E4);
			break;
		default:
			throw new IllegalArgumentException("No map image defined for arena " + index);
		}
		
		return arenaTexture;
	}
	
	public void dispose()
	{
		for (ColorPairKey key : teamArenas.keySet())
		{
			Map<Integer, Texture> coloredArenas = teamArenas.get(key);
			
			for (Integer arenaIndex : coloredArenas.keySet())
			{
				Texture textureToDispose = coloredArenas.get(arenaIndex);
				
				if (textureToDispose != null)
					textureToDispose.dispose();
			}
			
			coloredArenas.clear();
		}
		
		teamArenas.clear();
	}
}
