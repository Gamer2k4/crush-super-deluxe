package main.presentation.game.sprite;

import java.awt.Point;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class CrushSprite
{
	protected Point coords;
	protected TextureRegion tile;
	
	public static final Point OFFSCREEN_COORDS = new Point(-1000, -1000);
	
	protected CrushSprite(Point coords, TextureRegion tile)
	{
		this.coords = new Point(coords.x, coords.y);
		this.tile = tile;
	}
	
	public void setCoords(Point newCoords)
	{
		coords = new Point(newCoords.x, newCoords.y);
	}
	
	public Point getCoords()
	{
		return new Point(coords.x, coords.y);
	}
	
	public int getX()
	{
		return coords.x;
	}
	
	public int getY()
	{
		return coords.y;
	}
	
	public TextureRegion getImage()
	{
		return tile;
	}

	public void setArenaPosition(Point position)
	{
		setArenaPosition(position.x, position.y);
	}
	
	public void setArenaPosition(int row, int column)
	{
		coords.x = getXValueForColumn(column);
		coords.y = getYValueForRow(row);
	}
	
	public Point getArenaPosition()
	{
		System.out.println("Getting arena position for coords[" + coords + "]");
		
		int col = (coords.x / 36) - 1;
		int row = 30 - (coords.y / 30);
		
		System.out.println("Row, Column is (" + row + ", " + col + ")");
		
		return new Point(row, col);
	}
	
	protected int getXValueForColumn(int column)
	{
		return 36 * (column + 1);
	}
	
	protected int getYValueForRow(int row)
	{
		return 900 - (30 * row);		//needs to be "900 -" because y starts at the bottom, not the top
	}
}
