package main.presentation.game;

import java.awt.Color;
import java.awt.Point;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import main.presentation.common.image.ImageUtils;

public class GameText
{
	public static BitmapFont big = new BitmapFont(Gdx.files.internal("fonts/big.fnt"), Gdx.files.internal("fonts/gdx_big.png"), true);
	public static BitmapFont huge = new BitmapFont(Gdx.files.internal("fonts/huge.fnt"), Gdx.files.internal("fonts/gdx_huge.png"), true);
	public static BitmapFont small = new BitmapFont(Gdx.files.internal("fonts/small.fnt"), Gdx.files.internal("fonts/gdx_small.png"), true);
	public static BitmapFont smallSpread = new BitmapFont(Gdx.files.internal("fonts/small_spread.fnt"), Gdx.files.internal("fonts/gdx_small.png"), true);
	public static BitmapFont small2 = new BitmapFont(Gdx.files.internal("fonts/small2.fnt"), Gdx.files.internal("fonts/gdx_small2.png"), true);
	
	private BitmapFont font;
	private Point coords;
	private String text;
	private com.badlogic.gdx.graphics.Color color;
	
	private static final Point OFFSCREEN_COORDS = new Point(-10, -10);
	
	public GameText(FontType fontType, Point coords, Color color, String text)
	{
		setFont(fontType);
		this.color = ImageUtils.gdxColor(color);
		this.coords = new Point(coords.x, coords.y);
		this.text = text.toUpperCase();
	}
	
	public GameText(FontType fontType, Color color, String text)
	{
		this(fontType, OFFSCREEN_COORDS, color, text);
	}

	private void setFont(FontType fontType)
	{
		switch(fontType)
		{
		case FONT_BIG:
			font = big;
			return;
		case FONT_HUGE:
			font = huge;
			return;
		case FONT_SMALL:
			font = small;
			return;
		case FONT_SMALL2:
			font = small2;
			return;
		case FONT_SMALL_SPREAD:
			font = smallSpread;
			return;
		default:
			font = small2;
			break;
		}
		
		font = small2;	//TODO: remove this once I've created more fonts
	}
	
	public boolean isEmpty()
	{
		return text == null || text.isEmpty();
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
		big.dispose();
		huge.dispose();
		small.dispose();
		small2.dispose();
	}
	
	public int getStringStartX(int targetX, int maxWidth)
	{
		return getStringStartX(this.font, targetX, maxWidth, text);
	}
	
	public static int getStringStartX(BitmapFont fontUsed, int targetX, int maxWidth, String text)
	{
		int stringLength = getStringPixelLength(fontUsed, text);
		
		if (stringLength > maxWidth)
			return targetX;
		
		int lengthDif = maxWidth - getStringPixelLength(fontUsed, text);
		
		return targetX + (lengthDif / 2);
	}
	
	public int getStringPixelLength()
	{
		return GameText.getStringPixelLength(font, text);
	}
	
	public static int getStringPixelLength(BitmapFont fontUsed, String text)
	{
		int length = 0;
		
		for (int i = 0; i < text.length(); i++)
		{
			if (text.charAt(i) == ' ')
				length += getSpaceLength(fontUsed);
			else
				length += getCharacterLength(fontUsed);
		}
		
		return length;
	}
	
	private static int getSpaceLength(BitmapFont font)
	{
		if (font == huge)
			return 20;
		
		return 3;
	}
	
	private static int getCharacterLength(BitmapFont font)
	{
		if (font == huge)
			return 40;
		
		if (font == small || font == big)
			return 11;
		
		return 6;
	}
	
	public static GameText big(Point coords, Color color, String text)
	{
		return new GameText(FontType.FONT_BIG, coords, color, text);
	}
	
	public static GameText big(Color color, String text)
	{
		return big(OFFSCREEN_COORDS, color, text);
	}
	
	public static GameText huge(Point coords, String text)
	{
		return new GameText(FontType.FONT_HUGE, coords, Color.WHITE, text);
	}
	
	public static GameText huge(String text)
	{
		return huge(OFFSCREEN_COORDS, text);
	}
	
	public static GameText small(Point coords, Color color, String text)
	{
		return new GameText(FontType.FONT_SMALL, coords, color, text);
	}
	
	public static GameText small(Color color, String text)
	{
		return small(OFFSCREEN_COORDS, color, text);
	}
	
	public static GameText small2(Point coords, Color color, String text)
	{
		return new GameText(FontType.FONT_SMALL2, coords, color, text);
	}
	
	public static GameText small2(Color color, String text)
	{
		return small2(OFFSCREEN_COORDS, color, text);
	}
	
	public static GameText smallSpread(Point coords, Color color, String text)
	{
		return new GameText(FontType.FONT_SMALL_SPREAD, coords, color, text);
	}
	
	public static GameText smallSpread(Color color, String text)
	{
		return smallSpread(OFFSCREEN_COORDS, color, text);
	}
}
