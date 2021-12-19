package main.presentation.game;

public enum Action
{
	ACTION_NONE(-1),	//only should happen if there are no players on the team
	ACTION_MOVE(0),
	ACTION_CHECK(1),
	ACTION_JUMP(2),
	ACTION_HANDOFF(3),
	ACTION_END_TURN(4);
	
	private int index;
	
	private Action(int index)
	{
		this.index = index;
	}
	
	public int getIndex()
	{
		return index;
	}
}
