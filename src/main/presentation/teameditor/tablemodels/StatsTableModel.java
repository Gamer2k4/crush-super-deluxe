package main.presentation.teameditor.tablemodels;

import javax.swing.table.AbstractTableModel;

import main.data.entities.Team;

public class StatsTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 8775877124042751328L;

	public static final int VIEW_RUSH = 0;
	public static final int VIEW_CHECKING = 1;
	public static final int VIEW_CARNAGE = 2;
	public static final int VIEW_MISC = 3;

	private static final int COLUMN_COUNT = 6;
	private static final int MAX_PLAYERS = Team.MAX_TEAM_SIZE;

	private int currentView = VIEW_RUSH;

	// private String[][] columnNames = { { "Name", "Race", "Rushing Attempts", "Rushing Tiles", "Rushing Average", "Goals Scored" },
	// { "Name", "Race", "Checks Thrown", "Checks Landed", "Checking Average", "Sacks For" },
	// { "Name", "Race", "Injuries For", "Kills For", "Total For", "" },
	// { "Name", "Race", "Average Rating", "Highest Rating", "", "" } };

	private String[][] columnNames = {
			{ "<html><center>Name<br>&nbsp;</center></html>", "<html><center>Race<br>&nbsp;</center></html>",
					"<html><center>Rushing<br>Attempts</center></html>", "<html><center>Rushing<br>Tiles</center></html>",
					"<html><center>Rushing<br>Average</center></html>", "<html><center>Goals<br>Scored</center></html>" },
			{ "<html><center>Name<br>&nbsp;</center></html>", "<html><center>Race<br>&nbsp;</center></html>",
					"<html><center>Checks<br>Thrown</center></html>", "<html><center>Checks<br>Landed</center></html>",
					"<html><center>Checking<br>Average</center></html>", "<html><center>Sacks<br>For</center></html>" },
			{ "<html><center>Name<br>&nbsp;</center></html>", "<html><center>Race<br>&nbsp;</center></html>",
					"<html><center>Injuries<br>For</center></html>", "<html><center>Kills<br>For</center></html>",
					"<html><center>Total<br>For</center></html>", "" },
			{ "<html><center>Name<br>&nbsp;</center></html>", "<html><center>Race<br>&nbsp;</center></html>",
					"<html><center>Average<br>Rating</center></html>", "<html><center>Highest<br>Rating</center></html>", "", "" } };

	private String[][] playerData = new String[MAX_PLAYERS][COLUMN_COUNT];

	public StatsTableModel()
	{
		clearTable();
	}
	
	public void clearTable()
	{
		for (int i = 0; i < MAX_PLAYERS; i++)
		{
			for (int j = 0; j < COLUMN_COUNT; j++)
				playerData[i][j] = "";
		}
	}

	public void setView(int newView)
	{
		currentView = newView;
		fireTableStructureChanged();
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}

	@Override
	public Class<?> getColumnClass(int arg0)
	{
		return String.class;
	}

	@Override
	public int getColumnCount()
	{
		return COLUMN_COUNT;
	}

	@Override
	public String getColumnName(int column)
	{
		if (column < 0 || column >= COLUMN_COUNT)
			return "";

		return columnNames[currentView][column];
	}

	@Override
	public int getRowCount()
	{
		return MAX_PLAYERS;
	}

	@Override
	public Object getValueAt(int row, int column)
	{
		if (row < 0 || row >= MAX_PLAYERS || column < 0 || column >= COLUMN_COUNT)
			return null;

		return playerData[row][column];
	}

	@Override
	public void setValueAt(Object value, int row, int column)
	{
		if (row < 0 || row >= MAX_PLAYERS || column < 0 || column >= COLUMN_COUNT)
			return;

		playerData[row][column] = (String) value;

		fireTableCellUpdated(row, column);
	}
}
