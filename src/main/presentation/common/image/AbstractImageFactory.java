package main.presentation.common.image;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

public abstract class AbstractImageFactory
{
	public abstract BufferedImage getImage(ImageType type);
	public abstract BufferedImage copyImage(ImageType type);
	public abstract Dimension getImageSize(ImageType type);
	protected abstract String getBaseDirectory();
	
	protected String getTypeString(ImageType type)
	{	
		if (type == null)
			return ImageType.NO_TYPE.toString();
		
		return type.toString();
	}
}
