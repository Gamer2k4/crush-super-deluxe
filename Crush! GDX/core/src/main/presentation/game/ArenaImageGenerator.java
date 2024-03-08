package main.presentation.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import main.data.entities.Arena;
import main.data.factory.SimpleArenaFactory;
import main.presentation.common.image.ImageUtils;
import main.presentation.legacy.common.LegacyUiConstants;

public class ArenaImageGenerator
{
	private static ArenaImageGenerator instance = null;
	
	private Texture arenaImage = null;
	private Pixmap arenaPixmap = null;
	
	private List<Texture> arenaImages = new ArrayList<Texture>();
	private List<Pixmap> arenaPixmaps = new ArrayList<Pixmap>();
	
	//TODO: confirm these colors (they may also be different between the team editor and the actual game)
	public static final Color FLOOR_COLOR = ImageUtils.gdxColor(LegacyUiConstants.COLOR_LEGACY_BLACK);
	public static final Color TELE_COLOR = ImageUtils.gdxColor(LegacyUiConstants.COLOR_LEGACY_BLUE);
	public static final Color GOAL_COLOR = ImageUtils.gdxColor(LegacyUiConstants.COLOR_LEGACY_BLUE);
	public static final Color DIM_GOAL_COLOR = ImageUtils.gdxColor(new java.awt.Color(0, 64, 112));
	public static final Color PAD_COLOR = ImageUtils.gdxColor(LegacyUiConstants.COLOR_LEGACY_DULL_WHITE);
	public static final Color WALL_COLOR = ImageUtils.gdxColor(LegacyUiConstants.COLOR_LEGACY_BLUE_GREY);
	public static final Color BIN_COLOR =ImageUtils.gdxColor( LegacyUiConstants.COLOR_LEGACY_BLUE_GREY);
	
	public static ArenaImageGenerator getInstance()
	{
		if (instance == null)
			instance = new ArenaImageGenerator();
		
		return instance;
	}
	
	private ArenaImageGenerator()
	{
		for (int i = 0; i < Arena.TOTAL_ARENAS; i++)
		{
			Arena arena = SimpleArenaFactory.getInstance().generateArena(i);
			Pixmap pixmap = generateArenaImagePixmap(arena, 30, 2);
			Texture texture = new Texture(pixmap);
			
			arenaPixmaps.add(pixmap);
			arenaImages.add(texture);
		}
	}
	
	private void drawTile(Pixmap pixmap, int x, int y, int tile, int tileSize)
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
		
		drawTile(pixmap, x, y, fillColor, tileSize);
	}
	
	public void drawTile(int x, int y, Color fillColor, int tileSize)
	{
		drawTile(arenaPixmap, x, y, fillColor, tileSize);
	}
	
	public void drawTile(Pixmap pixmap, int x, int y, Color fillColor, int tileSize)
	{
		pixmap.setColor(fillColor);
		pixmap.fillRectangle(x * tileSize, y * tileSize, tileSize, tileSize);
	}
	
	//TODO: I think the coloration here isn't QUITE right; in fact, I think I give more detail than the original game does
	public void generateArenaImage(Arena arena, int arenaSideLength, int tileSize)
	{
		disposeCurrentImage();
		
		arenaPixmap = generateArenaImagePixmap(arena, arenaSideLength, tileSize);
		
		prepare();
	}
	
	private Pixmap generateArenaImagePixmap(Arena arena, int arenaSideLength, int tileSize)
	{
		int sideLengthWithBorders = arenaSideLength + 2;
		Pixmap pixmap = new Pixmap(sideLengthWithBorders * tileSize, sideLengthWithBorders * tileSize, Format.RGBA8888);
		
		for (int i = 0; i < sideLengthWithBorders; i++)
		{
			for (int j = 0; j < sideLengthWithBorders; j++)
			{
				if (i == 0 || j == 0 || i == sideLengthWithBorders - 1 || j == sideLengthWithBorders - 1)
				{
					drawTile(pixmap, j, i, Arena.TILE_WALL, tileSize);
					continue;
				}
				
				drawTile(pixmap, j, i, arena.getTile(i - 1, j - 1), tileSize);
			}
		}
		
		return pixmap;
	}
	
	public void prepare()
	{
		if (arenaImage != null)
			arenaImage.dispose();
		
		arenaImage = new Texture(arenaPixmap);
	}
	
	public Drawable getArenaImage()
	{
		return new TextureRegionDrawable(arenaImage);
	}
	
	public Drawable getArenaImage(int arenaIndex)
	{
		return new TextureRegionDrawable(arenaImages.get(arenaIndex));
	}
	
	private void disposeCurrentImage()
	{
		if (arenaPixmap != null && !arenaPixmap.isDisposed())
			arenaPixmap.dispose();
		
		if (arenaImage != null)
			arenaImage.dispose();
	}
	
	public void dispose()
	{
		disposeCurrentImage();
		
		for (int i = 0; i < Arena.TOTAL_ARENAS; i++)
		{
			Pixmap pixmap = arenaPixmaps.get(i);
			Texture texture = arenaImages.get(i);
			
			if (pixmap != null && !pixmap.isDisposed())
				pixmap.dispose();
			
			if (texture != null)
				texture.dispose();
		}
	}
}
