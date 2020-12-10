package main.presentation.legacy.teamselect;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import main.data.entities.Team;
import main.presentation.common.Logger;
import main.presentation.common.image.ImageType;
import main.presentation.legacy.framework.AbstractLegacyScreen;
import main.presentation.legacy.framework.ClickableRegion;
import main.presentation.legacy.framework.KeyCommand;
import main.presentation.legacy.framework.ScreenCommand;
import main.presentation.startupscreen.TeamRecordTracker;
import main.presentation.teameditor.common.TeamImages;
import main.presentation.teameditor.common.TeamUpdater;

public abstract class AbstractTeamSelectScreen extends AbstractLegacyScreen
{
	private static final int MAX_TEAMS = 12;
	
	protected Team[] teams;
	protected BufferedImage[] helmetImages;
	protected TeamUpdater teamUpdater;
	protected TeamRecordTracker teamRecordTracker;
	
	private BufferedImage foregroundImage;
	
	public AbstractTeamSelectScreen(ActionListener listener, ImageType backgroundImage, ImageType foregroundImage)
	{
		super(listener, backgroundImage);
		
		this.teamRecordTracker = new TeamRecordTracker();
		this.foregroundImage = imageFactory.getImage(foregroundImage);
		this.teamUpdater = new TeamUpdater();
		this.teams = new Team[MAX_TEAMS];
		this.helmetImages = new BufferedImage[MAX_TEAMS];
		
		resetScreen();
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
		int teamIndex = 0;
		for (Point coords : getHelmetLocations())
		{
			ScreenCommand command = ScreenCommand.valueOf("EDIT_TEAM_" + teamIndex);
			createClickZone(new Rectangle(coords, helmetDim), new ClickableRegion(coords, command));
			teamIndex++;
		}
	}

	private void refreshHelmetImages()
	{
		for (int i = 0; i < MAX_TEAMS; i++)
		{
			if (helmetImages[i] == null)
				helmetImages[i] = TeamImages.getHelmetImage(teams[i]);
		}
	}

	private void paintHelmetImages(Graphics2D g2)
	{
		List<Point> helmetLocations = getHelmetLocations();
		
		for (int i = 0; i < helmetLocations.size(); i++)
		{
			Point coords = helmetLocations.get(i);
			g2.drawImage(helmetImages[i], coords.x, coords.y, null);
		}
	}
	
	protected abstract List<Point> getHelmetLocations();
	
	public void updateTeam(int index, Team team)
	{
		if (index < 0 || index >= MAX_TEAMS)
		{
			Logger.warn("Cannot update team at slot " + index + "; index is out of bounds.");
			return;
		}
		
		teams[index] = team;		//TODO: this doesn't QUITE work for the tournament screen, where the same team can be in both rounds
		helmetImages[index] = null;
		refreshHelmetImages();
		updateScreenImage();
	}
	
	public Team getTeam(int index)
	{
		if (index < 0 || index >= MAX_TEAMS)
		{
			Logger.warn("Cannot get team at slot " + index + "; index is out of bounds.");
			return null;
		}
		
		return teams[index];
	}
	
	public List<Team> getTeams()
	{
		List<Team> teamList = new ArrayList<Team>();
		
		for (int i = 0; i < getTotalTeams(); i++)
			teamList.add(teams[i]);
			
		return teamList;
	}

	public int getBudget()
	{
		return 900;
	}

	public int getGoal()
	{
		return 1;
	}
	
	public boolean isSeasonStarted()
	{
		return teamRecordTracker.seasonStarted();
	}
	
	public abstract boolean isGameReadyToStart();
	protected abstract int getTotalTeams();
	public abstract int getSeasonWinner();
	
	@Override
	public void resetScreen()
	{
		for (int i = 0; i < MAX_TEAMS; i++)
		{
			teams[i] = new Team();
			helmetImages[i] = null;
		}
		
		refreshHelmetImages();
		
		//also reset budget, goal, editable, etc.
	}

	@Override
	protected void handleCommand(ScreenCommand command) {}
	
	@Override
	protected void handleKeyCommand(KeyCommand command) {}
}
