package main.presentation.common;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.presentation.common.image.ImageUtils;
import main.presentation.legacy.common.FontType;
import main.presentation.legacy.common.LegacyTextElement;

public abstract class AbstractFontFactory extends ImageUtils
{
	protected static boolean onlyUppercase = true;
	
	public static void setOnlyUppercaseValue(boolean value)
	{
		onlyUppercase = value;
	}

	public BufferedImage printStringToImage(BufferedImage image, int x, int y, String text)
	{
		return printStringToImage(image, x, y, text, Color.BLACK, FontType.FONT_SMALL2);
	}

	public BufferedImage printStringToImage(BufferedImage image, int x, int y, String text, Color color)
	{
		return printStringToImage(image, x, y, text, color, FontType.FONT_SMALL2);
	}

	public BufferedImage printStringToImage(BufferedImage image, int x, int y, String text, FontType fontType)
	{
		return printStringToImage(image, x, y, text, Color.BLACK, fontType);
	}

	public BufferedImage generateString(LegacyTextElement textElement)
	{
		return generateString(textElement.getText(), textElement.getColor(), FontType.FONT_SMALL2);
	}

	public BufferedImage generateString(LegacyTextElement textElement, FontType fontType)
	{
		return generateString(textElement.getText(), textElement.getColor(), fontType);
	}

	public BufferedImage generateString(String text)
	{
		return generateString(text, Color.BLACK, FontType.FONT_SMALL2);
	}

	public BufferedImage generateString(String text, Color color)
	{
		return generateString(text, color, FontType.FONT_SMALL2);
	}

	public BufferedImage generateString(String text, FontType fontType)
	{
		return generateString(text, Color.BLACK, fontType);
	}

	public abstract BufferedImage generateString(String text, Color color, FontType fontType);
	
	public BufferedImage printStringToImage(BufferedImage image, int x, int y, String text, Color color, FontType fontType)
	{
		BufferedImage blankImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		BufferedImage combinedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		BufferedImage stringImage = generateString(text, color, fontType);
		copySrcIntoDstAt(stringImage, blankImage, x, y);

		Graphics g = combinedImage.getGraphics();
		g.drawImage(image, 0, 0, null);
		g.drawImage(blankImage, 0, 0, null);
		
		return combinedImage;
	}
}