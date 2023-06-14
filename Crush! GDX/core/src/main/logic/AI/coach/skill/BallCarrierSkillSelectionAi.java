package main.logic.ai.coach.skill;

import java.util.ArrayList;
import java.util.List;

import main.data.entities.Skill;

public class BallCarrierSkillSelectionAi extends PrioritySkillSelectionAi
{
	@Override
	protected List<Skill> getSkillPriority()
	{
		List<Skill> priorities = new ArrayList<Skill>();
		
		priorities.add(Skill.INTUITION);
		priorities.add(Skill.SCOOP);
		priorities.add(Skill.QUICKENING);
		priorities.add(Skill.AWE);
		
		return priorities;
	}
}
