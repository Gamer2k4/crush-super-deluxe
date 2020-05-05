package main.presentation.common.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

import main.data.load.ByteFileReader;
import main.presentation.common.GameSettings;
import main.presentation.common.Logger;
import main.presentation.legacy.common.LegacyUiConstants;

public class LegacyImageFactory extends AbstractImageFactory
{
	private static Map<ImageType, Dimension> imageSizes = null;
	private static Map<ImageType, BufferedImage> loadedImages = null;
	private static ColorMap colorMap;

	private static final int OFFSET_PNT = 0;
	private static final int OFFSET_DVE = 768;
	private static final int OFFSET_MAP = 2874;
	private static final int OFFSET_FNT = 0;
	private static final int OFFSET_SPT = 0;
	private static final int OFFSET_TIL = 1084;

	private static final int FONT_CHARS = 56;
	private static final int SPRITE_COUNT = 192;
	private static final int SPRITE_COLUMNS = 1;	//was 4
	private static final int TILE_COUNT = 192;

	private static final int MAP_WIDTH = 1152;
	private static final int MAP_HEIGHT = 960;

	private static final int SPRITE_WIDTH = 35;
	private static final int SPRITE_HEIGHT = 30;

	private static final int TILE_WIDTH = 36;
	private static final int TILE_HEIGHT = 30;

	private static LegacyImageFactory instance = null;

	protected LegacyImageFactory()
	{
		if (imageSizes == null && loadedImages == null && colorMap == null)
			loadImages();
	}

	public static LegacyImageFactory getInstance()
	{
		if (instance == null)
			instance = new LegacyImageFactory();

		return instance;
	}

	@Override
	public BufferedImage getImage(ImageType type)
	{
		return loadedImages.get(type);
	}

	@Override
	public BufferedImage copyImage(ImageType type)
	{
		return ImageUtils.deepCopy(loadedImages.get(type));
	}

	@Override
	public Dimension getImageSize(ImageType type)
	{
		if (imageSizes.get(type) != null)
			return imageSizes.get(type);

		Dimension dimension = null;

		String typeString = getTypeString(type);
		BufferedImage image = loadedImages.get(type);

		if (image != null)
			dimension = new Dimension(image.getWidth(), image.getHeight());
		else
			throw new UnsupportedOperationException("Cannot get dimensions for image of type " + typeString
					+ "; image must be loaded first.");

		imageSizes.put(type, dimension); // since we got here, the dimension exists but isn't in the map for this particular type yet

		return dimension;
	}

	@Override
	protected String getBaseDirectory()
	{
		return GameSettings.getRootDirectory() + "\\DATA\\";
	}

	private void loadImages()
	{
		Logger.logMessage(" Loading legacy image data...", Logger.OUTPUT, false);
		
		createCacheDirectory();

		imageSizes = new HashMap<ImageType, Dimension>();
		loadedImages = new HashMap<ImageType, BufferedImage>();
		colorMap = new InGameColorMap();

		loadImage(ImageType.PROFILE_CURMIAN_S, "curmian.png", "CURMIAN.DVE");
		loadImage(ImageType.PROFILE_DRAGORAN_S, "dragoran.png", "ZIZANI.DVE");
		loadImage(ImageType.PROFILE_GRONK_S, "gronk.png", "GRONK.DVE");
		loadImage(ImageType.PROFILE_HUMAN_S, "human.png", "HUMAN.DVE");
		loadImage(ImageType.PROFILE_KURGAN_S, "kurgan.png", "ROTHGAR.DVE");
		loadImage(ImageType.PROFILE_NYNAX_S, "nynax.png", "NYNAX.DVE");
		loadImage(ImageType.PROFILE_SLITH_S, "slith.png", "SLITH.DVE");
		loadImage(ImageType.PROFILE_XJS9000_S, "xjs9000.png", "ROBOT.DVE");
		upscaleAndTrimRaceImages();

		loadImage(ImageType.GEAR_ALLGEAR, "gear.png", "EQUIP.DVE");
		extractGearImages();

		loadImage(ImageType.EJECT_INJURY, "ejecinj.png", "DOCTOR.DVE");
		loadImage(ImageType.EJECT_KILL, "ejeckill.png", "DEATH.DVE");
		loadImage(ImageType.EJECT_BLOB, "ejecblob.png", "MUTATION.DVE");
		loadImage(ImageType.EJECT_REF, "ejecref.png", "REF.DVE");
		loadedImages.put(ImageType.EDITOR_DOCBOT, ImageUtils.scaleImage(loadedImages.get(ImageType.EJECT_INJURY), 2));

		loadImage(ImageType.GAME_SIDEBAR, "sidebar.png", "SIDEBAR.DVE");
		loadImage(ImageType.GAME_ALLBUTTONS, "btnbar.png", "BTNBAR2.DVE");
		extractButtonImages();
		extractStatusLabels();
		extractButtonBarIndicators();
		extractButtonOverlays();
		extractButtonMap();

		loadImage(ImageType.BG_BG1, "bg1.png", "BG1.DVE");
		loadImage(ImageType.BG_BG2, "bg2.png", "BG2.DVE");
		loadImage(ImageType.BG_BG3, "bg3.png", "BG3.DVE");
		loadImage(ImageType.BG_BG4, "bg4.png", "BG4.DVE");
		loadImage(ImageType.BG_BG5, "bg5.png", "BG5.DVE");
		loadImage(ImageType.BG_BG6, "bg6.png", "BG6.DVE");
		loadImage(ImageType.BG_BG7, "bg7.png", "BG7.DVE");
		loadImage(ImageType.BG_BG8, "bg8.png", "BG8.DVE");
		correctBackgroundImages();
		
		loadImage(ImageType.SCREEN_TEAM_EDITOR_START, "rostbar.png", "ROSTBAR.DVE");
		loadImage(ImageType.SCREEN_TEAM_EDITOR_SETTINGS, "settings.png", "SETTINGS.DVE");
		loadImage(ImageType.SCREEN_TEAM_EDITOR_ACQUIRE, "acquire.png", "ACQUIRE.DVE");
		loadImage(ImageType.SCREEN_TEAM_EDITOR_OUTFIT, "outfit.png", "OUTFIT.DVE");
		loadImage(ImageType.SCREEN_TEAM_EDITOR_DRAFT, "stock.png", "STOCK.DVE");
		loadImage(ImageType.SCREEN_TEAM_EDITOR_DOCBOT, "docbot.png", "DOCBOT.DVE");
		loadImage(ImageType.SCREEN_TEAM_EDITOR_POWER, "power.png", "POWER.DVE");
		loadImage(ImageType.SCREEN_TEAM_EDITOR_AGILITY, "agility.png", "AGILITY.DVE");
		loadImage(ImageType.SCREEN_TEAM_EDITOR_PSYCHE, "psyche.png", "PSYCHE.DVE");
		fillTeamColorDiamonds();
		// also load screens POPUP2.DVE, possibly LEDITT.DVE, all out-of-game stats screens
		
		loadImage(ImageType.SCREEN_TEAM_EDITOR_ROSTER_GENERAL, "rosterg.png", "ROSTERG.DVE");
		loadImage(ImageType.SCREEN_TEAM_EDITOR_ROSTER_DETAILED, "rosterd.png", "ROSTERD.DVE");
		extractHelmetImage();

		loadImage(ImageType.SCREEN_STATS_CARNAGE, "stats1.png", "STATS1.DVE");
		loadImage(ImageType.SCREEN_STATS_CHK_CHECK, "stats2.png", "STATS2.DVE");
		loadImage(ImageType.SCREEN_STATS_RUSHING, "stats3.png", "STATS3.DVE");
		loadImage(ImageType.SCREEN_STATS_CHK_SACK, "stats5.png", "STATS5.DVE");
		loadImage(ImageType.SCREEN_STATS_OVERVIEW, "stats6.png", "STATS6.DVE");
		loadImage(ImageType.SCREEN_STATS_MVP, "stats7.png", "STATS7.DVE");
		extractButtonHighlights();

		Logger.output("Done!");

		loadTiles();
		loadSprites();
		loadMaps();
		loadFonts();
	}

	private void createCacheDirectory()
	{
		File directory = new File(getBaseDirectory() + "csdcache");
		directory.mkdirs();
	}

	private void loadTiles()
	{
		Logger.logMessage(" Loading legacy tile data...", Logger.OUTPUT, false);

		loadImage(ImageType.CRUSH_TILES, "tiles.png", "CRUSH.TIL");

		Logger.output("Done!");
	}

	private void loadSprites()
	{
		Logger.logMessage(" Loading legacy sprite data...", Logger.OUTPUT, false);

		loadImage(ImageType.SPRITES_CURMIAN, "scurmian.png", "CURMIAN.SPT");
		loadImage(ImageType.SPRITES_DRAGORAN, "sdragon.png", "REPTOID.SPT");
		loadImage(ImageType.SPRITES_GRONK, "sgronk.png", "GRONK.SPT");
		loadImage(ImageType.SPRITES_HUMAN, "shuman.png", "HUMAN.SPT");
		loadImage(ImageType.SPRITES_KURGAN, "skurgan.png", "BEAVIAN.SPT");
		loadImage(ImageType.SPRITES_NYNAX, "snynax.png", "ANTMAN.SPT");
		loadImage(ImageType.SPRITES_SLITH, "sslith.png", "SLITH.SPT");
		loadImage(ImageType.SPRITES_XJS9000, "sxjs9000.png", "ROBOT.SPT");

		Logger.output("Done!");
	}

	private void loadMaps()
	{
		Logger.output(" Loading legacy map images...");

		loadImage(ImageType.MAP_LAVA_BG, "lava3.png", "LAVA3.DVE");
		
		String path = GameSettings.getRootDirectory() + "\\DATA\\MAPLIST.DAT";
		int arenaLetter = 1;
		int arenaNumber = 1;
		
		BufferedReader in;
		Scanner s;
		
		try
		{
			in = new BufferedReader(new FileReader(path));
			s = new Scanner(in);

			while (s.hasNextLine())
			{
				String arenaFile = s.nextLine().substring(9);
				String cacheFile = arenaFile.substring(0, arenaFile.length() - 4) + ".png";
				String enumTypeName = "MAP_" + (char)(arenaLetter + 64) + String.valueOf(arenaNumber);
				Logger.output("  " + enumTypeName);
				loadImage(ImageType.valueOf(enumTypeName), cacheFile, arenaFile);
				
				arenaNumber++;
				
				if (arenaNumber > 4)
				{
					arenaNumber = 1;
					arenaLetter++;
				}
			}

			in.close();
			s.close();
		} catch (IOException e)
		{
			System.out.println("LegacyImageFactory - Could not read file " + path);
		}

		Logger.output("Done!\n");
	}

	private void loadFonts()
	{
		Logger.logMessage(" Loading legacy font data...", Logger.OUTPUT, false);

		loadImage(ImageType.FONT_BIG, "fbig.png", "BIG.FNT");
		loadImage(ImageType.FONT_HUGE, "fhuge.png", "HUGE.FNT");
		loadImage(ImageType.FONT_SMALL, "fsmall.png", "SMALL.FNT");
		loadImage(ImageType.FONT_SMALL2, "fsmall2.png", "SMALL2.FNT");

		Logger.output("Done!");
	}

	private void upscaleAndTrimRaceImages()
	{
		loadedImages.put(ImageType.PROFILE_CURMIAN, resizeRaceImage(loadedImages.get(ImageType.PROFILE_CURMIAN_S)));
		loadedImages.put(ImageType.PROFILE_DRAGORAN, resizeRaceImage(loadedImages.get(ImageType.PROFILE_DRAGORAN_S)));
		loadedImages.put(ImageType.PROFILE_GRONK, resizeRaceImage(loadedImages.get(ImageType.PROFILE_GRONK_S)));
		loadedImages.put(ImageType.PROFILE_HUMAN, resizeRaceImage(loadedImages.get(ImageType.PROFILE_HUMAN_S)));
		loadedImages.put(ImageType.PROFILE_KURGAN, resizeRaceImage(loadedImages.get(ImageType.PROFILE_KURGAN_S)));
		loadedImages.put(ImageType.PROFILE_NYNAX, resizeRaceImage(loadedImages.get(ImageType.PROFILE_NYNAX_S)));
		loadedImages.put(ImageType.PROFILE_SLITH, resizeRaceImage(loadedImages.get(ImageType.PROFILE_SLITH_S)));
		loadedImages.put(ImageType.PROFILE_XJS9000, resizeRaceImage(loadedImages.get(ImageType.PROFILE_XJS9000_S)));
	}

	private BufferedImage resizeRaceImage(BufferedImage originalImage)
	{
		if (originalImage.getHeight() != 75 && originalImage.getWidth() != 90)
			throw new IllegalArgumentException("Image passed into resizeRaceImage method has dimensions of " + originalImage.getHeight()
					+ "x" + originalImage.getWidth());

		return ImageUtils.padImage(ImageUtils.scaleImage(originalImage, 2), new Dimension(180, 160)).getSubimage(10, 0, 160, 160);
	}

	private void extractGearImages()
	{
		BufferedImage allGear = loadedImages.get(ImageType.GEAR_ALLGEAR);

		loadedImages.put(ImageType.GEAR_HEAVY_PADS, upscaleEquipmentImage(allGear.getSubimage(0, 0, 100, 50)));
		loadedImages.put(ImageType.GEAR_REINFORCED_PADS, upscaleEquipmentImage(allGear.getSubimage(100, 0, 100, 50)));
		loadedImages.put(ImageType.GEAR_REPULSOR_PADS, upscaleEquipmentImage(allGear.getSubimage(200, 0, 100, 50)));
		loadedImages.put(ImageType.GEAR_SPIKED_PADS, upscaleEquipmentImage(allGear.getSubimage(300, 0, 100, 50)));
		loadedImages.put(ImageType.GEAR_SURGE_PADS, upscaleEquipmentImage(allGear.getSubimage(400, 0, 100, 50)));
		loadedImages.put(ImageType.GEAR_VORTEX_PADS, upscaleEquipmentImage(allGear.getSubimage(500, 0, 100, 50)));

		loadedImages.put(ImageType.GEAR_BACKFIRE_BELT, upscaleEquipmentImage(allGear.getSubimage(0, 53, 48, 24)));
		loadedImages.put(ImageType.GEAR_BOOSTER_BELT, upscaleEquipmentImage(allGear.getSubimage(48, 53, 48, 24)));
		loadedImages.put(ImageType.GEAR_CLOAKING_BELT, upscaleEquipmentImage(allGear.getSubimage(96, 53, 48, 24)));
		loadedImages.put(ImageType.GEAR_HOLOGRAM_BELT, upscaleEquipmentImage(allGear.getSubimage(144, 53, 48, 24)));
		loadedImages.put(ImageType.GEAR_INTEGRITY_BELT, upscaleEquipmentImage(allGear.getSubimage(192, 53, 48, 24)));
		loadedImages.put(ImageType.GEAR_MEDICAL_BELT, upscaleEquipmentImage(allGear.getSubimage(240, 53, 48, 24)));
		loadedImages.put(ImageType.GEAR_SCRAMBLER_BELT, upscaleEquipmentImage(allGear.getSubimage(288, 53, 48, 24)));

		loadedImages.put(ImageType.GEAR_SPIKED_BOOTS, upscaleEquipmentImage(allGear.getSubimage(1, 81, 84, 34)));
		loadedImages.put(ImageType.GEAR_BOUNDER_BOOTS, upscaleEquipmentImage(allGear.getSubimage(85, 81, 84, 34)));
		loadedImages.put(ImageType.GEAR_INSULATED_BOOTS, upscaleEquipmentImage(allGear.getSubimage(169, 81, 84, 34)));
		loadedImages.put(ImageType.GEAR_MAGNETIC_BOOTS, upscaleEquipmentImage(allGear.getSubimage(253, 81, 84, 34)));
		loadedImages.put(ImageType.GEAR_SAAI_BOOTS, upscaleEquipmentImage(allGear.getSubimage(337, 81, 84, 34)));

		loadedImages.put(ImageType.GEAR_MAGNETIC_GLOVES, upscaleEquipmentImage(allGear.getSubimage(1, 117, 98, 24)));
		loadedImages.put(ImageType.GEAR_REPULSOR_GLOVES, upscaleEquipmentImage(allGear.getSubimage(99, 117, 98, 24)));
		loadedImages.put(ImageType.GEAR_SAAI_GLOVES, upscaleEquipmentImage(allGear.getSubimage(197, 117, 98, 24)));
		loadedImages.put(ImageType.GEAR_SPIKED_GLOVES, upscaleEquipmentImage(allGear.getSubimage(295, 117, 98, 24)));
		loadedImages.put(ImageType.GEAR_SURGE_GLOVES, upscaleEquipmentImage(allGear.getSubimage(393, 117, 98, 24)));
	}

	private BufferedImage upscaleEquipmentImage(BufferedImage image)
	{
		return ImageUtils.padImage(ImageUtils.scaleImage(image, 2), new Dimension(212, 112));
	}

	private void fillTeamColorDiamonds()
	{
		BufferedImage diamonds = ImageUtils.deepCopy(loadedImages.get(ImageType.SCREEN_TEAM_EDITOR_SETTINGS).getSubimage(321, 157, 15, 41));
		
		for (int i = 0; i < diamonds.getWidth(); i++)
		{
			for (int j = 0; j < diamonds.getHeight(); j++)
			{
				Color pixelColor = new Color(diamonds.getRGB(i, j), false);
				
				if (ImageUtils.rgbEquals(pixelColor, Color.BLACK) && j < 16)
					diamonds.setRGB(i, j, colorMap.getColor(79).getRGB());
				else if (ImageUtils.rgbEquals(pixelColor, Color.BLACK) && j > 24)
					diamonds.setRGB(i, j, colorMap.getColor(15).getRGB());
				else
					diamonds.setRGB(i, j, LegacyUiConstants.COLOR_LEGACY_TRANSPARENT.getRGB());
			}
		}
		
		loadedImages.put(ImageType.TEAM_COLOR_DIAMONDS, diamonds);
	}

	private void extractHelmetImage()
	{
		BufferedImage smallHelmet = ImageUtils.deepCopy(loadedImages.get(ImageType.SCREEN_TEAM_EDITOR_ROSTER_DETAILED).getSubimage(7, 9, 36, 36));

		for (int i = 0; i < 36; i++)
		{
			for (int j = 0; j < 36; j++)
			{
				Color pixelColor = new Color(smallHelmet.getRGB(i, j));

				if (ImageUtils.rgbEquals(pixelColor, new Color(103, 120, 143)))
				{
					pixelColor = new Color(0, 0, 0, 0);
				}

				smallHelmet.setRGB(i, j, pixelColor.getRGB());
			}
		}
		
		loadedImages.put(ImageType.EDITOR_HELMET_S, smallHelmet);
		loadedImages.put(ImageType.EDITOR_HELMET, ImageUtils.scaleImage(smallHelmet, 2));
	}

	private void extractButtonImages()
	{
		BufferedImage allButtons = loadedImages.get(ImageType.GAME_ALLBUTTONS);

		loadedImages.put(ImageType.GAME_BUTTONBAR, allButtons.getSubimage(0, 0, 640, 80));
	}

	private void extractStatusLabels()
	{
		BufferedImage allButtons = loadedImages.get(ImageType.GAME_ALLBUTTONS);
		BufferedImage stunStatusMask = ImageUtils.deepCopy(allButtons.getSubimage(10, 89, 17, 7));
		BufferedImage hurtStatusMask = ImageUtils.deepCopy(allButtons.getSubimage(31, 89, 17, 7));
		BufferedImage deadStatusMask = ImageUtils.deepCopy(allButtons.getSubimage(52, 89, 19, 7));
		BufferedImage egoStatusMask = ImageUtils.deepCopy(allButtons.getSubimage(73, 89, 17, 7));
		BufferedImage deckStatusMask = ImageUtils.deepCopy(allButtons.getSubimage(10, 100, 17, 7));
		BufferedImage outStatusMask = ImageUtils.deepCopy(allButtons.getSubimage(31, 100, 17, 7));
		BufferedImage lateStatusMask = ImageUtils.deepCopy(allButtons.getSubimage(52, 100, 17, 7));
		BufferedImage blobStatusMask = ImageUtils.deepCopy(allButtons.getSubimage(73, 100, 17, 7));
		
		loadedImages.put(ImageType.GAME_MASK_STUNSTATUS, stunStatusMask);
		loadedImages.put(ImageType.GAME_MASK_HURTSTATUS, hurtStatusMask);
		loadedImages.put(ImageType.GAME_MASK_DEADSTATUS, deadStatusMask);
		loadedImages.put(ImageType.GAME_MASK_EGOSTATUS, egoStatusMask);
		loadedImages.put(ImageType.GAME_MASK_DECKSTATUS, deckStatusMask);
		loadedImages.put(ImageType.GAME_MASK_OUTSTATUS, outStatusMask);
		loadedImages.put(ImageType.GAME_MASK_LATESTATUS, lateStatusMask);
		loadedImages.put(ImageType.GAME_MASK_BLOBSTATUS, blobStatusMask);
	}

	private void extractButtonBarIndicators()
	{
		BufferedImage allButtons = loadedImages.get(ImageType.GAME_ALLBUTTONS);
		BufferedImage selectedPlayerIndicator = ImageUtils.deepCopy(allButtons.getSubimage(71, 137, 12, 4));
		BufferedImage currentTeamIndicator = ImageUtils.deepCopy(allButtons.getSubimage(131, 134, 4, 7));
		BufferedImage teamOneBanner = ImageUtils.deepCopy(allButtons.getSubimage(138, 133, 8, 8));
		BufferedImage teamTwoBanner = ImageUtils.deepCopy(allButtons.getSubimage(138, 143, 8, 8));
		BufferedImage teamThreeBanner = ImageUtils.deepCopy(allButtons.getSubimage(138, 153, 8, 8));
		BufferedImage currentTeamBanner = ImageUtils.deepCopy(allButtons.getSubimage(156, 133, 55, 28));
		BufferedImage padsLeft = ImageUtils.deepCopy(allButtons.getSubimage(133, 113, 23, 9));
		//not loading the status overlap because I'm redrawing the button bar every time

		loadedImages.put(ImageType.GAME_OVERLAY_SELECTEDPLAYER, selectedPlayerIndicator);
		loadedImages.put(ImageType.GAME_OVERLAY_CURRENTTEAM, currentTeamIndicator);
		loadedImages.put(ImageType.GAME_OVERLAY_TEAM1BANNER, teamOneBanner);
		loadedImages.put(ImageType.GAME_OVERLAY_TEAM2BANNER, teamTwoBanner);
		loadedImages.put(ImageType.GAME_OVERLAY_TEAM3BANNER, teamThreeBanner);
		loadedImages.put(ImageType.GAME_OVERLAY_CURRENTTEAMBANNER, currentTeamBanner);
		loadedImages.put(ImageType.GAME_OVERLAY_PADSLEFT, padsLeft);
	}
	
	private void extractButtonOverlays()
	{
		BufferedImage allButtons = loadedImages.get(ImageType.GAME_ALLBUTTONS);
		BufferedImage depressedCheckButton = ImageUtils.deepCopy(allButtons.getSubimage(185, 89, 60, 36));
		BufferedImage depressedPrevPlayerButton = ImageUtils.deepCopy(allButtons.getSubimage(250, 88, 42, 37));
		BufferedImage depressedNextPlayerButton = ImageUtils.deepCopy(allButtons.getSubimage(298, 88, 42, 37));
		BufferedImage depressedJumpButton = ImageUtils.deepCopy(allButtons.getSubimage(346, 87, 37, 37));
		BufferedImage depressedMoveButton = ImageUtils.deepCopy(allButtons.getSubimage(387, 87, 37, 37));
		BufferedImage depressedEndButton = ImageUtils.deepCopy(allButtons.getSubimage(429, 87, 47, 40));
		BufferedImage depressedArhButton = ImageUtils.deepCopy(allButtons.getSubimage(218, 134, 36, 23));
		BufferedImage depressedCstButton = ImageUtils.deepCopy(allButtons.getSubimage(258, 134, 36, 23));
		BufferedImage depressedTimeoutButton = ImageUtils.deepCopy(allButtons.getSubimage(305, 131, 27, 28));
		BufferedImage depressedHandoffButton = ImageUtils.deepCopy(allButtons.getSubimage(346, 126, 37, 37));
		BufferedImage depressedStatsButton = ImageUtils.deepCopy(allButtons.getSubimage(387, 126, 37, 37));
		BufferedImage depressedHelpButton = ImageUtils.deepCopy(allButtons.getSubimage(431, 132, 44, 28));
		
		loadedImages.put(ImageType.DEPRESSED_CHECK_BUTTON, depressedCheckButton);
		loadedImages.put(ImageType.DEPRESSED_PREV_PLAYER_BUTTON, depressedPrevPlayerButton);
		loadedImages.put(ImageType.DEPRESSED_NEXT_PLAYER_BUTTON, depressedNextPlayerButton);
		loadedImages.put(ImageType.DEPRESSED_JUMP_BUTTON, depressedJumpButton);
		loadedImages.put(ImageType.DEPRESSED_MOVE_BUTTON, depressedMoveButton);
		loadedImages.put(ImageType.DEPRESSED_END_BUTTON, depressedEndButton);
		loadedImages.put(ImageType.DEPRESSED_ARH_BUTTON, depressedArhButton);
		loadedImages.put(ImageType.DEPRESSED_CST_BUTTON, depressedCstButton);
		loadedImages.put(ImageType.DEPRESSED_TIMEOUT_BUTTON, depressedTimeoutButton);
		loadedImages.put(ImageType.DEPRESSED_HANDOFF_BUTTON, depressedHandoffButton);
		loadedImages.put(ImageType.DEPRESSED_STATS_BUTTON, depressedStatsButton);
		loadedImages.put(ImageType.DEPRESSED_HELP_BUTTON, depressedHelpButton);
	}

	private void extractButtonMap()
	{
		BufferedImage allButtons = loadedImages.get(ImageType.GAME_ALLBUTTONS);
		BufferedImage buttonMap = ImageUtils.deepCopy(allButtons.getSubimage(0, 168, 640, 80));

		BufferedImage arenaMapClickZone = ImageUtils.createBlankBufferedImage(new Dimension(60, 60), Color.MAGENTA);
		ImageUtils.copySrcIntoDstAt(arenaMapClickZone, buttonMap, LegacyUiConstants.MINIMAP_X_START, LegacyUiConstants.MINIMAP_Y_START);
		
		loadedImages.put(ImageType.GAME_CLICKMAP, buttonMap);
	}
	
	//tinting the images doesn't quite work unless we make the darkest color a true black
	private void correctBackgroundImages()
	{
		loadedImages.put(ImageType.BG_BG1, ImageUtils.replaceColor(loadedImages.get(ImageType.BG_BG1), new Color(24, 31, 32), Color.BLACK));
		loadedImages.put(ImageType.BG_BG2, ImageUtils.replaceColor(loadedImages.get(ImageType.BG_BG2), new Color(24, 31, 32), Color.BLACK));
		loadedImages.put(ImageType.BG_BG3, ImageUtils.replaceColor(loadedImages.get(ImageType.BG_BG3), new Color(24, 31, 32), Color.BLACK));
		loadedImages.put(ImageType.BG_BG4, ImageUtils.replaceColor(loadedImages.get(ImageType.BG_BG4), new Color(24, 31, 32), Color.BLACK));
		loadedImages.put(ImageType.BG_BG5, ImageUtils.replaceColor(loadedImages.get(ImageType.BG_BG5), new Color(24, 31, 32), Color.BLACK));
		loadedImages.put(ImageType.BG_BG6, ImageUtils.replaceColor(loadedImages.get(ImageType.BG_BG6), new Color(24, 31, 32), Color.BLACK));
		loadedImages.put(ImageType.BG_BG7, ImageUtils.replaceColor(loadedImages.get(ImageType.BG_BG7), new Color(24, 31, 32), Color.BLACK));
		loadedImages.put(ImageType.BG_BG8, ImageUtils.replaceColor(loadedImages.get(ImageType.BG_BG8), new Color(24, 31, 32), Color.BLACK));
	}

	private void extractButtonHighlights()
	{
		Color buttonColor = new Color(103, 120, 143);
		Color lightColor = new Color(159, 159, 159);
		Color darkColor = new Color(40, 48, 56);
		
		BufferedImage sourceStatsImage = loadedImages.get(ImageType.SCREEN_STATS_CARNAGE);
		BufferedImage sourceSettingsImage = loadedImages.get(ImageType.SCREEN_TEAM_EDITOR_SETTINGS);
		
		BufferedImage smallButton = sourceStatsImage.getSubimage(586, 339, 35, 17);
		smallButton = ImageUtils.replaceColor(smallButton, buttonColor, LegacyUiConstants.COLOR_LEGACY_TRANSPARENT);
		loadedImages.put(ImageType.BUTTON_SMALL_NORMAL, smallButton);
		loadedImages.put(ImageType.BUTTON_SMALL_CLICKED, swapColors(smallButton, lightColor, darkColor));
		
		BufferedImage largeButton = sourceStatsImage.getSubimage(231, 359, 72, 17);
		largeButton = ImageUtils.replaceColor(largeButton, buttonColor, LegacyUiConstants.COLOR_LEGACY_TRANSPARENT);
		loadedImages.put(ImageType.BUTTON_LARGE_NORMAL, largeButton);
		loadedImages.put(ImageType.BUTTON_LARGE_CLICKED, swapColors(largeButton, lightColor, darkColor));
		
		BufferedImage smallEditorButton = sourceSettingsImage.getSubimage(246, 106, 37, 17);
		smallEditorButton = ImageUtils.replaceColor(smallButton, buttonColor, LegacyUiConstants.COLOR_LEGACY_TRANSPARENT);
		smallEditorButton = ImageUtils.replaceColor(smallButton, LegacyUiConstants.COLOR_LEGACY_BLACK, LegacyUiConstants.COLOR_LEGACY_TRANSPARENT);
		loadedImages.put(ImageType.BUTTON_EDITOR_SMALL_NORMAL, smallEditorButton);
		loadedImages.put(ImageType.BUTTON_EDITOR_SMALL_CLICKED, swapColors(smallEditorButton, lightColor, darkColor));
		
		BufferedImage arenaSetButton = sourceSettingsImage.getSubimage(134, 197, 15, 35);
		arenaSetButton = ImageUtils.replaceColor(arenaSetButton, buttonColor, LegacyUiConstants.COLOR_LEGACY_TRANSPARENT);
		loadedImages.put(ImageType.BUTTON_ARENA_SET_NORMAL, arenaSetButton);
		loadedImages.put(ImageType.BUTTON_ARENA_SET_CLICKED, swapColors(arenaSetButton, lightColor, darkColor));
	}
	
	private BufferedImage swapColors(BufferedImage image, Color color1, Color color2)
	{
		Color swapColor = new Color(mid(color1.getRed(), color2.getRed()), mid(color1.getGreen(), color2.getGreen()), mid(color1.getBlue(), color2.getBlue()));
		
		BufferedImage returnImage = ImageUtils.replaceColor(image, color1, swapColor);
		returnImage = ImageUtils.replaceColor(returnImage, color2, color1);
		returnImage = ImageUtils.replaceColor(returnImage, swapColor, color2);
		
		return returnImage;
	}
	
	private int mid(int a, int b)
	{
		return (a + b) / 2;
	}

	@Deprecated
	private void loadImage(ImageType type, String originalFileName)
	{
		BufferedImage image = loadLegacyImageFile(originalFileName);
		loadedImages.put(type, image);
	}

	private void loadImage(ImageType type, String cachedFileName, String originalFileName)
	{
		BufferedImage image = loadCachedImageFile(cachedFileName);
		
		if (image == null)
		{
			image = loadLegacyImageFile(originalFileName);
			cacheLegacyImageFile(cachedFileName, image);
		}

		loadedImages.put(type, image);
	}
	
	private void cacheLegacyImageFile(String cachedFileName, BufferedImage image)
	{
		File imageFile = new File(getBaseDirectory() + "csdcache\\" + cachedFileName);
		
		try
		{
			ImageIO.write(image, "png", imageFile);
		} catch (IOException e)
		{
			return;
		}
	}

	private BufferedImage loadCachedImageFile(String cachedFileName)
	{
		File imageFile = new File(getBaseDirectory() + "csdcache\\" + cachedFileName);
		if (!imageFile.exists())
			return null;
		
		try
		{
			return ImageUtils.convert(ImageIO.read(imageFile), BufferedImage.TYPE_INT_ARGB);
		} catch (IOException e)
		{
			return null;
		}
	}

	private BufferedImage loadLegacyImageFile(String originalFileName)
	{
		File imageFile = new File(getBaseDirectory() + originalFileName);

		if (imageFile.getName().toLowerCase().endsWith("pnt"))
			return loadImageFile(imageFile, OFFSET_PNT, true, 1, 1);
		else if (imageFile.getName().toLowerCase().endsWith("dve"))
			return loadImageFile(imageFile, OFFSET_DVE, false, 1, 1);
		else if (imageFile.getName().toLowerCase().endsWith("map"))
			return loadImageFile(imageFile, OFFSET_MAP, false, MAP_HEIGHT, MAP_WIDTH, 1, 1);
		else if (imageFile.getName().toLowerCase().endsWith("fnt"))
			return loadImageFile(imageFile, OFFSET_FNT, true, 1, FONT_CHARS);
		else if (imageFile.getName().toLowerCase().endsWith("spt"))
			return loadImageFile(imageFile, OFFSET_SPT, false, SPRITE_HEIGHT, SPRITE_WIDTH, SPRITE_COUNT / SPRITE_COLUMNS, SPRITE_COLUMNS);
		else if (imageFile.getName().toLowerCase().endsWith("til"))
			return loadImageFile(imageFile, OFFSET_TIL, true, TILE_HEIGHT, TILE_WIDTH, TILE_COUNT, 1);
		else
			throw new UnsupportedOperationException("Unrecognized file type.");
	}

	private BufferedImage loadImageFile(File file, int offset, boolean singlePixelsDefined, int finalRows, int finalColumns)
	{
		return prepImageFile(file, offset, singlePixelsDefined, true, -1, -1, finalRows, finalColumns);
	}

	private BufferedImage loadImageFile(File file, int offset, boolean singlePixelsDefined, int spriteHeight, int spriteWidth,
			int finalRows, int finalColumns)
	{
		return prepImageFile(file, offset, singlePixelsDefined, false, spriteHeight, spriteWidth, finalRows, finalColumns);
	}

	private BufferedImage prepImageFile(File file, int offset, boolean singlePixelsDefined, boolean getDimensionsFromFile,
			int spriteHeight, int spriteWidth, int finalRows, int finalColumns)
	{
		int height = spriteHeight;
		int width = spriteWidth;

		FileInputStream fis = null;
		DataInputStream dis = null;

		BufferedImage image = null;

		try
		{
			fis = new FileInputStream(file);
			dis = new DataInputStream(fis);

			// System.out.println("Total file size to read (in bytes) : " + fis.available());

			if (getDimensionsFromFile)
			{
				width = ByteFileReader.readShortBytes(dis);
				ByteFileReader.readShortBytes(dis); // 0, 0 - UNKNOWN
				height = ByteFileReader.readShortBytes(dis);
				ByteFileReader.readShortBytes(dis); // 0, 0 - UNKNOWN
			}

			image = loadImageData(dis, offset, singlePixelsDefined, height, width, finalRows, finalColumns);

			dis.close();
			fis.close();

		} catch (IOException e)
		{
			e.printStackTrace();
			System.out.println("Exception when reading legacy image file at " + file.getAbsolutePath());
		}

		return image;
	}

	// Necessary considerations:
	// MAP files have data before the image data that I actually know what to do with (defining which tiles are displayed on them)
	// SPT files have a bonus byte at the start of each of their lines
	// TIL files define their tile count in the file (first byte)
	private BufferedImage loadImageData(DataInputStream dis, int offset, boolean singlePixelsDefined, int spriteHeight, int spriteWidth,
			int finalRows, int finalColumns) throws IOException
	{
		int imageWidth = spriteWidth * finalColumns;
		int imageHeight = spriteHeight * finalRows;

		// pretty good chance this is a .spt file, meaning we skip the first byte of each row
		boolean isSprite = (spriteWidth == SPRITE_WIDTH && spriteHeight == SPRITE_HEIGHT && offset == OFFSET_SPT);
		boolean byteSkipped = false;

		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

		int startX = 0; // left side of current sprite
		int startY = 0; // top edge of current sprite
		int curX = 0; // X location in current sprite (NOT the full image)
		int curY = 0; // Y location in current sprite (NOT the full image)

		ByteFileReader.scanBytes(dis, offset);

		// System.out.println("Width is: " + spriteWidth + "\nHeight is: " + spriteHeight);

		while (dis.available() > 0)
		{
			int colorCode = ByteFileReader.readUnsignedByte(dis);

			// hack to deal with that extra byte at the start of each line on the player sprites
			if (curX == 0 && isSprite && !byteSkipped)
			{
				byteSkipped = true;
				continue;
			}

			int amount = singlePixelsDefined ? 1 : ByteFileReader.readUnsignedByte(dis);

			Color color = colorMap.getColor(colorCode);

			for (int j = 0; j < amount; j++)
			{
				image.setRGB(startX + curX, startY + curY, color.getRGB());

				curX++;

				if (curX >= spriteWidth) // move to the next row for this particular sprite
				{
					curX = 0;
					curY++;
					byteSkipped = false; // sprite byte skipping hack
				}

				if (curY >= spriteHeight) // past the bottom of the sprite, so move to the next sprite in the row
				{
					curX = 0;
					curY = 0;
					startX = startX + spriteWidth;
				}

				if (startX >= imageWidth) // if after completing a sprite (as above), we're past the edge of the image, move to the next row
				{
					curX = 0;
					curY = 0;
					startX = 0;
					startY = startY + spriteHeight;
				}
			}
		}

		return image;
	}
}
