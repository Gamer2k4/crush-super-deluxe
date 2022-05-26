package main.execute;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import main.data.entities.Team;
import main.data.factory.CpuTeamFactory;
import main.logic.Client;
import main.logic.Randomizer;
import main.logic.Server;
import main.presentation.CursorManager;
import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.TeamColorsManager;
import main.presentation.audio.AudioManager;
import main.presentation.audio.SoundType;
import main.presentation.common.Logger;
import main.presentation.common.ScreenCommand;
import main.presentation.game.ArenaImageGenerator;
import main.presentation.game.GameText;
import main.presentation.game.GdxGUI;
import main.presentation.game.StaticImage;
import main.presentation.game.sprite.CrushAnimatedTile;
import main.presentation.game.sprite.CrushArenaImageManager;
import main.presentation.game.sprite.CrushSprite;
import main.presentation.screens.CrushEventScreen;
import main.presentation.screens.EventDetails;
import main.presentation.screens.GameScreen;
import main.presentation.screens.GameScreenManager;
import main.presentation.screens.PregameScreen;
import main.presentation.screens.ScreenType;
import main.presentation.screens.teamselect.AbstractTeamSelectScreen;

//I believe this is the equivalent of GameWindow in the original project
public class CrushGame extends Game implements ActionListener
{
	private Server host = new Server();
	private Client client = null;

	private GameScreen activeScreen = null;

	private SpriteBatch spriteBatch;
	
	private OrthographicCamera fixedCamera;
	
	private boolean gameIsActive = false;

	@Override
	public void create()
	{
		spriteBatch = new SpriteBatch();
		fixedCamera = new OrthographicCamera();
		fixedCamera.setToOrtho(true, 640, 400);
		
		GameScreenManager.getInstance().initializeScreens(this);
		setScreen(GameScreenManager.getInstance().getScreen(ScreenType.GAME_SELECT));
		AudioManager.getInstance().loopSound(SoundType.THEME);
	}

	@Override
	public void setScreen(Screen screen)
	{
		GameScreen gamescreen = (GameScreen) screen;
		
		super.setScreen(screen);

		updateActiveScreen(gamescreen);

		if (screen == null)
			return;
		
		//TODO: don't do this just yet, even though it would be nice
//		AudioManager.getInstance().updateBackground(ScreenType.GAME_SELECT);
	}

	private void updateActiveScreen(GameScreen newActiveScreen)
	{
		if (activeScreen != null)
			activeScreen.deactivate();

		activeScreen = newActiveScreen;
		activeScreen.activate();
		Gdx.graphics.setCursor(newActiveScreen.getCursor());
	}

	@Override
	public void render()
	{
		GameScreen currentScreen = getCurrentScreen();
		
		currentScreen.update();
		Texture backgroundImage = currentScreen.getBackgroundImage();
		
		if (backgroundImage != null)
		{
			spriteBatch.setProjectionMatrix(fixedCamera.combined);
			spriteBatch.begin();
			spriteBatch.draw(backgroundImage, 0, 0);
			spriteBatch.end();
		}

		if (currentScreen.getCamera() != null)
			manageCameraDrivenRendering(currentScreen.getCamera());
		else
			super.render();
		
		renderStaticImages();
		renderStaticText();
	}

	private void manageCameraDrivenRendering(OrthographicCamera camera)
	{
		camera.update();
		CrushAnimatedTile.increaseElapsedTime(Gdx.graphics.getDeltaTime());

		List<CrushSprite> spritesToRender = getCurrentScreen().getSpritesToRender();

		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();

		for (CrushSprite sprite : spritesToRender)
		{
			spriteBatch.draw(sprite.getImage(), sprite.getX(), sprite.getY());
		}

		spriteBatch.end();

		//TODO: render stat text over players (if that's selected, of course)
		
		getCurrentScreen().getStage().draw();
	}
	
	private void renderStaticImages()
	{
		List<StaticImage> images = getCurrentScreen().getStaticImages(); 
		
		for (StaticImage image : images)
		{
			getCurrentScreen().getStage().addActor(image.getImage());
		}
		
		getCurrentScreen().getStage().draw();
	}
	
	private void renderStaticText()
	{
		List<GameText> gameTexts = getCurrentScreen().getStaticText();
		
		spriteBatch.setProjectionMatrix(fixedCamera.combined);
		spriteBatch.begin();
		
		for (GameText text : gameTexts)
		{
			text.render(spriteBatch);
		}
		
		spriteBatch.end();
	}

	@Override
	public void dispose()
	{
		GameScreenManager.getInstance().dispose();
		CrushArenaImageManager.getInstance().dispose();
		TeamColorsManager.getInstance().dispose();
		ImageFactory.getInstance().dispose();
		AudioManager.getInstance().dispose();
		CursorManager.getInstance().dispose();
		ArenaImageGenerator.dispose();
		GameText.dispose();
		spriteBatch.dispose();
	}

	private GameScreen getCurrentScreen()
	{
		return (GameScreen) getScreen();
	}

	private void prepareNewGame(AbstractTeamSelectScreen sourceScreen)
	{
		AudioManager.getInstance().stopSound(SoundType.THEME);
		
//		List<Team> gameTeams = getTeamsForGameStart(sourceScreen.getTeams(), sourceScreen.getBudget());
		List<Team> rawTeams = new ArrayList<Team>();
		rawTeams.add(new Team());
		rawTeams.add(new Team());
		rawTeams.add(new Team());
		
		List<Team> gameTeams = getTeamsForGameStart(rawTeams, 900);
		int arenaIndex = gameTeams.get(0).homeField;		//TODO: update this for playoffs
		
		if (DebugConstants.ARENA_OVERRIDE != -1)
			arenaIndex = DebugConstants.ARENA_OVERRIDE;
		
		EventDetails.setTeams(gameTeams);
		EventDetails.setArena(arenaIndex);
		
		PregameScreen pregameScreen = (PregameScreen) activeScreen;
		pregameScreen.newGame();
	}
	
	private void beginNewGame()
	{
		if (gameIsActive)
		{
			Logger.warn("Attempting to begin a new game while one is ongoing!");
			return;
		}
		
		gameIsActive = true;
		client = new Client(host, this);

		setScreen(GameScreenManager.getInstance().getScreen(ScreenType.GAME_PLAY));
		randomizeEventBackgroundImage();
		activeScreen.reset();

		GdxGUI gui = (GdxGUI) client.getGui();
		gui.setGameScreen(activeScreen);

		host.newGame(EventDetails.getTeams(), EventDetails.getArenaIndex());

		//TODO: set this elsewhere
		/* DEBUG */ EventDetails.getTeams().get(1).humanControlled = false;
		/* DEBUG */ EventDetails.getTeams().get(2).humanControlled = false;
		
		client.getGui().beginGame();
	}

	private void randomizeEventBackgroundImage()
	{
		CrushEventScreen eventScreen = (CrushEventScreen) activeScreen;
		
		int backgroundChoice = Randomizer.getRandomInt(1, 2);
		
		if (backgroundChoice == 1)
			eventScreen.setBackgroundImage(ImageType.MAP_LAVA_BG);
		else
			eventScreen.setBackgroundImage(ImageType.MAP_STARS_BG);
	}

	private List<Team> getTeamsForGameStart(List<Team> rawTeams, int budget)
	{
		List<Team> preparedTeams = new ArrayList<Team>();

		for (Team team : rawTeams)
		{
			if (team.isBlankTeam())
				team = CpuTeamFactory.generatePopulatedCpuTeam(budget);

			preparedTeams.add(team);

			if (preparedTeams.size() == 3)
				break;
		}

		while (preparedTeams.size() < 3)
			preparedTeams.add(null);

		return preparedTeams;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		ScreenCommand command = ScreenCommand.fromActionEvent(e);
		executeScreenCommand(command);
	}

	private void executeScreenCommand(ScreenCommand command)
	{
		// This is where screen swaps happen
		switch (command)
		{
		case EXIT:
			Gdx.app.exit();
			break;
		case EXHIBITION_PREGAME:
			setScreen(GameScreenManager.getInstance().getScreen(ScreenType.EXHIBITION_PREGAME_SCREEN));
			activeScreen.reset();
			prepareNewGame(null);
			break;
		case BEGIN_GAME:
			AudioManager.getInstance().stopSound(SoundType.PREGAME);
			AudioManager.getInstance().loopSound(SoundType.CROUD);
			beginNewGame();
			break;
		}
	}
}
