package main.presentation.teameditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import main.presentation.teameditor.utils.ColorReplacer;

public class TeamHelmetColorPanel extends TeamColorPanel
{
	private static final long serialVersionUID = 2993187078524011974L;
	
	private Color bgColor = new Color(238, 238, 238);
	
	private BufferedImage helmetImage;
	private BufferedImage originalImage;

	public TeamHelmetColorPanel(Color mainColor, Color trimColor)
	{
		Path path = Paths.get(System.getProperty("user.dir"));
		
		String pathString = path.getParent().toString() + "\\resources\\mask_72x72.bmp";
		
		try
		{
			originalImage = ImageIO.read(new File(pathString));
		} catch (IOException e)
		{
			System.out.println("TeamHelmetPanel.java - Could not load helmet graphic! Path was " + pathString);
		}
		
		this.mainColor = mainColor;
		setTrimColor(trimColor);

		this.setMinimumSize(new Dimension(72, 72));
		this.setMaximumSize(new Dimension(72, 72));
		this.setPreferredSize(new Dimension(72, 72));
	}

	@Override
	public void setMainColor(Color color)
	{
		mainColor = color;
		updateGraphic();
	}

	@Override
	public void setTrimColor(Color color)
	{
		trimColor = color;
		updateGraphic();
	}

	void updateGraphic()
	{
		helmetImage = ColorReplacer.setColors(originalImage, mainColor, trimColor, bgColor);
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.drawImage(helmetImage, 0, 0, null);
	}
}
