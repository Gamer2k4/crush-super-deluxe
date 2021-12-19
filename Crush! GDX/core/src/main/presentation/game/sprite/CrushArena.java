package main.presentation.game.sprite;

import java.awt.Point;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.data.entities.Arena;
import main.presentation.ImageFactory;
import main.presentation.ImageType;

public class CrushArena extends CrushSprite
{
	private static ImageFactory imageFactory = ImageFactory.getInstance(); 
	
	protected CrushArena(TextureRegion tile)
	{
		super(new Point(0, 0), tile);
	}

	public static CrushArena createArena(Arena arena)
	{
		if (arena == null)
			return null;
		
		return createArena(arena.getIndex());
	}

	public static CrushArena createArena(int index)
	{
		Texture arenaTexture;
		
		switch (index)
		{
		case 0:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_A1);
			break;
		case 1:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_A2);
			break;
		case 2:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_A3);
			break;
		case 3:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_A4);
			break;
		case 4:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_B1);
			break;
		case 5:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_B2);
			break;
		case 6:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_B3);
			break;
		case 7:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_B4);
			break;
		case 8:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_C1);
			break;
		case 9:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_C2);
			break;
		case 10:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_C3);
			break;
		case 11:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_C4);
			break;
		case 12:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_D1);
			break;
		case 13:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_D2);
			break;
		case 14:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_D3);
			break;
		case 15:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_D4);
			break;
		case 16:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_E1);
			break;
		case 17:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_E2);
			break;
		case 18:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_E3);
			break;
		case 19:
			arenaTexture = imageFactory.getTexture(ImageType.MAP_E4);
			break;
		default:
			throw new IllegalArgumentException("No map image defined for arena " + index);
		}
		
		TextureRegion region = new TextureRegion(arenaTexture);
		return new CrushArena(region);
	}
}
