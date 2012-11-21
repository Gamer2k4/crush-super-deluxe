package main.data.entities;

import java.util.ArrayList;
import java.util.List;

import main.logic.Engine;

public class Player
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
	
	//update these with real numbers later
	public static final int SKILL_RESILIENT = 20;
	public static final int SKILL_JUGGERNAUT = 19;
	public static final int SKILL_VICIOUS = 18;
	public static final int SKILL_DOOMSTRIKE = 17;
	public static final int SKILL_FIST_OF_IRON = 16;
	public static final int SKILL_COMBO = 15;
	public static final int SKILL_AWE = 14;
	public static final int SKILL_STOIC = 13;
	public static final int SKILL_CHECKMASTER = 12;
	public static final int SKILL_GYMNASTICS = 11;
	public static final int SKILL_QUICKENING = 10;
	public static final int SKILL_JUGGLING = 9;
	public static final int SKILL_BOXING = 8;
	public static final int SKILL_BRUTAL = 7;
	public static final int SKILL_STALWART = 6;
	public static final int SKILL_GUARD = 5;
	public static final int SKILL_TACTICS = 4;
	public static final int SKILL_CHARGE = 3;
	public static final int SKILL_SCOOP = 2;
	public static final int SKILL_INTUITION = 1;
	
	public static List<String> NAMES_CURMIAN = null;
	public static List<String> NAMES_DRAGORAN = null;
	public static List<String> NAMES_GRONK = null;
	public static List<String> NAMES_HUMAN = null;
	public static List<String> NAMES_KURGAN = null;
	public static List<String> NAMES_NYNAX = null;
	public static List<String> NAMES_SLITH = null;
	public static List<String> NAMES_XJS9000 = null;
	
	public static final int INJURY_KNOCKDOWN = 0;
	public static final int INJURY_STUN = 1;
	public static final int INJURY_TRIVIAL = 2;
	public static final int INJURY_MINOR = 3;
	public static final int INJURY_CRIPPLE_10 = 4;
	public static final int INJURY_CRIPPLE_15 = 5;
	public static final int INJURY_CRIPPLE_20 = 6;
	public static final int INJURY_DEATH_1 = 7;
	public static final int INJURY_DEATH_2 = 8;
	public static final int INJURY_DEATH_3 = 9;
	public static final int INJURY_DEATH_4 = 10;
	
	private static boolean namesDefined = false;
	
	public String toString()
	{
		return name + ": " + status + ", " + currentAP;
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
		status = STS_DECK;
		
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
		}
		else if (race == RACE_DRAGORAN)
		{
			attributes[ATT_AP] = 70;
			attributes[ATT_CH] = 50;
			attributes[ATT_ST] = 40;
			attributes[ATT_TG] = 40;
			attributes[ATT_RF] = 40;
			attributes[ATT_JP] = 70;
			attributes[ATT_HD] = 70;
			attributes[ATT_DA] = 30;
		}
		else if (race == RACE_GRONK)
		{
			attributes[ATT_AP] = 50;
			attributes[ATT_CH] = 60;
			attributes[ATT_ST] = 70;
			attributes[ATT_TG] = 60;
			attributes[ATT_RF] = 20;
			attributes[ATT_JP] = 30;
			attributes[ATT_HD] = 20;
			attributes[ATT_DA] = 10;
		}
		else if (race == RACE_HUMAN)
		{
			attributes[ATT_AP] = 50;
			attributes[ATT_CH] = 50;
			attributes[ATT_ST] = 50;
			attributes[ATT_TG] = 50;
			attributes[ATT_RF] = 30;
			attributes[ATT_JP] = 60;
			attributes[ATT_HD] = 70;
			attributes[ATT_DA] = 30;
		}
		else if (race == RACE_KURGAN)
		{
			attributes[ATT_AP] = 60;
			attributes[ATT_CH] = 60;
			attributes[ATT_ST] = 60;
			attributes[ATT_TG] = 55;
			attributes[ATT_RF] = 40;
			attributes[ATT_JP] = 50;
			attributes[ATT_HD] = 40;
			attributes[ATT_DA] = 10;
		}
		else if (race == RACE_NYNAX)
		{
			attributes[ATT_AP] = 70;
			attributes[ATT_CH] = 40;
			attributes[ATT_ST] = 40;
			attributes[ATT_TG] = 40;
			attributes[ATT_RF] = 20;
			attributes[ATT_JP] = 50;
			attributes[ATT_HD] = 80;
			attributes[ATT_DA] = 20;
		}
		else if (race == RACE_SLITH)
		{
			attributes[ATT_AP] = 60;
			attributes[ATT_CH] = 60;
			attributes[ATT_ST] = 40;
			attributes[ATT_TG] = 70;
			attributes[ATT_RF] = 40;
			attributes[ATT_JP] = 40;
			attributes[ATT_HD] = 30;
			attributes[ATT_DA] = 20;
		}
		else if (race == RACE_XJS9000)
		{
			attributes[ATT_AP] = 60;
			attributes[ATT_CH] = 30;
			attributes[ATT_ST] = 30;
			attributes[ATT_TG] = 30;
			attributes[ATT_RF] = 10;
			attributes[ATT_JP] = 20;
			attributes[ATT_HD] = 30;
			attributes[ATT_DA] = 10;
		}
		
		currentAP = attributes[ATT_AP];
		
		for (int i = 0; i < 28; i++)
		{
			hasSkill[i] = false;
		}
		
		healInjuries();
	}
	
	public int status;	//BLOB, DECK, LATE, HURT, STUN, DEAD, DOWN, and OKAY
	public String name;
	public int race;
	private int[] attributes = new int[8];
	private int[] injuries = new int[8];
	public boolean[] hasSkill = new boolean[28];
	public int currentAP;
	
	/*
	public int weeksOut;
	public String injuryType;
	public int[] equipment = new int[4];
	
	*/
	
	public int XP;	
	public Stats careerStats;
	
	public Player clone()
	{
		Player toRet = new Player(race, name);
		
		toRet.status = status;
		toRet.currentAP = currentAP;
		
		for (int i = 0; i < 8; i++)
		{
			toRet.attributes[i] = attributes[i];
			toRet.injuries[i] = injuries[i];
		}
		
		return toRet;
	}
	
	public void addXP(Stats gameStats)
	{
		XP += gameStats.getXP();
		careerStats.updateWithResults(gameStats);
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
	
	public int getAttributeWithModifiers(int attribute)
	{
		int bonus = 0;
		
		if (attribute == Player.ATT_ST && hasSkill[Player.SKILL_BRUTAL])
			bonus += 10;
		if (attribute == Player.ATT_TG && hasSkill[Player.SKILL_STALWART])
			bonus += 10;
		if (attribute == Player.ATT_CH && hasSkill[Player.SKILL_CHECKMASTER])
			bonus += 10;
		if (attribute == Player.ATT_JP && hasSkill[Player.SKILL_GYMNASTICS])
			bonus += 10;
		if (attribute == Player.ATT_DA && hasSkill[Player.SKILL_GYMNASTICS])
			bonus += 10;
		if (attribute == Player.ATT_RF && hasSkill[Player.SKILL_BOXING])
			bonus += 10;
		if (attribute == Player.ATT_HD && hasSkill[Player.SKILL_JUGGLING])
			bonus += 10;
		if (attribute == Player.ATT_AP && hasSkill[Player.SKILL_QUICKENING])
			bonus += 10;
		
		int toRet = attributes[attribute] + injuries[attribute] + bonus;
		
		if (toRet > 99) toRet = 99;
		if (toRet < 1) toRet = 1;
		
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
	
	//ensures that the same names don't get picked twice
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
		}
		else if (race == RACE_DRAGORAN)
		{
			nameIndex = Engine.Randint(0, NAMES_DRAGORAN.size() - 1);
			toRet = NAMES_DRAGORAN.remove(nameIndex);
		}
		else if (race == RACE_GRONK)
		{
			nameIndex = Engine.Randint(0, NAMES_GRONK.size() - 1);
			toRet = NAMES_GRONK.remove(nameIndex);
		}
		else if (race == RACE_HUMAN)
		{
			nameIndex = Engine.Randint(0, NAMES_HUMAN.size() - 1);
			toRet = NAMES_HUMAN.remove(nameIndex);
		}
		else if (race == RACE_KURGAN)
		{
			nameIndex = Engine.Randint(0, NAMES_KURGAN.size() - 1);
			toRet = NAMES_KURGAN.remove(nameIndex);
		}
		else if (race == RACE_NYNAX)
		{
			nameIndex = Engine.Randint(0, NAMES_NYNAX.size() - 1);
			toRet = NAMES_NYNAX.remove(nameIndex);
		}
		else if (race == RACE_SLITH)
		{
			nameIndex = Engine.Randint(0, NAMES_SLITH.size() - 1);
			toRet = NAMES_SLITH.remove(nameIndex);
		}
		else if (race == RACE_XJS9000)
		{
			nameIndex = Engine.Randint(0, NAMES_XJS9000.size() - 1);
			toRet = NAMES_XJS9000.remove(nameIndex);
		}
		
		return toRet;
	}
	
	private static void defineNames()
	{
		namesDefined = true;
		
		//Define Curmian names
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
		
		//Define Dragoran names
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
		
		//Define Gronk names
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
		
		//Define Human names
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
		
		//Define Kurgan names
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
		
		//Define Nynax names
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
		
		//Define Slith names
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
		
		//Define XJS9000 names
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
}
