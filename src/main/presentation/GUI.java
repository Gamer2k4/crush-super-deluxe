package main.presentation;

import main.data.Event;
import main.logic.Client;
import main.logic.Server;

public abstract class GUI
{
	protected Client myClient;
	protected Server myServer;
	
	public GUI(Client theClient, Server theServer)
	{
		myClient = theClient;
		myServer = theServer;
	}
	
	public void sendCommand(Event theCommand)
	{
		myClient.sendCommand(theCommand);
	}
	
	public abstract void receiveEvent(Event theEvent);
	protected abstract void refreshInterface();
}
