package test.presentation.legacy.framework;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import main.presentation.legacy.framework.ScreenCommand;

public class ScreenCommandTest
{
	@Test
	public void getCommandIndex_commandHasIndex_indexReturned()
	{
		assertEquals(10, ScreenCommand.EDIT_TEAM_10.getCommandIndex());
	}
	
	@Test
	public void getCommandIndex_commandHasIndex_negativeOneReturned()
	{
		assertEquals(-1, ScreenCommand.LEAGUE_TEAM_SELECT.getCommandIndex());
	}
}
