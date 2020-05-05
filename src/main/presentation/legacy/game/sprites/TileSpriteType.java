package main.presentation.legacy.game.sprites;

public enum TileSpriteType
{
	UNDEFINED_TYPE(-1, 0),
	STUN_STARS(-2, 0),	//necessary because the stun stars animation frames aren't sequential
	WALK_TARGET_RED(0),
//	RAT_SE(1),	//seems to be repeated later on
	TERROR(2),
	GOAL_00_DIM(3),
	GOAL_10_DIM(4),
	GOAL_20_DIM(5),
	GOAL_30_DIM(6),
	GOAL_01_DIM(7),
	GOAL_11_DIM(8),
	GOAL_21_DIM(9),
	GOAL_31_DIM(10),
	GOAL_02_DIM(11),
	GOAL_12_DIM(12),
	GOAL_22_DIM(13),
	GOAL_32_DIM(14),
	GOAL_03_DIM(15),
	GOAL_13_DIM(16),
	GOAL_23_DIM(17),
	GOAL_33_DIM(18),
	WALK_NW_RED(19),
	WALK_N_RED(20),
	WALK_NE_RED(21),
	WALK_E_RED(22),
	WALK_SE_RED(23),
	WALK_S_RED(24),
	WALK_SW_RED(25),
	WALK_W_RED(26),
	CURSOR(38),
	BALL_HIGHLIGHT(39),
	BIN_N_UNTRIED(43),
	BIN_S_UNTRIED(44),
	BIN_W_UNTRIED(45),
	BIN_E_UNTRIED(46),
	BIN_N_GREEN(47),
	BIN_S_GREEN(48),
	BIN_W_GREEN(49),
	BIN_E_GREEN(50),
	BIN_N_RED(51),
	BIN_S_RED(52),
	BIN_W_RED(53),
	BIN_E_RED(54),
	PORTAL_TILE(63, 4),
	PAD_N_UNTRIED(67),
	PAD_S_UNTRIED(68),
	PAD_W_UNTRIED(69),
	PAD_E_UNTRIED(70),
	PAD_N_TRIED(71),
	PAD_S_TRIED(72),
	PAD_W_TRIED(73),
	PAD_E_TRIED(74),
	ELECTRIC_TILE(75, 4),
	SLITH_GAS(79),
	VALOR(80),
	BLOODLUST(81),
	MEDICAL_BELT(82),
	SCRAMBLER(83),
	BALL(84),
	FIELD_INTEGRITY_BELT(90),
	BACKFIRE_BELT(94),
	WALK_TARGET_GREEN(127),
//	CHECK_GREEN(128),	//seems to be repeated later on
	HANDOFF(129),
	JUMP(130),
	GOAL_00_LIT(131),
	GOAL_10_LIT(132),
	GOAL_20_LIT(133),
	GOAL_30_LIT(134),
	GOAL_01_LIT(135),
	GOAL_11_LIT(136),
	GOAL_21_LIT(137),
	GOAL_31_LIT(138),
	GOAL_02_LIT(139),
	GOAL_12_LIT(140),
	GOAL_22_LIT(141),
	GOAL_32_LIT(142),
	GOAL_03_LIT(143),
	GOAL_13_LIT(144),
	GOAL_23_LIT(145),
	GOAL_33_LIT(146),
	WALK_NW_GREEN(147),
	WALK_N_GREEN(148),
	WALK_NE_GREEN(149),
	WALK_E_GREEN(150),
	WALK_SE_GREEN(151),
	WALK_S_GREEN(152),
	WALK_SW_GREEN(153),
	WALK_W_GREEN(154),
	CHECK_YELLOW(155),
	CHECK_RED(156),
	CHECK_GREEN(157),
	STUN_STARS1(162),
	STUN_STARS2(166),
	STUN_STARS3(170),
	STUN_STARS4(174),
	RAT_NW(178),
	RAT_SE(182);
	
	private int index;
	private int frames;
	
	private TileSpriteType(int index)
	{
		this(index, 1);
	}
	
	private TileSpriteType(int index, int frames)
	{
		this.index = index;
		this.frames = frames;
	}
	
	public int getIndex()
	{
		return index;
	}
	
	public boolean isStatic()
	{
		return frames == 1;
	}
	
	public static TileSpriteType getTileSpriteType(int typeIndex)
	{
		for (TileSpriteType type : values())
		{
			if (type.index == typeIndex)
				return type;
		}
		
		return TileSpriteType.UNDEFINED_TYPE;
	}
}
