package main.presentation.common.image;

import java.awt.Color;

import main.presentation.common.Logger;

public enum LegacyTeamColorType
{	
	RED(0, new Color(224, 0, 0)),
	ORANGE(1, new Color(224, 96, 0)),
	GOLD(2, new Color(224, 160, 0)),
	YELLOW(3, new Color(224, 224, 0)),
	LIGHT_GREEN(5, new Color(0, 224, 0)),
	AQUA(7, new Color(0, 224, 224)),

	BLUE(9, new Color(0, 0, 224)),
	PURPLE(11, new Color(160, 0, 224)),
	LIGHT_PURPLE(12, new Color(224, 0, 224)),
	GREEN(13, new Color(0, 128, 0)),
	PINK(14, new Color(224, 0, 96)),
	RED_BROWN(17, new Color(160, 64, 64)),

	LIGHT_BLUE(18, new Color(160, 192, 224)),
	BLACK(19, new Color(0, 0, 32)),
	WHITE(21, new Color(192, 192, 192)),
	BEIGE(26, new Color(224, 192, 160)),
	LIGHT_BROWN(28, new Color(128, 96, 64)),
	DARK_BROWN(29, new Color(128, 64, 32));
	
	private int index;
	private Color color;
	
	private LegacyTeamColorType(int index, Color color)
	{
		this.index = index;
		this.color = color;
	}
	
	public int getIndex()
	{
		return index;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public static boolean isValidLegacyTeamColor(Color colorToCheck)
	{
		for (LegacyTeamColorType colorType : values())
		{
			if (ImageUtils.rgbEquals(colorType.color, colorToCheck))
				return true;
		}
		
		return false;
	}
	
	public static Color getColor(int indexToSearch)
	{
		for (LegacyTeamColorType colorType : values())
		{
			if (colorType.index == indexToSearch)
				return colorType.color;
		}
		
		throw new IllegalArgumentException("No color found for index [" + indexToSearch + "].");
	}
	
	public static Integer getIndex(Color colorToSearch)
	{
		for (LegacyTeamColorType colorType : values())
		{
			if (ImageUtils.rgbEquals(colorType.color, colorToSearch))
				return colorType.index;
		}
		
		Logger.debug("No index found for color [" + colorToSearch + "].");
		return null;
	}
}
