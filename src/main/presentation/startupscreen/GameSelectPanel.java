package main.presentation.startupscreen;

import java.awt.Color;
import java.awt.event.ActionListener;

public class GameSelectPanel extends AbstractStartupScreenPanel
{
	private static final long serialVersionUID = -3490521990419109694L;

	public static final String EXHIB_TEXT = "Exhibition";
	public static final String TOURN_TEXT = "Tournament";
	public static final String LEAGUE_TEXT = "League";
	public static final String BACK_TEXT = "Back";
	
	protected GameSelectPanel(int width, int height, ActionListener actionListener)
	{
		super(width, height);
		setBackground(Color.BLACK);
		setBackgroundTint(Color.WHITE);
		addButtons(actionListener);
	}
	
	private void addButtons(ActionListener actionListener)
	{
		String[] buttonTexts = {EXHIB_TEXT, TOURN_TEXT, LEAGUE_TEXT, BACK_TEXT}; 
		
		for (int i = 0; i < 4; i++)
		{
			createAndAddButton(BUTTON_START_X, BUTTON_START_Y + (i * (BUTTON_HEIGHT + 10)), buttonTexts[i], actionListener);
		}
	}

	@Override
	protected String getBgFilename()
	{
		return "curmian_main_bg.bmp";
	}

	@Override
	public void resetScreen()
	{
		return;	//nothing needs to be done, since it's just buttons
	}
}
