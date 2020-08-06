package main.presentation.startupscreen;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import main.presentation.common.AbstractScreenPanel;

public abstract class AbstractStartupScreenPanel extends AbstractScreenPanel
{
	private static final long serialVersionUID = 7193565445403303507L;
	
	protected static final int BUTTON_HEIGHT = 25;
	protected static final int BUTTON_WIDTH = 120;
	protected static final int BUTTON_START_X = 540;
	protected static final int BUTTON_START_Y = 260;
	
	protected AbstractStartupScreenPanel(int width, int height)
	{
		this(new Dimension(width, height));
	}
	
	protected AbstractStartupScreenPanel(Dimension dimension)
	{
		super(dimension);
	}
	
	protected JButton createAndAddButton(int x, int y, String text, ActionListener actionListener)
	{
		JButton button = new JButton(text);
		button.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		button.setLocation(x, y);
		button.addActionListener(actionListener);
		button.setActionCommand(text);
		add(button);
		return button;
	}
}
