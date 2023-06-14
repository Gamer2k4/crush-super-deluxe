package main.presentation.screens.popupready;

import java.awt.Point;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.game.GameText;
import main.presentation.game.sprite.StaticSprite;
import main.presentation.screens.StandardButtonScreen;

public abstract class PopupReadyScreen extends StandardButtonScreen
{
	private StaticSprite popupDialog;
	private boolean popupIsActive = false;
	
	protected PopupReadyScreen(Game sourceGame, ActionListener eventListener)
	{
		super(sourceGame, eventListener);
		
		Texture popupTexture = ImageFactory.getInstance().getTexture(getPopupDialogSourceImage());
		popupDialog = new StaticSprite(getPopupDialogOrigin(), new TextureRegion(popupTexture));
	}
	
	protected void showPopup()
	{
		popupIsActive = true;
	}
	
	protected void hidePopup()
	{
		popupIsActive = false;
	}
	
	public boolean popupIsActive()
	{
		return popupIsActive;
	}
	
	public StaticSprite getDialog()
	{
		return popupDialog;
	}
	
	public List<GameText> getPopupText()
	{
		return new ArrayList<GameText>();
	}
	
	public abstract List<ImageButton> getPopupButtons();
	protected abstract ImageType getPopupDialogSourceImage();
	protected abstract Point getPopupDialogOrigin();
}
