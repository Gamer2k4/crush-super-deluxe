package main.presentation.game.ejectionalert;

import java.awt.Point;

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
	
	protected EjectionAlert(ImageType type, int textBoxWidth, int textBoxHeight)
	{
		super(textBoxWidth, textBoxHeight);
		
		image = new StaticImage(type, IMAGE_COORDS);
		offsetImage = new StaticImage(type, OFFSET_IMAGE_COORDS);
		
		int heightDif = (int)(image.getHeight() - textBoxHeight);
		
		textBox.setPosition(new Point(TEXTBOX_COORDS.x, TEXTBOX_COORDS.y + heightDif));
		offsetTextBox.setPosition(new Point(OFFSET_TEXTBOX_COORDS.x, OFFSET_TEXTBOX_COORDS.y + heightDif));
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
