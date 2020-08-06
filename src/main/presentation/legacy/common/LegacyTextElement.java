package main.presentation.legacy.common;

import java.awt.Color;

public class LegacyTextElement
{
	private String text;
	private Color color;
	private FontType font;

	public LegacyTextElement(String text)
	{
		this(text, LegacyUiConstants.COLOR_LEGACY_BLUE);
	}
	
	public LegacyTextElement(String text, Color color)
	{
		this(text, color, FontType.FONT_SMALL2);
	}
	
	public LegacyTextElement(String text, Color color, FontType font)
	{
		this.text = text;
		this.color = color;
		this.font = font;
	}

	public String getText()
	{
		return text;
	}

	public Color getColor()
	{
		return color;
	}
	
	public FontType getFont()
	{
		return font;
	}
}
