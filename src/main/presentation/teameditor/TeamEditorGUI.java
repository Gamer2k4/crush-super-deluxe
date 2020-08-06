package main.presentation.teameditor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import main.data.entities.Team;

public class TeamEditorGUI extends JFrame implements ActionListener
{
	private static final long serialVersionUID = -4815838996578005455L;

	private static final int FRAME_WIDTH = TeamEditorPanel.MAIN_PANE_WIDTH + TeamEditorPanel.SIDE_PANE_WIDTH + 15;
	private static final int FRAME_HEIGHT = TeamEditorPanel.MAIN_PANE_HEIGHT + 30;

	TeamEditorPanel teamEditorPanel;
	
	public TeamEditorGUI()
	{
		this(null);
	}
	
	public TeamEditorGUI(Team team)
	{
		setTitle("Crush! Super Deluxe Team Editor");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		setResizable(false);
		
		teamEditorPanel = new TeamEditorPanel(this, team);
		
		setContentPane(teamEditorPanel);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		//TODO: handle exiting 
	}
}