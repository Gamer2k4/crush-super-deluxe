package main.presentation.legacy.game;

import java.awt.Color;
import java.awt.Dimension;

import main.presentation.common.ImagePanel;

public class GameDisplayPanel extends ImagePanel
{
	private static final long serialVersionUID = 7969748358489475547L;

	public GameDisplayPanel(Dimension dimension)
	{
		super(dimension);
		
		//this.setBackground(Color.GREEN);
		this.setBackground(Color.BLACK);
	}
}
