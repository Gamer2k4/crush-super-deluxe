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

public class Team extends SaveableEntity
{
	public static final int MAX_TEAM_SIZE = 35;

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
		money = 900;

		for (int i = 0; i < 4; i++)
		{
			docbot[i] = false;
		}

		for (int i = 0; i < 2; i++)
		{
			teamColors[i] = Color.YELLOW;
		}
	}

	private List<Player> players;
	public String teamName;
	public String coachName;
	public int homeField;
	public int money;
	public boolean[] docbot = new boolean[4];
	public List<Integer> unassignedGear;
	public Color[] teamColors = new Color[2];

	// return sum of money and player value
	public int getValue()
	{
		return 0;
	}

	public void addPlayer(Player p)
	{
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

			// if the index is out of bounds, we'll just return null (unless it's REALLY out of bounds
		}

		return p;
	}

	public void setPlayer(int index, Player p)
	{
		try
		{
			players.set(index, p);
		} catch (IndexOutOfBoundsException e)
		{
			if (index < MAX_TEAM_SIZE)
			{
				for (int i = players.size(); i < index; i++)
				{
					players.add(null);
				}

				players.add(p);
			} else
			{
				throw new IllegalArgumentException("Invalid index for setting player: " + index, e);
			}
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
				p = PlayerFactory.createEmptyPlayer();
			
			String playerUid = p.getUniqueId();

			if (EntityMap.getPlayer(playerUid) == null)
				playerUid = EntityMap.put(playerUid, p);
			else
				playerUid = EntityMap.getSimpleKey(playerUid);

			toReturn.add(playerUid.substring(1));
		}

		return toReturn;
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
		ssb.addToken(new SaveToken(SaveTokenTag.T_MNY, String.valueOf(money)));

		ssb.addToken(new SaveToken(SaveTokenTag.T_DOC, convertDocbotSettingsToList()));
		ssb.addToken(new SaveToken(SaveTokenTag.T_UEQ, convertUnassignedEquipmentToList()));
		ssb.addToken(new SaveToken(SaveTokenTag.T_FGC, convertColorToList(0)));
		ssb.addToken(new SaveToken(SaveTokenTag.T_BGC, convertColorToList(1)));
		ssb.addToken(new SaveToken(SaveTokenTag.T_PLR, convertPlayersToList()));

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
		setMember(ssb, SaveTokenTag.T_MNY);
		setMember(ssb, SaveTokenTag.T_DOC);
		setMember(ssb, SaveTokenTag.T_UEQ);
		setMember(ssb, SaveTokenTag.T_FGC);
		setMember(ssb, SaveTokenTag.T_BGC);
		setMember(ssb, SaveTokenTag.T_PLR);

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

		case T_MNY:
			saveToken = ssb.getToken(saveTokenTag);
			money = Integer.parseInt(saveToken.getContents());
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

		if (!teamName.equals(team.teamName) || !coachName.equals(team.coachName) || homeField != team.homeField || money != team.money)
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
		hash = 31 * hash + money;
		hash = 31 * hash + teamColors[0].hashCode();
		hash = 31 * hash + teamColors[1].hashCode();

		for (int i = 0; i < 4; i++)
			hash = 31 * hash + (docbot[i] ? 1 : 0);

		for (int i = 0; i < unassignedGear.size(); i++)
			hash = 31 * hash + unassignedGear.get(i).intValue();

		for (int i = 0; i < players.size(); i++)
		{
			int toAdd = 1;
			
			if (getPlayer(i) != null)
				toAdd = getPlayer(i).hashCode();
			
			hash = 31 * hash + toAdd;
		}

		return hash;
	}
}
