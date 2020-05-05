package test.presentation.legacy.game.sprites;

import org.junit.Before;
import org.junit.Test;

import main.presentation.legacy.game.sprites.Sprite;
import main.presentation.legacy.game.sprites.SpriteFactory;
import main.presentation.legacy.game.sprites.TileSpriteType;

public class AnimatedSpriteTest
{
	double interpolation = 0;
	final int TICKS_PER_SECOND = 25;
	final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
	final int MAX_FRAMESKIP = 5;
	
	private AnimationWindow mainWindow;
	
	@Before
	public void setUp()
	{
		mainWindow = new AnimationWindow();
	}
	
	@Test
	public void testSprite()
	{
		Sprite sprite = SpriteFactory.getInstance().getTileSprite(TileSpriteType.BALL);
		mainWindow.setSprite(sprite);
		
		double next_game_tick = System.currentTimeMillis();
	    int loops;

	    while (true) {
	        loops = 0;
	        while (System.currentTimeMillis() > next_game_tick
	                && loops < MAX_FRAMESKIP) {

//	            mainWindow.refresh();

	            next_game_tick += SKIP_TICKS;
	            loops++;
	        }

	        interpolation = (System.currentTimeMillis() + SKIP_TICKS - next_game_tick
	                / (double) SKIP_TICKS);
	        mainWindow.refresh();
	    }
	}
}
