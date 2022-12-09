package main.logic;

import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import main.data.Data;
import main.data.DataImpl;
import main.data.Event;
import main.data.entities.Pace;
import main.data.entities.Team;
import main.presentation.common.Logger;
import main.presentation.game.GameGUI;
import main.presentation.game.GdxGUI;

public class Client
{
	private Data dataLayer;
	private GameGUI presentationLayer;

	private Server host;
	
	private Queue<Event> queuedEvents = new LinkedList<Event>();

	boolean[] teamControl = new boolean[3]; // if controlsTeam[dataLayer.currentTeam], send commands to the server

	public static final String ACTION_GAME_START = "BEGIN_GAME";
	public static String ACTION_GAME_END = "END_GAME";

	public Client(Server toConnectTo, ActionListener gameEndListener)
	{
		dataLayer = new DataImpl("client");

		host = toConnectTo;

		String ipAddr = "";
		String hostname = "";

		// take this out eventually
		try
		{
			InetAddress addr = InetAddress.getLocalHost();

			// Get IP Address
			ipAddr = addr.getHostAddress();

			// Get hostname
			hostname = addr.getHostName();
		} catch (UnknownHostException e)
		{
			//
		}

		host.addClient(this, ipAddr);

		for (int i = 0; i < 3; i++)
			teamControl[i] = true; // for now, in single player mode, this client controls all three teams

		presentationLayer = new GdxGUI(this, host, gameEndListener);
	}

	public void newGame(List<Team> teams, Integer fieldnum, Pace pace, int turns)
	{
		dataLayer.newGame(teams, fieldnum, pace, turns);
	}

	public void queueEventsForProcessing(Queue<Event> eventQueue)
	{
		try {
			queuedEvents.addAll(eventQueue);
			Logger.debug("\t\tClient received new events; total event queue size is " + queuedEvents.size());
		} catch (IndexOutOfBoundsException e)
		{
			Logger.warn("Client - Index out of bounds exception when adding events to queue.");
			System.out.println("Queued events size: " + queuedEvents.size());
			System.out.println("New event queue size: " + eventQueue.size());
			queuedEvents.clear();
			queuedEvents.addAll(eventQueue);
			System.out.println("Event queue cleared and new events added; new size: " + queuedEvents.size());
		}
	}
	
	public Event getNextEvent()
	{
		return queuedEvents.peek();
	}
	
	public void processNextEvent()
	{
		Event event = queuedEvents.poll();
		dataLayer.processEvent(event);
		Logger.debug("\t\tClient processed event; total event queue size is " + queuedEvents.size());
	}
		
	public void processEvents(Queue<Event> eventQueue)
	{
		for (Event e : eventQueue)
		{
			// give the presentation layer the event first, so it can animate the actions before they're finalized in data (note that this seems to screw up turn ends)
			dataLayer.processEvent(e);
			presentationLayer.receiveEvent(e);
			
			while (presentationLayer.isProcessingEvent())
			{
				System.out.println("\tdata is waiting for GUI to process event");		//TODO: the game fails without this line; i think it delays it just enough, so look into how i manage the GUI before the data is ready
			}	//empty look so flow is stopped until the presentation layer is finished handling the event
			
//			dataLayer.processEvent(e);
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
	
	public GameGUI getGui()
	{
		return presentationLayer;
	}
}
