package main.presentation.legacy.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;

import main.presentation.common.AbstractScreenPanel;
import main.presentation.common.image.ImageType;
import main.presentation.common.image.ImageUtils;
import main.presentation.common.image.LegacyImageFactory;
import main.presentation.legacy.teameditor.ScreenCommand;

//This is for stats screens, team screens, and anything else that's not the intro screen or the game
public abstract class AbstractLegacyImageBasedScreenPanel extends AbstractScreenPanel implements MouseListener
{
	private static final long serialVersionUID = 5124214369091235677L;

	private static int[] currentColorValues = { 0, 0, 0 };
	protected static final Dimension defaultlegacyPanelDims = new Dimension(640, 400);

	protected static Dimension buttonDimSmallStats;
	protected static Dimension buttonDimSmallEditor;
	protected static Dimension buttonDimLarge;
	protected static Dimension buttonDimArenaSet;

	protected BufferedImage screenBaseImage;
	private BufferedImage clickMap;
	private BufferedImage doubleClickMap;

	protected Map<Color, ScreenCommand> clickMappings;

	private JButton actionTrigger;
	
	protected static LegacyImageFactory imageFactory = LegacyImageFactory.getInstance();
	protected LegacyFontFactory fontFactory = LegacyFontFactory.getInstance();

	protected AbstractLegacyImageBasedScreenPanel(BufferedImage bgImage, BufferedImage baseImage)
	{
		super(new Dimension(bgImage.getWidth(), bgImage.getHeight()), bgImage);
		screenBaseImage = baseImage;

		buttonDimSmallStats = imageFactory.getImageSize(ImageType.BUTTON_SMALL_NORMAL);
		buttonDimSmallEditor = imageFactory.getImageSize(ImageType.BUTTON_EDITOR_SMALL_NORMAL);
		buttonDimLarge = imageFactory.getImageSize(ImageType.BUTTON_LARGE_NORMAL);
		buttonDimArenaSet = imageFactory.getImageSize(ImageType.BUTTON_ARENA_SET_NORMAL);

		clickMappings = new HashMap<Color, ScreenCommand>();
		clickMap = ImageUtils.createBlankBufferedImage(defaultlegacyPanelDims, Color.BLACK);
		doubleClickMap = ImageUtils.createBlankBufferedImage(defaultlegacyPanelDims, Color.BLACK);

		actionTrigger = new JButton();

		addMouseListener(this);
	}

	protected void setActionListener(ActionListener listener)
	{
		actionTrigger.addActionListener(listener);
	}
	
	protected void fireAction(ScreenCommand actionCommand)
	{
		actionTrigger.setActionCommand(actionCommand.name());
		actionTrigger.doClick();
	}

	private static Color getNextClickZoneColor()
	{
		currentColorValues[0]++;

		if (currentColorValues[0] > 255)
		{
			currentColorValues[0] = 0;
			currentColorValues[1]++;
		}

		if (currentColorValues[1] > 255)
		{
			currentColorValues[1] = 0;
			currentColorValues[2]++;
		}

		return new Color(currentColorValues[0], currentColorValues[1], currentColorValues[2]);
	}

	protected void addClickZone(Rectangle zoneArea, ScreenCommand zoneAction)
	{
		createClickZone(clickMap, zoneArea, zoneAction);
	}

	protected void addDoubleClickZone(Rectangle zoneArea, ScreenCommand zoneAction)
	{
		createClickZone(doubleClickMap, zoneArea, zoneAction);
	}
	
	private void createClickZone(BufferedImage image, Rectangle zoneArea, ScreenCommand zoneAction)
	{
		Color zoneKey = getNextClickZoneColor();

		// print the rectangle onto the clickMap
		Graphics2D graphics = image.createGraphics();
		graphics.setPaint(zoneKey);
		graphics.fill(zoneArea);

		clickMappings.put(zoneKey, zoneAction);
	}

	protected abstract void paintImages(Graphics2D graphics);

	protected abstract void paintText(Graphics2D graphics);

	protected abstract void paintButtonShading(Graphics2D graphics); // this way it includes skill tree alterations

	protected void paintTextElement(Graphics2D graphics, int x, int y, String text, FontType fontType, Color color)
	{
		LegacyTextElement element = new LegacyTextElement(text, color);
		graphics.drawImage(fontFactory.generateString(element, fontType), x, y, null);
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.drawImage(getBackgroundImage(), 0, 0, null);
		g2.drawImage(screenBaseImage, 0, 0, null);

		paintImages(g2);
		paintText(g2);
		paintButtonShading(g2);
	}

	protected abstract void handleCommand(ScreenCommand command);

	@Override
	public void mouseClicked(MouseEvent event)
	{
		//no default behavior
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		//no default behavior
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		//no default behavior
	}

	@Override
	public void mousePressed(MouseEvent event)
	{
//		System.out.println(event.getPoint());

		Color color = new Color(clickMap.getRGB(event.getX(), event.getY()));
		
		if (event.getClickCount() == 2)
			color = new Color(doubleClickMap.getRGB(event.getX(), event.getY()));
		
		
		ScreenCommand command = clickMappings.get(color);

		if (command != null)
			handleCommand(command);
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		//no default behavior
	}

	@Override
	protected String getBgFilename()
	{
		return null;
	}
}
