package main.presentation;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import main.data.Data;
import main.data.Event;
import main.logic.Server;

public class ServerJFrameGUI extends GUI
{
	private JFrame mainWindow;
	private JPanel mainPane;
	private JLabel currentTeam;
	
	private JTextArea eventsText;
	private JScrollPane eventsPane;
	
	private JTextArea messagesText;
	private JScrollPane messagesPane;
	
	public ServerJFrameGUI(Server theServer) {
		super(null, theServer);
		
		mainWindow = new JFrame("Crush! Server Window");
		mainWindow.setSize(600, 400);
		mainWindow.setFocusableWindowState(false);
		
		mainPane = new JPanel();
		mainPane.setLayout(new GridLayout(3, 1));
		mainWindow.add(mainPane);
		
		messagesText = new JTextArea(5, 30);
		messagesPane = new JScrollPane(messagesText);
		mainPane.add(messagesPane);
		
		currentTeam = new JLabel("Current Team: unknown");
		mainPane.add(currentTeam);
		
		eventsText = new JTextArea(5, 30);
		eventsPane = new JScrollPane(eventsText);
		mainPane.add(eventsPane);
		
		refreshInterface();
	}

	@Override
	public void receiveEvent(Event theEvent)
	{
		eventsText.setText(eventsText.getText() + theEvent.toString() + "\n");
		generateMessages(theEvent);
		refreshInterface();
	}

	@Override
	protected void refreshInterface()
	{
		Data myData = myServer.getData(); 
		
		int curTeam = myData.getCurrentTeam();
		currentTeam.setText("Current Team: " + curTeam);
		
		mainWindow.setVisible(true);
	}
	
	private void generateMessages(Event theEvent)
	{
		if (theEvent.getType() == Event.EVENT_TURN)
		{
			messagesText.setText("It is Team " + theEvent.flags[0] + "'s turn.");
		}
	}
}
