package main.presentation.common;

import main.presentation.game.PresentationMode;

public class GameSettings
{
//	private static PresentationMode presentationMode = PresentationMode.CURSES;
	private static PresentationMode presentationMode = PresentationMode.LEGACY;
	private static String rootDirectory = "C:\\Games\\Crush! Deluxe";
	
	
	public static PresentationMode getPresentationMode()
	{
		return presentationMode;
	}
	public static void setPresentationMode(PresentationMode presentationMode)
	{
		GameSettings.presentationMode = presentationMode;
	}
	public static String getRootDirectory()
	{
		return rootDirectory;
	}
	public static void setRootDirectory(String dataFileDirectory)
	{
		GameSettings.rootDirectory = dataFileDirectory;
	}
}
