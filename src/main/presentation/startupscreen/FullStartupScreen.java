package main.presentation.startupscreen;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import main.data.entities.Team;
import main.data.factory.CpuTeamFactory;
import main.logic.Client;
import main.logic.Server;
import main.presentation.common.AbstractScreenPanel;
import main.presentation.common.GameSettings;
import main.presentation.common.Logger;
import main.presentation.common.PaddedPanel;
import main.presentation.common.TeamEditor;
import main.presentation.game.GameRunnerGUI;
import main.presentation.game.PresentationMode;
import main.presentation.legacy.game.GamePanel;
import main.presentation.legacy.stats.gamestats.AbstractLegacyStatsScreenPanel;
import main.presentation.legacy.stats.gamestats.LegacyStatsScreenCarnage;
import main.presentation.legacy.stats.gamestats.LegacyStatsScreenChecking;
import main.presentation.legacy.stats.gamestats.LegacyStatsScreenMvp;
import main.presentation.legacy.stats.gamestats.LegacyStatsScreenOverview;
import main.presentation.legacy.stats.gamestats.LegacyStatsScreenRushing;
import main.presentation.legacy.stats.gamestats.LegacyStatsScreenSacking;
import main.presentation.legacy.teameditor.LegacyTeamEditorScreen;
import main.presentation.legacy.teameditor.ScreenCommand;
import main.presentation.teamchoicescreen.AbstractTeamSelectionScreen;
import main.presentation.teamchoicescreen.DraftSetupScreen;
import main.presentation.teamchoicescreen.ExhibitionTeamSelectionScreen;
import main.presentation.teamchoicescreen.GamePlayerSlotHelmetPanel;
import main.presentation.teameditor.TeamEditorPanel;

public class FullStartupScreen extends JFrame implements ActionListener
{
	private static final long serialVersionUID = -3309094530131086326L;

	private JPanel contentPane;

	private static final int FRAME_HEIGHT = 500;
	private static final int FRAME_WIDTH = 810;
	private static final Dimension FRAME_DIMENSION = new Dimension(FRAME_WIDTH, FRAME_HEIGHT);

	// TODO: ideal dimensions
	// private static final int FRAME_HEIGHT = 800;
	// private static final int FRAME_WIDTH = 1280;

	private static final String MAIN_TAG = "main";
	private static final String EDIT_TAG = "edit";
	private static final String LEGACY_EDIT_TAG = "edit_legacy";
	private static final String HOST_TAG = "select";
	private static final String GAME_TAG = "game";
	private static final String SETTINGS_TAG = "settings";
	private static final String GAME_RUSH_STATS_TAG = "rush_stats";
	private static final String GAME_CHECK_STATS_TAG = "check_stats";
	private static final String GAME_SACK_STATS_TAG = "sack_stats";
	private static final String GAME_CARNAGE_STATS_TAG = "carnage_stats";
	private static final String GAME_OVERVIEW_STATS_TAG = "overview_stats";
	private static final String GAME_MVP_STATS_TAG = "mvp_stats";
	private static final String DRAFT_TAG = "draft_setup";
	private static String EXHIB_TAG;
	

	private GameMainPanel gameMainPanel;
	private GameSelectPanel gameSelectPanel;
	private TeamEditorPanel swingTeamEditPanel;
	private ExhibitionTeamSelectionScreen exhibPanel;
	private DraftSetupScreen draftPanel;
	private AbstractScreenPanel gamePlayPanel;
	private SettingsPanel settingsPanel;

	private PaddedPanel legacyTeamEditPanel;
	
	private PaddedPanel gameRushingStatsPanel;
	private PaddedPanel gameCheckingStatsPanel;
	private PaddedPanel gameSackingStatsPanel;
	private PaddedPanel gameCarnageStatsPanel;
	private PaddedPanel gameOverviewStatsPanel;
	private PaddedPanel gameMvpStatsPanel;
	
	private Map<String, JPanel> panelMap = new HashMap<String, JPanel>();
	private JPanel currentPanel;

	private String teamEditSource = MAIN_TAG;
	private AbstractTeamSelectionScreen gameStartSource = null;
	private int teamEditIndex = -1;

	private Server host = new Server();
	private Client client = null;

	public FullStartupScreen()
	{
		super("Crush! Super Deluxe");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		setSize(FRAME_HEIGHT, FRAME_WIDTH);
		defineContentPane();

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		swapPane(EXHIB_TAG);	//TODO: remove this once i'm done testing the game gui
		runGameLoop();
	}
	
	private static double interpolation = 0;
	private static final int TICKS_PER_SECOND = 25;
	private static final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
	private static final int MAX_FRAMESKIP = 5;
	
	private void runGameLoop()
	{
		double next_game_tick = System.currentTimeMillis();
		int loops;

		while (true)
		{
			loops = 0;
			while (System.currentTimeMillis() > next_game_tick && loops < MAX_FRAMESKIP)
			{

				// refresh game data/content - not needed here because it's done on demand

				next_game_tick += SKIP_TICKS;
				loops++;
			}

			interpolation = (System.currentTimeMillis() + SKIP_TICKS - next_game_tick / (double) SKIP_TICKS);
			refresh();
		}
	}
	
	private void refresh()
	{
		currentPanel.repaint();
		
		if (client != null)
			client.getGui().refreshInterface();
	}

	private void defineContentPane()
	{
		Logger.output("Building interface...");
		
		contentPane = new JPanel();
		contentPane.setLayout(new CardLayout());

		gameMainPanel = new GameMainPanel(FRAME_WIDTH, FRAME_HEIGHT, this);
		gameSelectPanel = new GameSelectPanel(FRAME_WIDTH, FRAME_HEIGHT, this);
		swingTeamEditPanel = new TeamEditorPanel(this);
		exhibPanel = new ExhibitionTeamSelectionScreen(FRAME_WIDTH, FRAME_HEIGHT, this);
		draftPanel = new DraftSetupScreen(FRAME_WIDTH, FRAME_HEIGHT, this);
		gamePlayPanel = new GamePanel(FRAME_WIDTH, FRAME_HEIGHT, this);
		settingsPanel = new SettingsPanel(FRAME_WIDTH, FRAME_HEIGHT, this);

		legacyTeamEditPanel = new PaddedPanel(FRAME_DIMENSION, new LegacyTeamEditorScreen(this));
		
		gameRushingStatsPanel = new PaddedPanel(FRAME_DIMENSION, new LegacyStatsScreenRushing(this));
		gameCheckingStatsPanel = new PaddedPanel(FRAME_DIMENSION, new LegacyStatsScreenChecking(this));
		gameSackingStatsPanel = new PaddedPanel(FRAME_DIMENSION, new LegacyStatsScreenSacking(this));
		gameCarnageStatsPanel = new PaddedPanel(FRAME_DIMENSION, new LegacyStatsScreenCarnage(this));
		gameOverviewStatsPanel = new PaddedPanel(FRAME_DIMENSION, new LegacyStatsScreenOverview(this));
		gameMvpStatsPanel = new PaddedPanel(FRAME_DIMENSION, new LegacyStatsScreenMvp(this));
		
		EXHIB_TAG = exhibPanel.getScreenTag();

		// TODO: add different panels
		addAndMapPanel(gameMainPanel, MAIN_TAG);
		addAndMapPanel(settingsPanel, SETTINGS_TAG);
		addAndMapPanel(gameSelectPanel, HOST_TAG);
		addAndMapPanel(swingTeamEditPanel, EDIT_TAG);
		addAndMapPanel(legacyTeamEditPanel, LEGACY_EDIT_TAG);
		addAndMapPanel(exhibPanel, EXHIB_TAG);
		addAndMapPanel(draftPanel, DRAFT_TAG);
		// TODO: add a "game in progress" panel
		addAndMapPanel(gamePlayPanel, GAME_TAG);
		// TODO: add a victory panel
		addAndMapPanel(gameRushingStatsPanel, GAME_RUSH_STATS_TAG);
		addAndMapPanel(gameCheckingStatsPanel, GAME_CHECK_STATS_TAG);
		addAndMapPanel(gameSackingStatsPanel, GAME_SACK_STATS_TAG);
		addAndMapPanel(gameCarnageStatsPanel, GAME_CARNAGE_STATS_TAG);
		addAndMapPanel(gameOverviewStatsPanel, GAME_OVERVIEW_STATS_TAG);
		addAndMapPanel(gameMvpStatsPanel, GAME_MVP_STATS_TAG);
		
		setContentPane(contentPane);
		
		Logger.output("Done!\n");
	}
	
	private void addAndMapPanel(JPanel panel, String tag)
	{
		contentPane.add(panel, tag);
		panelMap.put(tag, panel);
	}

	private void swapPane(String newPaneTag)
	{
		currentPanel = panelMap.get(newPaneTag);
		CardLayout cl = (CardLayout) (getContentPane().getLayout());
		cl.show(getContentPane(), newPaneTag);
	}

	private void chooseTeam(String choiceCommand)
	{
		int indexLength = 2;

		String command = choiceCommand.substring(GamePlayerSlotHelmetPanel.CHOOSE_TEAM.length());
		teamEditIndex = Integer.parseInt(command.substring(0, indexLength));
		String source = command.substring(indexLength + 1);

		AbstractTeamSelectionScreen sourceScreen = (AbstractTeamSelectionScreen) panelMap.get(source);

		if (sourceScreen != null)
		{
			boolean seasonBegun = sourceScreen.isSeasonStarted();
			Team team = sourceScreen.getTeamForPlayerSlot(teamEditIndex);
			int budget = sourceScreen.getBudget();
			loadTeamEditor(team, budget, source, seasonBegun);
		}
	}

	private void startGameCheck(String startCommand)
	{
		String commandSource = startCommand.substring(Client.ACTION_GAME_START.length() + 1);
		AbstractTeamSelectionScreen sourceScreen = (AbstractTeamSelectionScreen) panelMap.get(commandSource);

		if (sourceScreen != null)
			startNewGame(sourceScreen);
	}

	private void startNewGame(AbstractTeamSelectionScreen sourceScreen)
	{
		if (!sourceScreen.readyToStart())
			return;

		gameStartSource = sourceScreen;
		List<Team> gameTeams = getTeamsForGameStart(sourceScreen.getTeams(), sourceScreen.getBudget());
		
		client = new Client(host, this, GameSettings.getPresentationMode());
		
		gameStartSource.disableControls();
		contentPane.add(((GameRunnerGUI)client.getGui()).getDisplayPanel(), GAME_TAG);
		swapPane(GAME_TAG);

		host.newGame(gameTeams);
		client.getGui().beginGame();
	}
	
	private List<Team> getTeamsForGameStart(List<Team> rawTeams, int budget)
	{
		List<Team> preparedTeams = new ArrayList<Team>();
		
		for (Team team : rawTeams)
		{
			if (team.isBlankTeam())
				team = CpuTeamFactory.generateCpuTeam(budget);
			
			preparedTeams.add(team);
			
			if (preparedTeams.size() == 3)
				break;
		}
		
		while (preparedTeams.size() < 3)
			preparedTeams.add(null);
		
		return preparedTeams;
	}

	private void loadTeamEditor(Team teamToShow, int budget, String source, boolean seasonStarted)
	{
		TeamEditor teamEditor = getTeamEditPanelForPresentationMode();
		teamEditor.setLoadEnabled(!seasonStarted);
		teamEditor.setTeam(teamToShow);
		teamEditor.setBudget(budget);
		teamEditSource = source;
		swapPane(getTeamEditorTagForPresentationMode());
	}

	private void exitTeamEditor()
	{
		TeamEditor teamEditor = getTeamEditPanelForPresentationMode();
		Team currentTeam = teamEditor.getTeam();
		swapPane(teamEditSource);

		AbstractTeamSelectionScreen sourceScreen = null;
		
		try {
			sourceScreen = (AbstractTeamSelectionScreen) panelMap.get(teamEditSource);
		} catch (ClassCastException cce)
		{
			return;
		}

		if (sourceScreen == null)
			return;

		sourceScreen.updatePlayerSlot(teamEditIndex, currentTeam);
		teamEditIndex = -1;
	}

	private TeamEditor getTeamEditPanelForPresentationMode()
	{
		if (GameSettings.getPresentationMode() == PresentationMode.LEGACY)
			return (TeamEditor) legacyTeamEditPanel.getPanel();
		
		return swingTeamEditPanel;
	}
	
	private String getTeamEditorTagForPresentationMode()
	{
		if (GameSettings.getPresentationMode() == PresentationMode.LEGACY)
			return LEGACY_EDIT_TAG;
		
		return EDIT_TAG;
	}

	private void exitTeamSelect(String exitCommand)
	{
		String commandSource = exitCommand.substring(AbstractTeamSelectionScreen.ACTION_TEAM_SELECT_BACK.length() + 1);
		AbstractTeamSelectionScreen sourceScreen = (AbstractTeamSelectionScreen) panelMap.get(commandSource);

		if (sourceScreen == null)
			return;

		sourceScreen.resetScreen();
		swapPane(HOST_TAG);
	}
	
	private void showVictoryScreen()
	{
		int seasonWinner = gameStartSource.getSeasonWinner();
		String seasonType = "Season";
		
		if (gameStartSource.getScreenTag().equals(EXHIB_TAG))
			seasonType = "Exhibition";
		
		String teamName = gameStartSource.getTeamForPlayerSlot(seasonWinner).teamName;
		
		JOptionPane.showMessageDialog(null, "Congratulations, " + teamName + "! Your team has won the " + seasonType + "!", seasonType + " Winner!",
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void endGame()
	{
		int winningTeamIndex = host.getData().getWinningTeamIndex();
		System.out.println("CrushRunner knows that the game is done! Winning team: " + winningTeamIndex);
		host.endGame(); // TODO: pretty sure this is backwards (why is the runner telling the server the game is done?)
		gameStartSource.setTeams(host.getData().getTeams());
		gameStartSource.updateTeamRecords(winningTeamIndex);
		setVisible(true);
		
		updateAndShowEndOfGameStats();
		client = null;
	}
	
	private void updateAndShowEndOfGameStats()
	{
		updateStatsDataForScreen(gameRushingStatsPanel);
		updateStatsDataForScreen(gameCheckingStatsPanel);
		updateStatsDataForScreen(gameSackingStatsPanel);
		updateStatsDataForScreen(gameCarnageStatsPanel);
		updateStatsDataForScreen(gameOverviewStatsPanel);
		updateStatsDataForScreen(gameMvpStatsPanel);
		
		swapPane(GAME_RUSH_STATS_TAG);
	}
	
	private void updateStatsDataForScreen(PaddedPanel panel)
	{
		AbstractLegacyStatsScreenPanel statsPanel = (AbstractLegacyStatsScreenPanel) panel.getPanel();
		statsPanel.updateDataImpl(host.getData());
	}
	
	private void transitionToTeamScreen()
	{
		swapPane(gameStartSource.getScreenTag());
		
		if (gameStartSource.getSeasonWinner() > -1)
			showVictoryScreen();
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();

		if (command.equals(GameMainPanel.EXIT_TEXT))
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		else if (command.equals(GameMainPanel.HOST_TEXT))
			swapPane(HOST_TAG);
		else if (command.equals(GameMainPanel.EDIT_TEXT))
			loadTeamEditor(new Team(), 900, MAIN_TAG, false);
		else if (command.equals(ScreenCommand.EXIT.name()))
			exitTeamEditor();
		else if (command.equals(GameSelectPanel.EXHIB_TEXT))
			swapPane(EXHIB_TAG);
		else if (command.equals(GameSelectPanel.DRAFT_TEXT))
			swapPane(DRAFT_TAG);
		else if (command.equals(GameSelectPanel.BACK_TEXT) || command.equals(SettingsPanel.SAVE_TEXT))
			swapPane(MAIN_TAG);
		else if (command.equals(GameMainPanel.SETTINGS_TEXT))
			swapPane(SETTINGS_TAG);
		else if (command.startsWith(GamePlayerSlotHelmetPanel.CHOOSE_TEAM))
			chooseTeam(command);
		else if (command.startsWith(Client.ACTION_GAME_START))
			startGameCheck(command);
		else if (command.startsWith(AbstractTeamSelectionScreen.ACTION_TEAM_SELECT_BACK))
			exitTeamSelect(command);
		else if (command.equals(Client.ACTION_GAME_END))
			endGame();
		else if (command.equals(ScreenCommand.SHOW_RUSHING.name()))
			swapPane(GAME_RUSH_STATS_TAG);
		else if (command.equals(ScreenCommand.SHOW_CHECKING.name()) || command.equals(ScreenCommand.SHOW_CHECK_TAB.name()))
			swapPane(GAME_CHECK_STATS_TAG);
		else if (command.equals(ScreenCommand.SHOW_SACK_TAB.name()))
			swapPane(GAME_SACK_STATS_TAG);
		else if (command.equals(ScreenCommand.SHOW_CARNAGE.name()))
			swapPane(GAME_CARNAGE_STATS_TAG);
		else if (command.equals(ScreenCommand.SHOW_OVERVIEW.name()) || command.equals(ScreenCommand.SHOW_MISC_TAB.name()))
			swapPane(GAME_OVERVIEW_STATS_TAG);
		else if (command.equals(ScreenCommand.SHOW_MVP_TAB.name()))
			swapPane(GAME_MVP_STATS_TAG);
		else if (command.equals(ScreenCommand.DONE.name()))
			transitionToTeamScreen();
	}
}
