package main.presentation.game.sprite;

import java.awt.Point;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CrushArena extends CrushSprite
{
	protected CrushArena(TextureRegion tile)
	{
		super(new Point(0, 0), tile);
	}
}
