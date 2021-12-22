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
import main.logic.Server;
import main.presentation.CursorManager;
import main.presentation.ImageFactory;
import main.presentation.TeamColorsManager;
import main.presentation.audio.AudioManager;
import main.presentation.common.ScreenCommand;
import main.presentation.game.ArenaImageGenerator;
import main.presentation.game.GameText;
import main.presentation.game.GdxGUI;
import main.presentation.game.StaticImage;
import main.presentation.game.sprite.CrushAnimatedTile;
import main.presentation.game.sprite.CrushSprite;
import main.presentation.screens.GameScreen;
import main.presentation.screens.GameScreenManager;
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

	@Override
	public void create()
	{
		spriteBatch = new SpriteBatch();
		fixedCamera = new OrthographicCamera();
		fixedCamera.setToOrtho(true, 640, 400);
		
		GameScreenManager.getInstance().initializeScreens(this);
		setScreen(GameScreenManager.getInstance().getScreen(ScreenType.GAME_SELECT));
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

	private void startNewGame(AbstractTeamSelectScreen sourceScreen)
	{
//		List<Team> gameTeams = getTeamsForGameStart(sourceScreen.getTeams(), sourceScreen.getBudget());
		List<Team> rawTeams = new ArrayList<Team>();
		rawTeams.add(new Team());
		rawTeams.add(new Team());
		rawTeams.add(new Team());
		
		List<Team> gameTeams = getTeamsForGameStart(rawTeams, 900);

		client = new Client(host, this);

		setScreen(GameScreenManager.getInstance().getScreen(ScreenType.GAME_PLAY));
		activeScreen.reset();

		GdxGUI gui = (GdxGUI) client.getGui();
		gui.setGameScreen(activeScreen);

		host.newGame(gameTeams);
		client.getGui().beginGame();
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
		case BEGIN_GAME:
			startNewGame(null);
			break;
		}
	}
}
