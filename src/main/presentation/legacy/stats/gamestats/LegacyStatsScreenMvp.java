package main.presentation.legacy.stats.gamestats;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import main.data.Data;
import main.data.entities.Player;
import main.data.entities.Stats;
import main.data.entities.Team;
import main.presentation.common.image.ImageType;
import main.presentation.common.image.ImageUtils;
import main.presentation.common.image.LegacyColorReplacer;
import main.presentation.legacy.common.FontType;
import main.presentation.legacy.common.LegacyTextElement;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.legacy.stats.StatsTable;
import main.presentation.legacy.stats.StatsTableRow;

public class LegacyStatsScreenMvp extends AbstractLegacyStatsScreenOverview
{
	private static final long serialVersionUID = -6223274103134556039L;

	private Player scoringPlayer = null;
	private Team scoringTeam = null;
	private String turnFound = "NA";
	private String turnScored = "NA";

	public LegacyStatsScreenMvp(ActionListener listener)
	{
		super(ImageType.SCREEN_STATS_MVP, listener);
	}

	@Override
	public void updateDataImpl(Data dataImpl)
	{
		scoringPlayer = dataImpl.getBallCarrier();
		scoringTeam = dataImpl.getTeamOfPlayer(scoringPlayer);
		turnFound = "0" + dataImpl.getTurnBallFound();
		turnScored = "0" + dataImpl.getTurnBallScored();

		turnFound = turnFound.substring(turnFound.length() - 2, turnFound.length());
		turnScored = turnScored.substring(turnScored.length() - 2, turnScored.length());

		super.updateDataImpl(dataImpl);
	}

	@Override
	protected void paintText(Graphics2D graphics)
	{
		super.paintText(graphics);

		// paint top players
		if (topPlayers.isEmpty())
			return;

		String[] statsStrings = topPlayers.get(0);

		Color winningTeamColor = scoringTeam.teamColors[0];
		
		String rating = "0" + statsStrings[13];
		rating = rating.substring(rating.length() - 3, rating.length());
		
		String scored = "NO";
		if (Integer.valueOf(statsStrings[14]) > 0)
			scored = "YES";

		int x = 213;

		paintTextElement(graphics, x, 209, statsStrings[3], FontType.FONT_SMALL2, LegacyUiConstants.COLOR_LEGACY_BLACK);
		paintTextElement(graphics, x, 217, "#" + statsStrings[2] + " " + statsStrings[0], FontType.FONT_SMALL2, LegacyUiConstants.COLOR_LEGACY_BLACK);
		
		x = 231;
		
		paintTextElement(graphics, x, 240, statsStrings[4], FontType.FONT_SMALL2, LegacyUiConstants.COLOR_LEGACY_GOLD);
		paintTextElement(graphics, x, 248, statsStrings[5], FontType.FONT_SMALL2, LegacyUiConstants.COLOR_LEGACY_GOLD);
		paintTextElement(graphics, x, 256, formatAverage(statsStrings[6]), FontType.FONT_SMALL2, LegacyUiConstants.COLOR_LEGACY_GOLD);
		
		paintTextElement(graphics, x, 266, statsStrings[7], FontType.FONT_SMALL2, LegacyUiConstants.COLOR_LEGACY_GOLD);
		paintTextElement(graphics, x, 274, statsStrings[8], FontType.FONT_SMALL2, LegacyUiConstants.COLOR_LEGACY_GOLD);
		paintTextElement(graphics, x, 282, formatAverage(statsStrings[9]), FontType.FONT_SMALL2, LegacyUiConstants.COLOR_LEGACY_GOLD);
		
		x = 321;
		
		paintTextElement(graphics, x, 240, statsStrings[10], FontType.FONT_SMALL2, LegacyUiConstants.COLOR_LEGACY_GOLD);
		paintTextElement(graphics, x, 248, statsStrings[11], FontType.FONT_SMALL2, LegacyUiConstants.COLOR_LEGACY_GOLD);
		paintTextElement(graphics, x, 256, formatAverage(statsStrings[12]), FontType.FONT_SMALL2, LegacyUiConstants.COLOR_LEGACY_GOLD);
		
		paintTextElement(graphics, x, 266, statsStrings[15], FontType.FONT_SMALL2, LegacyUiConstants.COLOR_LEGACY_GOLD);
		paintTextElement(graphics, x, 274, scored, FontType.FONT_SMALL2, LegacyUiConstants.COLOR_LEGACY_GOLD);
		paintTextElement(graphics, x, 282, rating, FontType.FONT_SMALL2, LegacyUiConstants.COLOR_LEGACY_GOLD);
		
		paintTextElement(graphics, 478, 241, turnFound, FontType.FONT_SMALL2, LegacyUiConstants.COLOR_LEGACY_GOLD);
		paintTextElement(graphics, 478, 257, turnScored, FontType.FONT_SMALL2, LegacyUiConstants.COLOR_LEGACY_GOLD);
		
		BufferedImage teamName = fontFactory.generateString(new LegacyTextElement(scoringTeam.teamName, winningTeamColor), FontType.FONT_SMALL2);
		graphics.drawImage(ImageUtils.padImage(teamName, new Dimension(123, FontType.FONT_SMALL.getSize())), 398, 274, null);
		
		String playerNameAndNumber = "#" + String.valueOf(scoringPlayer.getRosterIndex()) + " " + scoringPlayer.name;
		BufferedImage playerName = fontFactory.generateString(new LegacyTextElement(playerNameAndNumber, LegacyUiConstants.COLOR_LEGACY_RED), FontType.FONT_SMALL2);
		graphics.drawImage(ImageUtils.padImage(playerName, new Dimension(123, FontType.FONT_SMALL.getSize())), 398, 282, null);
	}

	@Override
	protected void paintImages(Graphics2D graphics)
	{
		if (topPlayers.isEmpty())
		{
			graphics.drawImage(imageFactory.getImage(ImageType.PROFILE_XJS9000_S), 63, 211, null);
			return;
		}

		String[] statsStrings = topPlayers.get(0);
		String race = "PROFILE_" + statsStrings[1] + "_S";
		Color fgColor = scoringTeam.teamColors[0];
		Color bgColor = scoringTeam.teamColors[1];

		LegacyColorReplacer replacer = LegacyColorReplacer.getInstance();
		BufferedImage mvpPlayer = replacer.setColors(imageFactory.getImage(ImageType.valueOf(race)), fgColor, bgColor,
				new Color(0, 0, 0, 0));

		graphics.drawImage(mvpPlayer, 63, 211, null);
	}

	@Override
	protected void paintButtonShading(Graphics2D graphics)
	{
		super.paintButtonShading(graphics);

		graphics.drawImage(imageFactory.getImage(ImageType.BUTTON_SMALL_NORMAL), 583, 221, null);
		graphics.drawImage(imageFactory.getImage(ImageType.BUTTON_SMALL_CLICKED), 583, 241, null);
	}

	@Override
	protected void setTopThree(StatsTable playerStatsTable, StatsTable teamStatsTable)
	{
		// these are pulled directly from GUIPlayerStats, so they should be nicely formatted already
		topPlayers = playerStatsTable.getTopPlayers(1, Stats.STATS_HIGHEST_RATING, false, StatsTableRow.STATS_PLAYER_NAME, StatsTableRow.STATS_PLAYER_RACE,
				StatsTableRow.STATS_PLAYER_ROSTER_INDEX, StatsTableRow.STATS_TEAM_NAME, Stats.STATS_CHECKS_THROWN, Stats.STATS_CHECKS_LANDED,				//2
				StatsTableRow.STATS_CHECKING_AVERAGE, StatsTableRow.STATS_TOTAL_CARNAGE_FOR, Stats.STATS_INJURIES_FOR, Stats.STATS_KILLS_FOR,					//6
				Stats.STATS_RUSHING_ATTEMPTS, Stats.STATS_RUSHING_YARDS, StatsTableRow.STATS_RUSHING_AVERAGE, Stats.STATS_HIGHEST_RATING,					//10
				Stats.STATS_GOALS_SCORED, Stats.STATS_PADS_ACTIVATED);

		topTeams = teamStatsTable.getTopPlayers(3, StatsTableRow.STATS_PLAYER_NAME, true, StatsTableRow.STATS_PLAYER_NAME, StatsTableRow.STATS_TOTAL_CARNAGE_FOR,
				StatsTableRow.STATS_TOTAL_CARNAGE_AGAINST, Stats.STATS_CHECKS_THROWN, Stats.STATS_RUSHING_YARDS, StatsTableRow.STATS_TEAM_VALUE,
				StatsTableRow.STATS_COLOR_FG_RGB);	// TODO: needs stats for damaged value
	}

}
