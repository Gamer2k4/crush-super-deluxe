package main.data.entities;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import main.data.save.EntityMap;
import main.data.save.SaveStringBuilder;
import main.data.save.SaveToken;
import main.data.save.SaveTokenTag;

public class Player extends SaveableEntity
{
	public static final int ATT_AP = 0;
	public static final int ATT_CH = 1;
	public static final int ATT_ST = 2;
	public static final int ATT_TG = 3;
	public static final int ATT_RF = 4;
	public static final int ATT_JP = 5;
	public static final int ATT_HD = 6;
	public static final int ATT_DA = 7;

	public static final int STS_LATE = 0;
	public static final int STS_DECK = 1;
	public static final int STS_OKAY = 2;
	public static final int STS_DOWN = 3;
	public static final int STS_STUN_DOWN = 4;
	public static final int STS_BLOB = 5;
	public static final int STS_HURT = 6;
	public static final int STS_DEAD = 7;
	public static final int STS_OUT = 8;
	public static final int STS_STUN_SIT = 9;
	
	// TODO: IMPORTANT! Update these two arrays if the race order ever changes (also update getSalary())
	public static final String[] races = { "Human", "Gronk", "Curmian", "Dragoran", "Nynax", "Slith", "Kurgan", "XJS9000" };
	public static final int[][] baseAttributes = { { 60, 50, 50, 50, 30, 60, 70, 30 }, { 50, 60, 70, 60, 20, 30, 20, 10 },
			{ 80, 30, 20, 30, 30, 90, 50, 30 }, { 70, 50, 40, 40, 40, 70, 70, 30 }, { 70, 40, 40, 40, 20, 50, 80, 20 },
			{ 60, 60, 40, 70, 40, 40, 30, 20 }, { 60, 60, 60, 55, 40, 50, 40, 10 }, { 60, 30, 30, 30, 10, 20, 30, 10 },
			{ 0, 0, 0, 0, 0, 0, 0, 0 } };
	
	// TODO: IMPORTANT! Update this if the skill order ever changes
//	public static final int[] skillCosts = { 0, 20, 40, 40, 60, 60, 80, 80, 100, 20, 20, 20, 40, 40, 40, 60, 60, 80, 100, 20, 40, 40, 40,
//			60, 60, 60, 80, 80, 100, 0 };

	public static final int INJURY_NONE = -1;
	public static final int INJURY_KNOCKDOWN = 0;
	public static final int INJURY_STUN = 1;
	public static final int INJURY_TRIVIAL = 2;
	public static final int INJURY_MINOR = 3;
	public static final int INJURY_CRIPPLING = 4;
	public static final int INJURY_CRIPPLE_10 = 4;
	public static final int INJURY_CRIPPLE_15 = 5;
	public static final int INJURY_CRIPPLE_20 = 6;
	public static final int INJURY_DEATH_1 = 7;
	public static final int INJURY_DEATH_2 = 8;
	public static final int INJURY_DEATH_3 = 9;
	public static final int INJURY_DEATH_4 = 10;

	public static final int RANK_ROOKIE = 0;
	public static final int RANK_REGULAR = 1;
	public static final int RANK_VETERAN = 2;
	public static final int RANK_CHAMPION = 3;
	public static final int RANK_CAPTAIN = 4;
	public static final int RANK_HERO = 5;
	public static final int RANK_LEGEND = 6;
	public static final int RANK_AVATAR = 7;

	// confirmed to match up with legacy format
	public static final int QUIRK_MORON = 1;
	public static final int QUIRK_INTELLIGENT = 2;
	public static final int QUIRK_EGOMANIAC = 3;
	public static final int QUIRK_SLACKER = 4;
	public static final int QUIRK_TECHNOPHOBIA = 5;
	public static final int QUIRK_ELECTROPHOBIA = 6;
	public static final int QUIRK_BLOBBOPHOBIA = 7;
	public static final int QUIRK_DISPLACER = 8;
	public static final int QUIRK_BOUNCER = 9;
	public static final int QUIRK_IMMUNITY = 10;
	public static final int QUIRK_SPACE_ROT = 11;
	public static final int QUIRK_GRIT = 12;

	@Override
	public String toString()
	{
		return "[" + name + "/" + hashCode() + "]";
		// return name + ": " + status + ", " + currentAP;
		// return name + ", " + getRank() + ", " + race + ", " + getSalary();
	}

	public Player(String serialString)
	{
		throw new UnsupportedOperationException("Players cannot yet be created from a data String.");
	}

	public Player(Race myRace, String myName)
	{
		race = myRace;
		name = myName;
		status = STS_DECK;

		int raceIndex = 8;
		if (race != null)
			raceIndex = race.getIndex();

		for (int i = 0; i < 8; i++)
		{
			attributes[i] = baseAttributes[raceIndex][i];
		}

		currentAP = attributes[ATT_AP];

		skills = new ArrayList<Skill>();
		
		gainRacialSkill();

		for (int i = 0; i < 4; i++)
		{
			equipment[i] = Equipment.EQUIP_NONE;
		}

		healInjuries();
		weeksOut = -1;
		injuryType = Player.INJURY_NONE;
		XP = 0;
		skillPoints = 0;
		rosterIndex = -1;

		lastGameStats = new Stats();
		seasonStats = new Stats();
		careerStats = new Stats();
		
		gamesPlayed = 0;
		totalSeasons = 0;
	}

	private void gainRacialSkill()
	{
		if (race == Race.HUMAN)
			gainSkill(Skill.UNCOMMON_VALOR);
		else if (race == Race.GRONK)
			gainSkill(Skill.REGENERATE);
		else if (race == Race.CURMIAN)
			gainSkill(Skill.HIGH_JUMP);
		else if (race == Race.DRAGORAN)
			gainSkill(Skill.POP_UP);
		else if (race == Race.NYNAX)
			gainSkill(Skill.HIVE_MIND);
		else if (race == Race.SLITH)
			gainSkill(Skill.DEATH_REEK);
		else if (race == Race.KURGAN)
			gainSkill(Skill.BLOODLUST);
		else if (race == Race.XJS9000)
			gainSkill(Skill.GYRO_STABILIZER);
	}

	public boolean isEmptyPlayer()
	{
		return (race == null);
	}

	public int status; // BLOB, DECK, LATE, HURT, STUN, DEAD, DOWN, and OKAY
	public String name;
	private Race race = null;
	private int[] attributes = new int[8];
	private int[] injuries = new int[8];
//	private boolean[] gainedSkills = new boolean[Skill.getTotalSkills() + 1];
//	private String orderOfGainedSkills;
	
	private List<Skill> skills;
	public int currentAP; // TODO: perhaps extract this to the data layer
	private int rosterIndex;	//TODO: not sure that I like the player knowing this, but it makes things easier

	// TODO: save these if they aren't - this includes equals() and clone()
	private int injuryType;
	private int weeksOut; // -1 if no injury; 0 for trivial or injury where the player is back the current week

	private int[] equipment = new int[4];

	private int XP;
	private int skillPoints;

	private Stats lastGameStats;
	private Stats seasonStats;
	private Stats careerStats;
	
	private int gamesPlayed;
	private int totalSeasons;

	@Override
	public Player clone()
	{
		Player toRet = new Player(race, name);

		toRet.status = status;
		toRet.currentAP = currentAP;
		toRet.rosterIndex = rosterIndex;
		toRet.weeksOut = weeksOut;
		toRet.injuryType = injuryType;
		toRet.XP = XP;
		toRet.skillPoints = skillPoints;
		toRet.lastGameStats = lastGameStats.clone();
		toRet.seasonStats = seasonStats.clone();
		toRet.careerStats = careerStats.clone();
		
		toRet.gamesPlayed = gamesPlayed;
		toRet.totalSeasons = totalSeasons;

		for (int i = 0; i < 8; i++)
		{
			toRet.attributes[i] = attributes[i];
			toRet.injuries[i] = injuries[i];
		}

		for (int i = 0; i < 4; i++)
		{
			toRet.equipment[i] = equipment[i];
		}

		for (Skill skill : skills)
		{
			toRet.gainSkill(skill);
		}

		return toRet;
	}

	public void clearLastGameStats()
	{
		lastGameStats = new Stats();
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
	
	public List<Skill> getSkills()
	{
		return skills;
	}
	
	public boolean hasSkill(Skill skill)
	{
		return skills.contains(skill);
	}
	
	public void gainSkill(Skill skill)
	{
		if (skill == null)
			return;
		
		if (hasSkill(skill))
			return;
		
		skills.add(skill);
		
		if (skills.size() == 29)	//28 plus racial skill
			skills.add(Skill.NINJA_MASTER);
	}
	
	public void purchaseSkill(Skill skill)
	{
		if (skill == null)
			return;
		
		if (hasSkill(skill))
			return;
		
		if (skillPoints < skill.getCost())
			return;
		
		skillPoints -= skill.getCost();
		gainSkill(skill);
	}
	
	public int getRosterIndex()
	{
		return rosterIndex + 1;
	}
	
	public void setRosterIndex(int index)
	{
		rosterIndex = index;
	}

	public int getWeeksOut()
	{
		return weeksOut;
	}

	public void setWeeksOut(int weekAmount)
	{
		weeksOut = weekAmount;
	}

	public int getInjuryType()
	{
		return injuryType;
	}

	public void setInjuryType(int type)
	{
		injuryType = type;
	}

	public int getRank()
	{
		if (XP >= 1600)
			return RANK_AVATAR;
		else if (XP >= 1200)
			return RANK_LEGEND;
		else if (XP >= 900)
			return RANK_HERO;
		else if (XP >= 600)
			return RANK_CAPTAIN;
		else if (XP >= 400)
			return RANK_CHAMPION;
		else if (XP >= 200)
			return RANK_VETERAN;
		else if (XP >= 60)
			return RANK_REGULAR;

		return RANK_ROOKIE;
	}

	public int getSalary()
	{
		int[] baseSalary = { 40, 80, 20, 50, 30, 60, 70, 10 };
		int[] salaryMods = { 0, 0, 20, 40, 60, 90, 120, 160 };

		// TODO: add salary reductions for injuries

		return baseSalary[race.getIndex()] + salaryMods[getRank()];
	}

	public void addXP(Stats statsToAdd)
	{
		XP += statsToAdd.getXP();
		skillPoints += statsToAdd.getXP();
		seasonStats.updateWithResults(statsToAdd);
		careerStats.updateWithResults(statsToAdd);
		lastGameStats = statsToAdd.clone();
	}

	public void setXpAndSkillPoints(int xpAmount, int skillPointAmount)
	{
		XP = xpAmount;
		skillPoints = skillPointAmount;
	}

	public void applyInjury(int attribute, int value)
	{
		injuries[attribute] -= value;
	}

	public void healInjuries()
	{
		for (int i = 0; i < 8; i++)
		{
			injuries[i] = 0;
		}
	}

	public void recoverInjuries(int weeks)
	{
		weeksOut -= weeks;

		if (weeksOut < 0)
		{
			weeksOut = 0;
			injuryType = INJURY_NONE; // a trivial injury will be set to 0, based on how the weeks off are store in Data
		}

		if (status == STS_BLOB)
			status = STS_OKAY;
	}

	public int getDetectionChance()
	{
		int toRet = 0;

		for (int i = 0; i < 4; i++)
		{
			if (equipment[i] > -1)
				toRet += Equipment.getEquipment(equipment[i]).detection;
		}

		if (hasSkill(Skill.SLY))
			toRet /= 2;

		return toRet;
	}

	public int equipItem(int eIndex)
	{
		return equipItem(Equipment.getEquipment(eIndex));
	}

	// always successful; returns the index of the equipment that was previously equipped
	public int equipItem(Equipment e)
	{
		if (e.type == Equipment.EQUIP_NONE)
			return e.type;

		int toRet = unequipItem(e.type);
		equipment[e.type] = e.index;

		return toRet;
	}

	public int unequipItem(int slot)
	{
		if (slot == Equipment.EQUIP_NONE)
			return slot;

		int gear = equipment[slot];
		equipment[slot] = Equipment.EQUIP_NONE;
		return gear;
	}

	public List<String> getEquipListForDisplay()
	{
		List<String> toRet = new ArrayList<String>();

		for (int i = 0; i < 4; i++)
		{
			String equipName = "(none)";

			if (equipment[i] != -1)
				equipName = Equipment.getEquipment(equipment[i]).name;

			toRet.add(equipName);
		}

		return toRet;
	}

	public int getEquipment(int equipSlot)
	{
		if (equipSlot < 0 || equipSlot > 3)
			throw new IllegalArgumentException("Invalid equipment slot specified: " + equipSlot);

		return equipment[equipSlot];
	}

	public int getAttributeWithModifiers(int attribute)
	{
		int bonus = 0;

		if (attribute == Player.ATT_ST && hasSkill(Skill.BRUTAL))
			bonus += 10;
		if (attribute == Player.ATT_TG && hasSkill(Skill.STALWART))
			bonus += 10;
		if (attribute == Player.ATT_CH && hasSkill(Skill.CHECKMASTER))
			bonus += 10;
		if (attribute == Player.ATT_JP && hasSkill(Skill.GYMNASTICS))
			bonus += 10;
		if (attribute == Player.ATT_DA && hasSkill(Skill.GYMNASTICS))
			bonus += 10;
		if (attribute == Player.ATT_RF && hasSkill(Skill.BOXING))
			bonus += 10;
		if (attribute == Player.ATT_HD && hasSkill(Skill.JUGGLING))
			bonus += 10;
		if (attribute == Player.ATT_AP && hasSkill(Skill.QUICKENING))
			bonus += 10;

		int toRet = attributes[attribute] + injuries[attribute] + bonus;

		for (int i = 0; i < 4; i++)
		{
			if (equipment[i] > -1)
			{
				toRet = Equipment.getEquipment(equipment[i]).getAttributeWithEquipment(attribute, toRet);
			}
		}

		if (toRet > 99)
			toRet = 99;
		if (toRet < 1)
			toRet = 1;

		return toRet;
	}

	public void setAttribute(int attribute, int value)
	{
		attributes[attribute] = value;
	}
	
	public void changeAttribute(int attribute, int changeAmount)
	{
		attributes[attribute] += changeAmount;
	}

	public int getAttributeWithoutModifiers(int attribute)
	{
		return attributes[attribute];
	}

	public void setRace(Race race)
	{
		this.race = race;
	}

	public Race getRace()
	{
		return race;
	}

	public int getStatus()
	{
		return status;
	}

	public boolean isInGame()
	{
		return status == Player.STS_OKAY || status == Player.STS_DOWN || status == Player.STS_STUN_DOWN || status == Player.STS_STUN_SIT;
	}
	
	public boolean canJump()
	{
		if (!isInGame())
			return false;
		
		if (currentAP >= 30)
			return true;
		
		if (hasSkill(Skill.HIGH_JUMP) && currentAP >= 20)
			return true;
		
		return false;
	}
	
	public boolean canThrowCheck()
	{
		if (!isInGame())
			return false;
		
		if (currentAP >= 20)
			return true;
		
		if (hasSkill(Skill.CHARGE) && currentAP >= 10)
			return true;
		
		return false;
	}

	public boolean canBeChecked()
	{
		return status == Player.STS_OKAY;
	}

	public String serialize()
	{
		return null;
	}

	public int getXP()
	{
		return XP;
	}

	public int getSkillPoints()
	{
		return skillPoints;
	}
	
	//TODO: this doesn't match up with the numbers in the legacy game, but I think my calculation is correct
	public int getAverageRating()
	{
		if (gamesPlayed == 0)
			return 0;
		
		return XP / gamesPlayed;
	}
	
	public int getSeasons()
	{
		return totalSeasons;
	}
	
	public void setSeasons(int seasons)
	{
		totalSeasons = seasons;
	}
	
	public int getGamesPlayed()
	{
		return gamesPlayed;
	}
	
	public void setGamesPlayed(int games)
	{
		gamesPlayed = games;
	}
	
	public void incrementGamesPlayed()
	{
		gamesPlayed++;
	}
	
	public void incrementSeasons()
	{
		totalSeasons++;
	}

	private List<String> convertAttributesToList()
	{
		List<String> toReturn = new ArrayList<String>();

		for (int i = 0; i < 8; i++)
			toReturn.add(String.valueOf(attributes[i]));

		return toReturn;
	}

	private List<String> convertInjuriesToList()
	{
		List<String> toReturn = new ArrayList<String>();

		for (int i = 0; i < 8; i++)
			toReturn.add(String.valueOf(injuries[i]));

		return toReturn;
	}

	private List<String> convertSkillsToList()
	{
		List<String> toReturn = new ArrayList<String>();

		for (Skill skill : skills)
			toReturn.add(skill.name());

		return toReturn;
	}

	private List<String> convertEquipmentToList()
	{
		List<String> toReturn = new ArrayList<String>();

		for (int i = 0; i < 4; i++)
			toReturn.add(String.valueOf(equipment[i]));

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
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.PLAYER);

		String playerUid = getUniqueId();

		if (EntityMap.getPlayer(playerUid) == null)
			playerUid = EntityMap.put(playerUid, this);
		else
			playerUid = EntityMap.getSimpleKey(playerUid);

		ssb.addToken(new SaveToken(SaveTokenTag.P_UID, playerUid));
		ssb.addToken(new SaveToken(SaveTokenTag.P_STS, String.valueOf(status)));
		ssb.addToken(new SaveToken(SaveTokenTag.P_NAM, name));
		ssb.addToken(new SaveToken(SaveTokenTag.P_RCE, String.valueOf(race.getIndex())));
		ssb.addToken(new SaveToken(SaveTokenTag.P_WKS, String.valueOf(weeksOut)));
		ssb.addToken(new SaveToken(SaveTokenTag.P_ITP, String.valueOf(injuryType)));
		ssb.addToken(new SaveToken(SaveTokenTag.P_XP_, String.valueOf(XP)));
		ssb.addToken(new SaveToken(SaveTokenTag.P_SP_, String.valueOf(skillPoints)));
		ssb.addToken(new SaveToken(SaveTokenTag.P_RTR, String.valueOf(rosterIndex)));

		ssb.addToken(new SaveToken(SaveTokenTag.P_ATT, convertAttributesToList()));
		ssb.addToken(new SaveToken(SaveTokenTag.P_INJ, convertInjuriesToList()));
		ssb.addToken(new SaveToken(SaveTokenTag.P_SKL, convertSkillsToList()));
		ssb.addToken(new SaveToken(SaveTokenTag.P_EQP, convertEquipmentToList()));

		ssb.addToken(new SaveToken(SaveTokenTag.P_GST, convertStatsToString(lastGameStats)));
		ssb.addToken(new SaveToken(SaveTokenTag.P_SST, convertStatsToString(seasonStats)));
		ssb.addToken(new SaveToken(SaveTokenTag.P_CST, convertStatsToString(careerStats)));
		
		ssb.addToken(new SaveToken(SaveTokenTag.P_GPL, String.valueOf(gamesPlayed)));
		ssb.addToken(new SaveToken(SaveTokenTag.P_SEA, String.valueOf(totalSeasons)));

		return ssb.getSaveString();
	}

	@Override
	public String loadFromText(String text) throws ParseException
	{
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.PLAYER, text);

		String toRet = getContentsForTag(ssb, SaveTokenTag.P_UID); // assumed to be defined

		setMember(ssb, SaveTokenTag.P_STS);
		setMember(ssb, SaveTokenTag.P_NAM);
		setMember(ssb, SaveTokenTag.P_RCE);
		setMember(ssb, SaveTokenTag.P_WKS);
		setMember(ssb, SaveTokenTag.P_ITP);
		setMember(ssb, SaveTokenTag.P_XP_);
		setMember(ssb, SaveTokenTag.P_SP_);
		setMember(ssb, SaveTokenTag.P_RTR);
		setMember(ssb, SaveTokenTag.P_ATT);
		setMember(ssb, SaveTokenTag.P_INJ);
		setMember(ssb, SaveTokenTag.P_SKL);
		setMember(ssb, SaveTokenTag.P_EQP);
		setMember(ssb, SaveTokenTag.P_CST);
		setMember(ssb, SaveTokenTag.P_GPL);
		setMember(ssb, SaveTokenTag.P_SEA);

		//TODO: intentionally not loading season and game stats (for now, i guess - assuming a "new season")

		return toRet;
	}

	@Override
	public String getUniqueId()
	{
		return EntityType.PLAYER.toString() + String.valueOf(Math.abs(saveHash()));
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
		case P_STS:
			saveToken = ssb.getToken(saveTokenTag);
			status = Integer.parseInt(saveToken.getContents());
			break;

		case P_NAM:
			saveToken = ssb.getToken(saveTokenTag);
			name = saveToken.getContents();
			break;

		case P_RCE:
			saveToken = ssb.getToken(saveTokenTag);
			race = Race.getRace(Integer.parseInt(saveToken.getContents()));
			break;

		case P_WKS:
			saveToken = ssb.getToken(saveTokenTag);
			weeksOut = Integer.parseInt(saveToken.getContents());
			break;

		case P_ITP:
			saveToken = ssb.getToken(saveTokenTag);
			injuryType = Integer.parseInt(saveToken.getContents());
			break;

		case P_XP_:
			saveToken = ssb.getToken(saveTokenTag);
			XP = Integer.parseInt(saveToken.getContents());
			break;

		case P_SP_:
			saveToken = ssb.getToken(saveTokenTag);
			skillPoints = Integer.parseInt(saveToken.getContents());
			break;

		case P_RTR:
			saveToken = ssb.getToken(saveTokenTag);
			rosterIndex = Integer.parseInt(saveToken.getContents());
			break;

		case P_ATT:
			saveToken = ssb.getToken(saveTokenTag);
			strVals = saveToken.getContentSet();
			for (int i = 0; i < 8; i++)
			{
				attributes[i] = Integer.parseInt(strVals.get(i));
			}
			break;

		case P_INJ:
			saveToken = ssb.getToken(saveTokenTag);
			strVals = saveToken.getContentSet();
			for (int i = 0; i < 8; i++)
			{
				injuries[i] = Integer.parseInt(strVals.get(i));
			}
			break;

		case P_SKL:
			saveToken = ssb.getToken(saveTokenTag);
			strVals = saveToken.getContentSet();
			for (String skillName : strVals)
			{
				skills.add(Skill.valueOf(skillName));
			}
			break;

		case P_EQP:
			saveToken = ssb.getToken(saveTokenTag);
			strVals = saveToken.getContentSet();
			for (int i = 0; i < 4; i++)
			{
				equipment[i] = Integer.parseInt(strVals.get(i));
			}
			break;

		case P_GST:
			saveToken = ssb.getToken(saveTokenTag);
			referenceKey = "S" + saveToken.getContents();
			lastGameStats = EntityMap.getStats(referenceKey).clone();
			break;

		case P_SST:
			saveToken = ssb.getToken(saveTokenTag);
			referenceKey = "S" + saveToken.getContents();
			seasonStats = EntityMap.getStats(referenceKey).clone();
			break;

		case P_CST:
			saveToken = ssb.getToken(saveTokenTag);
			referenceKey = "S" + saveToken.getContents();
			careerStats = EntityMap.getStats(referenceKey).clone();
			break;

		case P_GPL:
			saveToken = ssb.getToken(saveTokenTag);
			gamesPlayed = Integer.parseInt(saveToken.getContents());
			break;

		case P_SEA:
			saveToken = ssb.getToken(saveTokenTag);
			totalSeasons = Integer.parseInt(saveToken.getContents());
			break;

		default:
			throw new IllegalArgumentException("Player - Unhandled token: " + saveTokenTag.toString());
		}

		return;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;

		Player player;

		if (obj != null && obj instanceof Player)
			player = (Player) obj;
		else
			return false;

		if (status != player.status || !name.equals(player.name) ||  race != player.race || weeksOut != player.weeksOut || injuryType != player.injuryType || XP != player.XP
				|| skillPoints != player.skillPoints || !lastGameStats.equals(player.lastGameStats) || !seasonStats.equals(player.seasonStats)
				|| !careerStats.equals(player.careerStats) || gamesPlayed != player.gamesPlayed || totalSeasons != player.gamesPlayed)
			return false;

		for (int i = 0; i < 8; i++)
		{
			if (attributes[i] != player.attributes[i])
				return false;

			if (injuries[i] != player.injuries[i])
				return false;
		}
		
		if (skills.size() != player.skills.size())
			return false;

		for (int i = 0; i < skills.size(); i++)
		{
			if (skills.get(i) != player.skills.get(i))
				return false;
		}

		for (int i = 0; i < 4; i++)
		{
			if (equipment[i] != player.equipment[i])
				return false;
		}

		return true;
	}

	private int saveHash()
	{
		int hash = 11;

		hash = 31 * hash + status;
		hash = 31 * hash + name.hashCode();
		hash = 31 * hash + race.getIndex();
		hash = 31 * hash + rosterIndex;
		hash = 31 * hash + weeksOut;
		hash = 31 * hash + injuryType;
		hash = 31 * hash + XP;
		hash = 31 * hash + skillPoints;
		hash = 31 * hash + lastGameStats.hashCode();   // TODO: check if this should be saveHash()
		hash = 31 * hash + seasonStats.hashCode(); // TODO: check if this should be saveHash()
		hash = 31 * hash + careerStats.hashCode(); // TODO: check if this should be saveHash()
		hash = 31 * hash + gamesPlayed;
		hash = 31 * hash + totalSeasons;

		for (int i = 0; i < 8; i++)
		{
			hash = 31 * hash + attributes[i];
			hash = 31 * hash + injuries[i];
		}

		for (Skill skill : skills)
			hash = hash + skill.hashCode();

		for (int i = 0; i < 4; i++)
			hash = 31 * hash + equipment[i];

		return hash;
	}
}
