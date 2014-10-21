package main.presentation.teameditor.utils;

import main.data.entities.Player;

public class GUIPlayerAttributes
{
	private static String[] ranks = { "ROOKIE", "REGULAR", "VETERAN", "CHAMPION", "CAPTAIN", "HERO", "LEGEND", "AVATAR" };
	private static String[] races = { "CURMIAN", "DRAGORAN", "GRONK", "HUMAN", "KURGAN", "NYNAX", "SLITH", "XJS9000" };

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
		return (player == null) ? "" : races[player.getRace()];
	}

	public static String getValue(Player player)
	{
		if (player == null)
			return "000K";

		String value = String.valueOf(player.getSalary()) + "K";

		if (value.length() < 4)
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
		case Player.INJURY_MINOR:
			value = "MINOR INJURY";
			break;
		case Player.INJURY_CRIPPLING:
			value = "CRIPPLING INJURY";
			break;
		}

		return value;
	}
	
	public static String getSkillPoints(Player player)
	{
		return (player == null) ? "" : String.valueOf(player.getSkillPoints());
	}
	
	public static String getSeasons(Player player)
	{
		return "0";		//TODO: update this once stats are implemented
	}
	
	public static String getRating(Player player)
	{
		return "0/0";		//TODO: update this once stats are implemented
	}
	
	public static String getAttribute(Player player, int index)
	{
		return (player == null) ? "" : String.valueOf(player.getAttributeWithModifiers(index));
	}
}
