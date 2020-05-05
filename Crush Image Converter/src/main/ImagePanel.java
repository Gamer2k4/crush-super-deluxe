package main;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImagePanel extends JPanel
{
	private static final long serialVersionUID = -1311058427280472668L;
	
	private BufferedImage image;
	
	public ImagePanel(int height, int width)
	{
		this(new Dimension(height, width));
	}
	
	public ImagePanel(Dimension dimension)
	{
		updateSize(dimension);		
		updateImage(null);
	}
	
	private void updateSize(Dimension dimension)
	{
		this.setMinimumSize(dimension);
		this.setMaximumSize(dimension);
		this.setPreferredSize(dimension);
		this.setSize(dimension);
	}
	
	public void updateImage(BufferedImage newImage)
	{
		image = newImage;
		
		if (image != null)
			updateSize(new Dimension(image.getWidth(), image.getHeight()));
		
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
