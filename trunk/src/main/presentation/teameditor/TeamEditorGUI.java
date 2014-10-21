package main.presentation.teameditor;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
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
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.metal.MetalToggleButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import main.data.entities.Player;
import main.data.entities.Team;
import main.presentation.teameditor.utils.GUIPlayerAttributes;
import main.presentation.teameditor.utils.SkillButtonValidator;
import main.presentation.teameditor.utils.TeamFileFilter;
import main.presentation.teameditor.utils.TeamUpdater;

public class TeamEditorGUI extends JFrame implements ActionListener, MouseListener, ListSelectionListener, TableModelListener
{
	private static final long serialVersionUID = -4815838996578005455L;

	private TeamUpdater teamUpdater;
	private SkillButtonValidator skillButtonValidator;

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

	private JLabel playerNameLabel;
	private JLabel playerRankLabel;
	private JLabel playerSeasonsLabel;
	private JLabel playerRatingLabel;
	private JLabel playerStatusLabel;
	private JLabel playerSkillPointsLabel;
	private PlayerImagePanel rosterPlayerImagePanel;
	private JTable playerAbilityTable;

	private JToggleButton[] skillButtons = new JToggleButton[Player.TOTAL_SKILLS + 1];

	private TeamColorPanel teamColorPanel;
	private ArenaDisplayPanel arenaDisplayPanel;

	private ColorChooserPanel mainColorChooser;
	private ColorChooserPanel trimColorChooser;

	private PlayerImagePanel draftPlayerImagePanel;

	private static final int COLOR_PANEL_SIZE = 72;
	private static final int PLAYER_IMAGE_SIZE = 160;

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
	private static final int TEAM_PANE_HEIGHT = COLOR_PANEL_SIZE + 10;

	private static final int ROSTER_PANE_X = TEAM_PANE_X;
	private static final int ROSTER_PANE_Y = TEAM_PANE_HEIGHT + 5;
	private static final int ROSTER_PANE_WIDTH = SIDE_PANE_WIDTH;
	private static final int ROSTER_PANE_HEIGHT = 375 - COLOR_PANEL_SIZE;

	private static final int CONTROL_PANE_X = TEAM_PANE_X;
	private static final int CONTROL_PANE_Y = ROSTER_PANE_Y + ROSTER_PANE_HEIGHT + 5;
	private static final int CONTROL_PANE_WIDTH = SIDE_PANE_WIDTH;
	private static final int CONTROL_PANE_HEIGHT = 125;

	private static final int FRAME_WIDTH = MAIN_PANE_WIDTH + SIDE_PANE_WIDTH + 15;
	private static final int FRAME_HEIGHT = MAIN_PANE_HEIGHT + 30;

	private static final int SKILL_BUTTON_WIDTH = 115;
	private static final int SKILL_BUTTON_HEIGHT = 20;
	
	private static final int PLAYER_LABEL_WIDTH = 125;
	private static final int PLAYER_LABEL_HEIGHT = 15;

	private static final Color DARK_BLUE = new Color(20, 20, 200);
	private static final Color LIGHT_BLUE = new Color(70, 120, 220);

	private static final Color DARK_GREEN = new Color(0, 75, 0);
	private static final Color LIGHT_GREEN = Color.GREEN;

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
	private static final String EVENT_SKILL_GAIN = "skill";
	private static final String EVENT_DRAFT = "draft";

	private static final int MAX_VALUE = 900;
	private static final int MAX_TEAM_NAME_LENGTH = 13;
	private static final int MAX_PLAYER_NAME_LENGTH = 8;
	private static final String TEAM_VIEW = "Team View";
	private static final String PLAYER_VIEW = "Player View";

	private int currentPlayerIndex = 0;
	private int draftSelection = 0;

	public TeamEditorGUI()
	{
		setTitle("Crush! Super Deluxe Team Editor");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		setResizable(false);

		teamUpdater = new TeamUpdater();
		skillButtonValidator = new SkillButtonValidator(Player.TOTAL_SKILLS);

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
		teamColorPanel = new TeamHelmetColorPanel(teamUpdater.getMainColor(), teamUpdater.getTrimColor());
		// teamColorPanel = new TeamBoxColorPanel(teamUpdater.getMainColor(), teamUpdater.getTrimColor());
		teamColorPanel.setSize(COLOR_PANEL_SIZE, COLOR_PANEL_SIZE);
		teamColorPanel.setLocation(2, 5);

		JPanel namesPanel = new JPanel();
		namesPanel.setLayout(new BoxLayout(namesPanel, BoxLayout.Y_AXIS));
		namesPanel.setSize(220 - COLOR_PANEL_SIZE, 55);
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
		formatRosterTableColumns(rosterTable);
		rosterTable.getTableHeader().setReorderingAllowed(false);
		rosterTable.getTableHeader().setResizingAllowed(false);
		rosterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rosterTable.getSelectionModel().addListSelectionListener(this);
		rosterTable.getModel().addTableModelListener(this);
		rosterTable.changeSelection(0, 0, false, false);
		refreshRosterTable();

		return new JScrollPane(rosterTable);
	}

	private JPanel createRosterPlayerPane()
	{
		// TODO: keep formatting this

		JPanel panel = new JPanel();

		panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		panel.setLayout(null);

		playerNameLabel = setLabelFontSize(new JLabel(), 18);

		playerNameLabel.setLocation(20, 5);
		playerNameLabel.setSize(PLAYER_IMAGE_SIZE, 20);
		panel.add(playerNameLabel);
		
		JLabel rankTextLabel = createPlayerPaneTextLabel("Rank", playerNameLabel);
		rankTextLabel.setLocation(PLAYER_IMAGE_SIZE + 10, 5);
		panel.add(rankTextLabel);
		
		playerRankLabel = createPlayerPaneValueLabel(rankTextLabel);
		panel.add(playerRankLabel);
		
		JLabel seasonsTextLabel = createPlayerPaneTextLabel("Seasons", playerRankLabel);
		panel.add(seasonsTextLabel);
		
		playerSeasonsLabel = createPlayerPaneValueLabel(seasonsTextLabel);
		panel.add(playerSeasonsLabel);
		
		JLabel ratingTextLabel = createPlayerPaneTextLabel("Rating", playerSeasonsLabel);
		panel.add(ratingTextLabel);
		
		playerRatingLabel = createPlayerPaneValueLabel(ratingTextLabel);
		panel.add(playerRatingLabel);
		
		JLabel statusTextLabel = createPlayerPaneTextLabel("Status", playerRatingLabel);
		panel.add(statusTextLabel);
		
		playerStatusLabel = createPlayerPaneValueLabel(statusTextLabel);
		panel.add(playerStatusLabel);
		
		JLabel skillPointsTextLabel = createPlayerPaneTextLabel("Skill Points", playerStatusLabel);
		panel.add(skillPointsTextLabel);
		
		playerSkillPointsLabel = createPlayerPaneValueLabel(skillPointsTextLabel);
		panel.add(playerSkillPointsLabel);

		rosterPlayerImagePanel = createPlayerImagePanel();
		rosterPlayerImagePanel.setLocation(5, 25);
		panel.add(rosterPlayerImagePanel);

		JScrollPane playerAbilityPane = createPlayerAbilityTablePane();

		playerAbilityPane.setLocation(20, 185);
		playerAbilityPane.setSize(255, 39);

		panel.add(playerAbilityPane);

		return panel;
	}
	
	private JLabel createPlayerPaneTextLabel(String text, JLabel precedingLabel)
	{
		JLabel label = new JLabel(text, SwingConstants.CENTER);
		
		label.setLocation(PLAYER_IMAGE_SIZE + 10, precedingLabel.getY() + 20);
		label.setSize(PLAYER_LABEL_WIDTH, PLAYER_LABEL_HEIGHT);
		
		return label;
	}
	
	private JLabel createPlayerPaneValueLabel(JLabel precedingLabel)
	{
		JLabel label = setLabelFontSize(new JLabel(), 12, false);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setLocation(PLAYER_IMAGE_SIZE + 10, precedingLabel.getY() + PLAYER_LABEL_HEIGHT);
		label.setSize(PLAYER_LABEL_WIDTH, PLAYER_LABEL_HEIGHT);
		return label;
	}

	private JScrollPane createPlayerAbilityTablePane()
	{
		playerAbilityTable = new JTable(new PlayerAbilityTableModel());
		formatPlayerAbilityTableColumns(playerAbilityTable);
		playerAbilityTable.setEnabled(false);
		playerAbilityTable.getTableHeader().setReorderingAllowed(false);
		playerAbilityTable.getTableHeader().setResizingAllowed(false);

		return new JScrollPane(playerAbilityTable);
	}

	private JPanel createRosterPane()
	{
		JPanel panel = new JPanel();

		panel.setBounds(ROSTER_PANE_X, ROSTER_PANE_Y, ROSTER_PANE_WIDTH, ROSTER_PANE_HEIGHT);
		panel.setLayout(new CardLayout());
		panel.add(createRosterPlayerPane(), PLAYER_VIEW);
		panel.add(createRosterTablePane(), TEAM_VIEW);

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

		teamViewButton.doClick();

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
		tabbedPane.setTabPlacement(SwingConstants.BOTTOM);
		tabbedPane.setBounds(MAIN_PANE_X, MAIN_PANE_Y, MAIN_PANE_WIDTH, MAIN_PANE_HEIGHT);

		settingsPane = createSettingsPane();
		equipmentPane = createEquipmentPane();
		statsPane = createStatsPane();
		draftPane = createDraftPane();
		docbotPane = createDocbotPane();
		trainerPane = createTrainerPane();
		// schedulePane = createSchedulePane();

		tabbedPane.add("Settings", settingsPane);
		tabbedPane.add("Draft", draftPane);
		tabbedPane.add("Equipment", equipmentPane);
		tabbedPane.add("Trainer", trainerPane);
		tabbedPane.add("Docbot", docbotPane);
		tabbedPane.add("Stats", statsPane);

		// TODO: figure out out to work a team schedule into this, or if I even have to

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
		String[] arenaNames = { "BRIDGES", "JACKAL'S LAIR", "CRISSICK", "WHIRLWIND", "THE VOID", "OBSERVATORY", "THE ABYSS", "GADEL SPYRE",
				"FULCRUM", "SAVANNA", "BARROW", "MAELSTROM", "VAULT", "NEXUS", "DARKSUN", "BADLANDS", "LIGHTWAY", "EYES", "DARKSTAR",
				"SPACECOM" };

		JComboBox<String> arenaChooserBox = new JComboBox<String>(arenaNames);
		arenaChooserBox.setSelectedIndex(teamUpdater.getHomeField());
		arenaChooserBox.setActionCommand(EVENT_CHANGE_ARENA);
		arenaChooserBox.addActionListener(this);

		return arenaChooserBox;
	}

	private JPanel createDraftPane()
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);

		draftPlayerImagePanel = createPlayerImagePanel();
		draftPlayerImagePanel.setLocation(100, 100);
		panel.add(draftPlayerImagePanel);

		JPanel draftRacePane = createDraftRacePane();
		panel.add(draftRacePane);

		// TODO Auto-generated method stub
		// note that drafting inserts at the current location, bumping everyone else down (bump only happens if last slot is empty)
		// drafting doesn't work if the team is full

		return panel;
	}

	private JPanel createDraftRacePane()
	{
		String[] races = { "Curmian", "Dragoran", "Gronk", "Human", "Kurgan", "Nynax", "Slith", "XJS9000" };
		ButtonGroup group = new ButtonGroup();

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		panel.setSize(85, 197);
		panel.setLocation(10, 25);

		for (int i = 0; i < 8; i++)
			panel.add(createPlayerRadioButton(group, i, races[i]));

		return panel;
	}

	private PlayerImagePanel createPlayerImagePanel()
	{
		PlayerImagePanel panel = new PlayerImagePanel();

		panel.setSize(PLAYER_IMAGE_SIZE, PLAYER_IMAGE_SIZE);

		return panel;
	}

	private JRadioButton createPlayerRadioButton(ButtonGroup group, int index, String name)
	{
		JRadioButton button = new JRadioButton(name);
		button.setActionCommand(EVENT_DRAFT + index);
		button.addActionListener(this);
		group.add(button);

		return button;
	}

	private JPanel createEquipmentPane()
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);

		// TODO Auto-generated method stub

		return panel;
	}

	// TODO: add skill descriptions
	private JPanel createTrainerPane()
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);

		skillButtons[0] = null;

		// Power
		createNewSkillButton(panel, "Terror", Player.SKILL_TERROR, Color.YELLOW, 155, 10,
				"This is the Terror skill.  It costs 100 skill points, and at the start of each turn, there is a 33% chance"
						+ " that all adjacent opposing players collapse.");
		createNewSkillButton(panel, "Charge", Player.SKILL_CHARGE, Color.YELLOW, 50, 35,
				"This is the Charge skill.  It costs 80 skill points, and reduces a check cost to only 10AP.");
		createNewSkillButton(panel, "Juggernaut", Player.SKILL_JUGGERNAUT, Color.ORANGE, 260, 35,
				"This is the Juggernaut skill.  It costs 80 skill points, and prevents a player from being knocked down"
						+ " or pushed from a check unless KO'd, injured, or killed.");
		createNewSkillButton(panel, "Checkmaster", Player.SKILL_CHECKMASTER, Color.YELLOW, 50, 60,
				"This is the Checkmaster skill.  It costs 60 skill points, and increases the CH score of the player by 10.");
		createNewSkillButton(panel, "Vicious", Player.SKILL_VICIOUS, Color.ORANGE, 190, 60,
				"This is the Vicious skill.  It costs 60 skill points, and adds to the injury type of a successful check.");
		createNewSkillButton(panel, "Resilient", Player.SKILL_RESILIENT, Color.RED, 330, 60,
				"This is the Resilient skill.  It costs 60 skill points, and subtracts from the injury type of a successful check.");
		createNewSkillButton(panel, "Tactics", Player.SKILL_TACTICS, Color.YELLOW, 50, 85,
				"This is the Tactics skill.  It costs 40 skill points, and prevents opponents from getting assists on this player.");
		createNewSkillButton(panel, "Brutal", Player.SKILL_BRUTAL, Color.ORANGE, 190, 85,
				"This is the Brutal skill.  It costs 40 skill points, and increases the ST score of the player by 10.");
		createNewSkillButton(panel, "Stalwart", Player.SKILL_STALWART, Color.RED, 330, 85,
				"This is the Stalwart skill.  It costs 40 skill points, and increases the TG score of the player by 10.");
		createNewSkillButton(panel, "Guard", Player.SKILL_GUARD, Color.RED, 330, 110,
				"This is the Guard skill.  It costs 20 skill points, and increases the assist bonus for the player by 150%.");

		// Agility
		createNewSkillButton(panel, "Doomstrike", Player.SKILL_DOOMSTRIKE, DARK_BLUE, 155, 160,
				"This is the Doomstrike skill.  It costs 100 skill points, and gives the player a 16% chance of injuring"
						+ " the opponent during a checking attempt.", true);
		createNewSkillButton(panel, "Fist of Iron", Player.SKILL_FIST_OF_IRON, DARK_BLUE, 155, 185,
				"This is the Fist of Iron skill.  It costs 80 skill points, and gives the player a 16% chance of stunning"
						+ " the opponent during a checking attempt.", true);
		createNewSkillButton(panel, "Strip", Player.SKILL_STRIP, DARK_BLUE, 50, 210,
				"This is the Strip skill.  It costs 60 skill points, and gives the player a 33% chance of stripping the ball"
						+ " out of an adjacent player's hands at the end of each turn.", true);
		createNewSkillButton(panel, "Quickening", Player.SKILL_QUICKENING, LIGHT_BLUE, 260, 210,
				"This is the Quickening skill.  It costs 60 skill points, and increases the AP score of the player by 10.");
		createNewSkillButton(panel, "Scoop", Player.SKILL_SCOOP, DARK_BLUE, 50, 235,
				"This is the Scoop skill.  It costs 40 skill points, and allows the player to pick up the ball without any AP cost.", true);
		createNewSkillButton(panel, "Judo", Player.SKILL_JUDO, LIGHT_BLUE, 190, 235,
				"This is the Judo skill.  It costs 40 skill points, and raises the player's CH to the same level as"
						+ " any opposing player who attempts to check them.");
		createNewSkillButton(panel, "Combo", Player.SKILL_COMBO, Color.CYAN, 330, 235,
				"This is the Combo skill.  It costs 40 skill points, and gives the player two opportunites to reaction check, instead of one.");
		createNewSkillButton(panel, "Juggling", Player.SKILL_JUGGLING, DARK_BLUE, 50, 260,
				"This is the Juggling skill.  It costs 20 skill points, and increases the HD score of the player by 10.", true);
		createNewSkillButton(panel, "Gymnastics", Player.SKILL_GYMNASTICS, LIGHT_BLUE, 190, 260,
				"This is the Gymnastics skill.  It costs 20 skill points, and increases the DA and JP score of the player by 10.");
		createNewSkillButton(panel, "Boxing", Player.SKILL_BOXING, Color.CYAN, 330, 260,
				"This is the Boxing skill.  It costs 20 skill points, and increases the RF score of the player by 10.");

		// Psyche
		createNewSkillButton(panel, "Sensei", Player.SKILL_SENSEI, DARK_GREEN, 120, 320,
				"This is the Sensei skill.  It costs 100 skill points, and makes all skills 10% easier for team members to achieve."
						+ "  Effect not cumulative.", true);
		createNewSkillButton(panel, "Awe", Player.SKILL_AWE, DARK_GREEN, 50, 345,
				"This is the Awe skill.  It costs 80 skill points, and causes opposing players to react"
						+ " only 5% of the time to this player.", true);
		createNewSkillButton(panel, "Healer", Player.SKILL_HEALER, LIGHT_GREEN, 190, 345,
				"This is the Healer skill.  It costs 80 skill points, and gives each player a 2% chance, before each game,"
						+ " of healing all injured attributes.");
		createNewSkillButton(panel, "Leader", Player.SKILL_LEADER, DARK_GREEN, 50, 370,
				"This is the Leader/Hive Overseer skill.  It costs 60 skill points, and all players within"
						+ " 5 tiles receive 5 bonus to their CH.  Hive Overseer adds an additional +1 to Hive Mind effect."
						+ " (Nynax only - replaces Leader skill)", true);
		createNewSkillButton(panel, "Karma", Player.SKILL_KARMA, LIGHT_GREEN, 190, 370,
				"This is the Karma skill.  It costs 60 skill points, and allows a player to cheat death once per season.");
		createNewSkillButton(panel, "Stoic", Player.SKILL_STOIC, DARK_GREEN, 50, 395,
				"This is the Stoic skill.  It costs 40 skill points, and makes a player immune to Terror and Awe.", true);
		createNewSkillButton(panel, "Sly", Player.SKILL_SLY, LIGHT_GREEN, 190, 395,
				"This is the Sly skill.  It costs 40 skill points, and reduces the overall player equipment detection factor by 1/2.");
		createNewSkillButton(panel, "Intuition", Player.SKILL_INTUITION, LIGHT_GREEN, 190, 420,
				"This is the Intuition skill.  It costs 20 skill points, and doubles the player's chance of finding the ball.");

		return panel;
	}

	private JPanel createDocbotPane()
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);

		// TODO Auto-generated method stub

		return panel;
	}

	private JPanel createStatsPane()
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);

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

	private JToggleButton createNewSkillButton(JPanel parent, String text, int index, final Color color, int x, int y, String skillDesc)
	{
		return createNewSkillButton(parent, text, index, color, x, y, skillDesc, false);
	}

	private JToggleButton createNewSkillButton(JPanel parent, String text, int index, final Color color, int x, int y, String skillDesc,
			boolean whiteText)
	{
		JToggleButton button = new JToggleButton(text);

		button.setSize(SKILL_BUTTON_WIDTH, SKILL_BUTTON_HEIGHT);
		button.setLocation(x, y);
		button.setActionCommand(EVENT_SKILL_GAIN + index);
		button.addActionListener(this);
		button.setBackground(color);
		button.setToolTipText(skillDesc);

		if (whiteText)
			button.setForeground(Color.WHITE);

		button.setUI(new MetalToggleButtonUI()
		{
			@Override
			protected Color getSelectColor()
			{
				return color.darker();
			}
		});

		skillButtons[index] = button;
		parent.add(button);

		return button;
	}

	private JLabel setLabelFontSize(JLabel label, int fontSize)
	{
		return setLabelFontSize(label, fontSize, true);
	}

	private JLabel setLabelFontSize(JLabel label, int fontSize, boolean isBold)
	{
		Font labelFont = label.getFont();
		label.setFont(new Font(labelFont.getName(), labelFont.getStyle(), fontSize));

		if (!isBold)
		{
			labelFont = label.getFont();
			label.setFont(labelFont.deriveFont(labelFont.getStyle() ^ Font.BOLD));
		}

		return label;
	}

	private void formatRosterTableColumns(JTable unformattedRosterTable)
	{
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

		TableColumn column = null;
		for (int i = 0; i < 5; i++)
		{
			column = unformattedRosterTable.getColumnModel().getColumn(i);
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

	private void formatPlayerAbilityTableColumns(JTable unformattedPlayerAbilityTable)
	{
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

		// TODO: figure out how to make this truly centered
		// ((DefaultTableCellRenderer) playerAbilityTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

		TableColumn column = null;
		for (int i = 0; i < 9; i++)
		{
			column = unformattedPlayerAbilityTable.getColumnModel().getColumn(i);

			column.setCellRenderer(centerRenderer);

			if (i == 8)
			{
				column.setMaxWidth(45);
				column.setMinWidth(45);
			} else
			{
				column.setMaxWidth(26);
				column.setMinWidth(26);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent event)
	{
		Component eventComponent = event.getComponent();

		// System.out.println(eventComponent.getClass());

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

		updateSkillsPanel();
		refreshPlayerPane();
		// TODO: call method to update equipment panel

		// System.out.println("Row is " + currentPlayerIndex);
	}

	@Override
	public void tableChanged(TableModelEvent event)
	{
		int row = event.getFirstRow();
		int col = event.getColumn();
		RosterTableModel model = (RosterTableModel) event.getSource();

		if (col == 1)	//TODO: only do this if there's a player here; probably disable/enable the row (elsewhere) based on if there's a player
			setPlayerName(row, (String) model.getValueAt(row, col));

		// System.out.println("Table event! Row is " + event.getFirstRow() + " and column is " + event.getColumn() + ".  " + event.getType() + "; " +
		// event.getSource());
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();

		if (command.equals(EVENT_CHANGE_COACH))
		{
			String name = sanitizeString(settingsCoachField.getText(), MAX_TEAM_NAME_LENGTH);
			teamUpdater.setTeamCoach(name);
			refreshTeamCoach();
		} else if (command.equals(EVENT_CHANGE_NAME))
		{
			String name = sanitizeString(settingsNameField.getText(), MAX_TEAM_NAME_LENGTH);
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
			JRadioButton eventSource = (JRadioButton) event.getSource();
			selectView(eventSource.getText());
		} else if (command.startsWith(EVENT_SKILL_GAIN))
		{
			int skillIndex = Integer.parseInt(command.substring(EVENT_SKILL_GAIN.length()));
			// System.out.println("Skill clicked with value of " + skillIndex);
			gainSkill(skillIndex);
		} else if (command.startsWith(EVENT_DRAFT))
		{
			draftSelection = Integer.parseInt(command.substring(EVENT_DRAFT.length()));
			setDraftImage();
		}
	}

	public void gainSkill(int skillIndex)
	{
		Player player = teamUpdater.getPlayer(currentPlayerIndex);
		player.gainSkill(skillIndex);
		updateSkillsPanel();
		refreshPlayerPane();
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
		refreshPlayerPane();
	}

	private void selectView(String cardName)
	{
		CardLayout cl = (CardLayout) (rosterPane.getLayout());
		cl.show(rosterPane, cardName);
	}

	private String sanitizeString(String string, int maxLength)
	{
		String sanitizedString = string;

		sanitizedString = sanitizedString.replace(";", "");
		sanitizedString = sanitizedString.replace("[", "");
		sanitizedString = sanitizedString.replace("]", "");
		sanitizedString = sanitizedString.replace(",", "");
		sanitizedString = sanitizedString.replace("<", "");
		sanitizedString = sanitizedString.replace(">", "");

		if (sanitizedString.length() > maxLength)
			sanitizedString = sanitizedString.substring(0, maxLength);

		return sanitizedString.toUpperCase();
	}

	private void setPlayerName(int index, String name)
	{
		Player player = teamUpdater.getPlayer(index);
		if (player == null)
			return;
		if (player.name.equals(name))
			return;

		player.name = sanitizeString(name, MAX_PLAYER_NAME_LENGTH);
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
		String name = GUIPlayerAttributes.getNameEmpty(player);
		String rank = GUIPlayerAttributes.getRank(player);
		String race = GUIPlayerAttributes.getRace(player);
		String value = GUIPlayerAttributes.getValue(player);

		rosterTable.getModel().setValueAt(name, index, 1);
		rosterTable.getModel().setValueAt(rank, index, 2);
		rosterTable.getModel().setValueAt(race, index, 3);
		rosterTable.getModel().setValueAt(value, index, 4);
	}

	private void updateSkillsPanel()
	{
		Player player = teamUpdater.getPlayer(currentPlayerIndex);

		for (int i = 1; i <= Player.TOTAL_SKILLS; i++)
		{
			JToggleButton currentButton = skillButtons[i];
			currentButton.setEnabled(skillButtonValidator.isButtonEnabled(i, player));
			currentButton.setSelected(skillButtonValidator.isButtonSelected(i, player));
		}
	}

	private void setDraftImage()
	{
		draftPlayerImagePanel.updateImage(teamUpdater.getPlayerImage(draftSelection));
	}

	private void setRosterImage()
	{
		Player player = teamUpdater.getPlayer(currentPlayerIndex);

		if (player == null)
		{
			rosterPlayerImagePanel.updateImage(null);
			return;
		}

		rosterPlayerImagePanel.updateImage(teamUpdater.getPlayerImage(player.getRace()));
	}

	private void refreshTeam()
	{
		refreshTeamName();
		refreshTeamCoach();
		refreshMainColor();
		refreshTrimColor();
		refreshArena();
		refreshRosterTable();
		refreshPlayerPane();
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
		refreshPlayerImages();
	}

	private void refreshTrimColor()
	{
		teamColorPanel.setTrimColor(teamUpdater.getTrimColor());
		trimColorChooser.setColor(teamUpdater.getTrimColor());
		refreshPlayerImages();
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

	private void refreshPlayerPane()
	{
		Player player = teamUpdater.getPlayer(currentPlayerIndex);

		String index = "";

		if (currentPlayerIndex < 9)
			index = String.valueOf(currentPlayerIndex + 1);
		else
			index = String.valueOf((char) (currentPlayerIndex + 56));

		String name = GUIPlayerAttributes.getNameBlank(player);
		String rank = GUIPlayerAttributes.getRank(player);
		String seasons = GUIPlayerAttributes.getSeasons(player);
		String rating = GUIPlayerAttributes.getRating(player);
		String status = GUIPlayerAttributes.getStatus(player);
		String skillPoints = GUIPlayerAttributes.getSkillPoints(player);
		String value = GUIPlayerAttributes.getValue(player);

		playerNameLabel.setText(index + ") " + name);
		playerRankLabel.setText(rank);
		playerSeasonsLabel.setText(seasons);
		playerRatingLabel.setText(rating);
		playerStatusLabel.setText(status);
		playerSkillPointsLabel.setText(skillPoints);

		setRosterImage();

		for (int i = 0; i < 8; i++)
		{
			String attribute = GUIPlayerAttributes.getAttribute(player, i);
			playerAbilityTable.getModel().setValueAt(attribute, 0, i);
		}

		playerAbilityTable.getModel().setValueAt(value, 0, 8);

		// TODO: fill this out
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

	private void refreshPlayerImages()
	{
		setDraftImage();
		setRosterImage();
	}
}