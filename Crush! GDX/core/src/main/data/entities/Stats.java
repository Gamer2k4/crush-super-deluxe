package main.data.entities;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import main.data.save.EntityMap;
import main.data.save.SaveStringBuilder;
import main.data.save.SaveToken;
import main.data.save.SaveTokenTag;
import main.presentation.common.Logger;

public class Stats extends SaveableEntity
{
	public static final int GAME_STATS = 0;
	public static final int SEASON_STATS = 1;
	public static final int CAREER_STATS = 2;
	
	//player stats
	public static final int STATS_RUSHING_YARDS = 0;
	public static final int STATS_KILLS_FOR = 1;
	public static final int STATS_KILLS_AGAINST = 2;
	public static final int STATS_INJURIES_FOR = 3;
	public static final int STATS_INJURIES_AGAINST = 4;
	public static final int STATS_CHECKS_THROWN = 5;
	public static final int STATS_CHECKS_LANDED = 6;
	public static final int STATS_PADS_ACTIVATED = 7;
	public static final int STATS_FUMBLES = 8;
	public static final int STATS_RUSHING_ATTEMPTS = 9;
	public static final int STATS_BALL_CONTROL = 10;
	public static final int STATS_SACKS_AGAINST = 11;
	public static final int STATS_SACKS_FOR = 12;
	public static final int STATS_GOALS_SCORED = 13;
	public static final int STATS_GAMES_PLAYED = 14;
	public static final int STATS_HIGHEST_RATING = 15;
	
	//team stats
	public static final int STATS_EJECTIONS = 16;
	public static final int STATS_MUTATIONS = 17;
	public static final int STATS_WINS = 18;
	public static final int STATS_LOSSES = 19;
	public static final int STATS_TIES = 20;
	
	//extra stored stats
	public static final int STATS_TOTAL_RATING = 21;

	public static final int TOTAL_STATS = 22;

	public static final int STATS_RUSHING_AVERAGE = -1;
	public static final int STATS_CHECKING_AVERAGE = -2;
	public static final int STATS_AVERAGE_RATING = -3;
	public static final int STATS_CARNAGE_FOR = -4;
	public static final int STATS_CARNAGE_AGAINST = -5;

	private int[] statFields;

	public Stats()
	{
		statFields = new int[TOTAL_STATS];

		for (int i = 0; i < TOTAL_STATS; i++)
			statFields[i] = 0;
	}

	@Override
	public Stats clone()
	{
		Stats toRet = new Stats();
		toRet.updateWithResults(this);
		return toRet;
	}

	public int getStat(int statIndex)
	{
		if (statIndex >= 0 && statIndex < TOTAL_STATS)
			return statFields[statIndex];
		else if (statIndex == STATS_RUSHING_AVERAGE)
			return getRushingAverage();
		else if (statIndex == STATS_CHECKING_AVERAGE)
			return getCheckingAverage();
		else if (statIndex == STATS_AVERAGE_RATING)
			return getAverageRating();
		else if (statIndex == STATS_CARNAGE_FOR)
			return statFields[STATS_INJURIES_FOR] + statFields[STATS_KILLS_FOR];
		else if (statIndex == STATS_CARNAGE_AGAINST)
			return statFields[STATS_INJURIES_AGAINST] + statFields[STATS_KILLS_AGAINST];
		
		Logger.warn("Cannot find stat for index [" + statIndex + "]; returning 0 instead.");
		return 0;
	}

	private int getRushingAverage()
	{
		if (statFields[STATS_RUSHING_ATTEMPTS] == 0)
			return 0;
		
		return statFields[STATS_RUSHING_YARDS] / statFields[STATS_RUSHING_ATTEMPTS];
	}

	private int getCheckingAverage()
	{
		if (statFields[STATS_CHECKS_THROWN] == 0)
			return 0;
		
		//this one actually rounds correctly in the original game
		return (int)(((100.0 * statFields[STATS_CHECKS_LANDED]) / statFields[STATS_CHECKS_THROWN]) + .5);
		
		//TODO: consider edge cases, like topping out at .99 (unless it's truly 100%) or bottoming out at .01 (unless it's truly 0%)
	}

	private int getAverageRating()
	{
		if (statFields[STATS_GAMES_PLAYED] == 0)
			return 0;
		
		return statFields[STATS_TOTAL_RATING] / statFields[STATS_GAMES_PLAYED];
	}

	//used for loading a player
	public void setStat(int statIndex, int value)
	{
		statFields[statIndex] = value;
	}

	public void updateWithResults(Stats gameResults)
	{
		// store the higher rating of the two stat trackers
		int highestRating = statFields[STATS_HIGHEST_RATING];
		if (gameResults.getStat(STATS_HIGHEST_RATING) > highestRating)
			highestRating = gameResults.getStat(STATS_HIGHEST_RATING);

		for (int i = 0; i < TOTAL_STATS; i++)
			statFields[i] += gameResults.getStat(i);

		statFields[STATS_HIGHEST_RATING] = highestRating;
	}

	// call this whenever the player activates a ball bin (regardless of result)
	public void tryPad()
	{
		statFields[STATS_PADS_ACTIVATED]++;
	}

	// call this whenever the player throws a check
	public void check(boolean success, boolean sack)
	{
		statFields[STATS_CHECKS_THROWN]++;
		
		if (success)
			statFields[STATS_CHECKS_LANDED]++;
		
		if (sack)
			statFields[STATS_SACKS_FOR]++;
	}
	
	public void getSacked()
	{
		statFields[STATS_SACKS_AGAINST]++;
	}

	public void injure()
	{
		statFields[STATS_INJURIES_FOR]++;
	}

	public void getInjured()
	{
		statFields[STATS_INJURIES_AGAINST]++;
	}

	public void kill()
	{
		statFields[STATS_KILLS_FOR]++;
	}

	public void getKilled()
	{
		statFields[STATS_KILLS_AGAINST]++;
	}

	public void mutate()
	{
		statFields[STATS_MUTATIONS]++;
	}

	public void eject()
	{
		statFields[STATS_EJECTIONS]++;
	}

	// call this whenever the player gets the ball (handoff, bin, or pickup)
	// TODO: this should happen when a player hands off the ball too
	public void getBall()
	{
		statFields[STATS_RUSHING_ATTEMPTS]++;
	}
	public void fumbleBall()
	{
		statFields[STATS_FUMBLES]++;
	}

	// call this each time the player moves with the ball
	public void rush(int yards)
	{
		statFields[STATS_RUSHING_YARDS] += yards;
	}
	
	// call this each time a team begins their turn with control of the ball	//TODO: confirm this is correct
	public void controlBall()
	{
		statFields[STATS_BALL_CONTROL] ++;
	}

	// call this upon winning; exclusive with score()
	public void teamWon()
	{
		statFields[STATS_WINS]++;
	}

	// call this upon losing
	public void teamLost()
	{
		statFields[STATS_LOSSES]++;
	}

	// call this upon tying
	public void teamTied()
	{
		statFields[STATS_TIES]++;
	}

	// call this if the player scores; exclusive with teamWon()
	public void score()
	{
		statFields[STATS_GOALS_SCORED]++;
		statFields[STATS_WINS]++;
	}

	// call this upon successfully teleporting in for the first time
	public void enterGame()
	{
		statFields[STATS_GAMES_PLAYED]++;
	}

	// call this once the game concludes
	public int getXP()
	{
		int toRet = 10 * statFields[STATS_INJURIES_FOR] + 1 * statFields[STATS_SACKS_FOR] + 12 * statFields[STATS_KILLS_FOR] + 6
				* statFields[STATS_PADS_ACTIVATED] + 2 * statFields[STATS_CHECKS_LANDED] + (int) (.5 * statFields[STATS_RUSHING_YARDS]);
		if (statFields[STATS_GOALS_SCORED] > 0)
			toRet += 20;
		if (statFields[STATS_GAMES_PLAYED] > 0)
			toRet += 2;
		if (statFields[STATS_WINS] > 0)
			toRet += 2;

		return toRet;
	}

	private List<String> convertStatsToList()
	{
		List<String> toReturn = new ArrayList<String>();

		for (int i = 0; i < TOTAL_STATS; i++)
			toReturn.add(String.valueOf(statFields[i]));

		return toReturn;
	}

	@Override
	public String saveAsText()
	{
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.STATS);

		String statsUid = getUniqueId();

		if (EntityMap.getStats(statsUid) == null)
			statsUid = EntityMap.put(statsUid, this);
		else
			statsUid = EntityMap.getSimpleKey(statsUid);

		ssb.addToken(new SaveToken(SaveTokenTag.S_UID, statsUid));
		ssb.addToken(new SaveToken(SaveTokenTag.S_STS, convertStatsToList()));

		return ssb.getSaveString();
	}

	@Override
	public String loadFromText(String text) throws ParseException
	{
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.STATS, text);

		String toRet = getContentsForTag(ssb, SaveTokenTag.S_UID); // assumed to be defined

		setMember(ssb, SaveTokenTag.S_STS);

		return toRet;
	}

	@Override
	public String getUniqueId()
	{
		return EntityType.STATS.toString() + String.valueOf(Math.abs(saveHash()));
	}

	@Override
	protected void setMember(SaveStringBuilder ssb, SaveTokenTag saveTokenTag)
	{
		String contents = getContentsForTag(ssb, saveTokenTag);
		SaveToken saveToken = null;
		List<String> strVals = null;

		if (contents.equals(""))
			return;

		switch (saveTokenTag)
		{
		case S_STS:
			saveToken = ssb.getToken(saveTokenTag);
			strVals = saveToken.getContentSet();
			
			//older save files had fewer stats and in a different order
			if (TOTAL_STATS > strVals.size())
			{
				System.out.println("Stats data is from an outdated save configuration.  Skipping loading of stats.");
				break;
			}
			
			for (int i = 0; i < TOTAL_STATS; i++)
			{
				statFields[i] = Integer.parseInt(strVals.get(i));
			}
			break;

			//$CASES-OMITTED$
		default:
			throw new IllegalArgumentException("Stats - Unhandled token: " + saveTokenTag.toString());
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;

		Stats stats;

		if (obj != null && obj instanceof Stats)
			stats = (Stats) obj;
		else
			return false;

		for (int i = 0; i < TOTAL_STATS; i++)
		{
			if (statFields[i] != stats.getStat(i))
				return false;
		}

		return true;
	}

	private int saveHash()
	{
		int hash = 11;

		for (int i = 0; i < TOTAL_STATS; i++)
			hash = 31 * hash + statFields[i];

		return hash;
	}
}
