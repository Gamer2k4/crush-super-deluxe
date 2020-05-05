package main.presentation.teamcolorpanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.UIManager;

import main.presentation.common.image.AbstractColorReplacer;
import main.presentation.common.image.AbstractImageFactory;
import main.presentation.common.image.ImageType;
import main.presentation.common.image.LegacyColorReplacer;
import main.presentation.common.image.LegacyImageFactory;

public class TeamHelmetColorPanel extends TeamColorPanel
{
	private static final long serialVersionUID = 2993187078524011974L;
	
	private AbstractImageFactory imageFactory;
	private AbstractColorReplacer colorReplacer;
	
	private BufferedImage helmetImage;
	private BufferedImage originalImage;
	
	private Color backgroundColor;

	public TeamHelmetColorPanel()
	{
		this(UIManager.getColor("Panel.background"));
	}
	
	public TeamHelmetColorPanel(Color bgColor)
	{
		this(Color.WHITE, Color.WHITE, bgColor);
	}
	
	public TeamHelmetColorPanel(Color mainColor, Color trimColor, Color bgColor)
	{
		imageFactory = LegacyImageFactory.getInstance();
		colorReplacer = LegacyColorReplacer.getInstance();
		
		originalImage = imageFactory.getImage(ImageType.EDITOR_HELMET);
		backgroundColor = bgColor;
		
		this.mainColor = mainColor;
		setTrimColor(trimColor);
		
		Dimension helmetDimension = imageFactory.getImageSize(ImageType.EDITOR_HELMET);

		this.setSize(helmetDimension);
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

	private void updateGraphic()
	{
		helmetImage = colorReplacer.setColors(originalImage, mainColor, trimColor, backgroundColor);
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
