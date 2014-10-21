package main.presentation.teameditor.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

public class TeamImages
{
	private Color bgColor = new Color(238, 238, 238);

	private BufferedImage[] raceTemplates;
	private BufferedImage[] raceImages;

	public TeamImages(Color mainColor, Color trimColor)
	{
		raceImages = new BufferedImage[8];
		loadRaceTemplates();
		updateColors(mainColor, trimColor);
	}

	private void loadRaceTemplates()
	{
		raceTemplates = new BufferedImage[8];

		Path path = Paths.get(System.getProperty("user.dir"));
		String pathString = path.getParent().toString() + "\\resources\\player_profiles\\";
		String curPathString = "";

		String raceFiles[] = { "curmian_160x160.png", "dragoran_160x160.png", "gronk_160x160.png", "human1_160x160.png",
				"kurgan_160x160.png", "nynax_160x160.png", "slith_160x160.png", "xjs9000_160x160.png" };

		try
		{
			for (int i = 0; i < 8; i++)
			{
				curPathString = pathString + raceFiles[i];
				raceTemplates[i] = ImageIO.read(new File(curPathString));
			}
		} catch (IOException e)
		{
			System.out.println("TeamImages.java - Could not load player graphic! Path was " + curPathString);
		}
	}

	public void updateColors(Color mainColor, Color trimColor)
	{
		for (int i = 0; i < 8; i++)
		{
			raceImages[i] = ColorReplacer.setColors(raceTemplates[i], mainColor, trimColor, bgColor);
		}
	}

	public BufferedImage getPlayerImage(int race)
	{
		return raceImages[race];
	}
}
