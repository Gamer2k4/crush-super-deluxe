package main.presentation;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import main.data.entities.Race;
import main.data.entities.Team;

public class TeamColorsManager
{
	private Map<ColorPairKey, TeamImages> teamImages;
	
	private static TeamColorsManager instance = null;
	
	private TeamColorsManager()
	{
		teamImages = new HashMap<ColorPairKey, TeamImages>();
	}
	
	public static TeamColorsManager getInstance()
	{
		if (instance == null)
			instance = new TeamColorsManager();
		
		return instance;
	}
	
	
	private TeamImages getTeamImages(Team team)
	{
		return getTeamImages(team.teamColors[0], team.teamColors[1]);
	}
	
	private TeamImages getTeamImages(Color fg, Color bg)
	{
		return getTeamImages(new ColorPairKey(fg, bg));
	}
	
	private TeamImages getTeamImages(ColorPairKey colorPair)
	{
		if (!teamImages.containsKey(colorPair))
		{
			TeamImages teamImageSource = new TeamImages(colorPair.getForeground(), colorPair.getBackground());
			teamImages.put(colorPair, teamImageSource);
		}
		
		return teamImages.get(colorPair);
	}
	
	
	public Texture getPlayerImage(Race race, Color mainColor, Color trimColor)
	{
		TeamImages teamImageSource = getTeamImages(mainColor, trimColor);
		return teamImageSource.getPlayerImage(race);
	}
	
	public Texture getPlayerImage(Team team, Race race)
	{
		TeamImages teamImageSource = getTeamImages(team);
		return teamImageSource.getPlayerImage(race);
	}
	
	public Drawable getEquipmentImage(Team team, int equipment)
	{
		TeamImages teamImageSource = getTeamImages(team);
		return teamImageSource.getEquipmentImage(equipment);
	}
	
	public Drawable getSmallTeamBanner(Team team)
	{
		TeamImages teamImageSource = getTeamImages(team);
		return teamImageSource.getSmallTeamBanner();
	}
	
	public Drawable getLargeTeamBanner(Team team)
	{
		TeamImages teamImageSource = getTeamImages(team);
		return teamImageSource.getLargeTeamBanner();
	}
	
	public Texture getHelmetImage(Team team)
	{
		TeamImages teamImageSource = getTeamImages(team);
		return teamImageSource.getHelmetImage();
	}
	
	public Texture getSpriteSheet(ColorPairKey colorPair, Race race)
	{
		TeamImages teamImageSource = getTeamImages(colorPair);
		return teamImageSource.getSpriteSheet(race);
	}
	
	public Texture getSpriteSheet(Team team, Race race)
	{
		TeamImages teamImageSource = getTeamImages(team);
		return teamImageSource.getSpriteSheet(race);
	}
	
	public void dispose()
	{
		for (ColorPairKey key : teamImages.keySet())
		{
			TeamImages teamImage = teamImages.get(key);
			teamImage.dispose();
		}
		
		teamImages.clear();
	}
}
