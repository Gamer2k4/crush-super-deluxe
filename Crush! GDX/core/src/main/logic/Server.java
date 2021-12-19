package main.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import main.data.Data;
import main.data.DataImpl;
import main.data.Event;
import main.data.entities.Team;

public class Server
{
	private Data dataLayer;
	private Engine logicLayer;
//	private GameGUI presentationLayer;
	
	private List<Client> connectedClients;
	
	Map<Client, String> IPs;	//this is probably backwards
	
	public Server()
	{
		dataLayer = new DataImpl("server");
		logicLayer = new LegacyEngineImpl(dataLayer);
		
		connectedClients = new ArrayList<Client>();
		IPs = new HashMap<Client, String>();
	}
	
	public void newGame(List<Team> teams)
	{
		newGame(teams, null);
	}
	
	public void newGame(List<Team> teams, Integer fieldnum)
	{
//		presentationLayer = new ServerJFrameGUI(this);		//TODO: put this someplace else
		
		dataLayer.newGame(teams, fieldnum);
		
		for (Client c : connectedClients)
		{
			c.newGame(teams, fieldnum);
		}
		
		receiveCommand(Event.updateTurnPlayer(0));
	}
	
	public void endGame()
	{
//		presentationLayer.closeGUI();
	}
	
	//TODO: MAJORLY flesh this out once I get to network stuff
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
//			presentationLayer.receiveEvent(e);
		}

		for (Client c : connectedClients)
		{
			c.queueEventsForProcessing(eventQueue);
		}
	}
	
	public Data getData()
	{
		return dataLayer;
	}
	
	public Engine getEngine()
	{
		return logicLayer;
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
