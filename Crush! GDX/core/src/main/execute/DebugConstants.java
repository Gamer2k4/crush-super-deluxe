package main.execute;

import com.badlogic.gdx.graphics.Color;

import main.data.entities.Arena;
import main.presentation.common.Logger;

public class DebugConstants
{
//	public static final int ARENA_OVERRIDE = Arena.ARENA_MAELSTROM;
	public static final int ARENA_OVERRIDE = Arena.ARENA_SAVANNA;
//	public static final int ARENA_OVERRIDE = Arena.ARENA_NEXUS;
//	public static final int ARENA_OVERRIDE = Arena.ARENA_JACKALS_LAIR;
//	public static final int ARENA_OVERRIDE = -1;
	public static final boolean ALWAYS_BIN_SUCCESS = false;
	public static final boolean HIDE_EVENT_EXCEPTIONS = false;
	public static final boolean AUDIO_ON = false;
	public static final int LOGGING_LEVEL = Logger.WARN;
	public static final boolean ABSTRACT_SIMULATION = true;
	public static final Color BG_TINT = Color.WHITE;
	
	public static final boolean PLAYER0_IS_HUMAN = false;
	public static final boolean PLAYER1_IS_HUMAN = false;
	public static final boolean PLAYER2_IS_HUMAN = false;
}
