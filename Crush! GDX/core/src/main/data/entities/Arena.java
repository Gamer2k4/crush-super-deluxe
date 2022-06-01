package main.data.entities;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Arena implements Serializable
{
	private static final long serialVersionUID = 5167275044740085358L;
	
	public static final int TILE_WALL = 0;
	public static final int TILE_FLOOR = 1;
	public static final int TILE_GOAL = 2;
	public static final int TILE_TELE = 3;
	public static final int TILE_PAD = 4;
	public static final int TILE_BIN = 5;
	public static final int TILE_SHOCK = 6;
	
	public static final int STATE_UNTRIED = 0;
	public static final int STATE_FAILED = 1;
	public static final int STATE_SUCCESS = 2;
	
	public static final int ARENA_BRIDGES = 0;
	public static final int ARENA_JACKALS_LAIR = 1;
	public static final int ARENA_CRISSICK = 2;
	public static final int ARENA_WHIRLWIND = 3;
	public static final int ARENA_THE_VOID = 4;
	public static final int ARENA_OBSERVATORY = 5;
	public static final int ARENA_ABYSS = 6;
	public static final int ARENA_GADEL_SPYRE = 7;
	public static final int ARENA_FULCRUM = 8;
	public static final int ARENA_SAVANNA = 9;
	public static final int ARENA_BARROW = 10;
	public static final int ARENA_MAELSTROM = 11;
	public static final int ARENA_VAULT = 12;
	public static final int ARENA_NEXUS = 13;
	public static final int ARENA_DARKSUN = 14;
	public static final int ARENA_BADLANDS = 15;
	public static final int ARENA_LIGHTWAY = 16;
	public static final int ARENA_EYES = 17;
	public static final int ARENA_DARKSTAR = 18;
	public static final int ARENA_SPACECOM = 19;
	
	public static final int ARENA_DIMENSIONS = 30;
	
	private boolean ballFound = false;
	private int index;
	private String name;
	
	private int[][] legacyTileData = new int[ARENA_DIMENSIONS][ARENA_DIMENSIONS];
	private int[][] tiles = new int[ARENA_DIMENSIONS][ARENA_DIMENSIONS];
	private List<Point> portals = new ArrayList<Point>();
	private List<Point> ballPads = new ArrayList<Point>();
	private List<Point> ballBins = new ArrayList<Point>();
	private List<Integer> binState = new ArrayList<Integer>();
	
	private List<Point> dimGoalTiles = new ArrayList<Point>();	//needed for darkened goals on minimap
	
	public Arena()
	{
		initializeArena(-1, "NONAME", "000000000000000000000000000000022221111311111111113111122220022221111111111111111111122220022221111111111111111111122220022221111111111111111111122220011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110031111111111111111111111111130011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110031111111111111111111111111130011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110011111111111111111111111111110022221111111111111111111122220022221111111111111111111122220022221111111111111111111122220022221111311111111113111122220000000000000000000000000000000");
	}
	
	@Override
	public Arena clone()
	{
		Arena toRet = null;
		
		try {
			toRet = new Arena();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		toRet.portals.clear();
		toRet.index = index;
		toRet.name = name;
		
		for (Point point : portals)
		{
			Point p = new Point(point.x, point.y);
			toRet.portals.add(p);
		}
		
		for (Point point : ballPads)
		{
			Point p = new Point(point.x, point.y);
			toRet.ballPads.add(p);
		}
		
		for (Point point : ballBins)
		{
			Point p = new Point(point.x, point.y);
			toRet.ballBins.add(p);
		}
		
		for (Integer integer : binState)
		{
			Integer i = new Integer(integer.intValue());
			toRet.binState.add(i);
		}
		
		for (Point point : dimGoalTiles)
		{
			Point p = new Point(point.x, point.y);
			toRet.dimGoalTiles.add(p);
		}
		
		for (int i = 0; i < ARENA_DIMENSIONS; i++)
		{
			for (int j = 0; j < ARENA_DIMENSIONS; j++)
			{
				toRet.tiles[i][j] = tiles[i][j];
				toRet.legacyTileData[i][j] = legacyTileData[i][j];
			}
		}
		
		return toRet;
	}
	
	public Arena(int arenaIndex, String arenaName, String arenaAsString) throws IllegalArgumentException
	{
		if (arenaAsString.length() != 900)
			throw new IllegalArgumentException("Invalid map string passed into Field constructor!");
		
		initializeArena(arenaIndex, arenaName, arenaAsString);
	}
	
	public Arena(int arenaIndex, String arenaName, String arenaAsString, String legacyTileDataString) throws IllegalArgumentException
	{
		if (arenaAsString.length() != 900 || legacyTileDataString.length() != 900)
			throw new IllegalArgumentException("Invalid map string passed into Field constructor!");
		
		initializeArena(arenaIndex, arenaName, arenaAsString, legacyTileDataString);
	}
	
	//just map the legacy tile data empty if we're not given any
	//TODO: generate legacy tile data (correct indexes for legacy tiles based on tile positioning (converting a "goal" tile to the proper image, for example))
	public void initializeArena(int arenaIndex, String arenaName, String arenaAsString)
	{
		StringBuilder stringBuilder = new StringBuilder();
		
		for (int i = 0; i < 900; i++)
			stringBuilder.append((char)0);
		
		initializeArena(arenaIndex, arenaName, arenaAsString, new String(stringBuilder));
	}
	
	public void initializeArena(int arenaIndex, String arenaName, String arenaAsString, String legacyTileDataString)
	{
		index = arenaIndex;
		name = arenaName;
		
		int stringIndex = 0;
		
		for (int i = 0; i < ARENA_DIMENSIONS; i++)
		{
			for (int j = 0; j < ARENA_DIMENSIONS; j++)
			{
				tiles[i][j] = Integer.parseInt(arenaAsString.substring(stringIndex, stringIndex + 1));
				legacyTileData[i][j] = legacyTileDataString.charAt(stringIndex);
				stringIndex++;
			}
		}
		
		//scan for special features
		for (int i = 0; i < ARENA_DIMENSIONS; i++)
		{
			for (int j = 0; j < ARENA_DIMENSIONS; j++)
			{
				if (tiles[i][j] == TILE_TELE)
				{
					portals.add(new Point(i, j));
				}
				
				if (tiles[i][j] == TILE_PAD)
				{
					ballPads.add(new Point(i, j));
					
					//find the associated bin (for graphics purposes)
					for (int k = -1; k <= 1; k++)
					{
						for (int l = -1; l <= 1; l++)
						{
							//only check cardinal directions; this should make all maps read properly
							if (Math.abs(k) - Math.abs(l) == 0)
								continue;
							
							//System.out.print(tiles[i+k][j+l]);
							if (tiles[i + k][j + l] == TILE_BIN)
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
		
		//System.out.println(ballBins.size() + " " + ballBins);
		//System.out.println(ballPads.size() + " " + ballPads);
	}

	public int getTile(int x, int y)
	{
		return tiles[x][y];
	}
	
	public int getTile(Point p)
	{
		return tiles[p.x][p.y];
	}
	
	public int getLegacyTile(int x, int y)
	{
		return legacyTileData[x][y];
	}
	
	public int getLegacyTile(Point p)
	{
		return legacyTileData[p.x][p.y];
	}
	
	public int getPortalCount()
	{
		return portals.size();
	}
	
	public Point getPortal(int portalIndex)
	{
		return (Point)portals.get(portalIndex).clone();
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
	
	public List<Point> getDimPadLocations()
	{
		List<Point> failedPads = new ArrayList<Point>();
		
		for (int i = 0; i < binState.size(); i++)
		{
			if (binState.get(i) == STATE_FAILED)
			{
				Point failedPadPoint = ballPads.get(i);
				failedPads.add(new Point(failedPadPoint.x, failedPadPoint.y));
			}
		}
		
		return failedPads;
	}
	
	public List<Point> getDimGoalLocations()
	{
		return dimGoalTiles;
	}
	
	public int getBinStatus(int binIndex)
	{
		if (binIndex >= 0 && binIndex < 8)
			return binState.get(binIndex);
		
		return -1;
	}
	
	public Point getBinLocation(int binIndex)
	{
		if (binIndex >= 0 && binIndex < 8)
			return ballBins.get(binIndex);
		
		return null;
	}
	
	public Point getPadLocation(int binIndex)
	{
		if (binIndex >= 0 && binIndex < 8)
			return ballPads.get(binIndex);
		
		return null;
	}
	
	public void setBinStatus(int index, int state)
	{
		if (index < 0 || index >= 8)
			return;
		
		//don't update twice (especially important for legacy tile change below)
		if (binState.get(index) == state)
			return;
		
		binState.set(index, state);
		
		//dim tried pads in legacy display
		Point padLocation = ballPads.get(index);
		Point binLocation = ballBins.get(index);
		
		if (state == STATE_FAILED)
		{
			legacyTileData[padLocation.x][padLocation.y] += 4;
			legacyTileData[binLocation.x][binLocation.y] += 8;
		} else if (state == STATE_SUCCESS)
		{
			legacyTileData[binLocation.x][binLocation.y] += 4;
		}
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
	
	public boolean isObstructedForPlayer(int x, int y)
	{
		//out of bounds is obviously obstructed
		if (x < 0 || y < 0 || x > 29 || y > 29)
			return true;
		
		int tile = tiles[x][y];
		
		if (tile == TILE_FLOOR || tile == TILE_GOAL || tile == TILE_PAD || tile == TILE_SHOCK || tile == TILE_TELE)
			return false;
		
		return true;
	}
	
	public boolean isObstructedForBall(int x, int y)
	{
		//out of bounds is obviously obstructed
		if (x < 0 || y < 0 || x > 29 || y > 29)
			return true;
		
		int tile = tiles[x][y];
		
		if (tile == TILE_FLOOR || tile == TILE_GOAL || tile == TILE_PAD)	//balls don't move onto teleporters or shock tiles
			return false;
		
		return true;
	}
	
	public void ballFound(int binIndex)
	{
		ballFound = true;
		
		//turn off all bins but the correct one
		for (int i = 0; i < 8; i++)
		{
			if (i == binIndex)
			{
				setBinStatus(i, Arena.STATE_SUCCESS);
			}
			else
			{
				setBinStatus(i, Arena.STATE_FAILED);
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
				tiles[i][j] = TILE_FLOOR;
				
				//dim invalid goals
				if (legacyTileData[i][j] > 128)	//needed because this might be called twice for simplicity
				{
					legacyTileData[i][j] = legacyTileData[i][j] - 128;
					dimGoalTiles.add(new Point(i, j));
				}
			}
		}
	}
	
	public void clearTile(int row, int col)
	{
		tiles[row][col] = TILE_FLOOR;
		legacyTileData[row][col] = TILE_FLOOR;
	}
	
	public boolean isBallFound()
	{
		return ballFound;
	}
	
	public int getIndex()
	{
		return index;
	}
	
	public String getName()
	{
		return name;
	}
	
	//returns the inner corner of the active goal, defaulting to the lower right if it's not any of the others
	public Point getGoalFarCorner()
	{
		if (tiles[1][1] == TILE_GOAL)
			return new Point(1, 1);
		
		if (tiles[1][28] == TILE_GOAL)
			return new Point(1, 28);
		
		if (tiles[28][1] == TILE_GOAL)
			return new Point(28, 1);
		
		return new Point(28, 28);
	}

	// TODO: DEBUG
	public void printMap()
	{
		for (int i = 0; i < ARENA_DIMENSIONS; i++)
		{
			for (int j = 0; j < ARENA_DIMENSIONS; j++)
			{
//				System.out.print(tiles[i][j] + ", ");
				System.out.print(legacyTileData[i][j] + ", ");
			}

			System.out.println();
		}
	}
}
