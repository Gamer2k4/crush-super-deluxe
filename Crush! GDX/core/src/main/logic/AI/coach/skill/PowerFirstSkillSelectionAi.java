package main.logic.ai.coach.skill;

import java.util.ArrayList;
import java.util.List;

import main.data.entities.Skill;

public class PowerFirstSkillSelectionAi extends PrioritySkillSelectionAi
{
	@Override
	protected List<Skill> getSkillPriority()
	{
		List<Skill> priorities = new ArrayList<Skill>();
		
		priorities.add(Skill.TERROR);
		
		return priorities;
	}
}
