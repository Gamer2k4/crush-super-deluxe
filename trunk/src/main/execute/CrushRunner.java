package main.execute;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import main.data.entities.Equipment;
import main.data.entities.Player;
import main.data.entities.Team;
import main.logic.Client;
import main.logic.Server;
import main.presentation.StartupScreen;
import main.presentation.teameditor.TeamEditorGUI;

public class CrushRunner
{
	private static Server host;
	private static JFrame startupScreen;
	private static JFrame teamEditor;

	private static ActionListener actionListener;
	private static WindowListener windowListener;

	public static void main(String[] args)
	{
		defineActionListener();
		defineWindowListener();

		Equipment.defineEquipment();

		java.awt.EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				startupScreen = new StartupScreen(actionListener);
			}
		});
	}

	private static void startNewGame()
	{
		//startupScreen.setVisible(false);
		
		host = new Server();
		new Client(host);

		host.newGame(createTeams(), 0);
	}

	private static void startTeamEditor()
	{
		startupScreen.setVisible(false);
		
		teamEditor = new TeamEditorGUI();
		teamEditor.addWindowListener(windowListener);
	}

	private static List<Team> createTeams()
	{
		List<Team> theTeams = new ArrayList<Team>();

		Team team1 = new Team();
		Team team2 = new Team();
		Team team3 = new Team();

		team1.addPlayer(new Player(Player.RACE_HUMAN));
		/*
		team1.addPlayer(new Player(Player.RACE_HUMAN));

		team2.addPlayer(new Player(Player.RACE_CURMIAN));
		team2.addPlayer(new Player(Player.RACE_GRONK));

		team3.addPlayer(new Player(Player.RACE_XJS9000));

		team1.addPlayer(new Player(Player.RACE_DRAGORAN));
		team1.addPlayer(new Player(Player.RACE_DRAGORAN));
		team1.addPlayer(new Player(Player.RACE_DRAGORAN));
		team2.addPlayer(new Player(Player.RACE_XJS9000));
		team2.addPlayer(new Player(Player.RACE_XJS9000));
		team2.addPlayer(new Player(Player.RACE_XJS9000));
		team3.addPlayer(new Player(Player.RACE_XJS9000));
		team3.addPlayer(new Player(Player.RACE_XJS9000));

		team1.addPlayer(new Player(Player.RACE_CURMIAN));
		team1.addPlayer(new Player(Player.RACE_CURMIAN));
		team2.addPlayer(new Player(Player.RACE_CURMIAN));
		team2.addPlayer(new Player(Player.RACE_CURMIAN));
		team3.addPlayer(new Player(Player.RACE_CURMIAN));
		team3.addPlayer(new Player(Player.RACE_CURMIAN));

		team1.getPlayer(0).equipItem(Equipment.EQUIP_REINFORCED_ARMOR);
		team1.getPlayer(1).equipItem(Equipment.EQUIP_HEAVY_ARMOR);
		team1.getPlayer(0).equipItem(Equipment.EQUIP_FIELD_INTEGRITY_BELT);
		team1.getPlayer(2).equipItem(Equipment.EQUIP_INSULATED_BOOTS);

		team2.getPlayer(0).equipItem(Equipment.EQUIP_SAAI_GLOVES);
		team2.getPlayer(1).equipItem(Equipment.EQUIP_SPIKED_ARMOR);
		team2.getPlayer(1).equipItem(Equipment.EQUIP_SPIKED_GLOVES);
		team2.getPlayer(1).equipItem(Equipment.EQUIP_SPIKED_BOOTS);

		team3.getPlayer(0).equipItem(Equipment.EQUIP_SAAI_GLOVES);
		team3.getPlayer(0).equipItem(Equipment.EQUIP_SAAI_BOOTS);
		team3.getPlayer(1).equipItem(Equipment.EQUIP_SAAI_GLOVES);
		team3.getPlayer(1).equipItem(Equipment.EQUIP_SAAI_BOOTS);
		team3.getPlayer(2).equipItem(Equipment.EQUIP_SAAI_GLOVES);
		team3.getPlayer(2).equipItem(Equipment.EQUIP_SAAI_BOOTS);
		
		for (int i = 0; i < 7; i++)
			team1.getPlayer(i).equipItem(Equipment.EQUIP_REPULSOR_GLOVES);
		*/
		
		theTeams.add(team1);
		theTeams.add(team2);
		theTeams.add(team3);

		return theTeams;
	}

	private static void defineActionListener()
	{
		actionListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				if (event.getActionCommand().equals("game"))
				{
					startNewGame();
				} else if (event.getActionCommand().equals("edit"))
				{
					startTeamEditor();
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
				startupScreen.setVisible(true);
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
