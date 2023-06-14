package main.presentation.screens.stats;

import main.data.entities.Stats;
import main.presentation.ImageType;

public class TeamStatsTeamRushingScreen extends AbstractTeamStatsScreen
{
	protected TeamStatsTeamRushingScreen(AbstractTeamStatsParentScreen parentScreen)
	{
		super(parentScreen, ImageType.SCREEN_TEAM_STATS_TRUSH);
		
		defineColumnStatIndex(0, Stats.STATS_RUSHING_ATTEMPTS);
		defineColumnStatIndex(1, Stats.STATS_RUSHING_YARDS);
		defineColumnStatIndex(2, Stats.STATS_RUSHING_AVERAGE);
		defineColumnStatIndex(3, Stats.STATS_GOALS_SCORED);
		
		setColumnHighlight(0, "Rushing", 355, "Attempts", 352);
		setColumnHighlight(1, "Rushing", 412, "Tiles", 418);
		setColumnHighlight(2, "Rushing", 469, "Average", 469);
		setColumnHighlight(3, "Goals", 532, "Scored", 529);
	}
}
