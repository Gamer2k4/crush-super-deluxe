package main.presentation.screens.stats;

import java.awt.event.ActionListener;

import com.badlogic.gdx.Game;

import main.presentation.common.ScreenCommand;

public class TeamStatsTeamParentScreen extends AbstractTeamStatsParentScreen
{
	public TeamStatsTeamParentScreen(Game sourceGame, ActionListener eventListener)
	{
		super(sourceGame, eventListener);
		
		addScreenMapping(0, ScreenCommand.STATS_TRUSH, new TeamStatsTeamRushingScreen(this));
		addScreenMapping(1, ScreenCommand.STATS_TCHECK, new TeamStatsTeamCheckingScreen(this));
		addScreenMapping(2, ScreenCommand.STATS_TCARNAGE, new TeamStatsTeamCarnageScreen(this));
		addScreenMapping(3, ScreenCommand.STATS_TMISC, new TeamStatsTeamMiscScreen(this));
	}
}
