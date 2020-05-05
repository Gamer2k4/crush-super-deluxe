package main.presentation.legacy.game.sprites;

public enum PlayerSpriteType
{
	UNDEFINED_TYPE(-1, 0),
	PASSIVE_NW(0),
	KNOCKDOWN_NW(1, 2),
	DOWN_NW(3),
	PASSIVE_N(4),
	KNOCKDOWN_N(5, 2),
	DOWN_N(7),
	PASSIVE_NE(8),
	KNOCKDOWN_NE(9, 2),
	DOWN_NE(11),
	PASSIVE_E(12),
	KNOCKDOWN_E(13, 2),
	DOWN_E(15),
	PASSIVE_SE(16),
	KNOCKDOWN_SE(17, 2),
	DOWN_SE(19),
	PASSIVE_S(20),
	KNOCKDOWN_S(21, 2),
	DOWN_S(23),
	PASSIVE_SW(24),
	KNOCKDOWN_SW(25, 2),
	DOWN_SW(27),
	PASSIVE_W(28),
	KNOCKDOWN_W(28, 2),
	DOWN_W(31),
	WALK_NW(32, 2),
	CHECK_WEAK_NW(34),
	CHECK_STRONG_NW(35),
	WALK_N(36, 2),
	CHECK_WEAK_N(38),
	CHECK_STRONG_N(39),
	WALK_NE(40, 2),
	CHECK_WEAK_NE(42),
	CHECK_STRONG_NE(43),
	WALK_E(44, 2),
	CHECK_WEAK_E(46),
	CHECK_STRONG_E(47),
	WALK_SE(48, 2),
	CHECK_WEAK_SE(50),
	CHECK_STRONG_SE(51),
	WALK_S(52, 2),
	CHECK_WEAK_S(54),
	CHECK_STRONG_S(55),
	WALK_SW(56, 2),
	CHECK_WEAK_SW(58),
	CHECK_STRONG_SW(59),
	WALK_W(60, 2),
	CHECK_WEAK_W(62),
	CHECK_STRONG_W(63),
	JUMP_NW(64, 3),
	INJURY_NW(67),
	JUMP_N(68, 3),
	INJURY_N(71),
	JUMP_NE(72, 3),
	INJURY_NE(75),
	JUMP_E(76, 3),
	INJURY_E(79),
	JUMP_SE(80, 3),
	INJURY_SE(83),
	JUMP_S(84, 3),
	INJURY_S(87),
	JUMP_SW(88, 3),
	INJURY_SW(91),
	JUMP_W(92, 3),
	INJURY_W(95);
	//TODO: more
	
	private int index;
	private int frames;
	
	private PlayerSpriteType(int index)
	{
		this (index, 1);
	}
	
	private PlayerSpriteType(int index, int frames)
	{
		this.index = index;
		this.frames = frames;
	}
	
	public int getIndex()
	{
		return index;
	}
	
	public int getFrames()
	{
		return frames;
	}
	
	public boolean isStatic()
	{
		return frames == 1;
	}
	
	public static PlayerSpriteType getPlayerSpriteType(int typeIndex)
	{
		for (PlayerSpriteType type : values())
		{
			if (type.index == typeIndex)
				return type;
		}
		
		return PlayerSpriteType.UNDEFINED_TYPE;
	}
}
