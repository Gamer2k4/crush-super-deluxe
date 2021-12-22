package main.presentation.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import main.data.entities.Arena;
import main.presentation.common.image.ImageUtils;
import main.presentation.legacy.common.LegacyUiConstants;

public class ArenaImageGenerator
{
	private static Texture arenaImage = null;
	private static Pixmap arenaPixmap = null;
	
	//TODO: confirm these colors (they may also be different between the team editor and the actual game)
	public static final Color FLOOR_COLOR = ImageUtils.gdxColor(LegacyUiConstants.COLOR_LEGACY_BLACK);
	public static final Color TELE_COLOR = ImageUtils.gdxColor(LegacyUiConstants.COLOR_LEGACY_BLUE);
	public static final Color GOAL_COLOR = ImageUtils.gdxColor(LegacyUiConstants.COLOR_LEGACY_BLUE);
	public static final Color DIM_GOAL_COLOR = ImageUtils.gdxColor(new java.awt.Color(0, 64, 112));
	public static final Color PAD_COLOR = ImageUtils.gdxColor(LegacyUiConstants.COLOR_LEGACY_DULL_WHITE);
	public static final Color WALL_COLOR = ImageUtils.gdxColor(LegacyUiConstants.COLOR_LEGACY_BLUE_GREY);
	public static final Color BIN_COLOR =ImageUtils.gdxColor( LegacyUiConstants.COLOR_LEGACY_BLUE_GREY);
	
	public static void drawTile(int x, int y, int tile, int tileSize)
	{
		Color fillColor = FLOOR_COLOR;
		
		if (tile == Arena.TILE_TELE)
			fillColor = TELE_COLOR;
		else if (tile == Arena.TILE_GOAL)
			fillColor = GOAL_COLOR;
		else if (tile == Arena.TILE_PAD)
			fillColor = PAD_COLOR;
		else if (tile == Arena.TILE_WALL)
			fillColor = WALL_COLOR;
		else if (tile == Arena.TILE_BIN)
			fillColor = BIN_COLOR;
		
		drawTile(x, y, fillColor, tileSize);
	}
	
	public static void drawTile(int x, int y, Color fillColor, int tileSize)
	{
		arenaPixmap.setColor(fillColor);
		arenaPixmap.fillRectangle(x * tileSize, y * tileSize, tileSize, tileSize);
	}
	
	public static void generateArenaImage(Arena arena, int arenaSideLength, int tileSize)
	{
		dispose();
		arenaPixmap = new Pixmap(arenaSideLength * tileSize, arenaSideLength * tileSize, Format.RGBA8888);
		
		for (int i = 0; i < arenaSideLength; i++)
		{
			for (int j = 0; j < arenaSideLength; j++)
			{
				drawTile(j, i, arena.getTile(i, j), tileSize);
			}
		}
		
		prepare();
	}
	
	public static void prepare()
	{
		if (arenaImage != null)
			arenaImage.dispose();
		
		arenaImage = new Texture(arenaPixmap);
	}
	
	public static Drawable getArenaImage()
	{
		return new TextureRegionDrawable(arenaImage);
	}
	
	public static void dispose()
	{
		if (arenaPixmap != null && !arenaPixmap.isDisposed())
			arenaPixmap.dispose();
		
		if (arenaImage != null)
			arenaImage.dispose();
	}
}
