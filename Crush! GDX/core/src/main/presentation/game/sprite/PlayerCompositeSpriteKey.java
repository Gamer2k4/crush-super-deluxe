package main.presentation.game.sprite;

import main.data.entities.Race;

public class PlayerCompositeSpriteKey
{
	private int teamIndex;
	private Race race;
	private PlayerSpriteType type;
	
	public PlayerCompositeSpriteKey(int teamIndex, Race race, PlayerSpriteType type)
	{
		this.teamIndex = teamIndex;
		this.race = race;
		this.type = type;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((race == null) ? 0 : race.hashCode());
		result = prime * result + teamIndex;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		PlayerCompositeSpriteKey other = (PlayerCompositeSpriteKey) obj;
		return (race == other.race && teamIndex == other.teamIndex && type == other.type);
	}
	
	@Override
	public String toString()
	{
		return "PCSK[" + teamIndex + "," + race + "," + type + "]";
	}
}
