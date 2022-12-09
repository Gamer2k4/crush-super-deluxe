package main.presentation.screens.teameditor;

import main.presentation.ImageType;
import main.presentation.common.ScreenCommand;
import main.presentation.game.StaticImage;

public class TeamEditorDraftScreen extends AbstractTeamEditorSubScreen
{
	protected TeamEditorDraftScreen(TeamEditorParentScreen parentScreen)
	{
		super(parentScreen);
		subScreenImage = new StaticImage(ImageType.SCREEN_TEAM_EDITOR_DRAFT, screenOrigin);
		refreshContent();
	}

	@Override
	protected void refreshContent()
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void handleCommand(ScreenCommand command)
	{
		// TODO Auto-generated method stub
		
	}
}
