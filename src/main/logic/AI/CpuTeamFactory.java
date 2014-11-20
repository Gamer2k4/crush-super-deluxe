package main.logic.AI;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import main.data.entities.Team;
import main.data.factory.PlayerFactory;
import main.logic.Engine;

//TODO: possibly move this to main.data.factory
public class CpuTeamFactory
{
	private static List<String> teamNames = generateRandomTeamNames();
	
	public static Team generateCpuTeam(int budget)
	{
		//TODO: this is certainly incomplete
		Team cpuTeam = generateNewTeamWithPlayers();
		cpuTeam.teamName = generateRandomTeamName();
		cpuTeam.teamColors = generateRandomTeamColors();
		cpuTeam.homeField = Engine.Randint(0, 7);
		
		return cpuTeam;
	}
	
	private static Team generateNewTeamWithPlayers()
	{
		Team team = new Team();
		
		for (int i = 0; i < 9; i++)
			team.addPlayer(PlayerFactory.createPlayerWithRandomName(Engine.Randint(0, 7)));
		
		return team;
	}
	
	private static Color[] generateRandomTeamColors()
	{
		Color[] teamColors = new Color[2];
		
		teamColors[0] = new Color(Engine.Randint(0, 255), Engine.Randint(0, 255), Engine.Randint(0, 255));
		teamColors[1] = new Color(Engine.Randint(0, 255), Engine.Randint(0, 255), Engine.Randint(0, 255));
		
		return teamColors;
	}

	private static String generateRandomTeamName()
	{
		Random r = new Random();
		int index = r.nextInt(teamNames.size());
		return normalizeTeamName(teamNames.get(index));
	}

	private static String normalizeTeamName(String name)
	{
		if (name.isEmpty())
			return "";

		String normalizedName = "_" + name;

		while (normalizedName.contains("_"))
		{
			int index = normalizedName.indexOf("_");
			if (index == normalizedName.length() - 1)
			{
				normalizedName = normalizedName.substring(0, index);
				break;
			}

			String half1 = normalizedName.substring(0, index);
			String half2 = normalizedName.substring(index + 2);
			String nextChar = normalizedName.substring(index + 1, index + 2);

			normalizedName = half1 + " " + nextChar.toUpperCase() + half2;
		}

		return normalizedName.substring(1);
	}
	
	private static List<String> generateRandomTeamNames()
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
}
