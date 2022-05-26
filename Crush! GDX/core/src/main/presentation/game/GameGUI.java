package main.presentation.game;

import java.awt.Point;

import main.data.Data;
import main.data.Event;
import main.data.entities.Player;
import main.data.entities.Skill;
import main.logic.Client;
import main.logic.Server;

public abstract class GameGUI
{
	protected Client myClient;
	protected Server myServer;
	
	protected boolean isProcessingEvent = false;

	public GameGUI(Client theClient, Server theServer)
	{
		myClient = theClient;
		myServer = theServer;
	}

	public void sendCommand(Event theCommand)
	{
		myClient.sendCommand(theCommand);
	}

	// I don't like that I'm just reimplementing the call here. I think this should be a command at some point, but at least it's only in this one spot for now.
	protected int getAssistBonus(Player ally, Player target)
	{
		// tactics negates assist bonuses
		if (target.hasSkill(Skill.TACTICS))
			return 0;

		int team = getData().getTeamIndexOfPlayer(ally);
		Point coords = getData().getLocationOfPlayer(target);

		// bonus starts at 0 because the check is going to pick up the player involved in the attack
		int toRet = 0;

		for (int i = -1; i <= 1; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				int x = coords.x + i;
				int y = coords.y + j;
				Player p = getData().getPlayerAtLocation(x, y);

				// don't add the bonuses from the attacker or the defender
				if (p == ally || p == target)
					continue;

				// if the player is there, is standing, and is co-aligned
				if (p != null && p.getStatus() == Player.STS_OKAY && myClient.getData().getTeamIndexOfPlayer(p) == team)
				{
					toRet += 10;

					// teammates with guard help even more
					if (p.hasSkill(Skill.GUARD))
						toRet += 5;
				}
			}
		}

		return toRet;
	}
	
	protected Data getData()
	{
		return myClient.getData();
	}
	
	public boolean isProcessingEvent()
	{
		return isProcessingEvent;
	}

	public abstract void receiveEvent(Event theEvent);

	public abstract void closeGUI();

	public abstract void refreshInterface();
	
	public abstract void beginGame();
	
	public abstract void endGame();
	
	public abstract boolean gameStarted();
}
