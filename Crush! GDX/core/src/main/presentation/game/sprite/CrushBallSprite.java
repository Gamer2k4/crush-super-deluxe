package main.presentation.game.sprite;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CrushBallSprite extends CrushPlayerSprite
{
	private CrushTile ballImage;
	
	public CrushBallSprite()
	{
		super(null, null);
		ballImage = CrushTile.createTile(OFFSCREEN_COORDS, TileSpriteType.BALL);
	}
	
	@Override
	public TextureRegion getImage()
	{
		return ballImage.getImage();
	}
}
