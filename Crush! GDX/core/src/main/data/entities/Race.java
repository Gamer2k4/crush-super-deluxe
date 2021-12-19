package main.data.entities;

public enum Race
{
	HUMAN(0),
	GRONK(1),
	CURMIAN(2),
	DRAGORAN(3),
	NYNAX(4),
	SLITH(5),
	KURGAN(6),
	XJS9000(7);
	
	private int index;
	
	private Race(int index)
	{
		this.index = index;
	}
	
	public int getIndex()
	{
		return index;
	}
	
	public static Race getRace(int raceIndex)
	{
		for (Race race : values())
		{
			if (race.index == raceIndex)
				return race;
		}
		
		return null;
	}
	
	public static int getTotalRaces()
	{
		return values().length;
	}
}
