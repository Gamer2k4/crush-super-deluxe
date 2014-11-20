package main.presentation.teameditor.tablemodels;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import main.data.entities.Equipment;
import main.data.entities.Team;

public class OwnedEquipmentTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 8005585787101084195L;

	private static final int COLUMN_COUNT = 1;
	private static final int MAX_GEAR = Team.MAX_TEAM_SIZE * 4;

	private String[] columnNames = { "Inventory" };
	private Integer[] ownedEquipmentData = new Integer[MAX_GEAR];

	private int ownedGearCount = 0;

	public OwnedEquipmentTableModel()
	{
		refreshTable(null);
	}

	public void refreshTable(Team team)
	{
		ownedGearCount = 0;

		if (team == null)
			return;

		List<Integer> gearToDisplay = team.unassignedGear;

		for (Integer equipmentIndex : gearToDisplay)
		{
			ownedEquipmentData[ownedGearCount] = equipmentIndex;

			ownedGearCount++;
		}

		if (ownedGearCount == 0)
		{
//			ownedGearCount = 1;

			ownedEquipmentData[0] = -1;
		}
		

		fireTableDataChanged();
	}
	
	public Equipment getEquipment(int row)
	{
		if (ownedGearCount == 0)
			return Equipment.getEquipment(Equipment.EQUIP_NONE);
		
		if (row < 0)
			return Equipment.getEquipment(Equipment.EQUIP_BLANK);
		
		return Equipment.getEquipment(ownedEquipmentData[row]);
	}
	
	public Equipment removeEquipment(int row)
	{
		Equipment equip = Equipment.getEquipment(ownedEquipmentData[row]);
		
		for (int i = row; i < MAX_GEAR - 1; i++)
		{
			ownedEquipmentData[i] = ownedEquipmentData[i + 1];
		}
		
		ownedEquipmentData[MAX_GEAR - 1] = null;
		
		fireTableDataChanged();
		
		return equip;
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
		return ownedGearCount;
	}

	@Override
	public Object getValueAt(int row, int column)
	{
		if (row < 0 || row >= ownedGearCount || column < 0 || column >= COLUMN_COUNT)
			return null;

		return getEquipment(row).name;
	}

	@Override
	public void setValueAt(Object value, int row, int column)
	{
		if (row < 0 || row >= ownedGearCount || column < 0 || column >= COLUMN_COUNT)
			return;

		ownedEquipmentData[row] = (Integer) value;

		fireTableCellUpdated(row, column);
	}
}
