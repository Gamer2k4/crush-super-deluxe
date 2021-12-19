package main.presentation.game;

import java.awt.Color;
import java.awt.Point;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import main.presentation.common.image.ImageUtils;

public class GameText
{
	private static BitmapFont small2 = new BitmapFont(Gdx.files.internal("fonts/small2.fnt"), Gdx.files.internal("fonts/small2.png"), true);
	
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
			break;
		case FONT_HUGE:
			break;
		case FONT_SMALL:
			break;
		case FONT_SMALL2:
			font = small2;
			break;
		case FONT_SMALL_TIGHT:
			break;
		default:
			break;
		}
		
		font = small2;	//TODO: remove this once I've created more fonts
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
	
	public static GameText small2(Point coords, Color color, String text)
	{
		return new GameText(FontType.FONT_SMALL2, coords, color, text);
	}
}
