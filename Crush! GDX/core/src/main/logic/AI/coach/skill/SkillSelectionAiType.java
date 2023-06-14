package main.logic.ai.coach.skill;

public enum SkillSelectionAiType
{
	AGILITY_FIRST(new AgilityFirstSkillSelectionAi()),
	BALL_CARRIER(new BallCarrierSkillSelectionAi()),
	ENFORCER(new EnforcerSkillSelectionAi()),
	INVINCIBLE(new InvincibleSkillSelectionAi()),
	POWER_FIRST(new PowerFirstSkillSelectionAi()),
	PSYCHE_FIRST(new PsycheFirstSkillSelectionAi()),
	RANDOM(new RandomSkillSelectionAi());
	
	private SkillSelectionAi ai;
	
	private SkillSelectionAiType(SkillSelectionAi ai)
	{
		this.ai = ai;
	}
	
	public SkillSelectionAi getAi()
	{
		return ai;
	}
}
