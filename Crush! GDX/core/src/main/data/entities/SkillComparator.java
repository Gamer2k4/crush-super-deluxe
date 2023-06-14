package main.data.entities;

import java.util.Comparator;

public class SkillComparator implements Comparator<Skill>
{
	@Override
	public int compare(Skill skill1, Skill skill2)
	{
		if (skill1.getTier() == 6)
			return 1;
		if (skill2.getTier() == 6)
			return -1;
		
		if (skill1.getTier() == skill2.getTier())
			return Integer.compare(skill1.getLegacyIndex(), skill2.getLegacyIndex());
		
		return -1 * Integer.compare(skill1.getTier(), skill2.getTier());
	}
}
