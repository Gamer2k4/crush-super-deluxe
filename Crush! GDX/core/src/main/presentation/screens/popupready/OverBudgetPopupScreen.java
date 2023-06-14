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

public class OverBudgetPopupScreen extends PopupReadyScreen
{
	private List<ImageButton> buttons = new ArrayList<ImageButton>();
	
	private List<GameText> teamNames = new ArrayList<GameText>();
	
	protected OverBudgetPopupScreen(Game sourceGame, ActionListener eventListener)
	{
		super(sourceGame, eventListener);
		
		ImageButton acknowledgeZone = addClickZone(0, 0, 640, 400, ScreenCommand.POPUP_NO);
		buttons.add(acknowledgeZone);
	}
	
	protected void showPopup(List<String> teamsOverBudget)
	{
		teamNames.clear();
		
		for (int i = 0; i < teamsOverBudget.size(); i++)
		{
			String teamName = teamsOverBudget.get(i);
			int startX = GameText.getStringStartX(GameText.small, 205, ImageFactory.getInstance().getImageWidth(ImageType.POPUP_DIALOG_OVER_BUDGET), teamName);
			int startY = 80 + 13 * i;
			
			GameText popupText = GameText.small(new Point(startX, startY), LegacyUiConstants.COLOR_LEGACY_WHITE, teamName);
			teamNames.add(popupText);
		}
		
		showPopup();
	}
	
	@Override
	public List<GameText> getPopupText()
	{
		List<GameText> text = super.getPopupText();
		
		text.addAll(teamNames);
		
		return text;
	}

	@Override
	protected ImageType getPopupDialogSourceImage()
	{
		return ImageType.POPUP_DIALOG_OVER_BUDGET;
	}

	@Override
	protected Point getPopupDialogOrigin()
	{
		return new Point(205, 41);
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
