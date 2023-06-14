package main.logic.ai.coach.player;

public enum DraftingAiType
{
	RANDOM(new RandomPlayerDraftingAi()),
	BEST_PLAYER(new BestPlayerDraftingAi()),
	CURMIAN_RUSH(new CurmianRushDraftingAi()),
	HAPPY_TROOP(new HappyTroopDraftingAi()),
	KURGAN_SLITH(new KurganSlithArdentDraftingAi()),
	NYNAX_GRONK(new NynaxGronkDraftingAi()),
	NYNAX_STEALTH_SURGE(new NynaxStealthSurgeDraftingAi()),
	TEAM_ICE(new TeamIceDraftingAi());
	
	private DraftingAi ai;
	
	private DraftingAiType(DraftingAi ai)
	{
		this.ai = ai;
	}
	
	public DraftingAi getAi()
	{
		return ai;
	}
}
