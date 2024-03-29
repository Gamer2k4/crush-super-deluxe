package main.presentation.legacy.stats.gamestats;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import main.data.entities.Stats;
import main.presentation.common.image.ImageType;
import main.presentation.common.image.ImageUtils;
import main.presentation.common.image.LegacyImageFactory;
import main.presentation.legacy.common.FontType;
import main.presentation.legacy.common.LegacyTextElement;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.legacy.framework.ScreenCommand;
import main.presentation.legacy.stats.StatsTable;
import main.presentation.legacy.stats.StatsTableRow;

public class LegacyStatsScreenRushing extends AbstractLegacyStatsScreenPanel
{
	private static final long serialVersionUID = -3090205738857180063L;

	public LegacyStatsScreenRushing(ActionListener listener)
	{
		super(LegacyImageFactory.getInstance().getImage(ImageType.SCREEN_STATS_RUSHING));
		setActionListener(listener);
	}

	@Override
	protected void paintText(Graphics2D graphics)
	{
		// paint top teams
		if (!topTeams.isEmpty())
		{
			for (int i = 0; i < topTeams.size(); i++)
			{
				String[] statsStrings = topTeams.get(i);

				int y = 104 + (20 * i);
				paintTextElement(graphics, 73, y, statsStrings[0], FontType.FONT_SMALL, new Color(Integer.valueOf(statsStrings[6])));
				paintTextElement(graphics, 250, y, statsStrings[1], FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);
				paintTextElement(graphics, 307, y, statsStrings[2], FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);
				paintTextElement(graphics, 364, y, statsStrings[3], FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);
				paintTextElement(graphics, 421, y, statsStrings[4], FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);

				// 250, 307, 364, 421, 478, 535
			}
		}

		// paint top players
		if (topPlayers.isEmpty())
			return;

		for (int i = 0; i < topPlayers.size(); i++)
		{
			String[] statsStrings = topPlayers.get(i);

			Color teamColor = new Color(Integer.valueOf(statsStrings[5]));

			int y = 237 + (20 * i); // the original game gets this wrong - the numbers are either a pixel above or below the name, depending on the screen
			paintTextElement(graphics, 87, y, statsStrings[0], FontType.FONT_SMALL, teamColor);
			paintTextElement(graphics, 364, y, statsStrings[2], FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);
			paintTextElement(graphics, 420, y, statsStrings[3], FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);
			paintTextElement(graphics, 478, y, statsStrings[4], FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);

			BufferedImage teamName = fontFactory.generateString(new LegacyTextElement(statsStrings[1], teamColor, FontType.FONT_SMALL2));
			graphics.drawImage(ImageUtils.padImage(teamName, new Dimension(78, FontType.FONT_SMALL2.getSize())), 251, y, null);
		}
	}

	@Override
	protected void handleCommand(ScreenCommand command)
	{
		super.handleCommand(command);
	}

	@Override
	public void resetScreen()
	{
		super.repaint();
	}

	@Override
	protected void setTopThree(StatsTable playerStatsTable, StatsTable teamStatsTable)
	{
		// these are pulled directly from GUIPlayerStats, so they should be nicely formatted already
		topPlayers = playerStatsTable.getTopPlayers(3, StatsTableRow.STATS_RUSHING_AVERAGE, false, StatsTableRow.STATS_PLAYER_NAME, StatsTableRow.STATS_TEAM_NAME,
				Stats.STATS_RUSHING_ATTEMPTS, Stats.STATS_RUSHING_YARDS, StatsTableRow.STATS_RUSHING_AVERAGE, StatsTableRow.STATS_COLOR_FG_RGB);

		topTeams = teamStatsTable.getTopPlayers(3, StatsTableRow.STATS_PLAYER_NAME, true, StatsTableRow.STATS_PLAYER_NAME, Stats.STATS_RUSHING_ATTEMPTS,
				Stats.STATS_RUSHING_YARDS, StatsTableRow.STATS_RUSHING_AVERAGE, Stats.STATS_BALL_CONTROL, Stats.STATS_FUMBLES, StatsTableRow.STATS_COLOR_FG_RGB); // TODO: figure out how to sort on "nothing"
	}
}
