package main.presentation.legacy.game.sprites;

import java.awt.image.BufferedImage;

public class StaticSprite implements Sprite
{
	private BufferedImage spriteImage;
	
	public StaticSprite(BufferedImage spriteImage)
	{
		this.spriteImage = spriteImage;
	}
	
	@Override
	public BufferedImage getCurrentFrame()
	{
		return spriteImage;
	}

}
