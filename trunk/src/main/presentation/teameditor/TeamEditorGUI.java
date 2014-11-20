package main.presentation.teameditor;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
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
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import main.data.entities.Equipment;
import main.data.entities.Player;
import main.data.entities.Team;
import main.data.factory.PlayerFactory;
import main.presentation.teameditor.tablemodels.InjuredPlayersTableModel;
import main.presentation.teameditor.tablemodels.OwnedEquipmentTableModel;
import main.presentation.teameditor.tablemodels.RosterTableModel;
import main.presentation.teameditor.utils.ColorReplacer;
import main.presentation.teameditor.utils.GUIPlayerAttributes;
import main.presentation.teameditor.utils.ImageFactory;
import main.presentation.teameditor.utils.ImageType;
import main.presentation.teameditor.utils.SkillButtonValidator;
import main.presentation.teameditor.utils.TeamFileFilter;
import main.presentation.teameditor.utils.TeamUpdater;

public class TeamEditorGUI extends JFrame implements ActionListener, MouseListener, ListSelectionListener, TableModelListener
{
	private static final long serialVersionUID = -4815838996578005455L;

	public static final Color BG_COLOR = new Color(238, 238, 238);

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

	private JTable injuredPlayersTable;
	private JRadioButton[][] docbotButtons;
	private JLabel docbotCostLabel;

	private JPanel equipmentCardPanel;
	private EquipmentShopPanel equipmentShopPane;
	private EquipmentEquipPanel equipmentEquipPane;
	private JTable ownedEquipmentTable;
	private JTextPane equipmentDescriptionPane;
	private JLabel equipmentNameLabel;
	private JLabel equipmentDetectionLabel;

	private JTextField teamCost;
	private JTextField teamTreasury;

	private JTable rosterTable;
	private JComboBox<String> arenaChooser;
	private JFileChooser fileChooser;
	private JToggleButton swapButton;

	private RosterPlayerInfoPanel rosterPlayerInfoPanel;
	private DraftPlayerInfoPanel draftPlayerInfoPanel;

	private JToggleButton[] skillButtons = new JToggleButton[Player.TOTAL_SKILLS + 1];

	private TeamColorPanel teamColorPanel;
	private ArenaDisplayPanel arenaDisplayPanel;

	private ColorChooserPanel mainColorChooser;
	private ColorChooserPanel trimColorChooser;

	private static final int COLOR_PANEL_SIZE = (int) (ImageFactory.getImageSize(ImageType.HELMET).getHeight());
	private static final int PLAYER_IMAGE_SIZE = (int) (ImageFactory.getImageSize(ImageType.PROFILE_CURMIAN).getHeight());
	private static final int DOCBOT_IMAGE_HEIGHT = (int) (ImageFactory.getImageSize(ImageType.DOCBOT).getHeight());
	private static final int DOCBOT_IMAGE_WIDTH = (int) (ImageFactory.getImageSize(ImageType.DOCBOT).getWidth());

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

	private static final int EQUIP_PANE_X = 10;
	private static final int EQUIP_SIDEBAR_WIDTH = 130;

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

	private static final String ACTION_CHANGE_NAME = "changeName";
	private static final String ACTION_CHANGE_COACH = "changeCoach";
	private static final String ACTION_CHANGE_ARENA = "changeArena";
	private static final String ACTION_CHANGE_TRIM_COLOR = "changeTrimColor";
	private static final String ACTION_CHANGE_MAIN_COLOR = "changeMainColor";
	private static final String ACTION_LOAD_TEAM = "loadTeam";
	private static final String ACTION_SAVE_TEAM = "saveTeam";
	private static final String ACTION_PREVIOUS_PLAYER = "previousPlayer";
	private static final String ACTION_NEXT_PLAYER = "nextPlayer";
	private static final String ACTION_TEAM_VIEW = "teamView";
	private static final String ACTION_PLAYER_VIEW = "playerView";
	private static final String ACTION_SKILL_GAIN = "skill";
	private static final String ACTION_DRAFT = "draft";
	private static final String ACTION_HIRE = "hire";
	private static final String ACTION_FIRE = "fire";
	private static final String ACTION_DOCBOT = "docbot";
	private static final String ACTION_SHOP_VIEW = "shopView";
	private static final String ACTION_EQUIP_VIEW = "equipView";

	private static final int MAX_VALUE = 900;
	private static final int MAX_TEAM_NAME_LENGTH = 13;
	private static final int MAX_PLAYER_NAME_LENGTH = 8;
	private static final String TEAM_VIEW = "Team View";
	private static final String PLAYER_VIEW = "Player View";
	private static final String SHOP_VIEW = "Acquire";
	private static final String EQUIP_VIEW = "Outfit";

	private int currentPlayerIndex = 0;
	private int draftSelection = 0;
	private int equipmentOwnedSelection = 0;
	private boolean inOutfitScreen = false;

	//TODO: make this a singleton
	public TeamEditorGUI()
	{
		this(null);
	}
	
	public TeamEditorGUI(Team team)
	{
		if (team == null)
			teamUpdater = new TeamUpdater();
		else
			teamUpdater = new TeamUpdater(team);
		
		buildPanel();
		
		refreshTeam();
	}
	
	private void buildPanel()
	{
		setTitle("Crush! Super Deluxe Team Editor");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		setResizable(false);

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
	
	//TODO: when using the helmet, make the team name lower so it goes under the Swap button
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
		rosterTable = new JTable(new RosterTableModel())
		{
			private static final long serialVersionUID = -4344474350657252202L;

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
			{
				Component c = super.prepareRenderer(renderer, row, column);
				JComponent jc = (JComponent) c;

				// add custom rendering here
				Player p = teamUpdater.getPlayer(row);

				if (p != null && p.getWeeksOut() > 0)
					jc.setForeground(Color.RED);
				else
					jc.setForeground(Color.BLACK);

				return c;
			}
		};
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

	private JPanel createRosterPane()
	{
		JPanel panel = new JPanel();
		rosterPlayerInfoPanel = new RosterPlayerInfoPanel(PLAYER_IMAGE_SIZE, PLAYER_LABEL_WIDTH, PLAYER_LABEL_HEIGHT);

		panel.setBounds(ROSTER_PANE_X, ROSTER_PANE_Y, ROSTER_PANE_WIDTH, ROSTER_PANE_HEIGHT);
		panel.setLayout(new CardLayout());
		panel.add(rosterPlayerInfoPanel, PLAYER_VIEW);
		panel.add(createRosterTablePane(), TEAM_VIEW);

		return panel;
	}

	private JPanel createControlPane()
	{
		JPanel panel = new JPanel();
		panel.setBounds(CONTROL_PANE_X, CONTROL_PANE_Y, CONTROL_PANE_WIDTH, CONTROL_PANE_HEIGHT);
		panel.setLayout(null);

		JButton previousPlayer = createNewButton("Previous", ACTION_PREVIOUS_PLAYER);
		previousPlayer.setSize(100, 30);
		previousPlayer.setLocation(45, 0);

		JButton nextPlayer = createNewButton("Next", ACTION_NEXT_PLAYER);
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
		teamViewButton.setActionCommand(ACTION_TEAM_VIEW);
		teamViewButton.addActionListener(this);
		teamViewButton.setSelected(true);

		JRadioButton playerViewButton = new JRadioButton(PLAYER_VIEW);
		playerViewButton.setLocation(teamViewButton.getLocation().x, teamViewButton.getLocation().y + 22);
		playerViewButton.setSize(100, 20);
		playerViewButton.setActionCommand(ACTION_PLAYER_VIEW);
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

		tabbedPane.add("Settings", settingsPane);
		tabbedPane.add("Draft", draftPane);
		tabbedPane.add("Equipment", equipmentPane);
		tabbedPane.add("Trainer", trainerPane);
		tabbedPane.add("Docbot", docbotPane);
		tabbedPane.add("Stats", statsPane);

		return tabbedPane;
	}

	private JPanel createSettingsPane()
	{
		JButton saveButton = createNewButton("Save", ACTION_SAVE_TEAM);
		saveButton.setSize(70, 25);
		saveButton.setLocation(284, 225);

		JButton loadButton = createNewButton("Load", ACTION_LOAD_TEAM);
		loadButton.setSize(70, 25);
		loadButton.setLocation(saveButton.getLocation().x + 84, saveButton.getLocation().y);

		mainColorChooser = new ColorChooserPanel(teamUpdater.getMainColor(), this, ACTION_CHANGE_MAIN_COLOR);
		mainColorChooser.setSize(20, 20);
		mainColorChooser.setLocation(63, 230);

		trimColorChooser = new ColorChooserPanel(teamUpdater.getTrimColor(), this, ACTION_CHANGE_TRIM_COLOR);
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
		settingsNameField.setActionCommand(ACTION_CHANGE_NAME);
		settingsNameField.addActionListener(this);
		settingsNameField.setSize(160, 30);
		settingsNameField.setLocation(settingsNameLabel.getLocation().x, settingsNameLabel.getLocation().y + 25);

		JLabel settingsCoachLabel = new JLabel("Coach Name");
		settingsCoachLabel.setSize(160, 30);
		settingsCoachLabel.setLocation(settingsNameLabel.getLocation().x, settingsNameField.getLocation().y + 40);

		settingsCoachField = new JTextField(teamUpdater.getTeamCoach());
		settingsCoachField.setActionCommand(ACTION_CHANGE_COACH);
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
		arenaChooserBox.setActionCommand(ACTION_CHANGE_ARENA);
		arenaChooserBox.addActionListener(this);

		return arenaChooserBox;
	}

	private JPanel createDraftPane()
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);

		JPanel draftRacePane = createDraftRacePane();
		panel.add(draftRacePane);

		draftPlayerInfoPanel = new DraftPlayerInfoPanel(PLAYER_IMAGE_SIZE, PLAYER_LABEL_WIDTH, PLAYER_LABEL_HEIGHT);
		draftPlayerInfoPanel.setLocation(110, 50);
		panel.add(draftPlayerInfoPanel);

		JPanel hirePane = createDraftHirePane();
		panel.add(hirePane);

		return panel;
	}

	private JPanel createDraftRacePane()
	{
		ButtonGroup group = new ButtonGroup();

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		panel.setSize(85, 200);
		panel.setLocation(15, 100);

		for (int i = 0; i < 8; i++)
			panel.add(createPlayerDraftRadioButton(group, i, Player.races[i]));

		group.getElements().nextElement().setSelected(true);

		return panel;
	}

	private JPanel createDraftHirePane()
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		panel.setSize(85, 200);
		panel.setLocation(draftPlayerInfoPanel.getWidth() + 120, 100);

		JButton hireButton = createNewButton("Hire", ACTION_HIRE);
		hireButton.setSize(57, 30);
		hireButton.setLocation(14, 50);

		JButton fireButton = createNewButton("Fire", ACTION_FIRE);
		fireButton.setSize(57, 30);
		fireButton.setLocation(14, hireButton.getY() + 60);

		panel.add(hireButton);
		panel.add(fireButton);

		return panel;
	}

	private JRadioButton createPlayerDraftRadioButton(ButtonGroup group, int index, String name)
	{
		JRadioButton button = new JRadioButton(name);
		button.setActionCommand(ACTION_DRAFT + index);
		button.addActionListener(this);
		group.add(button);

		return button;
	}

	private JPanel createEquipmentPane()
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);

		equipmentShopPane = new EquipmentShopPanel(teamUpdater, this);
		equipmentEquipPane = new EquipmentEquipPanel(this);

		equipmentCardPanel = new JPanel();
		equipmentCardPanel.setLocation(10, EQUIP_PANE_X);
		equipmentCardPanel.setSize(equipmentShopPane.getSize());
		equipmentCardPanel.setLayout(new CardLayout());
		equipmentCardPanel.add(equipmentShopPane, SHOP_VIEW);
		equipmentCardPanel.add(equipmentEquipPane, EQUIP_VIEW);

		panel.add(equipmentCardPanel);
		panel.add(createEquipmentViewPanel());
		panel.add(createOwnedEquipmentTablePane());
		panel.add(createEquipmentDescriptionPanel());
		createAndAddEquipmentLabels(panel);

		// TODO Auto-generated method stub
		// The original game defaults to the outfit screen whenever you return to this panel,
		// but keeps the equipment where you left off if you choose to acquire again.

		return panel;
	}

	private JPanel createEquipmentViewPanel()
	{
		JRadioButton shopViewButton = new JRadioButton(SHOP_VIEW);
		shopViewButton.setLocation(60, 15);
		shopViewButton.setSize(80, 20);
		shopViewButton.setActionCommand(ACTION_SHOP_VIEW);
		shopViewButton.addActionListener(this);
		shopViewButton.setSelected(true);

		JRadioButton equipViewButton = new JRadioButton(EQUIP_VIEW);
		equipViewButton.setLocation(shopViewButton.getLocation().x + 150, shopViewButton.getLocation().y);
		equipViewButton.setSize(60, 20);
		equipViewButton.setActionCommand(ACTION_EQUIP_VIEW);
		equipViewButton.addActionListener(this);

		ButtonGroup viewGroup = new ButtonGroup();
		viewGroup.add(shopViewButton);
		viewGroup.add(equipViewButton);

		JPanel viewPanel = new JPanel();
		viewPanel.setLayout(null);
		viewPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		viewPanel.setSize(equipmentShopPane.getWidth(), 50);
		viewPanel.setLocation(10, equipmentShopPane.getHeight() + EQUIP_PANE_X + 10);

		viewPanel.add(shopViewButton);
		viewPanel.add(equipViewButton);

		shopViewButton.doClick();

		return viewPanel;
	}

	private JScrollPane createOwnedEquipmentTablePane()
	{
		ownedEquipmentTable = new JTable(new OwnedEquipmentTableModel());
		ownedEquipmentTable.getTableHeader().setReorderingAllowed(false);
		ownedEquipmentTable.getTableHeader().setResizingAllowed(false);
		ownedEquipmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		ListSelectionListener lsl = new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent event)
			{
				if (event.getValueIsAdjusting())
					return;

				ListSelectionModel lsm = (ListSelectionModel) event.getSource();
				equipmentOwnedSelection = lsm.getLeadSelectionIndex();

				refreshEquipmentLabels();
			}
		};

		MouseAdapter ma = new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (e.getClickCount() == 2 && inOutfitScreen)
				{
					JTable target = (JTable) e.getSource();
					equipmentOwnedSelection = target.getSelectedRow();
					wearEquipment();
				}
			}
		};

		ownedEquipmentTable.getSelectionModel().addListSelectionListener(lsl);
		ownedEquipmentTable.addMouseListener(ma);

		formatOwnedEquipmentTableColumns(ownedEquipmentTable);

		JScrollPane ownedEquipmentPane = new JScrollPane(ownedEquipmentTable);

		ownedEquipmentPane.setSize(EQUIP_SIDEBAR_WIDTH, 220);
		ownedEquipmentPane.setLocation(equipmentCardPanel.getX() + equipmentCardPanel.getWidth() + 10, equipmentCardPanel.getY());
		ownedEquipmentPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		return ownedEquipmentPane;
	}

	private void formatOwnedEquipmentTableColumns(JTable unformattedOwnedEquipmentTable)
	{
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.LEFT);

		TableColumn column = null;
		for (int i = 0; i < 1; i++)
		{
			column = unformattedOwnedEquipmentTable.getColumnModel().getColumn(i);

			if (i == 0)
			{
				column.setCellRenderer(centerRenderer);
				column.setMaxWidth(EQUIP_SIDEBAR_WIDTH);
				column.setMinWidth(EQUIP_SIDEBAR_WIDTH);
			}
		}
	}

	private void createAndAddEquipmentLabels(JPanel parent)
	{
		equipmentNameLabel = setLabelFontSize(new JLabel(), 12, true);
		equipmentNameLabel.setLocation(equipmentCardPanel.getX() + equipmentCardPanel.getWidth() + 10, EQUIP_PANE_X + 225);
		equipmentNameLabel.setSize(EQUIP_SIDEBAR_WIDTH, 13);
		equipmentNameLabel.setHorizontalAlignment(SwingConstants.CENTER);

		equipmentDetectionLabel = setLabelFontSize(new JLabel(), 12, false);
		equipmentDetectionLabel.setLocation(equipmentNameLabel.getX(), equipmentNameLabel.getY() + 18);
		equipmentDetectionLabel.setSize(equipmentNameLabel.getWidth(), equipmentNameLabel.getHeight());
		equipmentDetectionLabel.setHorizontalAlignment(SwingConstants.CENTER);

		parent.add(equipmentNameLabel);
		parent.add(equipmentDetectionLabel);
	}

	private JPanel createEquipmentDescriptionPanel()
	{
		JPanel descPane = new JPanel();
		descPane.setLayout(null);
		descPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		descPane.setLocation(equipmentCardPanel.getX() + equipmentCardPanel.getWidth() + 10, EQUIP_PANE_X + 260);
		descPane.setSize(EQUIP_SIDEBAR_WIDTH, 90);

		equipmentDescriptionPane = new JTextPane();
		equipmentDescriptionPane.setLocation(5, 5);
		equipmentDescriptionPane.setSize(descPane.getWidth() - 10, descPane.getHeight() - 10);
		equipmentDescriptionPane.setBackground(TeamEditorGUI.BG_COLOR);

		StyledDocument doc = equipmentDescriptionPane.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);

		descPane.add(equipmentDescriptionPane);

		return descPane;
	}

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

		panel.add(createDocbotImagePanel());
		panel.add(createDocbotOptionsPanel());
		panel.add(createDocbotTablePane());

		JLabel costText = setLabelFontSize(new JLabel(), 20, true);
		costText.setSize(100, 20);
		costText.setLocation(340, DOCBOT_IMAGE_HEIGHT + 60);
		costText.setHorizontalAlignment(SwingConstants.CENTER);
		costText.setText("COST:");

		docbotCostLabel = setLabelFontSize(new JLabel(), 20, false);
		docbotCostLabel.setSize(100, 20);
		docbotCostLabel.setLocation(costText.getX(), costText.getY() + 25);
		docbotCostLabel.setHorizontalAlignment(SwingConstants.CENTER);
		docbotCostLabel.setText("0K");

		panel.add(costText);
		panel.add(docbotCostLabel);

		return panel;
	}

	private ImagePanel createDocbotImagePanel()
	{
		ImagePanel panel = new ImagePanel(ImageFactory.getImageSize(ImageType.DOCBOT));
		BufferedImage docbotImage = ColorReplacer.setColors(ImageFactory.getImage(ImageType.DOCBOT), Color.WHITE, Color.WHITE,
				TeamEditorGUI.BG_COLOR);

		panel.updateImage(docbotImage);
		panel.setLocation(50, 35);

		return panel;
	}

	private JPanel createDocbotOptionsPanel()
	{
		docbotButtons = new JRadioButton[4][2];

		JPanel panel = new JPanel();

		panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		panel.setSize(200, DOCBOT_IMAGE_HEIGHT + 1);
		panel.setLocation(70 + DOCBOT_IMAGE_WIDTH, 35);
		panel.setLayout(new GridLayout(8, 1));

		panel.add(setLabelFontSize(new JLabel("EMERGENCY", SwingConstants.CENTER), 18));
		panel.add(createDocbotButtonGroup(0));
		panel.add(setLabelFontSize(new JLabel("SURGERY", SwingConstants.CENTER), 18));
		panel.add(createDocbotButtonGroup(1));
		panel.add(setLabelFontSize(new JLabel("RECOVERY", SwingConstants.CENTER), 18));
		panel.add(createDocbotButtonGroup(2));
		panel.add(setLabelFontSize(new JLabel("THERAPY", SwingConstants.CENTER), 18));
		panel.add(createDocbotButtonGroup(3));

		return panel;
	}

	private JPanel createDocbotButtonGroup(int aidType)
	{
		JPanel panel = new JPanel();

		ButtonGroup group = new ButtonGroup();

		JRadioButton standardButton = new JRadioButton("Standard");
		standardButton.setActionCommand(ACTION_DOCBOT + aidType + "0");
		standardButton.addActionListener(this);
		standardButton.setSelected(true);
		docbotButtons[aidType][0] = standardButton;

		JRadioButton enhancedButton = new JRadioButton("Enhanced");
		enhancedButton.setActionCommand(ACTION_DOCBOT + aidType + "1");
		enhancedButton.addActionListener(this);
		docbotButtons[aidType][1] = enhancedButton;

		group.add(standardButton);
		group.add(enhancedButton);

		panel.add(standardButton);
		panel.add(enhancedButton);

		return panel;
	}

	private JScrollPane createDocbotTablePane()
	{
		injuredPlayersTable = new JTable(new InjuredPlayersTableModel());
		injuredPlayersTable.setEnabled(false);
		injuredPlayersTable.getTableHeader().setReorderingAllowed(false);
		injuredPlayersTable.getTableHeader().setResizingAllowed(false);

		formatDocbotTableColumns(injuredPlayersTable);

		JScrollPane injuryPane = new JScrollPane(injuredPlayersTable);
		injuryPane.setSize(260, 70);
		injuryPane.setLocation(80, DOCBOT_IMAGE_HEIGHT + 50);

		return injuryPane;
	}

	private void formatDocbotTableColumns(JTable unformattedDocbotTable)
	{
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

		TableColumn column = null;
		for (int i = 0; i < 3; i++)
		{
			column = unformattedDocbotTable.getColumnModel().getColumn(i);

			if (i == 0)
			{
				column.setMaxWidth(5);
				column.setCellRenderer(centerRenderer);
			} else if (i == 1)
			{
				column.setMaxWidth(75);
				column.setMinWidth(75);
			}
		}
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
		button.setActionCommand(ACTION_SKILL_GAIN + index);
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

		if (eventSource.equals(ACTION_CHANGE_MAIN_COLOR))
		{
			teamUpdater.setMainColor(newColor);
			refreshMainColor();
		} else if (eventSource.equals(ACTION_CHANGE_TRIM_COLOR))
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
		refreshEquipmentPane();
	}

	@Override
	public void tableChanged(TableModelEvent event)
	{
		int row = event.getFirstRow();
		int col = event.getColumn();

		if (event.getSource() instanceof RosterTableModel)
		{
			RosterTableModel model = (RosterTableModel) event.getSource();

			if (col == 1) // TODO: only do this if there's a player here; probably disable/enable the row (elsewhere) based on if there's a player
				setPlayerName(row, (String) model.getValueAt(row, col));
		}
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();

		if (command.equals(ACTION_CHANGE_COACH))
		{
			String name = sanitizeString(settingsCoachField.getText(), MAX_TEAM_NAME_LENGTH);
			teamUpdater.setTeamCoach(name);
			refreshTeamCoach();
		} else if (command.equals(ACTION_CHANGE_NAME))
		{
			String name = sanitizeString(settingsNameField.getText(), MAX_TEAM_NAME_LENGTH);
			teamUpdater.setTeamName(name);
			refreshTeamName();
		} else if (command.equals(ACTION_CHANGE_ARENA))
		{
			teamUpdater.setHomeField(arenaChooser.getSelectedIndex());
			refreshArena();
		} else if (command.equals(ACTION_SAVE_TEAM))
		{
			saveTeam();
		} else if (command.equals(ACTION_LOAD_TEAM))
		{
			loadTeam();
		} else if (command.equals(ACTION_PREVIOUS_PLAYER))
		{
			selectPlayer(currentPlayerIndex - 1);

		} else if (command.equals(ACTION_NEXT_PLAYER))
		{
			selectPlayer(currentPlayerIndex + 1);
		} else if (command.equals(ACTION_TEAM_VIEW) || command.equals(ACTION_PLAYER_VIEW))
		{
			JRadioButton eventSource = (JRadioButton) event.getSource();
			selectPlayerView(eventSource.getText());
		} else if (command.startsWith(ACTION_SKILL_GAIN))
		{
			int skillIndex = Integer.parseInt(command.substring(ACTION_SKILL_GAIN.length()));
			gainSkill(skillIndex);
		} else if (command.startsWith(ACTION_DRAFT))
		{
			draftSelection = Integer.parseInt(command.substring(ACTION_DRAFT.length()));
			setDraftImage();
		} else if (command.equals(ACTION_HIRE))
		{
			hirePlayer();
		} else if (command.equals(ACTION_FIRE))
		{
			firePlayer();
		} else if (command.equals(EquipmentShopPanel.BUY))
		{
			buyEquipment();
		} else if (command.equals(EquipmentShopPanel.SELL))
		{
			sellEquipment();
		} else if (command.equals(EquipmentEquipPanel.EQUIP_WEAR))
		{
			wearEquipment();
		} else if (command.startsWith(EquipmentEquipPanel.EQUIP_REMOVE))
		{
			removeEquipment(Integer.parseInt(command.substring(EquipmentEquipPanel.EQUIP_REMOVE.length())));
		} else if (command.equals(ACTION_SHOP_VIEW) || command.equals(ACTION_EQUIP_VIEW))
		{
			JRadioButton eventSource = (JRadioButton) event.getSource();
			selectEquipmentView(eventSource.getText());
		} else if (command.startsWith(ACTION_DOCBOT))
		{
			int index = Integer.valueOf("" + command.charAt(6));
			boolean value = command.charAt(7) == '1' ? true : false;
			teamUpdater.setDocbotTreatment(index, value);
			refreshDocbotPane();
			refreshTeamValue();
		}
	}

	private void firePlayer()
	{
		Player player = teamUpdater.getPlayer(currentPlayerIndex);

		// doesn't do anything if there's no one to fire
		if (player == null)
			return;

		// give all the equipment back to the team
		for (int i = 0; i < 4; i++)
		{
			if (player.getEquipment(i) >= 0)
			{
				teamUpdater.addEquipment(player.unequipItem(i));
			}
		}

		// clear the slot
		teamUpdater.setPlayer(currentPlayerIndex, null);

		refreshTeam();
	}

	private void hirePlayer()
	{
		int budget = MAX_VALUE - teamUpdater.getTeamValue();
		int playerCost = new Player(draftSelection, "COST_CHECK").getSalary();

		if (budget < playerCost)
			return;

		boolean canDraft = teamUpdater.pushPlayersForDraft(currentPlayerIndex);

		if (canDraft)
		{
			teamUpdater.setPlayer(currentPlayerIndex, PlayerFactory.createPlayerWithRandomName(draftSelection));

			selectPlayer(currentPlayerIndex + 1);

			refreshTeam();
		}
	}

	private void buyEquipment()
	{
		int equipmentIndex = equipmentShopPane.getSelectedEquipmentIndex();

		int budget = MAX_VALUE - teamUpdater.getTeamValue();
		int equipCost = Equipment.getEquipment(equipmentIndex).cost;

		if (budget < equipCost)
			return;

		boolean canBuy = true; // TODO: determine how to find the cap for this; as it stands, the table can show up to 140 pieces of gear, while the player can
								// only by 90 piece (10K minimum).

		if (canBuy)
		{
			teamUpdater.addEquipment(equipmentIndex);
			refreshTeam();
		}
	}

	private void sellEquipment()
	{
		teamUpdater.removeEquipment(equipmentOwnedSelection);

		refreshTeam();

		// TODO: determine how big of a deal it is that equipment doesn't stay selected
		ownedEquipmentTable.getSelectionModel().setLeadSelectionIndex(equipmentOwnedSelection);
	}

	private void wearEquipment()
	{
		int equipIndex = teamUpdater.getEquipment(equipmentOwnedSelection);
		
		if (equipIndex < 0)
			return;
		
		Equipment toWear = Equipment.getEquipment(equipIndex);
		int equipType = toWear.type;
		
		Player player = teamUpdater.getPlayer(currentPlayerIndex);
		
		if (player == null || player.getEquipment(equipType) >= 0)
			return;
		
		player.equipItem(toWear);
		teamUpdater.removeEquipment(equipmentOwnedSelection);

		refreshTeam();
	}
	
	private void removeEquipment(int equipType)
	{
		Player player = teamUpdater.getPlayer(currentPlayerIndex);
		
		if (player == null)
			return;
		
		int equipIndex = player.unequipItem(equipType);
		
		if (equipIndex >= 0)
			teamUpdater.addEquipment(equipIndex);
		
		refreshTeam();
	}

	private void gainSkill(int skillIndex)
	{
		Player player = teamUpdater.getPlayer(currentPlayerIndex);
		player.gainSkill(skillIndex);
		teamUpdater.setPlayer(currentPlayerIndex, player);
		updateSkillsPanel();
		refreshPlayerPane();
		
		if (skillIndex == Player.SKILL_SLY)
			refreshEquipmentPane();
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

	private void selectPlayerView(String cardName)
	{
		CardLayout cl = (CardLayout) (rosterPane.getLayout());
		cl.show(rosterPane, cardName);

		if (cardName.equals(PLAYER_VIEW))
			swapButton.setEnabled(false);
		else
			swapButton.setEnabled(true);
	}

	private void selectEquipmentView(String cardName)
	{
		if (cardName.equals(EQUIP_VIEW))
			inOutfitScreen = true;
		else
			inOutfitScreen = false;
		
		CardLayout cl = (CardLayout) (equipmentCardPanel.getLayout());
		cl.show(equipmentCardPanel, cardName);
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
		sanitizedString = sanitizedString.replace("_", " ");

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

		for (int i = 1; i <= Player.TOTAL_SKILLS - 1; i++)	//total minus 1 because the last is ninja master
		{
			JToggleButton currentButton = skillButtons[i];
			currentButton.setEnabled(skillButtonValidator.isButtonEnabled(i, player));
			currentButton.setSelected(skillButtonValidator.isButtonSelected(i, player));
		}
	}

	private void setDraftImage()
	{
		draftPlayerInfoPanel.updatePanel(draftSelection, teamUpdater.getPlayerImage(draftSelection));
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
		refreshEquipmentPane();
		refreshDocbotPane();
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
		refreshEquipmentPane();
	}

	private void refreshTrimColor()
	{
		teamColorPanel.setTrimColor(teamUpdater.getTrimColor());
		trimColorChooser.setColor(teamUpdater.getTrimColor());
		refreshPlayerImages();
		refreshEquipmentPane();
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
		BufferedImage playerImage = null;

		if (player != null)
			playerImage = teamUpdater.getPlayerImage(player.getRace());

		rosterPlayerInfoPanel.updatePanel(player, currentPlayerIndex, playerImage);
	}

	private void refreshEquipmentPane()
	{
		Team team = teamUpdater.getTeam();
		OwnedEquipmentTableModel equipModel = (OwnedEquipmentTableModel) ownedEquipmentTable.getModel();

		equipmentShopPane.updatePanel();
		equipmentEquipPane.updateEquipment(teamUpdater.getPlayer(currentPlayerIndex));
		equipModel.refreshTable(team);
		refreshEquipmentLabels();

		// TODO: I think all that's left is refreshing the equipment for the currently selected player
	}

	private void refreshEquipmentLabels()
	{
		OwnedEquipmentTableModel equipModel = (OwnedEquipmentTableModel) ownedEquipmentTable.getModel();
		Equipment selectedGear = equipModel.getEquipment(equipmentOwnedSelection);

		equipmentNameLabel.setText(selectedGear.name);
		equipmentDetectionLabel.setText("Detection: " + selectedGear.detection + "%");
		equipmentDescriptionPane.setText(selectedGear.description);
	}

	private void refreshDocbotPane()
	{
		Team team = teamUpdater.getTeam();
		int budget = MAX_VALUE - teamUpdater.getTeamValue();
		int docbotCost = teamUpdater.getDocbotCost();
		int treatmentCosts[] = { 50, 40, 30, 30 };

		((InjuredPlayersTableModel) injuredPlayersTable.getModel()).refreshTable(team);

		for (int i = 0; i < 4; i++)
		{
			if (team.docbot[i])
			{
				docbotButtons[i][0].setSelected(false);
				docbotButtons[i][1].setSelected(true);
			} else
			{
				docbotButtons[i][0].setSelected(true);
				docbotButtons[i][1].setSelected(false);
			}

			if (budget < treatmentCosts[i] && !docbotButtons[i][1].isSelected())
			{
				docbotButtons[i][1].setEnabled(false);
			} else
			{
				docbotButtons[i][1].setEnabled(true);
			}
		}

		docbotCostLabel.setText(docbotCost + "K");
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
		refreshPlayerPane();
	}
}