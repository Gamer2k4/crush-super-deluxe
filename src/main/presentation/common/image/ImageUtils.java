package main.presentation.common.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;

public abstract class ImageUtils
{
	public static void copySrcIntoDstAt(final BufferedImage src, final BufferedImage dst, final int dx, final int dy)
	{
		int[] srcbuf = ((DataBufferInt) src.getRaster().getDataBuffer()).getData();
		int[] dstbuf = ((DataBufferInt) dst.getRaster().getDataBuffer()).getData();
		int width = src.getWidth();
		int height = src.getHeight();
		int dstoffs = dx + dy * dst.getWidth();
		int srcoffs = 0;
		for (int y = 0; y < height; y++, dstoffs += dst.getWidth(), srcoffs += width)
		{
			System.arraycopy(srcbuf, srcoffs, dstbuf, dstoffs, width);
		}
	}

	public static BufferedImage convert(BufferedImage src, int bufImgType)
	{
		BufferedImage img = new BufferedImage(src.getWidth(), src.getHeight(), bufImgType);
		Graphics2D g2d = img.createGraphics();
		g2d.drawImage(src, 0, 0, null);
		g2d.dispose();
		return img;
	}

	public static BufferedImage createBlankBufferedImage(Dimension dimension)
	{
		return createBlankBufferedImage(dimension, new Color(0, 0, 0, 0));
	}

	public static BufferedImage createBlankBufferedImage(Dimension dimension, Color color)
	{
		BufferedImage image = new BufferedImage((int) dimension.getWidth(), (int) dimension.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();

		graphics.setPaint(color);
		graphics.fillRect(0, 0, image.getWidth(), image.getHeight());

		return image;
	}

	// TODO: should this be in AbstractColorReplacer?
	public static BufferedImage replaceColor(BufferedImage source, Color oldColor, Color newColor)
	{
		BufferedImage image = deepCopy(source);

		for (int i = 0; i < source.getWidth(); i++)
		{
			for (int j = 0; j < source.getHeight(); j++)
			{
				Color pixelColor = new Color(image.getRGB(i, j), true);

				if (rgbEquals(pixelColor, oldColor))
				{
					pixelColor = newColor;
				}

				image.setRGB(i, j, pixelColor.getRGB());
			}
		}

		return image;
	}

	public static BufferedImage scaleImage(BufferedImage bi, int multiplier)
	{
		if (multiplier <= 1)
			return deepCopy(bi);

		int width = bi.getWidth();
		int height = bi.getHeight();

		BufferedImage scaledImage = new BufferedImage(width * multiplier, height * multiplier, bi.getType());

		for (int i = 0; i < width; i++)
		{
			for (int j = 0; j < height; j++)
			{
				for (int k = 0; k < multiplier; k++)
				{
					for (int l = 0; l < multiplier; l++)
					{
						int x = i * multiplier + k;
						int y = j * multiplier + l;

						scaledImage.setRGB(x, y, bi.getRGB(i, j));
					}
				}
			}
		}

		return scaledImage;
	}

	public static BufferedImage padImage(BufferedImage bi, Dimension newSize)
	{
		if (bi == null)
			return null;
		
		int width = (int) newSize.getWidth();
		int height = (int) newSize.getHeight();

		if (width <= 0 || height <= 0)
			throw new IllegalArgumentException("New size must have a height and width greater than zero.");
		else if (width < bi.getWidth() || height < bi.getHeight())
		{
			//TODO: this is the result of a RasterFormatException in the Checking stats screen; I think it happened when one team had checking stats but they were all dead
			//this would be when padding the team name
			//seems like it's when the name is too long?
//			System.out.format("Dimension width: %d Dimension height: %d; Image width: %d Image Height: %d", width, height, bi.getWidth(), bi.getHeight());
			return deepCopy(bi.getSubimage(0, 0, width, height));
		}

		int x = (width - bi.getWidth()) / 2;
		int y = (height - bi.getHeight()) / 2;

		BufferedImage image = new BufferedImage(width, height, bi.getType());
//		Logger.debug("new buffered image bg color is " + bi.getRGB(0, 0));
		copySrcIntoDstAt(bi, image, x, y);

		return image;
	}

	public static BufferedImage deepCopy(BufferedImage bi)
	{
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(bi.getRaster().createCompatibleWritableRaster());
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public static boolean rgbEquals(Color c1, Color c2)
	{
		return (c1.getRed() == c2.getRed() && c1.getGreen() == c2.getGreen() && c1.getBlue() == c2.getBlue());
	}
}
