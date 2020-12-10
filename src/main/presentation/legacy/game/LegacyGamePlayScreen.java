package main.presentation.legacy.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import main.presentation.common.image.ImageType;
import main.presentation.common.image.ImageUtils;
import main.presentation.common.image.LegacyImageFactory;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.legacy.framework.AbstractLegacyScreen;
import main.presentation.legacy.framework.ClickableRegion;
import main.presentation.legacy.framework.GuiCommand;
import main.presentation.legacy.framework.GuiCommandType;
import main.presentation.legacy.framework.KeyCommand;
import main.presentation.legacy.framework.ScreenCommand;

public class LegacyGamePlayScreen extends AbstractLegacyScreen
{
	/*
	 * Time to determine how this and LegacyGraphicsGUI interact.  I think the big distinction is that the GUI has access to Data, while this doesn't.  (It can, but the point
	 * is to keep the separation of responsibilities.)  In other words, this should manage button and mouse clicks and pass them on (though then it needs to know about GUI, which
	 * is backwards from how I originally architected GUI), and then request the proper bars and viewport from the GUI.
	 */
	
	public static final int VIEWPORT_HEIGHT = 320;
	private static final Point MINIMAP_ORIGIN = new Point(LegacyUiConstants.MINIMAP_X_START, LegacyUiConstants.MINIMAP_Y_START + VIEWPORT_HEIGHT + 1);	//seems like an off-by-one error, but if it works...
	
	private boolean showStatsPanel = false;
	private boolean showHelpPanel = false;
	
	private BufferedImage baseImage = null;
	
	private LegacyGraphicsGUI gui = null;
	
	private Point clickCoords = null;
	
	public LegacyGamePlayScreen(ActionListener listener)
	{
		super(listener, ImageType.NO_TYPE);
		updateInRealTime = true;
		defineClickMap();
//		refreshBaseImage();
	}
	
	private void defineClickMap()
	{
		clickMap.getGraphics().drawImage(LegacyImageFactory.getInstance().getImage(ImageType.GAME_CLICKMAP), 0, 321, null);
		System.out.println(MINIMAP_ORIGIN);
		clickMappings.put(new Color(clickMap.getRGB(MINIMAP_ORIGIN.x, MINIMAP_ORIGIN.y)), ClickableRegion.noHighlightButton(MINIMAP_ORIGIN, ScreenCommand.MINIMAP_CLICK));
		//TODO add this line for each buttonbar button: clickMappings.put(<color>, new ClickableRegion(args));
	}

	//this needs to be called every time the mouse is clicked (might be able to make it more specific later, but that's good enough for now)
	//TODO: but right now it's not called at all, and the whole screen refreshes at once
	public void refreshBaseImage()
	{
		baseImage = ImageUtils.createBlankBufferedImage(LEGACY_SCREEN_DIMENSION, Color.BLACK);
		baseImage.getGraphics().drawImage(LegacyImageFactory.getInstance().getImage(ImageType.MAP_STARS_BG), 0, 0, null);
//		baseImage.getGraphics().drawImage(LegacyImageFactory.getInstance().getImage(ImageType.MAP_LAVA_BG), 0, 0, null);
		if (gui != null)
			baseImage.getGraphics().drawImage(gui.getButtonBarImage(), 0, 321, null);
		
		if (showStatsPanel)
			baseImage.getGraphics().drawImage(LegacyImageFactory.getInstance().getImage(ImageType.GAME_SIDEBAR), 0, 0, null); 		//TODO: extract this to a stats sidebar factory
		
		//TODO: conditionally draw help sidebar
		
		
		//TODO: debug code
//		baseImage = clickMap;
	}
	
	public void setGui(LegacyGraphicsGUI gui)
	{
		this.gui = gui;
	}

	@Override
	public void resetScreen()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handleCommand(ScreenCommand command)
	{
		// TODO Auto-generated method stub
		switch (command)
		{
		case MINIMAP_CLICK:
			if (clickCoords == null)
				return;
			
			int row = (clickCoords.y - MINIMAP_ORIGIN.y) / 2;	//these are divided by 2 because the minimap uses 2x2 pixels for each map tile
			int col = (clickCoords.x - MINIMAP_ORIGIN.x) / 2;
			Point minimapRowCol = new Point(row, col);
			gui.handleMinimapClick(minimapRowCol);
			break;
		}
	}

	@Override
	protected void handleKeyCommand(KeyCommand command)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void paintComponent(Graphics2D graphics)
	{
		if (gui == null)
			return;
		
		graphics.drawImage(baseImage, 0, 0, null);
		graphics.drawImage(LegacyImageFactory.getInstance().getImage(ImageType.MAP_STARS_BG), 0, 0, null);
		graphics.drawImage(getViewportImage(), (showStatsPanel ? 115 : 0), 0, null);
		graphics.drawImage(gui.getButtonBarImage(), 0, 321, null);
		
		if (showStatsPanel)
			baseImage.getGraphics().drawImage(LegacyImageFactory.getInstance().getImage(ImageType.GAME_SIDEBAR), 0, 0, null);	//TODO: extract this to a stats sidebar factory
	}

	private BufferedImage getViewportImage()
	{
		BufferedImage viewportImage = gui.getViewportImage(new Dimension(getViewportWidth(), VIEWPORT_HEIGHT));
//		System.out.print("Viewport height and width is: " + viewportImage.getHeight() + "x" + viewportImage.getWidth());
		return viewportImage;
	}
	
	//TODO: right now these are magic numbers, so correct them
	public int getViewportWidth()
	{
		if (showStatsPanel && showHelpPanel)
			return 410;
		
		if (showStatsPanel || showHelpPanel)
			return 525;
		
		return 640;
	}

	@Override
	protected void defineClickableRegions()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void receiveGuiCommand(GuiCommand command)
	{
		super.receiveGuiCommand(command);		//TODO: shouldn't need to make this exclusive with the other logic, but we'll see
		
//		if (command.getType() == GuiCommandType.MOUSE_PRESS)
//			System.out.println("GuiCommand received on gameplay screen: " + command);
		
		int x = command.getArgument1();
		int y = command.getArgument2();
		
		if (command.getType() == GuiCommandType.MOUSE_PRESS)
		{
			clickCoords = new Point(x, y);
//			refreshBaseImage();
		}
		else
		{
			clickCoords = null;
		}
		
//		if (y >= VIEWPORT_HEIGHT)
//			handleButtonBarClick(new Point(x, y - VIEWPORT_HEIGHT));
	}
}
