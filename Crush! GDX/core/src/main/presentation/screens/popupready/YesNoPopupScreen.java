package main.presentation.screens.popupready;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.common.ScreenCommand;
import main.presentation.game.GameText;
import main.presentation.legacy.common.LegacyUiConstants;

public class YesNoPopupScreen extends PopupReadyScreen
{
	private List<ImageButton> buttons = new ArrayList<ImageButton>();
	
	private GameText popupText = null;
	
	protected YesNoPopupScreen(Game sourceGame, ActionListener eventListener)
	{
		super(sourceGame, eventListener);
		
		ImageButton yesButton = addButton(72, 242, 213, false, ScreenCommand.POPUP_YES);
		ImageButton noButton = addButton(72, 326, 213, false, ScreenCommand.POPUP_NO);
		buttons.add(yesButton);
		buttons.add(noButton);
	}
	
	protected void showPopup(String text)
	{
		int startX = GameText.getStringStartX(GameText.small, 204, ImageFactory.getInstance().getImageWidth(ImageType.POPUP_DIALOG_BLANK_PROMPT), text);
		int startY = 176;
		popupText = GameText.small(new Point(startX, startY), LegacyUiConstants.COLOR_LEGACY_BLACK, text);
		showPopup();
	}
	
	@Override
	public List<GameText> getPopupText()
	{
		List<GameText> text = super.getPopupText();
		
		text.add(popupText);
		
		return text;
	}

	@Override
	protected ImageType getPopupDialogSourceImage()
	{
		return ImageType.POPUP_DIALOG_BLANK_PROMPT;
	}

	@Override
	protected Point getPopupDialogOrigin()
	{
		return new Point(204, 162);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		//nothing for this class
	}

	@Override
	protected List<ImageButton> getButtons()
	{
		return new ArrayList<ImageButton>();
	}

	//this is distinct from the other one because it has different visibility
	@Override
	public List<ImageButton> getPopupButtons()
	{
		return buttons;
	}
}
