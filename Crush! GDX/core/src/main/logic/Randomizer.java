package main.logic;

public class Randomizer
{
	public static int getRandomInt(int lower, int upper)
	{
		return RandomGeneratorSingletonImpl.getInstance().getRandomInt(lower, upper);
	}
}
