package main.presentation.game.ejectionalert;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import main.presentation.game.GameText;
import main.presentation.game.StaticImage;

public abstract class PopupAlert
{
	protected StaticImage textBox;
	protected StaticImage offsetTextBox;
	
	protected StaticImage image;
	protected StaticImage offsetImage;
	
	public List<GameText> getInfoText()
	{
		return getInfoText(false);
	}
	
	public List<GameText> getInfoText(boolean isOffset)
	{
		List<GameText> gameTexts = getGameTexts();
		List<GameText> repositionedGameTexts = new ArrayList<GameText>();
		
		Point coordOffset = getTextBoxCoords();
		
		if (isOffset)
			coordOffset = getOffsetTextBoxCoords();
		
		for (GameText text : gameTexts)
		{
			GameText repositionedText = text.clone();
			Point originalCoords = text.getCoords();
			Point repositionedCoords = new Point(originalCoords.x + coordOffset.x, originalCoords.y + coordOffset.y - 267);
			repositionedText.setCoords(repositionedCoords);
			repositionedGameTexts.add(repositionedText);
		}
		
		return repositionedGameTexts;
	}

	public StaticImage getTextBox()
	{
		return getTextBox(false);
	}

	public StaticImage getTextBox(boolean isOffset)
	{
		if (isOffset)
			return offsetTextBox;
		
		return textBox;
	}
	
	public StaticImage getImage()
	{
		return getImage(false);
	}
	
	public StaticImage getImage(boolean isOffset)
	{
		if (isOffset)
			return offsetImage;
		
		return image;
	}
	
	protected abstract List<GameText> getGameTexts();
	
	protected abstract Point getTextBoxCoords();
	protected abstract Point getOffsetTextBoxCoords();
}
