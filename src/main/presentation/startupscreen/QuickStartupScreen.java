package main.presentation.startupscreen;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import main.data.TeamLoader;
import main.data.entities.Team;
import main.data.factory.CpuTeamFactory;
import main.logic.Client;
import main.presentation.teameditor.TeamEditorGUI;
import main.presentation.teameditor.common.TeamFileFilter;

public class QuickStartupScreen extends JFrame implements ActionListener
{
	private static final String ACTION_EDIT_TEAM = "Edit";

	private static final String ACTION_LOAD_TEAM = "Load";

	private static final String ACTION_TOGGLE_CPU = "Cpu";

	private static final long serialVersionUID = -3154049402344425271L;

	private static final String TEAM_0 = "Red";
	private static final String TEAM_1 = "Green";
	private static final String TEAM_2 = "Blue";

	private static final String NO_TEAM_NAME = "(choose a team)";

	private JFileChooser fileChooser;

	private JPanel teamPane;
	private JPanel controlPane;

	private JTextField[] teamName;
	private JButton[] teamLoader;
	private JButton[] teamEditer;
	private boolean[] isCpu = { false, false, false };
	private Team[] teams;

	private int budget = 900;
	private boolean readyToStart = false;

	private Map<String, Integer> teamMappings;

	private ActionListener parentActionListener;

	public QuickStartupScreen(ActionListener actionListener)
	{
		super("Crush! Super Deluxe");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		parentActionListener = actionListener;

		teamMappings = createTeamMappings();
		teams = new Team[3];

		fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(new TeamFileFilter());

		teamPane = createTeamPane();
		controlPane = createControlPane();

		Container contentPane = getContentPane();

		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.add(teamPane);
		contentPane.add(controlPane);

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private Map<String, Integer> createTeamMappings()
	{
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		mappings.put(TEAM_0, 0);
		mappings.put(TEAM_1, 1);
		mappings.put(TEAM_2, 2);

		return mappings;
	}

	private JPanel createTeamPane()
	{
		JPanel panel = new JPanel();

		teamName = new JTextField[3];
		teamName[0] = createTeamNameField();
		teamName[1] = createTeamNameField();
		teamName[2] = createTeamNameField();

		teamLoader = new JButton[3];
		teamLoader[0] = createButton(TEAM_0 + ACTION_LOAD_TEAM, "Load...");
		teamLoader[1] = createButton(TEAM_1 + ACTION_LOAD_TEAM, "Load...");
		teamLoader[2] = createButton(TEAM_2 + ACTION_LOAD_TEAM, "Load...");
		
		teamEditer = new JButton[3];
		teamEditer[0] = createButton(TEAM_0 + ACTION_EDIT_TEAM, "Edit...");
		teamEditer[1] = createButton(TEAM_1 + ACTION_EDIT_TEAM, "Edit...");
		teamEditer[2] = createButton(TEAM_2 + ACTION_EDIT_TEAM, "Edit...");

		panel.add(createTeamSelectPane(TEAM_0, teamName[0], teamLoader[0], teamEditer[0]));
		panel.add(createTeamSelectPane(TEAM_1, teamName[1], teamLoader[1], teamEditer[1]));
		panel.add(createTeamSelectPane(TEAM_2, teamName[2], teamLoader[2], teamEditer[2]));

		return panel;
	}

	private JPanel createTeamSelectPane(String teamColor, JTextField teamNameField, JButton teamLoaderButton, JButton teamEditerButton)
	{
		JPanel panel = new JPanel();
		JPanel buttonPane = new JPanel();
		
		buttonPane.add(teamLoaderButton);
		buttonPane.add(teamEditerButton);

		TitledBorder border = new TitledBorder(new EtchedBorder(), teamColor + " Team", TitledBorder.CENTER, TitledBorder.BELOW_TOP);
		border.setTitleColor(Color.black);
		panel.setBorder(border);

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(createEnableCheckBox(teamColor));
		panel.add(teamNameField);
		panel.add(buttonPane);

		return panel;
	}

	private JTextField createTeamNameField()
	{
		JTextField nameField = new JTextField(9);

		nameField.setEditable(false);
		nameField.setText(NO_TEAM_NAME);

		return nameField;
	}

	private JPanel createControlPane()
	{
		JPanel panel = new JPanel();

		panel.add(createButton("edit", "Team Editor"));
		panel.add(createButton(Client.ACTION_GAME_START, "Start Game!"));

		return panel;
	}

	private JButton createButton(String actionCommand, String buttonText)
	{
		JButton button = new JButton(buttonText);

		button.setActionCommand(actionCommand);
		button.addActionListener(this);
		button.addActionListener(parentActionListener);

		return button;
	}

	private JCheckBox createEnableCheckBox(String teamColor)
	{
		JCheckBox checkBox = new JCheckBox();

		checkBox.setText("CPU");
		checkBox.setSelected(false);
		checkBox.setActionCommand(teamColor + ACTION_TOGGLE_CPU);
		checkBox.addActionListener(this);

		return checkBox;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();

		if (command.endsWith(ACTION_TOGGLE_CPU))
			handleCpuToggle(trimCommandToColor(command, ACTION_TOGGLE_CPU.length()));
		else if (command.endsWith(ACTION_LOAD_TEAM))
			handleLoadTeam(trimCommandToColor(command, ACTION_LOAD_TEAM.length()));
		else if (command.endsWith(ACTION_EDIT_TEAM))
			handleEditTeam(trimCommandToColor(command, ACTION_EDIT_TEAM.length()));
	}

	private String trimCommandToColor(String command, int commandLength)
	{
		return command.substring(0, command.length() - commandLength);
	}

	private void handleCpuToggle(String team)
	{
		int teamIndex = teamMappings.get(team);
		isCpu[teamIndex] = !isCpu[teamIndex];

		if (isCpu[teamIndex])
		{
			Team cpuTeam = CpuTeamFactory.generateCpuTeam(budget);
			teamName[teamIndex].setText(cpuTeam.teamName);
			teamLoader[teamIndex].setEnabled(false);
			teams[teamIndex] = cpuTeam;
		} else
		{
			teamName[teamIndex].setText(NO_TEAM_NAME);
			teamLoader[teamIndex].setEnabled(true);
			teams[teamIndex] = null;
		}
	}

	private void handleLoadTeam(String teamColor)
	{
		int teamIndex = teamMappings.get(teamColor);

		Team team = null;

		int returnValue = fileChooser.showOpenDialog(this);

		if (returnValue == JFileChooser.APPROVE_OPTION)
		{
			File file = fileChooser.getSelectedFile();
			team = TeamLoader.loadTeamFromFile(file);
		}

		fileChooser.setSelectedFile(null);

		if (team != null)
		{
			teams[teamIndex] = team;
			teamName[teamIndex].setText(team.teamName);
		}
	}

	private void handleEditTeam(String teamColor)
	{
		int teamIndex = teamMappings.get(teamColor);
		
		if (teams[teamIndex] == null)
		{
			JOptionPane.showMessageDialog(null, "Please specify a team to edit.", "Team Missing", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		new TeamEditorGUI(teams[teamIndex]);
		// Yay Java! Since the teams are passed by reference, any changes made to them are reflected on the screen here.
	}

	private void checkGameStartConditions()
	{
		boolean budgetOkay = true;
		boolean cpuOkay = true;
		
		String overBudget = "";
		
		for (int i = 0; i < 3; i++)
		{
			Team team = teams[i];
			
			if (team == null)
			{
				cpuOkay = false;
			}
			else if	(team.getValue() > budget)
			{
				budgetOkay = false;
				overBudget = overBudget + "\n" + team.teamName;
			}
		}
		
		if (!cpuOkay)
		{
			JOptionPane.showMessageDialog(null, "Please choose a team to load for each slot, or check the CPU box.", "Teams Missing", JOptionPane.WARNING_MESSAGE);
		}
		else if (!budgetOkay)
		{
			JOptionPane.showMessageDialog(null, "The following teams are over budget:" + overBudget, "Teams Over Budget", JOptionPane.WARNING_MESSAGE);
		}
		else
		{
			readyToStart = true;
		}
	}

	public boolean readyToStart()
	{
		checkGameStartConditions();
		
		return readyToStart;
	}
	
	public List<Team> getTeams()
	{
		return new ArrayList<Team>(Arrays.asList(teams));
	}
	
	public void updateTeams(List<Team> newTeams)
	{
		for (int i = 0; i < 3; i++)
			teams[i] = newTeams.get(i);
	}
}
