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
import main.presentation.game.FontType;
import main.presentation.game.GameText;
import main.presentation.game.StaticImage;
import main.presentation.legacy.common.LegacyUiConstants;

public class NewTurnAlert extends PopupAlert
{
	private static Point TEXTBOX_COORDS = new Point(198, 220);
	
	private List<GameText> newTurnMessage;
	
	public NewTurnAlert(String teamName)
	{	
		Texture textBoxTexture = ImageFactory.getInstance().getTexture(ImageType.NEW_TURN_TEXTBOX);
		textBox = new StaticImage(new TextureRegionDrawable(textBoxTexture), new Point(TEXTBOX_COORDS.x, TEXTBOX_COORDS.y));
		
		Drawable singleBlackPixel = new TextureRegionDrawable(new TextureRegion(textBoxTexture, 0, 0, 1, 1));
		image = new StaticImage(singleBlackPixel, TEXTBOX_COORDS);
		offsetImage = image;
		
		GameText message = new GameText(FontType.FONT_SMALL, new Point(0, 0), LegacyUiConstants.COLOR_LEGACY_DULL_GREEN, teamName.toUpperCase() + " YOUR TURN");
		int messagePadding = textBoxTexture.getWidth() - message.getStringPixelLength();
		message.setCoords(new Point(messagePadding / 2, 202));
		
		newTurnMessage = new ArrayList<GameText>();
		newTurnMessage.add(message);
	}
	
	@Override
	protected List<GameText> getGameTexts()
	{
		return newTurnMessage;
	}

	@Override
	protected Point getTextBoxCoords()
	{
		return TEXTBOX_COORDS;
	}

	@Override
	protected Point getOffsetTextBoxCoords()
	{
		return getTextBoxCoords();		//textbox doesn't move even if there are sidebars
	}

}
