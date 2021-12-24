package main.presentation.game.sprite;

import java.awt.Point;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.data.entities.Team;
import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.TeamColorsManager;

public class CrushTile extends CrushSprite
{
	private static final int TOTAL_TILES = 192;
	
	protected static TextureRegion tiles[] = new TextureRegion[TOTAL_TILES];
	
	private static Team homeTeam = new Team();
	
	protected CrushTile(Point coords, TextureRegion tile)
	{
		super(coords, tile);
	}
	
	public static void setHomeTeam(Team team)
	{
		homeTeam = team;
		
		//clear out the cached goal tile images
		for (int i = 3; i <= 18; i++)
		{
			tiles[i] = null;
			tiles[i + 128] = null;
		}
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
		
		if (tiles[index] != null)
			return new CrushTile(coords, tiles[index]);
		
		if (index >= 3 && index <= 18)
		{
			TextureRegion region = getTextureRegion(index - 3, TeamColorsManager.getInstance().getDarkGoalTiles(homeTeam));
			tiles[index] = region;
			return new CrushTile(coords, region);
		}
		
		if (index >= 131 && index <= 146)
		{
			TextureRegion region = getTextureRegion(index - 131, TeamColorsManager.getInstance().getLitGoalTiles(homeTeam));
			tiles[index] = region;
			return new CrushTile(coords, region);
		}
		
		return new CrushTile(coords, getTextureRegion(index));
	}
	
	protected static TextureRegion getTextureRegion(int index)
	{
		TextureRegion region = getTextureRegion(index, ImageFactory.getInstance().getTexture(ImageType.CRUSH_TILES));
		tiles[index] = region;	
		return region;
	}
	
	protected static TextureRegion getTextureRegion(int index, Texture spriteSheet)
	{
		return new TextureRegion(spriteSheet, 0, 30 * index, 36, 30);
	}
}
