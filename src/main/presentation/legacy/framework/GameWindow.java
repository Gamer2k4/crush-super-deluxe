package main.presentation.legacy.framework;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import main.presentation.common.Logger;
import main.presentation.common.ResizableImagePanel;
import main.presentation.common.image.ImageType;
import main.presentation.common.image.LegacyImageFactory;
import main.presentation.legacy.teameditor.ScreenCommand;

public class GameWindow extends JFrame implements ActionListener, MouseListener, MouseMotionListener
{
	private static final long serialVersionUID = 7499113252189375976L;

	private static final int FRAME_HEIGHT = 400;
	private static final int FRAME_WIDTH = 640;
	
	private static final Dimension FRAME_DIMENSION = new Dimension(FRAME_WIDTH, FRAME_HEIGHT);

	private ResizableImagePanel panel;
	private AbstractLegacyScreen activeScreen;
	
	private List<AbstractLegacyScreen> allScreens = new ArrayList<AbstractLegacyScreen>();

	private LegacyImageFactory imageFactory = LegacyImageFactory.getInstance();
	
	private Point mousePressCoords = null;

	public GameWindow()
	{
		super("Crush! Super Deluxe");
		defineScreens();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);

		setSize(FRAME_DIMENSION);
		setPreferredSize(FRAME_DIMENSION);
		panel = new ResizableImagePanel(FRAME_DIMENSION);
		panel.addMouseListener(this);
		panel.addMouseMotionListener(this);
//		panel.updateImage(imageFactory.getImage(ImageType.MAIN_MENU));
		setContentPane(panel);
		setCursor(ImageType.POINTER_MAIN);

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		updateActiveScreen(allScreens.get(0));
		correctInitialDimensions();
	}

	private void defineScreens()
	{
		allScreens.add(new GameSelectScreen(this));
		allScreens.add(new ExhibitionTeamSelectScreen(this));
		allScreens.add(new TournamentTeamSelectScreen(this));
		allScreens.add(new LeagueTeamSelectScreen(this));
	}

	//required because the padding the frame provides causes the initial panel size to shrink to fit
	private void correctInitialDimensions()
	{
		int heightDif = FRAME_HEIGHT - panel.getHeight();
		int widthDif = FRAME_WIDTH - panel.getWidth();
		setSize(FRAME_WIDTH + widthDif, FRAME_HEIGHT + heightDif);
	}

	private void setCursor(ImageType cursorImageType)
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		BufferedImage cursorImage = imageFactory.getImage(cursorImageType);
		Cursor c = toolkit.createCustomCursor(cursorImage, new Point(panel.getX(), panel.getY()), null);
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
	public void actionPerformed(ActionEvent e)
	{
		Logger.debug("GameWindow - Action event received with command: " + e.getActionCommand());
		ScreenCommand command = ScreenCommand.fromActionEvent(e);
		
		activeScreen.receiveScreenCommand(command);
		
		//This is where screen swaps happen
		switch(command)
		{
		case EXIT:
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
			break;
		case MAIN_SCREEN:
			setCursor(ImageType.POINTER_MAIN);
			updateActiveScreen(allScreens.get(0));
			break;
		case BEGIN_EXHIBITION:
			setCursor(ImageType.POINTER_CRUSH);
			updateActiveScreen(allScreens.get(1));
			break;
		case BEGIN_TOURNAMENT:
			setCursor(ImageType.POINTER_CRUSH);
			updateActiveScreen(allScreens.get(2));
			break;
		case BEGIN_LEAGUE:
			setCursor(ImageType.POINTER_CRUSH);
			updateActiveScreen(allScreens.get(3));
			break;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {}		//graphics update on press, action fired on release, so nothing needed for the click itself

	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}
