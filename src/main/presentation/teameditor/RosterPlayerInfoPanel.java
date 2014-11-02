package main.presentation.teameditor;

import java.awt.Font;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import main.data.entities.Player;
import main.presentation.teameditor.tablemodels.PlayerAbilityTableModel;
import main.presentation.teameditor.utils.GUIPlayerAttributes;

public class RosterPlayerInfoPanel extends AbstractPlayerInfoPanel
{
	private static final long serialVersionUID = -3483400758355426073L;
	
	private JLabel playerNameLabel;
	private JLabel playerRankLabel;
	private JLabel playerSeasonsLabel;
	private JLabel playerRatingLabel;
	private JLabel playerStatusLabel;
	private JLabel playerSkillPointsLabel;
	private JTable playerAbilityTable;
	private JTextArea playerSkills;
	private JScrollPane skillListPane;
	private JTextArea playerQuirks;
	private JScrollPane quirkListPane;
	
	public RosterPlayerInfoPanel(int imageSize, int labelWidth, int labelHeight)
	{
		super(imageSize, labelWidth, labelHeight, 12);

		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		setLayout(null);

		playerNameLabel = setLabelFontSize(new JLabel(), 18, true);

		playerNameLabel.setLocation(20, 5);
		playerNameLabel.setSize(playerImageSize, 20);
		add(playerNameLabel);

		JLabel rankTextLabel = createPlayerPaneTextLabel("Rank", playerNameLabel);
		rankTextLabel.setLocation(playerImageSize + 10, 5);
		add(rankTextLabel);

		playerRankLabel = createPlayerPaneValueLabel(rankTextLabel);
		add(playerRankLabel);

		JLabel seasonsTextLabel = createPlayerPaneTextLabel("Seasons", playerRankLabel);
		add(seasonsTextLabel);

		playerSeasonsLabel = createPlayerPaneValueLabel(seasonsTextLabel);
		add(playerSeasonsLabel);

		JLabel ratingTextLabel = createPlayerPaneTextLabel("Rating", playerSeasonsLabel);
		add(ratingTextLabel);

		playerRatingLabel = createPlayerPaneValueLabel(ratingTextLabel);
		add(playerRatingLabel);

		JLabel statusTextLabel = createPlayerPaneTextLabel("Status", playerRatingLabel);
		add(statusTextLabel);

		playerStatusLabel = createPlayerPaneValueLabel(statusTextLabel);
		add(playerStatusLabel);

		JLabel skillPointsTextLabel = createPlayerPaneTextLabel("Skill Points", playerStatusLabel);
		add(skillPointsTextLabel);

		playerSkillPointsLabel = createPlayerPaneValueLabel(skillPointsTextLabel);
		add(playerSkillPointsLabel);

		imagePanel.setLocation(5, 25);
		add(imagePanel);

		JScrollPane playerAbilityPane = createPlayerAbilityTablePane();
		playerAbilityPane.setLocation(20, 185);
		playerAbilityPane.setSize(255, 39);
		add(playerAbilityPane);

		JLabel skillsTextLabel = createPlayerPaneTextLabel("Skills", playerStatusLabel);
		skillsTextLabel.setLocation(10, 230);
		skillsTextLabel.setHorizontalAlignment(SwingConstants.LEFT);
		add(skillsTextLabel);

		playerSkills = new JTextArea("");
		playerSkills.setEditable(false);
		playerSkills.setLineWrap(true);
		playerSkills.setWrapStyleWord(true);
		playerSkills.setFont(new Font("Sans-Serif", Font.PLAIN, 10));

		skillListPane = new JScrollPane(playerSkills);
		skillListPane.setLocation(10, 245);
		skillListPane.setSize(136, 50);
		skillListPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(skillListPane);

		JLabel quirksTextLabel = createPlayerPaneTextLabel("Quirks", playerStatusLabel);
		quirksTextLabel.setLocation(150, 230);
		quirksTextLabel.setHorizontalAlignment(SwingConstants.LEFT);
		add(quirksTextLabel);

		playerQuirks = new JTextArea("");
		playerQuirks.setEditable(false);
		playerQuirks.setLineWrap(true);
		playerQuirks.setWrapStyleWord(true);
		playerQuirks.setFont(new Font("Sans-Serif", Font.PLAIN, 10));

		quirkListPane = new JScrollPane(playerQuirks);
		quirkListPane.setLocation(150, 245);
		quirkListPane.setSize(136, 50);
		quirkListPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(quirkListPane);
	}

	private JScrollPane createPlayerAbilityTablePane()
	{
		playerAbilityTable = new JTable(new PlayerAbilityTableModel());
		formatPlayerAbilityTableColumns(playerAbilityTable);
		playerAbilityTable.setEnabled(false);
		playerAbilityTable.getTableHeader().setReorderingAllowed(false);
		playerAbilityTable.getTableHeader().setResizingAllowed(false);

		return new JScrollPane(playerAbilityTable);
	}

	private void formatPlayerAbilityTableColumns(JTable unformattedPlayerAbilityTable)
	{
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

		// TODO: figure out how to make this truly centered
		// ((DefaultTableCellRenderer) playerAbilityTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

		TableColumn column = null;
		for (int i = 0; i < 9; i++)
		{
			column = unformattedPlayerAbilityTable.getColumnModel().getColumn(i);

			column.setCellRenderer(centerRenderer);

			if (i == 8)
			{
				column.setMaxWidth(45);
				column.setMinWidth(45);
			} else
			{
				column.setMaxWidth(26);
				column.setMinWidth(26);
			}
		}
	}

	public void updatePanel(Player player, int currentPlayerIndex, BufferedImage playerImage)
	{
		String index = "";

		if (currentPlayerIndex < 9)
			index = String.valueOf(currentPlayerIndex + 1);
		else
			index = String.valueOf((char) (currentPlayerIndex + 56));

		String name = GUIPlayerAttributes.getNameBlank(player);
		String rank = GUIPlayerAttributes.getRank(player);
		String seasons = GUIPlayerAttributes.getSeasons(player);
		String rating = GUIPlayerAttributes.getRating(player);
		String status = GUIPlayerAttributes.getStatus(player);
		String skillPoints = GUIPlayerAttributes.getSkillPoints(player);
		String value = GUIPlayerAttributes.getValue(player);
		String skills = GUIPlayerAttributes.getSkills(player);
		String quirks = GUIPlayerAttributes.getQuirks(player);

		playerNameLabel.setText(index + ") " + name);
		playerRankLabel.setText(rank);
		playerSeasonsLabel.setText(seasons);
		playerRatingLabel.setText(rating);
		playerStatusLabel.setText(status);
		playerSkillPointsLabel.setText(skillPoints);

		setRosterImage(player, playerImage);

		for (int i = 0; i < 8; i++)
		{
			String attribute = GUIPlayerAttributes.getAttribute(player, i);
			playerAbilityTable.getModel().setValueAt(attribute, 0, i);
		}

		playerAbilityTable.getModel().setValueAt(value, 0, 8);

		playerSkills.setText(skills);
		playerSkills.setCaretPosition(0);
		playerQuirks.setText(quirks);
		playerQuirks.setCaretPosition(0);
	}
}
