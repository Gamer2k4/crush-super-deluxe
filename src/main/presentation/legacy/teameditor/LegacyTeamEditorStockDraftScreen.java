package main.presentation.legacy.teameditor;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import main.data.entities.Player;
import main.data.entities.Race;
import main.data.factory.PlayerFactory;
import main.presentation.audio.CrushAudioManager;
import main.presentation.audio.SoundClipType;
import main.presentation.common.image.ImageType;
import main.presentation.legacy.common.FontType;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.legacy.framework.ClickableRegion;
import main.presentation.legacy.framework.ScreenCommand;
import main.presentation.teameditor.common.TeamUpdater;

public class LegacyTeamEditorStockDraftScreen extends AbstractLegacyTeamEditorDraftScreen
{
	private int selectedRace = 0;
	private int maxBudget;
	
	private Map<Integer, SoundClipType> soundMappings = new HashMap<Integer, SoundClipType>();
	
	private static final Dimension RACE_SELECT_ZONE = new Dimension(38, 32);

	public LegacyTeamEditorStockDraftScreen(TeamUpdater teamUpdater, ActionListener actionListener, int maxBudget)
	{
		super(ImageType.SCREEN_TEAM_EDITOR_DRAFT, teamUpdater, actionListener);
		setSoundMappings();
		setBudget(maxBudget);
	}

	private void setSoundMappings()
	{
		soundMappings.put(0, SoundClipType.HUMVOC);
		soundMappings.put(1, SoundClipType.GRKVOC);
		soundMappings.put(2, SoundClipType.CURVOC);
		soundMappings.put(3, SoundClipType.DRGVOC);
		soundMappings.put(4, SoundClipType.NYNVOC);
		soundMappings.put(5, SoundClipType.SLTVOC);
		soundMappings.put(6, SoundClipType.KURVOC);
		soundMappings.put(7, SoundClipType.XJSVOC);
	}

	@Override
	protected void defineClickableRegions()
	{
		super.defineClickableRegions();
		createClickZone(new Rectangle(new Point(21, 19), RACE_SELECT_ZONE), ClickableRegion.noHighlightButton(new Point(21, 19), ScreenCommand.STOCK_DRAFT_SELECT_HUMAN));
		createClickZone(new Rectangle(new Point(21, 64), RACE_SELECT_ZONE), ClickableRegion.noHighlightButton(new Point(21, 64), ScreenCommand.STOCK_DRAFT_SELECT_GRONK));
		createClickZone(new Rectangle(new Point(21, 109), RACE_SELECT_ZONE), ClickableRegion.noHighlightButton(new Point(21, 109), ScreenCommand.STOCK_DRAFT_SELECT_CURMIAN));
		createClickZone(new Rectangle(new Point(21, 154), RACE_SELECT_ZONE), ClickableRegion.noHighlightButton(new Point(21, 154), ScreenCommand.STOCK_DRAFT_SELECT_DRAGORAN));
		createClickZone(new Rectangle(new Point(76, 41), RACE_SELECT_ZONE), ClickableRegion.noHighlightButton(new Point(76, 41), ScreenCommand.STOCK_DRAFT_SELECT_NYNAX));
		createClickZone(new Rectangle(new Point(76, 86), RACE_SELECT_ZONE), ClickableRegion.noHighlightButton(new Point(76, 86), ScreenCommand.STOCK_DRAFT_SELECT_SLITH));
		createClickZone(new Rectangle(new Point(76, 131), RACE_SELECT_ZONE), ClickableRegion.noHighlightButton(new Point(76, 131), ScreenCommand.STOCK_DRAFT_SELECT_KURGAN));
		createClickZone(new Rectangle(new Point(76, 176), RACE_SELECT_ZONE), ClickableRegion.noHighlightButton(new Point(76, 176), ScreenCommand.STOCK_DRAFT_SELECT_XJS9000));
	}

	private void hirePlayer()
	{
		Player playerToHire = PlayerFactory.createPlayerWithRandomName(Race.getRace(selectedRace));
		boolean draftSuccess = teamUpdater.hirePlayer(teamUpdater.getCurrentPlayerIndex(), playerToHire, maxBudget);
		
		if (draftSuccess)
			teamUpdater.selectNextPlayer();	//strictly speaking, this is wrong for two reasons: 1) a failed hire with sufficient funds should still move the cursor
																							//	2) the cursor should be able to move "off screen"
																							//	That said, I like my implementation better.
	}
	
	@Override
	protected void handleCommand(ScreenCommand command)
	{
		if (!isActive)
			return;
		
		String commandString = command.name();
		System.out.println(commandString);
		
		if (ScreenCommand.HIRE_PLAYER.equals(command))
		{
			hirePlayer();
		}
		else if (ScreenCommand.FIRE_PLAYER.equals(command))
		{
			teamUpdater.firePlayer(teamUpdater.getCurrentPlayerIndex());
		}
		else if (commandString.startsWith("STOCK_DRAFT_SELECT_"))
		{
			String raceName = commandString.substring(19);
			selectedRace = Race.valueOf(raceName).getIndex();
			CrushAudioManager.getInstance().playForegroundSound(soundMappings.get(selectedRace));
		}
	}
	
	@Override
	protected void paintComponent(Graphics2D graphics)
	{
		super.paintComponent(graphics);
		
		Player player = new Player(Race.getRace(selectedRace), "DRAFTEE");
		paintPlayer(graphics, player);
		
		paintPaddedTextElement(graphics, 198, 19, 110, Race.getRace(selectedRace).name(), FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_BLACK);
	}

	@Override
	public void resetScreen()
	{
		selectedRace = 0;
	}

	public void setBudget(int budget)
	{
		maxBudget = budget;
	}
}
