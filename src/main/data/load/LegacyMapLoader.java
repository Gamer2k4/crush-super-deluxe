package main.data.load;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.data.entities.Arena;
import main.presentation.common.GameSettings;
import main.presentation.common.Logger;

public class LegacyMapLoader extends ByteFileReader
{
	public static List<Arena> loadLegacyMaps()
	{
		List<Arena> arenas = new ArrayList<Arena>();
		
		String path = GameSettings.getRootDirectory() + "\\DATA\\MAPLIST.DAT";
		
		BufferedReader in;
		Scanner s;
		
		try
		{
			in = new BufferedReader(new FileReader(path));
			s = new Scanner(in);

			while (s.hasNextLine())
			{
				String nextLine = s.nextLine().replace("\\\\", "\\");
				String arenaPath = GameSettings.getRootDirectory() + nextLine.substring(1);
				System.out.println("\t" + arenaPath);
				arenas.add(loadMap(arenas.size(), arenaPath));
			}

			in.close();
			s.close();
		} catch (IOException e)
		{
			System.out.println("LegacyMapLoader - Could not read file " + path);
			return new ArrayList<Arena>();
		}
		
		return arenas;
	}
	
	public static Arena loadMap(int arenaIndex, String fullPath)
	{
		Arena arena = null;

		File file = new File(fullPath);
		FileInputStream fis = null;
		DataInputStream dis = null;

		try
		{
			fis = new FileInputStream(file);
			dis = new DataInputStream(fis);

			//System.out.println("Total file size to read (in bytes) : " + fis.available());

			arena = loadLegacyArena(arenaIndex, dis);

			dis.close();
			fis.close();

		} catch (IOException e)
		{
			e.printStackTrace();
			System.out.println("Exception when reading legacy map file at " + fullPath);
		}

		return arena;
	}

	private static Arena loadLegacyArena(int arenaIndex, DataInputStream dis) throws IOException
	{
		String arenaName = "";
		String mapData = "";
		String legacyTileData = "";
		int[][] mapArray = new int[32][32];

		arenaName = extractArenaName(dis).toUpperCase();
		
		// skip to the stuff I recognize
		scanBytes(dis, 49 - arenaName.length());
		
		//my code is all (row, column) coordinates, but this is all (x, y), so reference them backwards
		for (int i = 0; i < 32; i++)
		{
			for (int j = 0; j < 32; j++)
			{
				mapArray[j][i] = readUnsignedByte(dis);
			}
		}
		
		//but now that the tiles are in the right order, pull them out normally (no longer transposed)
		for (int i = 1; i < 31; i++)
		{
			for (int j = 1; j < 31; j++)
			{
				mapData = mapData + getTileCharacterForLegacyTileData(mapArray[i][j]);
				legacyTileData = legacyTileData + (char)mapArray[i][j];
			}
		}
		
		Logger.debug("\nMap data: " + mapData);
//		Logger.logMessage("\nLegacy data: " + legacyTileData, Logger.DEBUG);
		
		// Just load the image data separately.  It would be nice to do it together, but the code is a little smoother this way.

		return new Arena(arenaIndex, arenaName, mapData, legacyTileData);
		
//		for (int i = 0; i < 32; i++)
//		{
//			for (int j = 0; j < 32; j++)
//			{
//				System.out.print(getIntAsText(mapArray[i][j], 3) + " ");
//			}
//			
//			System.out.println();
//		}
		
	}

	private static String extractArenaName(DataInputStream dis) throws IOException
	{
		String name = "";
		
		do {
			char nextChar = readCharByte(dis);
			if (nextChar == '|')
				return name;
			
			name = name + nextChar;
		} while (true);
	}

	private static int getTileCharacterForLegacyTileData(int i)
	{
		if (i == 40)
			return Arena.TILE_WALL;
		else if (i == 64)
			return Arena.TILE_TELE;
		else if (i == 76)
			return Arena.TILE_SHOCK;
		else if (i >= 44 && i <= 47)
			return Arena.TILE_BIN;
		else if (i >= 68 && i <= 71)
			return Arena.TILE_PAD;
		else if (i >= 132 && i <= 147)
			return Arena.TILE_GOAL;
		
		return Arena.TILE_FLOOR;
	}
	
//	private static String getIntAsText(int value, int length)
//	{
//		return addLeadingZeros(String.valueOf(value), length);
//	}
//	
//	private static String addLeadingZeros(String s, int length)
//	{
//		if (s.length() >= length)
//			return s;
//
//		return String.format("%0" + (length - s.length()) + "d%s", 0, s);
//	}
}
