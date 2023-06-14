package main.logic.ai.coach.skill;

import main.data.entities.Player;
import main.data.entities.Skill;

public interface SkillSelectionAi
{
	Skill getNextSkillToGain(Player player);
}
