package main.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import main.data.Data;
import main.data.Event;
import main.data.entities.Team;
import main.presentation.GUI;
import main.presentation.ServerJFrameGUI;

public class Server
{
	private Data dataLayer;
	private Engine logicLayer;
	private GUI presentationLayer;
	
	private List<Client> connectedClients;
	
	Map<Client, String> IPs;	//this is probably backwards
	
	public Server()
	{
		dataLayer = new Data();
		logicLayer = new Engine(dataLayer);
		presentationLayer = new ServerJFrameGUI(this);
		
		connectedClients = new ArrayList<Client>();
		IPs = new HashMap<Client, String>();
	}
	
	public void newGame(List<Team> teams, int fieldnum)
	{
		dataLayer.newGame(teams, fieldnum);
		
		for (Client c : connectedClients)
		{
			c.newGame(teams, fieldnum);
		}
		
		receiveCommand(Event.updateTurnPlayer(0));
	}
	
	//MAJORLY flesh this out once I get to network stuff
	public void addClient(Client newClient, String IP)
	{
		connectedClients.add(newClient);
		IPs.put(newClient, IP);
	}
	
	public List<String> getIpAddresses()
	{
		List<String> addresses = new ArrayList<String>();
		
		for (Client c : connectedClients)
		{
			addresses.add(IPs.get(c));
		}
		
		return addresses;
	}
	
	public void receiveCommand(Event theCommand)
	{
		//note that the events are completely sanitized by the time they come out of the engine; players won't be running into walls, dead players won't act, etc.
		Queue<Event> eventQueue = logicLayer.generateEvents(theCommand);
		propogateEvents(eventQueue);
	}
	
	private void propogateEvents(Queue<Event> eventQueue)
	{
		//update own database first, then update the clients
		for (Event e : eventQueue)
		{
			dataLayer.processEvent(e);
			presentationLayer.receiveEvent(e);
		}

		for (Client c : connectedClients)
		{
			c.processEvents(eventQueue);
		}
	}
	
	public Data getData()
	{
		return dataLayer;
	}
	
	
	
	
	/* REAL SERVER CODE (USE LATER)
	
	private List<Socket> clients;
	private ServerSocket myService;
	private EngineOld myEngine;
	
	private Map<Integer, Socket> clientMappings; //user index, Socket
	
	private String createDataStream(Queue<Event> events)
	{
		return null;
	}
	
	 */
}