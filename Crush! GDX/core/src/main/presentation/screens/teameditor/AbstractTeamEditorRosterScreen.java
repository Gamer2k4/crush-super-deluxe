package main.presentation.screens.teameditor;

import java.awt.Dimension;
import java.awt.Point;
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

public abstract class AbstractTeamEditorRosterScreen extends AbstractTeamEditorSubScreen
{
	protected int topIndex = 0;
	
	private static final Dimension buttonDimScroll = new Dimension(45, 20);
	
	protected AbstractTeamEditorRosterScreen(TeamEditorParentScreen parentScreen)
	{
		super(parentScreen);
		screenOrigin = new Point(ImageFactory.getInstance().getImageWidth(ImageType.SCREEN_TEAM_EDITOR_START), 0);
	}
	
	@Override
	public List<Actor> getActors()
	{
		List<Actor> actors = super.getActors();
		
		Image helmetImage = new Image(TeamColorsManager.getInstance().getHelmetImage(teamUpdater.getTeam()));
		helmetImage.setPosition(393, 336);
		actors.add(helmetImage);
		
		return actors;
	}
	
	@Override
	public List<GameText> getScreenTexts()
	{
		List<GameText> screenTexts = super.getScreenTexts();
		
		screenTexts.add(new GameText(FontType.FONT_SMALL2, new Point(462, 30), LegacyUiConstants.COLOR_LEGACY_BLACK, teamUpdater.getTeam().coachName));
		screenTexts.add(new GameText(FontType.FONT_SMALL, new Point(428, 34), LegacyUiConstants.COLOR_LEGACY_BLACK, teamUpdater.getTeam().teamName));
		
		return screenTexts;
	}
	
	@Override
	public List<ImageButton> getScreenButtons()
	{
		List<ImageButton> screenButtons = super.getScreenButtons();
		
		//TODO: see if i've extracted highlight images for these buttons
		//TODO: a clickmap image is define in the original files for these; I can just paint that on the clickmap screen like I do for the action buttons in the game window
		
		screenButtons.add(parentScreen.addClickZone(447, 329, buttonDimScroll.width, buttonDimScroll.height, ScreenCommand.SCROLL_UP));
		screenButtons.add(parentScreen.addClickZone(504, 329, buttonDimScroll.width, buttonDimScroll.height, ScreenCommand.SCROLL_DOWN));
		
		return screenButtons;
	}

	@Override
	protected void refreshContent()
	{
		topIndex = 0;
	}
}
