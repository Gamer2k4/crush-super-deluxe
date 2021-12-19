package main.presentation.screens;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import main.presentation.CursorManager;
import main.presentation.game.GameText;
import main.presentation.game.StaticImage;
import main.presentation.game.sprite.CrushSprite;

//I believe this is the equivalent of AbstractLegacyScreen in the original project
public abstract class GameScreen implements Screen
{
	public static Dimension LEGACY_SCREEN_DIMENSION = new Dimension(640, 400);
	
	protected boolean isActive = false;
	
	protected Game game;
	protected Stage stage;
	
	protected GameScreen(Game sourceGame)
	{
		game = sourceGame;
		stage = new Stage(new StretchViewport(640, 400));
	}

	@Override
	public void show()
	{
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void resize(int width, int height)
	{
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void render(float delta)
	{
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
	}

	@Override
	public void pause()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void resume()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void hide()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose()
	{
		stage.dispose();
	}
	
	public void reset()
	{
		return;
	}
	
	public void update()
	{
		return;
	}
	
	public Stage getStage()
	{
		return stage;
	}
	
	public OrthographicCamera getCamera()
	{
		return null;
	}
	
	public Texture getBackgroundImage()
	{
		return null;
	}

	public List<CrushSprite> getSpritesToRender()
	{
		return new ArrayList<CrushSprite>();
	}
	
	public void activate()
	{
		isActive = true;
	}
	
	public void deactivate()
	{
		isActive = false;
	}

	public List<GameText> getStaticText()
	{
		return new ArrayList<GameText>();
	}

	public List<StaticImage> getStaticImages()
	{
		return new ArrayList<StaticImage>();
	}
	
	public Cursor getCursor()
	{
		return CursorManager.crush();
	}
}
