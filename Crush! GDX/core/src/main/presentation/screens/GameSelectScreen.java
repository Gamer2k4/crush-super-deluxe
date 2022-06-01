package main.presentation.screens;

import java.awt.event.ActionListener;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.common.ScreenCommand;

public class GameSelectScreen extends MouseOverButtonScreen
{
	public GameSelectScreen(Game sourceGame, ActionListener eventListener)
	{
		super(sourceGame, eventListener);
		screenIsReady = true;

		stage.addActor(new Image(ImageFactory.getInstance().getDrawable(ImageType.MAIN_MENU)));

		//TODO: these should all make SoundType.WINDOW, but it's tricky because it only happens from this screen
		//		that is, returning from post-game stats to the exhibition screen doesn't make the sound
		//		but going to stats from the end of game does
		addButton("MAIN_BUTTON_EXHIBITION", 426, 107, ScreenCommand.EXHIBITION_TEAM_SELECT);
		addButton("MAIN_BUTTON_TOURNAMENT", 426, 87, ScreenCommand.TOURNAMENT_TEAM_SELECT);
		addButton("MAIN_BUTTON_LEAGUE", 426, 67, ScreenCommand.LEAGUE_TEAM_SELECT);
		addButton("MAIN_BUTTON_EXIT", 426, 47, ScreenCommand.EXIT);
	}
}
