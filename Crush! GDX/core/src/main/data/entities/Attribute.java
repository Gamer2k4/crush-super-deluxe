package main.data.entities;

public enum Attribute
{
	AP(0, "ACTION POINTS"),
	CH(1, "CHECKING"),
	ST(2, "STRENGTH"),
	TG(3, "TOUGHNESS"),
	RF(4, "REFLEXES"),
	JP(5, "JUMPING"),
	HD(6, "HANDS"),
	DA(7, "DODGE");
	
	private int index;
	private String description;
	
	private Attribute(int index, String description)
	{
		this.index = index;
		this.description = description;
	}
	
	public int getIndex()
	{
		return index;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public String getLongDescription()
	{
		return description + " (" + name() + ")";
	}
	
	public static Attribute fromIndex(int indexToCheck)
	{
		for (Attribute attribute : Attribute.values())
		{
			if (attribute.getIndex() == indexToCheck)
				return attribute;
		}
		
		throw new IllegalArgumentException("No attribute found for index " + indexToCheck);
	}
}
