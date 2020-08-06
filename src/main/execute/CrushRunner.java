package main.execute;

import main.data.entities.Equipment;
import main.presentation.common.Logger;
import main.presentation.legacy.framework.GameWindow;
import main.presentation.startupscreen.FullStartupScreen;

public class CrushRunner
{
	private static double interpolation = 0;
	private static final int TICKS_PER_SECOND = 25;
	private static final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
	private static final int MAX_FRAMESKIP = 5;
	
	private static GameWindow gameWindow;
	
	public static void main(String[] args)
	{
		Logger.setLogLevel(Logger.DEBUG);

		Equipment.defineEquipment();

//		FullStartupScreen startupScreen = new FullStartupScreen();
		gameWindow = new GameWindow();
		runGameLoop();
	}
	
	private static void runGameLoop()
	{
		double next_game_tick = System.currentTimeMillis();
		int loops;

		while (true)
		{
			loops = 0;
			while (System.currentTimeMillis() > next_game_tick && loops < MAX_FRAMESKIP)
			{
				next_game_tick += SKIP_TICKS;
				loops++;
			}

			interpolation = (System.currentTimeMillis() + SKIP_TICKS - next_game_tick / (double) SKIP_TICKS);
			gameWindow.refresh();
		}
	}
}
