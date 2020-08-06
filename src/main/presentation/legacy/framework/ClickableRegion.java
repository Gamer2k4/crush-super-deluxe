package main.presentation.legacy.framework;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;

import main.presentation.common.image.ImageType;
import main.presentation.common.image.ImageUtils;
import main.presentation.common.image.LegacyImageFactory;
import main.presentation.legacy.teameditor.ScreenCommand;

public class ClickableRegion
{
	private Point origin;
	private BufferedImage highlight;
	private RegionTriggerType highlightTrigger;
	private ScreenCommand command;
	private RegionTriggerType commandTrigger;
	
	private static LegacyImageFactory imageFactory = LegacyImageFactory.getInstance();

	public ClickableRegion(Point origin, ScreenCommand command)
	{
		this(origin, ImageUtils.createBlankBufferedImage(new Dimension(1, 1)), RegionTriggerType.DOUBLE_CLICK, command,
				RegionTriggerType.SINGLE_CLICK);
	}

	public ClickableRegion(Point origin, BufferedImage highlight, RegionTriggerType highlightTrigger, ScreenCommand command,
			RegionTriggerType commandTrigger)
	{
		this.origin = origin;
		this.highlight = highlight;
		this.highlightTrigger = highlightTrigger;
		this.command = command;
		this.commandTrigger = commandTrigger;
	}

	public Point getOrigin()
	{
		return origin;
	}

	public BufferedImage getHighlight()
	{
		return highlight;
	}

	public ScreenCommand getCommand()
	{
		return command;
	}

	public boolean highlightOnClick()
	{
		return highlightTrigger == RegionTriggerType.SINGLE_CLICK;
	}

	public boolean highlightOnHover()
	{
		return highlightTrigger == RegionTriggerType.HOVER;
	}

	public boolean activateOnClick()
	{
		return commandTrigger == RegionTriggerType.SINGLE_CLICK;
	}

	public boolean activatetOnDoubleClick()
	{
		return commandTrigger == RegionTriggerType.DOUBLE_CLICK;
	}
	
	public static ClickableRegion smallButton(Point origin, ScreenCommand command)
	{
		return new ClickableRegion(origin, imageFactory.getImage(ImageType.BUTTON_SMALL_CLICKED), RegionTriggerType.SINGLE_CLICK, command, RegionTriggerType.SINGLE_CLICK);
	}
	
	public static ClickableRegion smallButton2(Point origin, ScreenCommand command)
	{
		return new ClickableRegion(origin, imageFactory.getImage(ImageType.BUTTON_SMALL2_CLICKED), RegionTriggerType.SINGLE_CLICK, command, RegionTriggerType.SINGLE_CLICK);
	}
	
	public static ClickableRegion largeButton(Point origin, ScreenCommand command)
	{
		return new ClickableRegion(origin, imageFactory.getImage(ImageType.BUTTON_LARGE_CLICKED), RegionTriggerType.SINGLE_CLICK, command, RegionTriggerType.SINGLE_CLICK);
	}
}
