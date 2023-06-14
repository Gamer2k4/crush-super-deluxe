package main.logic.ai.coach.skill;

import java.util.ArrayList;
import java.util.List;

import main.data.entities.Player;
import main.data.entities.Skill;
import main.logic.Randomizer;
import main.presentation.common.SkillPrerequisiteValidator;

public class RandomSkillSelectionAi implements SkillSelectionAi
{
	protected SkillPrerequisiteValidator validator = new SkillPrerequisiteValidator();
	
	@Override
	public Skill getNextSkillToGain(Player player)
	{
		return getRandomAvailableSkill(player);
	}
	
	protected Skill getRandomAvailableSkill(Player player)
	{
		List<Skill> potentialSkills = new ArrayList<Skill>();
		
		for (Skill skill : Skill.values())
		{
			if (skill.getCost() == 0)
				continue;
			
			if (player.hasSkill(skill))
				continue;
			
			if (!validator.hasRequiredSkills(skill, player))
				continue;
			
			if (skill.getCost() > player.getSkillPoints())
				continue;
			
			potentialSkills.add(skill);
		}
		
		if (potentialSkills.isEmpty())
			return null;
		
		return potentialSkills.get(Randomizer.getRandomInt(0, potentialSkills.size() - 1));
	}
}
