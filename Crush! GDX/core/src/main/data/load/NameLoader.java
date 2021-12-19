package main.data.load;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.presentation.common.GameSettings;
import main.presentation.common.Logger;

public class NameLoader
{
	public static List<String> loadNames(String filename)
	{
		List<String> names = new ArrayList<String>();
		
		String path = GameSettings.getRootDirectory() + "\\names\\" + filename.toLowerCase() + ".nms";
		
		BufferedReader in;
		Scanner s;
		
		try
		{
			in = new BufferedReader(new FileReader(path));
			s = new Scanner(in);
			int totalNames =  Integer.parseInt(s.nextLine());

			for (int i = 0; i < totalNames; i++)
			{
				String name = s.nextLine();
				names.add(name.substring(0, 1).toUpperCase() + name.substring(1));
			}

			in.close();
			s.close();
		} catch (IOException e)
		{
			Logger.error("NameLoader - Could not read file " + path);
			return names;
		}
		
		return names;
	}
}
