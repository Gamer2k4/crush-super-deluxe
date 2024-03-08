package main.data.entities;

public enum Quirk
{
	// confirmed to match up with legacy format
	MORON("Moron", 1),
	INTELLIGENT("Intelligent", 2),
	EGOMANIAC("Egomaniac", 3),
	SLACKER("Slacker", 4),
	TECHNOPHOBIA("Technophobia", 5),
	ELECTROPHOBIA("Electrophobia", 6),
	BLOBBOPHOBIA("Blobbophobia", 7),
	DISPLACER("Displacer", 8),
	BOUNCER("Bouncer", 9),
	IMMUNITY("Immunity", 10),
	SPACE_ROT("Space Rot", 11),
	GRIT("Grit", 12);
	
	private String name;
	private int legacyIndex;
	
	Quirk(String name, int legacyIndex)
	{
		this.name = name;
		this.legacyIndex = legacyIndex;
	}
	
	public static int getTotalSkills()
	{
		return values().length;
	}
	
	public static Quirk fromLegacyIndex(int index)
	{
		for (Quirk quirk : values())
		{
			if (quirk.legacyIndex == index)
				return quirk;
		}
		
		throw new IllegalArgumentException("No quirk exists for legacy index " + index + ".");
	}

	public String getName()
	{
		return name;
	}

	public int getLegacyIndex()
	{
		return legacyIndex;
	}
}
