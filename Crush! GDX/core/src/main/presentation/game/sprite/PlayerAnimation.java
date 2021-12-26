package main.presentation.game.sprite;

import java.util.HashMap;
import java.util.Map;

public class PlayerAnimation
{
	private static Map<PlayerState, Map<Facing, PlayerAnimation>> animations = new HashMap<PlayerState, Map<Facing, PlayerAnimation>>();
	
	private int frameCount;
	
	private PlayerAnimationFrame[] frames;
	
	private static final int DODGE_MOVEMENT_FRAME_DURATION = 40;
	private static final int DODGE_PAUSE_FRAME_DURATION = 400;
	private static final int CHECK_FRAME_DURATION = 35;
	private static final int SLIDE_FRAME_DURATION = 20;
	private static final int KNOCKBACK_KO_FRAME_DURATION = 30;
	
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
			return createStrongCheckAnimation();
		case CHECK_WEAK:
			return createWeakCheckAnimation();
		case CHECK_BALL:
			return createBallCheckAnimation();
		case DODGE:
			return createDodgeAnimation();
		case DODGE_BALL:
			return createDodgeBallAnimation();
		case INJURY:
			break;
		case SLIDE:
			return createSlideAnimation();
		case SLIDE_BALL:
			return createSlideBallAnimation();
		case KNOCKBACK_FALL:
			return createKnockbackFallAnimation();
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

	private static Map<Facing, PlayerAnimation> createDodgeAnimation()
	{
		Map<Facing, PlayerAnimation> createdAnimations = new HashMap<Facing, PlayerAnimation>();
		
		for (Facing facing : Facing.values())
		{
			PlayerAnimation animation = new PlayerAnimation(7);
			
			animation.frames[0] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_BOTH_" + facing.name()), DODGE_MOVEMENT_FRAME_DURATION, 0);
			animation.frames[1] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_LEFT_" + facing.name()), DODGE_MOVEMENT_FRAME_DURATION, -4);
			animation.frames[2] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_BOTH_" + facing.name()), DODGE_MOVEMENT_FRAME_DURATION, -4);
			animation.frames[3] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_RIGHT_" + facing.name()), DODGE_PAUSE_FRAME_DURATION, -4);
			animation.frames[4] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_BOTH_" + facing.name()), DODGE_MOVEMENT_FRAME_DURATION, 4);
			animation.frames[5] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_LEFT_" + facing.name()), DODGE_MOVEMENT_FRAME_DURATION, 4);
			animation.frames[6] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_BOTH_" + facing.name()), 0, 4);
			
			createdAnimations.put(facing, animation);
		}
		
		return createdAnimations;
	}

	private static Map<Facing, PlayerAnimation> createDodgeBallAnimation()
	{
		Map<Facing, PlayerAnimation> createdAnimations = new HashMap<Facing, PlayerAnimation>();
		
		for (Facing facing : Facing.values())
		{
			PlayerAnimation animation = new PlayerAnimation(7);
			
			animation.frames[0] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_BOTH_BALL_" + facing.name()), DODGE_MOVEMENT_FRAME_DURATION, 0);
			animation.frames[1] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_LEFT_BALL_" + facing.name()), DODGE_MOVEMENT_FRAME_DURATION, -4);
			animation.frames[2] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_BOTH_BALL_" + facing.name()), DODGE_MOVEMENT_FRAME_DURATION, -4);
			animation.frames[3] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_RIGHT_BALL_" + facing.name()), DODGE_PAUSE_FRAME_DURATION, -4);
			animation.frames[4] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_BOTH_BALL_" + facing.name()), DODGE_MOVEMENT_FRAME_DURATION, 4);
			animation.frames[5] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_LEFT_BALL_" + facing.name()), DODGE_MOVEMENT_FRAME_DURATION, 4);
			animation.frames[6] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_BOTH_BALL_" + facing.name()), 0, 4);
			
			createdAnimations.put(facing, animation);
		}
		
		return createdAnimations;
	}

	private static Map<Facing, PlayerAnimation> createStrongCheckAnimation()
	{
		Map<Facing, PlayerAnimation> createdAnimations = new HashMap<Facing, PlayerAnimation>();
		
		for (Facing facing : Facing.values())
		{
			PlayerAnimation animation = new PlayerAnimation(10);
			
			animation.frames[0] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("PASSIVE_" + facing.name()), 80, 0);		//to wait for the other player to dodge
			animation.frames[1] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("CHECK_WINDUP_" + facing.name()), CHECK_FRAME_DURATION, 0);
			animation.frames[2] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_BOTH_" + facing.name()), CHECK_FRAME_DURATION, 7);
			animation.frames[3] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("CHECK_STRONG_" + facing.name()), CHECK_FRAME_DURATION, 5);
			animation.frames[4] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("CHECK_STRONG_" + facing.name()), CHECK_FRAME_DURATION, 5);
			animation.frames[5] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("CHECK_STRONG_" + facing.name()), 2 * CHECK_FRAME_DURATION, 4);
			animation.frames[6] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("CHECK_STRONG_" + facing.name()), CHECK_FRAME_DURATION, -4);
			animation.frames[7] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_BOTH_" + facing.name()), CHECK_FRAME_DURATION, -5);
			animation.frames[8] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("CHECK_WINDUP_" + facing.name()), CHECK_FRAME_DURATION, -5);
			animation.frames[9] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("PASSIVE_" + facing.name()), 0, -7);
			
			createdAnimations.put(facing, animation);
		}
		
		return createdAnimations;
	}

	private static Map<Facing, PlayerAnimation> createWeakCheckAnimation()
	{
		Map<Facing, PlayerAnimation> createdAnimations = new HashMap<Facing, PlayerAnimation>();
		
		for (Facing facing : Facing.values())
		{
			PlayerAnimation animation = new PlayerAnimation(10);
			
			animation.frames[0] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("PASSIVE_" + facing.name()), 80, 0);		//to wait for the other player to dodge
			animation.frames[1] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("CHECK_WINDUP_" + facing.name()), CHECK_FRAME_DURATION, 0);
			animation.frames[2] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_BOTH_" + facing.name()), CHECK_FRAME_DURATION, 7);
			animation.frames[3] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("CHECK_WEAK_" + facing.name()), CHECK_FRAME_DURATION, 5);
			animation.frames[4] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("CHECK_WEAK_" + facing.name()), CHECK_FRAME_DURATION, 5);
			animation.frames[5] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("CHECK_WEAK_" + facing.name()), 2 * CHECK_FRAME_DURATION, 4);
			animation.frames[6] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("CHECK_WEAK_" + facing.name()), CHECK_FRAME_DURATION, -4);
			animation.frames[7] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_BOTH_" + facing.name()), CHECK_FRAME_DURATION, -5);
			animation.frames[8] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("CHECK_WINDUP_" + facing.name()), CHECK_FRAME_DURATION, -5);
			animation.frames[9] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("PASSIVE_" + facing.name()), 0, -7);
			
			createdAnimations.put(facing, animation);
		}
		
		return createdAnimations;
	}

	private static Map<Facing, PlayerAnimation> createBallCheckAnimation()
	{
		Map<Facing, PlayerAnimation> createdAnimations = new HashMap<Facing, PlayerAnimation>();
		
		for (Facing facing : Facing.values())
		{
			PlayerAnimation animation = new PlayerAnimation(10);
			
			animation.frames[0] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("PASSIVE_BALL_" + facing.name()), 80, 0);		//to wait for the other player to dodge
			animation.frames[1] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("CHECK_WINDUP_BALL_" + facing.name()), CHECK_FRAME_DURATION, 0);
			animation.frames[2] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_BOTH_BALL_" + facing.name()), CHECK_FRAME_DURATION, 7);
			animation.frames[3] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_BOTH_BALL_" + facing.name()), CHECK_FRAME_DURATION, 5);
			animation.frames[4] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_BOTH_BALL_" + facing.name()), CHECK_FRAME_DURATION, 5);
			animation.frames[5] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_BOTH_BALL_" + facing.name()), 2 * CHECK_FRAME_DURATION, 4);
			animation.frames[6] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_BOTH_BALL_" + facing.name()), CHECK_FRAME_DURATION, -4);
			animation.frames[7] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("WALK_BOTH_BALL_" + facing.name()), CHECK_FRAME_DURATION, -5);
			animation.frames[8] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("CHECK_WINDUP_BALL_" + facing.name()), CHECK_FRAME_DURATION, -5);
			animation.frames[9] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("PASSIVE_BALL_" + facing.name()), 0, -7);
			
			createdAnimations.put(facing, animation);
		}
		
		return createdAnimations;
	}

	private static Map<Facing, PlayerAnimation> createSlideAnimation()
	{
		Map<Facing, PlayerAnimation> createdAnimations = new HashMap<Facing, PlayerAnimation>();
		
		for (Facing facing : Facing.values())
		{
			PlayerAnimation animation = new PlayerAnimation(5);
			
			animation.frames[0] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("PASSIVE_" + facing.name()), SLIDE_FRAME_DURATION, 6);
			animation.frames[1] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("PASSIVE_" + facing.name()), SLIDE_FRAME_DURATION, 9);
			animation.frames[2] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("PASSIVE_" + facing.name()), SLIDE_FRAME_DURATION, 9);
			animation.frames[3] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("PASSIVE_" + facing.name()), SLIDE_FRAME_DURATION, 6);
			animation.frames[4] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("PASSIVE_" + facing.name()), 0, 6);
			
			createdAnimations.put(facing, animation);
		}
		
		return createdAnimations;
	}

	private static Map<Facing, PlayerAnimation> createSlideBallAnimation()
	{
		Map<Facing, PlayerAnimation> createdAnimations = new HashMap<Facing, PlayerAnimation>();
		
		for (Facing facing : Facing.values())
		{
			PlayerAnimation animation = new PlayerAnimation(5);
			
			animation.frames[0] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("PASSIVE_BALL_" + facing.name()), SLIDE_FRAME_DURATION, 6);
			animation.frames[1] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("PASSIVE_BALL_" + facing.name()), SLIDE_FRAME_DURATION, 9);
			animation.frames[2] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("PASSIVE_BALL_" + facing.name()), SLIDE_FRAME_DURATION, 9);
			animation.frames[3] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("PASSIVE_BALL_" + facing.name()), SLIDE_FRAME_DURATION, 6);
			animation.frames[4] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("PASSIVE_BALL_" + facing.name()), 0, 6);
			
			createdAnimations.put(facing, animation);
		}
		
		return createdAnimations;
	}

	private static Map<Facing, PlayerAnimation> createKnockbackFallAnimation()
	{
		Map<Facing, PlayerAnimation> createdAnimations = new HashMap<Facing, PlayerAnimation>();
		
		for (Facing facing : Facing.values())
		{
			PlayerAnimation animation = new PlayerAnimation(4);
			
			animation.frames[0] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("KNOCKBACK_KO1_" + facing.name()), KNOCKBACK_KO_FRAME_DURATION, 9);
			animation.frames[1] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("KNOCKBACK_KO2_" + facing.name()), KNOCKBACK_KO_FRAME_DURATION, 18);
			animation.frames[2] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("KNOCKBACK_KO2_" + facing.name()), KNOCKBACK_KO_FRAME_DURATION * 3, 9);
			animation.frames[3] = new PlayerAnimationFrame(PlayerSpriteType.valueOf("DOWN_" + facing.name()), KNOCKBACK_KO_FRAME_DURATION, 0);
			
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
