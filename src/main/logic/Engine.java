package main.logic;

import java.util.Queue;

import main.data.Event;
import main.data.entities.Player;

public interface Engine
{
	public abstract Queue<Event> generateEvents(Event theCommand);
	public abstract int getAssistBonus(Player ally, Player target);
}