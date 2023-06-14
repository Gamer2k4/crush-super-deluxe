package main.presentation.screens.teameditor;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import main.data.entities.Player;
import main.data.entities.Team;
import main.presentation.ImageType;
import main.presentation.common.ScreenCommand;
import main.presentation.game.FontType;
import main.presentation.game.GameText;
import main.presentation.game.StaticImage;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.screens.teameditor.utilities.TeamUpdater;

public class TeamEditorDocbotScreen extends AbstractTeamEditorSubScreen
{
	private DecimalFormat threeDigitFormatter = new DecimalFormat("000");
	private Point budgetCoords = new Point(46, 161);

	private List<Integer> injuredPlayerIndexes = new ArrayList<Integer>();

	private GameText[][] playerText = new GameText[5][3];
	private GameText docbotBudget = null;
	private ImageButton[][] docbotOptions = new ImageButton[4][2];
	private ImageButton docbotUp = parentScreen.addClickZone(240, 211, 39, 20, ScreenCommand.DOCBOT_UP);
	private ImageButton docbotDown = parentScreen.addClickZone(240, 234, 39, 20, ScreenCommand.DOCBOT_DOWN);

	private int topIndex = 0;

	protected TeamEditorDocbotScreen(TeamEditorParentScreen parentScreen)
	{
		super(parentScreen);
		subScreenImage = new StaticImage(ImageType.SCREEN_TEAM_EDITOR_DOCBOT, screenOrigin);
		defineDocbotOptionButtons();
		refreshContent();
	}

	private void defineDocbotOptionButtons()
	{
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 2; j++)
			{
				docbotOptions[i][j] = parentScreen.addButton(72, 122 + (80 * j), 53 + (36 * i), true,
						ScreenCommand.changeDocbotOption(i, j));
			}
		}

		docbotBudget = new GameText(FontType.FONT_SMALL_SPREAD, budgetCoords, LegacyUiConstants.COLOR_LEGACY_GOLD, "000");
	}

	@Override
	public void refreshContent()
	{
		topIndex = 0;
		refreshInjuredPlayers();
		refreshAllPlayerTexts();
		refreshDocbotOptions();
	}

	private void refreshInjuredPlayers()
	{
		injuredPlayerIndexes.clear();
		
		for (int i = 0; i < Team.MAX_TEAM_SIZE; i++)
		{
			Player player = teamUpdater.getPlayer(i);

			if (player == null)
				continue;

			if (player.getInjuryType() == Player.INJURY_NONE)
				continue;
			
			injuredPlayerIndexes.add(i);
		}
	}

	private void refreshAllPlayerTexts()
	{
		for (int i = 0; i < 5; i++)
		{
			String label = "";
			String name = "";
			String weeksOut = "";
			int y = 202 + (17 * i);
			
			int injuredPlayerListIndex = topIndex + i;
			
			if (injuredPlayerIndexes.size() > injuredPlayerListIndex)
			{
				int playerIndex = injuredPlayerIndexes.get(injuredPlayerListIndex);
				Player player = teamUpdater.getPlayer(playerIndex);
				
				label = PLAYER_LABELS.charAt(playerIndex) + "";
				name = player.name;
				weeksOut = "OUT: " + player.getWeeksOut() + " WEEKS";
			}
			
			playerText[i][0] = GameText.small2(new Point(29, y), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, label);
			playerText[i][1] = GameText.small2(new Point(42, y), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, name);
			playerText[i][2] = GameText.small2(new Point(96, y), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, weeksOut);
		}
	}

	private void refreshDocbotOptions()
	{
		boolean[] docbot = teamUpdater.getTeam().docbot;

		for (int i = 0; i < 4; i++)
		{
			if (docbot[i])
			{
				docbotOptions[i][0].setChecked(false);
				docbotOptions[i][1].setChecked(true);
			} else
			{
				docbotOptions[i][0].setChecked(true);
				docbotOptions[i][1].setChecked(false);
			}
		}

		docbotBudget = new GameText(FontType.FONT_SMALL_SPREAD, budgetCoords, LegacyUiConstants.COLOR_LEGACY_GOLD,
				threeDigitFormatter.format(teamUpdater.getDocbotCost()));
	}

	@Override
	protected void handleCommand(ScreenCommand command)
	{
		String commandString = command.name();

		if (command == ScreenCommand.DOCBOT_UP)
		{
			if (topIndex <= 0)
				return;
			
			topIndex--;
			refreshAllPlayerTexts();
		}
		else if (command == ScreenCommand.DOCBOT_DOWN)
		{
			if (topIndex >= injuredPlayerIndexes.size() - 5)
				return;
			
			topIndex++;
			refreshAllPlayerTexts();
		}
		else if (commandString.startsWith("DOCBOT_"))
		{
			int option = Integer.parseInt(commandString.substring(7, 8));
			int level = Integer.parseInt(commandString.substring(8));

			teamUpdater.setDocbotTreatment(option, level == 1);
		}
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		switch (event.getActionCommand())
		{
		case TeamUpdater.UPDATER_DOCBOT_CHANGED:
			refreshDocbotOptions();
			break;
		case TeamUpdater.UPDATER_PLAYERS_CHANGED:
			refreshDocbotOptions();
			break;
		}
	}

	@Override
	public List<ImageButton> getScreenButtons()
	{
		List<ImageButton> screenButtons = super.getScreenButtons();
		
		screenButtons.add(docbotUp);
		screenButtons.add(docbotDown);
		
		//disable changing docbot options if the player is not allowed to
		if (!canUserEditTeam())
			return screenButtons;

		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 2; j++)
			{
				screenButtons.add(docbotOptions[i][j]);
			}
		}

		return screenButtons;
	}

	@Override
	public List<GameText> getScreenTexts()
	{
		List<GameText> screenTexts = super.getScreenTexts();

		screenTexts.add(docbotBudget);
		
		for (int i = 0; i < 5; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				screenTexts.add(playerText[i][j]);
			}
		}

		return screenTexts;
	}
	
	@Override
	public List<Actor> getActors()
	{
		List<Actor> actors = super.getActors();
		
		actors.add(subScreenImage.getImage());
		
		//if the user can make changes, the buttons are working and we don't need to add pressed button images
		if (canUserEditTeam())
			return actors;
		
		boolean[] docbotBools = teamUpdater.getTeam().docbot; 
		
		for (int i = 0; i < 4; i++)
		{
			int column = 0;
			
			if (docbotBools[i])
				column = 1;
			
			ImageButton button = docbotOptions[i][column];
			Point location = new Point((int) button.getX(), (int) button.getY());
			
			StaticImage pressedButton = new StaticImage(ImageType.BUTTON_72x17_CLICKED, location);
			actors.add(pressedButton.getImage());
		}
		
		return actors;
	}
}
