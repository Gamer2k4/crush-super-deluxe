package main.presentation.screens;

import java.awt.event.ActionListener;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import main.presentation.CursorManager;
import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.common.Logger;
import main.presentation.common.ScreenCommand;

public abstract class StandardButtonScreen extends GameScreen implements ActionListener
{
	private Texture blackTexture = ImageFactory.getInstance().getTexture(ImageType.BLACK_SCREEN);
	
	protected ActionListener eventListener;
	
	protected StandardButtonScreen(Game sourceGame, ActionListener eventListener)
	{
		super(sourceGame);
		this.eventListener = eventListener;
	}
	
	protected ImageButton addClickZone(int xPos, int yPos, int width, int height, final ScreenCommand command)
	{
		return addButton(xPos, yPos, width, height, false, command, false);
	}
	
	protected ImageButton addButton(int size, int xPos, int yPos, final boolean remainPressed, final ScreenCommand command)
	{
		return addButton(xPos, yPos, size, 17, remainPressed, command, true);
	}
	
	protected ImageButton addButton(int xPos, int yPos, int width, int height, final boolean remainPressed, final ScreenCommand command, final boolean isVisible)
	{
		Logger.debug("Adding button with command " + command);
		
		final ImageButton imageButton;
		
		if (isVisible)
		{
			imageButton = new ImageButton(
				ImageFactory.getInstance().getDrawable(ImageType.valueOf("BUTTON_" + String.valueOf(width) + "x"+ String.valueOf(height) + "_NORMAL")),
				ImageFactory.getInstance().getDrawable(ImageType.valueOf("BUTTON_" + String.valueOf(width) + "x"+ String.valueOf(height) + "_CLICKED")),
				ImageFactory.getInstance().getDrawable(ImageType.valueOf("BUTTON_" + String.valueOf(width) + "x"+ String.valueOf(height) + "_CLICKED")));
		}
		else
		{
			Drawable buttonImage = new TextureRegionDrawable(new TextureRegion(blackTexture, 0, 0, width, height));
			imageButton = new ImageButton(buttonImage);
			imageButton.setColor(0, 0, 0, 0);
		}
		
		imageButton.setPosition(xPos, 400 - yPos - height);
		imageButton.addListener(new InputListener()
		{
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				if (!remainPressed)
					imageButton.setChecked(false);
				
				StandardButtonScreen.this.actionPerformed(command.asActionEvent());
				eventListener.actionPerformed(command.asActionEvent());
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				//TODO: for some reason this sounds staticy
				//		also it shouldn't trigger if the button isn't visible
//				AudioManager.getInstance().playSound(SoundType.BUTTON);
				
				Logger.debug("Button clicked at (" + x + ", " + y + ") with command " + command);
				
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
