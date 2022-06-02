package main.presentation.screens.victory;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import main.data.entities.Team;
import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.TeamColorsManager;
import main.presentation.common.ScreenCommand;
import main.presentation.common.image.TeamLineupGenerator;
import main.presentation.game.FontType;
import main.presentation.game.GameText;
import main.presentation.game.StaticImage;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.screens.StandardButtonScreen;

public abstract class AbstractVictoryScreen extends StandardButtonScreen
{
	private ImageButton clickToExit = null;
	
	private GameText hugeTeamName = null;
	private GameText normalTeamName = null;
	private GameText normalCoachName = null;
	private GameText pressAnyKey = GameText.small2(new Point(208, 385), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, "PRESS ANY KEY OR MOUSE BUTTON TO CONTINUE");
	
	private List<StaticImage> teamLineup = new ArrayList<StaticImage>();
	private StaticImage coachImage = null;
	
	protected AbstractVictoryScreen(Game sourceGame, ActionListener eventListener, ScreenCommand flowToSourceScreenCommand)
	{
		super(sourceGame, eventListener);
		
		clickToExit = addClickZone(0, 0, 640, 400, flowToSourceScreenCommand);
	}

	@Override
	public void reset()
	{
		setTeam(new Team());
		stage.clear();
		stage.addActor(new Image(ImageFactory.getInstance().getDrawable(ImageType.BG_BG8)));
		stage.addActor(new Image(ImageFactory.getInstance().getDrawable(getVictoryScreenImageType())));
		stage.addActor(clickToExit);	//the way images and text are added right now, this won't work if you click on custom text or images (since they're on top)
	}
	
	public void setTeam(Team winningTeam)
	{
		updateTeamAndCoachNames(winningTeam);
		updateCoachImage(winningTeam);
		updateTeamLineup(winningTeam);
	}
	
	private void updateTeamAndCoachNames(Team winningTeam)
	{
		hugeTeamName = new GameText(FontType.FONT_HUGE, new Point(0, 0), LegacyUiConstants.COLOR_LEGACY_WHITE, winningTeam.teamName);
		int padding = 640 - hugeTeamName.getStringPixelLength();
		hugeTeamName.setCoords(new Point(padding / 2, -10));
		
		normalTeamName = new GameText(FontType.FONT_SMALL, new Point(0, 0), LegacyUiConstants.COLOR_LEGACY_GREY, winningTeam.teamName);
		padding = 640 - normalTeamName.getStringPixelLength();
		normalTeamName.setCoords(new Point(padding / 2, 288));
		
		normalCoachName = new GameText(FontType.FONT_SMALL, new Point(0, 0), LegacyUiConstants.COLOR_LEGACY_GREY, "COACH  " + winningTeam.coachName);
		padding = 640 - normalCoachName.getStringPixelLength();
		normalCoachName.setCoords(new Point(padding / 2, 263));
	}

	private void updateCoachImage(Team winningTeam)
	{
		Texture coachTexture = TeamColorsManager.getInstance().getCoachImage(winningTeam);
		coachImage = new StaticImage(coachTexture, new Point(275, 156));
	}

	private void updateTeamLineup(Team winningTeam)
	{
		teamLineup = TeamLineupGenerator.getLineup(winningTeam, new Point(275, 270), true);
	}
	
	@Override
	public List<GameText> getStaticText()
	{
		List<GameText> gameTexts = new ArrayList<GameText>();
		
		gameTexts.addAll(getTeamAndCoachNames());
		
		return gameTexts;
	}
	
	@Override
	public List<StaticImage> getStaticImages()
	{
		List<StaticImage> images = new ArrayList<StaticImage>();

		images.addAll(teamLineup);
		images.add(coachImage);
		
		return images;
	}

	private List<GameText> getTeamAndCoachNames()
	{
		List<GameText> texts = new ArrayList<GameText>();
		
		if (hugeTeamName == null)
			return texts;
		
		texts.add(hugeTeamName);
		texts.add(normalCoachName);
		texts.add(normalTeamName);
		texts.add(pressAnyKey);
		
		return texts;
	}

	@Override
	public void actionPerformed(ActionEvent e) {}	//nothing to do here

	@Override
	protected List<ImageButton> getButtons()
	{
		return new ArrayList<ImageButton>();
	}
	
	protected abstract ImageType getVictoryScreenImageType();
}
