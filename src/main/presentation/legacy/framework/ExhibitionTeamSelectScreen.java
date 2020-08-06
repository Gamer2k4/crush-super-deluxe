package main.presentation.legacy.framework;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import main.presentation.common.image.ImageType;
import main.presentation.legacy.teameditor.ScreenCommand;

public class ExhibitionTeamSelectScreen extends AbstractTeamSelectScreen
{
	protected static final Point coordsButtonBack = new Point(585, 342);
	
	public ExhibitionTeamSelectScreen(ActionListener listener)
	{
		super(listener, ImageType.BG_BG1, ImageType.SCREEN_EXHIBITION_START);
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
		createClickZone(new Rectangle(coordsButtonBack, buttonDimSmall2), ClickableRegion.smallButton2(coordsButtonBack, ScreenCommand.MAIN_SCREEN));
	}

	@Override
	protected List<Point> getHelmetLocations()
	{
		List<Point> locations = new ArrayList<Point>();
		locations.add(new Point(305, 142));
		locations.add(new Point(205, 246));
		locations.add(new Point(405, 246));
		return locations;
	}
}
