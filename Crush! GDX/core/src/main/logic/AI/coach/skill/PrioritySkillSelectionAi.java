package main.logic.ai.coach.skill;

import java.util.ArrayList;
import java.util.List;

import main.data.entities.Player;
import main.data.entities.Skill;
import main.logic.Randomizer;

public abstract class PrioritySkillSelectionAi extends RandomSkillSelectionAi
{	
	@Override
	public Skill getNextSkillToGain(Player player)		//TODO: handle sensei (tricky because it's if any player on the team has it)
	{
		if (player.hasSkill(Skill.NINJA_MASTER))
			return null;
		
		boolean allPrioritySkillsGained = true;
		
		List<Skill> skillPriority = getSkillPriority();
		
		for (Skill skill : skillPriority)
		{
			if (player.hasSkill(skill))
				continue;
			
			allPrioritySkillsGained = false;
			
			Skill nextSkill = getNextAvailableSkill(player, skill);
			
			if (nextSkill == null)		//no available skills (likely too expensive, or simply all obtained) for this priority
				continue;
			
			return nextSkill;
		}
		
		if (allPrioritySkillsGained)
			return getRandomAvailableSkill(player);
		
		return null;		//if the player doesn't have all their priority skills but still got here, they likely can't afford the next one, so they're saving up
	}
	
	private Skill getNextAvailableSkill(Player player, Skill skill)
	{
		List<Skill> prereqs = validator.getSkillPrerequisites(skill);
		prereqs = shuffleList(prereqs);
		
		for (Skill prereq : prereqs)
		{
			if (player.hasSkill(prereq))
				continue;
			
			Skill nextSkill = getNextAvailableSkill(player, prereq);	//see if the prerequisite has any prerequisites
			
			if (nextSkill != null)
				return nextSkill;
		}
		
		if (skill.getCost() < player.getSkillPoints())
			return skill;
		
		return null;
	}
	
	private List<Skill> shuffleList(List<Skill> originalPrereqs)
	{
		List<Skill> prereqs = new ArrayList<Skill>(originalPrereqs); 
		List<Skill> shuffledPrereqs = new ArrayList<Skill>();
		
		
		while (!prereqs.isEmpty())
		{
			Skill skill = prereqs.remove(Randomizer.getRandomInt(0, prereqs.size() - 1));
			shuffledPrereqs.add(skill);
		}
		
		return shuffledPrereqs;
	}

	protected abstract List<Skill> getSkillPriority();	
}