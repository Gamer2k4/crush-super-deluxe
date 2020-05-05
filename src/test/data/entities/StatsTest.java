package test.data.entities;

import main.data.entities.Stats;

import org.junit.Before;
import org.junit.Test;

public class StatsTest
{
	private Stats stats1;
	
	@Before
	public void setUp()
	{
		stats1 = new Stats();
	}
	
	@Test
	public void testHash()
	{
		System.out.println(stats1.getUniqueId());	//TODO: not a real test
	}
}
