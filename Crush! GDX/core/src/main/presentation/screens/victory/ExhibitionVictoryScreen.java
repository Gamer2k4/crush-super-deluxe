package main.presentation.screens.victory;

import java.awt.event.ActionListener;

import com.badlogic.gdx.Game;

import main.presentation.ImageType;
import main.presentation.common.ScreenCommand;

public class ExhibitionVictoryScreen extends AbstractVictoryScreen
{
	public ExhibitionVictoryScreen(Game sourceGame, ActionListener eventListener, ScreenCommand flowToSourceScreenCommand)
	{
		super(sourceGame, eventListener, flowToSourceScreenCommand);
	}

	@Override
	protected ImageType getVictoryScreenImageType()
	{
		return ImageType.SCREEN_VICTORY_EXHIBITION;
	}
	
}
