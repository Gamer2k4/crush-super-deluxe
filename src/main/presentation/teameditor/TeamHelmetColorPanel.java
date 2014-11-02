package main.presentation.teameditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import main.presentation.teameditor.utils.ColorReplacer;
import main.presentation.teameditor.utils.ImageFactory;
import main.presentation.teameditor.utils.ImageType;

public class TeamHelmetColorPanel extends TeamColorPanel
{
	private static final long serialVersionUID = 2993187078524011974L;
	
	private BufferedImage helmetImage;
	private BufferedImage originalImage;

	public TeamHelmetColorPanel(Color mainColor, Color trimColor)
	{
		originalImage = ImageFactory.getImage(ImageType.HELMET);
		
		this.mainColor = mainColor;
		setTrimColor(trimColor);
		
		Dimension helmetDimension = ImageFactory.getImageSize(ImageType.HELMET);

		this.setMinimumSize(helmetDimension);
		this.setMaximumSize(helmetDimension);
		this.setPreferredSize(helmetDimension);
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
		helmetImage = ColorReplacer.setColors(originalImage, mainColor, trimColor, TeamEditorGUI.BG_COLOR);
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
