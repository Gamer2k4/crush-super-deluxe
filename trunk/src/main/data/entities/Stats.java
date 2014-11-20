package main.data.entities;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import main.data.save.EntityMap;
import main.data.save.SaveStringBuilder;
import main.data.save.SaveToken;
import main.data.save.SaveTokenTag;

public class Stats extends SaveableEntity
{
	public static final int STATS_CHECKS_THROWN = 0;
	public static final int STATS_CHECKS_LANDED = 1;
	public static final int STATS_SACKS = 2;
	public static final int STATS_INJURIES = 3;
	public static final int STATS_KILLS = 4;
	public static final int STATS_RUSHING_ATTEMPTS = 5;
	public static final int STATS_RUSHING_YARDS = 6;
	public static final int STATS_PADS_ACTIVATED = 7;
	public static final int STATS_GOALS_SCORED = 8;
	public static final int STATS_GAMES_PLAYED = 9;
	public static final int STATS_GAMES_WON = 10;
	public static final int STATS_HIGHEST_RATING = 11;

	public static final int TOTAL_STATS = 12;

	private int[] statFields;

	// TODO: update with more career-based values, if there are any - remember to update save fields as well

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
		return statFields[statIndex];
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
			statFields[STATS_SACKS]++;
	}

	public void injure()
	{
		statFields[STATS_INJURIES]++;
	}

	public void kill()
	{
		statFields[STATS_KILLS]++;
	}

	// call this whenever the player gets the ball (handoff, bin, or pickup)
	// TODO: this should happen when a player hands off the ball too
	public void getBall()
	{
		statFields[STATS_RUSHING_ATTEMPTS]++;
	}

	// call this each time the player moves with the ball
	public void rush(int yards)
	{
		statFields[STATS_RUSHING_YARDS] += yards;
	}

	// call this upon winning; exclusive with score()
	public void teamWon()
	{
		statFields[STATS_GAMES_WON]++;
	}

	// call this if the player scores; exclusive with teamWon()
	public void score()
	{
		statFields[STATS_GOALS_SCORED]++;
		statFields[STATS_GAMES_WON]++;
	}

	// call this upon successfully teleporting in for the first time
	public void playedGame()
	{
		statFields[STATS_GAMES_PLAYED]++;
	}

	// call this once the game concludes
	public int getXP()
	{
		int toRet = 2 + 10 * statFields[STATS_INJURIES] + 1 * statFields[STATS_SACKS] + 12 * statFields[STATS_KILLS] + 6
				* statFields[STATS_PADS_ACTIVATED] + 2 * statFields[STATS_CHECKS_LANDED] + (int) (.5 * statFields[STATS_RUSHING_YARDS]);
		if (statFields[STATS_GOALS_SCORED] > 0)
			toRet += 20;
		if (statFields[STATS_GAMES_PLAYED] > 0)
			toRet += 2;
		if (statFields[STATS_GAMES_WON] > 0)
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

		if (EntityMap.getPlayer(statsUid) == null)
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
			for (int i = 0; i < TOTAL_STATS; i++)
			{
				statFields[i] = Integer.parseInt(strVals.get(i));
			}
			break;

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
