package main.data.factory;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import main.data.entities.Team;
import main.data.load.NameLoader;
import main.logic.Randomizer;

public class CpuTeamFactory extends RandomlyNamedEntityFactory
{
	private List<String> TEAM_NAMES = new ArrayList<String>();
	private List<String> COACH_NAMES = new ArrayList<String>();
	
	private List<String> assignableTeamNames = new ArrayList<String>();
	private List<String> assignableCoachNames = new ArrayList<String>();
	
	private static CpuTeamFactory instance = null;
	
	private CpuTeamFactory()
	{
		List<String> rawNames = NameLoader.loadNames("team");
		
		for (String rawName : rawNames)
		{
			TEAM_NAMES.add(normalizeName(rawName));
		}
		
		rawNames = NameLoader.loadNames("coach");
		
		for (String rawName : rawNames)
		{
			COACH_NAMES.add(normalizeName(rawName));
		}
	}
	
	public static CpuTeamFactory getInstance()
	{
		if (instance == null)
			instance = new CpuTeamFactory();
		
		return instance;
	}
	
	public Team generateEmptyCpuTeam(int budget)
	{
		return updateTeamWithRandomElements(new Team());
	}
	
	public Team generatePopulatedCpuTeam(int budget)
	{
		return updateTeamWithRandomElements(generateNewTeamWithPlayers());
	}
		
	public Team updateTeamWithRandomElements(Team cpuTeam)
	{
		//TODO: this is certainly incomplete
		cpuTeam.teamName = generateRandomTeamName();
		cpuTeam.coachName = generateRandomCoachName();
		cpuTeam.teamColors = generateRandomTeamColors();
		cpuTeam.homeField = Randomizer.getRandomInt(0, 19);
		
		return cpuTeam;
	}
	
	private Team generateNewTeamWithPlayers()
	{
		Team team = new Team();
		
		for (int i = 0; i < 9; i++)
			team.addPlayer(PlayerFactory.getInstance().createRandomPlayer());
		
		return team;
	}
	
	private Color[] generateRandomTeamColors()
	{
		Color[] teamColors = new Color[2];
		
		teamColors[0] = new Color(Randomizer.getRandomInt(0, 255), Randomizer.getRandomInt(0, 255), Randomizer.getRandomInt(0, 255));
		teamColors[1] = new Color(Randomizer.getRandomInt(0, 255), Randomizer.getRandomInt(0, 255), Randomizer.getRandomInt(0, 255));
		
//		teamColors[0] = TeamColorType.GOLD.getColor();		//TODO: for debugging only
//		teamColors[1] = TeamColorType.RED.getColor();		//TODO: for debugging only
		
		return teamColors;
	}

	private String generateRandomTeamName()
	{
		return safeGetRandomName(TEAM_NAMES, assignableTeamNames);
	}

	private String generateRandomCoachName()
	{
		return safeGetRandomName(COACH_NAMES, assignableCoachNames);
	}
	
	/*
	private List<String> generateRandomTeamNames()
	{
		List<String> names = new ArrayList<String>();

		names.add("Maulers");
		names.add("Friends_of_8");
		names.add("Symphonics");
		names.add("Rat_Pack");
		names.add("spyre_jumpers");
		names.add("Happy_Troop");
		names.add("Freak_Show");
		names.add("Centrifuge");
		names.add("Typhoons");
		names.add("Tornados");
		names.add("Volcanos");
		names.add("Earthquakes");
		names.add("Lightnings");
		names.add("The_Thunder");
		names.add("Hail_Storm");
		names.add("Marshmallows");
		names.add("Hybrids");
		names.add("Bad_Guys");
		names.add("Good_Guys");
		names.add("Chaos_Lords");
		names.add("Zygoats");
		names.add("Rippers");
		names.add("Daemons");
		names.add("Devils");
		names.add("Angels");
		names.add("Judicators");
		names.add("Paladins");
		names.add("Vipers");
		names.add("Fruit_Cakes");
		names.add("Mind_Blasters");
		names.add("Old_Men");
		names.add("Heroes");
		names.add("Titans");
		names.add("Stone_Jackals");
		names.add("Knights");
		names.add("Seraphims");
		names.add("Spiral_Doom");
		names.add("Doom_Bringers");
		names.add("Nightmares");
		names.add("Jelly_Rolls");
		names.add("Ninjas");
		names.add("Shoguns");
		names.add("The_Nukes");
		names.add("Furies");
		names.add("Olympians");
		names.add("Mashers");
		names.add("Gut_Eaters");
		names.add("Rotting_Death");
		names.add("Silent_Death");
		names.add("The_Cult");

		return names;
	}
	*/
}
