package main.presentation.common.image;

import java.awt.Color;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ImageUtils
{
	public static boolean rgbEquals(Color c1, Color c2)
	{
		return (c1.getRed() == c2.getRed() && c1.getGreen() == c2.getGreen() && c1.getBlue() == c2.getBlue());
	}
	public static boolean rgbaEquals(Color c1, Color c2)
	{
		return (c1.getRed() == c2.getRed() && c1.getGreen() == c2.getGreen() && c1.getBlue() == c2.getBlue() && c1.getAlpha() == c2.getAlpha());
	}
	
	public static int getRGBAfromColor(Color color)
	{
		int alpha = color.getAlpha();
		int argbColor = color.getRGB();
		return (argbColor << 8) + alpha;
	}
	
	public static com.badlogic.gdx.graphics.Color gdxColor(Color originalColor)
	{
		return new com.badlogic.gdx.graphics.Color(getRGBAfromColor(originalColor));
	}
	
	public static Pixmap extractPixmapFromTextureRegion(TextureRegion textureRegion) {
	    TextureData textureData = textureRegion.getTexture().getTextureData();
	    if (!textureData.isPrepared()) {
	        textureData.prepare();
	    }
	    Pixmap pixmap = new Pixmap(
	            textureRegion.getRegionWidth(),
	            textureRegion.getRegionHeight(),
	            textureData.getFormat()
	    );
	    pixmap.drawPixmap(
	            textureData.consumePixmap(), // The other Pixmap
	            0, // The target x-coordinate (top left corner)
	            0, // The target y-coordinate (top left corner)
	            textureRegion.getRegionX(), // The source x-coordinate (top left corner)
	            textureRegion.getRegionY(), // The source y-coordinate (top left corner)
	            textureRegion.getRegionWidth(), // The width of the area from the other Pixmap in pixels
	            textureRegion.getRegionHeight() // The height of the area from the other Pixmap in pixels
	    );
	    return pixmap;
	}
}
