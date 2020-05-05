package main.presentation.teamchoicescreen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import main.data.entities.Team;
import main.presentation.teamcolorpanel.TeamHelmetColorPanel;

public class GamePlayerSlotHelmetPanel extends JPanel implements MouseListener
{
	private static final long serialVersionUID = 832724359504362680L;

	private TeamHelmetColorPanel helmetPanel;
	private Team selectedTeam;
	
	private JLabel teamNameLabel;
	private JLabel teamRecordLabel;
	private JLabel teamRecordValue;
	
	private JButton helmetClickTrigger;
	
	public static final String CHOOSE_TEAM = "CHOOSE_TEAM_";
	
	private final int RECORD_LABEL_WIDTH = 40;
	
	public GamePlayerSlotHelmetPanel(int index, AbstractTeamSelectionScreen source, ActionListener externalListener)
	{
		this(new Team(), index, source, externalListener);
	}
	
	public GamePlayerSlotHelmetPanel(Team team, int index, AbstractTeamSelectionScreen source, ActionListener externalListener)
	{	
		DecimalFormat myFormatter = new DecimalFormat("00");
		String command = CHOOSE_TEAM + myFormatter.format(index) + "_" + source.getScreenTag();
		
		defineHelmetClickTrigger(command, externalListener);
		
		helmetPanel = new TeamHelmetColorPanel(this.getBackground());
		helmetPanel.addMouseListener(this);
		
		createPanel();
		
		setTeam(team);
	}
	
	private void createPanel()
	{
		Dimension helmetDimension = helmetPanel.getSize();
		helmetDimension.height += 3;
		helmetDimension.width = 230;
		
		//TODO: set up additional controls (team name, radio buttons, ready checkbox, clear button, etc.
		//but for now, this is all we need
		setSize(helmetDimension);
		setMinimumSize(helmetDimension);
		setMaximumSize(helmetDimension);
		setPreferredSize(helmetDimension);
//		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		//setBackground(Color.BLACK);
		setLayout(null);
		
		helmetPanel.setLocation(1, 1);
		add(helmetPanel);
		
		teamNameLabel = createRecordLabel(new JLabel(""), 16, true);
		//teamNameLabel.setForeground(Color.WHITE);
		teamNameLabel.setSize(150, 25);
		teamNameLabel.setLocation(helmetPanel.getWidth() + 10, (helmetPanel.getHeight() / 2) - 15);
		add(teamNameLabel);
		
		teamRecordLabel = createRecordLabel(new JLabel("W/L/T: "), 12, true);
		teamRecordLabel.setLocation(helmetPanel.getWidth() + 10, (helmetPanel.getHeight() / 2) + 5);
		teamRecordLabel.setVisible(false);
		add(teamRecordLabel);
		
		teamRecordValue = createRecordLabel(new JLabel("0/0/0"), 12, false);
		teamRecordValue.setLocation(helmetPanel.getWidth() + 10 + RECORD_LABEL_WIDTH, (helmetPanel.getHeight() / 2) + 5);
		teamRecordValue.setVisible(false);
		add(teamRecordValue);
		
		helmetPanel.setMainColor(Color.GREEN);
		
		repaint();
	}
	
	private JLabel createRecordLabel(JLabel label, int fontSize, boolean isBold)
	{
		Font labelFont = label.getFont();
		label.setFont(new Font(labelFont.getName(), labelFont.getStyle(), fontSize));

		if (!isBold)
		{
			labelFont = label.getFont();
			label.setFont(labelFont.deriveFont(labelFont.getStyle() ^ Font.BOLD));
		}

		label.setHorizontalTextPosition(SwingConstants.LEFT);
		label.setSize(RECORD_LABEL_WIDTH, 25);
		
		return label;
	}
	
	private void defineHelmetClickTrigger(String command, ActionListener externalListener)
	{
		helmetClickTrigger = new JButton();
		helmetClickTrigger.setActionCommand(command);
		helmetClickTrigger.addActionListener(externalListener);
	}
	
	public void setTeam(Team team)
	{
		selectedTeam = team;
		
		helmetPanel.setMainColor(selectedTeam.teamColors[0]);
		helmetPanel.setTrimColor(selectedTeam.teamColors[1]);
		teamNameLabel.setText(selectedTeam.teamName.toUpperCase());
	}
	
	public Team getTeam()
	{
		return selectedTeam;
	}
	
	public void resetRecord()
	{
		teamRecordLabel.setVisible(false);
		teamRecordValue.setVisible(false);
		
		teamRecordValue.setText("0/0/0");
	}
	
	public void updateRecord(int[] record)
	{
		teamRecordLabel.setVisible(true);
		teamRecordValue.setVisible(true);
		
		teamRecordValue.setText(record[0] + "/" + record[1] + "/" + record[2]);
	}

	@Override
	public void mouseClicked(MouseEvent event)
	{
		Component eventComponent = event.getComponent();

		if (eventComponent.getClass() != TeamHelmetColorPanel.class)
			return;
		
		helmetClickTrigger.doClick();
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}
}
