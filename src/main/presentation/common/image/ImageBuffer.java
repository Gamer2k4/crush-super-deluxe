package main.presentation.common.image;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

//TODO: This is really only needed because I'm using ImagePanels, which repaint every time they update.
//      I have to composite the image before I can pass it to the ImagePanel.
public class ImageBuffer
{
	private BufferedImage baseImage;
	private BufferedImage compositeImage;
	private Graphics graphics;

	public ImageBuffer(BufferedImage image)
	{
		compositeImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		graphics = compositeImage.getGraphics();
		baseImage = ImageUtils.deepCopy(image);
		addLayer(0, 0, image);
	}

	public void addLayer(int x, int y, BufferedImage layer)
	{
		graphics.drawImage(layer, x, y, null);
	}

	public BufferedImage getCompositeImage()
	{
		return compositeImage;
	}

	public BufferedImage getBaseImage()
	{
		return baseImage;
	}
}
