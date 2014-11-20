package main.presentation.teameditor.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;

import main.presentation.teameditor.TeamEditorGUI;

public class TeamImages
{
	private BufferedImage[] raceTemplates;
	private BufferedImage[] raceImages;
	private BufferedImage[] gearTemplates;
	private BufferedImage[] gearImages;

	public TeamImages(Color mainColor, Color trimColor)
	{
		raceImages = new BufferedImage[8];
		gearImages = new BufferedImage[23];
		loadRaceTemplates();
		loadGearTemplates();

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

	private void loadGearTemplates()
	{
		gearTemplates = new BufferedImage[23];

		ImageType gearTypes[] = { ImageType.GEAR_REINFORCED_PADS, ImageType.GEAR_HEAVY_PADS, ImageType.GEAR_SPIKED_PADS,
				ImageType.GEAR_SURGE_PADS, ImageType.GEAR_VORTEX_PADS, ImageType.GEAR_REPULSOR_PADS, ImageType.GEAR_SAAI_GLOVES,
				ImageType.GEAR_REPULSOR_GLOVES, ImageType.GEAR_MAGNETIC_GLOVES, ImageType.GEAR_SURGE_GLOVES, ImageType.GEAR_SPIKED_GLOVES,
				ImageType.GEAR_SAAI_BOOTS, ImageType.GEAR_BOUNDER_BOOTS, ImageType.GEAR_MAGNETIC_BOOTS, ImageType.GEAR_SPIKED_BOOTS,
				ImageType.GEAR_INSULATED_BOOTS, ImageType.GEAR_MEDICAL_BELT, ImageType.GEAR_INTEGRITY_BELT, ImageType.GEAR_BOOSTER_BELT,
				ImageType.GEAR_BACKFIRE_BELT, ImageType.GEAR_CLOAKING_BELT, ImageType.GEAR_HOLOGRAM_BELT, ImageType.GEAR_SCRAMBLER_BELT };

		for (int i = 0; i < 23; i++)
		{
			gearTemplates[i] = ImageFactory.getImage(gearTypes[i]);
		}
	}

	public void updateColors(Color mainColor, Color trimColor)
	{
		for (int i = 0; i < 8; i++)
		{
			raceImages[i] = ColorReplacer.setColors(raceTemplates[i], mainColor, trimColor, TeamEditorGUI.BG_COLOR);
		}
		
		for (int i = 0; i < 23; i++)
		{
			gearImages[i] = ColorReplacer.setColors(gearTemplates[i], mainColor, trimColor, TeamEditorGUI.BG_COLOR);
		}
	}

	public BufferedImage getPlayerImage(int race)
	{
		return raceImages[race];
	}

	public BufferedImage getEquipmentImage(int equipment)
	{
		return gearImages[equipment];
	}
}
