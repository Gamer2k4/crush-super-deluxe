package main.presentation.screens.teameditor;

import main.presentation.ImageType;
import main.presentation.common.ScreenCommand;
import main.presentation.game.StaticImage;

public class TeamEditorDetailedRoster extends AbstractTeamEditorRosterScreen
{
	protected TeamEditorDetailedRoster(TeamEditorParentScreen parentScreen)
	{
		super(parentScreen);
		subScreenImage = new StaticImage(ImageType.SCREEN_TEAM_EDITOR_ROSTER_DETAILED, screenOrigin);
	}

	@Override
	protected void handleCommand(ScreenCommand command)
	{
		// TODO Auto-generated method stub
		
	}
}
