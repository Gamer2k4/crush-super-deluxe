package main.presentation.teameditor;

import javax.swing.table.AbstractTableModel;

public class RosterTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = -4405807385509146791L;

	private static final int COLUMN_COUNT = 5;
	private static final int MAX_PLAYERS = 35;

	private String[] columnNames = { "", "Name", "Rank", "Race", "Value" };
	private String[][] playerData = new String[MAX_PLAYERS][COLUMN_COUNT];

	public RosterTableModel()
	{
		for (int i = 0; i < MAX_PLAYERS; i++)
		{
			if (i < 9)
				playerData[i][0] = String.valueOf(i + 1);
			else
				playerData[i][0] = String.valueOf((char) (i + 56));

			playerData[i][1] = "EMPTY";
			playerData[i][4] = "000K";

			for (int j = 2; j < 4; j++)
				playerData[i][j] = "";
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int column)
	{
		if (column == 1)
			return true;
		
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

		return columnNames[column];
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
