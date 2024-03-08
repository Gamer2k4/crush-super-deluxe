package main.presentation.screens.teameditor;

import java.awt.Point;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import main.data.entities.Player;
import main.data.entities.Team;
import main.presentation.ImageType;
import main.presentation.TeamColorsManager;
import main.presentation.common.PlayerTextFactory;
import main.presentation.common.ScreenCommand;
import main.presentation.game.GameText;
import main.presentation.game.StaticImage;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.screens.teameditor.utilities.GUIPlayerAttributes;

public class TeamEditorDetailedRoster extends AbstractTeamEditorRosterScreen
{
	protected TeamEditorDetailedRoster(TeamEditorParentScreen parentScreen)
	{
		super(parentScreen);
		subScreenImage = new StaticImage(ImageType.SCREEN_TEAM_EDITOR_ROSTER_DETAILED, screenOrigin);
	}

	@Override
	protected void handleCommand(ScreenCommand command)
	{
		if (ScreenCommand.SCROLL_ROSTER_UP.equals(command))
			scrollUp();
		if (ScreenCommand.SCROLL_ROSTER_DOWN.equals(command))
			scrollDown();
	}
	
	//note that in the original game, scrolling while in detailed view doesn't change the topIndex AT ALL
	private void scrollUp()
	{
		if (teamUpdater.getCurrentPlayerIndex() > 0)
			teamUpdater.selectPreviousPlayer();
	}

	private void scrollDown()
	{
		if (teamUpdater.getCurrentPlayerIndex() < Team.MAX_TEAM_SIZE - 1)
			teamUpdater.selectNextPlayer();
	}
	
	@Override
	public List<Actor> getActors()
	{
		List<Actor> actors = super.getActors();
		
		Player player = teamUpdater.getCurrentlySelectedPlayer();
		
		if (player == null)
			return actors;
		
		Texture playerTexture = TeamColorsManager.getInstance().getPlayerImage(player.getRace(), teamUpdater.getMainColor(), teamUpdater.getTrimColor());
		Image playerImage = new Image(new TextureRegionDrawable(playerTexture));
		playerImage.setPosition(400, 221);
		actors.add(playerImage);
		
		return actors;
	}
	
	@Override
	public List<GameText> getScreenTexts()
	{
		List<GameText> screenTexts = super.getScreenTexts();
		
		screenTexts.add(GameText.small(new Point(405, 77), LegacyUiConstants.COLOR_LEGACY_GREY, PLAYER_LABELS.charAt(teamUpdater.getCurrentPlayerIndex()) + ""));
		screenTexts.add(GameText.small2(new Point(556, 131), LegacyUiConstants.COLOR_LEGACY_GREY, "SKILL"));
		screenTexts.add(GameText.small2(new Point(553, 137), LegacyUiConstants.COLOR_LEGACY_GREY, "POINTS"));
		
		Player player = teamUpdater.getCurrentlySelectedPlayer();
		
		if (player == null)
			return screenTexts;
		
		PlayerTextFactory.setPlayer(player);
		
		screenTexts.add(GameText.smallSpread(new Point(429, 77), LegacyUiConstants.COLOR_LEGACY_GREY, player.name));
		
		String ranking = GUIPlayerAttributes.getRank(player);
		screenTexts.add(GameText.small2(new Point(GameText.getStringStartX(GameText.small2, 472, 95, ranking), 112), LegacyUiConstants.COLOR_LEGACY_GOLD, ranking));
		
		String season = GUIPlayerAttributes.getSeasons(player);
		screenTexts.add(GameText.small2(new Point(GameText.getStringStartX(GameText.small2, 472, 95, season), 129), LegacyUiConstants.COLOR_LEGACY_GOLD, season));
		
		String rating = GUIPlayerAttributes.getRating(player);
		screenTexts.add(GameText.small2(new Point(GameText.getStringStartX(GameText.small2, 472, 95, rating), 146), LegacyUiConstants.COLOR_LEGACY_GOLD, rating));
		
		String skillPoints = GUIPlayerAttributes.getSkillPoints(player);
		screenTexts.add(GameText.small2(new Point(GameText.getStringStartX(GameText.small2, 553, 35, skillPoints), 146), LegacyUiConstants.COLOR_LEGACY_GOLD, skillPoints));
		
		String status = GUIPlayerAttributes.getStatus(player);
		screenTexts.add(GameText.small2(new Point(GameText.getStringStartX(GameText.small2, 472, 95, status), 163), LegacyUiConstants.COLOR_LEGACY_GOLD, status));
		
		for (int i = 0; i < 8; i++)
		{
			screenTexts.add(getAttributeText(new Point(400 + (20 * i), 189), i));
		}
		
		screenTexts.add(getPlayerCost(player));
		
		List<GameText> skillsTextBox = PlayerTextFactory.getSkills(new Point(400, 209));
		
		screenTexts.addAll(skillsTextBox);
		
		List<GameText> quirksTextBox = PlayerTextFactory.getQuirks(new Point(400, 287));
		
		screenTexts.addAll(quirksTextBox);
		
		return screenTexts;
	}

	private GameText getAttributeText(Point point, int attribute)
	{
		GameText attributeText = PlayerTextFactory.getColoredAttributeWithModifiers(attribute, LegacyUiConstants.COLOR_LEGACY_GOLD, LegacyUiConstants.COLOR_LEGACY_GREEN);
		attributeText.setCoords(point);
		return attributeText;
	}
	
	private GameText getPlayerCost(Player player)
	{
		String cost = GUIPlayerAttributes.getValue(player);
		if (cost.charAt(0) == '0')
			cost = cost.substring(1);
		
		int costX = 566;
		if (cost.length() == 3)
			costX = 560;
		
		GameText costText = PlayerTextFactory.getColoredCost(LegacyUiConstants.COLOR_LEGACY_GOLD, LegacyUiConstants.COLOR_LEGACY_GREEN);
		costText.setCoords(new Point(costX, 189));
		return costText;
	}
}
