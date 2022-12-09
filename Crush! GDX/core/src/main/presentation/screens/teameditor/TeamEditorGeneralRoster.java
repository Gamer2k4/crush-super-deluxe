package main.presentation.screens.teameditor;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import main.data.entities.Player;
import main.data.entities.Team;
import main.presentation.ImageType;
import main.presentation.common.ScreenCommand;
import main.presentation.game.GameText;
import main.presentation.game.StaticImage;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.screens.teameditor.utilities.GUIPlayerAttributes;
import main.presentation.screens.teameditor.utilities.TeamUpdater;

public class TeamEditorGeneralRoster extends AbstractTeamEditorRosterScreen
{
	private boolean swapping = false;
	
	private GameText[][] playerText = new GameText[Team.MAX_TEAM_SIZE][5];
	
	private static final int MAX_PLAYERS_TO_DISPLAY = 14;
	
	protected TeamEditorGeneralRoster(TeamEditorParentScreen parentScreen)
	{
		super(parentScreen);
		teamUpdater.addUpdateListener(this);
		subScreenImage = new StaticImage(ImageType.SCREEN_TEAM_EDITOR_ROSTER_GENERAL, screenOrigin);
		refreshContent();
	}
	
	public void refreshAllPlayerTexts()
	{
		for (int i = 0; i < Team.MAX_TEAM_SIZE; i++)
		{
			refreshPlayerText(i);
		}
	}
	
	public void refreshPlayerText(int index)
	{
		Player player = teamUpdater.getPlayer(index);
		
		String name = GUIPlayerAttributes.getNameEmpty(player);
		String rank = GUIPlayerAttributes.getRank(player);
		String race = GUIPlayerAttributes.getRace(player);
		String value = GUIPlayerAttributes.getValue(player);
		
		Color color = LegacyUiConstants.COLOR_LEGACY_DULL_WHITE;
		
		if (player != null && player.getWeeksOut() > 0)
			color = LegacyUiConstants.COLOR_LEGACY_RED;

		if (teamUpdater.getCurrentPlayerIndex() == index)
			color = LegacyUiConstants.COLOR_LEGACY_GOLD;
		
		playerText[index][0] = GameText.small2(color, PLAYER_LABELS.charAt(index) + "");
		playerText[index][1] = GameText.small2(color, name);
		playerText[index][2] = GameText.small2(color, rank);
		playerText[index][3] = GameText.small2(color, race);
		playerText[index][4] = GameText.small2(color, value);
	}

	@Override
	protected void handleCommand(ScreenCommand command)
	{
		String commandString = command.name();
//		System.out.println("Command received in General Roster: " + commandString);

		if (ScreenCommand.SCROLL_UP.equals(command))
			scrollUp();
		if (ScreenCommand.SCROLL_DOWN.equals(command))
			scrollDown();

//		if (command == ScreenCommand.SWAP_PLAYERS)
//		{
//			toggleSwapping();
//			updateScreenImage();
//		}

		if (commandString.startsWith("SELECT_PLAYER_"))
		{
			int playerIndex = Integer.parseInt(commandString.substring(14));

			if (!swapping)
				teamUpdater.setCurrentPlayerIndex(topIndex + playerIndex);
//			else
//			{
//				int secondPlayerIndex = topIndex + playerIndex;
//				teamUpdater.swapPlayers(teamUpdater.getCurrentPlayerIndex(), secondPlayerIndex);
//				toggleSwapping();
//			}
		}

//		if (commandString.startsWith("RENAME_PLAYER_"))
//		{
//			int playerIndex = Integer.parseInt(commandString.substring(14));
//			Player player = teamUpdater.getPlayer(topIndex + playerIndex);
//
//			if (player == null)
//				return;
//
//			renameIndex = playerIndex;
//			playerNameField.activate();
//		}
	}

	private void scrollUp()
	{
		if (topIndex <= 0)
			return;

		topIndex--;

		if (teamUpdater.getCurrentPlayerIndex() - topIndex > 13)
			teamUpdater.selectPreviousPlayer();
	}

	private void scrollDown()
	{
		if (topIndex >= 21)
			return;

		topIndex++;

		if (teamUpdater.getCurrentPlayerIndex() < topIndex)
			teamUpdater.setCurrentPlayerIndex(topIndex);
	}
	
	@Override
	public List<GameText> getScreenTexts()
	{
		List<GameText> screenTexts = super.getScreenTexts();
		
		for (int i = 0; i < MAX_PLAYERS_TO_DISPLAY; i++)
		{
			int playerIndex = topIndex + i;
			
			int y = 79 + (17 * i);
			
			GameText label = playerText[playerIndex][0];
			GameText name = playerText[playerIndex][1];
			GameText rank = playerText[playerIndex][2];
			GameText race = playerText[playerIndex][3];
			GameText value = playerText[playerIndex][4];
			
			label.setCoords(new Point(402, y));
			name.setCoords(new Point(415, y));
			rank.setCoords(new Point(469, y));
			race.setCoords(new Point(523, y));
			value.setCoords(new Point(580, y));
			
			screenTexts.add(label);
			screenTexts.add(name);
			screenTexts.add(value);
			
			if (!rank.isEmpty())
				screenTexts.add(rank);
			
			if (!race.isEmpty())
				screenTexts.add(race);
		}
		
		return screenTexts;
	}
	
	@Override
	public List<ImageButton> getScreenButtons()
	{
		List<ImageButton> screenButtons = super.getScreenButtons();
		
		int selectStartX = 397;
		int renameStartX = 415;
		int startY = 78;

		for (int i = 0; i < 14; i++)
		{
			int adjustedY = startY + (17 * i);
			screenButtons.add(parentScreen.addClickZone(selectStartX, adjustedY, 204, 17, ScreenCommand.valueOf("SELECT_PLAYER_" + i)));
			
			//this should be a double-click
//			screenButtons.add(parentScreen.addClickZone(renameStartX, adjustedY, 48, 17, ScreenCommand.valueOf("RENAME_PLAYER_" + i)));
		}
		
		return screenButtons;
	}

	@Override
	public void refreshContent()
	{
		super.refreshContent();
		refreshAllPlayerTexts();
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		if (event.getActionCommand().equals(TeamUpdater.UPDATER_PLAYER_SELECTION_CHANGED))
			refreshAllPlayerTexts();
	}
}
