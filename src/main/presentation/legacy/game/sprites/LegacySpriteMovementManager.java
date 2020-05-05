package main.presentation.legacy.game.sprites;

public class LegacySpriteMovementManager
{
	private static LegacySpriteMovementManager instance = null;
	
	private LegacySpriteMovementManager()
	{
		//TODO
	}
	
	public static LegacySpriteMovementManager getInstance()
	{
		if (instance == null)
			instance = new LegacySpriteMovementManager();
		
		return instance;
	}
	
	public boolean movingFinished()
	{
		return true;
	}
}
