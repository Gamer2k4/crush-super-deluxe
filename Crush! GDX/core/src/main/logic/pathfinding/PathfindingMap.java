package main.logic.pathfinding;

public class PathfindingMap
{
	int rows;
	int columns;
	
	int[][] map;
	
	public PathfindingMap(int rows, int columns)
	{
		this.rows = rows;
		this.columns = columns;
		
		map = new int[rows][columns];
		clearMap();
	}
	
	private void clearMap()
	{
		setMapToValue(0);
	}
	
	//TODO: perhaps not performant since we're usually only working on a subset of the map, but this will be fine for now
	private void setMapToValue(int value)
	{
		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < columns; j++)
			{
				setCell(i, j, value);
			}
		}
	}

	public PathfindingMap createRawDistanceMap()
	{
		PathfindingMap pfm = new PathfindingMap(rows, columns);
		pfm.setMapToValue(Short.MAX_VALUE);
		return pfm;
	}
	
	public boolean containsCell(int row, int col)
	{
		if (row < 0 || col < 0 || row >= rows || col >= columns)
			return false;
		
		return true;
	}
	
	public void setCell(int row, int column, int value)
	{
		map[row][column] = value;
	}
	
	public int getCell(int row, int column)
	{
		return map[row][column];
	}
	
	public void setBlocked(int row, int column, boolean blocked)
	{
		map[row][column] = 0;
		
		if (blocked)
			map[row][column] = 1;	
	}
	
	public boolean isBlocked(int row, int column)
	{
		return map[row][column] != 0;
	}
	
	public int getHeight()
	{
		return rows;
	}
	
	public int getWidth()
	{
		return columns;
	}
	
	public void printMap()
	{
		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < columns; j++)
			{
				char outputChar = ' ';
				
				int value = getCell(i, j);
				
				if (value >= 0 && value < 10)
					outputChar = String.valueOf(value).charAt(0);
				else if (value > 35)
					outputChar = '#';
				else if (value > 9)
					outputChar = (char)(value + 55);
				
				System.out.print(outputChar);
			}
			
			System.out.print("\n");
		}
	}
}
