package main.presentation.teameditor.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class ColorReplacer
{
	private static final Color COLOR1_DARKNESS_0 = new Color(224, 224, 224);
	private static final Color COLOR1_DARKNESS_1 = new Color(199, 199, 199);
	private static final Color COLOR1_DARKNESS_2 = new Color(168, 168, 168);
	private static final Color COLOR1_DARKNESS_3 = new Color(143, 143, 143);
	private static final Color COLOR1_DARKNESS_4 = new Color(87, 87, 87);
	private static final Color COLOR1_DARKNESS_5 = new Color(56, 56, 56);
	private static final Color COLOR1_DARKNESS_6 = new Color(31, 31, 31);
	
	private static final Color COLOR2_DARKNESS_0 = new Color(224, 0, 224);
	private static final Color COLOR2_DARKNESS_1 = new Color(199, 0, 199);
	private static final Color COLOR2_DARKNESS_2 = new Color(168, 0, 168);
	private static final Color COLOR2_DARKNESS_3 = new Color(143, 0, 143);
	private static final Color COLOR2_DARKNESS_4 = new Color(87, 0, 87);
	private static final Color COLOR2_DARKNESS_5 = new Color(56, 0, 56);
	private static final Color COLOR2_DARKNESS_6 = new Color(31, 0, 31);
	
	private static final Color BG_BASE = new Color(255, 0, 255);
	
	private static final double[] DARKEN = {1, .85, .7225, .6141, .3685, .2372, .1313};

	public static BufferedImage setColors(BufferedImage originalImage, Color fgColor1, Color fgColor2, Color bgColor)
	{
		BufferedImage copyImage = deepCopy(originalImage);

		for (int i = 0; i < copyImage.getWidth(); i++)
		{
			for (int j = 0; j < copyImage.getHeight(); j++)
			{
				Color pixelColor = new Color(copyImage.getRGB(i, j));

				if (rgbEquals(pixelColor, BG_BASE))
				{
					pixelColor = bgColor;
				} else if (rgbEquals(pixelColor, COLOR1_DARKNESS_0))
				{
					pixelColor = darkenColor(fgColor1, 0);
				} else if (rgbEquals(pixelColor, COLOR1_DARKNESS_1))
				{
					pixelColor = darkenColor(fgColor1, 1);
				} else if (rgbEquals(pixelColor, COLOR1_DARKNESS_2))
				{
					pixelColor = darkenColor(fgColor1, 2);
				} else if (rgbEquals(pixelColor, COLOR1_DARKNESS_3))
				{
					pixelColor = darkenColor(fgColor1, 3);
				} else if (rgbEquals(pixelColor, COLOR1_DARKNESS_4))
				{
					pixelColor = darkenColor(fgColor1, 4);
				} else if (rgbEquals(pixelColor, COLOR1_DARKNESS_5))
				{
					pixelColor = darkenColor(fgColor1, 5);
				} else if (rgbEquals(pixelColor, COLOR1_DARKNESS_6))
				{
					pixelColor = darkenColor(fgColor1, 6);
				} else if (rgbEquals(pixelColor, COLOR2_DARKNESS_0))
				{
					pixelColor = darkenColor(fgColor2, 0);
				} else if (rgbEquals(pixelColor, COLOR2_DARKNESS_1))
				{
					pixelColor = darkenColor(fgColor2, 1);
				} else if (rgbEquals(pixelColor, COLOR2_DARKNESS_2))
				{
					pixelColor = darkenColor(fgColor2, 2);
				} else if (rgbEquals(pixelColor, COLOR2_DARKNESS_3))
				{
					pixelColor = darkenColor(fgColor2, 3);
				} else if (rgbEquals(pixelColor, COLOR2_DARKNESS_4))
				{
					pixelColor = darkenColor(fgColor2, 4);
				} else if (rgbEquals(pixelColor, COLOR2_DARKNESS_5))
				{
					pixelColor = darkenColor(fgColor2, 5);
				} else if (rgbEquals(pixelColor, COLOR2_DARKNESS_6))
				{
					pixelColor = darkenColor(fgColor2, 6);
				}

				copyImage.setRGB(i, j, pixelColor.getRGB());
			}
		}
		
		return deepCopy(copyImage);
	}

	private static boolean rgbEquals(Color c1, Color c2)
	{
		return (c1.getRed() == c2.getRed() && c1.getGreen() == c2.getGreen() && c1.getBlue() == c2.getBlue());
	}

	private static BufferedImage deepCopy(BufferedImage bi)
	{
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	private static Color darkenColor(Color color, int level)
	{
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();
		
		if (level < 0 || level > 6)
			throw new IllegalArgumentException("Level must be between 0 and 4, inclusive.");
		
		red *= DARKEN[level];
		green *= DARKEN[level];
		blue *= DARKEN[level];
		
//		System.out.println("Colors for level " + level + ": R" + red + ", G" + green + ", B" + blue);
		
		return new Color(red, green, blue);
	}
}
