package main.presentation.legacy.framework;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import main.presentation.common.image.ImageType;
import main.presentation.common.image.LegacyImageFactory;

public class GameSelectScreen extends AbstractLegacyScreen
{
	public GameSelectScreen(ActionListener listener)
	{
		super(listener, ImageType.MAIN_MENU);
	}
	
	@Override
	protected void defineClickableRegions()
	{
		BufferedImage allHighlights = LegacyImageFactory.getInstance().getImage(ImageType.MENU);
		Dimension regionDimension = new Dimension(160, 16);
		ScreenCommand[] commands = {ScreenCommand.EXHIBITION_TEAM_SELECT, ScreenCommand.TOURNAMENT_TEAM_SELECT, ScreenCommand.LEAGUE_TEAM_SELECT, ScreenCommand.EXIT};
		
		for (int i = 0; i < 4; i++)
		{
			BufferedImage highlight = allHighlights.getSubimage(1, (20 * i) + 1, 158, 14);
			Point regionOrigin = new Point(426, 279 + (20 * i));
			Rectangle regionArea = new Rectangle(regionOrigin, regionDimension);
			ClickableRegion region = new ClickableRegion(regionOrigin, highlight, RegionTriggerType.HOVER, commands[i], RegionTriggerType.SINGLE_CLICK);
			createClickZone(regionArea, region);
		}
	}

	@Override
	protected void paintComponent(Graphics2D g2)
	{
		g2.drawImage(imageFactory.getImage(ImageType.MENU2), 425, 278, null);
	}

	@Override
	protected void handleCommand(ScreenCommand command)
	{
		// TODO Auto-generated method stub
		System.out.println("Command received: " + command);
	}

	@Override
	protected void handleKeyCommand(KeyCommand command) {}

	@Override
	public void resetScreen() {}
}
