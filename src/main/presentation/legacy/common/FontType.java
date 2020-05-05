package main.presentation.legacy.common;

import java.awt.image.BufferedImage;

import main.presentation.common.image.ImageType;
import main.presentation.common.image.LegacyImageFactory;

public enum FontType
{
	//using legacy font names
	FONT_BIG(ImageType.FONT_BIG, 1),
	FONT_HUGE(ImageType.FONT_HUGE, 1),
	FONT_SMALL(ImageType.FONT_SMALL, 2),
	FONT_SMALL_TIGHT(ImageType.FONT_SMALL, 1),
	FONT_SMALL2(ImageType.FONT_SMALL2, 1);
	
	private final int padding;
	private final ImageType fontSource;
	private final LegacyImageFactory imageFactory;
	
	private FontType(ImageType fontSource, int padding)
	{
		this.fontSource = fontSource;
		this.padding = padding;
		this.imageFactory = LegacyImageFactory.getInstance();
	}
	
	public int getSize()
	{
		return (int) imageFactory.getImageSize(fontSource).getHeight();
	}
	
	public int getPadding()
	{
		return padding; 
	}
	
	public BufferedImage getFontImage()
	{
		return imageFactory.getImage(fontSource);
	}
}
