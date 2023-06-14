package main.logic.ai.coach.skill;

import java.util.ArrayList;
import java.util.List;

import main.data.entities.Skill;

public class AgilityFirstSkillSelectionAi extends PrioritySkillSelectionAi
{
	@Override
	protected List<Skill> getSkillPriority()
	{
		List<Skill> priorities = new ArrayList<Skill>();
		
		priorities.add(Skill.DOOMSTRIKE);
		
		return priorities;
	}
}
