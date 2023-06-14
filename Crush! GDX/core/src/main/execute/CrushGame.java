package main.execute;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

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
import main.presentation.game.sprite.StaticSprite;
import main.presentation.screens.CrushEventScreen;
import main.presentation.screens.EventDetails;
import main.presentation.screens.GameScreen;
import main.presentation.screens.GameScreenManager;
import main.presentation.screens.PregameScreen;
import main.presentation.screens.ScreenType;
import main.presentation.screens.popupready.PopupReadyScreen;
import main.presentation.screens.stats.AbstractTeamStatsParentScreen;
import main.presentation.screens.stats.TeamStatsTeamParentScreen;
import main.presentation.screens.teameditor.TeamEditor;
import main.presentation.screens.teameditor.TeamEditorParentScreen;
import main.presentation.screens.teameditor.utilities.TeamUpdater;
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
		TeamColorsManager.getInstance().refresh(new Team());		//initialize images for default color scheme (gold on gold)
	}
	
	private void setScreen(ScreenType screen)
	{
		setScreen(GameScreenManager.getInstance().getScreen(screen));
	}

	@Override
	public void setScreen(Screen screen)
	{
		GameScreen gamescreen = (GameScreen) screen;
		updateActiveScreen(gamescreen);
		super.setScreen(screen);
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
		renderStaticSprites();
		renderStaticText();
		renderPopup();
		
		//draws a darkening rectangle, like needed for the equipment tab, but at present it doesn't stretch if the screen size changes
//		Gdx.gl.glEnable(GL20.GL_BLEND);
//	    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//	    ShapeRenderer shapeRenderer = new ShapeRenderer();
//	    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//	    shapeRenderer.setColor(new Color(0, 0, 0, 0.5f));
//	    shapeRenderer.rect(264, 135, 100, 51);
//	    shapeRenderer.end();
//	    Gdx.gl.glDisable(GL20.GL_BLEND);
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
	
	private void renderStaticSprites()
	{
		List<CrushSprite> sprites = getCurrentScreen().getStaticSprites();
		
		spriteBatch.setProjectionMatrix(fixedCamera.combined);
		spriteBatch.begin();
		
		for (CrushSprite sprite : sprites)
		{
			if (!sprite.getImage().isFlipY())
				sprite.getImage().flip(false, true);
			
			Sprite spriteWithAlpha = new Sprite(sprite.getImage());
			spriteWithAlpha.setPosition(sprite.getX(), sprite.getY());
			spriteWithAlpha.draw(spriteBatch, sprite.getAlpha());
		}
		
		spriteBatch.end();
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
	
	private void renderPopup()
	{
		if (!(getCurrentScreen() instanceof PopupReadyScreen))
			return;
		
		PopupReadyScreen popupScreen = (PopupReadyScreen) getCurrentScreen();
		
		if (!popupScreen.popupIsActive())
			return;
		
		spriteBatch.setProjectionMatrix(fixedCamera.combined);
		spriteBatch.begin();
		
		StaticSprite sprite = popupScreen.getDialog();
		
		if (!sprite.getImage().isFlipY())
			sprite.getImage().flip(false, true);
		
		spriteBatch.draw(sprite.getImage(), sprite.getX(), sprite.getY());
		
		for (GameText text : popupScreen.getPopupText())
		{
			text.render(spriteBatch);
		}
		
		spriteBatch.end();

		for (ImageButton button : popupScreen.getPopupButtons())
			popupScreen.getStage().addActor(button);
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
			//Also, this resets all equipment, which technically should happen at the start of the game, not the end
			sourceScreen.updateTeam(0, data.getTeam(0).advanceWeek());
			sourceScreen.updateTeam(1, data.getTeam(1).advanceWeek());
			sourceScreen.updateTeam(2, data.getTeam(2).advanceWeek());
		}
		
		//done via screen commands to make the audio change appropriately
		if (sourceScreen.isEventCompleted())
			executeScreenCommand(ScreenCommand.fromScreenType(sourceScreen.getVictoryScreenType()));
		else
			executeScreenCommand(ScreenCommand.fromScreenType(gameSourceScreen));
		
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
		try {
			ScreenCommand command = ScreenCommand.fromActionEvent(e);
			executeScreenCommand(command);
		} catch (IllegalArgumentException iae)
		{
			//don't process events we can't parse
		}
	}

	private void executeScreenCommand(ScreenCommand command)
	{
		if (command.isEditTeam())
		{
			flowToTeamEditorForTeamIndex(command.getCommandIndex());
			return;
		}
		
		if (command.isEventPregame())
		{
			AbstractTeamSelectScreen teamSelectScreen = (AbstractTeamSelectScreen) getScreen();
			if (!teamSelectScreen.getTeamsOverBudget().isEmpty())
				return;	//the screen itself will handle the popup
		}
		
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
			AudioManager.getInstance().stopSound(SoundType.PREGAME);
			AudioManager.getInstance().loopSound(SoundType.THEME);
			setScreen(ScreenType.EXHIBITION_TEAM_SELECT);
			break;
		case EXHIBITION_PREGAME:
			gameSourceScreen = ScreenType.EXHIBITION_TEAM_SELECT;
			setScreen(ScreenType.EXHIBITION_PREGAME);
			activeScreen.reset();
			prepareNewGame();
			break;
		case EXHIBITION_VICTORY:
			AudioManager.getInstance().stopSound(SoundType.THEME);
			AudioManager.getInstance().playSound(SoundType.PREGAME);
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
		case STATS_VIEW:
			flowToTeamStatsFromTeamEditor();
			break;
		case STATS_BACK:
			returnFromStatsScreen();
			break;
		case EXIT_TEAM_EDITOR_DONE:
			flowToTeamSelectFromTeamEditor();
			break;
		}
	}

	private void flowToTeamEditorForTeamIndex(int teamIndex)
	{
		Logger.debug("Flowing to team editor for team index [" + teamIndex + "].");
		
		AbstractTeamSelectScreen sourceScreen = (AbstractTeamSelectScreen) activeScreen;
		Team teamToEdit = sourceScreen.getTeam(teamIndex);
		int budget = sourceScreen.getBudget();
		setScreen(ScreenType.TEAM_EDITOR);
		TeamEditor editorScreen = (TeamEditor) activeScreen;
		editorScreen.setTeamIndex(teamIndex);
		editorScreen.setTeam(teamToEdit);
		editorScreen.setBudget(budget);
		editorScreen.setOriginScreen(GameScreenManager.getInstance().getScreenType(sourceScreen));
		activeScreen.reset();
	}

	private void flowToTeamSelectFromTeamEditor()
	{
		activeScreen.reset();
		TeamEditor editorScreen = (TeamEditor) activeScreen;
		setScreen(editorScreen.getOriginScreen());
		AbstractTeamSelectScreen teamSelectScreen = (AbstractTeamSelectScreen) activeScreen;
		
		TeamUpdater teamUpdater = editorScreen.getTeamUpdater();
		int teamIndex = editorScreen.getTeamIndex();
		Team team = teamUpdater.getTeam();
		
		teamSelectScreen.updateTeam(teamIndex, team);
		teamSelectScreen.activate();		//technically it's already activated, but this refreshes the content after the team in the editor has been loaded
	}
	
	private void flowToTeamStatsFromTeamEditor()
	{
		//DON'T reset the team editor; in the original game, everything is as though you never left
		TeamEditorParentScreen editorScreen = (TeamEditorParentScreen) activeScreen;
		TeamStatsTeamParentScreen statsScreen = (TeamStatsTeamParentScreen) GameScreenManager.getInstance().getScreen(ScreenType.TEAM_STATS_TEAM);
		statsScreen.setOriginScreen(editorScreen);
		statsScreen.setTeam(editorScreen.getTeam());
		setScreen(statsScreen);
	}

	private void returnFromStatsScreen()
	{
		activeScreen.reset();
		AbstractTeamStatsParentScreen statsScreen = (AbstractTeamStatsParentScreen) activeScreen;
		setScreen(statsScreen.getOriginScreen());
		//no resetting of the new screen, since its state is supposed to be unchanged
	}
}
