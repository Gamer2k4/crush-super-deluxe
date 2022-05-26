package main.presentation.game.sprite;

public enum PlayerState
{
	PASSIVE,
	PASSIVE_BALL,
	SHOCK,
	DOWN,
	SIT,
	WALK,
	WALK_BALL,
	CHECK_WEAK,
	CHECK_STRONG,
	CHECK_BALL,
	DODGE,
	DODGE_BALL,
	JUMP,
	JUMP_BALL,
	SLIDE,
	SLIDE_BALL,
	KNOCKBACK_FALL,
	INJURY,
	BALL_GIVE,
	BALL_RECEIVE,
	BALL_HURL;
	
	public PlayerState withBall()
	{
		if (this == PlayerState.CHECK_WEAK || this == PlayerState.CHECK_STRONG)
			return CHECK_BALL;
		if (this == PlayerState.PASSIVE)
			return PASSIVE_BALL;
		if (this == PlayerState.WALK)
			return WALK_BALL;
		if (this == PlayerState.DODGE)
			return DODGE_BALL;
		if (this == PlayerState.SLIDE)
			return SLIDE_BALL;
		
		return this;
	}
	
	public PlayerState withoutBall()
	{
		if (this == PlayerState.CHECK_BALL)
			return CHECK_WEAK;
		if (this == PlayerState.PASSIVE_BALL)
			return PASSIVE;
		if (this == PlayerState.WALK_BALL)
			return WALK;
		if (this == PlayerState.DODGE_BALL)
			return DODGE;
		if (this == PlayerState.SLIDE_BALL)
			return SLIDE;
		
		return this;
	}
}
