package main.logic;

import java.util.Random;

public class RandomGeneratorSingletonImpl implements RandomGenerator
{
	private static volatile RandomGenerator instance = null;
	
	private RandomGeneratorSingletonImpl()
	{
		//intentionally blank for the singleton pattern
	}
	
	public static RandomGenerator getInstance()
	{
		if (instance == null) {
            synchronized (RandomGenerator.class) {
                // Double check
                if (instance == null) {
                    instance = new RandomGeneratorSingletonImpl();
                }
            }
        }
        return instance;
	}
	
	@Override
	public int getRandomInt(int lower, int upper)
	{
		Random r = new Random();
		
		return r.nextInt(upper + 1 - lower) + lower;
	}
}
