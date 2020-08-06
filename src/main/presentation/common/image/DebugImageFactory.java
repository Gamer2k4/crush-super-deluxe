package main.presentation.common.image;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class DebugImageFactory extends AbstractImageFactory
{
	private static final String GAME_FOLDER = "game_images\\";
	private static final String EDITOR_FOLDER = "editor_images\\";
	private static final String GEAR_FOLDER = EDITOR_FOLDER + "gear\\";
	private static final String PROFILE_FOLDER = EDITOR_FOLDER + "race_profiles\\";
	private static final String MAP_FOLDER = "maps\\";

	private static Map<String, BufferedImage> loadedImages = new HashMap<String, BufferedImage>();

	@Override
	public BufferedImage getImage(ImageType type)
	{
		String typeString = getTypeString(type);

		BufferedImage image = null;
		String path = getBaseDirectory();

		if (typeString.startsWith("GEAR"))
			path = path.concat(GEAR_FOLDER);
		else if (typeString.startsWith("PROFILE"))
			path = path.concat(PROFILE_FOLDER);
		else if (typeString.startsWith("GAME"))
			path = path.concat(GAME_FOLDER);
		else if (typeString.startsWith("EDITOR"))
			path = path.concat(EDITOR_FOLDER);
		else if (typeString.startsWith("MAP"))
			path = path.concat(MAP_FOLDER);

		path = path.concat(getFileName(type));

		if (loadedImages.containsKey(path))
			return loadedImages.get(path);

		try
		{
			image = ImageIO.read(new File(path));
		} catch (IOException e)
		{
			System.out.println("ImageFactory.java - Could not load graphic! Path was " + path);
		}

		loadedImages.put(path, image);

		return image;
	}
	
	@Override
	public BufferedImage copyImage(ImageType type)
	{
		return ImageUtils.deepCopy(getImage(type));
	}

	@Override
	public Dimension getImageSize(ImageType type)
	{
		Dimension dimension = null;

		String typeString = getTypeString(type);

		if (typeString.startsWith("GEAR"))
			dimension = new Dimension(212, 112);
		else if (typeString.startsWith("PROFILE"))
			dimension = new Dimension(160, 160);
		else if (typeString.equals("EDITOR_DOCBOT"))
			dimension = new Dimension(184, 244);
		else if (typeString.equals("EDITOR_HELMET"))
			dimension = new Dimension(72, 72);

		if (dimension == null)
			throw new UnsupportedOperationException("Dimensions for image of type " + typeString + " are unknown.");

		return dimension;
	}

	private String getFileName(ImageType type)
	{
		switch (type)
		{
		case MAP_LAVA_BG:
			return "bg_lava.png";
		case MAP_A1:
			return "A1_bridges.png";
		case EDITOR_HELMET:
			return "mask_72x72.bmp";
		case EDITOR_DOCBOT:
			return "docbot.png";
		case GAME_SIDEBAR:
			return "sidebar.png";
		case GAME_BUTTONBAR:
			return "button_bar.png";
		case GAME_CLICKMAP:
			return "button_bar_click_map.png";
		case PROFILE_CURMIAN_L:
			return "curmian_160x160.png";
		case PROFILE_DRAGORAN_L:
			return "dragoran_160x160.png";
		case PROFILE_GRONK_L:
			return "gronk_160x160.png";
		case PROFILE_HUMAN_L:
			return "human1_160x160.png";
		case PROFILE_KURGAN_L:
			return "kurgan_160x160.png";
		case PROFILE_NYNAX_L:
			return "nynax_160x160.png";
		case PROFILE_SLITH_L:
			return "slith_160x160.png";
		case PROFILE_XJS9000_L:
			return "xjs9000_160x160.png";
		case GEAR_BACKFIRE_BELT:
			return "belt_backfire_212x112.png";
		case GEAR_BOOSTER_BELT:
			return "belt_booster_212x112.png";
		case GEAR_CLOAKING_BELT:
			return "belt_cloaking_212x112.png";
		case GEAR_INTEGRITY_BELT:
			return "belt_field_integrity_212x112.png";
		case GEAR_HOLOGRAM_BELT:
			return "belt_hologram_212x112.png";
		case GEAR_MEDICAL_BELT:
			return "belt_medical_212x112.png";
		case GEAR_SCRAMBLER_BELT:
			return "belt_scrambler_212x112.png";
		case GEAR_BOUNDER_BOOTS:
			return "boots_bounder_212x112.png";
		case GEAR_INSULATED_BOOTS:
			return "boots_insulated_212x112.png";
		case GEAR_MAGNETIC_BOOTS:
			return "boots_magnetic_212x112.png";
		case GEAR_SAAI_BOOTS:
			return "boots_saai_212x112.png";
		case GEAR_SPIKED_BOOTS:
			return "boots_spiked_212x112.png";
		case GEAR_MAGNETIC_GLOVES:
			return "gloves_magnetic_212x112.png";
		case GEAR_REPULSOR_GLOVES:
			return "gloves_repulsor_212x112.png";
		case GEAR_SAAI_GLOVES:
			return "gloves_saai_212x112.png";
		case GEAR_SPIKED_GLOVES:
			return "gloves_spiked_212x112.png";
		case GEAR_SURGE_GLOVES:
			return "gloves_surge_212x112.png";
		case GEAR_HEAVY_PADS:
			return "pads_heavy_212x112.png";
		case GEAR_REINFORCED_PADS:
			return "pads_reinforced_212x112.png";
		case GEAR_REPULSOR_PADS:
			return "pads_repulsor_212x112.png";
		case GEAR_SPIKED_PADS:
			return "pads_spiked_212x112.png";
		case GEAR_SURGE_PADS:
			return "pads_surge_212x112.png";
		case GEAR_VORTEX_PADS:
			return "pads_vortex_212x112.png";
		case NO_TYPE:
			break;
		default:
			break;
		}

		throw new UnsupportedOperationException("File name for image of type " + type.toString() + " is unknown.");
	}

	@Override
	protected String getBaseDirectory()
	{
		return Paths.get(System.getProperty("user.dir")).getParent().toString() + "\\resources\\";
	}
}
