package main.presentation.teameditor.common;

import java.text.DecimalFormat;

import main.data.entities.Player;
import main.data.entities.Stats;

public class GUIStatsFormatter
{
	private static String[] races = { "HUMAN", "GRONK", "CURMIAN", "DRAGORAN", "NYNAX", "SLITH", "KURGAN", "XJS9000" };
	private static Player currentPlayer;
	private static Stats currentStats;

	private static int currentStatsType = Stats.CAREER_STATS;

	private static DecimalFormat decimalFormatter = new DecimalFormat("0.00");
	private static DecimalFormat intFormatter = new DecimalFormat("00");

	private GUIStatsFormatter()
	{
		//we want this to only be accessed statically
	}
	
	public static String getNameEmpty()
	{
		return getName("EMPTY");
	}

	public static String getNameBlank()
	{
		return getName("");
	}

	private static String getName(String emptyString)
	{
		return (currentPlayer == null) ? emptyString : currentPlayer.name.toUpperCase();
	}

	public static String getRace()
	{
		return (currentPlayer == null) ? "" : races[currentPlayer.getRace()];
	}

	public static String getRushAttempts()
	{
		return (currentPlayer == null) ? "" : intFormatter.format(currentStats.getStat(Stats.STATS_RUSHING_ATTEMPTS));
	}

	public static String getRushTiles()
	{
		return (currentPlayer == null) ? "" : intFormatter.format(currentStats.getStat(Stats.STATS_RUSHING_YARDS));
	}

	public static String getRushAverage()
	{	
		if (currentPlayer == null)
			return "";
		
		double rushingAttempts = currentStats.getStat(Stats.STATS_RUSHING_ATTEMPTS);
		double rushingYards = currentStats.getStat(Stats.STATS_RUSHING_YARDS);
		String rushingAverage = "00";

		if (rushingAttempts > 0)
		{
			double average = rushingYards / rushingAttempts;
			rushingAverage = intFormatter.format(average);
		}

		return rushingAverage;
	}

	public static String getPadsActivated()
	{
		return (currentPlayer == null) ? "" : intFormatter.format(currentStats.getStat(Stats.STATS_PADS_ACTIVATED));
	}

	public static String getBallControl()
	{
		return (currentPlayer == null) ? "" : intFormatter.format(currentStats.getStat(Stats.STATS_BALL_CONTROL));
	}

	public static String getBallsFumbled()
	{
		return (currentPlayer == null) ? "" : intFormatter.format(currentStats.getStat(Stats.STATS_FUMBLES));
	}

	public static String getGoalsScored()
	{
		return (currentPlayer == null) ? "" : intFormatter.format(currentStats.getStat(Stats.STATS_GOALS_SCORED));
	}

	public static String getChecksThrown()
	{
		return (currentPlayer == null) ? "" : intFormatter.format(currentStats.getStat(Stats.STATS_CHECKS_THROWN));
	}

	public static String getChecksLanded()
	{
		return (currentPlayer == null) ? "" : intFormatter.format(currentStats.getStat(Stats.STATS_CHECKS_LANDED));
	}

	public static String getCheckingAverage()
	{	
		if (currentPlayer == null)
			return "";
		
		double checksThrown = currentStats.getStat(Stats.STATS_CHECKS_THROWN);
		double checksLanded = currentStats.getStat(Stats.STATS_CHECKS_LANDED);
		String checkingAverage = "0.00";

		if (checksThrown > 0)
		{
			double average = checksLanded / checksThrown;
			checkingAverage = decimalFormatter.format(average);
		}

		return checkingAverage;
	}

	public static String getSacksFor()
	{
		return (currentPlayer == null) ? "" : intFormatter.format(currentStats.getStat(Stats.STATS_SACKS_FOR));
	}

	public static String getSacksAgainst()
	{
		return (currentPlayer == null) ? "" : intFormatter.format(currentStats.getStat(Stats.STATS_SACKS_AGAINST));
	}

	public static String getInjuriesFor()
	{
		return (currentPlayer == null) ? "" : intFormatter.format(currentStats.getStat(Stats.STATS_INJURIES_FOR));
	}

	public static String getInjuriesAgainst()
	{
		return (currentPlayer == null) ? "" : intFormatter.format(currentStats.getStat(Stats.STATS_INJURIES_AGAINST));
	}

	public static String getKillsFor()
	{
		return (currentPlayer == null) ? "" : intFormatter.format(currentStats.getStat(Stats.STATS_KILLS_FOR));
	}

	public static String getKillsAgainst()
	{
		return (currentPlayer == null) ? "" : intFormatter.format(currentStats.getStat(Stats.STATS_KILLS_AGAINST));
	}

	public static String getTotalCarnageFor()
	{
		return (currentPlayer == null) ? "" : intFormatter.format(currentStats.getStat(Stats.STATS_INJURIES_FOR) + currentStats.getStat(Stats.STATS_KILLS_FOR));
	}

	public static String getTotalCarnageAgainst()
	{
		return (currentPlayer == null) ? "" : intFormatter.format(currentStats.getStat(Stats.STATS_INJURIES_AGAINST) + currentStats.getStat(Stats.STATS_KILLS_AGAINST));
	}

	public static String getEjections()
	{
		return (currentPlayer == null) ? "" : intFormatter.format(currentStats.getStat(Stats.STATS_EJECTIONS));
	}

	public static String getMutations()
	{
		return (currentPlayer == null) ? "" : intFormatter.format(currentStats.getStat(Stats.STATS_MUTATIONS));
	}

	public static String getAverageRating()
	{
		if (currentPlayer == null)
			return "";
		
		double totalRating = currentStats.getXP();
		double gamesPlayed = currentStats.getStat(Stats.STATS_GAMES_PLAYED);
		String averageRating = "0";

		if (gamesPlayed > 0)
		{
			double average = totalRating / gamesPlayed;
			averageRating = intFormatter.format(average);
		}
		
		return averageRating;
	}

	public static String getHighestRating()
	{
		return (currentPlayer == null) ? "" : intFormatter.format(currentStats.getStat(Stats.STATS_HIGHEST_RATING));
	}

	public static String getGamesPlayed()
	{
		return (currentPlayer == null) ? "" : intFormatter.format(currentStats.getStat(Stats.STATS_GAMES_PLAYED));
	}

	public static String getGamesWon()
	{
		return (currentPlayer == null) ? "" : intFormatter.format(currentStats.getStat(Stats.STATS_WINS));
	}

	public static String getGamesLost()
	{
		return (currentPlayer == null) ? "" : intFormatter.format(currentStats.getStat(Stats.STATS_LOSSES));
	}

	public static String getGamesTied()
	{
		return (currentPlayer == null) ? "" : intFormatter.format(currentStats.getStat(Stats.STATS_TIES));
	}
	
	public static Stats getStats()
	{
		return currentStats;
	}

	public static void setPlayer(Player player)
	{
		currentPlayer = player;
		setStatsType(currentStatsType);
	}

	public static void setStatsType(int statsType)
	{
		currentStatsType = statsType;

		if (currentPlayer == null)
			return;

		if (statsType == Stats.GAME_STATS)
			currentStats = currentPlayer.getLastGameStats(); // TODO
		else if (statsType == Stats.SEASON_STATS)
			currentStats = currentPlayer.getSeasonStats(); // TODO
		else if (statsType == Stats.CAREER_STATS)
			currentStats = currentPlayer.getCareerStats();
		else
			throw new IllegalArgumentException("Player " + currentPlayer.name + " doesn't have stats defined for stats type " + statsType + ".");
	}
}
