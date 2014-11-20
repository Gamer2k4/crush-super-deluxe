package main.presentation.startupscreen;

import java.awt.event.ActionListener;

import javax.swing.JButton;


public class MainScreen extends AbstractStartupScreenPanel
{
	private static final long serialVersionUID = -4401310895835035591L;

	protected MainScreen(int height, int width, ActionListener actionListener)
	{
		super(height, width);
		
		//TODO: put this in its own method and make it right
		JButton temp = new JButton("Exit");
		temp.setSize(80, 25);
		temp.setLocation(500, 300);
		temp.addActionListener(actionListener);
		add(temp);
	}

	@Override
	protected String getBgFilename()
	{
		return "bg_red_gronk.jpg";
	}
}
