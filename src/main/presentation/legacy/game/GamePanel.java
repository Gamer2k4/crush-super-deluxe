package main.presentation.legacy.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;

import main.data.entities.Player;
import main.data.entities.Race;
import main.data.entities.Stats;
import main.data.factory.PlayerFactory;
import main.presentation.common.AbstractFontFactory;
import main.presentation.common.AbstractScreenPanel;
import main.presentation.common.ImagePanel;
import main.presentation.common.image.AbstractImageFactory;
import main.presentation.common.image.ImageBuffer;
import main.presentation.common.image.ImageType;
import main.presentation.common.image.ImageUtils;
import main.presentation.common.image.LegacyImageFactory;
import main.presentation.legacy.common.FontType;
import main.presentation.legacy.common.LegacyFontFactory;
import main.presentation.legacy.common.LegacyTextElement;
import main.presentation.legacy.common.LegacyUiConstants;

public class GamePanel extends AbstractScreenPanel implements MouseListener, MouseMotionListener
{
	private static final long serialVersionUID = 1281623443797222129L;

	private ImagePanel sideBar;
	private ImagePanel buttonBar;
	private ImagePanel gameDisplayPanel;
	
	private BufferedImage minimap;

	private JButton commandTrigger;

	public static final int GAME_WINDOW_HEIGHT = 400;
	public static final int GAME_WINDOW_WIDTH = 640;
	public static final int VIEWPORT_HEIGHT = 320;
	public static final int VIEWPORT_WIDTH = 525;

	private static final int SCROLL_THRESHOLD = 10;

	private int xOffset;
	private int yOffset;

	private int lastX = GAME_WINDOW_WIDTH / 2;
	private int lastY = GAME_WINDOW_HEIGHT / 2;

	private AbstractFontFactory fontFactory;
	private AbstractImageFactory imageFactory;
	private Map<Integer, Point> statTextCoords;

	// TODO: right now this is a gameEndListener, but I think it should be an action listener from the GUI
	public GamePanel(int width, int height, ActionListener listener)
	{
		this(new Dimension(width, height), listener);
	}

	// TODO: figure out how this, GameDisplayPanel, and LegacyGraphicsGUI are related
	public GamePanel(Dimension dimension, ActionListener listener)
	{
		super(dimension);
		defineStatTextCoords();

		fontFactory = LegacyFontFactory.getInstance();
		imageFactory = LegacyImageFactory.getInstance();

		setBackground(Color.BLACK);
		setBackgroundTint(Color.BLACK);
		
		sideBar = new ImagePanel(115, 320);

		minimap = ImageUtils.createBlankBufferedImage(new Dimension(60, 60), Color.CYAN);
		
		buttonBar = new ImagePanel(640, 80);
		buttonBar.setBackground(Color.BLACK);

		gameDisplayPanel = new GameDisplayPanel(new Dimension(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));

		xOffset = (dimension.width - GAME_WINDOW_WIDTH) / 2;
		yOffset = (dimension.height - GAME_WINDOW_HEIGHT) / 2;

		addPanel(xOffset, yOffset, sideBar);
		addPanel(xOffset, yOffset + 321, buttonBar);
		addPanel(xOffset + 116, yOffset, gameDisplayPanel);

		commandTrigger = new JButton();
		commandTrigger.addActionListener(listener);

		addMouseListener(this);
		addMouseMotionListener(this);

		// updateStatsForPlayer(null);
		updateStatsForPlayer(PlayerFactory.createPlayerWithRandomName(Race.GRONK));
	}

	public void updateViewportImage(BufferedImage image)
	{
		// TODO: probably keep background stuff somewhere else, but maybe not
		ImageBuffer.setBaseImage(imageFactory.getImage(ImageType.MAP_LAVA_BG));
		ImageBuffer.addLayer(0, 0, image);
		gameDisplayPanel.updateImage(ImageBuffer.getCompositeImage());
	}

	public void updateButtonBar(BufferedImage image)
	{
		buttonBar.updateImage(image);
	}

	private void defineStatTextCoords()
	{
		statTextCoords = new HashMap<Integer, Point>();
		statTextCoords.put(Stats.STATS_RUSHING_ATTEMPTS, new Point(80, 135));
		statTextCoords.put(Stats.STATS_RUSHING_YARDS, new Point(80, 143));
		statTextCoords.put(Stats.STATS_CHECKS_THROWN, new Point(80, 170));
		statTextCoords.put(Stats.STATS_CHECKS_LANDED, new Point(80, 178));
		statTextCoords.put(Stats.STATS_SACKS_FOR, new Point(80, 194));
		statTextCoords.put(Stats.STATS_INJURIES_FOR, new Point(80, 135));
		statTextCoords.put(Stats.STATS_KILLS_FOR, new Point(80, 135));
	}

	//TODO: extract this to a stats sidebar factory
	public void updateStatsForPlayer(Player player)
	{
		ImageBuffer.setBaseImage(imageFactory.getImage(ImageType.GAME_SIDEBAR));

		// addStatsText(Stats.STATS_RUSHING_ATTEMPTS, LegacyPlayerTextFactory.getRushAttempts());
		// addStatsText(Stats.STATS_RUSHING_YARDS, LegacyPlayerTextFactory.getRushTiles());
		// addStatsText(Stats.STATS_CHECKS_THROWN, 12);
		// addStatsText(Stats.STATS_CHECKS_LANDED, 10);
		// addStatsText(Stats.STATS_SACKS, 1);

		sideBar.updateImage(ImageBuffer.getCompositeImage());
	}

	private void addStatsText(int statsType, LegacyTextElement statsText)
	{
		Point coords = statTextCoords.get(statsType);

		ImageBuffer.addLayer(coords.x, coords.y, fontFactory.generateString(statsText));
	}

	private String addLeadingZeros(String s, int length)
	{
		if (s.length() >= length)
			return s;

		return String.format("%0" + (length - s.length()) + "d%s", 0, s);
	}

	private void addPanel(int x, int y, JPanel panel)
	{
		panel.setLocation(x, y);
		add(panel);
	}

	@Override
	protected String getBgFilename()
	{
//		return "bg_human_checking_gronk.png";
		return "unused_bg.png";
	}

	@Override
	public void resetScreen()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		int x = e.getX() - xOffset;
		int y = e.getY() - yOffset;

		if (x < 0 || y < 0 || x > GAME_WINDOW_WIDTH || y > GAME_WINDOW_HEIGHT)
			return;

		System.out.println("Mouse press at (" + x + ", " + y + ")");

		if (x < sideBar.getWidth() && y < sideBar.getHeight())
			handleSideBarClick(new Point(x, y));
		else if (y >= sideBar.getHeight())
			handleButtonBarClick(new Point(x, y - sideBar.getHeight()));
		else
			handleGameDisplayClick(new Point(x - sideBar.getWidth(), y));
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	private void handleSideBarClick(Point location)
	{
		System.out.println("Sidebar Mouse press at (" + location.x + ", " + location.y + ")");
		// TODO;
	}

	private void handleButtonBarClick(Point location)
	{
		System.out.println("Button Bar Mouse press at (" + location.x + ", " + location.y + ")");
		fireAction(getCommandFromClickMap(location));
	}

	private void handleGameDisplayClick(Point location)
	{
		System.out.println("Game Display Mouse press at (" + location.x + ", " + location.y + ")");
		fireAction("VIEWPORT_CLICK_" + addLeadingZeros(String.valueOf(location.x), 3) + "_"
				+ addLeadingZeros(String.valueOf(location.y), 3));
	}

	private void fireAction(String actionCommand)
	{
		commandTrigger.setActionCommand(actionCommand);
		commandTrigger.doClick();
	}

	private String getCommandFromClickMap(Point location)
	{
		int color = imageFactory.getImage(ImageType.GAME_CLICKMAP).getRGB(location.x, location.y);

		System.out.println("Button type for the click location is" + color);

		switch (color)
		{
		case -7389440:
			return "SELECT_PLAYER_1";
		case -5748736:
			return "SELECT_PLAYER_2";
		case -3713280:
			return "SELECT_PLAYER_3";
		case -2072576:
			return "SELECT_PLAYER_4";
		case -14739712:
			return "SELECT_PLAYER_5";
		case -13096960:
			return "SELECT_PLAYER_6";
		case -11059456:
			return "SELECT_PLAYER_7";
		case -9416704:
			return "SELECT_PLAYER_8";
		case -7379200:
			return "SELECT_PLAYER_9";
		case -7405568:
			return "PREV_PLAYER";
		case -5767168:
			return "NEXT_PLAYER";
		case -11075584:
			return "MOVE";
		case 0:
			return "CHECK"; // the "check" clickmap is actually color 0 - no RGB, no alpha
		case -9437184:
			return "JUMP";
		case -3735552:
			return "HANDOFF";
		case -2097152:
			return "PROFILE";
		case -11065600:
			return "HELP";
		case -14741504:
			return "STATS_CST";
		case -13101056:
			return "STATS_ARH"; // TODO: confirm this is what gets shown (that is, that the letters are right)
		case -13107200:
			return "END_TURN";
		case -9424896:
			return "TIME_OUT";
		case -65281:
			return getMinimapCommand(location);
		default:
			return "NO_COMMAND";
		}
	}

	private String getMinimapCommand(Point location)
	{
		DecimalFormat intFormatter = new DecimalFormat("00");
		
		int x = (location.x - LegacyUiConstants.MINIMAP_X_START) / 2;
		int y = (location.y - LegacyUiConstants.MINIMAP_Y_START) / 2;
		
		String xString = intFormatter.format(x);
		String yString = intFormatter.format(y);
		
		return "MINIMAP_CLICK_" + xString + "_" + yString;
	}

	@Override
	public void mouseDragged(MouseEvent arg0)
	{
	}

	@Override
	public void mouseMoved(MouseEvent event)
	{
		int x = event.getX();
		int y = event.getY();

		int xMax = GAME_WINDOW_WIDTH - SCROLL_THRESHOLD;
		int yMax = GAME_WINDOW_HEIGHT - SCROLL_THRESHOLD;

		if (x < SCROLL_THRESHOLD && lastX >= SCROLL_THRESHOLD)
			fireAction("SCROLL_X-");
		if (x >= SCROLL_THRESHOLD && lastX < SCROLL_THRESHOLD)
			fireAction("SCROLL_X0");
		if (x > xMax && lastX <= xMax)
			fireAction("SCROLL_X+");
		if (x <= xMax && lastX > xMax)
			fireAction("SCROLL_X0");
		if (y < SCROLL_THRESHOLD && lastY >= SCROLL_THRESHOLD)
			fireAction("SCROLL_Y-");
		if (y >= SCROLL_THRESHOLD && lastY < SCROLL_THRESHOLD)
			fireAction("SCROLL_Y0");
		if (y > yMax && lastY <= yMax)
			fireAction("SCROLL_Y+");
		if (y <= yMax && lastY > yMax)
			fireAction("SCROLL_Y0");

		lastX = x;
		lastY = y;
	}
}
