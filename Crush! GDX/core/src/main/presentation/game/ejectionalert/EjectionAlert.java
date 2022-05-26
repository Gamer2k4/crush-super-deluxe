package main.presentation.game.ejectionalert;

import java.awt.Point;
import java.awt.Rectangle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.game.StaticImage;

public abstract class EjectionAlert extends PopupAlert
{
	private static final int X_OFFSET = ImageFactory.getInstance().getTexture(ImageType.GAME_SIDEBAR).getWidth();
	
	private static final Point IMAGE_COORDS = new Point(11, 270);
	private static final Point OFFSET_IMAGE_COORDS = new Point(IMAGE_COORDS.x + X_OFFSET, IMAGE_COORDS.y);
	
	protected static final Point TEXTBOX_COORDS = new Point(IMAGE_COORDS.x + 100, IMAGE_COORDS.y);
	protected static final Point OFFSET_TEXTBOX_COORDS = new Point(OFFSET_IMAGE_COORDS.x + 100, OFFSET_IMAGE_COORDS.y);
	
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
	
	@Override
	protected Point getTextBoxCoords()
	{
		return TEXTBOX_COORDS;
	}
	
	@Override
	protected Point getOffsetTextBoxCoords()
	{
		return OFFSET_TEXTBOX_COORDS;
	}
}
