package main.presentation.teameditor.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.data.entities.Player;
import main.data.entities.Skill;

public class SkillButtonValidator
{
	private Map<Skill, List<Skill>> skillPrereqs = new HashMap<Skill, List<Skill>>();
	private boolean isSensei;

	public SkillButtonValidator()
	{
		this.isSensei = false;
		
		fillRequirements();
	}

	// call from TeamEditorGUI when the sensei skill is gained
	public void setSensei(boolean hasSensei)
	{
		isSensei = hasSensei;
	}

	public boolean isButtonEnabled(Skill skill, Player player)
	{
		if (player == null)
			return false;

		if (player.hasSkill(skill))
			return false;
		
		if (player.getSkillPoints() < getSkillXpCost(skill, player))
			return false;
		
		if (!hasRequiredSkills(skill, player))
			return false;
		
		return true;
	}

	private int getSkillXpCost(Skill skill, Player player)
	{
		int xpCost = skill.getCost();

		// TODO: check for Intelligent quirk
		// TODO: check for Moron quirk

		if (isSensei)
			xpCost = (int) (xpCost * .9);

		return xpCost;
	}
	
	private boolean hasRequiredSkills(Skill skill, Player player)
	{
		List<Skill> prereqs = skillPrereqs.get(skill);
		
		if (prereqs == null)
			return true;
		
		for (Skill prereq : prereqs)
		{
			if (!player.hasSkill(prereq))
				return false;
		}
		
		return true;
	}

	public boolean isButtonSelected(Skill skill, Player player)
	{
		return (player != null && player.hasSkill(skill));
	}

	private void fillRequirements()
	{
		addPrereq(Skill.SLY, Skill.INTUITION);
		addPrereq(Skill.LEADER, Skill.STOIC);
		addPrereq(Skill.KARMA, Skill.SLY);
		addPrereq(Skill.AWE, Skill.LEADER);
		addPrereq(Skill.HEALER, Skill.KARMA);
		addPrereq(Skill.SENSEI, Skill.AWE);
		addPrereq(Skill.SENSEI, Skill.HEALER);

		addPrereq(Skill.SCOOP, Skill.JUGGLING);
		addPrereq(Skill.JUDO, Skill.GYMNASTICS);
		addPrereq(Skill.COMBO, Skill.BOXING);
		addPrereq(Skill.STRIP, Skill.SCOOP);
		addPrereq(Skill.QUICKENING, Skill.JUDO);
		addPrereq(Skill.QUICKENING, Skill.COMBO);
		addPrereq(Skill.FIST_OF_IRON, Skill.STRIP);
		addPrereq(Skill.FIST_OF_IRON, Skill.QUICKENING);
		addPrereq(Skill.DOOMSTRIKE, Skill.FIST_OF_IRON);

		addPrereq(Skill.STALWART, Skill.GUARD);
		addPrereq(Skill.CHECKMASTER, Skill.TACTICS);
		addPrereq(Skill.VICIOUS, Skill.BRUTAL);
		addPrereq(Skill.RESILIENT, Skill.STALWART);
		addPrereq(Skill.CHARGE, Skill.CHECKMASTER);
		addPrereq(Skill.JUGGERNAUT, Skill.VICIOUS);
		addPrereq(Skill.JUGGERNAUT, Skill.RESILIENT);
		addPrereq(Skill.TERROR, Skill.CHARGE);
		addPrereq(Skill.TERROR, Skill.JUGGERNAUT);
	}

	private void addPrereq(Skill skill, Skill requiredSkill)
	{
		List<Skill> prereqs = skillPrereqs.get(skill);
		
		if (prereqs == null)
			prereqs = new ArrayList<Skill>();
		
		prereqs.add(requiredSkill);
		
		skillPrereqs.put(skill, prereqs);
	}
}
