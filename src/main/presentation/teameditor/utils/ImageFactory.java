package main.presentation.teameditor.utils;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

public class ImageFactory
{
	private static final String BASE_PATH = Paths.get(System.getProperty("user.dir")).getParent().toString()
			+ "\\resources\\editor_images\\";
	private static final String GEAR_FOLDER = "gear\\";
	private static final String PROFILE_FOLDER = "race_profiles\\";

	public static BufferedImage getImage(ImageType type)
	{
		if (type == null)
			type = ImageType.NO_TYPE;

		String typeString = type.toString();

		BufferedImage image = null;
		String path = BASE_PATH;

		if (typeString.startsWith("GEAR"))
			path = path.concat(GEAR_FOLDER);
		else if (typeString.startsWith("PROFILE"))
			path = path.concat(PROFILE_FOLDER);

		path = path.concat(getFileName(type));

		try
		{
			image = ImageIO.read(new File(path));
		} catch (IOException e)
		{
			System.out.println("ImageFactory.java - Could not load graphic! Path was " + path);
		}

		return image;
	}

	public static Dimension getImageSize(ImageType type)
	{
		if (type == null)
			type = ImageType.NO_TYPE;

		Dimension dimension = null;

		String typeString = type.toString();

		if (typeString.startsWith("GEAR"))
			dimension = new Dimension(212, 112);
		else if (typeString.startsWith("PROFILE"))
			dimension = new Dimension(160, 160);
		else if (typeString.equals("DOCBOT"))
			dimension = new Dimension(184, 244);
		else if (typeString.equals("HELMET"))
			dimension = new Dimension(72, 72);

		if (dimension == null)
			throw new UnsupportedOperationException("Dimensions for image of type " + type.toString() + " are unknown.");

		return dimension;
	}

	private static String getFileName(ImageType type)
	{
		switch (type)
		{
		case HELMET:
			return "mask_72x72.bmp";
		case DOCBOT:
			return "docbot.png";
		case PROFILE_CURMIAN:
			return "curmian_160x160.png";
		case PROFILE_DRAGORAN:
			return "dragoran_160x160.png";
		case PROFILE_GRONK:
			return "gronk_160x160.png";
		case PROFILE_HUMAN:
			return "human1_160x160.png";
		case PROFILE_KURGAN:
			return "kurgan_160x160.png";
		case PROFILE_NYNAX:
			return "nynax_160x160.png";
		case PROFILE_SLITH:
			return "slith_160x160.png";
		case PROFILE_XJS9000:
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
		}

		throw new UnsupportedOperationException("File name for image of type " + type.toString() + " is unknown.");
	}
}
