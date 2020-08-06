package main.presentation.legacy.teameditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import main.data.entities.Player;
import main.presentation.common.image.ImageType;
import main.presentation.legacy.common.FontType;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.teameditor.common.GUIPlayerAttributes;

public class LegacyTeamEditorGeneralRoster extends AbstractLegacyTeamEditorRosterScreen implements ActionListener
{
	private static final long serialVersionUID = -2518698944880751415L;

	private int renameIndex = -1;
	private boolean swapping = false;

	private LegacyTextField playerNameField;

	private static final Point coordsButtonSwap = new Point(570, 43);

	public LegacyTeamEditorGeneralRoster(LegacyTeamEditorScreen screenToPaint)
	{
		super(screenToPaint);

		playerNameField = new LegacyTextField("", 8, FontType.FONT_SMALL2, this);

		addClickZone(new Rectangle(coordsButtonSwap, buttonDimSmallStats), ScreenCommand.SWAP_PLAYERS);

		addPlayerClickZones();
	}

	private void addPlayerClickZones()
	{
		int selectStartX = 397;
		int renameStartX = 415;
		int startY = 66;

		for (int i = 0; i < 14; i++)
		{
			Rectangle selectArea = new Rectangle(selectStartX, startY + (17 * i), 204, 17);
			Rectangle renameArea = new Rectangle(renameStartX, startY + (17 * i), 48, 17);

			addClickZone(selectArea, ScreenCommand.valueOf("SELECT_PLAYER_" + i));
			addDoubleClickZone(renameArea, ScreenCommand.valueOf("RENAME_PLAYER_" + i));
		}
	}

	@Override
	protected void handleCommand(ScreenCommand command)
	{
		if (!buttonsEnabled)
			return;
		
		String commandString = command.name();
		System.out.println("Command received: " + commandString);

		if (ScreenCommand.SCROLL_UP.equals(command))
			scrollUp();
		if (ScreenCommand.SCROLL_DOWN.equals(command))
			scrollDown();

		if (command == ScreenCommand.SWAP_PLAYERS)
			swapping = !swapping;

		if (commandString.startsWith("SELECT_PLAYER_"))
		{
			int playerIndex = Integer.parseInt(commandString.substring(14));

			if (!swapping)
				teamEditorScreen.currentPlayerIndex = topIndex + playerIndex;
			else
			{
				int secondPlayerIndex = topIndex + playerIndex;
				teamUpdater.swapPlayers(teamEditorScreen.currentPlayerIndex, secondPlayerIndex);
				swapping = false;
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

	@Override
	protected void paintText(Graphics2D graphics)
	{
		super.paintText(graphics);

		for (int i = 0; i < 14; i++)
		{
			int playerIndex = topIndex + i;
			Player player = teamUpdater.getPlayer(playerIndex);

			int y = 72 + (17 * i);
			Color color = LegacyUiConstants.COLOR_LEGACY_DULL_WHITE;

			if (teamEditorScreen.currentPlayerIndex == playerIndex)
				color = LegacyUiConstants.COLOR_LEGACY_GOLD;

			String name = GUIPlayerAttributes.getNameEmpty(player);
			String rank = GUIPlayerAttributes.getRank(player);
			String race = GUIPlayerAttributes.getRace(player);
			String value = GUIPlayerAttributes.getValue(player);

			paintTextElement(graphics, 402, y, PLAYER_LABELS.charAt(playerIndex) + "", FontType.FONT_SMALL2, color);
			paintTextElement(graphics, 469, y, rank, FontType.FONT_SMALL2, color);
			paintTextElement(graphics, 523, y, race, FontType.FONT_SMALL2, color);
			paintTextElement(graphics, 580, y, value, FontType.FONT_SMALL2, color);

			if (renameIndex != i)
				paintTextElement(graphics, 415, y, name, FontType.FONT_SMALL2, color);
			else
				graphics.drawImage(playerNameField.getTextImage(), 415, y, null);
		}
	}

	@Override
	protected void paintButtonShading(Graphics2D graphics)
	{
		if (swapping)
			graphics.drawImage(imageFactory.getImage(ImageType.BUTTON_SMALL_CLICKED), coordsButtonSwap.x, coordsButtonSwap.y, null);
	}

	private void scrollUp()
	{
		if (topIndex <= 0)
			return;

		topIndex--;

		if (teamEditorScreen.currentPlayerIndex - topIndex > 13)
			teamEditorScreen.currentPlayerIndex--;
	}

	private void scrollDown()
	{
		if (topIndex >= 21)
			return;

		topIndex++;

		if (teamEditorScreen.currentPlayerIndex < topIndex)
			teamEditorScreen.currentPlayerIndex = topIndex;
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
	protected void keyAction(ActionEvent keyAction)
	{
		if (playerNameField.isActive())
			playerNameField.pressKey(keyAction);
	}

	@Override
	public void mousePressed(MouseEvent event)
	{
		playerNameField.deactivate();
		renameIndex = -1;
		super.mousePressed(event);
	}

	@Override
	public void resetScreen()
	{
		topIndex = 0;
		renameIndex = -1;
		swapping = false;
		playerNameField.deactivate();
	}
}
