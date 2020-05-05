package main.presentation.game;

public class HighlightIcon
{
	public int x;
	public int y;
	private int type;

	public HighlightIcon(int x, int y, int type)
	{
		this.x = x;
		this.y = y;
		this.type = type;
	}

	public boolean isHandoffTarget()
	{
		return (type == HI_HANDOFF);
	}

	public boolean isJumpTarget()
	{
		return (type == HI_JUMP);
	}

	public boolean isBadCheckTarget()
	{
		return (type == HI_CHECK_BAD);
	}

	public boolean isEvenCheckTarget()
	{
		return (type == HI_CHECK_EVEN);
	}

	public boolean isGoodCheckTarget()
	{
		return (type == HI_CHECK_GOOD);
	}
	
	public boolean isValidMoveTarget()
	{
		return (type == HI_MOVE_GOOD);
	}
	
	public boolean isInvalidMoveTarget()
	{
		return (type == HI_MOVE_BAD);
	}
	
	public static final int HI_HANDOFF = 0;
	public static final int HI_JUMP = 1;
	public static final int HI_CHECK_BAD = 2;
	public static final int HI_CHECK_EVEN = 3;
	public static final int HI_CHECK_GOOD = 4;
	public static final int HI_MOVE_GOOD = 5;
	public static final int HI_MOVE_BAD = 6;
}
