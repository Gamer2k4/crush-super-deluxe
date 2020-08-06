package main.presentation.teamchoicescreen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import main.presentation.common.AbstractScreenPanel;

public class DraftSetupScreen extends AbstractScreenPanel
{
	private static final long serialVersionUID = -8204800489330893727L;
	
	private static final Color BG_TINT = Color.MAGENTA;
	
	public DraftSetupScreen(int width, int height, ActionListener listener)
	{
		this(new Dimension(width, height), listener);
	}

	public DraftSetupScreen(Dimension dimension, ActionListener listener)
	{
		super(dimension);
		setBackgroundTint(BG_TINT);
		setBackground(Color.BLACK);
	}

	@Override
	protected String getBgFilename()
	{
		return "bg_slith_kurgan.png";
	}

	@Override
	public void resetScreen()
	{
		// TODO Auto-generated method stub
		
	}
}
