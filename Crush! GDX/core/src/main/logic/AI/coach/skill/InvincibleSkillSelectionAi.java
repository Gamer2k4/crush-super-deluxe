package main.logic.ai.coach.skill;

import java.util.ArrayList;
import java.util.List;

import main.data.entities.Skill;

public class InvincibleSkillSelectionAi extends PrioritySkillSelectionAi
{
	@Override
	protected List<Skill> getSkillPriority()
	{
		List<Skill> priorities = new ArrayList<Skill>();
		
		priorities.add(Skill.STOIC);
		priorities.add(Skill.KARMA);
		priorities.add(Skill.RESILIENT);
		
		return priorities;
	}
}
