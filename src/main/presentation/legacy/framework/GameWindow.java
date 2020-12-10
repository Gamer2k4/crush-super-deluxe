package main.presentation.legacy.framework;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

import main.data.entities.Team;
import main.data.factory.CpuTeamFactory;
import main.logic.Client;
import main.logic.Server;
import main.presentation.audio.CrushAudioManager;
import main.presentation.audio.SoundClipType;
import main.presentation.common.GameSettings;
import main.presentation.common.Logger;
import main.presentation.common.ResizableImagePanel;
import main.presentation.common.TeamEditor;
import main.presentation.common.image.ImageType;
import main.presentation.common.image.LegacyImageFactory;
import main.presentation.game.GameRunnerGUI;
import main.presentation.legacy.game.LegacyGamePlayScreen;
import main.presentation.legacy.game.LegacyGraphicsGUI;
import main.presentation.legacy.teameditor.LegacyTeamEditorScreen;
import main.presentation.legacy.teamselect.AbstractTeamSelectScreen;
import main.presentation.legacy.teamselect.ExhibitionTeamSelectScreen;
import main.presentation.legacy.teamselect.LeagueTeamSelectScreen;
import main.presentation.legacy.teamselect.TournamentTeamSelectScreen;

public class GameWindow extends JFrame implements ActionListener, MouseListener, MouseMotionListener, KeyListener
{
	private static final long serialVersionUID = 7499113252189375976L;

	private static final int FRAME_HEIGHT = 400;
	private static final int FRAME_WIDTH = 640;
	
	private static final Dimension FRAME_DIMENSION = new Dimension(FRAME_WIDTH, FRAME_HEIGHT);
	private static final double INITIAL_ZOOM = 2;

//	private JMenuBar menu;
	
	private ResizableImagePanel panel;
	private AbstractLegacyScreen activeScreen = null;
	private ActionEvent commandToReturnToPreviousScreen = null;		//when loading the team editor, this is set based on the current screen; this is the command that gets executed after leaving the team editor
	private int teamEditIndex = -1;
	
	private Map<ScreenType, AbstractLegacyScreen> allScreens = new HashMap<ScreenType, AbstractLegacyScreen>();

	private LegacyImageFactory imageFactory = LegacyImageFactory.getInstance();
	private CrushAudioManager audioManager = CrushAudioManager.getInstance();
	
	private Point mousePressCoords = null;
	
	private ImageType cursorImage = null;
	private Timer cursorTimer;
	
	private Server host = new Server();
	private Client client = null;

	public GameWindow()
	{
		super("Crush! Super Deluxe");
		defineScreens();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);
		
		CrushAudioManager.getInstance().mute();

		setSize(FRAME_DIMENSION);
		setPreferredSize(FRAME_DIMENSION);
		panel = new ResizableImagePanel(FRAME_DIMENSION);
		panel.addMouseListener(this);
		panel.addMouseMotionListener(this);
		addKeyListener(this);
		setContentPane(panel);
		//createAndAddMenuBar();		//TODO: this offsets y coords and messes with the custom cursor, so it's on hold for now
		createCursorTimer();

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		activeScreen = allScreens.get(ScreenType.GAME_SELECT);
//		actionPerformed(new ActionEvent(this, 0, ScreenCommand.MAIN_SCREEN.name()));
		actionPerformed(new ActionEvent(this, 0, ScreenCommand.EXHIBITION_TEAM_SELECT.name()));
//		correctInitialDimensions();
		scaleWindowSize(INITIAL_ZOOM);
	}

//	private void createAndAddMenuBar()
//	{
//		menu = new JMenuBar();
//		JMenu windowMenu = new JMenu("Window");
//		JMenuItem oneX = new JMenuItem("Resize to 1x");
//		JMenuItem twoX = new JMenuItem("Resize to 2x");
//		JCheckBoxMenuItem enableResizing = new JCheckBoxMenuItem("Enable Resizing", true);
//		
//		menu.add(windowMenu);
//		windowMenu.add(oneX);
//		windowMenu.add(twoX);
//		windowMenu.add(enableResizing);
//		
//		this.setJMenuBar(menu);
//	}
	
	private void scaleWindowSize(double amount)
	{
		//these first three lines are required because the padding the frame provides causes the initial panel size to shrink to fit
		setSize(FRAME_DIMENSION);
		int heightDif = FRAME_HEIGHT - panel.getHeight();
		int widthDif = FRAME_WIDTH - panel.getWidth();
		
		setSize((int)(FRAME_WIDTH * amount) + widthDif, (int)(FRAME_HEIGHT * amount) + heightDif);
	}

	private void createCursorTimer()
	{
		TimerTask task = new TimerTask() {
	        @Override
			public void run() {
	           if (cursorImage == ImageType.POINTER_COMP1)
	        	   setCursor(ImageType.POINTER_COMP2);
	           else if (cursorImage == ImageType.POINTER_COMP2)
	        	   setCursor(ImageType.POINTER_COMP3);
	           else if (cursorImage == ImageType.POINTER_COMP3)
	        	   setCursor(ImageType.POINTER_COMP4);
	           else if (cursorImage == ImageType.POINTER_COMP4)
	        	   setCursor(ImageType.POINTER_COMP1);
	           else if (cursorImage == ImageType.POINTER_NET1)
	        	   setCursor(ImageType.POINTER_NET2);
	           else if (cursorImage == ImageType.POINTER_NET2)
	        	   setCursor(ImageType.POINTER_NET3);
	           else if (cursorImage == ImageType.POINTER_NET3)
	        	   setCursor(ImageType.POINTER_NET4);
	           else if (cursorImage == ImageType.POINTER_NET4)
	        	   setCursor(ImageType.POINTER_NET1);
	        }
	    };
	    
		cursorTimer = new Timer();
		cursorTimer.scheduleAtFixedRate(task, 0, 250);
	}

	private void defineScreens()
	{
		allScreens.put(ScreenType.GAME_SELECT, new GameSelectScreen(this));
		allScreens.put(ScreenType.EXHIBITION_TEAM_SELECT, new ExhibitionTeamSelectScreen(this));
		allScreens.put(ScreenType.TOURNAMENT_TEAM_SELECT, new TournamentTeamSelectScreen(this));
		allScreens.put(ScreenType.LEAGUE_TEAM_SELECT, new LeagueTeamSelectScreen(this));
		allScreens.put(ScreenType.TEAM_EDITOR, new LegacyTeamEditorScreen(this));
		allScreens.put(ScreenType.GAME_PLAY, new LegacyGamePlayScreen(this));
	}

	private void setCursor(ImageType cursorImageType)
	{
		if (cursorImage == cursorImageType)
			return;
		
		cursorImage = cursorImageType;
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		BufferedImage cursor = imageFactory.getImage(cursorImageType);
		Cursor c = toolkit.createCustomCursor(cursor, new Point(panel.getX(), panel.getY()), null);
		panel.setCursor(c);
	}
	
	private void updateActiveScreen(AbstractLegacyScreen screen)
	{
		if (activeScreen != null)
			activeScreen.deactivate();
		
		activeScreen = screen;
		activeScreen.activate();
	}

	public void refresh()
	{
		if (activeScreen == null)
			return;
		
		panel.updateImage(activeScreen.getScreenImage());
		panel.repaint();
	}
	
	private Point convertMouseCoordinates(int x, int y)
	{
		int curWidth = panel.getWidth();
		int curHeight = panel.getHeight();
		
		double xProportion = FRAME_WIDTH / (double)curWidth;
		double yProportion = FRAME_HEIGHT / (double)curHeight;
		
		return new Point((int)((x * xProportion) + .5), (int)((y * yProportion) + .5));
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		Point convertedPoint = convertMouseCoordinates(e.getX(), e.getY());
		activeScreen.receiveGuiCommand(GuiCommand.mouseMoved(convertedPoint.x, convertedPoint.y));
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		mousePressCoords = convertMouseCoordinates(e.getX(), e.getY());
		
		if (e.getClickCount() == 2)
			activeScreen.receiveGuiCommand(GuiCommand.mouseDoubleClick(mousePressCoords.x, mousePressCoords.y));
		else
			activeScreen.receiveGuiCommand(GuiCommand.mousePress(mousePressCoords.x, mousePressCoords.y));
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (mousePressCoords != null)
			activeScreen.receiveGuiCommand(GuiCommand.mouseRelease(mousePressCoords.x, mousePressCoords.y));	//fire the event based on where the mouse was clicked, not where it was released
		mousePressCoords = null;
	}

	@Override
	public void keyPressed(KeyEvent ke)
	{
		Logger.output("GameWindow - Key event received with code: " + ke.getKeyCode() + ", character " + ke.getKeyChar());
		
		activeScreen.receiveKeyCommand(KeyCommand.fromKeyEvent(ke));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Logger.debug("GameWindow - Action event received with command: " + e.getActionCommand());
		ScreenCommand command = ScreenCommand.fromActionEvent(e);
		
		//This is where screen swaps happen
		switch(command)
		{
		case EXIT:
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
			break;
		case MAIN_SCREEN:
			setCursor(ImageType.POINTER_MAIN);
			activeScreen.resetScreen();			//going to the main screen should always clear out everything else
			updateActiveScreen(allScreens.get(ScreenType.GAME_SELECT));
			audioManager.playBackgroundLoop(SoundClipType.THEME);
			break;
		case EXHIBITION_TEAM_SELECT:
			setCursor(ImageType.POINTER_CRUSH);
			updateActiveScreen(allScreens.get(ScreenType.EXHIBITION_TEAM_SELECT));
			audioManager.playBackgroundLoop(SoundClipType.THEME);
			break;
		case TOURNAMENT_TEAM_SELECT:
			setCursor(ImageType.POINTER_CRUSH);
			updateActiveScreen(allScreens.get(ScreenType.TOURNAMENT_TEAM_SELECT));
			audioManager.playBackgroundLoop(SoundClipType.THEME);
			break;
		case LEAGUE_TEAM_SELECT:
			setCursor(ImageType.POINTER_CRUSH);
			updateActiveScreen(allScreens.get(ScreenType.LEAGUE_TEAM_SELECT));
			audioManager.playBackgroundLoop(SoundClipType.THEME);
			break;
		case BEGIN_GAME:
			startNewGame((AbstractTeamSelectScreen)activeScreen);
			break;
		case EXIT_TEAM_EDITOR_DONE:
			exitTeamEditor();
			break;
		case TOGGLE_SWAP:
			updateCursorForSwap();
			break;
			//$CASES-OMITTED$
		default:
			if (activeScreen != null)
				activeScreen.receiveScreenCommand(command);
			break;
		}
		
		if (command.isEditTeam())
		{
			commandToReturnToPreviousScreen = setReturnCommand();
			editTeam(command.getCommandIndex());
			setCursor(ImageType.POINTER_CRUSH);
			updateActiveScreen(allScreens.get(ScreenType.TEAM_EDITOR));
			activeScreen.resetScreen();		//always reset the team editor when entering it
			audioManager.playBackgroundLoop(SoundClipType.THEME);
		}
	}
	
	private void startNewGame(AbstractTeamSelectScreen sourceScreen)
	{
		if (!sourceScreen.isGameReadyToStart())
			return;
		
		List<Team> gameTeams = getTeamsForGameStart(sourceScreen.getTeams(), sourceScreen.getBudget());
		
		client = new Client(host, this, GameSettings.getPresentationMode());
		
		commandToReturnToPreviousScreen = setReturnCommand();
		setCursor(ImageType.POINTER_CRUSH);
		updateActiveScreen(allScreens.get(ScreenType.GAME_PLAY));
		activeScreen.resetScreen();		//no reason not to update the game screen for a new game
		
		LegacyGraphicsGUI gui = (LegacyGraphicsGUI) client.getGui();
		gui.setGameScreen(activeScreen);
		
		audioManager.playBackgroundLoop(SoundClipType.CROUD);
		
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
	
	private void updateCursorForSwap()
	{
		if (cursorImage == ImageType.POINTER_SWAP)
			setCursor(ImageType.POINTER_CRUSH);
		else
			setCursor(ImageType.POINTER_SWAP);
	}

	private ActionEvent setReturnCommand()
	{
		//maybe change this to checking for equality (same object) as the elements of the map, though in practice it shouldn't matter
		if (activeScreen instanceof ExhibitionTeamSelectScreen)
			return ScreenCommand.EXHIBITION_TEAM_SELECT.asActionEvent();
		else if (activeScreen instanceof TournamentTeamSelectScreen)
			return ScreenCommand.TOURNAMENT_TEAM_SELECT.asActionEvent();
		else if (activeScreen instanceof LeagueTeamSelectScreen)
			return ScreenCommand.LEAGUE_TEAM_SELECT.asActionEvent();
		return ScreenCommand.MAIN_SCREEN.asActionEvent();
	}

	private void editTeam(int teamIndex)
	{
		AbstractTeamSelectScreen sourceScreen = (AbstractTeamSelectScreen) activeScreen;

		if (sourceScreen != null)
		{
			teamEditIndex = teamIndex;
			boolean seasonBegun = sourceScreen.isSeasonStarted();
			Team team = sourceScreen.getTeam(teamIndex);
			int budget = sourceScreen.getBudget();
			loadTeamEditor(team, budget, seasonBegun);
		}
	}
	
	private void loadTeamEditor(Team teamToShow, int budget, boolean seasonStarted)
	{
		TeamEditor teamEditor = (TeamEditor) allScreens.get(ScreenType.TEAM_EDITOR);
		teamEditor.setLoadEnabled(!seasonStarted);
		teamEditor.setTeam(teamToShow);
		teamEditor.setBudget(budget);
	}

	private void exitTeamEditor()
	{
		TeamEditor teamEditor = (TeamEditor) allScreens.get(ScreenType.TEAM_EDITOR);	//safer than activeScreen
		Team currentTeam = teamEditor.getTeam();
		actionPerformed(commandToReturnToPreviousScreen);

		AbstractTeamSelectScreen sourceScreen = null;
		
		try {
			sourceScreen = (AbstractTeamSelectScreen) activeScreen;
		} catch (ClassCastException cce)
		{
			return;
		}

		if (sourceScreen == null)
			return;

		sourceScreen.updateTeam(teamEditIndex, currentTeam);
		teamEditIndex = -1;
	}

	@Override
	public void mouseClicked(MouseEvent e) {}		//graphics update on press, action fired on release, so nothing needed for the click itself

	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}
}
