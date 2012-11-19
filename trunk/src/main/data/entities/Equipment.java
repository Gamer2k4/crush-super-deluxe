package main.data.entities;

public class Equipment
{
	public int index;
	public int type;
		
	//note that this doesn't need to be serialized, since it can't change
	//really generalize this in the future; for now, just have concrete effects based on the index number.
}