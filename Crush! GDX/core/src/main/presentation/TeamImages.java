package main.presentation;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;

import main.data.entities.Equipment;
import main.data.entities.Race;
import main.presentation.common.image.AbstractColorReplacer;
import main.presentation.common.image.ColorReplacer;
import main.presentation.common.image.TeamColorType;

//TODO: uncomment line about helmetColoredImage once I load it in the imagefactory
//TODO: based on how texture regions work, I might be better served just updating the colors on the original equipment
//		texture, then offering sections of it as requested
public class TeamImages
{
	private ColorPairKey colorPairKey;
	
	private static Map<Race, Texture> raceProfileTemplates = null;
	private static Map<Race, Texture> raceSpriteTemplates = null;
	private static Texture[] gearTemplates = null;
	
	private Map<Race, Texture> raceColoredProfiles = new HashMap<Race, Texture>();
	private Map<Race, Texture> raceColoredSprites = new HashMap<Race, Texture>();
	private Texture[] gearColoredImages = null;
	private Texture helmetColoredImage = null;
	
	private static ImageFactory imageFactory = ImageFactory.getInstance();
	private static AbstractColorReplacer colorReplacer = ColorReplacer.getInstance();

	public TeamImages(TeamColorType mainColor, TeamColorType trimColor)
	{
		this(mainColor.getColor(), trimColor.getColor());
	}
	
	public TeamImages(Color mainColor, Color trimColor)
	{
		gearColoredImages = new Texture[Equipment.EQUIP_TYPE_COUNT];
		loadRaceProfileTemplates();
		loadRaceSpriteTemplates();
//		loadGearTemplates();

		updateColors(mainColor, trimColor);
	}
	
	public ColorPairKey getColorPairKey()
	{
		return colorPairKey;
	}

	private static void loadRaceProfileTemplates()
	{
		if (raceProfileTemplates != null)
			return;
		
		raceProfileTemplates = new HashMap<Race, Texture>();
		
		for (Race race : Race.values())
		{
			String imageName = "PROFILE_" + race.name();
			raceProfileTemplates.put(race, imageFactory.getTexture(ImageType.valueOf(imageName)));
		}
	}

	private static void loadRaceSpriteTemplates()
	{
		if (raceSpriteTemplates != null)
			return;
		
		raceSpriteTemplates = new HashMap<Race, Texture>();
		
		for (Race race : Race.values())
		{
			String imageName = "SPRITES_" + race.name();
			raceSpriteTemplates.put(race, imageFactory.getTexture(ImageType.valueOf(imageName)));
		}
	}

	private void loadGearTemplates()
	{
		if (gearTemplates != null)
			return;
		
		gearTemplates = new Texture[Equipment.EQUIP_TYPE_COUNT];

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
			gearTemplates[i] = imageFactory.getTexture(gearTypes[i]);
		}
	}

	private void updateColors(Color mainColor, Color trimColor)
	{
		raceColoredProfiles.clear();
		raceColoredSprites.clear();
		
		Color transparentBg = new Color(0, 0, 0, 0);
		
		for (Race race : Race.values())
		{
			raceColoredProfiles.put(race, colorReplacer.setColors(raceProfileTemplates.get(race), mainColor, trimColor, transparentBg));
			raceColoredSprites.put(race, colorReplacer.setColors(raceSpriteTemplates.get(race), mainColor, trimColor, transparentBg));
		}
		
		for (int i = 0; i < Equipment.EQUIP_TYPE_COUNT; i++)
		{
//			gearColoredImages[i] = colorReplacer.setColors(gearTemplates[i], mainColor, trimColor, transparentBg);
		}
		
//		helmetColoredImage = colorReplacer.setColors(imageFactory.getTexture(ImageType.EDITOR_HELMET), mainColor, trimColor, transparentBg);
	}

	public Texture getPlayerImage(Race race)
	{
		return raceColoredProfiles.get(race);
	}
	
	public Texture getSpriteSheet(Race race)
	{
		return raceColoredSprites.get(race);
	}

	public Texture getEquipmentImage(int equipment)
	{
		return gearColoredImages[equipment];
	}
	
	public Texture getHelmetImage()
	{
		return helmetColoredImage;
	}
	
	public void dispose()
	{
		for (Race race : Race.values())
		{
			Texture profile = raceColoredProfiles.get(race);
			Texture sprite = raceColoredSprites.get(race);
			
			if (profile != null)
				profile.dispose();
			
			if (sprite != null)
				sprite.dispose();
			
			raceColoredProfiles.remove(race);
			raceColoredSprites.remove(race);
		}
		
		for (int i = 0; i < Equipment.EQUIP_TYPE_COUNT; i++)
		{
//			Texture gear = gearColoredImages[i];
//			gear.dispose();
			gearColoredImages = null;
		}
		
		if (helmetColoredImage != null)
			helmetColoredImage.dispose();
	}
}
