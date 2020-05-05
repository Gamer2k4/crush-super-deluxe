package main.presentation.startupscreen;

import java.awt.Color;
import java.awt.event.ActionListener;

public class GameMainPanel extends AbstractStartupScreenPanel
{
	private static final long serialVersionUID = -4401310895835035591L;

	public static final String HOST_TEXT = "Host Game";
	public static final String JOIN_TEXT = "Join Game";
	public static final String EDIT_TEXT = "Team Editor";
	public static final String SETTINGS_TEXT = "Settings";
	public static final String EXIT_TEXT = "Exit";

	protected GameMainPanel(int width, int height, ActionListener actionListener)
	{
		super(width, height);
		setBackground(Color.BLACK);
		setBackgroundTint(Color.WHITE);
		addButtons(actionListener);
	}

	private void addButtons(ActionListener actionListener)
	{
		String[] buttonTexts = { HOST_TEXT, JOIN_TEXT, EDIT_TEXT, SETTINGS_TEXT, EXIT_TEXT };

		for (int i = 0; i < 5; i++)
		{
			createAndAddButton(BUTTON_START_X, BUTTON_START_Y + (i * (BUTTON_HEIGHT + 10)), buttonTexts[i], actionListener);
		}
	}

	@Override
	protected String getBgFilename()
	{
		return "gronk_main_bg.bmp";
	}

	@Override
	public void resetScreen()
	{
		return;	//nothing needs to be done, since it's just buttons
	}
}
