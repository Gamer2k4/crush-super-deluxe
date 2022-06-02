package main.execute;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import main.data.Data;
import main.data.DataImpl;
import main.data.entities.Team;
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
	private ScreenType gameSourceScreen = null;

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
	
	private void setScreen(ScreenType screen)
	{
		setScreen(GameScreenManager.getInstance().getScreen(screen));
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
			spriteBatch.setColor(DebugConstants.BG_TINT);		//this doesn't actually do what I want it to, but it's a cool in-game effect
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

	private void prepareNewGame()
	{
		if (gameSourceScreen == null)
		{
			Logger.error("Cannot start a game without a source screen to return to!");
			return;
		}
		
		AudioManager.getInstance().stopSound(SoundType.THEME);
		
		AbstractTeamSelectScreen sourceScreen = (AbstractTeamSelectScreen)GameScreenManager.getInstance().getScreen(gameSourceScreen);
		List<Team> gameTeams = sourceScreen.getTeamsForNextGame();
		int arenaIndex = gameTeams.get(0).homeField;		//TODO: update this for playoffs
		
		if (DebugConstants.ARENA_OVERRIDE != -1)
			arenaIndex = DebugConstants.ARENA_OVERRIDE;
		
		EventDetails.setTeams(gameTeams);
		EventDetails.setArena(arenaIndex);
		EventDetails.setPace(sourceScreen.getPace());
		EventDetails.setTurns(sourceScreen.getTurns());
		
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

		host.newGame(EventDetails.getTeams(), EventDetails.getArenaIndex(), EventDetails.getPace(), EventDetails.getTurns());

		//TODO: set this elsewhere
		/* DEBUG */ EventDetails.getTeams().get(0).humanControlled = DebugConstants.PLAYER0_IS_HUMAN;
		/* DEBUG */ EventDetails.getTeams().get(1).humanControlled = DebugConstants.PLAYER1_IS_HUMAN;
		/* DEBUG */ EventDetails.getTeams().get(2).humanControlled = DebugConstants.PLAYER2_IS_HUMAN;
		
		client.getGui().beginGame();
	}

	private void endCurrentGame()
	{
		gameIsActive = false;
		host.endGame();
		Data data = host.getData();
		int gameWinner = data.getWinningTeamIndex();
		
		if (gameWinner == DataImpl.GAME_IN_PROGRESS)
		{
			gameWinner = DataImpl.GAME_CANCELLED;
			data.endGame(gameWinner);
		}
		
		Logger.debug("Game is done, winning team is: " + data.getWinningTeamIndex());
		
		if (gameSourceScreen == null)
		{
			Logger.error("No source screen to return to; exiting game.");
			Gdx.app.exit();
		}
		
		AbstractTeamSelectScreen sourceScreen = (AbstractTeamSelectScreen)GameScreenManager.getInstance().getScreen(gameSourceScreen);
		sourceScreen.updateRecords(gameWinner);
		
		if (gameWinner != DataImpl.GAME_CANCELLED)
		{
			//I'll probably need to update this if Data shuffles the teams at the start of the game
			sourceScreen.updateTeam(0, data.getTeam(0));
			sourceScreen.updateTeam(1, data.getTeam(1));
			sourceScreen.updateTeam(2, data.getTeam(2));
		}
		
		if (sourceScreen.isEventCompleted())
			setScreen(sourceScreen.getVictoryScreenType());
		else
			setScreen(gameSourceScreen);
		
		gameSourceScreen = null;
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
		case MAIN_SCREEN:
			activeScreen.reset();
			setScreen(ScreenType.GAME_SELECT);
			activeScreen.reset();
			break;
		case EXHIBITION_TEAM_SELECT:
			setScreen(ScreenType.EXHIBITION_TEAM_SELECT);
			break;
		case EXHIBITION_PREGAME:
			gameSourceScreen = ScreenType.EXHIBITION_TEAM_SELECT; 
			setScreen(ScreenType.EXHIBITION_PREGAME);
			activeScreen.reset();
			prepareNewGame();
			break;
		case EXHIBITION_VICTORY:
			setScreen(ScreenType.EXHIBITION_VICTORY);
			break;
		case BEGIN_GAME:
			AudioManager.getInstance().stopSound(SoundType.PREGAME);
			AudioManager.getInstance().loopSound(SoundType.CROUD);
			beginNewGame();
			break;
		case END_GAME:
			AudioManager.getInstance().stopSound(SoundType.VICTORY);
			AudioManager.getInstance().stopSound(SoundType.CROUD);
			AudioManager.getInstance().loopSound(SoundType.THEME);
			endCurrentGame();
			break;
		}
	}
}
