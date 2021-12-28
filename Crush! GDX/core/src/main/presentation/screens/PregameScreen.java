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
import main.data.entities.Player;
import main.data.entities.Team;
import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.TeamColorsManager;
import main.presentation.audio.AudioManager;
import main.presentation.audio.SoundType;
import main.presentation.common.ScreenCommand;
import main.presentation.game.ArenaImageGenerator;
import main.presentation.game.GameText;

public class PregameScreen extends MouseOverButtonScreen
{
	private ImageType backgroundImage;
	private List<GameText> names = new ArrayList<GameText>();

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
		AudioManager.getInstance().playSound(SoundType.PREGAME);
		startPreloadThread();
		addArenaImage();
		addPlayerImages();
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

	private void addPlayerImages()
	{
		List<Team> teams = EventDetails.getTeams();
		
		for (int t = 0; t < teams.size(); t++)
		{
			Team team = teams.get(t);
			
			for (int i = 8; i >= 0; i--)
			{
				Player player = team.getPlayer(i);
				
				if (player == null)
					continue;
				
				if (player.getWeeksOut() > 0)	//I think the original game shows players that have one week left as well, though that may be a mistake on their part
					continue;
				
				Image playerImage = new Image(TeamColorsManager.getInstance().getPlayerImage(team, player.getRace()));
				
				int positionIndex = (i + 1) / 2;	//integer division rounds this down
				int indexMultiplier = 1;
				
				if (i % 2 == 0)
					indexMultiplier = -1;
				
				playerImage.setPosition(275 + (indexMultiplier * 45 * positionIndex), (129 * (3 - t) - 108));
				stage.addActor(playerImage);
			}
		}
			
	}
	
	@Override
	public List<GameText> getStaticText()
	{
		return names;
	}
}
