package main.presentation;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

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
	
	private Map<Race, Texture> raceColoredProfiles = new HashMap<Race, Texture>();
	private Map<Race, Texture> raceColoredSprites = new HashMap<Race, Texture>();

	private static Texture gearColoredTemplate = null;
	private Drawable[] gearColoredImages = new Drawable[Equipment.EQUIP_TYPE_COUNT];
	
	private Texture smallTeamBanner = null;
	private Texture largeTeamBanner = null;
	private Texture helmetColoredImage = null;
	private Texture darkGoalTiles = null;
	private Texture litGoalTiles = null;

	private Drawable drawableSmallTeamBanner = null;
	private Drawable drawableLargeTeamBanner = null;
	
	private static ImageFactory imageFactory = ImageFactory.getInstance();
	private static AbstractColorReplacer colorReplacer = ColorReplacer.getInstance();

	public TeamImages(TeamColorType mainColor, TeamColorType trimColor)
	{
		this(mainColor.getColor(), trimColor.getColor());
	}
	
	public TeamImages(Color mainColor, Color trimColor)
	{
		loadRaceProfileTemplates();
		loadRaceSpriteTemplates();

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

	private void updateColors(Color mainColor, Color trimColor)
	{
		dispose();
		
		Color transparentBg = new Color(0, 0, 0, 0);
		
		for (Race race : Race.values())
		{
			raceColoredProfiles.put(race, colorReplacer.setColors(raceProfileTemplates.get(race), mainColor, trimColor, transparentBg));
			raceColoredSprites.put(race, colorReplacer.setColors(raceSpriteTemplates.get(race), mainColor, trimColor, transparentBg));
		}
		
		gearColoredTemplate = colorReplacer.setColors(imageFactory.getTexture(ImageType.GEAR_ALLGEAR), mainColor, trimColor, transparentBg);
		loadGearImages();
		
		smallTeamBanner = colorReplacer.setColors(imageFactory.getTexture(ImageType.GAME_OVERLAY_TEAM1BANNER), mainColor, trimColor, transparentBg);
		largeTeamBanner = colorReplacer.setColors(imageFactory.getTexture(ImageType.GAME_OVERLAY_CURRENTTEAMBANNER), mainColor, trimColor, transparentBg);
		drawableSmallTeamBanner = new TextureRegionDrawable(smallTeamBanner);
		drawableLargeTeamBanner = new TextureRegionDrawable(largeTeamBanner);
		
		darkGoalTiles = colorReplacer.setColors(imageFactory.getTexture(ImageType.DARK_GOAL_TILES), mainColor, trimColor, transparentBg);
		litGoalTiles = colorReplacer.setColors(imageFactory.getTexture(ImageType.LIT_GOAL_TILES), mainColor, trimColor, transparentBg);
		
//		helmetColoredImage = colorReplacer.setColors(imageFactory.getTexture(ImageType.EDITOR_HELMET), mainColor, trimColor, transparentBg);
	}

	private void loadGearImages()
	{
		gearColoredImages[Equipment.EQUIP_HEAVY_ARMOR] = new TextureRegionDrawable(new TextureRegion(gearColoredTemplate, 0, 0, 100, 50));
		gearColoredImages[Equipment.EQUIP_REINFORCED_ARMOR] = new TextureRegionDrawable(new TextureRegion(gearColoredTemplate, 100, 0, 100, 50));
		gearColoredImages[Equipment.EQUIP_REPULSOR_ARMOR] = new TextureRegionDrawable(new TextureRegion(gearColoredTemplate, 200, 0, 100, 50));
		gearColoredImages[Equipment.EQUIP_SPIKED_ARMOR] = new TextureRegionDrawable(new TextureRegion(gearColoredTemplate, 300, 0, 100, 50));
		gearColoredImages[Equipment.EQUIP_SURGE_ARMOR] = new TextureRegionDrawable(new TextureRegion(gearColoredTemplate, 400, 0, 100, 50));
		gearColoredImages[Equipment.EQUIP_VORTEX_ARMOR] = new TextureRegionDrawable(new TextureRegion(gearColoredTemplate, 500, 0, 100, 50));
		
		gearColoredImages[Equipment.EQUIP_BACKFIRE_BELT] = new TextureRegionDrawable(new TextureRegion(gearColoredTemplate, 0, 53, 48, 24));
		gearColoredImages[Equipment.EQUIP_BOOSTER_BELT] = new TextureRegionDrawable(new TextureRegion(gearColoredTemplate, 48, 53, 48, 24));
		gearColoredImages[Equipment.EQUIP_CLOAKING_BELT] = new TextureRegionDrawable(new TextureRegion(gearColoredTemplate, 96, 53, 48, 24));
		gearColoredImages[Equipment.EQUIP_HOLOGRAM_BELT] = new TextureRegionDrawable(new TextureRegion(gearColoredTemplate, 144, 53, 48, 24));
		gearColoredImages[Equipment.EQUIP_FIELD_INTEGRITY_BELT] = new TextureRegionDrawable(new TextureRegion(gearColoredTemplate, 192, 53, 48, 24));
		gearColoredImages[Equipment.EQUIP_MEDICAL_BELT] = new TextureRegionDrawable(new TextureRegion(gearColoredTemplate, 240, 53, 48, 24));
		gearColoredImages[Equipment.EQUIP_SCRAMBLER_BELT] = new TextureRegionDrawable(new TextureRegion(gearColoredTemplate, 288, 53, 48, 24));

		gearColoredImages[Equipment.EQUIP_SPIKED_BOOTS] = new TextureRegionDrawable(new TextureRegion(gearColoredTemplate, 1, 81, 84, 34));
		gearColoredImages[Equipment.EQUIP_BOUNDER_BOOTS] = new TextureRegionDrawable(new TextureRegion(gearColoredTemplate, 85, 81, 84, 34));
		gearColoredImages[Equipment.EQUIP_INSULATED_BOOTS] = new TextureRegionDrawable(new TextureRegion(gearColoredTemplate, 169, 81, 84, 34));
		gearColoredImages[Equipment.EQUIP_MAGNETIC_BOOTS] = new TextureRegionDrawable(new TextureRegion(gearColoredTemplate, 253, 81, 84, 34));
		gearColoredImages[Equipment.EQUIP_SAAI_BOOTS] = new TextureRegionDrawable(new TextureRegion(gearColoredTemplate, 337, 81, 84, 34));
		
		gearColoredImages[Equipment.EQUIP_MAGNETIC_GLOVES] = new TextureRegionDrawable(new TextureRegion(gearColoredTemplate, 1, 117, 98, 24));
		gearColoredImages[Equipment.EQUIP_REPULSOR_GLOVES] = new TextureRegionDrawable(new TextureRegion(gearColoredTemplate, 99, 117, 98, 24));
		gearColoredImages[Equipment.EQUIP_SAAI_GLOVES] = new TextureRegionDrawable(new TextureRegion(gearColoredTemplate, 197, 117, 98, 24));
		gearColoredImages[Equipment.EQUIP_SPIKED_GLOVES] = new TextureRegionDrawable(new TextureRegion(gearColoredTemplate, 295, 117, 98, 24));
		gearColoredImages[Equipment.EQUIP_SURGE_GLOVES] = new TextureRegionDrawable(new TextureRegion(gearColoredTemplate, 393, 117, 98, 24));
	}

	public Texture getPlayerImage(Race race)
	{
		return raceColoredProfiles.get(race);
	}
	
	public Texture getSpriteSheet(Race race)
	{
		return raceColoredSprites.get(race);
	}

	public Drawable getEquipmentImage(int equipment)
	{
		return gearColoredImages[equipment];
	}
	
	public Drawable getSmallTeamBanner()
	{
		return drawableSmallTeamBanner;
	}
	
	public Drawable getLargeTeamBanner()
	{
		return drawableLargeTeamBanner;
	}
	
	public Texture getDarkGoalTiles()
	{
		return darkGoalTiles;
	}
	
	public Texture getLitGoalTiles()
	{
		return litGoalTiles;
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
			gearColoredImages[i] = null;
		}
		
		if (gearColoredTemplate != null)
			gearColoredTemplate.dispose();
		
		if (smallTeamBanner != null)
			smallTeamBanner.dispose();
		
		if (largeTeamBanner != null)
			largeTeamBanner.dispose();
		
		if (darkGoalTiles != null)
			darkGoalTiles.dispose();
		
		if (litGoalTiles != null)
			litGoalTiles.dispose();
		
		if (helmetColoredImage != null)
			helmetColoredImage.dispose();
	}
}
