package main.presentation.game.sprite;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.data.Event;
import main.data.entities.Race;
import main.data.entities.Team;
import main.presentation.audio.AudioManager;
import main.presentation.audio.SoundType;

public class CrushPlayerSprite extends CrushSprite implements ActionListener
{
	public static final float PLAYER_ANIMATION_FRAME_DURATION = .080f;
	private static final int X_MOVE_INCREMENT = 6;
	private static final int Y_MOVE_INCREMENT = 5;
	
	private PlayerState state;
	private Facing facing;
	private final Team team;
	private final Race race;
	
	private Point targetCoords;
	private int animationFramesRemaining = 0;
	private float animationElapsedTime = 0f;
	
	private static Timer playerAnimationTimer = new Timer((int)(1000 * PLAYER_ANIMATION_FRAME_DURATION), null);
	
	public CrushPlayerSprite(Team team, Race race)
	{
		super(new Point(0, 0), null);

		state = PlayerState.PASSIVE;
		facing = Facing.N;
		this.team = team;
		this.race = race;
		targetCoords = new Point(coords.x, coords.y);
		
		if (!playerAnimationTimer.isRunning())
			playerAnimationTimer.start();
		
		playerAnimationTimer.addActionListener(this);
		
		//note that while the data will have to track a player's tile for engine purposes, the sprites themselves shouldn't be
		//tied to a particular spot on the map (so that they can travel pixel by pixel across tiles)
	}
	
	//TODO: this class manages everything about the sprite (including its position), meaning it's what decides which frame is returned
	//		the state will be updated by methods the GUI can call
	
	//TODO: some animations will have to be defined in some AnimationSequence class that keeps track of frames and
	//		their durations.  This would be for things like jumping, which presumably have a short "ready" pose, a
	//		long "jump" pose, and a short "land" pose.
	
	@Override
	public TextureRegion getImage()
	{
		Animation<TextureRegion> animation = PlayerAnimationManager.getInstance().getAnimation(team, race, PlayerSpriteType.getPlayerSpriteType(state, facing));
		return animation.getKeyFrame(animationElapsedTime, true);
	}
	
	public boolean isActive()
	{
		if (animationFramesRemaining > 0)
			return true;
		
		if (targetCoords.x != coords.x || targetCoords.y != coords.y)
			return true;
		
		return false;
	}
	
	public void rotateClockwise()
	{
		facing = facing.rotateClockwise();
	}
	
	public void rotateCounterclockwise()
	{
		facing = facing.rotateCounterclockwise();
	}
	
	public void walk(Event event)
	{
		System.out.println("\tA sprite is walking: event is " + event);
		int targetRow = event.flags[2];
		int targetCol = event.flags[3];
		moveToArenaPosition(targetRow, targetCol);
	}
	
	public void moveToArenaPosition(Point position)
	{
		moveToArenaPosition(position.x, position.y);
	}
	
	public void moveToArenaPosition(int row, int column)
	{
		targetCoords.x = getXValueForColumn(column);
		targetCoords.y = getYValueForRow(row);
		
		String facingString = "";
		
		if (targetCoords.y < coords.y)
			facingString = "S";
		if (targetCoords.y > coords.y)
			facingString = "N";
		if (targetCoords.x < coords.x)
			facingString = facingString + "W";
		if (targetCoords.x > coords.x)
			facingString = facingString + "E";
		
		facing = Facing.valueOf(facingString);
		state = PlayerState.WALK;
		
		int xMoveDist = Math.abs(targetCoords.x - coords.x); 
		int yMoveDist = Math.abs(targetCoords.y - coords.y);
		animationFramesRemaining = Math.max(xMoveDist, yMoveDist) / X_MOVE_INCREMENT;	//TODO: not quite correct anymore, with two increments
		animationElapsedTime = 0f;
	}
	
	@Override
	public void setArenaPosition(int row, int column)
	{
		super.setArenaPosition(row, column);
		targetCoords.x = coords.x;
		targetCoords.y = coords.y;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
//		System.out.println("Timer firing received for player sprite: " + this);
		
		if (animationFramesRemaining > 0)
		{
			animationFramesRemaining--;
			animationElapsedTime += PLAYER_ANIMATION_FRAME_DURATION;
			
			if (state == PlayerState.WALK && animationFramesRemaining % 2 == 0)
				playWalkSound();
		}
		
		//TODO: if the move increment is too large, this can overshoot, then overcorrect, and it never hits
		if (targetCoords.x < coords.x)
			coords.x -= X_MOVE_INCREMENT;
		if (targetCoords.x > coords.x)
			coords.x += X_MOVE_INCREMENT;
		if (targetCoords.y < coords.y)
			coords.y -= Y_MOVE_INCREMENT;
		if (targetCoords.y > coords.y)
			coords.y += Y_MOVE_INCREMENT;
		
		if (isActive())
			return;
		
		//reset everything if the sprite isn't active
		animationElapsedTime = 0f;
		
		if (state == PlayerState.WALK)
			state = PlayerState.PASSIVE;
	}

	private void playWalkSound()
	{
		SoundType walkSound = SoundType.MEDWALK;
		
		if (race == Race.CURMIAN)
			walkSound = SoundType.KURWALK;
		else if (race == Race.SLITH)
			walkSound = SoundType.SLWALK;
		else if (race == Race.XJS9000)
			walkSound = SoundType.ROBWALK;
		else if (race == Race.GRONK)
			walkSound = SoundType.LOWWALK;
			
		AudioManager.getInstance().playSound(walkSound);
	}
}
