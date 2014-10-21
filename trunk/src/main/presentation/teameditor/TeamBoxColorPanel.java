package main.presentation.teameditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

public class TeamBoxColorPanel extends TeamColorPanel
{
	private static final long serialVersionUID = -2732166283064560435L;

	private Polygon mainColorTriangle;
	private Polygon trimColorTriangle;

	public TeamBoxColorPanel(Color mainColor, Color trimColor)
	{
		mainColorTriangle = new Polygon();
		mainColorTriangle.addPoint(0, 0);
		mainColorTriangle.addPoint(0, 50);
		mainColorTriangle.addPoint(50, 0);

		trimColorTriangle = new Polygon();
		trimColorTriangle.addPoint(0, 50);
		trimColorTriangle.addPoint(50, 0);
		trimColorTriangle.addPoint(50, 50);

		setMainColor(mainColor);
		setTrimColor(trimColor);

		this.setMinimumSize(new Dimension(50, 50));
		this.setMaximumSize(new Dimension(50, 50));
		this.setPreferredSize(new Dimension(50, 50));
	}

	@Override
	public void setMainColor(Color color)
	{
		mainColor = color;
		repaint();
	}

	@Override
	public void setTrimColor(Color color)
	{
		trimColor = color;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(mainColor);
		g2.fill(mainColorTriangle);

		g2.setColor(trimColor);
		g2.fill(trimColorTriangle);
		
		g2.setColor(Color.BLACK);
		g2.drawLine(0, 49, 49, 0);
	}
}
