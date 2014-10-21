package main.presentation;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class StartupScreen extends JFrame implements ActionListener
{
	private static final long serialVersionUID = -3154049402344425271L;

	private static final String team0 = "Red";
	private static final String team1 = "Green";
	private static final String team2 = "Blue";

	private static final String NO_TEAM_NAME = "(choose a team)";

	private JPanel teamPane;
	private JPanel controlPane;

	private JTextField[] teamName;
	private JButton[] teamLoader;
	private boolean[] isCpu = { false, false, false };

	private List<String> teamNames;
	private Map<String, Integer> teamMappings;

	private ActionListener parentActionListener;

	public StartupScreen(ActionListener actionListener)
	{
		super("Crush! Super Deluxe");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		parentActionListener = actionListener;

		teamMappings = createTeamMappings();
		teamNames = generateRandomTeamNames();

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

		mappings.put(team0, 0);
		mappings.put(team1, 1);
		mappings.put(team2, 2);

		return mappings;
	}

	private JPanel createTeamPane()
	{
		JPanel panel = new JPanel();

		teamName = new JTextField[3];
		teamName[0] = createTeamNameField(team0);
		teamName[1] = createTeamNameField(team1);
		teamName[2] = createTeamNameField(team2);

		teamLoader = new JButton[3];
		teamLoader[0] = createButton(team0 + "Load", "Load Team...");
		teamLoader[1] = createButton(team1 + "Load", "Load Team...");
		teamLoader[2] = createButton(team2 + "Load", "Load Team...");

		panel.add(createTeamSelectPane(team0, teamName[0], teamLoader[0]));
		panel.add(createTeamSelectPane(team1, teamName[1], teamLoader[1]));
		panel.add(createTeamSelectPane(team2, teamName[2], teamLoader[2]));

		return panel;
	}

	private JPanel createTeamSelectPane(String teamColor, JTextField teamNameField, JButton teamLoaderButton)
	{
		JPanel panel = new JPanel();

		TitledBorder border = new TitledBorder(new EtchedBorder(), teamColor + " Team", TitledBorder.CENTER, TitledBorder.BELOW_TOP);
		border.setTitleColor(Color.black);
		panel.setBorder(border);

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(createEnableCheckBox(teamColor));
		panel.add(teamNameField);
		panel.add(teamLoaderButton);

		return panel;
	}

	private JTextField createTeamNameField(String teamColor)
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
		panel.add(createButton("game", "Start Game!"));

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
		checkBox.setActionCommand(teamColor + "Cpu");
		checkBox.addActionListener(this);

		return checkBox;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();

		if (command.endsWith("Cpu"))
			handleCpuToggle(trimCommandToColor(command, 3));
		else if (command.endsWith("Load"))
			handleLoadTeam(trimCommandToColor(command, 4));
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
			teamName[teamIndex].setText(getRandomTeamName());
			teamLoader[teamIndex].setEnabled(false);
		} else
		{
			teamName[teamIndex].setText(NO_TEAM_NAME);
			teamLoader[teamIndex].setEnabled(true);
		}
	}

	private void handleLoadTeam(String team)
	{
		// TODO: bring up a JFileChooser
		// team files will be *.csdt (Crush! Super Deluxe Team)
		// ultimately try to make it backwards compatible with *.tme files
		return;
	}

	private String getRandomTeamName()
	{
		Random r = new Random();
		int index = r.nextInt(teamNames.size());
		return normalizeTeamName(teamNames.get(index));
	}

	private String normalizeTeamName(String name)
	{
		if (name.isEmpty())
			return "";

		String normalizedName = "_" + name;

		while (normalizedName.contains("_"))
		{
			int index = normalizedName.indexOf("_");
			if (index == normalizedName.length() - 1)
			{
				normalizedName = normalizedName.substring(0, index);
				break;
			}

			String half1 = normalizedName.substring(0, index);
			String half2 = normalizedName.substring(index + 2);
			String nextChar = normalizedName.substring(index + 1, index + 2);

			normalizedName = half1 + " " + nextChar.toUpperCase() + half2;
		}

		return normalizedName.substring(1);
	}

	private List<String> generateRandomTeamNames()
	{
		List<String> names = new ArrayList<String>();

		names.add("Maulers");
		names.add("Friends_of_8");
		names.add("Symphonics");
		names.add("Rat_Pack");
		names.add("spyre_jumpers");
		names.add("Happy_Troop");
		names.add("Freak_Show");
		names.add("Centrifuge");
		names.add("Typhoons");
		names.add("Tornados");
		names.add("Volcanos");
		names.add("Earthquakes");
		names.add("Lightnings");
		names.add("The_Thunder");
		names.add("Hail_Storm");
		names.add("Marshmallows");
		names.add("Hybrids");
		names.add("Bad_Guys");
		names.add("Good_Guys");
		names.add("Chaos_Lords");
		names.add("Zygoats");
		names.add("Rippers");
		names.add("Daemons");
		names.add("Devils");
		names.add("Angels");
		names.add("Judicators");
		names.add("Paladins");
		names.add("Vipers");
		names.add("Fruit_Cakes");
		names.add("Mind_Blasters");
		names.add("Old_Men");
		names.add("Heroes");
		names.add("Titans");
		names.add("Stone_Jackals");
		names.add("Knights");
		names.add("Seraphims");
		names.add("Spiral_Doom");
		names.add("Doom_Bringers");
		names.add("Nightmares");
		names.add("Jelly_Rolls");
		names.add("Ninjas");
		names.add("Shoguns");
		names.add("The_Nukes");
		names.add("Furies");
		names.add("Olympians");
		names.add("Mashers");
		names.add("Gut_Eaters");
		names.add("Rotting_Death");
		names.add("Silent_Death");
		names.add("The_Cult");

		return names;
	}
}
