package main.presentation.screens.stats;

import main.data.entities.Stats;
import main.presentation.ImageType;

public class TeamStatsTeamCheckingScreen extends AbstractTeamStatsScreen
{
	protected TeamStatsTeamCheckingScreen(AbstractTeamStatsParentScreen parentScreen)
	{
		super(parentScreen, ImageType.SCREEN_TEAM_STATS_TCHECK);
		
		defineColumnStatIndex(0, Stats.STATS_CHECKS_THROWN);
		defineColumnStatIndex(1, Stats.STATS_CHECKS_LANDED);
		defineColumnStatIndex(2, Stats.STATS_CHECKING_AVERAGE);
		defineColumnStatIndex(3, Stats.STATS_SACKS_FOR);
		
		setColumnHighlight(0, "Checks", 359, "Thrown", 359);
		setColumnHighlight(1, "Checks", 416, "Landed", 416);
		setColumnHighlight(2, "Checking", 467, "Average", 470);
		setColumnHighlight(3, "Sacks", 533, "For", 539);
	}
}
