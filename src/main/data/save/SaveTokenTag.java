package main.data.save;

public enum SaveTokenTag
{
	T_UID,	//team unique identifier
	T_NAM,	//team name
	T_CNM,	//team coach name 
	T_FLD,	//team home field
	T_MNY,	//team money
	T_DOC,	//team docbot settings
	T_UEQ,	//team unassigned equipment
	T_FGC,	//team foreground color
	T_BGC,	//team background color
	T_PLR,	//team players
	
	P_UID,	//player unique identifier
	P_STS,	//player status
	P_NAM,	//player name
	P_RCE,	//player race
	P_ATT,	//player attributes
	P_INJ,	//player injury attribute modifiers
	P_SKL,	//player skills
	P_WKS,	//player weeks out
	P_ITP,	//player injury type
	P_EQP,	//player equipment
	P_XP_	//player xp
	//current action points aren't saved because they're never relevant outside of the game
}
