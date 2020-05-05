package main.presentation.legacy.game.sprites;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

public class AnimatedSprite implements Sprite, ActionListener
{
	private List<BufferedImage> spriteFrames;
	private int totalFrames;
	private int currentFrame;
	
	public AnimatedSprite()
	{
		spriteFrames = new ArrayList<BufferedImage>();
		totalFrames = 0;
		currentFrame = -1;
	}
	
	public AnimatedSprite(Timer frameChangeTimer)
	{
		this();
		frameChangeTimer.addActionListener(this);
	}
	
	//TODO: this would be risky if two of the same sprite ever animated independently of each other, but I think that only happens with ball bins.
	public AnimatedSprite reset()
	{
		currentFrame = 0;
		return this;
	}
	
	public void addFrame(BufferedImage frame)
	{
		spriteFrames.add(frame);
		totalFrames = spriteFrames.size();
	}

	@Override
	public BufferedImage getCurrentFrame()
	{
		if (currentFrame < 0 || currentFrame >= totalFrames)
			return new BufferedImage(1, 1, 1);
		
		return spriteFrames.get(currentFrame);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (totalFrames == 0)
			return;
		
		currentFrame++;
		if (currentFrame >= totalFrames)
			currentFrame = 0;
	}
}
