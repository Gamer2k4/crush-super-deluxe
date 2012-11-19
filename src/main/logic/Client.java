package main.logic;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Queue;

import main.data.Data;
import main.data.Event;
import main.data.entities.Team;
import main.presentation.ClientCursesGUI;
import main.presentation.GUI;

public class Client
{
	private Data dataLayer;
	private GUI presentationLayer;
	
	private Server host;
	
	boolean[] teamControl = new boolean[3];	//if controlsTeam[dataLayer.currentTeam], send commands to the server
	
	public Client(Server toConnectTo)
	{
		dataLayer = new Data();
		
		host = toConnectTo;
		
		String ipAddr = "";
		String hostname = "";
		
		//take this out eventually
		try
		{
		    InetAddress addr = InetAddress.getLocalHost();

		    // Get IP Address
		    ipAddr = addr.getHostAddress();

		    // Get hostname
		    hostname = addr.getHostName();
		}
		catch (UnknownHostException e)
		{
			//
		}
		
		
		host.addClient(this, ipAddr);
		
		for (int i = 0; i < 3; i++)
			teamControl[i] = true;		//for now, in single player mode, this client controls all three teams
		
		presentationLayer = new ClientCursesGUI(this);
	}
	
	public void newGame(List<Team> teams, int fieldnum)
	{
		dataLayer.newGame(teams, fieldnum);
	}
	
	public void processEvents(Queue<Event> eventQueue)
	{
		for (Event e : eventQueue)
		{
			//make sure the data is updated first, so when the GUI gets the event, the database will already reflect the correct values
			dataLayer.processEvent(e);
			presentationLayer.receiveEvent(e);
		}
	}
	
	public void sendCommand(Event theCommand)
	{
		host.receiveCommand(theCommand);
	}
	
	public boolean controlsTeam(int teamNum)
	{
		if (teamNum < 0 || teamNum > 2)
			return false;
		
		return teamControl[teamNum];
	}
	
	public Data getData()
	{
		return dataLayer;
	}
	
	public Server getHost()
	{
		return host;
	}
}
