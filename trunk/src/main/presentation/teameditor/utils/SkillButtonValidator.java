package main.presentation.teameditor.utils;

import main.data.entities.Player;

public class SkillButtonValidator
{
	private int skillCosts[];
	private String skillPrereqs[];
	private boolean isSensei;

	public SkillButtonValidator(int totalSkills)
	{
		skillCosts = new int[totalSkills + 1];
		skillPrereqs = new String[totalSkills + 1];
		this.isSensei = false;
		
		fillRequirements();
	}

	// call from TeamEditorGUI when the sensei skill is gained
	public void setSensei(boolean hasSensei)
	{
		isSensei = hasSensei;
	}

	public boolean isButtonEnabled(int skill, Player player)
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

	private int getSkillXpCost(int skill, Player player)
	{
		int xpCost = skillCosts[skill];

		// TODO: check for Intelligent quirk
		// TODO: check for Moron quirk

		if (isSensei)
			xpCost = (int) (xpCost * .9);

		return xpCost;
	}
	
	private boolean hasRequiredSkills(int skill, Player player)
	{
		String prereqs = skillPrereqs[skill];
		
		for (int i = 0; i < prereqs.length(); i++)
		{
			int prereqIndex = prereqs.charAt(i) - 65;
			if (!player.hasSkill(prereqIndex))
				return false;
		}
		
		return true;
	}

	public boolean isButtonSelected(int skill, Player player)
	{
		return (player != null && player.hasSkill(skill));
	}

	private void fillRequirements()
	{
		skillCosts[0] = 0;
		skillCosts[Player.SKILL_INTUITION] = 20;
		skillCosts[Player.SKILL_STOIC] = 40;
		skillCosts[Player.SKILL_SLY] = 40;
		skillCosts[Player.SKILL_LEADER] = 60;
		skillCosts[Player.SKILL_KARMA] = 60;
		skillCosts[Player.SKILL_AWE] = 80;
		skillCosts[Player.SKILL_HEALER] = 80;
		skillCosts[Player.SKILL_SENSEI] = 100;

		skillCosts[Player.SKILL_JUGGLING] = 20;
		skillCosts[Player.SKILL_GYMNASTICS] = 20;
		skillCosts[Player.SKILL_BOXING] = 20;
		skillCosts[Player.SKILL_SCOOP] = 40;
		skillCosts[Player.SKILL_JUDO] = 40;
		skillCosts[Player.SKILL_COMBO] = 40;
		skillCosts[Player.SKILL_STRIP] = 60;
		skillCosts[Player.SKILL_QUICKENING] = 60;
		skillCosts[Player.SKILL_FIST_OF_IRON] = 80;
		skillCosts[Player.SKILL_DOOMSTRIKE] = 100;

		skillCosts[Player.SKILL_GUARD] = 20;
		skillCosts[Player.SKILL_TACTICS] = 40;
		skillCosts[Player.SKILL_BRUTAL] = 40;
		skillCosts[Player.SKILL_STALWART] = 40;
		skillCosts[Player.SKILL_CHECKMASTER] = 60;
		skillCosts[Player.SKILL_VICIOUS] = 60;
		skillCosts[Player.SKILL_RESILIENT] = 60;
		skillCosts[Player.SKILL_CHARGE] = 80;
		skillCosts[Player.SKILL_JUGGERNAUT] = 80;
		skillCosts[Player.SKILL_TERROR] = 100;

		for (int i = 0; i < skillPrereqs.length; i++)
			skillPrereqs[i] = "";

		addPrereq(Player.SKILL_SLY, Player.SKILL_INTUITION);
		addPrereq(Player.SKILL_LEADER, Player.SKILL_STOIC);
		addPrereq(Player.SKILL_KARMA, Player.SKILL_SLY);
		addPrereq(Player.SKILL_AWE, Player.SKILL_LEADER);
		addPrereq(Player.SKILL_HEALER, Player.SKILL_KARMA);
		addPrereq(Player.SKILL_SENSEI, Player.SKILL_AWE);
		addPrereq(Player.SKILL_SENSEI, Player.SKILL_HEALER);

		addPrereq(Player.SKILL_SCOOP, Player.SKILL_JUGGLING);
		addPrereq(Player.SKILL_JUDO, Player.SKILL_GYMNASTICS);
		addPrereq(Player.SKILL_COMBO, Player.SKILL_BOXING);
		addPrereq(Player.SKILL_STRIP, Player.SKILL_SCOOP);
		addPrereq(Player.SKILL_QUICKENING, Player.SKILL_JUDO);
		addPrereq(Player.SKILL_QUICKENING, Player.SKILL_COMBO);
		addPrereq(Player.SKILL_FIST_OF_IRON, Player.SKILL_STRIP);
		addPrereq(Player.SKILL_FIST_OF_IRON, Player.SKILL_QUICKENING);
		addPrereq(Player.SKILL_DOOMSTRIKE, Player.SKILL_FIST_OF_IRON);

		addPrereq(Player.SKILL_STALWART, Player.SKILL_GUARD);
		addPrereq(Player.SKILL_CHECKMASTER, Player.SKILL_TACTICS);
		addPrereq(Player.SKILL_VICIOUS, Player.SKILL_BRUTAL);
		addPrereq(Player.SKILL_RESILIENT, Player.SKILL_STALWART);
		addPrereq(Player.SKILL_CHARGE, Player.SKILL_CHECKMASTER);
		addPrereq(Player.SKILL_JUGGERNAUT, Player.SKILL_VICIOUS);
		addPrereq(Player.SKILL_JUGGERNAUT, Player.SKILL_RESILIENT);
		addPrereq(Player.SKILL_TERROR, Player.SKILL_CHARGE);
		addPrereq(Player.SKILL_TERROR, Player.SKILL_JUGGERNAUT);
	}

	private void addPrereq(int skill, int requiredSkill)
	{
		String preReqs = skillPrereqs[skill];

		preReqs = preReqs + (char) (requiredSkill + 65);

		skillPrereqs[skill] = preReqs;
	}
}
