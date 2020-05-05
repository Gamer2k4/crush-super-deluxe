package main.presentation.legacy.teameditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import main.data.entities.Player;
import main.presentation.legacy.common.FontType;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.teameditor.common.GUIPlayerAttributes;

public class LegacyTeamEditorGeneralRoster extends LegacyTeamEditorScreenDecorator implements ActionListener
{
	private static final long serialVersionUID = -2518698944880751415L;
	
	private int topIndex = 0;
	private int renameIndex = -1;
	
	private LegacyTextField playerNameField;
	
	private static final String PLAYER_LABELS = "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	private static final Point coordsButtonScrollUp = new Point(448, 317);
	private static final Point coordsButtonScrollDown = new Point(505, 317);
	
	private static final Dimension buttonDimScroll = new Dimension(45, 20);
	
	public LegacyTeamEditorGeneralRoster(LegacyTeamEditorScreen screenToPaint)
	{
		super(screenToPaint);
		
		playerNameField = new LegacyTextField("", 8, FontType.FONT_SMALL2, this);
		
		addClickZone(new Rectangle(coordsButtonScrollUp, buttonDimScroll), ScreenCommand.SCROLL_UP);
		addClickZone(new Rectangle(coordsButtonScrollDown, buttonDimScroll), ScreenCommand.SCROLL_DOWN);
		
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
		String commandString = command.name();
		System.out.println("Command received: " + commandString);
		
		if (ScreenCommand.SCROLL_UP.equals(command))
			scrollUp();
		if (ScreenCommand.SCROLL_DOWN.equals(command))
			scrollDown();
		
		if (commandString.startsWith("SELECT_PLAYER_"))
		{
			int playerIndex = Integer.parseInt(commandString.substring(14));
			screenToPaint.currentPlayerIndex = topIndex + playerIndex;
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

	private void scrollUp()
	{
		if (topIndex <= 0)
			return;
		
		topIndex--;
		
		if (screenToPaint.currentPlayerIndex - topIndex > 13)
			screenToPaint.currentPlayerIndex--;
	}

	private void scrollDown()
	{
		if (topIndex >= 21)
			return;
		
		topIndex++;
		
		if (screenToPaint.currentPlayerIndex < topIndex)
			screenToPaint.currentPlayerIndex = topIndex;
	}

	@Override
	protected void paintImages(Graphics2D graphics)
	{
		graphics.drawImage(teamUpdater.getHelmetImage(), 390, 19, null);
	}

	@Override
	protected void paintText(Graphics2D graphics)
	{
		paintTextElement(graphics, 462, 23, teamUpdater.getTeamCoach(), FontType.FONT_SMALL2, LegacyUiConstants.COLOR_LEGACY_BLACK);
		paintTextElement(graphics, 428, 32, teamUpdater.getTeamName(), FontType.FONT_SMALL_TIGHT, LegacyUiConstants.COLOR_LEGACY_BLACK);
		
		for (int i = 0; i < 14; i++)
		{
			int playerIndex = topIndex + i;
			Player player = teamUpdater.getPlayer(playerIndex);
			
			int y = 72 + (17 * i);
			Color color = LegacyUiConstants.COLOR_LEGACY_DULL_WHITE;
			
			if (screenToPaint.currentPlayerIndex == playerIndex)
				color = LegacyUiConstants.COLOR_LEGACY_GOLD;
			
			String name = GUIPlayerAttributes.getNameBlank(player);
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
		// TODO Auto-generated method stub

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
}
