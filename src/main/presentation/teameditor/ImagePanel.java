package main.presentation.teameditor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import main.presentation.teameditor.utils.ImageFactory;
import main.presentation.teameditor.utils.ImageType;

public class ImagePanel extends JPanel
{
	private static final long serialVersionUID = -1311058427280472668L;
	
	private BufferedImage image;

	public ImagePanel(ImageType imageType)
	{
		this(ImageFactory.getImageSize(imageType));
	}
	
	public ImagePanel(int height, int width)
	{
		this(new Dimension(height, width));
	}
	
	public ImagePanel(Dimension dimension)
	{
		this.setMinimumSize(dimension);
		this.setMaximumSize(dimension);
		this.setPreferredSize(dimension);
		this.setSize(dimension);
		
		updateImage(null);
	}
	
	public void updateImage(BufferedImage newImage)
	{
		image = newImage;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		if (image == null)
			return;
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.drawImage(image, 0, 0, null);
	}
}
