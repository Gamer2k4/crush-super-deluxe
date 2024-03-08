package main.presentation.common;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import main.data.Data;
import main.data.entities.Player;
import main.data.entities.Quirk;
import main.data.entities.Skill;
import main.data.entities.Stats;
import main.data.factory.PlayerFactory;
import main.presentation.game.FontType;
import main.presentation.game.GameText;
import main.presentation.legacy.common.LegacyUiConstants;

public class PlayerTextFactory
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

	public static GameText getRushAttempts()
	{
		return GameText.small2(LegacyUiConstants.COLOR_LEGACY_DULL_GREEN,
				getIntAsText(currentGameStatsForPlayer.getStat(Stats.STATS_RUSHING_ATTEMPTS), 3));
	}

	public static GameText getRushTiles()
	{
		return GameText.small2(LegacyUiConstants.COLOR_LEGACY_DULL_GREEN,
				getIntAsText(currentGameStatsForPlayer.getStat(Stats.STATS_RUSHING_YARDS), 3));
	}

	public static GameText getRushAverage()
	{
		int yards = currentGameStatsForPlayer.getStat(Stats.STATS_RUSHING_YARDS);
		int attempts = currentGameStatsForPlayer.getStat(Stats.STATS_RUSHING_ATTEMPTS);
		int average = 0;

		if (attempts > 0)
			average = (int) (yards / attempts);

		return GameText.small2(LegacyUiConstants.COLOR_LEGACY_DULL_GREEN, getIntAsText(average, 3));
	}

	public static GameText getColoredAttributeWithModifiers(int attribute, Color defaultColor, Color enhancedColor)
	{
		return getColoredAttributeWithModifiers(attribute, defaultColor, enhancedColor, FontType.FONT_SMALL2);
	}

	public static GameText getColoredAttributeWithModifiers(int attribute, Color defaultColor, Color enhancedColor, FontType font)
	{
		Color color = defaultColor;

		Player basePlayer = PlayerFactory.getInstance().createPlayerWithDefinedName(currentPlayer.getRace(), "BASE");
		int baseAttribute = basePlayer.getAttributeWithoutModifiers(attribute);
		int modifiedAttribute = currentPlayer.getAttributeWithModifiers(attribute, currentGameDataImpl);

		if (modifiedAttribute > 99)
			modifiedAttribute = 99;

		if (modifiedAttribute < baseAttribute)
			color = LegacyUiConstants.COLOR_LEGACY_RED;

		if (modifiedAttribute > baseAttribute)
			color = enhancedColor;

		return new GameText(font, color, getIntAsText(modifiedAttribute, 2));
	}

	public static GameText getColoredCost(Color defaultColor, Color enhancedColor)
	{
		Color color = defaultColor;

		Player basePlayer = PlayerFactory.getInstance().createPlayerWithDefinedName(currentPlayer.getRace(), "BASE");

		int baseCost = basePlayer.getSalary();
		int currentCost = currentPlayer.getSalary();

		if (currentCost < baseCost)
			color = LegacyUiConstants.COLOR_LEGACY_RED;

		if (currentCost > baseCost)
			color = enhancedColor;

		return GameText.small2(color, String.valueOf(currentCost));
	}
	
	public static List<GameText> getSkills(Point coords)
	{
		List<String> skills = new ArrayList<String>();
		
		for (Skill skill : currentPlayer.getSkills())
		{
			skills.add(skill.getName());
		}
		
		return getTextBox(coords, skills);
	}
	
	public static List<GameText> getQuirks(Point coords)
	{
		List<String> quirks = new ArrayList<String>();

		for (Quirk quirk : currentPlayer.getQuirks())
		{
			quirks.add(quirk.getName());
		}
		
		return getTextBox(coords, quirks);
	}
	
	private static List<GameText> getTextBox(Point coords, List<String> textElements)
	{
		int maxWidth = 198;
		
		List<GameText> textBoxLines = new ArrayList<GameText>();
		
		if (textElements.isEmpty())
			return textBoxLines;
		
		String currentLine = "";
		
		for (int i = 0; i < textElements.size(); i++)
		{
			String textElement = textElements.get(i);
			String lineToCheck = currentLine + "  " + textElement + ",";	//yes, the original game has a double space between elements
			lineToCheck = lineToCheck.trim();	//it's a bit of a waste to do this with every addition, but it's the simplest way to ensure lines don't start with spaces
			
			int lineWidth = GameText.getStringPixelLength(GameText.small2, lineToCheck);
			
			if (lineWidth > maxWidth)
			{
				int x = coords.x;
				int y = coords.y + (6 * textBoxLines.size());
				GameText textLine = GameText.small2(new Point(x, y), LegacyUiConstants.COLOR_LEGACY_GREY, currentLine);
				currentLine = "";
				textBoxLines.add(textLine);
				i--;
			}
			else
			{
				currentLine = lineToCheck;
			}
		}
		
		textBoxLines.add(GameText.small2(new Point(coords.x, coords.y + (6 * textBoxLines.size())), LegacyUiConstants.COLOR_LEGACY_GREY, currentLine));
		
		return textBoxLines;
	}
}
