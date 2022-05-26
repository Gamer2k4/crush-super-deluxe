package main.logic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.swing.Timer;

import main.data.Data;
import main.data.DataImpl;
import main.data.Event;
import main.data.entities.Team;
import main.logic.AI.AI;
import main.logic.AI.BasicAI;

//TODO: the AI timer is probably in the wrong place, but it should be done on the server, and it doesn't really quite fit into the
//		engine (whose only job is to act on received events)
public class Server implements ActionListener
{
	private Data dataLayer;
	private Engine logicLayer;
	
	private List<Client> connectedClients;
	
	Map<Client, String> IPs;	//this is probably backwards
	
	private AI ai;
	private Timer aiTimer;
	
	public Server()
	{
		dataLayer = new DataImpl("server");
		logicLayer = new LegacyEngineImpl(dataLayer);
		
		connectedClients = new ArrayList<Client>();
		IPs = new HashMap<Client, String>();
		
		ai = new BasicAI(dataLayer);
		aiTimer = new Timer(250, this);
		aiTimer.start();
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
	
	
	//AI timer firing
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (!dataLayer.isGameActive())
			return;
		
		if (dataLayer.isCurrentTeamHumanControlled())
			return;
		
		Event event = ai.generateEvent();
		System.out.println("AI has generated an event: " + event);
		
		if (event != null)
			receiveCommand(event);
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
