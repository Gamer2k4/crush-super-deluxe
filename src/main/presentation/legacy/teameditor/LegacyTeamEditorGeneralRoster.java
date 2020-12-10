package main.presentation.legacy.teameditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import main.data.entities.Player;
import main.presentation.common.image.ImageType;
import main.presentation.legacy.common.FontType;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.legacy.framework.ClickableRegion;
import main.presentation.legacy.framework.GuiCommand;
import main.presentation.legacy.framework.GuiCommandType;
import main.presentation.legacy.framework.KeyCommand;
import main.presentation.legacy.framework.RegionTriggerType;
import main.presentation.legacy.framework.ScreenCommand;
import main.presentation.teameditor.common.GUIPlayerAttributes;
import main.presentation.teameditor.common.TeamUpdater;

public class LegacyTeamEditorGeneralRoster extends AbstractLegacyTeamEditorRosterScreen implements ActionListener
{
	private int renameIndex = -1;
	private boolean swapping = false;
	private ClickableRegion swapRegion = null;

	private LegacyTextField playerNameField;

	private static final Point coordsButtonSwap = new Point(570, 43);

	public LegacyTeamEditorGeneralRoster(TeamUpdater teamUpdater, ActionListener actionListener)
	{
		super(ImageType.SCREEN_TEAM_EDITOR_ROSTER_GENERAL, teamUpdater, actionListener);
		playerNameField = new LegacyTextField("", 8, FontType.FONT_SMALL2, this);
		defineClickableRegions();
	}
	
	@Override
	protected void defineClickableRegions()
	{
		super.defineClickableRegions();
		swapRegion = ClickableRegion.smallButton2(coordsButtonSwap, ScreenCommand.SWAP_PLAYERS);	//TODO: for whatever reason, this button isn't clicking
		createClickZone(new Rectangle(coordsButtonSwap, buttonDimSmall2), swapRegion);
		
		int selectStartX = 397;
		int renameStartX = 415;
		int startY = 66;

		for (int i = 0; i < 14; i++)
		{
			int adjustedY = startY + (17 * i);
			Rectangle selectArea = new Rectangle(selectStartX, adjustedY, 204, 27);
			Rectangle renameArea = new Rectangle(renameStartX, adjustedY, 48, 27);
			
			createClickZone(selectArea, ClickableRegion.noHighlightButton(new Point(selectStartX, adjustedY), ScreenCommand.valueOf("SELECT_PLAYER_" + i)));
			createClickZone(renameArea, ClickableRegion.noHighlightButton(new Point(renameStartX, adjustedY), ScreenCommand.valueOf("RENAME_PLAYER_" + i), RegionTriggerType.DOUBLE_CLICK));
		}
	}

	@Override
	protected void handleCommand(ScreenCommand command)
	{
		if (!isActive)
			return;
		
		String commandString = command.name();
		System.out.println("Command received: " + commandString);

		if (ScreenCommand.SCROLL_UP.equals(command))
			scrollUp();
		if (ScreenCommand.SCROLL_DOWN.equals(command))
			scrollDown();

		if (command == ScreenCommand.SWAP_PLAYERS)
		{
			toggleSwapping();
			updateScreenImage();
		}

		if (commandString.startsWith("SELECT_PLAYER_"))
		{
			int playerIndex = Integer.parseInt(commandString.substring(14));

			if (!swapping)
				teamUpdater.setCurrentPlayerIndex(topIndex + playerIndex);
			else
			{
				int secondPlayerIndex = topIndex + playerIndex;
				teamUpdater.swapPlayers(teamUpdater.getCurrentPlayerIndex(), secondPlayerIndex);
				toggleSwapping();
			}
		}

		if (commandString.startsWith("RENAME_PLAYER_"))
		{
			int playerIndex = Integer.parseInt(commandString.substring(14));
			Player player = teamUpdater.getPlayer(topIndex + playerIndex);

			if (player == null)
				return;

			renameIndex = playerIndex;
			playerNameField.activate();
		}
	}
	
	private void toggleSwapping()
	{
		swapping = !swapping;
		fireAction(ScreenCommand.TOGGLE_SWAP);
	}

	@Override
	protected void paintComponent(Graphics2D graphics)
	{
		super.paintComponent(graphics);
		paintText(graphics);
	}

	protected void paintText(Graphics2D graphics)
	{
		for (int i = 0; i < 14; i++)
		{
			int playerIndex = topIndex + i;
			Player player = teamUpdater.getPlayer(playerIndex);

			int y = 62 + (17 * i);
			Color color = LegacyUiConstants.COLOR_LEGACY_DULL_WHITE;

			if (teamUpdater.getCurrentPlayerIndex() == playerIndex)
				color = LegacyUiConstants.COLOR_LEGACY_GOLD;

			String name = GUIPlayerAttributes.getNameEmpty(player);
			String rank = GUIPlayerAttributes.getRank(player);
			String race = GUIPlayerAttributes.getRace(player);
			String value = GUIPlayerAttributes.getValue(player);

			paintTextElement(graphics, 19, y, PLAYER_LABELS.charAt(playerIndex) + "", FontType.FONT_SMALL2, color);
			paintTextElement(graphics, 86, y, rank, FontType.FONT_SMALL2, color);
			paintTextElement(graphics, 140, y, race, FontType.FONT_SMALL2, color);
			paintTextElement(graphics, 197, y, value, FontType.FONT_SMALL2, color);

			if (renameIndex != i)
				paintTextElement(graphics, 32, y, name, FontType.FONT_SMALL2, color);
			else
				graphics.drawImage(playerNameField.getTextImage(), 32, y, null);
		}
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
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();

		if (LegacyTextField.REVERT_ACTION.equals(command))
			renameIndex = -1;

		if (LegacyTextField.SUBMIT_ACTION.equals(command))
		{
			String newName = playerNameField.getText();
			int playerIndex = topIndex + renameIndex;
			teamUpdater.getPlayer(playerIndex).name = newName;
			renameIndex = -1;
		}
	}

	@Override
	protected void handleKeyCommand(KeyCommand command)
	{
		if (playerNameField.isActive())
			playerNameField.pressKey(command);
	}

	@Override
	public void receiveGuiCommand(GuiCommand command)
	{
		if (command.getType() == GuiCommandType.MOUSE_PRESS)
		{
			playerNameField.deactivate();
			renameIndex = -1;
		}
		
		super.receiveGuiCommand(command);
	}

	@Override
	public void resetScreen()
	{
		topIndex = 0;
		renameIndex = -1;
		swapping = false;
		playerNameField.deactivate();
	}
	
	@Override
	protected Set<ClickableRegion> getAlwaysHighlightedRegions()
	{
		Set<ClickableRegion> highlightedRegions = new HashSet<ClickableRegion>();
		
		if (swapping)
			highlightedRegions.add(swapRegion);
		
		return highlightedRegions;
	}
}
