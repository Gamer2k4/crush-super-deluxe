package main.presentation.game.sprite;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.data.Event;
import main.data.entities.Player;
import main.data.entities.Race;
import main.data.entities.Team;
import main.presentation.audio.AudioManager;
import main.presentation.audio.SoundType;

public class CrushPlayerSprite extends CrushSprite implements ActionListener
{
	private PlayerState state;
	private Facing facing;
	private final Team team;
	private final Race race;
	private boolean hasBall = false;
	
	private Point targetCoords;
	
	private Timer playerAnimationTimer = new Timer(0, this);
	private TextureRegion currentSpriteImage = null;
	private PlayerAnimation currentAnimation = null;
	private int currentAnimationFrame = 0;
	private List<PlayerState> queuedAnimationStates = new ArrayList<PlayerState>();
	
	private double yMoveFraction = 0;
	private static final double FRACTION_TOLERANCE = .01;
	
	public CrushPlayerSprite(Team team, Race race)
	{
		super(new Point(0, 0), null);

		state = PlayerState.PASSIVE;
		facing = Facing.N;
		this.team = team;
		this.race = race;
		targetCoords = new Point(coords.x, coords.y);
		
		playerAnimationTimer.stop();
	}
	
	@Override
	public TextureRegion getImage()
	{
		if (currentSpriteImage == null)
			refreshSpriteImage();
		
		return currentSpriteImage;
	}
	
	private void refreshSpriteImage()
	{
		currentSpriteImage = PlayerAnimationManager.getInstance().getSprite(team, race, PlayerSpriteType.getPlayerSpriteType(state, facing));
	}
	
	public boolean isActive()
	{
		if (isAnimating())
			return true;
		
		if (targetCoords.x != coords.x || targetCoords.y != coords.y)
			return true;
		
		return false;
	}
	
	private boolean isAnimating()
	{
		return (currentAnimation != null);
	}
	
	public void setHasBall(boolean hasBall)
	{
		this.hasBall = hasBall;
	}
	
	public void rotateClockwise()
	{
		facing = facing.rotateClockwise();
		refreshSpriteImage();
	}
	
	public void rotateCounterclockwise()
	{
		facing = facing.rotateCounterclockwise();
		refreshSpriteImage();
	}
	
	public void shock()
	{
		hasBall = false;
		beginAnimation(PlayerState.SHOCK);
	}
	
	public void changeStatus(Event event)
	{
		int newStatus = event.flags[2];
		
		if (newStatus == Player.STS_OKAY)
			setState(PlayerState.PASSIVE);
		else if (newStatus == Player.STS_STUN_SIT)
			setState(PlayerState.SIT);
		else if (newStatus == Player.STS_HURT || newStatus == Player.STS_DEAD)
			setState(PlayerState.INJURY);
		else if (newStatus == Player.STS_DOWN || newStatus == Player.STS_STUN_DOWN)
		{
			playOohSound();
			setState(PlayerState.DOWN);
		}
	}
	
	public void hurt()
	{
		setState(PlayerState.INJURY);
		playHurtSound();
	}

	public void walk(Event event)
	{
		int targetRow = event.flags[2];
		int targetCol = event.flags[3];
		targetCoords.x = getXValueForColumn(targetCol);
		targetCoords.y = getYValueForRow(targetRow);
		turnTowardPixelCoords(targetCoords);
		
		if (hasBall)
			beginAnimation(PlayerState.WALK_BALL);
		else
			beginAnimation(PlayerState.WALK);
	}
	
	public void receiveBall(Event event)
	{
		//TODO: probably do all this if it's a handoff event
//		int targetRow = event.flags[2];
//		int targetCol = event.flags[3];
//		targetCoords.x = getXValueForColumn(targetCol);
//		targetCoords.y = getYValueForRow(targetRow);
//		turnTowardPixelCoords(targetCoords);
		
		//otherwise assume facing is correct already
		beginAnimation(PlayerState.RECEIVE_BALL);
	}
	
	public void turnTowardArenaLocation(Point location)
	{
		turnTowardPixelCoords(new Point(getXValueForColumn(location.y), getYValueForRow(location.x)));
	}
	
	public void turnTowardPixelCoords(Point location)
	{
		String facingString = "";
		
		if (location.y < coords.y)
			facingString = "S";
		if (location.y > coords.y)
			facingString = "N";
		if (location.x < coords.x)
			facingString = facingString + "W";
		if (location.x > coords.x)
			facingString = facingString + "E";
		
		facing = Facing.valueOf(facingString);
		refreshSpriteImage();
	}
	
	@Override
	public void setArenaPosition(int row, int column)
	{
		super.setArenaPosition(row, column);
		targetCoords.x = coords.x;
		targetCoords.y = coords.y;
	}

	@Override
	public void actionPerformed(ActionEvent e)	//the current animation frame has hit its display duration, so advance to the next one
	{
		advanceAnimationFrame();
	}
	
	private void beginAnimation(PlayerState animation)
	{
		if (isAnimating())
		{
			queuedAnimationStates.add(animation);
			return;
		}
		
		state = animation;
		currentAnimation = PlayerAnimation.getAnimation(animation, facing);
		currentAnimationFrame = -1;
		advanceAnimationFrame();
	}
	
	private void advanceAnimationFrame()
	{
		currentAnimationFrame++;
		PlayerAnimationFrame currentFrame = currentAnimation.getFrame(currentAnimationFrame);
		
		if (currentFrame == null)
		{
			endAnimation();
			return;
		}
		
		currentSpriteImage = PlayerAnimationManager.getInstance().getSprite(team, race, currentFrame.getFrame());
		advanceSprite(currentFrame.getPositionPixelChange());
		playAnimationFrameSound();
		playerAnimationTimer.setInitialDelay(currentFrame.getFrameDuration());
		playerAnimationTimer.restart();
	}

	private void endAnimation()
	{
		playerAnimationTimer.stop();
		currentAnimation = null;
		
		if (queuedAnimationStates.isEmpty())
		{
			setState(PlayerState.PASSIVE);
			return;
		}
		
		PlayerState nextAnimation = queuedAnimationStates.remove(0);
		beginAnimation(nextAnimation);
	}

	private void advanceSprite(int positionPixelChange)
	{
		double xChange = positionPixelChange;
		double yChange = positionPixelChange * (5.0 / 6.0);
		
		yChange = adjustYChangeForFraction(yChange);
		
		if (facing == Facing.SW || facing == Facing.S || facing == Facing.SE)
			yChange *= -1;
		if (facing == Facing.NW || facing == Facing.W || facing == Facing.SW)
			xChange *= -1;
		if (facing == Facing.E || facing == Facing.W)
			yChange = 0;
		if (facing == Facing.N || facing == Facing.S)
			xChange = 0;
		
		coords.x += xChange;
		coords.y += yChange;
	}

	private int adjustYChangeForFraction(double yChange)
	{
		double fractionalAmount = yChange - (int)yChange;
		
		if (fractionalAmount < FRACTION_TOLERANCE)
			return (int)yChange;
		
		yMoveFraction += fractionalAmount;
		
		if (yMoveFraction + FRACTION_TOLERANCE < 1)
			return ((int)yChange);
		
		yMoveFraction = 0;
		return (int)yChange + 1;
	}

	@SuppressWarnings("incomplete-switch")
	private void playAnimationFrameSound()
	{
		switch(state)
		{
		case WALK:
		case WALK_BALL:
			if (currentAnimationFrame % 2 == 0)
				playWalkSound();
			break;
		case SHOCK:
			if (currentAnimationFrame == 0)
				AudioManager.getInstance().playSound(SoundType.ZAP);
			break;
		}
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
	
	private void playOohSound()
	{
		SoundType walkSound = SoundType.MEDOOH;
		
		if (race == Race.CURMIAN)
			walkSound = SoundType.HIGHOOH;
		else if (race == Race.SLITH)
			walkSound = SoundType.SLTOOH;
		else if (race == Race.XJS9000)
			walkSound = SoundType.ROBOOH;
		else if (race == Race.GRONK)
			walkSound = SoundType.LOWOOH;
		else if (race == Race.DRAGORAN)
			walkSound = SoundType.DRGOOH;
		else if (race == Race.NYNAX)
			walkSound = SoundType.ANTOOH;
		else if (race == Race.KURGAN)
			walkSound = SoundType.BEVOOH;
			
		AudioManager.getInstance().playSound(walkSound);
	}
	
	private void playHurtSound()
	{
		SoundType walkSound = SoundType.HUMDEAT;
		
		if (race == Race.CURMIAN)
			walkSound = SoundType.KRMDEAT;
		else if (race == Race.SLITH)
			walkSound = SoundType.SLDEAT;
		else if (race == Race.XJS9000)
			walkSound = SoundType.ROBDEAT;
		else if (race == Race.GRONK)
			walkSound = SoundType.GRDEAT;
		else if (race == Race.DRAGORAN)
			walkSound = SoundType.DRGDEAT;
		else if (race == Race.NYNAX)
			walkSound = SoundType.ANTDEAT;
		else if (race == Race.KURGAN)
			walkSound = SoundType.KURDEAT;
			
		AudioManager.getInstance().playSound(walkSound);
	}

	private void setState(PlayerState newState)
	{
		if (!hasBall)
			state = newState;
		else if (newState == PlayerState.CHECK_WEAK || newState == PlayerState.CHECK_STRONG || newState == PlayerState.PASSIVE || newState == PlayerState.WALK)
			state = PlayerState.valueOf(newState.name() + "_BALL");
		else
			state = newState;
		
		currentSpriteImage = PlayerAnimationManager.getInstance().getSprite(team, race, PlayerSpriteType.getPlayerSpriteType(state, facing));
	}
}
