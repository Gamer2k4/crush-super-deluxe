package main.logic;

import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Queue;

import main.data.Data;
import main.data.DataImpl;
import main.data.Event;
import main.data.entities.Team;
import main.presentation.game.ClientCursesGUI;
import main.presentation.game.GameGUI;
import main.presentation.game.PresentationMode;
import main.presentation.legacy.game.LegacyGraphicsGUI;

public class Client
{
	private Data dataLayer;
	private GameGUI presentationLayer;

	private Server host;

	boolean[] teamControl = new boolean[3]; // if controlsTeam[dataLayer.currentTeam], send commands to the server

	public static final String ACTION_GAME_START = "gameStart";
	public static String ACTION_GAME_END = "gameEnd";

	public Client(Server toConnectTo, ActionListener gameEndListener, PresentationMode guiMode)
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

		switch (guiMode)
		{
		case CURSES:
			presentationLayer = new ClientCursesGUI(this, gameEndListener);
			break;
		case LEGACY:
			presentationLayer = new LegacyGraphicsGUI(this, gameEndListener);
			break;
		}
	}

	public void newGame(List<Team> teams, Integer fieldnum)
	{
		dataLayer.newGame(teams, fieldnum);
	}

	public void processEvents(Queue<Event> eventQueue)
	{
		for (Event e : eventQueue)
		{
			// make sure the data is updated first, so when the GUI gets the event, the database will already reflect the correct values
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
	
	public GameGUI getGui()
	{
		return presentationLayer;
	}
}
