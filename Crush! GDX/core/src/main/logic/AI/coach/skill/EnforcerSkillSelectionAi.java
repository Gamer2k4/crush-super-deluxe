package main.logic.ai.coach.skill;

import java.util.ArrayList;
import java.util.List;

import main.data.entities.Skill;

public class EnforcerSkillSelectionAi extends PrioritySkillSelectionAi
{
	@Override
	protected List<Skill> getSkillPriority()
	{
		List<Skill> priorities = new ArrayList<Skill>();
		
		priorities.add(Skill.CHARGE);
		priorities.add(Skill.VICIOUS);
		priorities.add(Skill.COMBO);
		
		return priorities;
	}
}
