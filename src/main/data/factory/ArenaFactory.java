package main.data.factory;

import main.data.entities.Arena;

public abstract class ArenaFactory
{
	public abstract Arena generateArena(int arenaNumber);
}
