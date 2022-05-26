package main.presentation.game.sprite;

public enum Facing
{
	NW, N, NE, W, E, SW, S, SE;
	
	public Facing rotateClockwise()
	{
		if (this == NW)
			return Facing.N;
		if (this == N)
			return Facing.NE;
		if (this == NE)
			return Facing.E;
		if (this == E)
			return Facing.SE;
		if (this == SE)
			return Facing.S;
		if (this == S)
			return Facing.SW;
		if (this == SW)
			return Facing.W;
		if (this == W)
			return Facing.NW;
		
		return this;
	}
	
	public Facing rotateCounterclockwise()
	{
		if (this == NW)
			return Facing.W;
		if (this == N)
			return Facing.NW;
		if (this == NE)
			return Facing.N;
		if (this == E)
			return Facing.NE;
		if (this == SE)
			return Facing.E;
		if (this == S)
			return Facing.SE;
		if (this == SW)
			return Facing.S;
		if (this == W)
			return Facing.SW;
		
		return this;
	}
}
