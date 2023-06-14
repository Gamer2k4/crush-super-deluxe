package main.presentation.screens.stats;

import main.data.entities.Stats;
import main.presentation.ImageType;

public class TeamStatsTeamCarnageScreen extends AbstractTeamStatsScreen
{
	protected TeamStatsTeamCarnageScreen(AbstractTeamStatsParentScreen parentScreen)
	{
		super(parentScreen, ImageType.SCREEN_TEAM_STATS_TCARNAGE);
		
		defineColumnStatIndex(0, Stats.STATS_INJURIES_FOR);
		defineColumnStatIndex(1, Stats.STATS_KILLS_FOR);
		defineColumnStatIndex(2, Stats.STATS_CARNAGE_FOR);
		
		setColumnHighlight(0, "Injuries", 352, "For", 367);
		setColumnHighlight(1, "Kills", 418, "For", 424);
		setColumnHighlight(2, "Total", 475, "For", 481);
	}
}
