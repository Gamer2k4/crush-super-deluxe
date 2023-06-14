package main.presentation.screens;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import main.data.GameDataPreloader;
import main.data.entities.Team;
import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.audio.AudioManager;
import main.presentation.audio.SoundType;
import main.presentation.common.ScreenCommand;
import main.presentation.common.image.TeamLineupGenerator;
import main.presentation.game.ArenaImageGenerator;
import main.presentation.game.GameText;
import main.presentation.game.StaticImage;

public class PregameScreen extends MouseOverButtonScreen
{
	private ImageType backgroundImage;
	private List<GameText> names = new ArrayList<GameText>();
	private List<StaticImage> playerImages = new ArrayList<StaticImage>();

	protected PregameScreen(Game sourceGame, ActionListener eventListener, ImageType backgroundImage)
	{
		super(sourceGame, eventListener);
		this.backgroundImage = backgroundImage;
		reset();
	}

	@Override
	public void reset()
	{
		// screenIsReady = false;
		stage.clear();
		stage.addActor(new Image(ImageFactory.getInstance().getDrawable(backgroundImage)));
		addButton("GAME_START_BUTTON", 542, 23, ScreenCommand.BEGIN_GAME);
	}

	public void newGame()
	{
		startPreloadThread();
		addArenaImage();
		definePlayerImages();
		AudioManager.getInstance().playSound(SoundType.PREGAME);		//done here because the screen won't show up until at least the player profile images are loaded
		addTeamAndArenaNames();
	}

	private void startPreloadThread()
	{
		Gdx.app.postRunnable(new Runnable()
		{
			@Override
			public void run()
			{
				GameDataPreloader.preloadGameData(EventDetails.getTeams(), EventDetails.getArenaIndex());
				screenIsReady = true;
			}
		});
	}

	private void addTeamAndArenaNames()
	{
		names.clear();
		List<Team> teams = EventDetails.getTeams();
		
		GameText arenaName = GameText.small2(new Point(0, 0), Color.RED, EventDetails.getArena().getName().toUpperCase());
		GameText team1Name = GameText.huge(new Point(0, 0), teams.get(0).teamName);
		GameText team2Name = GameText.huge(new Point(0, 0), teams.get(1).teamName);
		GameText team3Name = GameText.huge(new Point(0, 0), teams.get(2).teamName);
		
		int arenaStartX = arenaName.getStringStartX(22, 80);
		int team1NameStartX = team1Name.getStringStartX(0, 640);
		int team2NameStartX = team2Name.getStringStartX(0, 640);
		int team3NameStartX = team3Name.getStringStartX(0, 640);
		
		arenaName.setCoords(new Point(arenaStartX, 310));
		team1Name.setCoords(new Point(team1NameStartX, -9));
		team2Name.setCoords(new Point(team2NameStartX, 120));
		team3Name.setCoords(new Point(team3NameStartX, 250));
		
		names.add(arenaName);
		names.add(team1Name);
		names.add(team2Name);
		names.add(team3Name);
	}
	
	private void addArenaImage()
	{
		ArenaImageGenerator.generateArenaImage(EventDetails.getArena(), 30, 1);
		Image arenaImage = new Image(ArenaImageGenerator.getArenaImage());
		arenaImage.setPosition(45, 43);
		stage.addActor(arenaImage);
	}

	private void definePlayerImages()
	{
		List<Team> teams = EventDetails.getTeams();
		playerImages.clear();
		
		for (int t = 0; t < teams.size(); t++)
		{
			Team team = teams.get(t);
			playerImages.addAll(TeamLineupGenerator.getLineup(team, new Point(275, 129 * (3 - t) - 108), true));
		}	
	}
	
	@Override
	public List<GameText> getStaticText()
	{
		return names;
	}
	
	@Override
	public List<StaticImage> getStaticImages()
	{
		return playerImages;
	}
}
