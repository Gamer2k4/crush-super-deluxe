package main.presentation.legacy.stats;

import java.text.DecimalFormat;

import main.data.entities.Player;
import main.data.entities.Stats;
import main.data.entities.Team;
import main.data.factory.PlayerFactory;
import main.presentation.teameditor.common.GUIStatsFormatter;

public class StatsTableRow implements Comparable<StatsTableRow>
{
	public static final int STATS_RUSHING_AVERAGE = Stats.TOTAL_STATS;
	public static final int STATS_CHECKING_AVERAGE = Stats.TOTAL_STATS + 1;
	public static final int STATS_TOTAL_CARNAGE_FOR = Stats.TOTAL_STATS + 2;
	public static final int STATS_TOTAL_CARNAGE_AGAINST = Stats.TOTAL_STATS + 3;
	public static final int STATS_PLAYER_RACE = Stats.TOTAL_STATS + 4;
	public static final int STATS_PLAYER_ROSTER_INDEX = Stats.TOTAL_STATS + 5;
	public static final int STATS_PLAYER_NAME = Stats.TOTAL_STATS + 6;
	public static final int STATS_TEAM_NAME = Stats.TOTAL_STATS + 7;
	public static final int STATS_COLOR_FG_RGB = Stats.TOTAL_STATS + 8;
	public static final int STATS_TEAM_VALUE = Stats.TOTAL_STATS + 9;
	
	private static final int TOTAL_MISC_STATS = 10;
	private static final int FIELDED_TEAM_SIZE = 9;
	
	private String[] stats;
	private Player player;

	private static int keySortColumn = 0;
	
	public StatsTableRow(Player player, Team team, int statsType)
	{
		setupData(player, team, statsType);
	}
	
	//kind of hacky, but it should work - just make a "player" with the name of the team and all the stats of the team
	public StatsTableRow(Team team, int statsType)
	{
		GUIStatsFormatter.setStatsType(statsType);
		Player teamPlayer = PlayerFactory.createPlayerWithDefinedName(0, team.teamName);
		
		//Stats teamStats = team.getTeamStats(FIELDED_TEAM_SIZE, statsType);	//TODO: for now, just doing this at a game level (that is, the first 9 players)
		Stats teamStats = team.getLastGameStats();
		
		//this SHOULD work - the player should start with an empty stats field, and updating that field with "results" should just set it to those results
		if (statsType == Stats.GAME_STATS)
			teamPlayer.getLastGameStats().updateWithResults(teamStats);
		else if (statsType == Stats.SEASON_STATS)
			teamPlayer.getSeasonStats().updateWithResults(teamStats);
		else if (statsType == Stats.CAREER_STATS)
			teamPlayer.getCareerStats().updateWithResults(teamStats);
		
		setupData(teamPlayer, team, statsType);
	}
	
	private void setupData(Player player, Team team, int statsType)
	{
		this.player = player;
		GUIStatsFormatter.setPlayer(player);
		GUIStatsFormatter.setStatsType(statsType);

		stats = new String[Stats.TOTAL_STATS + TOTAL_MISC_STATS];
		
		DecimalFormat teamFormatter = new DecimalFormat("000");
		
		// these are to get proper zero padding
		stats[Stats.STATS_CHECKS_THROWN] = GUIStatsFormatter.getChecksThrown();
		stats[Stats.STATS_CHECKS_LANDED] = GUIStatsFormatter.getChecksLanded();
		stats[Stats.STATS_SACKS_FOR] = GUIStatsFormatter.getSacksFor();
		stats[Stats.STATS_SACKS_AGAINST] = GUIStatsFormatter.getSacksAgainst();
		stats[Stats.STATS_INJURIES_FOR] = GUIStatsFormatter.getInjuriesFor();
		stats[Stats.STATS_INJURIES_AGAINST] = GUIStatsFormatter.getInjuriesAgainst();
		stats[Stats.STATS_KILLS_FOR] = GUIStatsFormatter.getKillsFor();
		stats[Stats.STATS_KILLS_AGAINST] = GUIStatsFormatter.getKillsAgainst();
		stats[Stats.STATS_RUSHING_ATTEMPTS] = GUIStatsFormatter.getRushAttempts();
		stats[Stats.STATS_RUSHING_YARDS] = GUIStatsFormatter.getRushTiles();
		stats[Stats.STATS_PADS_ACTIVATED] = GUIStatsFormatter.getPadsActivated();
		stats[Stats.STATS_BALL_CONTROL] = GUIStatsFormatter.getBallControl();
		stats[Stats.STATS_FUMBLES] = GUIStatsFormatter.getBallsFumbled();
		stats[Stats.STATS_GOALS_SCORED] = GUIStatsFormatter.getGoalsScored();
		stats[Stats.STATS_GAMES_PLAYED] = GUIStatsFormatter.getGamesPlayed();
		stats[Stats.STATS_WINS] = GUIStatsFormatter.getGamesWon();
		stats[Stats.STATS_HIGHEST_RATING] = GUIStatsFormatter.getHighestRating();
		stats[Stats.STATS_EJECTIONS] = GUIStatsFormatter.getEjections();
		stats[Stats.STATS_MUTATIONS] = GUIStatsFormatter.getMutations();
		stats[STATS_RUSHING_AVERAGE] = GUIStatsFormatter.getRushAverage();
		stats[STATS_CHECKING_AVERAGE] = GUIStatsFormatter.getCheckingAverage();
		stats[STATS_TOTAL_CARNAGE_FOR] = GUIStatsFormatter.getTotalCarnageFor();
		stats[STATS_TOTAL_CARNAGE_AGAINST] = GUIStatsFormatter.getTotalCarnageAgainst();
		stats[STATS_PLAYER_RACE] = GUIStatsFormatter.getRace();
		stats[STATS_PLAYER_ROSTER_INDEX] = String.valueOf(player.getRosterIndex());
		stats[STATS_PLAYER_NAME] = player.name;
		stats[STATS_TEAM_NAME] = team.teamName;
		stats[STATS_COLOR_FG_RGB] = String.valueOf(team.teamColors[0].getRGB());
		stats[STATS_TEAM_VALUE] = teamFormatter.format(team.getValue());
	}
	
	public static void setSortKey(int statsIndex)
	{
		keySortColumn = statsIndex;
	}
	
	public String getStat(int statIndex)
	{
		return stats[statIndex];
	}
	
	public Player getPlayer()
	{
		return player;
	}

	@Override
	public int compareTo(StatsTableRow row)
	{
		String value1 = getStat(keySortColumn);
		String value2 = row.getStat(keySortColumn);
		
		try {
            return Double.compare(Double.parseDouble(value1), Double.parseDouble(value2));
        } catch (NumberFormatException e) {
            return value1.compareTo(value2);
        }
	}
}
