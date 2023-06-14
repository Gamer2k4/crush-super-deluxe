package main.presentation.screens.stats;

import main.data.entities.Stats;
import main.presentation.ImageType;

public class TeamStatsTeamMiscScreen extends AbstractTeamStatsScreen
{
	protected TeamStatsTeamMiscScreen(AbstractTeamStatsParentScreen parentScreen)
	{
		super(parentScreen, ImageType.SCREEN_TEAM_STATS_TMISC);
		
		defineColumnStatIndex(0, Stats.STATS_AVERAGE_RATING);
		defineColumnStatIndex(1, Stats.STATS_HIGHEST_RATING);
		
		setColumnHighlight(0, "Avg.", 367, "Rating", 358);
		setColumnHighlight(1, "Highest", 412, "Rating", 415);
	}
}
