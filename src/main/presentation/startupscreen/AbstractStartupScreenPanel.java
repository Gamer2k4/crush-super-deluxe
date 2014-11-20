package main.presentation.startupscreen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import main.presentation.teameditor.utils.ColorReplacer;

public abstract class AbstractStartupScreenPanel extends JPanel
{
	private static final long serialVersionUID = 7193565445403303507L;
	
	private static final Color BG_TINT = Color.GREEN;
	private static final String BG_IMAGE_PATH = Paths.get(System.getProperty("user.dir")).getParent().toString()
	+ "\\resources\\editor_images\\backgrounds\\";
	
	private BufferedImage bgImage;
	
	protected AbstractStartupScreenPanel(int height, int width)
	{
		this(new Dimension(width, height));
	}
	
	protected AbstractStartupScreenPanel(Dimension dimension)
	{
		setMinimumSize(dimension);
		setMaximumSize(dimension);
		setPreferredSize(dimension);
		setSize(dimension);
		setLayout(null);
		
		setBackgroundImage();
	}
	
	private void setBackgroundImage()
	{
		String path = BG_IMAGE_PATH + getBgFilename();
		
		try
		{
			bgImage = ImageIO.read(new File(path));
		} catch (IOException e)
		{
			System.out.println("Startup Screen - Could not load graphic! Path was " + path);
		}
		
		//TODO: doesn't work, but might when we get proper image files
		bgImage = ColorReplacer.setColors(bgImage, BG_TINT, Color.MAGENTA, Color.BLACK);
		
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		if (bgImage == null)
			return;
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.drawImage(bgImage, 0, 0, null);
	}
	
	protected abstract String getBgFilename();
}
