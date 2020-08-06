package main.presentation.teameditor.common;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import main.data.entities.Equipment;
import main.data.entities.Race;
import main.data.entities.Team;
import main.presentation.common.image.AbstractColorReplacer;
import main.presentation.common.image.AbstractImageFactory;
import main.presentation.common.image.ImageType;
import main.presentation.common.image.LegacyColorReplacer;
import main.presentation.common.image.LegacyImageFactory;

public class TeamImages
{
	private static Map<Race, BufferedImage> raceTemplates = null;
	private static Map<Race, BufferedImage> largeRaceTemplates = null;
	private static Map<Race, BufferedImage> raceImages = new HashMap<Race, BufferedImage>();
	private static Map<Race, BufferedImage> largeRaceImages = new HashMap<Race, BufferedImage>();
	
	private static BufferedImage[] gearTemplates = null;
	private BufferedImage[] gearImages = null;
	private BufferedImage helmetImage = null;
	
	private AbstractImageFactory imageFactory;
	private AbstractColorReplacer colorReplacer;

	public TeamImages(Color mainColor, Color trimColor)
	{
		imageFactory = LegacyImageFactory.getInstance();
		colorReplacer = LegacyColorReplacer.getInstance();
		
		gearImages = new BufferedImage[Equipment.EQUIP_TYPE_COUNT];
		loadRaceTemplates();
		loadGearTemplates();

		updateColors(mainColor, trimColor);
	}

	private void loadRaceTemplates()
	{
		if (raceTemplates != null)
			return;
		
		raceTemplates = new HashMap<Race, BufferedImage>();
		largeRaceTemplates = new HashMap<Race, BufferedImage>();
		
		for (Race race : Race.values())
		{
			String imageName = "PROFILE_" + race.name();
			String largeImageName = imageName + "_L";
			
			raceTemplates.put(race, imageFactory.getImage(ImageType.valueOf(imageName)));
			largeRaceTemplates.put(race, imageFactory.getImage(ImageType.valueOf(largeImageName)));
		}
	}

	private void loadGearTemplates()
	{
		if (gearTemplates != null)
			return;
		
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
		
		for (Race race : Race.values())
		{
			raceImages.put(race, colorReplacer.setColors(raceTemplates.get(race), mainColor, trimColor, transparentBg));
			largeRaceImages.put(race, colorReplacer.setColors(largeRaceTemplates.get(race), mainColor, trimColor, transparentBg));
		}
		
		for (int i = 0; i < Equipment.EQUIP_TYPE_COUNT; i++)
		{
			gearImages[i] = colorReplacer.setColors(gearTemplates[i], mainColor, trimColor, transparentBg);
		}
		
		helmetImage = colorReplacer.setColors(imageFactory.getImage(ImageType.EDITOR_HELMET_S), mainColor, trimColor, transparentBg);
	}

	public BufferedImage getPlayerImage(Race race)
	{
		return raceImages.get(race);
	}

	public BufferedImage getLargePlayerImage(Race race)
	{
		return largeRaceImages.get(race);
	}

	public BufferedImage getEquipmentImage(int equipment)
	{
		return gearImages[equipment];
	}
	
	public BufferedImage getHelmetImage()
	{
		return helmetImage;
	}
	
	public static BufferedImage getHelmetImage(Team team)
	{
		return LegacyColorReplacer.getInstance().setColors(LegacyImageFactory.getInstance().getImage(ImageType.EDITOR_HELMET_S), team.teamColors[0], team.teamColors[1], new Color(0, 0, 0, 0));
	}
}
