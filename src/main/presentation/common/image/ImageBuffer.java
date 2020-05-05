package main.presentation.common.image;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

//TODO: This is really only needed because I'm using ImagePanels, which repaint every time they update.
//      I have to composite the image before I can pass it to the ImagePanel.
public class ImageBuffer
{
	private static BufferedImage baseImage;
	private static BufferedImage compositeImage;
	private static Graphics graphics;

	public static void setBaseImage(BufferedImage image)
	{
		compositeImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		graphics = compositeImage.getGraphics();
		baseImage = ImageUtils.deepCopy(image);
		addLayer(0, 0, image);
	}

	public static void addLayer(int x, int y, BufferedImage layer)
	{
		graphics.drawImage(layer, x, y, null);
	}

	public static BufferedImage getCompositeImage()
	{
		return compositeImage;
	}

	public static BufferedImage getBaseImage()
	{
		return baseImage;
	}
}
