package main.presentation.game;

import java.awt.Color;
import java.awt.Point;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import main.presentation.common.image.ImageUtils;

public class GameText
{
	private static BitmapFont big = new BitmapFont(Gdx.files.internal("fonts/big.fnt"), Gdx.files.internal("fonts/gdx_big.png"), true);
	private static BitmapFont small = new BitmapFont(Gdx.files.internal("fonts/small.fnt"), Gdx.files.internal("fonts/gdx_small.png"), true);
	private static BitmapFont small2 = new BitmapFont(Gdx.files.internal("fonts/small2.fnt"), Gdx.files.internal("fonts/gdx_small2.png"), true);
	
	private BitmapFont font;
	private Point coords;
	private String text;
	private com.badlogic.gdx.graphics.Color color;
	
	public GameText(FontType fontType, Point coords, Color color, String text)
	{
		setFont(fontType);
		this.color = ImageUtils.gdxColor(color);
		this.coords = new Point(coords.x, coords.y);
		this.text = text.toUpperCase();
	}

	private void setFont(FontType fontType)
	{
		switch(fontType)
		{
		case FONT_BIG:
			font = big;
			return;
		case FONT_HUGE:
			break;
		case FONT_SMALL:
			font = small;
			return;
		case FONT_SMALL2:
			font = small2;
			return;
		case FONT_SMALL_TIGHT:
			break;
		default:
			font = small2;
			break;
		}
		
		font = small2;	//TODO: remove this once I've created more fonts
	}
	
	@Override
	public GameText clone()
	{
		GameText clone = new GameText(FontType.FONT_SMALL, getCoords(), Color.BLACK, text);
		clone.font = font;
		clone.color = color.cpy();
		return clone;
	}
	
	public Point getCoords()
	{
		return new Point(coords.x, coords.y);
	}
	
	public void setCoords(Point newCoords)
	{
		coords = new Point(newCoords.x, newCoords.y);
	}
	
	public void render(SpriteBatch spriteBatch)
	{
		font.setColor(color);
		font.draw(spriteBatch, text, coords.x, coords.y);
	}
	
	public static void dispose()
	{
		small2.dispose();
	}
	
	public static GameText big(Point coords, Color color, String text)
	{
		return new GameText(FontType.FONT_BIG, coords, color, text);
	}
	
	public static GameText small(Point coords, Color color, String text)
	{
		return new GameText(FontType.FONT_SMALL, coords, color, text);
	}
	
	public static GameText small2(Point coords, Color color, String text)
	{
		return new GameText(FontType.FONT_SMALL2, coords, color, text);
	}
}
