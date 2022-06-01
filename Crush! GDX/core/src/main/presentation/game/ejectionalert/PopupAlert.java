package main.presentation.game.ejectionalert;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.game.GameText;
import main.presentation.game.StaticImage;

public abstract class PopupAlert
{
	protected Texture blackTexture = ImageFactory.getInstance().getTexture(ImageType.BLACK_SCREEN);
	
	protected StaticImage textBox;
	protected StaticImage offsetTextBox;
	
	protected StaticImage image;
	protected StaticImage offsetImage;
	
	protected PopupAlert(int textBoxWidth, int textBoxHeight)
	{
		Drawable singleBlackPixel = new TextureRegionDrawable(new TextureRegion(blackTexture, 0, 0, 1, 1));
		image = new StaticImage(singleBlackPixel, new Point(-1, -1));
		offsetImage = image;
		
		Drawable resizedTextBox = new TextureRegionDrawable(new TextureRegion(blackTexture, 0, 0, textBoxWidth, textBoxHeight));
		
		textBox = new StaticImage(resizedTextBox, new Point(getTextBoxCoords().x, getTextBoxCoords().y));
		offsetTextBox = new StaticImage(resizedTextBox, new Point(getOffsetTextBoxCoords().x, getOffsetTextBoxCoords().y));
	}
	
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
	
	protected abstract Point getTextBoxCoords();
	protected abstract Point getOffsetTextBoxCoords();
	protected abstract List<GameText> getGameTexts();
}
