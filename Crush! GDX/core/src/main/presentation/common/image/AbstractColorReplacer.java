package main.presentation.common.image;

import java.awt.Color;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;

import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.common.Logger;

public abstract class AbstractColorReplacer
{
	protected static final int MAX_DARKEN_LEVEL = 7;
	private static final double[] DARKEN = { 1, .85, .7225, .6141, .5, .3685, .2372, .1313 };

	protected abstract Color getColor1(int level);
	protected abstract Color getColor2(int level);
	protected abstract Color getBackgroundBase();
	
	public Texture setColors(Texture originalImage, Color fgColor1, Color fgColor2, Color bgColor)
	{
		if (fgColor1 == null || fgColor2 == null || bgColor == null)
		{
			Logger.warn("ColorReplacer.setColors() - All three color arguments must be non-null; args were fgColor1[" + fgColor1 + "], fgColor2[" + fgColor2 + "], bgColor[" + bgColor + "]/");
			return originalImage;
		}
		
		TextureData textureData = originalImage.getTextureData();
	    if (!textureData.isPrepared())
	    	textureData.prepare();
	    
	    Pixmap originalPixmap = textureData.consumePixmap();
	    Pixmap targetPixmap = new Pixmap(originalPixmap.getWidth(), originalPixmap.getHeight(), originalPixmap.getFormat());

		for (int i = 0; i < originalPixmap.getWidth(); i++)
		{
			for (int j = 0; j < originalPixmap.getHeight(); j++)
			{
				Color pixelColor = getColorFromPixmapPixel(originalPixmap, i, j);

				if (ImageUtils.rgbaEquals(pixelColor, getBackgroundBase()))
				{
					pixelColor = bgColor;
				} else if (ImageUtils.rgbEquals(pixelColor, getColor1(0)))
				{
					pixelColor = darkenColor(fgColor1, 0);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor1(1)))
				{
					pixelColor = darkenColor(fgColor1, 1);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor1(2)))
				{
					pixelColor = darkenColor(fgColor1, 2);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor1(3)))
				{
					pixelColor = darkenColor(fgColor1, 3);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor1(4)))
				{
					pixelColor = darkenColor(fgColor1, 4);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor1(5)))
				{
					pixelColor = darkenColor(fgColor1, 5);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor1(6)))
				{
					pixelColor = darkenColor(fgColor1, 6);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor1(7)))
				{
					pixelColor = darkenColor(fgColor1, 7);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor2(0)))
				{
					pixelColor = darkenColor(fgColor2, 0);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor2(1)))
				{
					pixelColor = darkenColor(fgColor2, 1);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor2(2)))
				{
					pixelColor = darkenColor(fgColor2, 2);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor2(3)))
				{
					pixelColor = darkenColor(fgColor2, 3);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor2(4)))
				{
					pixelColor = darkenColor(fgColor2, 4);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor2(5)))
				{
					pixelColor = darkenColor(fgColor2, 5);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor2(6)))
				{
					pixelColor = darkenColor(fgColor2, 6);
				} else if (ImageUtils.rgbEquals(pixelColor, getColor2(7)))
				{
					pixelColor = darkenColor(fgColor2, 7);
				}
				
				targetPixmap.drawPixel(i, j, ImageUtils.getRGBAfromColor(pixelColor));
			}
		}
		
		Texture newTexture = setTextureFromPixmap(targetPixmap);
//		Texture newTexture = new Texture(targetPixmap);
		
		textureData.disposePixmap();		//I think this might be unnecessary
//	    originalPixmap.dispose();

		return newTexture;
	}
	
	private Texture setTextureFromPixmap(Pixmap pixmap)
	{
		Texture texture = null;
		
		while (texture == null)
		{
			try
			{
				texture = new Texture(pixmap);
			} catch (RuntimeException re)
			{
				if (re.getMessage() != null && re.getMessage().contains("No OpenGL context found in the current thread"))
					Logger.warn("AbstractColorReplacer - Hiding OpenGL exception and returning null Texture.");
				
				return null;
			}
		}
		
		return texture;
	}

	public Texture replaceColor(Texture source, Color oldColor, Color newColor)
	{
		TextureData textureData = source.getTextureData();
	    if (!textureData.isPrepared())
	    	textureData.prepare();
	    
	    Pixmap originalPixmap = textureData.consumePixmap();
	    Pixmap targetPixmap = new Pixmap(originalPixmap.getWidth(), originalPixmap.getHeight(), originalPixmap.getFormat());

		for (int i = 0; i < originalPixmap.getWidth(); i++)
		{
			for (int j = 0; j < originalPixmap.getHeight(); j++)
			{
				Color pixelColor = getColorFromPixmapPixel(originalPixmap, i, j);

				if (ImageUtils.rgbEquals(pixelColor, oldColor))
				{
					pixelColor = newColor;
				}

				targetPixmap.drawPixel(i, j, ImageUtils.getRGBAfromColor(pixelColor));
			}
		}

		Texture newTexture = new Texture(targetPixmap);
		textureData.disposePixmap();		//this might be unnecessary

		return newTexture;
	}
	
	//required because pixmaps store in RGBA format, while Color is in ARGB instead.
	private Color getColorFromPixmapPixel(Pixmap pixmap, int x, int y)
	{
		int rgbaPixelColor = pixmap.getPixel(x, y);
		int rgbPixelColor = rgbaPixelColor >> 8;
		int alphaThenRgb = (rgbaPixelColor << 24) + rgbPixelColor;
		return new Color(alphaThenRgb, true);
	}

	protected Color darkenColor(Color color, int level)
	{
		if (color == null)
			return null;
		
		if (level < 0 || level > MAX_DARKEN_LEVEL)
			throw new IllegalArgumentException("Level must be between 0 and " + MAX_DARKEN_LEVEL + ", inclusive.");
		
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();

		red *= DARKEN[level];
		green *= DARKEN[level];
		blue *= DARKEN[level];

		return new Color(red, green, blue);
	}
}
