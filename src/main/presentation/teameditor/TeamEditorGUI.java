package main.presentation.teameditor;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import main.data.entities.Player;
import main.data.entities.Team;
import main.logic.TeamUpdater;

public class TeamEditorGUI extends JFrame implements ActionListener, MouseListener, ListSelectionListener, TableModelListener
{
	private static final long serialVersionUID = -4815838996578005455L;

	private TeamUpdater teamUpdater;

	private JTabbedPane mainPane;
	private JPanel sidePane;

	private JPanel teamPane;
	private JPanel rosterPane;
	private JPanel controlPane;

	private JPanel settingsPane;
	private JPanel draftPane;
	private JPanel equipmentPane;
	private JPanel trainerPane;
	private JPanel docbotPane;
	private JPanel statsPane;

	private JTextField settingsNameField;
	private JTextField settingsCoachField;

	private JLabel coachLabel;
	private JLabel nameLabel;

	private JTextField teamCost;
	private JTextField teamTreasury;

	private JTable rosterTable;
	private JComboBox<String> arenaChooser;
	private JFileChooser fileChooser;
	private JToggleButton swapButton;

	private TeamColorPanel teamColorPanel;
	private ArenaDisplayPanel arenaDisplayPanel;

	private ColorChooserPanel mainColorChooser;
	private ColorChooserPanel trimColorChooser;

	private static final int MAIN_PANE_X = 0;
	private static final int MAIN_PANE_Y = 0;
	private static final int MAIN_PANE_WIDTH = 500;
	private static final int MAIN_PANE_HEIGHT = 500;

	private static final int SIDE_PANE_X = MAIN_PANE_WIDTH + 5;
	private static final int SIDE_PANE_Y = 0;
	private static final int SIDE_PANE_WIDTH = 300;
	private static final int SIDE_PANE_HEIGHT = MAIN_PANE_HEIGHT;

	private static final int TEAM_PANE_X = 0;
	private static final int TEAM_PANE_Y = 0;
	private static final int TEAM_PANE_WIDTH = SIDE_PANE_WIDTH;
	private static final int TEAM_PANE_HEIGHT = 60;

	private static final int ROSTER_PANE_X = TEAM_PANE_X;
	private static final int ROSTER_PANE_Y = TEAM_PANE_HEIGHT + 5;
	private static final int ROSTER_PANE_WIDTH = SIDE_PANE_WIDTH;
	private static final int ROSTER_PANE_HEIGHT = 325;

	private static final int CONTROL_PANE_X = TEAM_PANE_X;
	private static final int CONTROL_PANE_Y = ROSTER_PANE_Y + ROSTER_PANE_HEIGHT + 5;
	private static final int CONTROL_PANE_WIDTH = SIDE_PANE_WIDTH;
	private static final int CONTROL_PANE_HEIGHT = 125;

	private static final int FRAME_WIDTH = MAIN_PANE_WIDTH + SIDE_PANE_WIDTH + 15;
	private static final int FRAME_HEIGHT = MAIN_PANE_HEIGHT + 30;

	private static final String EVENT_CHANGE_NAME = "changeName";
	private static final String EVENT_CHANGE_COACH = "changeCoach";
	private static final String EVENT_CHANGE_ARENA = "changeArena";
	private static final String EVENT_CHANGE_TRIM_COLOR = "changeTrimColor";
	private static final String EVENT_CHANGE_MAIN_COLOR = "changeMainColor";
	private static final String EVENT_LOAD_TEAM = "loadTeam";
	private static final String EVENT_SAVE_TEAM = "saveTeam";
	private static final String EVENT_PREVIOUS_PLAYER = "previousPlayer";
	private static final String EVENT_NEXT_PLAYER = "nextPlayer";
	private static final String EVENT_TEAM_VIEW = "teamView";
	private static final String EVENT_PLAYER_VIEW = "playerView";

	private static final int MAX_VALUE = 900;
	private static final int MAX_NAME_LENGTH = 13;
	private static final String TEAM_VIEW = "Team View";
	private static final String PLAYER_VIEW = "Player View";

	private int currentPlayerIndex = 0;

	public TeamEditorGUI()
	{
		setTitle("Crush! Super Deluxe Team Editor");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		setResizable(false);

		teamUpdater = new TeamUpdater();

		fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(new TeamFileFilter());

		mainPane = createMainPane();
		sidePane = createSidePane();

		Container contentPane = getContentPane();
		contentPane.setLayout(null);
		contentPane.add(mainPane);
		contentPane.add(sidePane);
		
		refreshTeam();

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private JPanel createSidePane()
	{
		teamPane = createTeamPane();
		rosterPane = createRosterPane();
		controlPane = createControlPane();

		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(SIDE_PANE_X, SIDE_PANE_Y, SIDE_PANE_WIDTH, SIDE_PANE_HEIGHT);

		panel.add(teamPane);
		panel.add(rosterPane);
		panel.add(controlPane);

		return panel;
	}

	private JPanel createTeamPane()
	{
		teamColorPanel = new TeamColorPanel(teamUpdater.getMainColor(), teamUpdater.getTrimColor());
		teamColorPanel.setSize(50, 50);
		teamColorPanel.setLocation(2, 5);

		JPanel namesPanel = new JPanel();
		namesPanel.setLayout(new BoxLayout(namesPanel, BoxLayout.Y_AXIS));
		namesPanel.setSize(170, 55);
		namesPanel.setLocation(teamColorPanel.getLocation().x + teamColorPanel.getSize().width + 3, 10);

		coachLabel = new JLabel("COACH " + teamUpdater.getTeamCoach().toUpperCase());
		nameLabel = new JLabel(teamUpdater.getTeamName().toUpperCase());

		coachLabel = setLabelFontSize(coachLabel, 10);
		nameLabel = setLabelFontSize(nameLabel, 18);

		namesPanel.add(coachLabel);
		namesPanel.add(nameLabel);
		
		swapButton = new JToggleButton("Swap");
		swapButton.setSize(66, 25);
		swapButton.setLocation(TEAM_PANE_WIDTH - swapButton.getSize().width, 18);

		JPanel teamPanel = new JPanel();
		teamPanel.setBounds(TEAM_PANE_X, TEAM_PANE_Y, TEAM_PANE_WIDTH, TEAM_PANE_HEIGHT);
		teamPanel.setLayout(null);

		teamPanel.add(teamColorPanel);
		teamPanel.add(namesPanel);
		teamPanel.add(swapButton);

		return teamPanel;
	}

	private JScrollPane createRosterTablePane()
	{
		rosterTable = new JTable(new RosterTableModel());
		formatTableColumns(rosterTable);
		rosterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rosterTable.getSelectionModel().addListSelectionListener(this);
		rosterTable.getModel().addTableModelListener(this);
		rosterTable.changeSelection(0, 0, false, false);
		refreshRosterTable();

		JScrollPane scrollPane = new JScrollPane(rosterTable);

		return scrollPane;
	}

	private JPanel createRosterPlayerPane()
	{
		JPanel panel = new JPanel();

		panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		return panel;
	}

	private JPanel createRosterPane()
	{
		JPanel panel = new JPanel();

		panel.setBounds(ROSTER_PANE_X, ROSTER_PANE_Y, ROSTER_PANE_WIDTH, ROSTER_PANE_HEIGHT);
		panel.setLayout(new CardLayout());
		panel.add(createRosterTablePane(), TEAM_VIEW);
		panel.add(createRosterPlayerPane(), PLAYER_VIEW);

		return panel;
	}

	private JPanel createControlPane()
	{
		JPanel panel = new JPanel();
		panel.setBounds(CONTROL_PANE_X, CONTROL_PANE_Y, CONTROL_PANE_WIDTH, CONTROL_PANE_HEIGHT);
		panel.setLayout(null);

		JButton previousPlayer = createNewButton("Previous", EVENT_PREVIOUS_PLAYER);
		previousPlayer.setSize(100, 30);
		previousPlayer.setLocation(45, 0);

		JButton nextPlayer = createNewButton("Next", EVENT_NEXT_PLAYER);
		nextPlayer.setSize(100, 30);
		nextPlayer.setLocation(previousPlayer.getLocation().x + 105, 0);

		panel.add(previousPlayer);
		panel.add(nextPlayer);
		panel.add(createRosterViewPanel());
		panel.add(createMoneyPanel());

		return panel;
	}

	private JPanel createRosterViewPanel()
	{
		JRadioButton teamViewButton = new JRadioButton(TEAM_VIEW);
		teamViewButton.setLocation(10, 8);
		teamViewButton.setSize(100, 20);
		teamViewButton.setActionCommand(EVENT_TEAM_VIEW);
		teamViewButton.addActionListener(this);
		teamViewButton.setSelected(true);

		JRadioButton playerViewButton = new JRadioButton(PLAYER_VIEW);
		playerViewButton.setLocation(teamViewButton.getLocation().x, teamViewButton.getLocation().y + 22);
		playerViewButton.setSize(100, 20);
		playerViewButton.setActionCommand(EVENT_PLAYER_VIEW);
		playerViewButton.addActionListener(this);
		
		ButtonGroup viewGroup = new ButtonGroup();
		viewGroup.add(teamViewButton);
		viewGroup.add(playerViewButton);

		JPanel viewPanel = new JPanel();
		viewPanel.setLayout(null);
		viewPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		viewPanel.setSize(120, 60);
		viewPanel.setLocation(26, 35);

		viewPanel.add(teamViewButton);
		viewPanel.add(playerViewButton);

		return viewPanel;
	}

	private JPanel createMoneyPanel()
	{
		JLabel costLabel = new JLabel("Total Cost:");
		costLabel.setSize(60, 20);
		costLabel.setLocation(5, 8);

		JLabel treasuryLabel = new JLabel("Treasury:");
		treasuryLabel.setSize(60, 20);
		treasuryLabel.setLocation(costLabel.getLocation().x + 7, costLabel.getLocation().y + 22);
		
		teamCost = new JTextField();
		teamCost.setEditable(false);
		teamCost.setSize(45, 18);
		teamCost.setLocation(costLabel.getLocation().x + 65, costLabel.getLocation().y + 1);

		teamTreasury = new JTextField();
		teamTreasury.setEditable(false);
		teamTreasury.setSize(45, 18);
		teamTreasury.setLocation(teamCost.getLocation().x, treasuryLabel.getLocation().y + 1);
		
		refreshTeamValue();

		JPanel moneyPanel = new JPanel();
		moneyPanel.setLayout(null);
		moneyPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		moneyPanel.setSize(120, 60);
		moneyPanel.setLocation(150, 35);

		moneyPanel.add(costLabel);
		moneyPanel.add(teamCost);
		moneyPanel.add(treasuryLabel);
		moneyPanel.add(teamTreasury);

		return moneyPanel;
	}

	private JTabbedPane createMainPane()
	{
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
		tabbedPane.setBounds(MAIN_PANE_X, MAIN_PANE_Y, MAIN_PANE_WIDTH, MAIN_PANE_HEIGHT);

		settingsPane = createSettingsPane();
		equipmentPane = createEquipmentPane();
		statsPane = createStatsPane();
		draftPane = createDraftPane();
		docbotPane = createDocbotPane();
		trainerPane = createTrainerPane();
		//schedulePane = createSchedulePane();

		tabbedPane.add("Settings", settingsPane);
		tabbedPane.add("Draft", draftPane);
		tabbedPane.add("Equipment", equipmentPane);
		tabbedPane.add("Trainer", trainerPane);
		tabbedPane.add("Docbot", docbotPane);
		tabbedPane.add("Stats", statsPane);
		
		//TODO: figure out out to work a team schedule into this, or if I even have to

		return tabbedPane;
	}

	private JPanel createSettingsPane()
	{
		JButton saveButton = createNewButton("Save", EVENT_SAVE_TEAM);
		saveButton.setSize(70, 25);
		saveButton.setLocation(284, 225);
		
		JButton loadButton = createNewButton("Load", EVENT_LOAD_TEAM);
		loadButton.setSize(70, 25);
		loadButton.setLocation(saveButton.getLocation().x + 84, saveButton.getLocation().y);

		mainColorChooser = new ColorChooserPanel(teamUpdater.getMainColor(), this, EVENT_CHANGE_MAIN_COLOR);
		mainColorChooser.setSize(20, 20);
		mainColorChooser.setLocation(63, 230);
		
		trimColorChooser = new ColorChooserPanel(teamUpdater.getTrimColor(), this, EVENT_CHANGE_TRIM_COLOR);
		trimColorChooser.setSize(20, 20);
		trimColorChooser.setLocation(mainColorChooser.getLocation().x + 90, mainColorChooser.getLocation().y);
		
		JLabel jerseyLabel = new JLabel("Jersey");
		jerseyLabel.setSize(100, 20);
		jerseyLabel.setLocation(mainColorChooser.getLocation().x + 27, mainColorChooser.getLocation().y - 1);
		
		JLabel trimLabel = new JLabel("Trim");
		trimLabel.setSize(100, 20);
		trimLabel.setLocation(trimColorChooser.getLocation().x + 27, jerseyLabel.getLocation().y);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		
		panel.add(createSettingsNamesPanel());
		panel.add(createArenaPanel());
		panel.add(saveButton);
		panel.add(loadButton);
		panel.add(mainColorChooser);
		panel.add(trimColorChooser);
		panel.add(jerseyLabel);
		panel.add(trimLabel);

		return panel;
	}
	
	private JPanel createSettingsNamesPanel()
	{
		JLabel settingsNameLabel = new JLabel("Team Name");
		settingsNameLabel.setSize(160, 30);
		settingsNameLabel.setLocation(13, 8);
		
		settingsNameField = new JTextField(teamUpdater.getTeamName());
		settingsNameField.setActionCommand(EVENT_CHANGE_NAME);
		settingsNameField.addActionListener(this);
		settingsNameField.setSize(160, 30);
		settingsNameField.setLocation(settingsNameLabel.getLocation().x, settingsNameLabel.getLocation().y + 25);
		
		JLabel settingsCoachLabel = new JLabel("Coach Name");
		settingsCoachLabel.setSize(160, 30);
		settingsCoachLabel.setLocation(settingsNameLabel.getLocation().x, settingsNameField.getLocation().y + 40);

		settingsCoachField = new JTextField(teamUpdater.getTeamCoach());
		settingsCoachField.setActionCommand(EVENT_CHANGE_COACH);
		settingsCoachField.addActionListener(this);
		settingsCoachField.setSize(160, 30);
		settingsCoachField.setLocation(settingsNameLabel.getLocation().x, settingsCoachLabel.getLocation().y + 25);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		panel.setSize(190, 150);
		panel.setLocation(40, 65);
		
		panel.add(settingsNameLabel);
		panel.add(settingsNameField);
		panel.add(settingsCoachLabel);
		panel.add(settingsCoachField);
		
		return panel;
	}
	
	private JPanel createArenaPanel()
	{
		arenaDisplayPanel = new ArenaDisplayPanel(teamUpdater.getHomeField());
		arenaDisplayPanel.setSize(92, 92);
		arenaDisplayPanel.setLocation(47, 15);
		
		arenaChooser = createArenaChooser();
		arenaChooser.setSize(150, 20);
		arenaChooser.setLocation(arenaDisplayPanel.getLocation().x - 27, arenaDisplayPanel.getLocation().y + 100);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		panel.setSize(190, 150);
		panel.setLocation(268, 65);
		
		panel.add(arenaDisplayPanel);
		panel.add(arenaChooser);
		
		return panel;
	}

	private JComboBox<String> createArenaChooser()
	{
		String[] arenaNames = { "BRIDGES", "JACKAL'S LAIR", "CRISSICK", "WHIRLWIND", "THE VOID", "OBSERVATORY", "THE ABYSS", "GADEL SPYRE", "FULCRUM",
				"SAVANNA", "BARROW", "MAELSTROM", "VAULT", "NEXUS", "DARKSUN", "BADLANDS", "LIGHTWAY", "EYES", "DARKSTAR", "SPACECOM" };

		JComboBox<String> arenaChooser = new JComboBox<String>(arenaNames);
		arenaChooser.setSelectedIndex(teamUpdater.getHomeField());
		arenaChooser.setActionCommand(EVENT_CHANGE_ARENA);
		arenaChooser.addActionListener(this);

		return arenaChooser;
	}

	private JPanel createDraftPane()
	{
		JPanel panel = new JPanel();

		// TODO Auto-generated method stub

		return panel;
	}

	private JPanel createEquipmentPane()
	{
		JPanel panel = new JPanel();

		// TODO Auto-generated method stub

		return panel;
	}

	private JPanel createTrainerPane()
	{
		JPanel panel = new JPanel();

		// TODO Auto-generated method stub

		return panel;
	}

	private JPanel createDocbotPane()
	{
		JPanel panel = new JPanel();

		// TODO Auto-generated method stub

		return panel;
	}

	private JPanel createStatsPane()
	{
		JPanel panel = new JPanel();

		// TODO Auto-generated method stub

		return panel;
	}

	private JButton createNewButton(String text, String actionCommand)
	{
		JButton button = new JButton(text);

		button.setActionCommand(actionCommand);
		button.addActionListener(this);

		return button;
	}

	private JLabel setLabelFontSize(JLabel label, int fontSize)
	{
		Font labelFont = label.getFont();
		label.setFont(new Font(labelFont.getName(), labelFont.getStyle(), fontSize));

		return label;
	}

	private void formatTableColumns(JTable rosterTable)
	{
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);

		TableColumn column = null;
		for (int i = 0; i < 5; i++)
		{
			column = rosterTable.getColumnModel().getColumn(i);
			if (i == 0)
			{
				column.setMaxWidth(5);
				column.setCellRenderer(centerRenderer);
			} else if (i == 4)
			{
				column.setMaxWidth(50);
			} else
			{
				column.setMaxWidth(75);
			}
			
			if (i == 1 || i == 3)
				column.setMinWidth(75);
		}
	}

	@Override
	public void mouseClicked(MouseEvent event)
	{
		Component eventComponent = event.getComponent();
		
		//System.out.println(eventComponent.getClass());
		
		if (eventComponent.getClass() != ColorChooserPanel.class)
			return;

		String eventSource = event.getComponent().getName();
		Color newColor = JColorChooser.showDialog(this, "Choose Color", eventComponent.getBackground());

		if (newColor == null)
			return;

		if (eventSource.equals(EVENT_CHANGE_MAIN_COLOR))
		{
			teamUpdater.setMainColor(newColor);
			refreshMainColor();
		} else if (eventSource.equals(EVENT_CHANGE_TRIM_COLOR))
		{
			teamUpdater.setTrimColor(newColor);
			refreshTrimColor();
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{
		return;
	}

	@Override
	public void mouseExited(MouseEvent arg0)
	{
		return;
	}

	@Override
	public void mousePressed(MouseEvent arg0)
	{
		return;
	}

	@Override
	public void mouseReleased(MouseEvent arg0)
	{
		return;
	}

	@Override
	public void valueChanged(ListSelectionEvent event)
	{
		if (event.getValueIsAdjusting())
			return;
		
		ListSelectionModel lsm = (ListSelectionModel) event.getSource();
		int tempIndex = lsm.getLeadSelectionIndex();
		
		if (swapButton.isSelected())
		{
			swapPlayers(currentPlayerIndex, tempIndex);
			swapButton.setSelected(false);
		}
		
		currentPlayerIndex = tempIndex;

		//System.out.println("Row is " + currentPlayerIndex);
	}

	@Override
	public void tableChanged(TableModelEvent event)
	{
		int row = event.getFirstRow();
		int col = event.getColumn();
		RosterTableModel model = (RosterTableModel) event.getSource();
		
		if (col == 1)
			setPlayerName(row, (String) model.getValueAt(row, col));
		
		//System.out.println("Table event! Row is " + event.getFirstRow() + " and column is " + event.getColumn() + ".  " + event.getType() + "; " + event.getSource());
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();

		if (command.equals(EVENT_CHANGE_COACH))
		{
			String name = sanitizeString(settingsCoachField.getText());
			teamUpdater.setTeamCoach(name);
			refreshTeamCoach();
		} else if (command.equals(EVENT_CHANGE_NAME))
		{
			String name = sanitizeString(settingsNameField.getText());
			teamUpdater.setTeamName(name);
			refreshTeamName();
		} else if (command.equals(EVENT_CHANGE_ARENA))
		{
			teamUpdater.setHomeField(arenaChooser.getSelectedIndex());
			refreshArena();
		} else if (command.equals(EVENT_SAVE_TEAM))
		{
			saveTeam();
		} else if (command.equals(EVENT_LOAD_TEAM))
		{
			loadTeam();
		} else if (command.equals(EVENT_PREVIOUS_PLAYER))
		{
			selectPlayer(currentPlayerIndex - 1);

		} else if (command.equals(EVENT_NEXT_PLAYER))
		{
			selectPlayer(currentPlayerIndex + 1);
		} else if (command.equals(EVENT_TEAM_VIEW) || command.equals(EVENT_PLAYER_VIEW))
		{
			JRadioButton eventSource = (JRadioButton)event.getSource(); 
			selectView(eventSource.getText());
		}
	}

	private void saveTeam()
	{
		int returnValue = fileChooser.showSaveDialog(this);

		if (returnValue == JFileChooser.APPROVE_OPTION)
		{
			File file = fileChooser.getSelectedFile();
			teamUpdater.saveTeam(file);
		}

		fileChooser.setSelectedFile(null);
	}

	private void loadTeam()
	{
		int returnValue = fileChooser.showOpenDialog(this);

		if (returnValue == JFileChooser.APPROVE_OPTION)
		{
			File file = fileChooser.getSelectedFile();
			teamUpdater.loadTeam(file);
		}

		fileChooser.setSelectedFile(null);
		refreshTeam();
	}

	private void selectPlayer(int index)
	{
		if (index < 0 || index == Team.MAX_TEAM_SIZE)
			return;

		rosterTable.changeSelection(index, 0, false, false);
	}
	
	private void selectView(String cardName)
	{
		CardLayout cl = (CardLayout)(rosterPane.getLayout());
        cl.show(rosterPane, cardName);
	}
	
	private String sanitizeString(String string)
	{
		string = string.replace(";", "");
		string = string.replace("[", "");
		string = string.replace("]", "");
		string = string.replace(",", "");
		string = string.replace("<", "");
		string = string.replace(">", "");
		
		if (string.length() > MAX_NAME_LENGTH)
			string = string.substring(0, MAX_NAME_LENGTH);
		
		return string.toUpperCase();
	}
	
	private void setPlayerName(int index, String name)
	{
		Player player = teamUpdater.getPlayer(index);
		if (player == null)
			return;
		if (player.name.equals(name))
			return;
		
		player.name = sanitizeString(name);
		updatePlayer(index, player);
	}
	
	private void swapPlayers(int firstIndex, int secondIndex)
	{
		Player player1 = teamUpdater.getPlayer(firstIndex);
		Player player2 = teamUpdater.getPlayer(secondIndex);
		
		teamUpdater.setPlayer(firstIndex, player2);
		teamUpdater.setPlayer(secondIndex, player1);
		
		refreshRosterTable();
	}

	private void updatePlayer(int index, Player player)
	{
		teamUpdater.setPlayer(index, player);
		updateRosterTableRow(index, player);
	}

	private void updateRosterTableRow(int index, Player player)
	{
		String name = "EMPTY";
		String rank = "";
		String race = "";
		String value = "000K";

		String[] ranks = { "ROOKIE", "REGULAR", "VETERAN", "CHAMPION", "CAPTAIN", "HERO", "LEGEND", "AVATAR" };
		String[] races = { "CURMIAN", "DRAGORAN", "GRONK", "HUMAN", "KURGAN", "NYNAX", "SLITH", "XJS9000" };

		if (player != null)
		{
			name = player.name.toUpperCase();
			rank = ranks[player.getRank()];
			race = races[player.getRace()];
			value = String.valueOf(player.getSalary()) + "K";
			if (value.length() < 4)
				value = "0" + value;
		}

		rosterTable.getModel().setValueAt(name, index, 1);
		rosterTable.getModel().setValueAt(rank, index, 2);
		rosterTable.getModel().setValueAt(race, index, 3);
		rosterTable.getModel().setValueAt(value, index, 4);
	}

	private void refreshTeam()
	{
		refreshTeamName();
		refreshTeamCoach();
		refreshMainColor();
		refreshTrimColor();
		refreshArena();
		refreshRosterTable();
		refreshTeamValue();
	}

	private void refreshTeamName()
	{
		nameLabel.setText(teamUpdater.getTeamName().toUpperCase());
		settingsNameField.setText(teamUpdater.getTeamName().toUpperCase());
	}

	private void refreshTeamCoach()
	{
		coachLabel.setText("COACH " + teamUpdater.getTeamCoach().toUpperCase());
		settingsCoachField.setText(teamUpdater.getTeamCoach().toUpperCase());
	}

	private void refreshMainColor()
	{
		teamColorPanel.setMainColor(teamUpdater.getMainColor());
		mainColorChooser.setColor(teamUpdater.getMainColor());
	}

	private void refreshTrimColor()
	{
		teamColorPanel.setTrimColor(teamUpdater.getTrimColor());
		trimColorChooser.setColor(teamUpdater.getTrimColor());
	}

	private void refreshArena()
	{
		arenaDisplayPanel.setArena(teamUpdater.getHomeField());
		arenaChooser.setSelectedIndex(teamUpdater.getHomeField());
	}

	private void refreshRosterTable()
	{
		for (int i = 0; i < Team.MAX_TEAM_SIZE; i++)
		{
			Player player = teamUpdater.getPlayer(i);
			updateRosterTableRow(i, player);
		}
	}
	
	private void refreshTeamValue()
	{
		teamCost.setForeground(Color.BLACK);
		teamTreasury.setForeground(Color.BLACK);
		
		int value = teamUpdater.getTeamValue();
		int treasury = MAX_VALUE - value;
		
		if (treasury < 0)
		{
			teamTreasury.setForeground(Color.RED);
			treasury *= -1;
		}
		
		String valueString = String.valueOf(value) + "K";
		String treasuryString = String.valueOf(treasury) + "K";
		
		while (valueString.length() < 4)
			valueString = "0" + valueString;
		
		while (treasuryString.length() < 4)
			treasuryString = "0" + treasuryString;
		
		teamCost.setText(valueString);
		teamTreasury.setText(treasuryString);
	}
}
