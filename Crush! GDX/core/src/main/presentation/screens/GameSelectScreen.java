package main.presentation.screens;

import java.awt.event.ActionListener;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import main.presentation.CursorManager;
import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.common.ScreenCommand;

public class GameSelectScreen extends GameScreen
{
	private ActionListener eventListener;
	
	public GameSelectScreen(Game sourceGame, ActionListener eventListener)
	{
		super(sourceGame);
		
		this.eventListener = eventListener;

		stage.addActor(new Image(ImageFactory.getInstance().getDrawable(ImageType.MAIN_MENU)));

//		addButton("EXHIBITION", 107, ScreenCommand.EXHIBITION_TEAM_SELECT);
		addButton("EXHIBITION", 107, ScreenCommand.BEGIN_GAME);
		addButton("TOURNAMENT", 87, ScreenCommand.TOURNAMENT_TEAM_SELECT);
		addButton("LEAGUE", 67, ScreenCommand.LEAGUE_TEAM_SELECT);
		addButton("EXIT", 47, ScreenCommand.EXIT);
	}

	private void addButton(String buttonType, int yPos, final ScreenCommand command)
	{
		ImageButton imageButton = new ImageButton(ImageFactory.getInstance().getDrawable(ImageType.valueOf("MAIN_BUTTON_" + buttonType + "_UP")), ImageFactory.getInstance().getDrawable(ImageType.valueOf("MAIN_BUTTON_" + buttonType + "_DOWN")));
		imageButton.getStyle().imageOver = ImageFactory.getInstance().getDrawable(ImageType.valueOf("MAIN_BUTTON_" + buttonType + "_DOWN"));
		imageButton.setPosition(426, yPos);
		imageButton.addListener(new InputListener()
		{
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				eventListener.actionPerformed(command.asActionEvent());
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
		});
		
		stage.addActor(imageButton);
	}
	
	@Override
	public Cursor getCursor()
	{
		return CursorManager.main();
	}
}
