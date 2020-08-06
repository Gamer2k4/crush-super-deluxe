package main.presentation.legacy.stats.gamestats;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import main.data.entities.Stats;
import main.presentation.common.image.ImageType;
import main.presentation.common.image.ImageUtils;
import main.presentation.legacy.common.FontType;
import main.presentation.legacy.common.LegacyTextElement;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.legacy.stats.StatsTable;
import main.presentation.legacy.stats.StatsTableRow;

public class LegacyStatsScreenChecking extends AbstractLegacyStatsScreenChecking
{
	private static final long serialVersionUID = -1006232845319619268L;

	public LegacyStatsScreenChecking(ActionListener listener)
	{
		super(ImageType.SCREEN_STATS_CHK_CHECK, listener);
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

			Color teamColor = new Color(Integer.valueOf(statsStrings[5]));

			int y = 237 + (20 * i); // the original game gets this wrong - the numbers are either a pixel above or below the name, depending on the screen
			paintTextElement(graphics, 87, y, statsStrings[0], FontType.FONT_SMALL, teamColor);
			paintTextElement(graphics, 364, y, statsStrings[2], FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);
			paintTextElement(graphics, 420, y, statsStrings[3], FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);
			paintTextElement(graphics, 478, y, formatAverage(statsStrings[4]), FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);

			BufferedImage teamName = fontFactory.generateString(new LegacyTextElement(statsStrings[1], teamColor, FontType.FONT_SMALL2));
			graphics.drawImage(ImageUtils.padImage(teamName, new Dimension(78, FontType.FONT_SMALL2.getSize())), 251, y, null);
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
		topPlayers = playerStatsTable.getTopPlayers(3, StatsTableRow.STATS_CHECKING_AVERAGE, false, StatsTableRow.STATS_PLAYER_NAME, StatsTableRow.STATS_TEAM_NAME,
				Stats.STATS_CHECKS_THROWN, Stats.STATS_CHECKS_LANDED, StatsTableRow.STATS_CHECKING_AVERAGE, StatsTableRow.STATS_COLOR_FG_RGB);

		topTeams = teamStatsTable.getTopPlayers(3, StatsTableRow.STATS_PLAYER_NAME, true, StatsTableRow.STATS_PLAYER_NAME, Stats.STATS_CHECKS_THROWN,
				Stats.STATS_CHECKS_LANDED, StatsTableRow.STATS_CHECKING_AVERAGE, Stats.STATS_SACKS_FOR, Stats.STATS_SACKS_AGAINST, StatsTableRow.STATS_COLOR_FG_RGB);
	}
}
