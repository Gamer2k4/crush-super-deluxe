package main.presentation.common.image;

import java.awt.Color;

public class ImageUtils
{
	public static boolean rgbEquals(Color c1, Color c2)
	{
		return (c1.getRed() == c2.getRed() && c1.getGreen() == c2.getGreen() && c1.getBlue() == c2.getBlue());
	}
	
	public static int getRGBAfromColor(Color color)
	{
		int alpha = color.getAlpha();
		int argbColor = color.getRGB();
		return (argbColor << 8) + alpha;
	}
	
	public static com.badlogic.gdx.graphics.Color gdxColor(Color originalColor)
	{
		return new com.badlogic.gdx.graphics.Color(getRGBAfromColor(originalColor));
	}
}
