package main.presentation.teamcolorpanel;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public abstract class TeamColorPanel extends JPanel
{
	private static final long serialVersionUID = -3960409348995529472L;
	
	protected Color mainColor;
	protected Color trimColor;

	public abstract void setMainColor(Color color);

	public abstract void setTrimColor(Color color);
	
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
	}

}