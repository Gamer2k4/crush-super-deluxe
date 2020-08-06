package main.data.entities;

public enum Skill
{
	DEATH_REEK("Death Reek", 1, 0),
	POP_UP("Pop Up", 2, 0),
	HIGH_JUMP("High Jump", 3, 0),
	REGENERATE("Regenerate", 4, 0),
	BLOODLUST("Bloodlust", 5, 0),
	HIVE_MIND("Hive Mind", 6, 0),
	GYRO_STABILIZER("Gyro Stabilizer", 7, 0),
	UNCOMMON_VALOR("Uncommon Valor", 8, 0),
	TERROR("Terror", 9, 100),
	JUGGERNAUT("Juggernaut", 10, 80),
	TACTICS("Tactics", 11, 40),
	VICIOUS("Vicious", 12, 60),
	BRUTAL("Brutal", 13, 40),
	CHECKMASTER("Checkmaster", 14, 60),
	STALWART("Stalwart", 15, 40),
	GUARD("Guard", 16, 20),
	RESILIENT("Resilient", 17, 60),
	CHARGE("Charge", 18, 80),
	BOXING("Boxing", 19, 20),
	COMBO("Combo", 20, 40),
	QUICKENING("Quickening", 21, 60),
	GYMNASTICS("Gymnastics", 22, 20),
	JUGGLING("Juggling", 23, 20),
	SCOOP("Scoop", 24, 40),
	STRIP("Strip", 25, 60),
	JUDO("Judo", 26, 40),
	FIST_OF_IRON("Fist of Iron", 27, 80),
	DOOMSTRIKE("Doomstrike", 28, 100),
	AWE("Awe", 29, 80),
	STOIC("Stoic", 30, 40),
	LEADER("Leader", 31, 60),
	SENSEI("Sensei", 32, 100),
	SLY("Sly", 33, 40),
	INTUITION("Intuition", 34, 20),
	HEALER("Healer", 35, 80),
	KARMA("Karma", 36, 60),
	NINJA_MASTER("Ninja Master", 37, 0);
	
	private String name;
	private int legacyIndex;
	private int cost;
	
	Skill(String name, int legacyIndex, int cost)
	{
		this.name = name;
		this.legacyIndex = legacyIndex;
		this.cost = cost;
	}
	
	public static int getTotalSkills()
	{
		return values().length;
	}
	
	public static Skill fromLegacyIndex(int index)
	{
		for (Skill skill : values())
		{
			if (skill.legacyIndex == index)
				return skill;
		}
		
		throw new IllegalArgumentException("No skill exists for legacy index " + index + ".");
	}

	public String getName()
	{
		return name;
	}

	public int getLegacyIndex()
	{
		return legacyIndex;
	}

	public int getCost()
	{
		return cost;
	}
}
