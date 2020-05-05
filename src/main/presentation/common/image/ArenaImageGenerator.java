package main.presentation.common.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.data.entities.Arena;
import main.presentation.legacy.common.LegacyUiConstants;

public class ArenaImageGenerator
{
	//TODO: confirm these colors (they may also be different between the team editor and the actual game)
	public static final Color FLOOR_COLOR = LegacyUiConstants.COLOR_LEGACY_BLACK;
	public static final Color TELE_COLOR = LegacyUiConstants.COLOR_LEGACY_BLUE;
	public static final Color GOAL_COLOR = LegacyUiConstants.COLOR_LEGACY_BLUE;
	public static final Color DIM_GOAL_COLOR = new Color(0, 64, 112);
	public static final Color PAD_COLOR = LegacyUiConstants.COLOR_LEGACY_DULL_WHITE;
	public static final Color WALL_COLOR = LegacyUiConstants.COLOR_LEGACY_BLUE_GREY;
	public static final Color BIN_COLOR = LegacyUiConstants.COLOR_LEGACY_BLUE_GREY;
	
	public static void drawTile(Graphics2D g2, int x, int y, int tile, int tileSize)
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
		
		drawTile(g2, x, y, fillColor, tileSize);
	}
	
	public static void drawTile(Graphics2D g2, int x, int y, Color fillColor, int tileSize)
	{
		Rectangle tileRect = new Rectangle(tileSize * x, tileSize * y, tileSize, tileSize);
		
		g2.setColor(fillColor);
		g2.fill(tileRect);
	}
	
	public static BufferedImage getArenaImage(Arena arena, int arenaSideLength, int tileSize)
	{
		BufferedImage arenaImage = ImageUtils.createBlankBufferedImage(new Dimension(arenaSideLength * tileSize, arenaSideLength * tileSize), Color.BLACK);
		Graphics2D graphics = arenaImage.createGraphics();
		
		for (int i = 0; i < arenaSideLength; i++)
		{
			for (int j = 0; j < arenaSideLength; j++)
			{
				drawTile(graphics, j, i, arena.getTile(i, j), tileSize);
			}
		}
		
		return arenaImage;
	}
}
