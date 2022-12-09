package main.presentation.screens.teameditor;

import main.presentation.ImageType;
import main.presentation.common.ScreenCommand;
import main.presentation.game.StaticImage;

public class TeamEditorStartScreen extends AbstractTeamEditorSubScreen
{
	protected TeamEditorStartScreen()
	{
		super(null);
		subScreenImage = new StaticImage(ImageType.SCREEN_TEAM_EDITOR_START, screenOrigin);
		refreshContent();
	}

	@Override
	public void refreshContent()
	{
		//nothing to do here
	}

	@Override
	protected void handleCommand(ScreenCommand command)
	{
		//nothing to do here
	}
}
