package main.presentation.legacy.common;

import java.awt.Color;

public class LegacyTextElement
{
	private String text;
	private Color color;

	public LegacyTextElement(String text)
	{
		this(text, LegacyUiConstants.COLOR_LEGACY_BLUE);
	}
	
	public LegacyTextElement(String text, Color color)
	{
		this.text = text;
		this.color = color;
	}

	public String getText()
	{
		return text;
	}

	public Color getColor()
	{
		return color;
	}
	
	public void setColor(Color color)
	{
		this.color = color;
	}
}
