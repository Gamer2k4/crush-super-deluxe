package main.data.factory;

import java.util.ArrayList;
import java.util.List;

import main.logic.Randomizer;

public class RandomlyNamedEntityFactory
{
	protected String safeGetRandomName(List<String> all, List<String> available)
	{
		if (available.isEmpty())
		{
			available.addAll(randomNameFill(all));
		}
		
		int nameIndex = Randomizer.getRandomInt(0, available.size() - 1);
		return available.remove(nameIndex);
	}

	protected List<String> randomNameFill(List<String> source)
	{
		List<String> shuffledList = new ArrayList<String>();
		List<String> unshuffledList = deepCopyList(source);
		
		while (!unshuffledList.isEmpty())
		{
			int index = Randomizer.getRandomInt(0, unshuffledList.size() - 1);
			shuffledList.add(unshuffledList.remove(index));
		}
		
		return shuffledList;
	}
	
	protected List<String> deepCopyList(List<String> toCopy)
	{
		List<String> copy = new ArrayList<String>();
		
		for (String s : toCopy)
		{
			copy.add(new String(s));
		}
		
		return copy;
	}

	protected String normalizeName(String name)
	{
		if (name.isEmpty())
			return "";

		String normalizedName = "_" + name;

		while (normalizedName.contains("_"))
		{
			int index = normalizedName.indexOf("_");
			if (index == normalizedName.length() - 1)
			{
				normalizedName = normalizedName.substring(0, index);
				break;
			}

			String half1 = normalizedName.substring(0, index);
			String half2 = normalizedName.substring(index + 2);
			String nextChar = normalizedName.substring(index + 1, index + 2);

			normalizedName = half1 + " " + nextChar.toUpperCase() + half2;
		}

		return normalizedName.substring(1);
	}
}
