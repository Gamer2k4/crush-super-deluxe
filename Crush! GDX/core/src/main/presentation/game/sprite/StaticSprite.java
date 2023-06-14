package main.presentation.game.sprite;

import java.awt.Point;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class StaticSprite extends CrushSprite
{
	public StaticSprite(Point coords, TextureRegion tile)
	{
		super(coords, tile);
	}
	
	public StaticSprite(Point coords, TextureRegion tile, float alpha)
	{
		super(coords, tile, alpha);
	}
}
