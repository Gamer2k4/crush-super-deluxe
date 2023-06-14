package main.presentation.screens.stats;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import main.data.entities.SortableStatsCollection;
import main.data.entities.Team;
import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.common.ScreenCommand;
import main.presentation.game.GameText;
import main.presentation.screens.GameScreen;
import main.presentation.screens.StandardButtonScreen;

public abstract class AbstractTeamStatsParentScreen extends StandardButtonScreen
{
	private static final int STAT_SCREEN_COUNT = 4;
	
	private ScreenCommand currentStatsScreen;
	private SortableStatsCollection stats;
	
	private int topIndex = 0;
	
	private GameScreen originScreen = null;
	
	private Map<ScreenCommand, AbstractTeamStatsScreen> screenMappings = new HashMap<ScreenCommand, AbstractTeamStatsScreen>();
	private Map<ScreenCommand, Integer> buttonIndexMappings = new HashMap<ScreenCommand, Integer>();
	
	private ImageButton[] screenSelectButtons = new ImageButton[STAT_SCREEN_COUNT];
	
	private ImageButton backButton = addButton(35, 586, 339, false, ScreenCommand.STATS_BACK);
	private List<ImageButton> columnSelectButtons = new ArrayList<ImageButton>();
	
	protected AbstractTeamStatsParentScreen(Game sourceGame, ActionListener eventListener)
	{
		super(sourceGame, eventListener);
		stats = new SortableStatsCollection();
		defineColumnSelectButtons();
	}

	private void defineColumnSelectButtons()
	{
		columnSelectButtons.add(addClickZone(347, 72, 57, 20, ScreenCommand.STATS_SORT_COLUMN_0));
		columnSelectButtons.add(addClickZone(404, 72, 57, 20, ScreenCommand.STATS_SORT_COLUMN_1));
		columnSelectButtons.add(addClickZone(461, 72, 57, 20, ScreenCommand.STATS_SORT_COLUMN_2));
		columnSelectButtons.add(addClickZone(518, 72, 57, 20, ScreenCommand.STATS_SORT_COLUMN_3));
	}

	public void setTeam(Team team)
	{
		stats.clear();
		
		for (int i = 0; i < Team.MAX_TEAM_SIZE; i++)
		{
			stats.addPlayerEntry(team, i);
		}
		
		reset();
	}
	
	@Override
	public void activate()
	{
		super.activate();
		refreshStage();
	}
	
	@Override
	public void reset()
	{
		currentStatsScreen = getDefaultStatsScreen();
		updateSelectedScreenButton(currentStatsScreen);
		topIndex = 0;
		sortScreenColumn(0);
	}
	
	private ScreenCommand getDefaultStatsScreen()
	{
		for (ScreenCommand key : buttonIndexMappings.keySet())
		{
			Integer screenIndex = buttonIndexMappings.get(key);
			
			if (screenIndex.intValue() == 0)
				return key;
		}
		
		return null;
	}

	protected void refreshStage()
	{
		stage.clear();
		stage.addActor(new Image(ImageFactory.getInstance().getDrawable(ImageType.BG_BG1)));
		
		AbstractTeamStatsScreen currentScreen = screenMappings.get(currentStatsScreen);
		
		for (Actor actor : currentScreen.getActors())
		{
			stage.addActor(actor);
		}

		for (ImageButton button : getButtons())
		{
			stage.addActor(button);
		}
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		if (!isActive)
			return;
		
		ScreenCommand command = ScreenCommand.fromActionEvent(event);
		
		if (command == currentStatsScreen)
			return;
		
		int newSortColumn = -1;
		
		if (screenMappings.containsKey(command))
		{
			updateSelectedScreenButton(command);
			currentStatsScreen = command;
			newSortColumn = 0;
		}
		
		if (command.isStatColumnSort())
		{
			int commandIndex = command.getCommandIndex();
			AbstractTeamStatsScreen currentScreen = screenMappings.get(currentStatsScreen);
			if (currentScreen.isColumnEnabled(commandIndex))
				newSortColumn = commandIndex;
		}
		
		if (newSortColumn == -1)
			return;
		
		sortScreenColumn(newSortColumn);
	}
	
	private void sortScreenColumn(int columnIndex)
	{
		AbstractTeamStatsScreen currentScreen = screenMappings.get(currentStatsScreen);
		
		int statIndex = currentScreen.selectColumnToSortOn(columnIndex);
		stats.sortStats(statIndex);
		refreshStage();
	}

	private void updateSelectedScreenButton(ScreenCommand command)
	{
		int indexToRemainPressed = buttonIndexMappings.get(command);
		
		for (int i = 0; i < STAT_SCREEN_COUNT; i++)
		{
			if (i == indexToRemainPressed)
				screenSelectButtons[i].setChecked(true);
			else
				screenSelectButtons[i].setChecked(false);
		}
	}

	@Override
	protected List<ImageButton> getButtons()
	{
		List<ImageButton> buttons = new ArrayList<ImageButton>();
		
		for (int i = 0; i < STAT_SCREEN_COUNT; i++)
		{
			ImageButton button = screenSelectButtons[i];
			
			if (button != null)
				buttons.add(button);
		}
		
		for (ImageButton button : columnSelectButtons)
		{
			buttons.add(button);
		}

		buttons.add(backButton);		
		return buttons;
	}
	
	@Override
	public List<GameText> getStaticText()
	{
		List<GameText> gameTexts = new ArrayList<GameText>();
		
		AbstractTeamStatsScreen currentScreen = screenMappings.get(currentStatsScreen);
		
		gameTexts.addAll(currentScreen.getScreenTexts());
		
		return gameTexts;
	}
	
	public void setOriginScreen(GameScreen screen)
	{
		originScreen = screen;
	}
	
	public GameScreen getOriginScreen()
	{
		return originScreen;
	}
	
	public SortableStatsCollection getStats()
	{
		return stats;
	}
	
	public int getTopIndex()
	{
		return topIndex;
	}
	
	protected void addScreenMapping(int buttonIndex, ScreenCommand command, AbstractTeamStatsScreen screen)
	{
		screenMappings.put(command, screen);
		buttonIndexMappings.put(command, buttonIndex);
		screenSelectButtons[buttonIndex] = addButton(72, 231 + (85 * buttonIndex), 359, true, command);
		
		if (buttonIndex == 0)
			screenSelectButtons[0].setChecked(true);
	}
}
