package main.presentation.legacy.teameditor;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;

import main.data.entities.Player;
import main.presentation.legacy.common.FontType;
import main.presentation.legacy.common.LegacyPlayerTextFactory;
import main.presentation.legacy.common.LegacyTextElement;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.teameditor.common.GUIPlayerAttributes;

public class LegacyTeamEditorDetailedRoster extends AbstractLegacyTeamEditorRosterScreen
{
	private static final long serialVersionUID = -3553412591115551360L;

	public LegacyTeamEditorDetailedRoster(LegacyTeamEditorScreen screenToPaint)
	{
		super(screenToPaint);
	}

	@Override
	protected void paintImages(Graphics2D graphics)
	{
		super.paintImages(graphics);
		
		Player player = teamUpdater.getPlayer(teamEditorScreen.currentPlayerIndex);
		
		if (player != null)
			graphics.drawImage(teamUpdater.getPlayerImage(player.getRace()), 400, 92, null);
	}

	@Override
	protected void paintText(Graphics2D graphics)
	{
		super.paintText(graphics);
		
		Player player = teamUpdater.getPlayer(teamEditorScreen.currentPlayerIndex);
		
		String index = PLAYER_LABELS.charAt(teamEditorScreen.currentPlayerIndex) + "";
		String name = GUIPlayerAttributes.getNameEmpty(player);
		
		paintTextElement(graphics, 405, 75, index, FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);
		paintTextElement(graphics, 429, 75, name, FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);
		
		if (player == null)
			return;
		
		LegacyPlayerTextFactory.setPlayer(player);
		
		//work on the text box width and location
		paintPaddedTextElement(graphics, 498, 122, 41, GUIPlayerAttributes.getSeasons(player), FontType.FONT_SMALL2, LegacyUiConstants.COLOR_LEGACY_GOLD);
		paintPaddedTextElement(graphics, 498, 139, 41, GUIPlayerAttributes.getRating(player), FontType.FONT_SMALL2, LegacyUiConstants.COLOR_LEGACY_GOLD);
		
		for (int i = 0; i < 8; i++)
		{
			LegacyTextElement text = LegacyPlayerTextFactory.getColoredAttributeWithModifiers(i, LegacyUiConstants.COLOR_LEGACY_GOLD, LegacyUiConstants.COLOR_LEGACY_GREEN);
			graphics.drawImage(fontFactory.generateString(text), 400 + (20 * i), 182, null);
		}
		
		paintSkillsTextBox(player);
	}

	@Override
	protected void paintButtonShading(Graphics2D graphics)
	{
		// nothing to paint
	}

	private void paintSkillsTextBox(Player player)
	{
		// TODO Auto-generated method stub
//		400, 202 start pixel
//		200 pixels wide, 59 pixels high (but make it 65 "just in case" there are more skills)
		
		//note that this should be extracted to a helper class, since it needs to take in arbitrary dimensions if the pool draft screen is to make use of it 
	}
	
	@Override
	protected void handleCommand(ScreenCommand command)
	{
		if (!buttonsEnabled)
			return;
		
		if (ScreenCommand.SCROLL_UP.equals(command))
			teamEditorScreen.previousPlayer();
		if (ScreenCommand.SCROLL_DOWN.equals(command))
			teamEditorScreen.nextPlayer();
	}

	@Override
	protected void keyAction(ActionEvent keyAction)
	{
		// nothing to do
	}

	@Override
	public void resetScreen()
	{
		topIndex = 0;
	}
}
