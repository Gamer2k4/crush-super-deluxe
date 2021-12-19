package main.presentation;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class ImageFactory
{
	private static ImageFactory instance = null;
	
	private Map<ImageType, Drawable> images = null;
	private Map<ImageType, Texture> textures = null;
	private Map<ImageType, Pixmap> cursorImages = null;
	
	protected ImageFactory()
	{
		if (images == null)
			loadImages();
	}

	public static ImageFactory getInstance()
	{
		if (instance == null)
			instance = new ImageFactory();

		return instance;
	}
	
	public Drawable getDrawable(ImageType imageType)
	{
		return images.get(imageType);
	}
	
	public Pixmap getPixMap(ImageType imageType)
	{
		return cursorImages.get(imageType);
	}
	
	public Texture getTexture(ImageType imageType)
	{
		return textures.get(imageType);
	}

	private void loadImages()
	{
		images = new HashMap<ImageType, Drawable>();
		textures = new HashMap<ImageType, Texture>();
		cursorImages = new HashMap<ImageType, Pixmap>();
		
		addScreens();
		addArenas();
		addMainButtons();
		addCursorImages();
		addEventBarsAndButtons();
		extractStatusLabels();
		extractButtonBarIndicators();
		addSprites();
		addProfiles();
		addGearImages();
	}

	private void addScreens()
	{
		addImage(ImageType.MAIN_MENU, "screens/main.png");
	}

	private void addArenas()
	{
		addImage(ImageType.MAP_A1, "arenas/A1_bridges.png");
		addImage(ImageType.MAP_A2, "arenas/A2_jackals_lair.png");
		addImage(ImageType.MAP_A3, "arenas/A3_crissick.png");
		addImage(ImageType.MAP_A4, "arenas/A4_whirlwind.png");
		addImage(ImageType.MAP_B1, "arenas/B1_the_void.png");
		addImage(ImageType.MAP_B2, "arenas/B2_observatory.png");
		addImage(ImageType.MAP_B3, "arenas/B3_abyss.png");
		addImage(ImageType.MAP_B4, "arenas/B4_gadel_spyre.png");
		addImage(ImageType.MAP_C1, "arenas/C1_fulcrum.png");
		addImage(ImageType.MAP_C2, "arenas/C2_savanna.png");
		addImage(ImageType.MAP_C3, "arenas/C3_barrow.png");
		addImage(ImageType.MAP_C4, "arenas/C4_maelstrom.png");
		addImage(ImageType.MAP_D1, "arenas/D1_vault.png");
		addImage(ImageType.MAP_D2, "arenas/D2_nexus.png");
		addImage(ImageType.MAP_D3, "arenas/D3_darksun.png");
		addImage(ImageType.MAP_D4, "arenas/D4_badlands.png");
		addImage(ImageType.MAP_E1, "arenas/E1_lightway.png");
		addImage(ImageType.MAP_E2, "arenas/E2_eyes.png");
		addImage(ImageType.MAP_E3, "arenas/E3_darkstar.png");
		addImage(ImageType.MAP_E4, "arenas/E4_spacecom.png");
		
		addImage(ImageType.MAP_LAVA_BG, "arenas/bg_lava.png");
		addImage(ImageType.MAP_STARS_BG, "arenas/bg_stars.png");
	}

	private void addMainButtons()
	{
		Texture menuUpButtons = generateTexture(ImageType.MENU2, "screens/menu2.png");
		Texture menuDownButtons = generateTexture(ImageType.MENU, "screens/menu.png");
		
		images.put(ImageType.MAIN_BUTTON_EXHIBITION_UP, new TextureRegionDrawable(new TextureRegion(menuUpButtons, 1, 1, 158, 14)));
		images.put(ImageType.MAIN_BUTTON_TOURNAMENT_UP, new TextureRegionDrawable(new TextureRegion(menuUpButtons, 1, 21, 158, 14)));
		images.put(ImageType.MAIN_BUTTON_LEAGUE_UP, new TextureRegionDrawable(new TextureRegion(menuUpButtons, 1, 41, 158, 14)));
		images.put(ImageType.MAIN_BUTTON_EXIT_UP, new TextureRegionDrawable(new TextureRegion(menuUpButtons, 1, 61, 158, 14)));
		
		images.put(ImageType.MAIN_BUTTON_EXHIBITION_DOWN, new TextureRegionDrawable(new TextureRegion(menuDownButtons, 1, 1, 158, 14)));
		images.put(ImageType.MAIN_BUTTON_TOURNAMENT_DOWN, new TextureRegionDrawable(new TextureRegion(menuDownButtons, 1, 21, 158, 14)));
		images.put(ImageType.MAIN_BUTTON_LEAGUE_DOWN, new TextureRegionDrawable(new TextureRegion(menuDownButtons, 1, 41, 158, 14)));
		images.put(ImageType.MAIN_BUTTON_EXIT_DOWN, new TextureRegionDrawable(new TextureRegion(menuDownButtons, 1, 61, 158, 14)));
		
	}
	
	private void addCursorImages()
	{
		generateCursorPixmap(ImageType.POINTER_MAIN, "cursors/pointer_main.png");
		generateCursorPixmap(ImageType.POINTER_CRUSH, "cursors/pointer_crush.png");
		generateCursorPixmap(ImageType.POINTER_COMP1, "cursors/pointer_comp1.png");
		generateCursorPixmap(ImageType.POINTER_COMP2, "cursors/pointer_comp2.png");
		generateCursorPixmap(ImageType.POINTER_COMP3, "cursors/pointer_comp3.png");
		generateCursorPixmap(ImageType.POINTER_COMP4, "cursors/pointer_comp4.png");
		generateCursorPixmap(ImageType.POINTER_NET1, "cursors/pointer_net1.png");
		generateCursorPixmap(ImageType.POINTER_NET2, "cursors/pointer_net2.png");
		generateCursorPixmap(ImageType.POINTER_NET3, "cursors/pointer_net3.png");
		generateCursorPixmap(ImageType.POINTER_NET4, "cursors/pointer_net4.png");
		
		generateTexture(ImageType.POINTER_MAIN, "cursors/pointer_main.png");
	}

	private void addEventBarsAndButtons()
	{
		addImage(ImageType.GAME_SIDEBAR, "eventoverlays/sidebar.png");
		Texture allButtons = generateTexture(ImageType.GAME_ALLBUTTONS, "eventoverlays/button_bar.png");
		
		images.put(ImageType.GAME_BUTTONBAR, new TextureRegionDrawable(new TextureRegion(allButtons, 0, 0, 640, 80)));
		images.put(ImageType.GAME_CLICKMAP, new TextureRegionDrawable(new TextureRegion(allButtons, 0, 168, 640, 80)));
		
		images.put(ImageType.DEPRESSED_CHECK_BUTTON, new TextureRegionDrawable(new TextureRegion(allButtons, 185, 89, 60, 36)));
		images.put(ImageType.DEPRESSED_PREV_PLAYER_BUTTON, new TextureRegionDrawable(new TextureRegion(allButtons, 250, 88, 42, 37)));
		images.put(ImageType.DEPRESSED_NEXT_PLAYER_BUTTON, new TextureRegionDrawable(new TextureRegion(allButtons, 298, 88, 42, 37)));
		images.put(ImageType.DEPRESSED_JUMP_BUTTON, new TextureRegionDrawable(new TextureRegion(allButtons, 346, 87, 37, 37)));
		images.put(ImageType.DEPRESSED_MOVE_BUTTON, new TextureRegionDrawable(new TextureRegion(allButtons, 387, 87, 37, 37)));
		images.put(ImageType.DEPRESSED_END_BUTTON, new TextureRegionDrawable(new TextureRegion(allButtons, 429, 87, 47, 40)));
		images.put(ImageType.DEPRESSED_ARH_BUTTON, new TextureRegionDrawable(new TextureRegion(allButtons, 218, 134, 36, 23)));
		images.put(ImageType.DEPRESSED_CST_BUTTON, new TextureRegionDrawable(new TextureRegion(allButtons, 258, 134, 36, 23)));
		images.put(ImageType.DEPRESSED_TIMEOUT_BUTTON, new TextureRegionDrawable(new TextureRegion(allButtons, 305, 131, 27, 28)));
		images.put(ImageType.DEPRESSED_HANDOFF_BUTTON, new TextureRegionDrawable(new TextureRegion(allButtons, 346, 126, 37, 37)));
		images.put(ImageType.DEPRESSED_STATS_BUTTON, new TextureRegionDrawable(new TextureRegion(allButtons, 387, 126, 37, 37)));
		images.put(ImageType.DEPRESSED_HELP_BUTTON, new TextureRegionDrawable(new TextureRegion(allButtons, 431, 132, 44, 28)));
	}

	private void extractStatusLabels()
	{
		Texture allButtons = textures.get(ImageType.GAME_ALLBUTTONS);
		
		images.put(ImageType.GAME_MASK_STUNSTATUS, new TextureRegionDrawable(new TextureRegion(allButtons, 10, 89, 17, 7)));
		images.put(ImageType.GAME_MASK_HURTSTATUS, new TextureRegionDrawable(new TextureRegion(allButtons, 31, 89, 17, 7)));
		images.put(ImageType.GAME_MASK_DEADSTATUS, new TextureRegionDrawable(new TextureRegion(allButtons, 52, 89, 19, 7)));
		images.put(ImageType.GAME_MASK_EGOSTATUS, new TextureRegionDrawable(new TextureRegion(allButtons, 73, 89, 17, 7)));
		images.put(ImageType.GAME_MASK_DECKSTATUS, new TextureRegionDrawable(new TextureRegion(allButtons, 10, 100, 17, 7)));
		images.put(ImageType.GAME_MASK_OUTSTATUS, new TextureRegionDrawable(new TextureRegion(allButtons, 31, 100, 17, 7)));
		images.put(ImageType.GAME_MASK_LATESTATUS, new TextureRegionDrawable(new TextureRegion(allButtons, 52, 100, 17, 7)));
		images.put(ImageType.GAME_MASK_BLOBSTATUS, new TextureRegionDrawable(new TextureRegion(allButtons, 73, 100, 17, 7)));
	}
	

	private void extractButtonBarIndicators()
	{
		Texture allButtons = textures.get(ImageType.GAME_ALLBUTTONS);
		
		images.put(ImageType.GAME_OVERLAY_SELECTEDPLAYER, new TextureRegionDrawable(new TextureRegion(allButtons, 71, 137, 12, 4)));
		images.put(ImageType.GAME_OVERLAY_CURRENTTEAM, new TextureRegionDrawable(new TextureRegion(allButtons, 131, 134, 4, 7)));
		images.put(ImageType.GAME_OVERLAY_TEAM1BANNER, new TextureRegionDrawable(new TextureRegion(allButtons, 138, 133, 8, 8)));
		images.put(ImageType.GAME_OVERLAY_TEAM2BANNER, new TextureRegionDrawable(new TextureRegion(allButtons, 138, 143, 8, 8)));
		images.put(ImageType.GAME_OVERLAY_TEAM3BANNER, new TextureRegionDrawable(new TextureRegion(allButtons, 138, 153, 8, 8)));
		images.put(ImageType.GAME_OVERLAY_CURRENTTEAMBANNER, new TextureRegionDrawable(new TextureRegion(allButtons, 156, 133, 55, 28)));
		images.put(ImageType.GAME_OVERLAY_PADSLEFT, new TextureRegionDrawable(new TextureRegion(allButtons, 133, 113, 23, 9)));
	}

	private void addSprites()
	{
		addImage(ImageType.CRUSH_TILES, "sprites/tile_crush.png");
		addImage(ImageType.SPRITES_CURMIAN, "sprites/sprite_curmian.png");
		addImage(ImageType.SPRITES_DRAGORAN, "sprites/sprite_reptoid.png");
		addImage(ImageType.SPRITES_GRONK, "sprites/sprite_gronk.png");
		addImage(ImageType.SPRITES_HUMAN, "sprites/sprite_human.png");
		addImage(ImageType.SPRITES_KURGAN, "sprites/sprite_beavian.png");
		addImage(ImageType.SPRITES_NYNAX, "sprites/sprite_antman.png");
		addImage(ImageType.SPRITES_SLITH, "sprites/sprite_slith.png");
		addImage(ImageType.SPRITES_XJS9000, "sprites/sprite_robot.png");
	}

	private void addProfiles()
	{
		addImage(ImageType.PROFILE_CURMIAN, "profiles/curmian.png");
		addImage(ImageType.PROFILE_DRAGORAN, "profiles/dragoran.png");
		addImage(ImageType.PROFILE_GRONK, "profiles/gronk.png");
		addImage(ImageType.PROFILE_HUMAN, "profiles/human.png");
		addImage(ImageType.PROFILE_KURGAN, "profiles/kurgan.png");
		addImage(ImageType.PROFILE_NYNAX, "profiles/nynax.png");
		addImage(ImageType.PROFILE_SLITH, "profiles/slith.png");
		addImage(ImageType.PROFILE_XJS9000, "profiles/xjs9000.png");
	}

	private void addGearImages()
	{
		addImage(ImageType.GEAR_ALLGEAR, "general/gear.png");
		Texture allGear = textures.get(ImageType.GEAR_ALLGEAR);
		
		//TODO: pulling these out might be done in TeamImages instead of here
//		images.put(ImageType.GEAR_HEAVY_PADS, new TextureRegionDrawable(new TextureRegion(allGear, 0, 0, 100, 50)));
//		images.put(ImageType.GEAR_REINFORCED_PADS, new TextureRegionDrawable(new TextureRegion(allGear, 100, 0, 100, 50)));
//		images.put(ImageType.GEAR_REPULSOR_PADS, new TextureRegionDrawable(new TextureRegion(allGear, 200, 0, 100, 50)));
//		images.put(ImageType.GEAR_SPIKED_PADS, new TextureRegionDrawable(new TextureRegion(allGear, 300, 0, 100, 50)));
//		images.put(ImageType.GEAR_SURGE_PADS, new TextureRegionDrawable(new TextureRegion(allGear, 400, 0, 100, 50)));
//		images.put(ImageType.GEAR_VORTEX_PADS, new TextureRegionDrawable(new TextureRegion(allGear, 500, 0, 100, 50)));
//		
//		images.put(ImageType.GEAR_BACKFIRE_BELT, new TextureRegionDrawable(new TextureRegion(allGear, 0, 53, 48, 24)));
//		images.put(ImageType.GEAR_BOOSTER_BELT, new TextureRegionDrawable(new TextureRegion(allGear, 48, 53, 48, 24)));
//		images.put(ImageType.GEAR_CLOAKING_BELT, new TextureRegionDrawable(new TextureRegion(allGear, 96, 53, 48, 24)));
//		images.put(ImageType.GEAR_HOLOGRAM_BELT, new TextureRegionDrawable(new TextureRegion(allGear, 144, 53, 48, 24)));
//		images.put(ImageType.GEAR_INTEGRITY_BELT, new TextureRegionDrawable(new TextureRegion(allGear, 192, 53, 48, 24)));
//		images.put(ImageType.GEAR_MEDICAL_BELT, new TextureRegionDrawable(new TextureRegion(allGear, 240, 53, 48, 24)));
//		images.put(ImageType.GEAR_SCRAMBLER_BELT, new TextureRegionDrawable(new TextureRegion(allGear, 288, 53, 48, 24)));
//		
//		images.put(ImageType.GEAR_SPIKED_BOOTS, new TextureRegionDrawable(new TextureRegion(allGear, 1, 81, 84, 34)));
//		images.put(ImageType.GEAR_BOUNDER_BOOTS, new TextureRegionDrawable(new TextureRegion(allGear, 85, 81, 84, 34)));
//		images.put(ImageType.GEAR_INSULATED_BOOTS, new TextureRegionDrawable(new TextureRegion(allGear, 169, 81, 84, 34)));
//		images.put(ImageType.GEAR_MAGNETIC_BOOTS, new TextureRegionDrawable(new TextureRegion(allGear, 253, 81, 84, 34)));
//		images.put(ImageType.GEAR_SAAI_BOOTS, new TextureRegionDrawable(new TextureRegion(allGear, 337, 81, 84, 34)));
//		
//		images.put(ImageType.GEAR_MAGNETIC_GLOVES, new TextureRegionDrawable(new TextureRegion(allGear, 1, 117, 98, 24)));
//		images.put(ImageType.GEAR_REPULSOR_GLOVES, new TextureRegionDrawable(new TextureRegion(allGear, 99, 117, 98, 24)));
//		images.put(ImageType.GEAR_SAAI_GLOVES, new TextureRegionDrawable(new TextureRegion(allGear, 197, 117, 98, 24)));
//		images.put(ImageType.GEAR_SPIKED_GLOVES, new TextureRegionDrawable(new TextureRegion(allGear, 295, 117, 98, 24)));
//		images.put(ImageType.GEAR_SURGE_GLOVES, new TextureRegionDrawable(new TextureRegion(allGear, 393, 117, 98, 24)));
	}

	private void generateCursorPixmap(ImageType imageType, String path)
	{
		Pixmap originalImage = new Pixmap(Gdx.files.internal(path));
		Pixmap cursorPixmap = new Pixmap(32, 32, originalImage.getFormat());
		cursorPixmap.drawPixmap(originalImage, 0, 0);
		cursorImages.put(imageType, cursorPixmap);
		originalImage.dispose();
	}

	private void addImage(ImageType imageType, String path)
	{
		Texture texture = generateTexture(imageType, path);
		images.put(imageType, new TextureRegionDrawable(texture));
	}
	
	private Texture generateTexture(ImageType imageType, String path)
	{
		Texture texture = new Texture(Gdx.files.internal(path));
		textures.put(imageType, texture);
		return texture;
	}
	
	public void dispose()
	{
		for (ImageType imageType : ImageType.values())
		{
			Texture texture = textures.get(imageType);
			
			if (texture != null)
				texture.dispose();
		}
		
		textures.clear();
		
		for (ImageType imageType : cursorImages.keySet())
		{
			Pixmap cursor = cursorImages.get(imageType);
			
			if (cursor != null)
				cursor.dispose();
		}
		
		cursorImages.clear();
	}
}
