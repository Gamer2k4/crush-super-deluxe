package main.presentation.common;

public class Logger
{
	public static final int INFO = 0;
	public static final int DEBUG = 1;
	public static final int WARN = 2;
	public static final int ERROR = 3;
	public static final int OUTPUT = 4;		//this is because we always want to display info text ("loading map"), even if we're not showing warnings and the like
	
	private static int logLevelToShow = 0;
	private static String[] levelStrings = {"[INFO] ", "[DEBUG] ", "[WARN] ", "[ERROR] ", ""}; 

	public static void setLogLevel(int logLevel)
	{
		logLevelToShow = logLevel;
	}
	
	private static void logMessage(String message, int logLevel)
	{
		logMessage(message, logLevel, true);
	}
	
	public static void logMessage(String message, int logLevel, boolean newLine)
	{
		
		
		if (logLevel < logLevelToShow)
			return;
		
		System.out.print(levelStrings[logLevel] + message);
		
		if (newLine)
			System.out.print("\n");
	}
	
	public static void info(String message) {
		logMessage(message, INFO);
	}
	
	public static void debug(String message) {
		logMessage(message, DEBUG);
	}
	
	public static void warn(String message) {
		logMessage(message, WARN);
	}
	
	public static void error(String message) {
		logMessage(message, ERROR);
	}
	
	public static void output(String message) {
		logMessage(message, OUTPUT);
	}
}
