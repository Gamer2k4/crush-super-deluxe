package main.presentation.game.sprite;

import java.awt.Point;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.presentation.ImageFactory;
import main.presentation.ImageType;

public class CrushTile extends CrushSprite
{
	private static final int TOTAL_TILES = 192;
	
	protected static TextureRegion tiles[] = new TextureRegion[TOTAL_TILES];
	
	protected CrushTile(Point coords, TextureRegion tile)
	{
		super(coords, tile);
	}
	
	public static CrushTile createTile(int x, int y, TileSpriteType type)
	{
		return createTile(x, y, type.getIndex());
	}
	
	public static CrushTile createTile(int x, int y, int index)
	{
		return createTile(new Point(x, y), index);
	}
	
	public static CrushTile createTile(Point coords, TileSpriteType type)
	{
		return createTile(coords, type.getIndex());
	}
	
	public static CrushTile createTile(Point coords, int index)
	{
		if (!TileSpriteType.getTileSpriteType(index).isStatic())
			return CrushAnimatedTile.createTile(coords, index);
		
		return new CrushTile(coords, getTextureRegion(index));
	}
	
	protected static TextureRegion getTextureRegion(int index)
	{
		if (tiles[index] != null)
			return tiles[index];
		
		Texture baseTexture = ImageFactory.getInstance().getTexture(ImageType.CRUSH_TILES);
		TextureRegion region = new TextureRegion(baseTexture, 0, (30 * index) - 2, 36, 30);
		tiles[index] = region;
		
		return region;
	}
}
