package main.presentation.screens;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.SnapshotArray;

import main.presentation.CursorManager;
import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.common.Logger;
import main.presentation.common.ScreenCommand;
import main.presentation.game.EventButtonBarFactory;
import main.presentation.game.GameText;
import main.presentation.game.GdxGUI;
import main.presentation.game.StaticImage;
import main.presentation.game.sprite.CrushSprite;
import main.presentation.legacy.common.LegacyUiConstants;

//I believe this is the equivalent of LegacyGamePlayScreen in the original project
public class CrushEventScreen extends GameScreen
{
	public static final int VIEWPORT_HEIGHT = 320;
	// public static final int SIDEBAR_WIDTH = LegacyImageFactory.getInstance().getImage(ImageType.GAME_SIDEBAR).getWidth();
	public static final int SIDEBAR_WIDTH = 0; // TODO: give a proper value
	private static final int SCROLL_SPEED = 1000;
	
	private Texture mapTexture = ImageFactory.getInstance().getTexture(ImageType.MAP_A1);
	
	private Image buttonBar = new Image(ImageFactory.getInstance().getDrawable(ImageType.GAME_BUTTONBAR));
	private Pixmap clickMap;
	
	private int mapHeight = mapTexture.getHeight();
	private int mapWidth = mapTexture.getWidth();
	private int buttonBarHeight = (int)buttonBar.getHeight();

	private OrthographicCamera camera;
	private List<CrushSprite> activeSprites = new ArrayList<CrushSprite>();

	private boolean showStatsPanel = false;
	private boolean showHelpPanel = false;
	
	private boolean touchRegistered = false;
	
	private GdxGUI gui = null;

	protected CrushEventScreen(Game sourceGame)
	{
		super(sourceGame);
		defineClickMap();
		
		camera = new OrthographicCamera();
		// camera.setToOrtho(false, getViewportWidth(), VIEWPORT_HEIGHT);
		camera.setToOrtho(false, getViewportWidth(), 400); // display over the full screen, and just overlap it with other things
		
		Gdx.input.setInputProcessor(stage);
	}

	public void refreshTextures()
	{
		Logger.info("Refreshing textures...");
		activeSprites = gui.getActiveSprites();
	}

	@Override
	public void update()
	{
		updateCursor();
		clearActors();
		processMouseClick();
		
		if (Gdx.input.isKeyPressed(Keys.LEFT))
			camera.position.x -= SCROLL_SPEED * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
			camera.position.x += SCROLL_SPEED * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Keys.DOWN))
			camera.position.y -= SCROLL_SPEED * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Keys.UP))
			camera.position.y += SCROLL_SPEED * Gdx.graphics.getDeltaTime();

		int widthOffset = (getViewportWidth() / 2);
		int heightOffset = LEGACY_SCREEN_DIMENSION.height / 2;
		
		if (camera.position.x < widthOffset)
			camera.position.x = widthOffset;
		if (camera.position.y < heightOffset - buttonBarHeight)
			camera.position.y = heightOffset - buttonBarHeight;
		if (camera.position.x > mapWidth - widthOffset)
			camera.position.x = mapWidth - widthOffset;
		if (camera.position.y > mapHeight - heightOffset)
			camera.position.y = mapHeight - heightOffset;
	}
	
	private void updateCursor()
	{
		if (gui.inputOkay())
			Gdx.graphics.setCursor(CursorManager.crush());
		else
			Gdx.graphics.setCursor(CursorManager.computer());
	}

	private void clearActors()
	{
		SnapshotArray<Actor> actors = new SnapshotArray<Actor>(stage.getActors());
	    for(Actor actor : actors) {
	        actor.remove();
	    }
	    
	    stage.addActor(buttonBar);
	}

	//TODO: this doesn't account for sidebars yet
	//referencing:
	//	LegacyGamePlayScreen (handleMainPanelClick())
	//	LegacyGraphicsGUI (getMapCoordsFromCursorLocation())
	private void processMouseClick()
	{
		if (!gui.inputOkay())
			return;
		
		if (Gdx.input.isTouched())
		{
			if (touchRegistered)
				return;
			
			if (gui.showingEjectionAlert())
			{
				gui.confirmEjectionAlert();
				touchRegistered = true;
				return;
			}
			
			touchRegistered = true;
			
			Point cursorCoords = convertMouseCoordinates(Gdx.input.getX(), Gdx.input.getY());
			
			if (cursorCoords.y > VIEWPORT_HEIGHT)
				clickButtonBar(cursorCoords.x, cursorCoords.y - VIEWPORT_HEIGHT);
			else
				clickArenaLocation(getMapCoordsFromCursorLocation(cursorCoords));
		}
		else
		{
			touchRegistered = false;
		}
	}
	
	private void clickArenaLocation(Point mapCoordsFromCursorLocation)
	{
		int x = mapCoordsFromCursorLocation.y;
		int y = mapCoordsFromCursorLocation.x;
		
		if (x < 1 || y < 1 || x > 28 || y > 28)
			return;
		
		gui.clickArenaLocation(x, y);
		
		Logger.debug("Arena clicked at " + mapCoordsFromCursorLocation);
	}
	
	private boolean miniMapClicked(int x, int y)
	{
		return (x > 570 && x < 628 && y > 8 && y < 65);
	}

	private void clickButtonBar(int x, int y)
	{
		if (miniMapClicked(x, y))
		{
			int row = 3 + (y - EventButtonBarFactory.MINIMAP_Y_START) / 2;	//these are divided by 2 because the minimap uses 2x2 pixels for each map tile
			int col = (x - EventButtonBarFactory.MINIMAP_X_START) / 2;
			Point minimapRowCol = new Point(row, col);
			Logger.debug("Minimap clicked at " + minimapRowCol);
			gui.handleMinimapClick(minimapRowCol);
			return;
		}
		
		Logger.debug("Button bar clicked at (" + x + ", " + y + ")");
		Logger.debug("\tColor there is " + clickMap.getPixel(x, y + 167));
		
		int clickColor = clickMap.getPixel(x, y + 167);
		
		ScreenCommand command = null;
		
		if (clickColor == -1895825153)
			command = ScreenCommand.GAME_PREV_PLAYER;
		else if (clickColor == -1476394753)
			command = ScreenCommand.GAME_NEXT_PLAYER;
		else if (clickColor == 1459618047)
			command = ScreenCommand.GAME_MOVE_ACTION;
		else if (clickColor == 0)
			command = ScreenCommand.GAME_CHECK_ACTION;
		else if (clickColor == 1879048447)
			command = ScreenCommand.GAME_JUMP_ACTION;
		else if (clickColor == -956301057)
			command = ScreenCommand.GAME_HANDOFF_ACTION;
		else if (clickColor == -536870657)
			command = ScreenCommand.GAME_TOGGLE_STATS_PANEL;
		else if (clickColor == 521142527)
			command = ScreenCommand.GAME_STATS_CST;
		else if (clickColor == 941097215)
			command = ScreenCommand.GAME_STATS_ARH;
		else if (clickColor == 1462173951)
			command = ScreenCommand.GAME_TOGGLE_HELP_PANEL;
		else if (clickColor == 939524351)
			command = ScreenCommand.GAME_END_TURN;
		else if (clickColor == -1891696385)
			command = ScreenCommand.GAME_SELECT_PLAYER_1;
		else if (clickColor == -1471676161)
			command = ScreenCommand.GAME_SELECT_PLAYER_2;
		else if (clickColor == -950599425)
			command = ScreenCommand.GAME_SELECT_PLAYER_3;
		else if (clickColor == -530579201)
			command = ScreenCommand.GAME_SELECT_PLAYER_4;
		else if (clickColor == 521601279)
			command = ScreenCommand.GAME_SELECT_PLAYER_5;
		else if (clickColor == 942145791)
			command = ScreenCommand.GAME_SELECT_PLAYER_6;
		else if (clickColor == 1463746815)
			command = ScreenCommand.GAME_SELECT_PLAYER_7;
		else if (clickColor == 1884291327)
			command = ScreenCommand.GAME_SELECT_PLAYER_8;
		else if (clickColor == -1889074945)
			command = ScreenCommand.GAME_SELECT_PLAYER_9;
		
		if (command != null)
			gui.handleCommand(command);
	}

	private Point convertMouseCoordinates(int x, int y)
	{
		int curWidth = Gdx.graphics.getWidth();
		int curHeight = Gdx.graphics.getHeight();
		
		double xProportion = LEGACY_SCREEN_DIMENSION.width / (double)curWidth;
		double yProportion = LEGACY_SCREEN_DIMENSION.height / (double)curHeight;
		
		return new Point((int)((x * xProportion) + .5), (int)((y * yProportion) + .5));
	}

	private Point getMapCoordsFromCursorLocation(Point screenCoords)
	{
		// a tile is 36x30 pixels, starting at 72, 60 (that is, there are two tiles worth of padding around the main map)
//		System.out.println("Translating coordinates from (" + screenCoords.x + ", " + screenCoords.y + "); camera is at (" + camera.position.x + ", " + camera.position.y + ")");
		
		//TODO: 320 and 760 are both magic numbers here
		int cameraTranslatedX = (int)camera.position.x - 320;
		int cameraTranslatedY = 760 - (int)camera.position.y;
		
//		System.out.println("Translated camera coords: " + new Point(cameraTranslatedX, cameraTranslatedY));
		
		int screenOriginX = screenCoords.x + cameraTranslatedX;
		int screenOriginY = screenCoords.y + cameraTranslatedY;
		
//		System.out.println("Screen origin: " + new Point(screenOriginX, screenOriginY));

		int tileRow = screenOriginY / LegacyUiConstants.TILE_IMAGE_HEIGHT;
		int tileColumn = screenOriginX / LegacyUiConstants.TILE_IMAGE_WIDTH;

		return new Point(tileColumn - 1, tileRow - 1); // column, row is X, Y...though the Y value is inverted (increasing as it gets lower on the screen)
	}

	public int getViewportWidth()
	{
		int visibleSidebars = 0;

		if (showStatsPanel)
			visibleSidebars++;

		if (showHelpPanel)
			visibleSidebars++;

		return LEGACY_SCREEN_DIMENSION.width - (visibleSidebars * SIDEBAR_WIDTH);
	}
	
	@Override
	public Texture getBackgroundImage()
	{
		return ImageFactory.getInstance().getTexture(ImageType.MAP_LAVA_BG);
//		return ImageFactory.getInstance().getTexture(ImageType.MAP_STARS_BG);
	}

	@Override
	public OrthographicCamera getCamera()
	{
		return camera;
	}

	@Override
	public List<CrushSprite> getSpritesToRender()
	{
		return activeSprites;
	}

	@Override
	public List<GameText> getStaticText()
	{
		return gui.getGameText();
	}

	@Override
	public List<StaticImage> getStaticImages()
	{
		return gui.getStaticImages();
	}

	public void setGui(GdxGUI gdxGUI)
	{
		this.gui = gdxGUI;
		System.out.println("GUI has been set to " + gdxGUI);
	}
	
	public void setCameraPosition(Point position)
	{
		camera.position.x = position.x;
		camera.position.y = position.y;
	}

	private void defineClickMap()
	{
		Texture allButtons = ImageFactory.getInstance().getTexture(ImageType.GAME_ALLBUTTONS);
		
		TextureData textureData = allButtons.getTextureData();
	    textureData.prepare();
	    
	    clickMap = textureData.consumePixmap();
	    textureData.disposePixmap();
	}
	
	@Override
	public void dispose()
	{
		clickMap.dispose();
	}
}
