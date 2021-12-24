package main.presentation.game.sprite;

import java.util.HashMap;
import java.util.Map;

public class PlayerAnimation
{
	private static Map<PlayerState, Map<Facing, PlayerAnimation>> animations = new HashMap<PlayerState, Map<Facing, PlayerAnimation>>();
	
	private int frameCount;
	
	private PlayerAnimationFrame[] frames;
	
	private PlayerAnimation(int totalFrames)
	{
		frameCount = totalFrames;
		frames = new PlayerAnimationFrame[frameCount];
	}
	
	public PlayerAnimationFrame getFrame(int frameIndex)
	{
		try
		{
			return frames[frameIndex];
		} catch (ArrayIndexOutOfBoundsException oob)
		{
			return null;
		}
	}
	
	public static PlayerAnimation getAnimation(PlayerState state, Facing facing)
	{
		if (!animations.containsKey(state))
			animations.put(state, createAnimations(state));
		
		return animations.get(state).get(facing);
	}

	private static Map<Facing, PlayerAnimation> createAnimations(PlayerState state)
	{
		switch (state)
		{
		case PASSIVE:
			return createPassiveAnimation();
		case PASSIVE_BALL:
			return createPassiveBallAnimation();
		case WALK:
			return createWalkAnimation();
		case WALK_BALL:
			return createWalkBallAnimation();
		case CHECK_STRONG:
			break;
		case CHECK_WEAK:
			break;
		case INJURY:
			break;
		case JUMP:
			break;
		case DOWN:
			break;
		case SIT:
			break;
		case RECEIVE_BALL:
			return createReceiveBallAnimation();
		case SHOCK:
			return createShockAnimation();
		default:
			break;
		}
		
		throw new IllegalArgumentException("PlayerAnimation: No animation defined for state " + state);
	}

	private static Map<Facing, PlayerAnimation> createPassiveAnimation()
	{
		Map<Facing, PlayerAnimation> createdAnimations = new HashMap<Facing, PlayerAnimation>();
		
		for (Facing facing : Facing.values())
		{
			PlayerAnimation animation = new PlayerAnimation(1);
			animation.frames[0] = new PlayerAnimationFrame(PlayerSpriteType.getPlayerSpriteType(PlayerState.PASSIVE, facing), 1, 0);
			createdAnimations.put(facing, animation);
		}
		
		return createdAnimations;
	}

	private static Map<Facing, PlayerAnimation> createPassiveBallAnimation()
	{
		Map<Facing, PlayerAnimation> createdAnimations = new HashMap<Facing, PlayerAnimation>();
		
		for (Facing facing : Facing.values())
		{
			PlayerAnimation animation = new PlayerAnimation(1);
			animation.frames[0] = new PlayerAnimationFrame(PlayerSpriteType.getPlayerSpriteType(PlayerState.PASSIVE_BALL, facing), 1, 0);
			createdAnimations.put(facing, animation);
		}
		
		return createdAnimations;
	}

	private static Map<Facing, PlayerAnimation> createWalkAnimation()
	{
		Map<Facing, PlayerAnimation> createdAnimations = new HashMap<Facing, PlayerAnimation>();
		int frameDurationInMs = 80;
		
		for (Facing facing : Facing.values())
		{
			PlayerAnimation animation = new PlayerAnimation(4);
			
			animation.frames[0] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_RIGHT_" + facing.name()), frameDurationInMs, 9);
			animation.frames[1] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_BOTH_" + facing.name()), frameDurationInMs, 9);
			animation.frames[2] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_LEFT_" + facing.name()), frameDurationInMs, 9);
			animation.frames[3] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_BOTH_" + facing.name()), frameDurationInMs, 9);
			
			createdAnimations.put(facing, animation);
		}
		
		return createdAnimations;
	}

	private static Map<Facing, PlayerAnimation> createWalkBallAnimation()
	{
		Map<Facing, PlayerAnimation> createdAnimations = new HashMap<Facing, PlayerAnimation>();
		int frameDurationInMs = 80;
		
		for (Facing facing : Facing.values())
		{
			PlayerAnimation animation = new PlayerAnimation(4);
			
			animation.frames[0] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_RIGHT_BALL_" + facing.name()), frameDurationInMs, 9);
			animation.frames[1] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_BOTH_BALL_" + facing.name()), frameDurationInMs, 9);
			animation.frames[2] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_LEFT_BALL_" + facing.name()), frameDurationInMs, 9);
			animation.frames[3] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_BOTH_BALL_" + facing.name()), frameDurationInMs, 9);
			
			createdAnimations.put(facing, animation);
		}
		
		return createdAnimations;
	}

	private static Map<Facing, PlayerAnimation> createReceiveBallAnimation()
	{
		Map<Facing, PlayerAnimation> createdAnimations = new HashMap<Facing, PlayerAnimation>();
		int frameDurationInMs = 300;
		
		for (Facing facing : Facing.values())
		{
			PlayerAnimation animation = new PlayerAnimation(2);
			
			animation.frames[0] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("RECEIVE_BALL_FORWARD_" + facing.name()), frameDurationInMs, 0);
			animation.frames[1] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("RECEIVE_BALL_BACK_" + facing.name()), frameDurationInMs, 0);
			
			createdAnimations.put(facing, animation);
		}
		
		return createdAnimations;
	}

	private static Map<Facing, PlayerAnimation> createShockAnimation()
	{
		Map<Facing, PlayerAnimation> createdAnimations = new HashMap<Facing, PlayerAnimation>();
		int frameDurationInMs = 60;
		
		for (Facing facing : Facing.values())
		{
			PlayerAnimation animation = new PlayerAnimation(34);
			
			for (int i = 0; i < 17; i++)
			{
				animation.frames[2 * i] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("SHOCK_OFF_" + facing.name()), frameDurationInMs, 0);
				animation.frames[(2 * i) + 1] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("SHOCK_ON_" + facing.name()), frameDurationInMs, 0);
			}
			
			createdAnimations.put(facing, animation);
		}
		
		return createdAnimations;
	}
}
