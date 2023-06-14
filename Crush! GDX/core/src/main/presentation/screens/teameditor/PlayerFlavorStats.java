package main.presentation.screens.teameditor;

import main.data.entities.Race;

public class PlayerFlavorStats
{
	public String height;
	public String weight;
	public String world;
	public String special;

	private PlayerFlavorStats(String height, String weight, String world, String special)
	{
		this.height = height;
		this.weight = weight;
		this.world = world;
		this.special = special;
	}
	
	public static PlayerFlavorStats getFlavorStats(Race race)
	{
		switch (race)
		{
		case CURMIAN:
			return new PlayerFlavorStats("2.8", "85", "Curmadon", "High Jump");
		case DRAGORAN:
			return new PlayerFlavorStats("7.1", "145", "Bakus", "Pop Up");
		case GRONK:
			return new PlayerFlavorStats("7.0", "560", "Daboo", "Regenerate");
		case HUMAN:
			return new PlayerFlavorStats("6.0", "200", "Earth", "Uncommon Valor");
		case KURGAN:
			return new PlayerFlavorStats("4.2", "275", "Kra", "Blood Lust");
		case NYNAX:
			return new PlayerFlavorStats("4.9", "110", "Volticon", "Hive Mind");
		case SLITH:
			return new PlayerFlavorStats("5.4", "260", "Slogotha", "Death Reek");
		case XJS9000:
			return new PlayerFlavorStats("6.0", "375", "XJS - Fab. 9", "Gyro Stabilizer");
		default:
			return new PlayerFlavorStats("0.0", "0", "None", "None");
		}
	}
}
