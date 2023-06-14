package main.presentation.game;

import java.awt.Point;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import main.presentation.ImageFactory;
import main.presentation.ImageType;

public class StaticImage
{
	private Image image;
	
	public StaticImage(Image image, Point coords)
	{
		this.image = image;
		setPosition(coords);
	}
	
	public StaticImage(ImageType imageType, Point coords)
	{
		this(new Image(ImageFactory.getInstance().getDrawable(imageType)), coords);
	}
	
	public StaticImage(Drawable drawable, Point coords)
	{
		this(new Image(drawable), coords);
	}
	
	public StaticImage(Texture texture, Point coords)
	{
		this(new TextureRegionDrawable(texture), coords);
	}
	
	public void setPosition(Point coords)
	{
		image.setPosition(coords.x, coords.y);
	}
	
	public Point getPosition()
	{
		return new Point((int)image.getImageX(), (int)image.getImageY());
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
