package main.data.entities;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Field implements Serializable
{
	public static final int WALL_TILE = 0;
	public static final int FLOOR_TILE = 1;
	public static final int GOAL_TILE = 2;
	public static final int TELE_TILE = 3;
	public static final int PAD_TILE = 4;
	public static final int BIN_TILE = 5;
	public static final int SHOCK_TILE = 6;
	
	public static final int STATE_UNTRIED = 0;
	public static final int STATE_FAILED = 1;
	public static final int STATE_SUCCESS = 2;
	
	int[][] tiles = new int[30][30];
	List<Point> portals = new ArrayList<Point>();
	List<Point> ballPads = new ArrayList<Point>();
	List<Point> ballBins = new ArrayList<Point>();
	List<Integer> binState = new ArrayList<Integer>();
	
	public Field() throws Exception
	{
		this("000000000000000000000000000000022221111311111111113111122220022221111111111111111111122220022221111111111111111111122220022221111111111111111111122220011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110031111111111111111111111111130011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110031111111111111111111111111130011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110022221111111111111111111122220022221111111111111111111122220022221111111111111111111122220022221111311111111113111122220000000000000000000000000000000");
	}
	
	public Field clone()
	{
		Field toRet = null;
		
		try {
			toRet = new Field();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		toRet.portals.clear();
		
		for (int i = 0; i < portals.size(); i++)
		{
			Point p = new Point(portals.get(i).x, portals.get(i).y);
			toRet.portals.add(p);
		}
		
		for (int i = 0; i < 30; i++)
		{
			for (int j = 0; j < 30; j++)
			{
				toRet.tiles[i][j] = tiles[i][j];
			}
		}
		
		return toRet;
	}
	
	public Field(String mapAsString) throws Exception
	{
		if (mapAsString.length() != 900)
			throw new Exception("Invalid map string passed into Field constructor!");
		
		int index = 0;
		
		for (int i = 0; i < 30; i++)
		{
			for (int j = 0; j < 30; j++)
			{
				tiles[i][j] = Integer.parseInt(mapAsString.substring(index, index + 1));				
				index++;
			}
		}
		
		//scan for special features
		for (int i = 0; i < 30; i++)
		{
			for (int j = 0; j < 30; j++)
			{
				if (tiles[i][j] == TELE_TILE)
				{
					portals.add(new Point(i, j));
				}
				
				if (tiles[i][j] == PAD_TILE)
				{
					ballPads.add(new Point(i, j));
					
					//find the associated bin (for graphics purposes); note that this won't work for all maps
					for (int k = -1; k <= 1; k++)
					{
						for (int l = -1; l <= 1; l++)
						{
							//System.out.print(tiles[i+k][j+l]);
							if (tiles[i + k][j + l] == BIN_TILE)
							{
								ballBins.add(new Point(i + k, j + l));
								binState.add(STATE_UNTRIED);
							}
						}
						//System.out.println();
					}
					//System.out.println();
				}
			}
		}
		
		System.out.println(ballBins.size() + " " + ballBins);
		System.out.println(ballPads.size() + " " + ballPads);
	}
	
	public int getTile(int x, int y)
	{
		return tiles[x][y];
	}
	
	public int getTile(Point p)
	{
		return tiles[p.x][p.y];
	}
	
	public Point getPortal(int index)
	{
		return (Point)portals.get(index).clone();
	}
	
	public int getBinIndex(int x, int y)
	{
		int binIndex = -1;
		
		//assumes exactly 8 ball bins
		for (int i = 0; i < 8; i++)
		{
			Point toCheck = (Point)ballBins.get(i).clone();
			
			if (toCheck.x == x && toCheck.y == y)
			{
				binIndex = i;
				break;
			}
		}
		
		return binIndex;
	}
	
	public int getPadIndex(int x, int y)
	{
		int padIndex = -1;
		
		//assumes exactly 8 ball bins
		for (int i = 0; i < 8; i++)
		{
			Point toCheck = (Point)ballPads.get(i).clone();
			
			if (toCheck.x == x && toCheck.y == y)
			{
				padIndex = i;
				break;
			}
		}
		
		return padIndex;
	}
	
	public int getBinStatus(int index)
	{
		if (index >= 0 && index < 8)
			return binState.get(index);
		
		return -1;
	}
	
	public void setBinStatus(int index, int state)
	{
		if (index >= 0 && index < 8)
			binState.set(index, state);
	}
	
	public int getUntriedBinCount()
	{
		int count = 0;
		
		for (int i = 0; i < 8; i++)
		{
			if (getBinStatus(i) == STATE_UNTRIED)
				count++;
		}
		
		return count;
	}
	
	public boolean isObstructed(int x, int y)
	{
		//out of bounds is obviously obstructed
		if (x < 0 || y < 0 || x > 29 || y > 29)
			return true;
		
		int tile = tiles[x][y];
		
		if (tile == FLOOR_TILE || tile == GOAL_TILE || tile == PAD_TILE || tile == SHOCK_TILE || tile == TELE_TILE)
			return false;
		
		return true;
	}
	
	public void ballFound(int binIndex)
	{
		//turn off all bins but the correct one
		for (int i = 0; i < 8; i++)
		{
			if (i == binIndex)
			{
				setBinStatus(i, Field.STATE_SUCCESS);
			}
			else
			{
				setBinStatus(i, Field.STATE_FAILED);
			}
		}
		
		//clear out three of the goals
		Point p = ballPads.get(binIndex);
		
		int x = p.x;
		int y = p.y;
		
		if (x > 14)	//bottom
		{
			clearQuadrant(3);
			clearQuadrant(4);
		}
		if (x < 15)	//top
		{
			clearQuadrant(2);
			clearQuadrant(1);
		}
		if (y > 14)	//right
		{
			clearQuadrant(1);
			clearQuadrant(4);
		}
		if (y < 15)	//left
		{
			clearQuadrant(2);
			clearQuadrant(3);
		}
	}
	
	//quadNum is based on the Cartesian plane numbering
	//21
	//34
	private void clearQuadrant(int quadNum)
	{
		int xMin = 1;
		int yMin = 1;

		if (quadNum == 1 || quadNum == 4)
			yMin = 25;
		if (quadNum == 3 || quadNum == 4)
			xMin = 25;
			
		for (int i = xMin; i <= xMin + 3; i++)
		{
			for (int j = yMin; j <= yMin + 3; j++)
			{
				tiles[i][j] = FLOOR_TILE;
			}
		}
	}
}
