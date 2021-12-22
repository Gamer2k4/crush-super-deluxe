package main.presentation.game.sprite;

public class PlayerAnimationFrame
{
	private PlayerSpriteType frame;
	private int frameDuration;
	private int positionPixelChange;
	//I'd like to have the sounds here, but it's different for each race
	
	public PlayerAnimationFrame(PlayerSpriteType frame, int frameDuration, int positionPixelChange)
	{
		this.frame = frame;
		this.frameDuration = frameDuration;
		this.positionPixelChange = positionPixelChange;
	}
	
	public PlayerSpriteType getFrame()
	{
		return frame;
	}
	
	public int getFrameDuration()
	{
		return frameDuration;
	}
	
	public int getPositionPixelChange()
	{
		return positionPixelChange;
	}
}
