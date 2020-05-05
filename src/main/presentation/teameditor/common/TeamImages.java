package main.presentation.teameditor.common;

import java.awt.Color;
import java.awt.image.BufferedImage;

import main.data.entities.Equipment;
import main.data.entities.Player;
import main.presentation.common.image.AbstractColorReplacer;
import main.presentation.common.image.AbstractImageFactory;
import main.presentation.common.image.ImageType;
import main.presentation.common.image.LegacyColorReplacer;
import main.presentation.common.image.LegacyImageFactory;

public class TeamImages
{
	private BufferedImage[] raceTemplates;
	private BufferedImage[] raceImages;
	private BufferedImage[] gearTemplates;
	private BufferedImage[] gearImages;
	private BufferedImage helmetImage;
	
	private AbstractImageFactory imageFactory;
	private AbstractColorReplacer colorReplacer;

	public TeamImages(Color mainColor, Color trimColor)
	{
		imageFactory = LegacyImageFactory.getInstance();
		colorReplacer = LegacyColorReplacer.getInstance();
		
		raceImages = new BufferedImage[Player.TOTAL_RACES];
		gearImages = new BufferedImage[Equipment.EQUIP_TYPE_COUNT];
		loadRaceTemplates();
		loadGearTemplates();

		updateColors(mainColor, trimColor);
	}

	private void loadRaceTemplates()
	{
		raceTemplates = new BufferedImage[Player.TOTAL_RACES];

		ImageType raceTypes[] = new ImageType[Player.TOTAL_RACES];
		
		raceTypes[Player.RACE_HUMAN] = ImageType.PROFILE_HUMAN;
		raceTypes[Player.RACE_GRONK] = ImageType.PROFILE_GRONK;
		raceTypes[Player.RACE_CURMIAN] = ImageType.PROFILE_CURMIAN;
		raceTypes[Player.RACE_DRAGORAN] = ImageType.PROFILE_DRAGORAN;
		raceTypes[Player.RACE_NYNAX] = ImageType.PROFILE_NYNAX;
		raceTypes[Player.RACE_SLITH] = ImageType.PROFILE_SLITH;
		raceTypes[Player.RACE_KURGAN] = ImageType.PROFILE_KURGAN;
		raceTypes[Player.RACE_XJS9000] = ImageType.PROFILE_XJS9000;
		
		for (int i = 0; i < Player.TOTAL_RACES; i++)
		{
			raceTemplates[i] = imageFactory.getImage(raceTypes[i]);
		}
	}

	private void loadGearTemplates()
	{
		gearTemplates = new BufferedImage[Equipment.EQUIP_TYPE_COUNT];

		ImageType gearTypes[] = new ImageType[Equipment.EQUIP_TYPE_COUNT];
		
		gearTypes[Equipment.EQUIP_HEAVY_ARMOR] = ImageType.GEAR_HEAVY_PADS;
		gearTypes[Equipment.EQUIP_REINFORCED_ARMOR] = ImageType.GEAR_REINFORCED_PADS;
		gearTypes[Equipment.EQUIP_REPULSOR_ARMOR] = ImageType.GEAR_REPULSOR_PADS;
		gearTypes[Equipment.EQUIP_SPIKED_ARMOR] = ImageType.GEAR_SPIKED_PADS;
		gearTypes[Equipment.EQUIP_SURGE_ARMOR] = ImageType.GEAR_SURGE_PADS;
		gearTypes[Equipment.EQUIP_VORTEX_ARMOR] = ImageType.GEAR_VORTEX_PADS;
		
		gearTypes[Equipment.EQUIP_BACKFIRE_BELT] = ImageType.GEAR_BACKFIRE_BELT;
		gearTypes[Equipment.EQUIP_BOOSTER_BELT] = ImageType.GEAR_BOOSTER_BELT;
		gearTypes[Equipment.EQUIP_CLOAKING_BELT] = ImageType.GEAR_CLOAKING_BELT;
		gearTypes[Equipment.EQUIP_HOLOGRAM_BELT] = ImageType.GEAR_HOLOGRAM_BELT;
		gearTypes[Equipment.EQUIP_FIELD_INTEGRITY_BELT] = ImageType.GEAR_INTEGRITY_BELT;
		gearTypes[Equipment.EQUIP_MEDICAL_BELT] = ImageType.GEAR_MEDICAL_BELT;
		gearTypes[Equipment.EQUIP_SCRAMBLER_BELT] = ImageType.GEAR_SCRAMBLER_BELT;
		
		gearTypes[Equipment.EQUIP_SPIKED_BOOTS] = ImageType.GEAR_SPIKED_BOOTS;
		gearTypes[Equipment.EQUIP_BOUNDER_BOOTS] = ImageType.GEAR_BOUNDER_BOOTS;
		gearTypes[Equipment.EQUIP_INSULATED_BOOTS] = ImageType.GEAR_INSULATED_BOOTS;
		gearTypes[Equipment.EQUIP_MAGNETIC_BOOTS] = ImageType.GEAR_MAGNETIC_BOOTS;
		gearTypes[Equipment.EQUIP_SAAI_BOOTS] = ImageType.GEAR_SAAI_BOOTS;
		
		gearTypes[Equipment.EQUIP_MAGNETIC_GLOVES] = ImageType.GEAR_MAGNETIC_GLOVES;
		gearTypes[Equipment.EQUIP_REPULSOR_GLOVES] = ImageType.GEAR_REPULSOR_GLOVES;
		gearTypes[Equipment.EQUIP_SAAI_GLOVES] = ImageType.GEAR_SAAI_GLOVES;
		gearTypes[Equipment.EQUIP_SPIKED_GLOVES] = ImageType.GEAR_SPIKED_GLOVES;
		gearTypes[Equipment.EQUIP_SURGE_GLOVES] = ImageType.GEAR_SURGE_GLOVES;
		
		for (int i = 0; i < Equipment.EQUIP_TYPE_COUNT; i++)
		{
			gearTemplates[i] = imageFactory.getImage(gearTypes[i]);
		}
	}

	public void updateColors(Color mainColor, Color trimColor)
	{
		Color transparentBg = new Color(0, 0, 0, 0);
		
		for (int i = 0; i < Player.TOTAL_RACES; i++)
		{
			raceImages[i] = colorReplacer.setColors(raceTemplates[i], mainColor, trimColor, transparentBg);
		}
		
		for (int i = 0; i < Equipment.EQUIP_TYPE_COUNT; i++)
		{
			gearImages[i] = colorReplacer.setColors(gearTemplates[i], mainColor, trimColor, transparentBg);
		}
		
		helmetImage = colorReplacer.setColors(imageFactory.getImage(ImageType.EDITOR_HELMET_S), mainColor, trimColor, transparentBg);
	}

	public BufferedImage getPlayerImage(int race)
	{
		return raceImages[race];
	}

	public BufferedImage getEquipmentImage(int equipment)
	{
		return gearImages[equipment];
	}
	
	public BufferedImage getHelmetImage()
	{
		return helmetImage;
	}
}
