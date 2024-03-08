package main.data.entities;

import java.awt.Color;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import main.data.factory.PlayerFactory;
import main.data.save.EntityMap;
import main.data.save.SaveStringBuilder;
import main.data.save.SaveToken;
import main.data.save.SaveTokenTag;
import main.logic.Randomizer;

public class Team extends SaveableEntity
{
	public static final int MAX_TEAM_SIZE = 35;

	public static final int DOCBOT_EMERGENCY = 0;
	public static final int DOCBOT_SURGERY = 1;
	public static final int DOCBOT_RECOVERY = 2;
	public static final int DOCBOT_THERAPY = 3;

	public Team(String serialString)
	{
		throw new UnsupportedOperationException("Teams cannot yet be created from a data String.");
	}

	public Team()
	{
		players = new ArrayList<Player>();
		unassignedGear = new ArrayList<Integer>();
		teamName = "Anonymous";
		coachName = "Anonymous";
		homeField = Arena.ARENA_BRIDGES;

		for (int i = 0; i < 4; i++)
		{
			docbot[i] = false;
		}

		for (int i = 0; i < 2; i++)
		{
			teamColors[i] = new Color(224, 160, 0);	//TODO: perhaps extract this to a constant; it's in LegacyTextElement, but I don't like bringing that in
		}
		
		lastGameStats = new Stats();
		seasonStats = new Stats();
		careerStats = new Stats();
		
		humanControlled = true;
	}

	private List<Player> players;
	public String teamName;
	public String coachName;
	public int homeField;
	public boolean[] docbot = new boolean[4];
	private List<Integer> unassignedGear;
	public Color[] teamColors = new Color[2];
	
	private Stats lastGameStats;
	private Stats seasonStats;
	private Stats careerStats;
	//TODO: add season and career stats - remember to update constructor, equals(), hashCode(), clone(), and saving
	
	public boolean humanControlled;
	
	@Override
	public Team clone()
	{
		Team team = new Team();
		
		for (Player p : players)
		{
			Player clonedPlayer = null;
			
			if (p != null)
				clonedPlayer = p.clone();
				
			team.addPlayer(clonedPlayer);
		}
		
		team.teamName = teamName;
		team.coachName = coachName;
		team.homeField = homeField;
		
		for (int i = 0; i < 4; i++)
			team.docbot[i] = docbot[i];
		
		for (Integer i : unassignedGear)
		{
			team.unassignedGear.add(new Integer(i.intValue()));
		}
		
		for (int i = 0; i < 2; i++)
			team.teamColors[i] = teamColors[i];
		
		team.lastGameStats = lastGameStats.clone();
		team.seasonStats = seasonStats.clone();
		team.careerStats = careerStats.clone();
		
		team.humanControlled = humanControlled;
		
		return team;
	}
	
	public boolean isBlankTeam()
	{
		return players.isEmpty();		//legacy functionality - doesn't matter what else has been set; if there are no players, make a random team
		//return this.equals(new Team());
	}
	
	public void clearLastGameStats()
	{
		for (int i = 0; i < MAX_TEAM_SIZE; i++)
		{
			Player p = getPlayer(i);
			
			if (p == null)
				continue;
			
			p.clearLastGameStats();
		}
		
		lastGameStats = new Stats();
	}
	
	// return sum of money and player value
	public int getValue()
	{
		int teamValue = 0;

		for (int i = 0; i < MAX_TEAM_SIZE; i++)
		{
			Player p = getPlayer(i);
			if (p != null)
			{
				teamValue += p.getSalary();

				for (int j = 0; j < 4; j++)
				{
					if (p.getEquipment(j) != Equipment.EQUIP_NONE)
					{
						Equipment eq = Equipment.getEquipment(p.getEquipment(j));
						teamValue += eq.cost;
					}
				}
			}
		}

		teamValue += getDocbotCost();

		for (Integer equipmentIndex : unassignedGear)
		{
			teamValue += Equipment.getEquipment(equipmentIndex.intValue()).cost;
		}

		return teamValue;
	}
	
	public int getDocbotCost()
	{
		int value = 0;
		
		if (docbot[0])
			value += 50;
		if (docbot[1])
			value += 40;
		if (docbot[2])
			value += 30;
		if (docbot[3])
			value += 30;
		
		return value;
	}
	
	public List<Integer> getEquipment()
	{
		return unassignedGear;
	}
	
	public void unequipAllItems()
	{
		for (int i = 0; i < MAX_TEAM_SIZE; i++)
		{
			if (players.get(i) == null)
				continue;
			
			unequipItemsFromPlayer(players.get(i));
		}
	}
	
	public void unequipItemsFromPlayer(Player player)
	{
		for (int j = 0; j < 4; j++)
		{
			int equipment = player.unequipItem(j);
			if (equipment >= 0)
				unassignedGear.add(equipment);
		}
	}
	
	public void triggerHealChecks()
	{
		for (Player player : players)
		{
			if (player == null || Randomizer.getRandomInt(1, 50) < 50)
				continue;
			
			player.healInjuries();
		}
	}

	public void addPlayer(Player p)
	{
		if (p != null)
			p.setRosterIndex(players.size());
		
		players.add(p);
	}

	public Player getPlayer(int index)
	{
		Player p = null;

		try
		{
			p = players.get(index);
		} catch (IndexOutOfBoundsException e)
		{
			if (index >= MAX_TEAM_SIZE)
				throw new IllegalArgumentException("Invalid index for getting player: " + index, e);

			// if the index is out of bounds, we'll just return null (unless it's REALLY out of bounds)
		}

		return p;
	}

	public void setPlayer(int index, Player p)
	{
		try
		{
			players.set(index, p);
			
			if (p != null)
				p.setRosterIndex(index);
		} catch (IndexOutOfBoundsException e)
		{
			if (index < MAX_TEAM_SIZE)
			{
				for (int i = players.size(); i < index; i++)
				{
					players.add(null);
				}
				
				addPlayer(p);
			} else
			{
				throw new IllegalArgumentException("Invalid index for setting player: " + index, e);
			}
		}
	}
	
	public Stats getLastGameStats()
	{
		return lastGameStats;
	}
	
	public Stats getSeasonStats()
	{
		return seasonStats;
	}
	
	public Stats getCareerStats()
	{
		return careerStats;
	}
	
	public Team advanceWeek()
	{
		for (Player player : players)
		{
			if (player == null)
				continue;
			player.recoverInjuries(1);
			rollForQuirks(player);
			
			//this makes it hard to track equipment of AI teams in the team editor, but it means equipment can be reallocated each game
			unequipItemsFromPlayer(player);
		}
		
		return this;
	}

	private void rollForQuirks(Player player)
	{
		int quirkChance = 2;
		
		if (docbot[DOCBOT_THERAPY])
			quirkChance = 1;
		
		int quirkRoll = Randomizer.getRandomInt(1, 100);
		
		if (quirkRoll <= quirkChance)
		{
			player.gainRandomQuirk();
			
			if (player.hasQuirk(Quirk.TECHNOPHOBIA))
				unequipItemsFromPlayer(player);
		}
	}

	private List<String> convertDocbotSettingsToList()
	{
		List<String> toReturn = new ArrayList<String>();

		for (int i = 0; i < 4; i++)
			toReturn.add(String.valueOf(docbot[i]));

		return toReturn;
	}

	private List<String> convertColorToList(int index)
	{
		List<String> toReturn = new ArrayList<String>();

		Color c = teamColors[index];

		toReturn.add(String.valueOf(c.getRed()));
		toReturn.add(String.valueOf(c.getGreen()));
		toReturn.add(String.valueOf(c.getBlue()));
		toReturn.add(String.valueOf(c.getAlpha()));

		return toReturn;
	}

	private List<String> convertUnassignedEquipmentToList()
	{
		List<String> toReturn = new ArrayList<String>();

		for (Integer i : unassignedGear)
			toReturn.add(String.valueOf(i));

		return toReturn;
	}

	private List<String> convertPlayersToList()
	{
		List<String> toReturn = new ArrayList<String>();

		for (Player p : players)
		{
			if (p == null)
				p = PlayerFactory.getInstance().createEmptyPlayer();
			
			String playerUid = p.getUniqueId();

			if (EntityMap.getPlayer(playerUid) == null)
				playerUid = EntityMap.put(playerUid, p);
			else
				playerUid = EntityMap.getSimpleKey(playerUid);

			toReturn.add(playerUid.substring(1));
		}

		return toReturn;
	}

	private String convertStatsToString(Stats stats)
	{
		String statsUid = stats.getUniqueId();

		if (EntityMap.getStats(statsUid) == null)
			statsUid = EntityMap.put(statsUid, stats);
		else
			statsUid = EntityMap.getSimpleKey(statsUid);

		return statsUid.substring(1);
	}

	@Override
	public String saveAsText()
	{
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.TEAM);

		String teamUid = getUniqueId();

		if (EntityMap.getTeam(teamUid) == null)
			teamUid = EntityMap.put(teamUid, this);
		else
			teamUid = EntityMap.getSimpleKey(teamUid);

		ssb.addToken(new SaveToken(SaveTokenTag.T_UID, teamUid));
		ssb.addToken(new SaveToken(SaveTokenTag.T_NAM, teamName));
		ssb.addToken(new SaveToken(SaveTokenTag.T_CNM, coachName));
		ssb.addToken(new SaveToken(SaveTokenTag.T_FLD, String.valueOf(homeField)));

		ssb.addToken(new SaveToken(SaveTokenTag.T_DOC, convertDocbotSettingsToList()));
		ssb.addToken(new SaveToken(SaveTokenTag.T_UEQ, convertUnassignedEquipmentToList()));
		ssb.addToken(new SaveToken(SaveTokenTag.T_FGC, convertColorToList(0)));
		ssb.addToken(new SaveToken(SaveTokenTag.T_BGC, convertColorToList(1)));
		ssb.addToken(new SaveToken(SaveTokenTag.T_PLR, convertPlayersToList()));

		ssb.addToken(new SaveToken(SaveTokenTag.T_GST, convertStatsToString(lastGameStats)));
		ssb.addToken(new SaveToken(SaveTokenTag.T_SST, convertStatsToString(seasonStats)));
		ssb.addToken(new SaveToken(SaveTokenTag.T_CST, convertStatsToString(careerStats)));
		
		ssb.addToken(new SaveToken(SaveTokenTag.T_HUM, String.valueOf(humanControlled)));

		return ssb.getSaveString();
	}

	@Override
	public String loadFromText(String text) throws ParseException
	{
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.TEAM, text);

		String toRet = getContentsForTag(ssb, SaveTokenTag.T_UID); // assumed to be defined

		setMember(ssb, SaveTokenTag.T_NAM);
		setMember(ssb, SaveTokenTag.T_CNM);
		setMember(ssb, SaveTokenTag.T_FLD);
		setMember(ssb, SaveTokenTag.T_DOC);
		setMember(ssb, SaveTokenTag.T_UEQ);
		setMember(ssb, SaveTokenTag.T_FGC);
		setMember(ssb, SaveTokenTag.T_BGC);
		setMember(ssb, SaveTokenTag.T_PLR);
		setMember(ssb, SaveTokenTag.T_GST);
		setMember(ssb, SaveTokenTag.T_HUM);

		//TODO: intentionally not loading season and game stats (for now, i guess - assuming a "new season")
		
		return toRet;
	}

	@Override
	public String getUniqueId()
	{
		return EntityType.TEAM.toString() + String.valueOf(Math.abs(hashCode()));
	}

	@Override
	protected void setMember(SaveStringBuilder ssb, SaveTokenTag saveTokenTag)
	{
		String contents = getContentsForTag(ssb, saveTokenTag);
		SaveToken saveToken = null;
		List<String> strVals = null;
		String referenceKey = "";

		if (contents.equals(""))
			return;

		switch (saveTokenTag)
		{
		case T_NAM:
			saveToken = ssb.getToken(saveTokenTag);
			teamName = saveToken.getContents();
			break;

		case T_CNM:
			saveToken = ssb.getToken(saveTokenTag);
			coachName = saveToken.getContents();
			break;

		case T_FLD:
			saveToken = ssb.getToken(saveTokenTag);
			homeField = Integer.parseInt(saveToken.getContents());
			break;

		case T_DOC:
			saveToken = ssb.getToken(saveTokenTag);
			strVals = saveToken.getContentSet();
			for (int i = 0; i < 4; i++)
			{
				docbot[i] = Boolean.parseBoolean(strVals.get(i));
			}
			break;

		case T_UEQ:
			saveToken = ssb.getToken(saveTokenTag);
			strVals = saveToken.getContentSet();
			unassignedGear.clear();
			
			for (String value : strVals)
			{
				unassignedGear.add(Integer.parseInt(value));
			}
			break;

		case T_FGC:
			saveToken = ssb.getToken(saveTokenTag);
			strVals = saveToken.getContentSet();
			teamColors[0] = new Color(Integer.parseInt(strVals.get(0)), Integer.parseInt(strVals.get(1)), Integer.parseInt(strVals.get(2)),
					Integer.parseInt(strVals.get(3)));
			break;

		case T_BGC:
			saveToken = ssb.getToken(saveTokenTag);
			strVals = saveToken.getContentSet();
			teamColors[1] = new Color(Integer.parseInt(strVals.get(0)), Integer.parseInt(strVals.get(1)), Integer.parseInt(strVals.get(2)));
			break;

		case T_GST:
			saveToken = ssb.getToken(saveTokenTag);
			referenceKey = "S" + saveToken.getContents();
			lastGameStats = EntityMap.getStats(referenceKey).clone();
			break;

		case T_SST:
			saveToken = ssb.getToken(saveTokenTag);
			referenceKey = "S" + saveToken.getContents();
			seasonStats = EntityMap.getStats(referenceKey).clone();
			break;

		case T_CST:
			saveToken = ssb.getToken(saveTokenTag);
			referenceKey = "S" + saveToken.getContents();
			careerStats = EntityMap.getStats(referenceKey).clone();
			break;

		case T_PLR:
			saveToken = ssb.getToken(saveTokenTag);
			strVals = saveToken.getContentSet();
			players.clear();
			
			for (String value : strVals)
			{
				referenceKey = "P" + value;
				Player player = EntityMap.getPlayer(referenceKey).clone();
				
				if (player.isEmptyPlayer())
					addPlayer(null);
				else
					addPlayer(player);
			}
			break;
			
		case T_HUM:
			saveToken = ssb.getToken(saveTokenTag);
			humanControlled = Boolean.parseBoolean(saveToken.getContents());
			break;

		default:
			throw new IllegalArgumentException("Team - Unhandled token: " + saveTokenTag.toString());
		}

		return;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		
		Team team;

		if (obj != null && obj instanceof Team)
			team = (Team) obj;
		else
			return false;

		if (!teamName.equals(team.teamName) || !coachName.equals(team.coachName) || homeField != team.homeField || 
				!lastGameStats.equals(team.getLastGameStats()) || !seasonStats.equals(team.getLastGameStats()) ||
				!careerStats.equals(team.getLastGameStats()) || humanControlled != team.humanControlled)
			return false;

		if (!teamColors[0].equals(team.teamColors[0]) || !teamColors[1].equals(team.teamColors[1]))
			return false;

		for (int i = 0; i < 4; i++)
		{
			if (docbot[i] != team.docbot[i])
				return false;
		}

		if (unassignedGear.size() != team.unassignedGear.size())
			return false;

		for (int i = 0; i < unassignedGear.size(); i++)
		{
			if (!unassignedGear.get(i).equals(team.unassignedGear.get(i)))
				return false;
		}

		if (players.size() != team.players.size())
			return false;

		for (int i = 0; i < players.size(); i++)
		{
			if (!getPlayer(i).equals(team.getPlayer(i)))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 7;

		hash = 31 * hash + teamName.hashCode();
		hash = 31 * hash + coachName.hashCode();
		hash = 31 * hash + homeField;
		hash = 31 * hash + teamColors[0].hashCode();
		hash = 31 * hash + teamColors[1].hashCode();
		hash = 31 * hash + lastGameStats.hashCode();   // TODO: check if this should be saveHash()
		hash = 31 * hash + seasonStats.hashCode();   // TODO: check if this should be saveHash()
		hash = 31 * hash + careerStats.hashCode();   // TODO: check if this should be saveHash()
		hash = 31 * hash + (humanControlled ? 1 : 0);

		for (int i = 0; i < 4; i++)
			hash = 31 * hash + (docbot[i] ? 1 : 0);

		for (int i = 0; i < unassignedGear.size(); i++)
			hash = 31 * hash + unassignedGear.get(i).intValue();

		for (int i = 0; i < players.size(); i++)
		{
			int toAdd = 1;
			
			if (getPlayer(i) != null)
				toAdd = getPlayer(i).hashCode();		//TODO: check if this should be saveHash()
			
			hash = 31 * hash + toAdd;
		}

		return hash;
	}
}
