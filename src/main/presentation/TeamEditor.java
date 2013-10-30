package main.presentation;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

//TODO: get rid of all "magic numbers" regarding component dimensions
public class TeamEditor extends JFrame
{
	private static final long serialVersionUID = -4815838996578005455L;

	private JTabbedPane mainPane;
	private JPanel sidePane;
	
	private JPanel teamPane;
	private JScrollPane rosterPane;
	private JPanel controlPane;

	private JPanel settingsPane;
	private JPanel equipmentPane;
	private JPanel rostersPane;
	private JPanel statsPane;
	private JPanel draftPane;
	private JPanel docbotPane;
	private JPanel trainerPane;
	private JPanel schedulePane;

	public TeamEditor()
	{
		setTitle("Crush! Super Deluxe Team Editor");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(800, 500));
		setResizable(false);

		mainPane = createMainPane();
		sidePane = createSidePane();

		Container contentPane = getContentPane();
		contentPane.add(mainPane, BorderLayout.WEST);
		contentPane.add(sidePane, BorderLayout.EAST);

		pack();
		setVisible(true);
	}
	
	private JPanel createSidePane()
	{
		teamPane = createTeamPane();
		rosterPane = createRosterPane();
		controlPane = createControlPane();
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		panel.add(teamPane);
		panel.add(rosterPane);
		panel.add(controlPane);

		return panel;
	}

	private JPanel createTeamPane()
	{
		JPanel panel = new JPanel();
		
		// TODO Auto-generated method stub
		
		return panel;
	}

	private JScrollPane createRosterPane()
	{
		JTable rosterTable = new JTable(new RosterTableModel());
		formatTableColumns(rosterTable);

		JScrollPane scrollPane = new JScrollPane(rosterTable);
		scrollPane.setMaximumSize(new Dimension(250, 310));
		scrollPane.setPreferredSize(new Dimension(250, 310));
		
		return scrollPane;
	}

	private JPanel createControlPane()
	{
		JPanel panel = new JPanel();
		
		// TODO Auto-generated method stub
		
		return panel;
	}

	private JTabbedPane createMainPane()
	{
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
		tabbedPane.setMinimumSize(new Dimension(550, 500));
		tabbedPane.setPreferredSize(new Dimension(550, 500));

		settingsPane = createSettingsPane();
		equipmentPane = createEquipmentPane();
		rostersPane = createRostersPane();
		statsPane = createStatsPane();
		draftPane = createDraftPane();
		docbotPane = createDocbotPane();
		trainerPane = createTrainerPane();
		schedulePane = createSchedulePane();

		tabbedPane.add("Settings", settingsPane);
		tabbedPane.add("Equipment", equipmentPane);
		tabbedPane.add("Rosters", rostersPane);
		tabbedPane.add("Stats", statsPane);
		tabbedPane.add("Draft", draftPane);
		tabbedPane.add("Docbot", docbotPane);
		tabbedPane.add("Trainer", trainerPane);
		tabbedPane.add("Schedule", schedulePane);

		return tabbedPane;
	}

	private JPanel createSettingsPane()
	{
		JPanel panel = new JPanel();

		// TODO Auto-generated method stub

		return panel;
	}

	private JPanel createEquipmentPane()
	{
		JPanel panel = new JPanel();

		// TODO Auto-generated method stub

		return panel;
	}

	private JPanel createRostersPane()
	{
		JPanel panel = new JPanel();

		// TODO Auto-generated method stub

		return panel;
	}

	private JPanel createStatsPane()
	{
		JPanel panel = new JPanel();

		// TODO Auto-generated method stub

		return panel;
	}

	private JPanel createDraftPane()
	{
		JPanel panel = new JPanel();

		// TODO Auto-generated method stub

		return panel;
	}

	private JPanel createDocbotPane()
	{
		JPanel panel = new JPanel();

		// TODO Auto-generated method stub

		return panel;
	}

	private JPanel createTrainerPane()
	{
		JPanel panel = new JPanel();

		// TODO Auto-generated method stub

		return panel;
	}

	private JPanel createSchedulePane()
	{
		JPanel panel = new JPanel();

		// TODO Auto-generated method stub

		return panel;
	}

	private void formatTableColumns(JTable rosterTable)
	{
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);

		TableColumn column = null;
		for (int i = 0; i < 5; i++)
		{
			column = rosterTable.getColumnModel().getColumn(i);
			if (i == 0)
			{
				column.setMaxWidth(5);
				column.setCellRenderer(centerRenderer);
			} else if (i == 4)
			{
				column.setMaxWidth(50);
			} else
			{
				column.setMaxWidth(80);
			}
		}
	}

	private class RosterTableModel extends AbstractTableModel
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
		}
	};
}
