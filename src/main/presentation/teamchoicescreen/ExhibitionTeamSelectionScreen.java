package main.presentation.teamchoicescreen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import main.data.entities.Team;
import main.logic.Client;

public class ExhibitionTeamSelectionScreen extends AbstractTeamSelectionScreen
{
	private static final long serialVersionUID = -6452545844889795863L;

	private JSpinner goalSpinner;
	private JSpinner budgetSpinner;
	private JComboBox<String> paceBox;
	private JSpinner turnsSpinner;
	
	private JButton startButton;

	private static final int CONTROL_Y = 28;
	private static final Color BG_TINT = Color.blue;

	public ExhibitionTeamSelectionScreen(int width, int height, ActionListener listener)
	{
		this(new Dimension(width, height), listener);
	}

	public ExhibitionTeamSelectionScreen(Dimension dimension, ActionListener listener)
	{
		super(dimension, listener);

		setBackgroundTint(BG_TINT);

		createAndAddTitleLabel();

		for (int i = 0; i < 3; i++)
			createPlayerSlot(i);

		createControlBar();
		createButtonPanel();
	}

	private void createControlBar()
	{
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLocation(235, 415);
		optionsPanel.setSize(330, 60);
		// optionsPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		optionsPanel.setLayout(null);

		goalSpinner = createGoalSpinner();
		budgetSpinner = createBudgetSpinner();
		paceBox = createPaceComboBox();
		turnsSpinner = createTurnsSpinner();

		optionsPanel.add(goalSpinner);
		optionsPanel.add(budgetSpinner);
		optionsPanel.add(paceBox);
		optionsPanel.add(turnsSpinner);

		optionsPanel.add(createLabel("Goal:", goalSpinner));
		optionsPanel.add(createLabel("Budget:", budgetSpinner));
		optionsPanel.add(createLabel("Pace:", paceBox));
		optionsPanel.add(createLabel("Turns:", turnsSpinner));

		add(optionsPanel);
	}

	private void createButtonPanel()
	{
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLocation(655, 405);
		buttonPanel.setSize(140, 80);
		buttonPanel.setLayout(null);

		startButton = createButton(10, 10, 120, 26, "Start Game!", getDefaultStartCommand());
		buttonPanel.add(startButton);
		buttonPanel.add(createButton(10, 45, 120, 26, "Back", ACTION_TEAM_SELECT_BACK + "_" + getScreenTag()));

		add(buttonPanel);
	}

	private void createAndAddTitleLabel()
	{
		boolean isBold = true;
		int fontSize = 40;

		JLabel label = new JLabel("EXHIBITION");

		Font labelFont = label.getFont();
		label.setFont(new Font(labelFont.getName(), labelFont.getStyle(), fontSize));

		if (!isBold)
		{
			labelFont = label.getFont();
			label.setFont(labelFont.deriveFont(labelFont.getStyle() ^ Font.BOLD));
		}

		label.setForeground(BG_TINT);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);
		label.setSize(240, 50);
		label.setLocation(5, 5);

		add(label);
	}

	private JLabel createLabel(String text, Component pairedComponent)
	{
		JLabel label = new JLabel(text);

		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setSize(pairedComponent.getSize());
		label.setLocation(pairedComponent.getX(), pairedComponent.getY() - pairedComponent.getHeight());

		return label;
	}

	private JSpinner createGoalSpinner()
	{
		SpinnerModel model = new SpinnerNumberModel(1, 1, 5, 1);

		JSpinner spinner = new JSpinner(model);
		spinner.setSize(50, 20);
		spinner.setLocation(20, CONTROL_Y);
		((DefaultEditor) spinner.getEditor()).getTextField().setEditable(false);

		return spinner;
	}

	private JSpinner createBudgetSpinner()
	{
		SpinnerModel model = new SpinnerNumberModel(600, 300, 900, 100);

		JSpinner spinner = new JSpinner(model);
		spinner.setSize(50, 20);
		spinner.setLocation(90, CONTROL_Y);
		((DefaultEditor) spinner.getEditor()).getTextField().setEditable(false);

		return spinner;
	}

	private JComboBox<String> createPaceComboBox()
	{
		String[] paceNames = { "Frenzied", "Standard", "Relaxed" };

		JComboBox<String> comboBox = new JComboBox<String>(paceNames);
		comboBox.setSelectedIndex(2);
		comboBox.setSize(80, 20);
		comboBox.setLocation(160, CONTROL_Y);

		return comboBox;
	}

	private JSpinner createTurnsSpinner()
	{
		SpinnerModel model = new SpinnerNumberModel(20, 15, 25, 5);

		JSpinner spinner = new JSpinner(model);
		spinner.setSize(50, 20);
		spinner.setLocation(260, CONTROL_Y);
		((DefaultEditor) spinner.getEditor()).getTextField().setEditable(false);

		return spinner;
	}

	protected JButton createButton(int x, int y, int width, int height, String text, String command)
	{
		JButton button = new JButton(text);

		button.setSize(width, height);
		button.setLocation(x, y);
		button.addActionListener(externalListener);
		button.setActionCommand(command);

		return button;
	}
	
	private String getDefaultStartCommand()
	{
		return Client.ACTION_GAME_START + "_" + getScreenTag();
	}

	@Override
	protected void checkGameStartConditions()
	{
		readyToStart = false;
		
		int budget = getBudget();
		boolean budgetOkay = true;

		String overBudget = "";

		for (int i = 0; i < 3; i++)
		{
			Team team = playerSlots.get(i).getTeam();

			if (team.getValue() > budget)
			{
				budgetOkay = false;
				overBudget = overBudget + "\n" + team.teamName;
			}
		}
		
		int seasonWinner = getSeasonWinner();

		if (!budgetOkay)	//TODO: being over budget doesn't matter if we're already in the exhibition (that is, if players got better).
		{
			JOptionPane.showMessageDialog(null, "The following teams are over budget:" + overBudget, "Teams Over Budget",
					JOptionPane.ERROR_MESSAGE);
		} else if (seasonWinner > -1)	//TODO: extract this to the startup screen to display an actual victory screen
		{
			JOptionPane.showMessageDialog(null, "Team " + seasonWinner + " has won the exhibition!", "Exhibition Winner!",
					JOptionPane.WARNING_MESSAGE);
		} else
		{
			readyToStart = true;
		}
	}

	@Override
	protected void addPlayerSlotToPanel(int index, GamePlayerSlotHelmetPanel playerSlot)
	{
		int x = getWidth() / 2;
		int y = getHeight() / 3;

		if (index > 0)
		{
			x = getWidth() / 3;
			x *= index;
			y *= 2;
		}

		x -= playerSlot.getWidth() / 2;
		y -= playerSlot.getHeight();

		playerSlot.setLocation(x, y);
		add(playerSlot);

		repaint();
	}

	@Override
	public String getScreenTag()
	{
		return "exhib";
	}

	@Override
	protected String getBgFilename()
	{
		return "gronk_run.bmp";
	}

	@Override
	public void resetScreen()
	{
		enableControls();
		goalSpinner.getModel().setValue(1);
		budgetSpinner.getModel().setValue(600);
		paceBox.setSelectedIndex(2);
		turnsSpinner.getModel().setValue(20);

		for (GamePlayerSlotHelmetPanel playerSlot : playerSlots)
		{
			playerSlot.setTeam(new Team());
			playerSlot.resetRecord();
		}
		
		teamRecordTracker.resetRecords();
		startButton.setActionCommand(getDefaultStartCommand());
	}

	@Override
	public int getBudget()
	{
		return (Integer) budgetSpinner.getModel().getValue();
	}

	@Override
	public int getGoal()
	{
		return (Integer) goalSpinner.getModel().getValue();
	}

	@Override
	public int getSeasonWinner()
	{
		int seasonWinner = -1;
		int topScore = teamRecordTracker.getTopScore();
		
		if (topScore >= getGoal())
			seasonWinner = teamRecordTracker.getLeadingTeamIndex();
		
		return seasonWinner;
	}

	@Override
	public void disableControls()
	{
		goalSpinner.setEnabled(false);
		budgetSpinner.setEnabled(false);
		paceBox.setEnabled(false);
		turnsSpinner.setEnabled(false);
	}

	@Override
	public void enableControls()
	{
		goalSpinner.setEnabled(true);
		budgetSpinner.setEnabled(true);
		paceBox.setEnabled(true);
		turnsSpinner.setEnabled(true);
	}
}
