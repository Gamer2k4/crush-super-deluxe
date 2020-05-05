package main.presentation.common.image;

import java.awt.Color;
import java.awt.image.BufferedImage;

import main.presentation.common.Logger;

public abstract class AbstractColorReplacer
{
	private static final double[] DARKEN = { 1, .85, .7225, .6141, .3685, .2372, .1313 };

	protected abstract Color getColor1(int level);
	protected abstract Color getColor2(int level);
	protected abstract Color getBackgroundBase();

	public static BufferedImage tintImage(BufferedImage originalImage, Color tint)
	{
		BufferedImage copyImage = ImageUtils.deepCopy(originalImage);

		double rMult = tint.getRed() / 255.0;
		double gMult = tint.getGreen() / 255.0;
		double bMult = tint.getBlue() / 255.0;

		for (int i = 0; i < copyImage.getWidth(); i++)
		{
			for (int j = 0; j < copyImage.getHeight(); j++)
			{
				Color oldColor = new Color(copyImage.getRGB(i, j));
				Color newColor = new Color((int) (rMult * oldColor.getRed()), (int) (gMult * oldColor.getGreen()),
						(int) (bMult * oldColor.getBlue()));
				copyImage.setRGB(i, j, newColor.getRGB());
			}
		}

		return copyImage;
	}

	public BufferedImage setColors(BufferedImage originalImage, Color fgColor1, Color fgColor2, Color bgColor)
	{
		BufferedImage copyImage = ImageUtils.deepCopy(originalImage);
		
		if (fgColor1 == null || fgColor2 == null || bgColor == null)
		{
			Logger.warn("ColorReplacer.setColors() - All three color arguments must be non-null; args were fgColor1[" + fgColor1 + "], fgColor2[" + fgColor2 + "], bgColor[" + bgColor + "]/");
			return copyImage;
		}

		for (int i = 0; i < copyImage.getWidth(); i++)
		{
			for (int j = 0; j < copyImage.getHeight(); j++)
			{
				Color pixelColor = new Color(copyImage.getRGB(i, j), true);

				if (ImageUtils.rgbEquals(pixelColor, getBackgroundBase()))
				{
					pixelColor = bgColor;
					// pixelColor = new Color(0, 0, 0, 255); //TODO: make sure the BufferedImage supports transparency (done in ImageFactory)
				} else if (ImageUtils.rgbEquals(pixelColor, getColor1(0)))
				{
					pixelColor = darkenColor(fgColor1, 0);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor1(1)))
				{
					pixelColor = darkenColor(fgColor1, 1);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor1(2)))
				{
					pixelColor = darkenColor(fgColor1, 2);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor1(3)))
				{
					pixelColor = darkenColor(fgColor1, 3);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor1(4)))
				{
					pixelColor = darkenColor(fgColor1, 4);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor1(5)))
				{
					pixelColor = darkenColor(fgColor1, 5);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor1(6)))
				{
					pixelColor = darkenColor(fgColor1, 6);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor2(0)))
				{
					pixelColor = darkenColor(fgColor2, 0);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor2(1)))
				{
					pixelColor = darkenColor(fgColor2, 1);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor2(2)))
				{
					pixelColor = darkenColor(fgColor2, 2);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor2(3)))
				{
					pixelColor = darkenColor(fgColor2, 3);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor2(4)))
				{
					pixelColor = darkenColor(fgColor2, 4);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor2(5)))
				{
					pixelColor = darkenColor(fgColor2, 5);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor2(6)))
				{
					pixelColor = darkenColor(fgColor2, 6);
				}
				
				copyImage.setRGB(i, j, pixelColor.getRGB());
			}
		}

		return ImageUtils.deepCopy(copyImage);
	}

	protected Color darkenColor(Color color, int level)
	{
		if (color == null)
			return null;
		
		if (level < 0 || level > 6)
			throw new IllegalArgumentException("Level must be between 0 and 4, inclusive.");
		
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();

		red *= DARKEN[level];
		green *= DARKEN[level];
		blue *= DARKEN[level];

		return new Color(red, green, blue);
	}
}
