package main.presentation.legacy.stats.gamestats;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;

import main.data.entities.Stats;
import main.presentation.common.image.ImageType;
import main.presentation.legacy.common.FontType;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.legacy.stats.StatsTable;
import main.presentation.legacy.stats.StatsTableRow;

public class LegacyStatsScreenOverview extends AbstractLegacyStatsScreenOverview
{
	private static final long serialVersionUID = -6223274103134556039L;

	public LegacyStatsScreenOverview(ActionListener listener)
	{
		super(ImageType.SCREEN_STATS_OVERVIEW, listener);
	}

	@Override
	protected void paintText(Graphics2D graphics)
	{
		super.paintText(graphics);

		// paint top players
		if (topPlayers.isEmpty())
			return;

		for (int i = 0; i < topPlayers.size(); i++)
		{
			String[] statsStrings = topPlayers.get(i);

			Color teamColor = new Color(Integer.valueOf(statsStrings[6]));

			int y = 237 + (20 * i); // the original game gets this wrong - the numbers are either a pixel above or below the name, depending on the screen
			paintTextElement(graphics, 87, y, statsStrings[0], FontType.FONT_SMALL, teamColor);
			paintTextElement(graphics, 250, y, statsStrings[1], FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);
			paintTextElement(graphics, 307, y, statsStrings[2], FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);
			paintTextElement(graphics, 364, y, statsStrings[3], FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);
			paintTextElement(graphics, 421, y, statsStrings[4], FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);
			paintTextElement(graphics, 469, y, statsStrings[5], FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);
		}
	}

	@Override
	protected void paintButtonShading(Graphics2D graphics)
	{
		super.paintButtonShading(graphics);

		graphics.drawImage(imageFactory.getImage(ImageType.BUTTON_SMALL_CLICKED), 583, 221, null);
		graphics.drawImage(imageFactory.getImage(ImageType.BUTTON_SMALL_NORMAL), 583, 241, null);
	}

	@Override
	protected void setTopThree(StatsTable playerStatsTable, StatsTable teamStatsTable)
	{
		// these are pulled directly from GUIPlayerStats, so they should be nicely formatted already
		topPlayers = teamStatsTable.getTopPlayers(3, StatsTableRow.STATS_PLAYER_NAME, true, StatsTableRow.STATS_PLAYER_NAME, Stats.STATS_BALL_CONTROL,
				Stats.STATS_FUMBLES, Stats.STATS_PADS_ACTIVATED, Stats.STATS_EJECTIONS, Stats.STATS_MUTATIONS, StatsTableRow.STATS_COLOR_FG_RGB);

		topTeams = teamStatsTable.getTopPlayers(3, StatsTableRow.STATS_PLAYER_NAME, true, StatsTableRow.STATS_PLAYER_NAME, StatsTableRow.STATS_TOTAL_CARNAGE_FOR,
				StatsTableRow.STATS_TOTAL_CARNAGE_AGAINST, Stats.STATS_CHECKS_THROWN, Stats.STATS_RUSHING_YARDS, StatsTableRow.STATS_TEAM_VALUE,
				StatsTableRow.STATS_COLOR_FG_RGB);	// TODO: needs stats for damaged value
	}

}
