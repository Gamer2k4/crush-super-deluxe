package main.presentation.legacy.teameditor;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import main.data.entities.Player;
import main.data.entities.Race;
import main.data.factory.PlayerFactory;
import main.presentation.legacy.common.FontType;
import main.presentation.legacy.common.LegacyUiConstants;

public class LegacyTeamEditorStockDraftScreen extends AbstractLegacyTeamEditorDraftScreen implements ActionListener
{
	private static final long serialVersionUID = -25869557840982323L;
	
	private int selectedRace = 0;
	
	private static final Dimension RACE_SELECT_ZONE = new Dimension(38, 32);

	public LegacyTeamEditorStockDraftScreen(LegacyTeamEditorScreen screenToPaint)
	{
		super(screenToPaint);
		teamUpdater.addUpdateListener(this);
		
		addClickZone(new Rectangle(new Point(21, 19), RACE_SELECT_ZONE), ScreenCommand.STOCK_DRAFT_SELECT_HUMAN);
		addClickZone(new Rectangle(new Point(21, 64), RACE_SELECT_ZONE), ScreenCommand.STOCK_DRAFT_SELECT_GRONK);
		addClickZone(new Rectangle(new Point(21, 109), RACE_SELECT_ZONE), ScreenCommand.STOCK_DRAFT_SELECT_CURMIAN);
		addClickZone(new Rectangle(new Point(21, 154), RACE_SELECT_ZONE), ScreenCommand.STOCK_DRAFT_SELECT_DRAGORAN);
		addClickZone(new Rectangle(new Point(76, 41), RACE_SELECT_ZONE), ScreenCommand.STOCK_DRAFT_SELECT_NYNAX);
		addClickZone(new Rectangle(new Point(76, 86), RACE_SELECT_ZONE), ScreenCommand.STOCK_DRAFT_SELECT_SLITH);
		addClickZone(new Rectangle(new Point(76, 131), RACE_SELECT_ZONE), ScreenCommand.STOCK_DRAFT_SELECT_KURGAN);
		addClickZone(new Rectangle(new Point(76, 176), RACE_SELECT_ZONE), ScreenCommand.STOCK_DRAFT_SELECT_XJS9000);
	}

	private void hirePlayer()
	{
		Player playerToHire = PlayerFactory.createPlayerWithRandomName(Race.getRace(selectedRace));
		boolean draftSuccess = teamUpdater.hirePlayer(teamEditorScreen.currentPlayerIndex, playerToHire, teamEditorScreen.getBudget());
		
		if (draftSuccess)
			teamEditorScreen.nextPlayer();	//strictly speaking, this is wrong for two reasons: 1) a failed hire with sufficient funds should still move the cursor
																							//	2) the cursor should be able to move "off screen"
																							//	That said, I like my implementation better.
	}
	
	@Override
	protected void handleCommand(ScreenCommand command)
	{
		if (!buttonsEnabled)
			return;
		
		String commandString = command.name();
		System.out.println(commandString);
		
		if (ScreenCommand.HIRE_PLAYER.equals(command))
		{
			hirePlayer();
		}
		else if (ScreenCommand.FIRE_PLAYER.equals(command))
		{
			teamUpdater.firePlayer(teamEditorScreen.currentPlayerIndex);
		}
		else if (commandString.startsWith("STOCK_DRAFT_SELECT_"))
		{
			String raceName = commandString.substring(19);
			selectedRace = Race.valueOf(raceName).getIndex();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void keyAction(ActionEvent keyAction)
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void paintImages(Graphics2D graphics)
	{
		Player player = new Player(Race.getRace(selectedRace), "DRAFTEE");
		paintPlayer(graphics, player);
	}

	@Override
	protected void paintText(Graphics2D graphics)
	{
		paintPaddedTextElement(graphics, 198, 19, 110, Race.getRace(selectedRace).name(), FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_BLACK);
	}

	@Override
	protected void paintButtonShading(Graphics2D graphics)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void resetScreen()
	{
		selectedRace = 0;
	}
}
