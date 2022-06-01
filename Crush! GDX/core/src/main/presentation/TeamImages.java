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
public class TeamImages
{
	private static final Color TRANSPARENT_BG = new Color(0, 0, 0, 0);
	
	private ColorPairKey colorPairKey;
	
	private static Map<Race, Texture> raceProfileTemplates = null;
	private static Map<Race, Texture> raceSpriteTemplates = null;
	
	private Map<Race, Texture> raceColoredProfiles = new HashMap<Race, Texture>();
	private Map<Race, Texture> raceColoredSprites = new HashMap<Race, Texture>();

	private Texture gearColoredTemplate = null;
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
		colorPairKey = new ColorPairKey(mainColor, trimColor);
		
		loadRaceProfileTemplates();
		loadRaceSpriteTemplates();
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
	
	private void updateRaceProfiles()
	{
		disposeProfiles();
		
		for (Race race : Race.values())
		{
			raceColoredProfiles.put(race, colorReplacer.setColors(raceProfileTemplates.get(race), colorPairKey.getForeground(), colorPairKey.getBackground(), TRANSPARENT_BG));
		}
	}
	
	private void updateRaceSprites()
	{
		disposeSprites();
		
		for (Race race : Race.values())
		{
			raceColoredSprites.put(race, colorReplacer.setColors(raceSpriteTemplates.get(race), colorPairKey.getForeground(), colorPairKey.getBackground(), TRANSPARENT_BG));
		}
	}

	private void updateGear()
	{
		disposeGear();
		
		gearColoredTemplate = colorReplacer.setColors(imageFactory.getTexture(ImageType.GEAR_ALLGEAR), colorPairKey.getForeground(), colorPairKey.getBackground(), TRANSPARENT_BG);
		
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
	
	private void updateBanners()
	{
		disposeBanners();
		
		smallTeamBanner = colorReplacer.setColors(imageFactory.getTexture(ImageType.GAME_OVERLAY_TEAM1BANNER), colorPairKey.getForeground(), colorPairKey.getBackground(), TRANSPARENT_BG);
		largeTeamBanner = colorReplacer.setColors(imageFactory.getTexture(ImageType.GAME_OVERLAY_CURRENTTEAMBANNER), colorPairKey.getForeground(), colorPairKey.getBackground(), TRANSPARENT_BG);
		drawableSmallTeamBanner = new TextureRegionDrawable(smallTeamBanner);
		drawableLargeTeamBanner = new TextureRegionDrawable(largeTeamBanner);
	}
	
	private void updateGoalTiles()
	{
		disposeGoalTiles();
		
		darkGoalTiles = colorReplacer.setColors(imageFactory.getTexture(ImageType.DARK_GOAL_TILES), colorPairKey.getForeground(), colorPairKey.getBackground(), TRANSPARENT_BG);
		litGoalTiles = colorReplacer.setColors(imageFactory.getTexture(ImageType.LIT_GOAL_TILES), colorPairKey.getForeground(), colorPairKey.getBackground(), TRANSPARENT_BG);
	}

	private void updateHelmetImage()
	{
		disposeHelmetImage();
		
		helmetColoredImage = colorReplacer.setColors(imageFactory.getTexture(ImageType.EDITOR_HELMET), colorPairKey.getForeground(), colorPairKey.getBackground(), TRANSPARENT_BG);
	}

	public Texture getPlayerImage(Race race)
	{
		if (raceColoredProfiles.isEmpty())
			updateRaceProfiles();
		
		return raceColoredProfiles.get(race);
	}
	
	public Texture getSpriteSheet(Race race)
	{
		if (raceColoredSprites.isEmpty())
			updateRaceSprites();
		
		return raceColoredSprites.get(race);
	}

	public Drawable getEquipmentImage(int equipment)
	{
		if (gearColoredTemplate == null)
			updateGear();
		
		return gearColoredImages[equipment];
	}
	
	public Drawable getSmallTeamBanner()
	{
		if (smallTeamBanner == null)
			updateBanners();
		
		return drawableSmallTeamBanner;
	}
	
	public Drawable getLargeTeamBanner()
	{
		if (largeTeamBanner == null)
			updateBanners();
		
		return drawableLargeTeamBanner;
	}
	
	public Texture getDarkGoalTiles()
	{
		if (darkGoalTiles == null)
			updateGoalTiles();
		
		return darkGoalTiles;
	}
	
	public Texture getLitGoalTiles()
	{
		if (litGoalTiles == null)
			updateGoalTiles();
		
		return litGoalTiles;
	}
	
	public Texture getHelmetImage()
	{
		if (helmetColoredImage == null)
			updateHelmetImage();
		
		return helmetColoredImage;
	}
	
	public void dispose()
	{
		disposeProfiles();
		disposeSprites();
		disposeGear();
		disposeBanners();
		disposeGoalTiles();
		disposeHelmetImage();
	}
	
	private void disposeProfiles()
	{
		for (Race race : Race.values())
		{
			Texture profile = raceColoredProfiles.get(race);
			
			if (profile != null)
				profile.dispose();
		}
		
		raceColoredProfiles.clear();
	}
	
	private void disposeSprites()
	{
		for (Race race : Race.values())
		{
			Texture sprite = raceColoredSprites.get(race);
			
			if (sprite != null)
				sprite.dispose();
		}
		
		raceColoredSprites.clear();
	}
	
	private void disposeGear()
	{
		for (int i = 0; i < Equipment.EQUIP_TYPE_COUNT; i++)
		{
			gearColoredImages[i] = null;
		}
		
		if (gearColoredTemplate != null)
			gearColoredTemplate.dispose();
	}
	
	private void disposeBanners()
	{
		if (smallTeamBanner != null)
			smallTeamBanner.dispose();
		
		if (largeTeamBanner != null)
			largeTeamBanner.dispose();
	}
	
	private void disposeGoalTiles()
	{
		if (darkGoalTiles != null)
			darkGoalTiles.dispose();
		
		if (litGoalTiles != null)
			litGoalTiles.dispose();
	}
	
	private void disposeHelmetImage()
	{
		if (helmetColoredImage != null)
			helmetColoredImage.dispose();
	}
}
