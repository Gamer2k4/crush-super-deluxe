package main.data.load;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class HexTeamLoader extends ByteFileReader
{
	public static void printTeam(String fullPath)
	{
		int MAX_TEAM_PLAYERS = 35;

		File file = new File(fullPath);
		FileInputStream fis = null;
		DataInputStream dis = null;

		try
		{
			fis = new FileInputStream(file);
			dis = new DataInputStream(fis);

			System.out.println("Total file size to read (in bytes) : " + fis.available());

			System.out.print("\nLoading Players:");
			for (int i = 0; i < MAX_TEAM_PLAYERS; i++)
			{
				printLegacyPlayer(dis);
			}
			
			System.out.println("\n\nLoading Team:");
			printLegacyTeam(dis);
			
			dis.close();
			fis.close();

		} catch (IOException e)
		{
			e.printStackTrace();
			System.out.println("Exception when reading legacy team file at " + fullPath);
		}
	}

	private static void printLegacyPlayer(DataInputStream dis) throws IOException
	{
		System.out.println();
		
		for (int i = 0; i < 172; i++)
		{
			int value = readUnsignedByte(dis);
			
			System.out.print(value + ", ");
		}
	}

	private static void printLegacyTeam(DataInputStream dis) throws IOException
	{
		for (int i = 0; i < 248; i++)
		{
			int value = readUnsignedByte(dis);
			
			System.out.print(value + ", ");
		}
	}
}
