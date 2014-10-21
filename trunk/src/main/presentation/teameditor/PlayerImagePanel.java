package main.presentation.teameditor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class PlayerImagePanel extends JPanel
{
	private static final long serialVersionUID = -1311058427280472668L;
	
	private BufferedImage playerImage;

	public PlayerImagePanel()
	{
		updateImage(null);

		this.setMinimumSize(new Dimension(160, 160));
		this.setMaximumSize(new Dimension(160, 160));
		this.setPreferredSize(new Dimension(160, 160));
	}
	
	public void updateImage(BufferedImage image)
	{
		playerImage = image;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		if (playerImage == null)
			return;
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.drawImage(playerImage, 0, 0, null);
	}
}
