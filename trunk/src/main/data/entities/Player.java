package main.data.entities;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import main.data.save.EntityMap;
import main.data.save.SaveStringBuilder;
import main.data.save.SaveToken;
import main.data.save.SaveTokenTag;
import main.logic.Engine;

public class Player extends SaveableEntity
{
	public static final int RACE_CURMIAN = 0;
	public static final int RACE_DRAGORAN = 1;
	public static final int RACE_GRONK = 2;
	public static final int RACE_HUMAN = 3;
	public static final int RACE_KURGAN = 4;
	public static final int RACE_NYNAX = 5;
	public static final int RACE_SLITH = 6;
	public static final int RACE_XJS9000 = 7;

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
	public static final int STS_STUN = 4;
	public static final int STS_BLOB = 5;
	public static final int STS_HURT = 6;
	public static final int STS_DEAD = 7;
	public static final int STS_OUT = 8;

	// TODO: add racial abilities as skills
	public static final int TOTAL_SKILLS = 28;
	public static final int SKILL_TERROR = 28;
	public static final int SKILL_JUGGERNAUT = 27;
	public static final int SKILL_CHARGE = 26;
	public static final int SKILL_RESILIENT = 25;
	public static final int SKILL_VICIOUS = 24;
	public static final int SKILL_CHECKMASTER = 23;
	public static final int SKILL_STALWART = 22;
	public static final int SKILL_BRUTAL = 21;
	public static final int SKILL_TACTICS = 20;
	public static final int SKILL_GUARD = 19;
	public static final int SKILL_DOOMSTRIKE = 18;
	public static final int SKILL_FIST_OF_IRON = 17;
	public static final int SKILL_QUICKENING = 16;
	public static final int SKILL_STRIP = 15;
	public static final int SKILL_COMBO = 14;
	public static final int SKILL_JUDO = 13;
	public static final int SKILL_SCOOP = 12;
	public static final int SKILL_BOXING = 11;
	public static final int SKILL_GYMNASTICS = 10;
	public static final int SKILL_JUGGLING = 9;
	public static final int SKILL_SENSEI = 8;
	public static final int SKILL_HEALER = 7;
	public static final int SKILL_AWE = 6;
	public static final int SKILL_KARMA = 5;
	public static final int SKILL_LEADER = 4;
	public static final int SKILL_SLY = 3;
	public static final int SKILL_STOIC = 2;
	public static final int SKILL_INTUITION = 1;

	public static final String[] skillNames = { "None", "Intuition", "Stoic", "Sly", "Leader", "Karma", "Awe", "Healer", "Sensei",
			"Juggling", "Gymnastics", "Boxing", "Scoop", "Judo", "Combo", "Strip", "Quickening", "Fist of Iron", "Doomstrike", "Guard",
			"Tactics", "Brutal", "Stalwart", "Checkmaster", "Vicious", "Resilient", "Charge", "Juggernaut", "Terror" };

	// TODO: IMPORTANT! Update this if the skill order ever changes
	public static int[] skillCosts = { 0, 20, 40, 40, 60, 60, 80, 80, 100, 20, 20, 20, 40, 40, 40, 60, 60, 80, 100, 20, 40, 40, 40, 60, 60,
			60, 80, 80, 100 };

	public static List<String> NAMES_CURMIAN = null;
	public static List<String> NAMES_DRAGORAN = null;
	public static List<String> NAMES_GRONK = null;
	public static List<String> NAMES_HUMAN = null;
	public static List<String> NAMES_KURGAN = null;
	public static List<String> NAMES_NYNAX = null;
	public static List<String> NAMES_SLITH = null;
	public static List<String> NAMES_XJS9000 = null;

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

	private static boolean namesDefined = false;

	@Override
	public String toString()
	{
		return "[" + name + "/" + hashCode() + "]";
		// return name + ": " + status + ", " + currentAP;
		// return name + ", " + getRank() + ", " + race + ", " + getSalary();
	}

	public Player(String serialString)
	{
		//
	}

	public Player(int myRace)
	{
		this(myRace, randomName(myRace));
	}

	public Player(int myRace, String myName)
	{
		race = myRace;
		name = myName;
		orderOfGainedSkills = "";
		status = STS_DECK;

		// TODO: give each race their initial starting skill

		if (race == RACE_CURMIAN)
		{
			attributes[ATT_AP] = 80;
			attributes[ATT_CH] = 30;
			attributes[ATT_ST] = 20;
			attributes[ATT_TG] = 30;
			attributes[ATT_RF] = 30;
			attributes[ATT_JP] = 90;
			attributes[ATT_HD] = 50;
			attributes[ATT_DA] = 30;
		} else if (race == RACE_DRAGORAN)
		{
			attributes[ATT_AP] = 70;
			attributes[ATT_CH] = 50;
			attributes[ATT_ST] = 40;
			attributes[ATT_TG] = 40;
			attributes[ATT_RF] = 40;
			attributes[ATT_JP] = 70;
			attributes[ATT_HD] = 70;
			attributes[ATT_DA] = 30;
		} else if (race == RACE_GRONK)
		{
			attributes[ATT_AP] = 50;
			attributes[ATT_CH] = 60;
			attributes[ATT_ST] = 70;
			attributes[ATT_TG] = 60;
			attributes[ATT_RF] = 20;
			attributes[ATT_JP] = 30;
			attributes[ATT_HD] = 20;
			attributes[ATT_DA] = 10;
		} else if (race == RACE_HUMAN)
		{
			attributes[ATT_AP] = 60;
			attributes[ATT_CH] = 50;
			attributes[ATT_ST] = 50;
			attributes[ATT_TG] = 50;
			attributes[ATT_RF] = 30;
			attributes[ATT_JP] = 60;
			attributes[ATT_HD] = 70;
			attributes[ATT_DA] = 30;
		} else if (race == RACE_KURGAN)
		{
			attributes[ATT_AP] = 60;
			attributes[ATT_CH] = 60;
			attributes[ATT_ST] = 60;
			attributes[ATT_TG] = 55;
			attributes[ATT_RF] = 40;
			attributes[ATT_JP] = 50;
			attributes[ATT_HD] = 40;
			attributes[ATT_DA] = 10;
		} else if (race == RACE_NYNAX)
		{
			attributes[ATT_AP] = 70;
			attributes[ATT_CH] = 40;
			attributes[ATT_ST] = 40;
			attributes[ATT_TG] = 40;
			attributes[ATT_RF] = 20;
			attributes[ATT_JP] = 50;
			attributes[ATT_HD] = 80;
			attributes[ATT_DA] = 20;
		} else if (race == RACE_SLITH)
		{
			attributes[ATT_AP] = 60;
			attributes[ATT_CH] = 60;
			attributes[ATT_ST] = 40;
			attributes[ATT_TG] = 70;
			attributes[ATT_RF] = 40;
			attributes[ATT_JP] = 40;
			attributes[ATT_HD] = 30;
			attributes[ATT_DA] = 20;
		} else if (race == RACE_XJS9000)
		{
			attributes[ATT_AP] = 60;
			attributes[ATT_CH] = 30;
			attributes[ATT_ST] = 30;
			attributes[ATT_TG] = 30;
			attributes[ATT_RF] = 10;
			attributes[ATT_JP] = 20;
			attributes[ATT_HD] = 30;
			attributes[ATT_DA] = 10;
		} else
		// "empty" player
		{
			attributes[ATT_AP] = 0;
			attributes[ATT_CH] = 0;
			attributes[ATT_ST] = 0;
			attributes[ATT_TG] = 0;
			attributes[ATT_RF] = 0;
			attributes[ATT_JP] = 0;
			attributes[ATT_HD] = 0;
			attributes[ATT_DA] = 0;
		}

		currentAP = attributes[ATT_AP];

		for (int i = 0; i < 28; i++)
		{
			gainedSkills[i] = false;
		}

		for (int i = 0; i < 4; i++)
		{
			equipment[i] = Equipment.EQUIP_NONE;
		}

		healInjuries();
		weeksOut = -1;
		injuryType = Player.INJURY_NONE;
		XP = 0;
		skillPoints = 0;
	}

	public static Player createEmptyPlayer()
	{
		return new Player(-1, "EMPTY");
	}

	public boolean isEmptyPlayer()
	{
		return (race == -1);
	}

	public int status; // BLOB, DECK, LATE, HURT, STUN, DEAD, DOWN, and OKAY
	public String name;
	public int race;
	private int[] attributes = new int[8];
	private int[] injuries = new int[8];
	private boolean[] gainedSkills = new boolean[TOTAL_SKILLS + 1];
	private String orderOfGainedSkills;
	public int currentAP; // TODO: perhaps extract this to the data layer

	// TODO: save these if they aren't - this includes equals() and clone()
	private int injuryType;
	private int weeksOut; // -1 if no injury; 0 for trivial or injury where the player is back the current week

	private int[] equipment = new int[4];

	private int XP;
	private int skillPoints;

	// public Stats careerStats; //TODO

	@Override
	public Player clone()
	{
		Player toRet = new Player(race, name);

		toRet.status = status;
		toRet.currentAP = currentAP;
		toRet.weeksOut = weeksOut;
		toRet.injuryType = injuryType;
		toRet.XP = XP;
		toRet.skillPoints = skillPoints;
		toRet.orderOfGainedSkills = orderOfGainedSkills;

		for (int i = 0; i < 8; i++)
		{
			toRet.attributes[i] = attributes[i];
			toRet.injuries[i] = injuries[i];
		}

		for (int i = 0; i < 4; i++)
		{
			toRet.equipment[i] = equipment[i];
		}

		for (int i = 0; i <= TOTAL_SKILLS; i++)
		{
			toRet.gainedSkills[i] = gainedSkills[i];
		}

		return toRet;
	}

	public List<String> getSkillList()
	{
		List<String> skillList = new ArrayList<String>();

		if (orderOfGainedSkills.length() == 0)
		{
			skillList.add("None");
			return skillList;
		}

		for (int i = 0; i < orderOfGainedSkills.length(); i++)
		{
			int skillIndex = (int) orderOfGainedSkills.charAt(i) - 65;
			skillList.add(skillNames[skillIndex]);
		}

		return skillList;
	}

	public boolean hasSkill(int index)
	{
		return gainedSkills[index];
	}

	public void gainSkill(int index)
	{
		if (gainedSkills[index])
			return;

		gainedSkills[index] = true;
		orderOfGainedSkills = orderOfGainedSkills + (char) (65 + index);

		skillPoints -= skillCosts[index];
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
		int[] baseSalary = { 20, 50, 80, 40, 70, 30, 60, 10 };
		int[] salaryMods = { 0, 0, 20, 40, 60, 90, 120, 160 };

		// TODO: add salary reductions for injuries

		return baseSalary[race] + salaryMods[getRank()];
	}

	public void addXP(Stats gameStats)
	{
		XP += gameStats.getXP();
		skillPoints += gameStats.getXP();
		// careerStats.updateWithResults(gameStats); //TODO
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

	public int getDetectionChance()
	{
		int toRet = 0;

		for (int i = 0; i < 4; i++)
		{
			if (equipment[i] > -1)
				toRet += Equipment.getEquipment(equipment[i]).detection;
		}

		if (gainedSkills[SKILL_SLY])
			toRet /= 2;

		return toRet;
	}

	public int equipItem(int eIndex)
	{
		System.out.println("EQUIPMENT: " + eIndex);
		return equipItem(Equipment.getEquipment(eIndex));
	}

	public int equipItem(Equipment e)
	{
		System.out.println("EQUIPMENT 2: " + e.index);

		int toRet = -1;

		if (equipment[e.type] != -1)
		{
			toRet = equipment[e.type];
		}

		equipment[e.type] = e.index;

		return toRet;
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

		if (attribute == Player.ATT_ST && gainedSkills[Player.SKILL_BRUTAL])
			bonus += 10;
		if (attribute == Player.ATT_TG && gainedSkills[Player.SKILL_STALWART])
			bonus += 10;
		if (attribute == Player.ATT_CH && gainedSkills[Player.SKILL_CHECKMASTER])
			bonus += 10;
		if (attribute == Player.ATT_JP && gainedSkills[Player.SKILL_GYMNASTICS])
			bonus += 10;
		if (attribute == Player.ATT_DA && gainedSkills[Player.SKILL_GYMNASTICS])
			bonus += 10;
		if (attribute == Player.ATT_RF && gainedSkills[Player.SKILL_BOXING])
			bonus += 10;
		if (attribute == Player.ATT_HD && gainedSkills[Player.SKILL_JUGGLING])
			bonus += 10;
		if (attribute == Player.ATT_AP && gainedSkills[Player.SKILL_QUICKENING])
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

	public int getAttributeWithoutModifiers(int attribute)
	{
		return attributes[attribute];
	}

	public int getRace()
	{
		return race;
	}

	public int getStatus()
	{
		return status;
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

	// ensures that the same names don't get picked twice
	private static String randomName(int race)
	{
		if (!namesDefined)
			defineNames();

		int nameIndex = 0;
		String toRet = "NO NAME";

		if (race == RACE_CURMIAN)
		{
			nameIndex = Engine.Randint(0, NAMES_CURMIAN.size() - 1);
			toRet = NAMES_CURMIAN.remove(nameIndex);
		} else if (race == RACE_DRAGORAN)
		{
			nameIndex = Engine.Randint(0, NAMES_DRAGORAN.size() - 1);
			toRet = NAMES_DRAGORAN.remove(nameIndex);
		} else if (race == RACE_GRONK)
		{
			nameIndex = Engine.Randint(0, NAMES_GRONK.size() - 1);
			toRet = NAMES_GRONK.remove(nameIndex);
		} else if (race == RACE_HUMAN)
		{
			nameIndex = Engine.Randint(0, NAMES_HUMAN.size() - 1);
			toRet = NAMES_HUMAN.remove(nameIndex);
		} else if (race == RACE_KURGAN)
		{
			nameIndex = Engine.Randint(0, NAMES_KURGAN.size() - 1);
			toRet = NAMES_KURGAN.remove(nameIndex);
		} else if (race == RACE_NYNAX)
		{
			nameIndex = Engine.Randint(0, NAMES_NYNAX.size() - 1);
			toRet = NAMES_NYNAX.remove(nameIndex);
		} else if (race == RACE_SLITH)
		{
			nameIndex = Engine.Randint(0, NAMES_SLITH.size() - 1);
			toRet = NAMES_SLITH.remove(nameIndex);
		} else if (race == RACE_XJS9000)
		{
			nameIndex = Engine.Randint(0, NAMES_XJS9000.size() - 1);
			toRet = NAMES_XJS9000.remove(nameIndex);
		}

		return toRet;
	}

	// TODO: define all the names, not just a sampling
	private static void defineNames()
	{
		namesDefined = true;

		// Define Curmian names
		NAMES_CURMIAN = new ArrayList<String>();
		NAMES_CURMIAN.add("Regee");
		NAMES_CURMIAN.add("Risbe");
		NAMES_CURMIAN.add("Roggo");
		NAMES_CURMIAN.add("Crannok");
		NAMES_CURMIAN.add("Croscka");
		NAMES_CURMIAN.add("Crigget");
		NAMES_CURMIAN.add("Refret");
		NAMES_CURMIAN.add("Rinick");
		NAMES_CURMIAN.add("Robnit");
		NAMES_CURMIAN.add("Crabnic");
		NAMES_CURMIAN.add("Cronop");
		NAMES_CURMIAN.add("Cridmic");
		NAMES_CURMIAN.add("Rennoc");
		NAMES_CURMIAN.add("Risboc");

		// Define Dragoran names
		NAMES_DRAGORAN = new ArrayList<String>();
		NAMES_DRAGORAN.add("Aytricus");
		NAMES_DRAGORAN.add("Flavicus");
		NAMES_DRAGORAN.add("Bruticus");
		NAMES_DRAGORAN.add("Octaicus");
		NAMES_DRAGORAN.add("Vaticus");
		NAMES_DRAGORAN.add("Creticus");
		NAMES_DRAGORAN.add("Lynticus");
		NAMES_DRAGORAN.add("Radicus");
		NAMES_DRAGORAN.add("Deticus");
		NAMES_DRAGORAN.add("Extricus");

		// Define Gronk names
		NAMES_GRONK = new ArrayList<String>();
		NAMES_GRONK.add("Darg");
		NAMES_GRONK.add("Sigg");
		NAMES_GRONK.add("Nirk");
		NAMES_GRONK.add("Lonk");
		NAMES_GRONK.add("Jork");
		NAMES_GRONK.add("Mook");
		NAMES_GRONK.add("Koog");
		NAMES_GRONK.add("Zuck");
		NAMES_GRONK.add("Purk");
		NAMES_GRONK.add("Jurg");

		// Define Human names
		NAMES_HUMAN = new ArrayList<String>();
		NAMES_HUMAN.add("Joe Cool");
		NAMES_HUMAN.add("Ben G.");
		NAMES_HUMAN.add("David A.");
		NAMES_HUMAN.add("Barry G.");
		NAMES_HUMAN.add("Milton");
		NAMES_HUMAN.add("Rocky");
		NAMES_HUMAN.add("Dr. Death");
		NAMES_HUMAN.add("Freddy");
		NAMES_HUMAN.add("Billy");
		NAMES_HUMAN.add("Bob");

		// Define Kurgan names
		NAMES_KURGAN = new ArrayList<String>();
		NAMES_KURGAN.add("Grimfang");
		NAMES_KURGAN.add("Lockjaw");
		NAMES_KURGAN.add("Razor");
		NAMES_KURGAN.add("Slice");
		NAMES_KURGAN.add("Cutter");
		NAMES_KURGAN.add("Chomps");
		NAMES_KURGAN.add("Deadsoul");
		NAMES_KURGAN.add("Doomchop");
		NAMES_KURGAN.add("Coolhate");
		NAMES_KURGAN.add("Psycho");

		// Define Nynax names
		NAMES_NYNAX = new ArrayList<String>();
		NAMES_NYNAX.add("Nenain");
		NAMES_NYNAX.add("Nythu");
		NAMES_NYNAX.add("Nxnon");
		NAMES_NYNAX.add("Thinu");
		NAMES_NYNAX.add("Thunon");
		NAMES_NYNAX.add("Thwix");
		NAMES_NYNAX.add("Phydox");
		NAMES_NYNAX.add("Nenu");
		NAMES_NYNAX.add("Nythain");
		NAMES_NYNAX.add("Nxcon");

		// Define Slith names
		NAMES_SLITH = new ArrayList<String>();
		NAMES_SLITH.add("Ssinot");
		NAMES_SLITH.add("Sscrit");
		NAMES_SLITH.add("Ssdesid");
		NAMES_SLITH.add("Ssloit");
		NAMES_SLITH.add("Ssthirt");
		NAMES_SLITH.add("Ssadwi");
		NAMES_SLITH.add("Ssviyt");
		NAMES_SLITH.add("SSqurit");
		NAMES_SLITH.add("Ssopit");
		NAMES_SLITH.add("Ssjisut");

		// Define XJS9000 names
		NAMES_XJS9000 = new ArrayList<String>();
		NAMES_XJS9000.add("XJS9011");
		NAMES_XJS9000.add("XJS9765");
		NAMES_XJS9000.add("XJS9567");
		NAMES_XJS9000.add("XJS9113");
		NAMES_XJS9000.add("XJS9234");
		NAMES_XJS9000.add("XJS9456");
		NAMES_XJS9000.add("XJS9809");
		NAMES_XJS9000.add("XJS9657");
		NAMES_XJS9000.add("XJS9690");
		NAMES_XJS9000.add("XJS9645");
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

		for (int i = 0; i <= TOTAL_SKILLS; i++)
			toReturn.add(String.valueOf(gainedSkills[i]));

		return toReturn;
	}

	private List<String> convertEquipmentToList()
	{
		List<String> toReturn = new ArrayList<String>();

		for (int i = 0; i < 4; i++)
			toReturn.add(String.valueOf(equipment[i]));

		return toReturn;
	}

	@Override
	public String saveAsText()
	{
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.PLAYER);

		String teamUid = getUniqueId();

		if (EntityMap.getPlayer(teamUid) == null)
			teamUid = EntityMap.put(teamUid, this);
		else
			teamUid = EntityMap.getSimpleKey(teamUid);

		ssb.addToken(new SaveToken(SaveTokenTag.P_UID, teamUid));
		ssb.addToken(new SaveToken(SaveTokenTag.P_STS, String.valueOf(status)));
		ssb.addToken(new SaveToken(SaveTokenTag.P_NAM, name));
		ssb.addToken(new SaveToken(SaveTokenTag.P_SKO, name));
		ssb.addToken(new SaveToken(SaveTokenTag.P_RCE, String.valueOf(race)));
		ssb.addToken(new SaveToken(SaveTokenTag.P_WKS, String.valueOf(weeksOut)));
		ssb.addToken(new SaveToken(SaveTokenTag.P_ITP, String.valueOf(injuryType)));
		ssb.addToken(new SaveToken(SaveTokenTag.P_XP_, String.valueOf(XP)));
		ssb.addToken(new SaveToken(SaveTokenTag.P_SP_, String.valueOf(skillPoints)));

		ssb.addToken(new SaveToken(SaveTokenTag.P_ATT, convertAttributesToList()));
		ssb.addToken(new SaveToken(SaveTokenTag.P_INJ, convertInjuriesToList()));
		ssb.addToken(new SaveToken(SaveTokenTag.P_SKL, convertSkillsToList()));
		ssb.addToken(new SaveToken(SaveTokenTag.P_EQP, convertEquipmentToList()));

		return ssb.getSaveString();
	}

	@Override
	public String loadFromText(String text) throws ParseException
	{
		SaveStringBuilder ssb = new SaveStringBuilder(EntityType.PLAYER, text);

		String toRet = getContentsForTag(ssb, SaveTokenTag.P_UID); // assumed to be defined

		setMember(ssb, SaveTokenTag.P_STS);
		setMember(ssb, SaveTokenTag.P_NAM);
		setMember(ssb, SaveTokenTag.P_SKO);
		setMember(ssb, SaveTokenTag.P_RCE);
		setMember(ssb, SaveTokenTag.P_WKS);
		setMember(ssb, SaveTokenTag.P_ITP);
		setMember(ssb, SaveTokenTag.P_XP_);
		setMember(ssb, SaveTokenTag.P_SP_);
		setMember(ssb, SaveTokenTag.P_ATT);
		setMember(ssb, SaveTokenTag.P_INJ);
		setMember(ssb, SaveTokenTag.P_SKL);
		setMember(ssb, SaveTokenTag.P_EQP);

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

		case P_SKO:
			saveToken = ssb.getToken(saveTokenTag);
			orderOfGainedSkills = saveToken.getContents();
			break;

		case P_RCE:
			saveToken = ssb.getToken(saveTokenTag);
			race = Integer.parseInt(saveToken.getContents());
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
			for (int i = 0; i <= TOTAL_SKILLS; i++)
			{
				gainedSkills[i] = Boolean.parseBoolean(strVals.get(i));
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

		if (status != player.status || !name.equals(player.name) || !orderOfGainedSkills.equals(player.orderOfGainedSkills)
				|| race != player.race || weeksOut != player.weeksOut || injuryType != player.injuryType || XP != player.XP
				|| skillPoints != player.skillPoints)
			return false;

		for (int i = 0; i < 8; i++)
		{
			if (attributes[i] != player.attributes[i])
				return false;

			if (injuries[i] != player.injuries[i])
				return false;
		}

		for (int i = 0; i <= TOTAL_SKILLS; i++)
		{
			if (gainedSkills[i] != player.gainedSkills[i])
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
		hash = 31 * hash + orderOfGainedSkills.hashCode();
		hash = 31 * hash + race;
		hash = 31 * hash + weeksOut;
		hash = 31 * hash + injuryType;
		hash = 31 * hash + XP;
		hash = 31 * hash + skillPoints;

		for (int i = 0; i < 8; i++)
		{
			hash = 31 * hash + attributes[i];
			hash = 31 * hash + injuries[i];
		}

		for (int i = 0; i <= TOTAL_SKILLS; i++)
			hash = hash + (gainedSkills[i] ? 1 : 0);

		for (int i = 0; i < 4; i++)
			hash = 31 * hash + equipment[i];

		return hash;
	}
}
