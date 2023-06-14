package main.presentation.screens.teameditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.TeamColorsManager;
import main.presentation.common.ScreenCommand;
import main.presentation.game.FontType;
import main.presentation.game.GameText;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.screens.teameditor.utilities.TeamUpdater;

public abstract class AbstractTeamEditorRosterScreen extends AbstractTeamEditorSubScreen
{
	protected int topIndex = 0;
	
	private static final Dimension SCROLL_BUTTON_DIM = new Dimension(45, 20);
	
	private Image helmetImage;
	private GameText totalCost;
	private GameText treasury;
	
	private DecimalFormat threeDigitFormatter = new DecimalFormat("000");
	
	protected AbstractTeamEditorRosterScreen(TeamEditorParentScreen parentScreen)
	{
		super(parentScreen);
		screenOrigin = new Point(ImageFactory.getInstance().getImageWidth(ImageType.SCREEN_TEAM_EDITOR_START), 0);
		updateHelmetImage();
		refreshBudgetText();
	}

	private void updateHelmetImage()
	{
		helmetImage = new Image(TeamColorsManager.getInstance().getHelmetImage(teamUpdater.getTeam()));
		helmetImage.setPosition(393, 336);
	}
	
	private void refreshBudgetText()
	{
		int totalCostInt = teamUpdater.getTeam().getValue();
		int treasuryInt = parentScreen.getBudget() - totalCostInt;
		Color treasuryColor = LegacyUiConstants.COLOR_LEGACY_GOLD;
		FontType totalCostFont = FontType.FONT_SMALL_SPREAD;
		Point totalCostOrigin = new Point(516, 347);
		
		if (treasuryInt < 0)
		{
			treasuryInt = -1 * treasuryInt;
			treasuryColor = LegacyUiConstants.COLOR_LEGACY_RED;
		}
		
		if (totalCostInt > 999)
		{
			totalCostFont = FontType.FONT_SMALL2;
			totalCostOrigin = new Point(516, 352);
		}
		
		totalCost = new GameText(totalCostFont, totalCostOrigin, LegacyUiConstants.COLOR_LEGACY_GOLD, threeDigitFormatter.format(totalCostInt));
		treasury = new GameText(FontType.FONT_SMALL_SPREAD, new Point(516, 367), treasuryColor, threeDigitFormatter.format(treasuryInt));
	}

	@Override
	public List<Actor> getActors()
	{
		List<Actor> actors = super.getActors();
		
		actors.add(helmetImage);
		
		return actors;
	}
	
	@Override
	public List<GameText> getScreenTexts()
	{
		List<GameText> screenTexts = super.getScreenTexts();
		
		screenTexts.add(new GameText(FontType.FONT_SMALL2, new Point(462, 30), LegacyUiConstants.COLOR_LEGACY_BLACK, teamUpdater.getTeam().coachName));
		screenTexts.add(new GameText(FontType.FONT_SMALL, new Point(428, 34), LegacyUiConstants.COLOR_LEGACY_BLACK, teamUpdater.getTeam().teamName));
		screenTexts.add(totalCost);
		screenTexts.add(treasury);
		
		return screenTexts;
	}
	
	@Override
	public List<ImageButton> getScreenButtons()
	{
		List<ImageButton> screenButtons = super.getScreenButtons();
		
		//TODO: see if i've extracted highlight images for these buttons
		//TODO: a clickmap image is define in the original files for these; I can just paint that on the clickmap screen like I do for the action buttons in the game window
		
		screenButtons.add(parentScreen.addClickZone(447, 329, SCROLL_BUTTON_DIM.width, SCROLL_BUTTON_DIM.height, ScreenCommand.SCROLL_ROSTER_UP));
		screenButtons.add(parentScreen.addClickZone(504, 329, SCROLL_BUTTON_DIM.width, SCROLL_BUTTON_DIM.height, ScreenCommand.SCROLL_ROSTER_DOWN));
		
		return screenButtons;
	}

	@Override
	protected void refreshContent()
	{
		topIndex = 0;
		updateHelmetImage();
		refreshBudgetText();
	}
	
	@Override
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();
		
		if (command.equals(TeamUpdater.UPDATER_NEW_TEAM) || command.equals(TeamUpdater.UPDATER_COLORS_CHANGED))
		{
			updateHelmetImage();
			refreshParentStage();
		}
		
		if (command.equals(TeamUpdater.UPDATER_NEW_TEAM) || command.equals(TeamUpdater.UPDATER_DOCBOT_CHANGED) || command.equals(TeamUpdater.UPDATER_EQUIPMENT_CHANGED) || command.equals(TeamUpdater.UPDATER_PLAYERS_CHANGED))
			refreshBudgetText();	
	}
}
