package main.presentation.legacy.framework;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;

import main.presentation.audio.CrushAudioManager;
import main.presentation.audio.SoundClipType;
import main.presentation.common.Logger;
import main.presentation.common.image.AbstractColorReplacer;
import main.presentation.common.image.ImageType;
import main.presentation.common.image.ImageUtils;
import main.presentation.common.image.LegacyImageFactory;
import main.presentation.legacy.common.FontType;
import main.presentation.legacy.common.LegacyFontFactory;
import main.presentation.legacy.common.LegacyTextElement;

public abstract class AbstractLegacyScreen
{
	public static Dimension LEGACY_SCREEN_DIMENSION = new Dimension(640, 400);
	private static int[] currentColorValues = { 0, 0, 0 };
	
	private Point currentCursorLocation = null;
	
	protected boolean isActive = false;
	protected boolean updateInRealTime = false;
	
	protected LegacyImageFactory imageFactory = LegacyImageFactory.getInstance();
	protected LegacyFontFactory fontFactory = LegacyFontFactory.getInstance();
	
	protected BufferedImage buttonClickedSmall = imageFactory.getImage(ImageType.BUTTON_SMALL_CLICKED);
	protected BufferedImage buttonClickedSmall2 = imageFactory.getImage(ImageType.BUTTON_SMALL2_CLICKED);
	protected BufferedImage buttonClickedLarge = imageFactory.getImage(ImageType.BUTTON_LARGE_CLICKED);
	
	protected Dimension buttonDimSmall = imageFactory.getImageSize(ImageType.BUTTON_SMALL_NORMAL);
	protected Dimension buttonDimSmall2 = imageFactory.getImageSize(ImageType.BUTTON_SMALL2_NORMAL);
	protected Dimension buttonDimLarge = imageFactory.getImageSize(ImageType.BUTTON_LARGE_NORMAL);
	protected Dimension helmetDim = imageFactory.getImageSize(ImageType.EDITOR_HELMET_S);
	
	protected BufferedImage screenImage = null;
	private BufferedImage tintedBackgroundImage = null;
	private ImageType backgroundImage = null;
	protected BufferedImage clickMap = ImageUtils.createBlankBufferedImage(LEGACY_SCREEN_DIMENSION);
	
	protected Map<Color, ClickableRegion> clickMappings = new HashMap<Color, ClickableRegion>();
	private Set<ClickableRegion> highlightedRegions = new HashSet<ClickableRegion>();
	private ClickableRegion clickedRegion = null;
	
	private JButton actionTrigger;
	
	public AbstractLegacyScreen(ActionListener listener, ImageType backgroundImage)
	{
		actionTrigger = new JButton();
		actionTrigger.addActionListener(listener);
		this.backgroundImage = backgroundImage;
		refreshBackground();
		defineClickableRegions();
	}
	
	public void activate()
	{
		isActive = true;
		updateScreenImage();
	}
	
	public void deactivate()
	{
		isActive = false;
	}
	
	public void receiveGuiCommand(GuiCommand command)
	{
		if (!isActive)
			return;
		
		if (command.getType() == GuiCommandType.MOUSE_MOVE)
		{
			currentCursorLocation = new Point(command.getArgument1(), command.getArgument2());
		}
		else if (command.getType() == GuiCommandType.MOUSE_PRESS)
		{
			ClickableRegion region = getRegionAtLocation(command.getArgument1(), command.getArgument2());
			
			if (region == null)
				clickedRegion = null;
			else if (region.activateOnClick())
			{
				clickedRegion = region;
				CrushAudioManager.getInstance().playForegroundSound(SoundClipType.BUTTON);
			}
		}
		else if (command.getType() == GuiCommandType.MOUSE_DOUBLE_CLICK)
		{
			ClickableRegion region = getRegionAtLocation(command.getArgument1(), command.getArgument2());
			
			if (region != null && region.activateOnDoubleClick())
				fireAction(region.getCommand());
		}
		else if (command.getType() == GuiCommandType.MOUSE_RELEASE)
		{
			if (clickedRegion != null && clickedRegion.activateOnClick())
				fireAction(clickedRegion.getCommand());
			
			clickedRegion = null;
		}
		
		if (updateHighlightedRegions())
			updateScreenImage();
	}
	
	public void receiveScreenCommand(ScreenCommand command)
	{
		if (!isActive || command == null)
			return;
		
		handleCommand(command);
	}
	
	public void receiveKeyCommand(KeyCommand command)
	{
		if (!isActive || command == null)
			return;
		
		handleKeyCommand(command);
	}

	public BufferedImage getScreenImage()
	{
		if (!isActive)
			return null;
		
		if (updateInRealTime)
			updateScreenImage();
		
		return screenImage;
	}

	protected BufferedImage getBackgroundImage()
	{
		return tintedBackgroundImage;
	}
	
	//TODO: this isn't ideal, since i don't want to have to call this for every window if the settings change
	public void refreshBackground()
	{
		if (backgroundImage == null)
		{
			tintedBackgroundImage = ImageUtils.createBlankBufferedImage(new Dimension(1, 1));
			return;
		}
		
		BufferedImage bgImage = LegacyImageFactory.getInstance().getImage(backgroundImage);
		Color bgTint = getBackgroundTint();
		
		if (!backgroundImage.name().startsWith("BG_"))
			bgTint = null;
		
		if (bgTint != null)
			tintedBackgroundImage = AbstractColorReplacer.tintImage(bgImage, bgTint);
		else
			tintedBackgroundImage = bgImage;
	}
	
	private Color getBackgroundTint()
	{
		return Color.RED;
//		eventually return something from the static settings file
	}
	
	protected void fireAction(ScreenCommand actionCommand)
	{
		if (actionCommand == null)
		{
			Logger.warn("Null screen command received!");
			return;
		}
		
		actionTrigger.setActionCommand(actionCommand.name());
		actionTrigger.doClick();
	}
	
	public void updateScreenImage()
	{
		screenImage = ImageUtils.createBlankBufferedImage(getScreenDimensions());
		Graphics2D g2 = (Graphics2D) screenImage.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.drawImage(getBackgroundImage(), 0, 0, null);
		paintComponent(g2);
		highlightActiveRegions(g2);
	}
	
	protected Dimension getScreenDimensions()
	{
		return LEGACY_SCREEN_DIMENSION;
	}

	private void highlightActiveRegions(Graphics2D g2)
	{
		for (ClickableRegion region : highlightedRegions)
		{
			g2.drawImage(region.getHighlight(), region.getOrigin().x, region.getOrigin().y, null);
		}
	}

	//returns true if the highlighted regions have changed
	private boolean updateHighlightedRegions()
	{
		Set<ClickableRegion> newHighlights = new HashSet<ClickableRegion>();
		ClickableRegion region = null;
		
		if (currentCursorLocation != null)
			region = getRegionAtLocation(currentCursorLocation.x, currentCursorLocation.y);
		
		if (region != null && region.highlightOnHover())
			newHighlights.add(region);
		
		if (clickedRegion != null && clickedRegion.highlightOnClick())
			newHighlights.add(clickedRegion);
		
		Set<ClickableRegion> additionalRegions = getAlwaysHighlightedRegions();
			newHighlights.addAll(additionalRegions);
		
		if (newHighlights.equals(highlightedRegions))
			return false;
		
		highlightedRegions = newHighlights;
		Logger.info("highlighted regions have changed; display will update; new size is " + highlightedRegions.size());
		
		return true;
	}

	private ClickableRegion getRegionAtLocation(int x, int y)
	{
		try {
			Color regionColor = new Color(clickMap.getRGB(x, y));
			return clickMappings.get(regionColor);
		} catch (ArrayIndexOutOfBoundsException aioobe)
		{
			return null;
		}
	}
	
	protected void createClickZone(Rectangle zoneArea, ClickableRegion region)
	{
		Color zoneKey = getNextClickZoneColor();

		Graphics2D graphics = clickMap.createGraphics();
		graphics.setPaint(zoneKey);
		graphics.fill(zoneArea);

		clickMappings.put(zoneKey, region);
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
	
	protected Set<ClickableRegion> getAlwaysHighlightedRegions()
	{
		return new HashSet<ClickableRegion>();
	}

	protected void paintTextElement(Graphics2D graphics, int x, int y, String text, FontType fontType, Color color)
	{
		LegacyTextElement element = new LegacyTextElement(text, color, fontType);
		graphics.drawImage(fontFactory.generateString(element), x, y, null);
	}

	protected void paintPaddedTextElement(Graphics2D graphics, int x, int y, int width, String text, FontType fontType, Color color)
	{
		LegacyTextElement element = new LegacyTextElement(text, color, fontType);
		BufferedImage textString = fontFactory.generateString(element);
		textString = ImageUtils.padImage(textString, new Dimension(width, fontType.getSize()));
		graphics.drawImage(textString, x, y, null);
	}

	public abstract void resetScreen();
	protected abstract void handleCommand(ScreenCommand command);
	protected abstract void handleKeyCommand(KeyCommand command);
	protected abstract void paintComponent(Graphics2D graphics);	//repaint screen
	protected abstract void defineClickableRegions();
}
