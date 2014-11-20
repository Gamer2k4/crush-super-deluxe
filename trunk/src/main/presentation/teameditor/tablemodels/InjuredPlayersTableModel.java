package main.presentation.teameditor.tablemodels;

import javax.swing.table.AbstractTableModel;

import main.data.entities.Player;
import main.data.entities.Team;
import main.presentation.teameditor.utils.GUIPlayerAttributes;

public class InjuredPlayersTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 2867253032894146773L;

	private static final int COLUMN_COUNT = 3;
	private static final int MAX_PLAYERS = Team.MAX_TEAM_SIZE;

	private String[] columnNames = { "", "Name", "Weeks Out" };
	private String[][] playerInjuryData = new String[MAX_PLAYERS][COLUMN_COUNT];
	
	private int injuredPlayerCount = 0;

	public InjuredPlayersTableModel()
	{
		refreshTable(null);
	}
	
	public void refreshTable(Team team)
	{
		injuredPlayerCount = 0;
		
		if (team == null)
			return;
		
		for (int i = 0; i < MAX_PLAYERS; i++)
		{
			Player p = team.getPlayer(i);
			
			if (p != null && p.getInjuryType() != Player.INJURY_NONE)
			{
				playerInjuryData[injuredPlayerCount][0] = GUIPlayerAttributes.getIndexString(i);
				playerInjuryData[injuredPlayerCount][1] = GUIPlayerAttributes.getNameBlank(p);
				playerInjuryData[injuredPlayerCount][2] = GUIPlayerAttributes.getWeeksOut(p);
				fireTableDataChanged();
				
				injuredPlayerCount++;
			}
		}
		
		if (injuredPlayerCount == 0)
		{
			injuredPlayerCount = 1;
			
			playerInjuryData[0][0] = "";
			playerInjuryData[0][1] = "";
			playerInjuryData[0][2] = "";

			fireTableDataChanged();
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
		return injuredPlayerCount;
	}

	@Override
	public Object getValueAt(int row, int column)
	{
		if (row < 0 || row >= injuredPlayerCount || column < 0 || column >= COLUMN_COUNT)
			return null;

		return playerInjuryData[row][column];
	}

	@Override
	public void setValueAt(Object value, int row, int column)
	{
		if (row < 0 || row >= injuredPlayerCount || column < 0 || column >= COLUMN_COUNT)
			return;

		playerInjuryData[row][column] = (String) value;

		fireTableCellUpdated(row, column);
	}
}