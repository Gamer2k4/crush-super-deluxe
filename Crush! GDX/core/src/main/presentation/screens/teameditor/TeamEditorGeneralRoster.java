package main.presentation.screens.teameditor;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import main.data.entities.Player;
import main.data.entities.Team;
import main.presentation.CursorManager;
import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.common.GdxKeyMappings;
import main.presentation.common.ScreenCommand;
import main.presentation.game.GameText;
import main.presentation.game.StaticImage;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.screens.teameditor.utilities.GUIPlayerAttributes;
import main.presentation.screens.teameditor.utilities.TeamUpdater;

public class TeamEditorGeneralRoster extends AbstractTeamEditorRosterScreen
{
	private boolean swapping = false;
	private Timer swapEndTimer = new Timer();
	private TimerTask swapTimerTask = null;
	
	private GameText[][] playerText = new GameText[Team.MAX_TEAM_SIZE][5];
	
	private static final int MAX_PLAYERS_TO_DISPLAY = 14;
	
	private ImageButton swapButton;
	
	private Image textEditCursor;
	private String playerNameEditText = null;
	private static final int MAX_NAME_LENGTH = 8;
	
	protected TeamEditorGeneralRoster(TeamEditorParentScreen parentScreen)
	{
		super(parentScreen);
		teamUpdater.addUpdateListener(this);
		subScreenImage = new StaticImage(ImageType.SCREEN_TEAM_EDITOR_ROSTER_GENERAL, screenOrigin);
		swapButton = parentScreen.addButton(37, 570, 55, false, ScreenCommand.SWAP_PLAYERS);
		textEditCursor = new Image(new TextureRegionDrawable(new TextureRegion(ImageFactory.getInstance().getTexture(ImageType.SETTINGS_COLOR_PALETTE), 24, 24, 5, 5)));
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
		super.handleCommand(command);
		
		String commandString = command.name();
		System.out.println("Command received in General Roster: " + commandString);

		if (ScreenCommand.SCROLL_ROSTER_UP.equals(command))
			scrollUp();
		if (ScreenCommand.SCROLL_ROSTER_DOWN.equals(command))
			scrollDown();

		if (command == ScreenCommand.SWAP_PLAYERS && canUserEditTeam())
		{
			Gdx.graphics.setCursor(CursorManager.swap());
			swapping = true;
		}

		if (commandString.startsWith("SELECT_PLAYER_"))
		{
			int playerIndex = Integer.parseInt(commandString.substring(14));
			
			if (playerNameEditText == null && isDoubleClick() && teamUpdater.getCurrentlySelectedPlayer() != null)
			{
				Gdx.graphics.setCursor(CursorManager.hidden());
				playerNameEditText = "";
				return;
			}
			
			System.out.println("Player " + playerIndex + " selected; swapping = " + swapping);

			if (!swapping)
				teamUpdater.setCurrentPlayerIndex(topIndex + playerIndex);
			else
			{
				int secondPlayerIndex = topIndex + playerIndex;
				teamUpdater.swapPlayers(teamUpdater.getCurrentPlayerIndex(), secondPlayerIndex);
				endSwap();
			}
		}
	}
	
	@Override
	protected void pressKey(String key)
	{
		if (key == GdxKeyMappings.ESCAPE)
		{
			endSwap();	//TODO: right now this doesn't work because keys aren't enabled unless a player name is being edited
			playerNameEditText = null;
			refreshParentStage();
		}
		
		if (playerNameEditText == null)
			return;
		
		if (key == GdxKeyMappings.BACKSPACE)
		{
			if (playerNameEditText != null && playerNameEditText.length() > 0)
				playerNameEditText = playerNameEditText.substring(0, playerNameEditText.length() - 1);
		}
		
		if (key.length() == 1)
		{
			if (playerNameEditText != null)
				playerNameEditText = playerNameEditText + key;
		}
		
		if (playerNameEditText != null)
		{
			if (playerNameEditText.length() >= MAX_NAME_LENGTH || key == GdxKeyMappings.ENTER)
			{
				Gdx.graphics.setCursor(parentScreen.getCursor());
				teamUpdater.setCurrentPlayerName(playerNameEditText);
				playerNameEditText = null;
			}
		}
		
		refreshPlayerText(teamUpdater.getCurrentPlayerIndex());
		refreshParentStage();
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
	public List<Actor> getActors()
	{
		List<Actor> actors = super.getActors();
		
		if (playerNameEditText != null)
		{
			int y = 311 - (17 * (teamUpdater.getCurrentPlayerIndex() - topIndex));
			textEditCursor.setPosition(415 + (6 * playerNameEditText.length()), y);
			actors.add(textEditCursor);
		}
		
		return actors;
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
			screenTexts.add(value);
			
			if (!rank.isEmpty())
				screenTexts.add(rank);
			
			if (!race.isEmpty())
				screenTexts.add(race);
			
			if (playerIndex == teamUpdater.getCurrentPlayerIndex() && playerNameEditText != null)
				screenTexts.add(GameText.small2(new Point(415, y), LegacyUiConstants.COLOR_LEGACY_GOLD, playerNameEditText));
			else
				screenTexts.add(name);			
		}
		
		return screenTexts;
	}
	
	@Override
	public List<ImageButton> getScreenButtons()
	{
		List<ImageButton> screenButtons = super.getScreenButtons();
		
		screenButtons.add(swapButton);
		
		int selectStartX = 397;
		int startY = 78;

		for (int i = 0; i < 14; i++)
		{
			int adjustedY = startY + (17 * i);
			screenButtons.add(parentScreen.addClickZone(selectStartX, adjustedY, 204, 17, ScreenCommand.valueOf("SELECT_PLAYER_" + i)));
		}
		
		return screenButtons;
	}

	@Override
	public void refreshContent()
	{
		super.refreshContent();
		swapping = false;
		playerNameEditText = null;
		refreshAllPlayerTexts();
	}
	
	private void endSwap()
	{
		Gdx.graphics.setCursor(parentScreen.getCursor());	//TODO: this doesn't show up until the mouse is moved (and refreshParentStage() doesn't fix it)
		swapping = false;
		swapTimerTask = null;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		super.actionPerformed(event);
		
		if (event.getActionCommand().equals(TeamUpdater.UPDATER_PLAYER_SELECTION_CHANGED))
			refreshAllPlayerTexts();
		if (event.getActionCommand().equals(TeamUpdater.UPDATER_NEW_TEAM))
			refreshAllPlayerTexts();
		if (event.getActionCommand().equals(TeamUpdater.UPDATER_PLAYERS_CHANGED))
			refreshAllPlayerTexts();
	}
	
	@Override
	public boolean keysEnabled()
	{
		return (playerNameEditText != null);
	}
	
	@Override
	protected void mouseClicked(Point clickCursorCoords)
	{
		if (swapTimerTask != null || !swapping)
			return;

		swapTimerTask = new TimerTask() {
	        @Override
			public void run() {
	        	endSwap();
	        }
	    };
		
	    swapEndTimer.schedule(swapTimerTask, 200);	//end the swap after a fifth of a second, so there's time to handle the swap screen command
	}
}
