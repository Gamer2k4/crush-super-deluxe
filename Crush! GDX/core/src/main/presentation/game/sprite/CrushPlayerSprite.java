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
import main.logic.Randomizer;
import main.presentation.audio.AudioManager;
import main.presentation.audio.SoundType;

public class CrushPlayerSprite extends CrushSprite implements ActionListener
{
	private PlayerState state;
	protected Facing facing;
	private final Team team;
	private final Race race;
	private boolean hasBall = false;
	
	protected Point targetCoords;
	
	private Timer playerAnimationTimer = new Timer(0, this);
	private TextureRegion currentSpriteImage = null;
	private PlayerAnimation currentAnimation = null;
	private int currentAnimationFrame = 0;
	private List<PlayerState> queuedAnimationStates = new ArrayList<PlayerState>();
	
	private double yMoveFraction = 0;
	private static final double FRACTION_TOLERANCE = .01;
	
	private Point lastCoordCheckResult = new Point(-50, -50);
	private int timesCoordCheckWasUnchanged = 0;
	
	private static final int MAX_ITERATIONS_BEFORE_EASING_SPRITE = 5000;
	
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
		{
			easeSpriteAlong();
			return true;
		}
		
		return false;
	}
	
	//to prevent infinite looping because the sprite never arrives at its destination (due to fractions being lost in the x -> y conversion), this will
	//slowly shift the sprite toward where it's supposed to go.
	protected void easeSpriteAlong()
	{
		if (lastCoordCheckResult.x == coords.x && lastCoordCheckResult.y == coords.y)
			timesCoordCheckWasUnchanged++;
		else
		{
			timesCoordCheckWasUnchanged = 0;
			lastCoordCheckResult = new Point(coords.x, coords.y);
		}
		
		if (timesCoordCheckWasUnchanged > MAX_ITERATIONS_BEFORE_EASING_SPRITE)
		{
			System.out.println("Current coords have been " + coords + " for too long; easing sprite along to target: " + targetCoords);
			timesCoordCheckWasUnchanged = 0;
			yMoveFraction = 0;
			
			if (coords.x < targetCoords.x)
				coords.x++;
			if (coords.x > targetCoords.x)
				coords.x--;
			if (coords.y < targetCoords.y)
				coords.y++;
			if (coords.y > targetCoords.y)
				coords.y--;
			
		}
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
	
	public void dodge()
	{
		AudioManager.getInstance().playSound(SoundType.DODGE);
		
		if (hasBall)
			beginAnimation(PlayerState.DODGE_BALL);
		else
			beginAnimation(PlayerState.DODGE);
	}
	
	public void check()
	{
		playOohSound();
		
		if (hasBall)
			beginAnimation(PlayerState.CHECK_BALL);
		else if (Randomizer.getRandomInt(1, 3) == 1)		//a 1 in 3 chance is probably TOTALLY not how this is determined
			beginAnimation(PlayerState.CHECK_STRONG);
		else
			beginAnimation(PlayerState.CHECK_WEAK);
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
		setTargetCoords(event.flags[2], event.flags[3]);
		turnTowardPixelCoords(targetCoords);
		
		if (hasBall)
			beginAnimation(PlayerState.WALK_BALL);
		else
			beginAnimation(PlayerState.WALK);
	}
	
	public void slide(Event event)
	{
		setTargetCoords(event.flags[2], event.flags[3]);
		
		if (hasBall)
			beginAnimation(PlayerState.SLIDE_BALL);
		else
			beginAnimation(PlayerState.SLIDE);
	}

	public void jump(Event event)
	{
		AudioManager.getInstance().playSound(SoundType.JUMP);
		setTargetCoords(event.flags[2], event.flags[3]);
		turnTowardPixelCoords(targetCoords);
		
		if (hasBall)
			beginAnimation(PlayerState.JUMP_BALL);
		else
			beginAnimation(PlayerState.JUMP);
	}
	
	public void knockbackKo(Event event)
	{
		setTargetCoords(event.flags[2], event.flags[3]);
		beginAnimation(PlayerState.KNOCKBACK_FALL);
	}
	
	private void setTargetCoords(int targetRow, int targetCol)
	{
		targetCoords.x = getXValueForColumn(targetCol);
		targetCoords.y = getYValueForRow(targetRow);
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
		facing = getDirectionTowardPixelCoords(location);
		refreshSpriteImage();
	}
	
	public Facing getDirectionTowardPixelCoords(Point location)
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
		if (facingString.isEmpty())
			facingString = facing.name();
		
		return Facing.valueOf(facingString);
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
		
		if (movingToNewTile())
			advanceSprite(currentFrame.getPositionPixelChange());
		else
			advanceSpriteByFacing(currentFrame.getPositionPixelChange());
		
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

	protected void advanceSpriteByFacing(int positionPixelChange)
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
	
	private void advanceSprite(int positionPixelChange)
	{
		//assume the sprite is going NE, since that's positive change for both axes
		
		Facing directionToMove = getDirectionTowardPixelCoords(targetCoords);
		
		double xChange = positionPixelChange;
		double yChange = positionPixelChange * (5.0 / 6.0);
		
		yChange = adjustYChangeForFraction(yChange);
		
		if (directionToMove == Facing.SW || directionToMove == Facing.S || directionToMove == Facing.SE)
			yChange *= -1;
		if (directionToMove == Facing.NW || directionToMove == Facing.W || directionToMove == Facing.SW)
			xChange *= -1;
		if (directionToMove == Facing.E || directionToMove == Facing.W)
			yChange = 0;
		if (directionToMove == Facing.N || directionToMove == Facing.S)
			xChange = 0;
		
		coords.x += xChange;
		coords.y += yChange;
	}

	private int adjustYChangeForFraction(double yChange)
	{
		double fractionalAmount = yChange - (int)yChange;
		
		if (Math.abs(fractionalAmount) < FRACTION_TOLERANCE)
			return (int)yChange;
		
		yMoveFraction += fractionalAmount;
		
		if (Math.abs(yMoveFraction) + FRACTION_TOLERANCE < 1)
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

	private boolean movingToNewTile()
	{
		return state == PlayerState.WALK || state == PlayerState.WALK_BALL || state == PlayerState.SLIDE || state == PlayerState.SLIDE_BALL
				|| state == PlayerState.KNOCKBACK_FALL || state == PlayerState.JUMP || state == PlayerState.JUMP_BALL;
	}
}
