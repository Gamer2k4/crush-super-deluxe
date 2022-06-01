package main.presentation.screens;

import java.awt.event.ActionListener;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import main.presentation.CursorManager;
import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.common.Logger;
import main.presentation.common.ScreenCommand;

public abstract class StandardButtonScreen extends GameScreen implements ActionListener
{
	private ActionListener eventListener;
	
	protected StandardButtonScreen(Game sourceGame, ActionListener eventListener)
	{
		super(sourceGame);
		this.eventListener = eventListener;
	}
	
	protected ImageButton addButton(int size, int xPos, int yPos, final boolean remainPressed, final ScreenCommand command)
	{
		final ImageButton imageButton = new ImageButton(ImageFactory.getInstance().getDrawable(ImageType.valueOf("BUTTON_" + String.valueOf(size) + "x17_NORMAL")),
				ImageFactory.getInstance().getDrawable(ImageType.valueOf("BUTTON_" + String.valueOf(size) + "x17_CLICKED")),
				ImageFactory.getInstance().getDrawable(ImageType.valueOf("BUTTON_" + String.valueOf(size) + "x17_CLICKED")));
		
		imageButton.setPosition(xPos, yPos);
		imageButton.addListener(new InputListener()
		{
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				StandardButtonScreen.this.actionPerformed(command.asActionEvent());
				eventListener.actionPerformed(command.asActionEvent());
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				//TODO: for some reason this sounds staticy
//				AudioManager.getInstance().playSound(SoundType.BUTTON);
				
				if (remainPressed)
					imageButton.setChecked(true);
				
				Logger.output("Checked status: " + imageButton.isChecked());
				
				return true;
			}
		});
		
		return imageButton;
	}
	
	@Override
	public Cursor getCursor()
	{
		return CursorManager.crush();
	}
	
	@Override
	public void reset()
	{
		for (ImageButton button : getButtons())
		{
			stage.addActor(button);
		}
	}
	
	protected abstract List<ImageButton> getButtons();
}
