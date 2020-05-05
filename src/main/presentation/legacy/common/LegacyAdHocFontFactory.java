package main.presentation.legacy.common;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.HashMap;
import java.util.Map;

public class LegacyAdHocFontFactory extends LegacyFontFactory
{
	private static Map<Character, String> codeMap = null;

	private static final int LETTER_PADDING = 1;
	private static final int FONT_SIZE = 5;
	
	private static final int CHARACTER_DIMENSION = FONT_SIZE * (FONT_SIZE + LETTER_PADDING);

	@Override
	public BufferedImage generateString(String text, Color color, FontType fontType)
	{
		if (fontType != FontType.FONT_SMALL2)
			throw new IllegalArgumentException("Only font SMALL2 is allowed right now.  Parameter value was " + fontType.name() + ".");
		
		int fontSize = FONT_SIZE;
		int charWidth = fontSize + LETTER_PADDING;
		int width = charWidth * text.length();
		BufferedImage image = new BufferedImage(width, fontSize, BufferedImage.TYPE_INT_ARGB);

		for (int i = 0; i < text.length(); i++)
		{
			BufferedImage segment = new BufferedImage(charWidth, fontSize, BufferedImage.TYPE_INT_ARGB);

			int[] letter = getMapForCharacter(text.charAt(i), color);
			int[] imgData = ((DataBufferInt) segment.getRaster().getDataBuffer()).getData();

			System.arraycopy(letter, 0, imgData, 0, CHARACTER_DIMENSION);
			copySrcIntoDstAt(segment, image, charWidth * i, 0);
		}

		return image;
	}

	private int[] getMapForCharacter(char c, Color color)
	{
		if (codeMap == null)
			defineCodeMap();

		String code;

		if (onlyUppercase)
			code = codeMap.get(Character.toUpperCase(c));
		else
			code = codeMap.get(c);

		int remainingCount = getValueForCodeChar(code, 0);
		int codeIndex = 0;
		boolean shouldOutput = false; // first code represents blanks
		int offset = 0;

		int[] returnMap = new int[CHARACTER_DIMENSION];

		for (int i = 0; i < FONT_SIZE; i++)
		{
			for (int j = 0; j < (FONT_SIZE + LETTER_PADDING); j++)
			{
				if (j > FONT_SIZE - 1)
				{
					returnMap[(FONT_SIZE * i) + j + offset] = 0;
					offset++;
					continue;
				}

				while (remainingCount == 0)
				{
					codeIndex++;
					remainingCount = getValueForCodeChar(code, codeIndex);
					shouldOutput = !shouldOutput;
				}

				returnMap[(FONT_SIZE * i) + j + offset] = (shouldOutput ? color.getRGB() : 0);

				remainingCount--;
			}
		}

		return returnMap;
	}

	private int getValueForCodeChar(String code, int index)
	{
		if (index < 0 || index > code.length())
			return 0;

		return ((int) code.charAt(index)) - 48;
	}

	private void defineCodeMap()
	{
		codeMap = new HashMap<Character, String>();
		codeMap.put('A', "1311373231");
		codeMap.put('B', "04113511351");
		codeMap.put('C', "15414154");
		codeMap.put('D', "04113232351");
		codeMap.put('E', "15441154");
		codeMap.put('F', "064411414");
		codeMap.put('G', "1541143114");
		codeMap.put('H', "0132373231");
		codeMap.put('I', "0521414125");
		codeMap.put('J', "41414231131");
		codeMap.put('K', "0132211321211131");
		codeMap.put('L', "0141414154");
		codeMap.put('M', "013313111211121111");
		codeMap.put('N', "01332211122331");
		codeMap.put('O', "1311323231131");
		codeMap.put('P', "04113511414");
		codeMap.put('Q', "13113232212211");
		codeMap.put('R', "041135113231");
		codeMap.put('S', "1553551");
		codeMap.put('T', "05214141412");
		codeMap.put('U', "0132323231131");
		codeMap.put('V', "01323111112111312");
		codeMap.put('W', "0132321112111111111");
		codeMap.put('X', "013111113131111131");
		codeMap.put('Y', "013111113141412");
		codeMap.put('Z', "0531313136");
		codeMap.put(' ', "5050505050");
		codeMap.put('0', "06323236");
		codeMap.put('1', "0341414125");
		codeMap.put('2', "054745");
		codeMap.put('3', "05412346");
		codeMap.put('4', "0132364141");
		codeMap.put('5', "064546");
		codeMap.put('6', "064636");
		codeMap.put('7', "0541414141");
		codeMap.put('8', "063736");
		codeMap.put('9', "06364141");
	}
}
