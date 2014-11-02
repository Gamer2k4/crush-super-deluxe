package main.presentation.teameditor.tablemodels;

import javax.swing.table.AbstractTableModel;

public class PlayerAbilityTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = -4405807385509146791L;

	private static final int COLUMN_COUNT = 9;
	private static final int ROW_COUNT = 1;

	private String[] columnNames = { "AP", "CH", "ST", "TG", "RF", "JP", "HD", "DA", "Cost" };
	private String[][] playerAbilityData = new String[ROW_COUNT][COLUMN_COUNT];

	public PlayerAbilityTableModel()
	{
		for (int i = 0; i < ROW_COUNT; i++)
		{
			for (int j = 0; j < COLUMN_COUNT - 1; j++)
			{
				playerAbilityData[i][j] = "";
			}
			
			playerAbilityData[i][COLUMN_COUNT - 1] = "000K";
		}
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

		return columnNames[column];
	}

	@Override
	public int getRowCount()
	{
		return ROW_COUNT;
	}

	@Override
	public Object getValueAt(int row, int column)
	{
		if (row < 0 || row >= ROW_COUNT || column < 0 || column >= COLUMN_COUNT)
			return null;

		return playerAbilityData[row][column];
	}

	@Override
	public void setValueAt(Object value, int row, int column)
	{
		if (row < 0 || row >= ROW_COUNT || column < 0 || column >= COLUMN_COUNT)
			return;

		playerAbilityData[row][column] = (String) value;
		
		fireTableCellUpdated(row, column);
	}
}
