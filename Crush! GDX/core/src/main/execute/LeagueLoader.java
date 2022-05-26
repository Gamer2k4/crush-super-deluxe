package main.execute;

import java.util.List;

import main.data.entities.Team;
import main.data.load.LegacyLeagueLoader;
import main.presentation.common.GameSettings;

public class LeagueLoader
{
	public static void main(String[] args)
	{
		String filename = "league2";
		String path = GameSettings.getRootDirectory() + "\\League\\" + filename.toLowerCase() + ".tms";
		
		System.out.println("Loading league [" + filename + "]");
		List<Team> leagueTeams = LegacyLeagueLoader.loadLeague(path);
		System.out.println("League loaded!");
	}
}
