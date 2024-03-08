package main.data.entities;

import java.awt.Color;

import main.presentation.legacy.common.LegacyUiConstants;

public enum Skill
{
	DEATH_REEK("Death Reek", 1, 0, 6),
	POP_UP("Pop Up", 2, 0, 6),
	HIGH_JUMP("High Jump", 3, 0, 6),
	REGENERATE("Regenerate", 4, 0, 6),
	BLOODLUST("Bloodlust", 5, 0, 6),
	HIVE_MIND("Hive Mind", 6, 0, 6),
	GYRO_STABILIZER("Gyro Stabilizer", 7, 0, 6),
	UNCOMMON_VALOR("Uncommon Valor", 8, 0, 6),
	TERROR("Terror", 9, 100, 4),
	JUGGERNAUT("Juggernaut", 10, 80, 3),
	TACTICS("Tactics", 11, 40, 1),
	VICIOUS("Vicious", 12, 60, 2),
	BRUTAL("Brutal", 13, 40, 1),
	CHECKMASTER("Checkmaster", 14, 60, 2),
	STALWART("Stalwart", 15, 40, 1),
	GUARD("Guard", 16, 20, 0),
	RESILIENT("Resilient", 17, 60, 2),
	CHARGE("Charge", 18, 80, 3),
	BOXING("Boxing", 19, 20, 0),
	COMBO("Combo", 20, 40, 1),
	QUICKENING("Quickening", 21, 60, 2),
	GYMNASTICS("Gymnastics", 22, 20, 0),
	JUGGLING("Juggling", 23, 20, 0),
	SCOOP("Scoop", 24, 40, 1),
	STRIP("Strip", 25, 60, 2),
	JUDO("Judo", 26, 40, 1),
	FIST_OF_IRON("Fist of Iron", 27, 80, 3),
	DOOMSTRIKE("Doomstrike", 28, 100, 4),
	AWE("Awe", 29, 80, 3),
	STOIC("Stoic", 30, 40, 1),
	LEADER("Leader", 31, 60, 2),
	SENSEI("Sensei", 32, 100, 4),
	SLY("Sly", 33, 40, 1),
	INTUITION("Intuition", 34, 20, 0),
	HEALER("Healer", 35, 80, 3),
	KARMA("Karma", 36, 60, 2),
	NINJA_MASTER("Ninja Master", 37, 0, 5),
	HIVE_OVERSEER("Hive Overseer", 38, 60, 2);
	//TODO: tie the cost with the tier (20 * (tier + 1)), rather than having them be separate values
	
	private String name;
	private int legacyIndex;
	private int cost;
	private int tier;
	
	private Color[] colors = {
			LegacyUiConstants.COLOR_LEGACY_DULL_WHITE,
			LegacyUiConstants.COLOR_LEGACY_DULL_GREEN,
			LegacyUiConstants.COLOR_LEGACY_BLUE,
			new Color(160, 0, 224),	//this is the purple team color from the color map
			LegacyUiConstants.COLOR_LEGACY_ORANGE,
			LegacyUiConstants.COLOR_LEGACY_GOLD,
			LegacyUiConstants.COLOR_LEGACY_GOLD		//racial ability
			};
	
	Skill(String name, int legacyIndex, int cost, int tier)
	{
		this.name = name;
		this.legacyIndex = legacyIndex;
		this.cost = cost;
		this.tier = tier;
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

	public int getTier()
	{
		return tier;
	}
	
	public Color getColor()
	{
		return colors[tier];
	}
}
