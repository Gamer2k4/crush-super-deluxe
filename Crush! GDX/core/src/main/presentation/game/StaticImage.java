package main.presentation.game;

import java.awt.Point;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import main.presentation.ImageFactory;
import main.presentation.ImageType;

public class StaticImage
{
	private Image image;
	
	public StaticImage(ImageType imageType, Point coords)
	{
		image = new Image(ImageFactory.getInstance().getDrawable(imageType));
		setPosition(coords);
	}
	
	public StaticImage(Drawable drawable, Point coords)
	{
		image = new Image(drawable);
		setPosition(coords);
	}
	
	public void setPosition(Point coords)
	{
		image.setPosition(coords.x, coords.y);
	}
	
	public Image getImage()
	{
		return image;
	}
	
	public int getWidth()
	{
		return (int)image.getWidth();
	}
	
	public int getHeight()
	{
		return (int)image.getHeight();
	}
}
