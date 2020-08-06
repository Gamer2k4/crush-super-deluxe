package main.presentation.legacy.framework;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import main.presentation.common.image.ImageType;
import main.presentation.legacy.teameditor.ScreenCommand;

public class LeagueTeamSelectScreen extends AbstractTeamSelectScreen
{
	protected static final Point coordsButtonBack = new Point(411, 325);
	
	public LeagueTeamSelectScreen(ActionListener listener)
	{
		super(listener, ImageType.BG_BG2, ImageType.SCREEN_LEAGUE_START);
	}

	@Override
	protected void handleCommand(ScreenCommand command)
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void defineClickableRegions()
	{
		super.defineClickableRegions();
		createClickZone(new Rectangle(coordsButtonBack, buttonDimLarge), ClickableRegion.largeButton(coordsButtonBack, ScreenCommand.MAIN_SCREEN));
	}

	@Override
	protected List<Point> getHelmetLocations()
	{
		List<Point> locations = new ArrayList<Point>();
		locations.add(new Point(27, 94));
		locations.add(new Point(27, 133));
		locations.add(new Point(27, 172));
		locations.add(new Point(27, 212));
		locations.add(new Point(231, 94));
		locations.add(new Point(231, 133));
		locations.add(new Point(231, 172));
		locations.add(new Point(231, 212));
		locations.add(new Point(435, 94));
		locations.add(new Point(435, 133));
		locations.add(new Point(435, 172));
		locations.add(new Point(435, 212));
		return locations;
	}
}
