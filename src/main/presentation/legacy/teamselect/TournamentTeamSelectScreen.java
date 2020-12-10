package main.presentation.legacy.teamselect;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import main.presentation.common.image.ImageType;
import main.presentation.legacy.framework.ClickableRegion;
import main.presentation.legacy.framework.ScreenCommand;

public class TournamentTeamSelectScreen extends AbstractTeamSelectScreen
{
	protected static final Point coordsButtonBack = new Point(586, 342);
	
	public TournamentTeamSelectScreen(ActionListener listener)
	{
		super(listener, ImageType.BG_BG3, ImageType.SCREEN_TOURNAMENT_START);
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
		createClickZone(new Rectangle(coordsButtonBack, buttonDimSmall), ClickableRegion.smallButton(coordsButtonBack, ScreenCommand.MAIN_SCREEN));
	}

	@Override
	protected List<Point> getHelmetLocations()
	{
		List<Point> locations = new ArrayList<Point>();
		locations.add(new Point(11, 11));
		locations.add(new Point(11, 49));
		locations.add(new Point(11, 87));
		locations.add(new Point(11, 146));
		locations.add(new Point(11, 184));
		locations.add(new Point(11, 222));
		locations.add(new Point(11, 281));
		locations.add(new Point(11, 319));
		locations.add(new Point(11, 357));
		locations.add(new Point(242, 146));
		locations.add(new Point(242, 184));
		locations.add(new Point(242, 222));
		return locations;
	}
}
