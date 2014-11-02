package main.presentation.teameditor.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;

import main.presentation.teameditor.TeamEditorGUI;

public class TeamImages
{
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

		ImageType raceTypes[] = { ImageType.PROFILE_CURMIAN, ImageType.PROFILE_DRAGORAN, ImageType.PROFILE_GRONK, ImageType.PROFILE_HUMAN,
				ImageType.PROFILE_KURGAN, ImageType.PROFILE_NYNAX, ImageType.PROFILE_SLITH, ImageType.PROFILE_XJS9000 };

		for (int i = 0; i < 8; i++)
		{
			raceTemplates[i] = ImageFactory.getImage(raceTypes[i]);
		}
	}

	public void updateColors(Color mainColor, Color trimColor)
	{
		for (int i = 0; i < 8; i++)
		{
			raceImages[i] = ColorReplacer.setColors(raceTemplates[i], mainColor, trimColor, TeamEditorGUI.BG_COLOR);
		}
	}

	public BufferedImage getPlayerImage(int race)
	{
		return raceImages[race];
	}
}
