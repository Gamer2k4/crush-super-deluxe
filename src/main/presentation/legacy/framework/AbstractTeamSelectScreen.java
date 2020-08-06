package main.presentation.legacy.framework;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.List;

import main.data.entities.Team;
import main.presentation.common.image.ImageType;
import main.presentation.teameditor.common.TeamImages;
import main.presentation.teameditor.common.TeamUpdater;

public abstract class AbstractTeamSelectScreen extends AbstractLegacyScreen
{
	protected Team[] teams;
	protected TeamUpdater teamUpdater;
	
	private BufferedImage foregroundImage;
	
	public AbstractTeamSelectScreen(ActionListener listener, ImageType backgroundImage, ImageType foregroundImage)
	{
		super(listener, backgroundImage);
		this.foregroundImage = imageFactory.getImage(foregroundImage);
		this.teamUpdater = new TeamUpdater();
		this.teams = new Team[12];
		
		for (int i = 0; i < 12; i++)
			teams[i] = new Team();
	}

	@Override
	protected void paintComponent(Graphics2D g2)
	{
		g2.drawImage(foregroundImage, 0, 0, null);
		paintHelmetImages(g2);
	}

	@Override
	protected void defineClickableRegions()
	{
		for (Point coords : getHelmetLocations())
		{
			createClickZone(new Rectangle(coords, helmetDim), new ClickableRegion(coords, null));		//TODO: get the proper screen command
		}
	}

	private void paintHelmetImages(Graphics2D g2)
	{
		List<Point> helmetLocations = getHelmetLocations();
		
		for (int i = 0; i < helmetLocations.size(); i++)
		{
			Point coords = helmetLocations.get(i);
			g2.drawImage(TeamImages.getHelmetImage(teams[i]), coords.x, coords.y, null);
		}
	}
	
	protected abstract List<Point> getHelmetLocations();
}
