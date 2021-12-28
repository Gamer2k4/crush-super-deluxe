package main.presentation.screens;

import java.awt.event.ActionListener;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import main.presentation.CursorManager;
import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.common.ScreenCommand;

public abstract class MouseOverButtonScreen extends GameScreen
{
	private ActionListener eventListener;
	protected boolean screenIsReady = false;
	
	protected MouseOverButtonScreen(Game sourceGame, ActionListener eventListener)
	{
		super(sourceGame);
		this.eventListener = eventListener;
	}

	protected void addButton(String buttonType, int xPos, int yPos, final ScreenCommand command)
	{
		ImageButton imageButton = new ImageButton(ImageFactory.getInstance().getDrawable(ImageType.valueOf(buttonType + "_UP")), ImageFactory.getInstance().getDrawable(ImageType.valueOf(buttonType + "_DOWN")));
		imageButton.getStyle().imageOver = ImageFactory.getInstance().getDrawable(ImageType.valueOf(buttonType + "_DOWN"));
		imageButton.setPosition(xPos, yPos);
		imageButton.addListener(new InputListener()
		{
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				if (screenIsReady)
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
