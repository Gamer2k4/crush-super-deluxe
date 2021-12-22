package main.presentation.game.ejectionalert;

import java.awt.Point;
import java.awt.Rectangle;
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

public abstract class EjectionAlert
{
	private static final int X_OFFSET = ImageFactory.getInstance().getTexture(ImageType.GAME_SIDEBAR).getWidth();
	
	private static final Point IMAGE_COORDS = new Point(11, 270);
	private static final Point OFFSET_IMAGE_COORDS = new Point(IMAGE_COORDS.x + X_OFFSET, IMAGE_COORDS.y);
	
	protected static final Point TEXTBOX_COORDS = new Point(IMAGE_COORDS.x + 100, IMAGE_COORDS.y);
	protected static final Point OFFSET_TEXTBOX_COORDS = new Point(OFFSET_IMAGE_COORDS.x + 100, OFFSET_IMAGE_COORDS.y);
	
	private StaticImage image;
	private StaticImage offsetImage;
	private StaticImage textBox;
	private StaticImage offsetTextBox;
	
	protected EjectionAlert(ImageType type, Rectangle textBoxArea)
	{
		image = new StaticImage(type, IMAGE_COORDS);
		offsetImage = new StaticImage(type, OFFSET_IMAGE_COORDS);
		
		Texture textBoxTexture = ImageFactory.getInstance().getTexture(ImageType.EJECT_TEXTBOX);
		Drawable resizedTextBox = new TextureRegionDrawable(new TextureRegion(textBoxTexture, textBoxArea.x, textBoxArea.y, textBoxArea.width, textBoxArea.height));
		
		int heightDif = (int)(image.getImage().getHeight() - resizedTextBox.getMinHeight());
		
		textBox = new StaticImage(resizedTextBox, new Point(TEXTBOX_COORDS.x, TEXTBOX_COORDS.y + heightDif));
		offsetTextBox = new StaticImage(resizedTextBox, new Point(OFFSET_TEXTBOX_COORDS.x, OFFSET_TEXTBOX_COORDS.y + heightDif));
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
	
	public List<GameText> getInfoText()
	{
		return getInfoText(false);
	}
	
	public List<GameText> getInfoText(boolean isOffset)
	{
		List<GameText> gameTexts = getGameTexts();
		List<GameText> repositionedGameTexts = new ArrayList<GameText>();
		
		Point coordOffset = TEXTBOX_COORDS;
		
		if (isOffset)
			coordOffset = OFFSET_TEXTBOX_COORDS;
		
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
	
	protected abstract List<GameText> getGameTexts();
}
