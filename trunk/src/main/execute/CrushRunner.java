package main.execute;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JFrame;

import main.data.TeamLoader;
import main.data.entities.Equipment;
import main.logic.Client;
import main.logic.Server;
import main.presentation.QuickStartupScreen;
import main.presentation.startupscreen.FullStartupScreen;
import main.presentation.teameditor.TeamEditorGUI;

public class CrushRunner
{
	private static Server host;
	private static QuickStartupScreen quickStartupScreen;
	private static JFrame teamEditor;

	private static ActionListener actionListener;
	private static WindowListener windowListener;

	public static void main(String[] args)
	{
//		TeamLoader.loadTeamFromFile(new File("C:\\Users\\Jared\\Documents\\Crush! Teams\\test teams\\team_ice_year_17.tme"));
//		TeamLoader.loadTeamFromFile(new File("C:\\Users\\Jared\\Documents\\Crush! Teams\\test teams\\team_ice.tme"));
//		TeamLoader.loadTeamFromFile(new File("C:\\Users\\Jared\\Documents\\Crush! Teams\\test teams\\test2c.tme"));
		
		
		defineActionListener();
		defineWindowListener();

		Equipment.defineEquipment();

		java.awt.EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				quickStartupScreen = new QuickStartupScreen(actionListener);
//				FullStartupScreen startupScreen = new FullStartupScreen();
			}
		});
		
	}

	private static void startNewGame()
	{
		quickStartupScreen.setVisible(false);

		//TODO: all of this should be moved to StartupScreen
		host = new Server();
		new Client(host, actionListener);

		host.newGame(quickStartupScreen.getTeams());
	}

	private static void startTeamEditor()
	{
		quickStartupScreen.setVisible(false);

		teamEditor = new TeamEditorGUI();
		teamEditor.addWindowListener(windowListener);
	}

	private static void defineActionListener()
	{
		actionListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				if (event.getActionCommand().equals(Client.ACTION_GAME_START))
				{
					if (quickStartupScreen.readyToStart())
						startNewGame();
				} else if (event.getActionCommand().equals("edit"))
				{
					startTeamEditor();
				} else if (event.getActionCommand().equals(Client.ACTION_GAME_END))
				{
					System.out.println("CrushRunner knows that the game is done!");
					host.endGame();	//TODO: pretty sure this is backwards
					quickStartupScreen.updateTeams(host.getData().getTeams());
					quickStartupScreen.setVisible(true);
				}
			}
		};
	}

	private static void defineWindowListener()
	{
		windowListener = new WindowListener()
		{

			@Override
			public void windowActivated(WindowEvent arg0)
			{
				return;
			}

			@Override
			public void windowClosed(WindowEvent arg0)
			{
				quickStartupScreen.setVisible(true);
			}

			@Override
			public void windowClosing(WindowEvent arg0)
			{
				return;
			}

			@Override
			public void windowDeactivated(WindowEvent arg0)
			{
				return;
			}

			@Override
			public void windowDeiconified(WindowEvent arg0)
			{
				return;
			}

			@Override
			public void windowIconified(WindowEvent arg0)
			{
				return;
			}

			@Override
			public void windowOpened(WindowEvent arg0)
			{
				return;
			}

		};
	}
}
