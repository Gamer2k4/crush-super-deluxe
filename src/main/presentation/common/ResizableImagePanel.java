package main.presentation.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ResizableImagePanel extends JPanel
{
	private static final long serialVersionUID = 3124036591147131453L;
	
	private BufferedImage image = null;
	
	public ResizableImagePanel(int width, int height)
	{
		setSize(width, height);
		setBackground(Color.BLUE);
	}

	public ResizableImagePanel(Dimension dimension)
	{
		setSize(dimension);
		setBackground(Color.BLUE);
	}
	
	public void updateImage(BufferedImage newImage)
	{
		image = newImage;
		repaint();
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		if (image == null)
			return;

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.drawImage(image, 0, 0, getWidth(), getHeight(), null);
	}
}
