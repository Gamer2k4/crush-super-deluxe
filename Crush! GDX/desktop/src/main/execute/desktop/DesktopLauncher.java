package main.execute.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import main.execute.CrushGame;
import main.execute.DebugConstants;
import main.presentation.common.Logger;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Logger.setLogLevel(DebugConstants.LOGGING_LEVEL);
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Crush! Super Deluxe";
		config.height = 400;
		config.width = 640;
		new LwjglApplication(new CrushGame(), config);
	}
}
