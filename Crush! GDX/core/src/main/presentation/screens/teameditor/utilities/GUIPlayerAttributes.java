package main.presentation.screens.teameditor.utilities;

import main.data.entities.Player;
import main.data.entities.Skill;

public class GUIPlayerAttributes
{
	private static String[] ranks = { "ROOKIE", "REGULAR", "VETERAN", "CHAMPION", "CAPTAIN", "HERO", "LEGEND", "AVATAR" };

	public static String getNameEmpty(Player player)
	{
		return getName(player, "EMPTY");
	}

	public static String getNameBlank(Player player)
	{
		return getName(player, "");
	}

	private static String getName(Player player, String emptyString)
	{
		return (player == null) ? emptyString : player.name.toUpperCase();
	}

	public static String getRank(Player player)
	{
		return (player == null) ? "" : ranks[player.getRank()];
	}

	public static String getRace(Player player)
	{
		return (player == null) ? "" : player.getRace().name();
	}

	public static String getValueWithK(Player player)
	{
		if (player == null)
			return "000K";

		String value = String.valueOf(player.getSalary()) + "K";

		if (value.length() < 4)
			value = "0" + value;

		return value;
	}

	public static String getValue(Player player)
	{
		if (player == null)
			return "000";

		String value = String.valueOf(player.getSalary());

		if (value.length() < 3)
			value = "0" + value;

		return value;
	}

	public static String getStatus(Player player)
	{
		if (player == null)
			return "";

		String value = "";

		switch (player.getInjuryType())
		{
		case Player.INJURY_NONE:
			value = "NORMAL";
			break;
		case Player.INJURY_TRIVIAL:
			value = "TRIVIAL INJURY";
			break;
		case Player.INJURY_MINOR:
			value = "MINOR INJURY";
			break;
		case Player.INJURY_CRIPPLING:
			value = "CRIPPLING INJURY";
			break;
		}

		return value;
	}
	
	public static String getWeeksOut(Player player)
	{
		if (player == null)
			return "";
		
		int weeksOut = player.getWeeksOut();
		
		String weeks = weeksOut + " week";
		
		if (weeksOut != 1)
			return weeks + "s";
		
		return weeks;
	}
	
	public static String getSkillPoints(Player player)
	{
		return (player == null) ? "" : String.valueOf(player.getSkillPoints());
	}
	
	public static String getSkillsString(Player player)
	{
		if (player == null)
			return "";
		
		String skillString = "";
		
		for (Skill skill : player.getSkills())
		{
			skillString = skillString + skill.getName() + ", ";
		}
		
		return skillString.substring(0, skillString.length() - 2);
	}
	
	public static String getQuirks(Player player)
	{
		return "";		//TODO: update this once quirks are implemented
	}
	
	public static String getSeasons(Player player)
	{
		if (player == null)
			return "0";
		
		return "" + player.getSeasons();
	}
	
	public static String getRating(Player player)
	{
		if (player == null)
			return "0 / 0";
		
		return player.getXP() + " / " + player.getAverageRating();
	}
	
	public static String getAttribute(Player player, int index)
	{
		return (player == null) ? "" : String.valueOf(player.getAttributeWithModifiers(index));
	}
	
	public static String getIndexString(int index)
	{
		if (index < 9)
			return String.valueOf(index + 1);
		
		return String.valueOf((char) (index + 56));
	}
}
