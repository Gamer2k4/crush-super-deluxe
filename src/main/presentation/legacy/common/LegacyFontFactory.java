package main.presentation.legacy.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import main.presentation.common.AbstractFontFactory;
import main.presentation.common.Logger;
import main.presentation.common.image.ImageUtils;

public class LegacyFontFactory extends AbstractFontFactory
{
	private static Map<Character, Integer> codeMap = null;
	
	private static final Color COLOR_FONT_ORIGINAL = new Color(224, 0, 0);
	private static final String SHORT_CHARS = " .,\'";
	
	private static LegacyFontFactory instance = null;
	
	protected LegacyFontFactory() {} //singleton
	
	public static LegacyFontFactory getInstance()
	{
		if (instance == null)
			instance = new LegacyFontFactory();

		return instance;
	}

	@Override
	public BufferedImage generateString(String text, Color color, FontType fontType)
	{
		int offset = 0;	//add 3 * padding for each "small" character
		char lastChar = 206;
		
		if (text.isEmpty())
			return ImageUtils.createBlankBufferedImage(new Dimension(1, 1), LegacyUiConstants.COLOR_LEGACY_TRANSPARENT);
		
		if (codeMap == null)
			defineCodeMap();

		int fontSize = fontType.getSize();
		int charWidth = fontSize + fontType.getPadding();	//TODO: in the original game, periods, spaces, and so on are given narrower characters
		int width = charWidth * text.length();
		String textToPrint = text.toUpperCase();
		
		BufferedImage fontSource = ImageUtils.replaceColor(fontType.getFontImage(), COLOR_FONT_ORIGINAL, color);
		BufferedImage stringImage = new BufferedImage(width, fontSize, BufferedImage.TYPE_INT_ARGB);

		for (int i = 0; i < textToPrint.length(); i++)
		{
			char character = textToPrint.charAt(i);
			int index;
			
			try
			{
				index = codeMap.get(character);
			} catch (Exception e)
			{
				Logger.warn("Null pointer for word " + textToPrint + ", character " + character + " (" + (int)character + ").");
				continue;
			}
			
			//the current character should be printed closer if the previous character is small
			if (isShortChar(lastChar))
				offset += (3 * fontType.getPadding());

			BufferedImage charImage = ImageUtils.deepCopy(fontSource.getSubimage(index * fontSize, 0, fontSize, fontSize));
			copySrcIntoDstAt(charImage, stringImage, (charWidth * i) - offset, 0);
			
			lastChar = character;
		}

		return stringImage;
	}

	private void defineCodeMap()
	{
		codeMap = new HashMap<Character, Integer>();

		//letters
		for (int i = 0; i < 26; i++)
		{
			codeMap.put((char) (i + 65), i);
		}
		
		//numbers
		for (int i = 26; i < 36; i++)
		{
			codeMap.put((char) (i + 22), i);
		}
		
		codeMap.put('!', 36);
		codeMap.put('\"', 37);
		codeMap.put('#', 38);
		codeMap.put('?', 39);
		codeMap.put('%', 40);
		codeMap.put('&', 41);
		codeMap.put('\'', 42);
		codeMap.put('(', 43);
		codeMap.put(')', 44);
		codeMap.put('+', 45); // TODO: figure out what all of these are
		codeMap.put('`', 46);
		codeMap.put(',', 47);
		codeMap.put('-', 48);
		codeMap.put('.', 49);	//should be correct
		codeMap.put(':', 50);
		codeMap.put(';', 51);
		codeMap.put('>', 52);
		codeMap.put(':', 53);
		codeMap.put(' ', 54);	//should be correct
		codeMap.put(',', 55);
		
		codeMap.put('_', 54);
	}
	
	public static boolean isShortChar(char character)
	{
		return SHORT_CHARS.contains("" + character);
	}
}
