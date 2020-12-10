package main.presentation.legacy.stats.gamestats;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import main.data.Data;
import main.data.entities.Player;
import main.data.entities.Stats;
import main.data.entities.Team;
import main.presentation.common.image.ImageType;
import main.presentation.common.image.LegacyImageFactory;
import main.presentation.legacy.common.AbstractLegacyImageBasedScreenPanel;
import main.presentation.legacy.framework.ScreenCommand;
import main.presentation.legacy.stats.StatsTable;

public abstract class AbstractLegacyStatsScreenPanel extends AbstractLegacyImageBasedScreenPanel
{
	protected static final Point coordsButtonRushing = new Point(231, 359);
	protected static final Point coordsButtonChecking = new Point(316, 359);
	protected static final Point coordsButtonCarnage = new Point(401, 359);
	protected static final Point coordsButtonOverview = new Point(486, 359);
	protected static final Point coordsButtonDone = new Point(586, 339);
	protected static final Point coordsButtonTop = new Point(583, 221);
	protected static final Point coordsButtonBottom = new Point(583, 241);

	protected static int currentStatsScreen;

	protected List<String[]> topPlayers = new ArrayList<String[]>();
	protected List<String[]> topTeams = new ArrayList<String[]>();

	private static final long serialVersionUID = -5379168441155775384L;

	public AbstractLegacyStatsScreenPanel(BufferedImage baseImage)
	{
		super(LegacyImageFactory.getInstance().getImage(ImageType.BG_BG6), baseImage);

		addClickZone(new Rectangle(coordsButtonRushing, buttonDimLarge), ScreenCommand.SHOW_RUSHING);
		addClickZone(new Rectangle(coordsButtonChecking, buttonDimLarge), ScreenCommand.SHOW_CHECKING);
		addClickZone(new Rectangle(coordsButtonCarnage, buttonDimLarge), ScreenCommand.SHOW_CARNAGE);
		addClickZone(new Rectangle(coordsButtonOverview, buttonDimLarge), ScreenCommand.SHOW_OVERVIEW);
		addClickZone(new Rectangle(coordsButtonDone, buttonDimSmallStats), ScreenCommand.DONE);
		currentStatsScreen = 0;
	}

	public void updateDataImpl(Data dataImpl)
	{
		StatsTable playerStatsTable = new StatsTable();
		StatsTable teamStatsTable = new StatsTable();
		List<Team> teams = dataImpl.getTeams();

		for (Team team : teams)
		{
			if (team == null)
				continue;

			teamStatsTable.addTeam(team, Stats.GAME_STATS);

			for (int i = 0; i < 9; i++)
			{
				Player player = team.getPlayer(i);

				if (player != null)
					playerStatsTable.addPlayer(player, team, Stats.GAME_STATS);
			}
		}

		setTopThree(playerStatsTable, teamStatsTable);
		repaint();
	}

	protected String formatAverage(String stat)
	{
		String formattedStat = stat;
		
		if (stat.length() == 4 && stat.charAt(1) == '.')
		{
			if (stat.charAt(0) == '1')
				return "99";

			formattedStat = stat.substring(2);
		}

		return formattedStat;
	}

	protected abstract void setTopThree(StatsTable playerStatsTable, StatsTable teamStatsTable);

	@Override
	protected void paintImages(Graphics2D graphics)
	{
	}

	@Override
	protected void paintButtonShading(Graphics2D graphics)
	{
		for (int i = 0; i < 4; i++)
		{
			int x = 231 + (85 * i);
			int y = 359;

			if (currentStatsScreen == i)
				graphics.drawImage(imageFactory.getImage(ImageType.BUTTON_LARGE_CLICKED), x, y, null);
			else
				graphics.drawImage(imageFactory.getImage(ImageType.BUTTON_LARGE_NORMAL), x, y, null);
		}
	}

	@Override
	protected void handleCommand(ScreenCommand command)
	{
		System.out.println("Command received for stats screen: " + command);

		int lastScreen = currentStatsScreen;

		if (ScreenCommand.SHOW_RUSHING.equals(command))
			currentStatsScreen = 0;
		else if (ScreenCommand.SHOW_CHECKING.equals(command))
			currentStatsScreen = 1;
		else if (ScreenCommand.SHOW_CARNAGE.equals(command))
			currentStatsScreen = 2;
		else if (ScreenCommand.SHOW_OVERVIEW.equals(command))
			currentStatsScreen = 3;
		else if (ScreenCommand.DONE.equals(command))
			currentStatsScreen = -1;

		if (lastScreen != currentStatsScreen || ScreenCommand.SHOW_SACK_TAB.equals(command) || ScreenCommand.SHOW_CHECK_TAB.equals(command)
				|| ScreenCommand.SHOW_MISC_TAB.equals(command) || ScreenCommand.SHOW_MVP_TAB.equals(command))
			fireAction(command);

		repaint();
	}
}
