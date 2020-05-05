package main.presentation.startupscreen;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import main.presentation.common.GameSettings;
import main.presentation.game.PresentationMode;

public class SettingsPanel extends AbstractStartupScreenPanel implements ActionListener
{
	private static final long serialVersionUID = 6136885128247165799L;

	public static final String SAVE_TEXT = "Save";
	public static final String SET_DIRECTORY_ACTION = "directory";
	public static final String CURSES_MODE = "ASCII Characters";
	public static final String LEGACY_MODE = "Legacy Graphics";

	private JTextField gameDirectoryInput;
	private JFileChooser directoryChooser;
	private JRadioButton cursesModeButton;
	private JRadioButton legacyModeButton;

	protected SettingsPanel(int width, int height, ActionListener actionListener)
	{
		super(width, height);
		setBackground(Color.BLACK);
		setBackgroundTint(Color.WHITE);
		addButtons(actionListener);
		addDirectoryControls();
		addPresentationModeControls();
		resetScreen();
	}

	private void addButtons(ActionListener actionListener)
	{
		String[] buttonTexts = { SAVE_TEXT, GameSelectPanel.BACK_TEXT };

		for (int i = 3; i < 5; i++)
		{
			JButton button = createAndAddButton(BUTTON_START_X, BUTTON_START_Y + (i * (BUTTON_HEIGHT + 10)), buttonTexts[i - 3], actionListener);
			button.addActionListener(this);
		}
	}

	private void addDirectoryControls()
	{
		gameDirectoryInput = new JTextField();
		gameDirectoryInput.setSize(350, 20);
		gameDirectoryInput.setLocation(75, 75);
		add(gameDirectoryInput);

		JLabel directoryLabel = new JLabel("Crush! Installation Directory:");
		directoryLabel.setForeground(Color.WHITE);
		directoryLabel.setSize(175, 20);
		directoryLabel.setLocation(gameDirectoryInput.getX(), gameDirectoryInput.getY() - directoryLabel.getHeight());
		add(directoryLabel);

		JButton directoryChooserButton = new JButton("...");
		directoryChooserButton.setSize(20, 20);
		directoryChooserButton.setLocation(gameDirectoryInput.getX() + gameDirectoryInput.getWidth() + 5, gameDirectoryInput.getY());
		directoryChooserButton.setActionCommand(SET_DIRECTORY_ACTION);
		directoryChooserButton.addActionListener(this);
		add(directoryChooserButton);
		
		directoryChooser = new JFileChooser();
		directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}

	private void addPresentationModeControls()
	{
		JLabel presentationModeLabel = new JLabel("Display Mode:");
		presentationModeLabel.setForeground(Color.WHITE);
		presentationModeLabel.setSize(175, 20);
		presentationModeLabel.setLocation(gameDirectoryInput.getX(), gameDirectoryInput.getY() + 45);
		add(presentationModeLabel);

		cursesModeButton = new JRadioButton(CURSES_MODE);
		cursesModeButton.setLocation(10, 5);
		cursesModeButton.setSize(130, 20);
		cursesModeButton.addActionListener(this);
		cursesModeButton.setSelected(true);

		legacyModeButton = new JRadioButton(LEGACY_MODE);
		legacyModeButton.setLocation(cursesModeButton.getLocation().x + cursesModeButton.getWidth() + 20, cursesModeButton.getLocation().y);
		legacyModeButton.setSize(120, 20);
		legacyModeButton.addActionListener(this);

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(cursesModeButton);
		buttonGroup.add(legacyModeButton);

		JPanel presentationModePanel = new JPanel();
		presentationModePanel.setLayout(null);
		presentationModePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		presentationModePanel.setSize(300, 30);
		presentationModePanel.setLocation(presentationModeLabel.getX(), presentationModeLabel.getY() + presentationModeLabel.getHeight());

		presentationModePanel.add(cursesModeButton);
		presentationModePanel.add(legacyModeButton);

		add(presentationModePanel);
	}

	@Override
	protected String getBgFilename()
	{
		return "bg_settings_screen.bmp";
	}

	@Override
	public void resetScreen()
	{
		gameDirectoryInput.setText(GameSettings.getRootDirectory());
		
		if (GameSettings.getPresentationMode().equals(PresentationMode.CURSES))
		{
			cursesModeButton.setSelected(true);
			legacyModeButton.setSelected(false);
		}
		else if (GameSettings.getPresentationMode().equals(PresentationMode.LEGACY))
		{
			cursesModeButton.setSelected(false);
			legacyModeButton.setSelected(true);
		}
		
		return;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		
		if (SAVE_TEXT.equals(command))
			saveSettings();
		else if (GameSelectPanel.BACK_TEXT.equals(command))
			resetScreen();
		
		if (SET_DIRECTORY_ACTION.equals(command))
		{
			directoryChooser.showOpenDialog(this);
			File file = directoryChooser.getSelectedFile();
			
			if (file != null)
				gameDirectoryInput.setText(file.getAbsolutePath());
		}
	}

	private void saveSettings()
	{
		GameSettings.setRootDirectory(gameDirectoryInput.getText());
		
		if (cursesModeButton.isSelected())
			GameSettings.setPresentationMode(PresentationMode.CURSES);
		if (legacyModeButton.isSelected())
			GameSettings.setPresentationMode(PresentationMode.LEGACY);
		
		//TODO: write to a file
		//when first starting the game, check for a settings file in the current directory.  if it exists, fill GameSettings with its value.
		//if not, create the file and set the initial directory to the one the game is being run from
		//if the crush files cannot be found, throw an error and force the user to pick the directory or exit the game
	}
}
