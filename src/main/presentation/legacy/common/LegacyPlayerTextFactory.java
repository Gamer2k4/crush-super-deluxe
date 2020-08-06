package main.presentation.legacy.common;

import java.awt.Color;

import main.data.Data;
import main.data.entities.Player;
import main.data.entities.Stats;
import main.data.factory.PlayerFactory;

public class LegacyPlayerTextFactory
{
	private static Data currentGameDataImpl = null;
	private static Player currentPlayer;
	private static Stats currentGameStatsForPlayer;

	public static void setPlayer(Player player)
	{
		currentPlayer = player;

		if (currentGameDataImpl != null)
			refreshPlayerStats();
	}

	public static void setGameDataImpl(Data dataImpl)
	{
		currentGameDataImpl = dataImpl;
	}

	public static void refreshPlayerStats()
	{
		currentGameStatsForPlayer = currentGameDataImpl.getStatsOfPlayer(currentPlayer);
	}

	private static String addLeadingZeros(String s, int length)
	{
		if (s.length() >= length)
			return s;

		return String.format("%0" + (length - s.length()) + "d%s", 0, s);
	}

	private static String getIntAsText(int value, int length)
	{
		return addLeadingZeros(String.valueOf(value), length);
	}

	public static LegacyTextElement getRushAttempts()
	{
		return new LegacyTextElement(getIntAsText(currentGameStatsForPlayer.getStat(Stats.STATS_RUSHING_ATTEMPTS), 3),
				LegacyUiConstants.COLOR_LEGACY_DULL_GREEN);
	}

	public static LegacyTextElement getRushTiles()
	{
		return new LegacyTextElement(getIntAsText(currentGameStatsForPlayer.getStat(Stats.STATS_RUSHING_YARDS), 3),
				LegacyUiConstants.COLOR_LEGACY_DULL_GREEN);
	}

	public static LegacyTextElement getRushAverage()
	{
		int yards = currentGameStatsForPlayer.getStat(Stats.STATS_RUSHING_YARDS);
		int attempts = currentGameStatsForPlayer.getStat(Stats.STATS_RUSHING_ATTEMPTS);
		int average = 0;

		if (attempts > 0)
			average = (int) (yards / attempts);

		return new LegacyTextElement(getIntAsText(average, 3), LegacyUiConstants.COLOR_LEGACY_DULL_GREEN);
	}
	
	public static LegacyTextElement getColoredAttributeWithModifiers(int attribute, Color defaultColor, Color enhancedColor)
	{
		Color color = defaultColor;
		
		Player basePlayer = PlayerFactory.createPlayerWithDefinedName(currentPlayer.getRace(), "BASE");
		int baseAttribute = basePlayer.getAttributeWithoutModifiers(attribute);
		int modifiedAttribute = currentPlayer.getAttributeWithModifiers(attribute);
		
		if (modifiedAttribute > 99)
			modifiedAttribute = 99;
		
		if (modifiedAttribute < baseAttribute)
			color = LegacyUiConstants.COLOR_LEGACY_RED;
		
		if (modifiedAttribute > baseAttribute)
			color = enhancedColor;
		
		return new LegacyTextElement(getIntAsText(modifiedAttribute, 2), color);
	}
	
	public static LegacyTextElement getColoredCost(Color defaultColor, Color enhancedColor)
	{
		Color color = defaultColor;
		
		Player basePlayer = PlayerFactory.createPlayerWithDefinedName(currentPlayer.getRace(), "BASE");
		
		int baseCost = basePlayer.getSalary();
		int currentCost = currentPlayer.getSalary();
		
		if (currentCost < baseCost)
			color = LegacyUiConstants.COLOR_LEGACY_RED;
		
		if (currentCost > baseCost)
			color = enhancedColor;
		
		return new LegacyTextElement(getIntAsText(currentCost, 3), color);		//note that the cost doesn't actually have leading zeroes
	}
}
