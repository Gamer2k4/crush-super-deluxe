package main.presentation.game;

import java.awt.GridLayout;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import main.data.Data;
import main.data.Event;
import main.data.entities.Player;
import main.logic.Server;

public class ServerJFrameGUI extends GameGUI
{
	private JFrame mainWindow;
	private JPanel mainPane;
	private JLabel currentTeam;

	private JTextArea eventsText;
	private JScrollPane eventsPane;

	private JTextArea messagesText;
	private JScrollPane messagesPane;

	public ServerJFrameGUI(Server theServer)
	{
		super(null, theServer);

		mainWindow = new JFrame("Crush! Server Window");
		mainWindow.setSize(600, 400);
		mainWindow.setFocusableWindowState(false);
		mainWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

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
	public void refreshInterface()
	{
		Data myDataImpl = myServer.getData();

		int curTeam = myDataImpl.getCurrentTeam();
		currentTeam.setText("Current Team: " + curTeam);

		mainWindow.setVisible(true);
	}

	private void generateMessages(Event theEvent)
	{
		Data myDataImpl = myServer.getData();

		Player player = null;
		String newLine = "";

		switch (theEvent.getType())
		{
		case Event.EVENT_TURN:
			messagesText.setText("It is Team " + theEvent.flags[0] + "'s turn.");
			return;
		case Event.EVENT_MOVE:
			player = myDataImpl.getPlayer(theEvent.flags[0]);
			int x = theEvent.flags[2];
			int y = theEvent.flags[3];
			String verb = "moved";

			if (theEvent.flags[5] == 1)
			{
				verb = "jumped";
			}

			newLine = player.name + " " + verb + " to (" + x + ", " + y + ")";

			if (theEvent.flags[4] == 1)
				newLine = newLine + " against his will";

			if (theEvent.flags[6] == 1)
				newLine = newLine + " and fell down";

			newLine = newLine + ".";

			break;
		case Event.EVENT_TELE:
			player = myDataImpl.getPlayer(theEvent.flags[0]);
			int portal1 = theEvent.flags[2];
			int portal2 = theEvent.flags[3];

			String origin = "the bench";

			if (portal1 != -1)
			{
				origin = "Portal " + portal1;
			}

			newLine = player.name + " teleported from " + origin + " to Portal " + portal2 + ".";

			break;
		case Event.EVENT_STS:
			player = myDataImpl.getPlayer(theEvent.flags[0]);

			String[] status = { "running late", "on deck", "okay", "down", "stunned", "blobbed", "injured", "dead", "ejected" };

			newLine = player.name + " is now " + status[theEvent.flags[2]] + ".";

			break;
		case Event.EVENT_RECVR:
			player = myDataImpl.getPlayer(theEvent.flags[0]);

			newLine = player.name + " is recovering.";

			break;
		case Event.EVENT_BIN:
			player = myDataImpl.getPlayer(theEvent.flags[0]);

			String result = ", but the ball wasn't there.";

			if (theEvent.flags[3] == 1)
			{
				result = ", and found the ball!";
			}

			newLine = player.name + " tried Bin " + theEvent.flags[2] + result;

			break;
		case Event.EVENT_BALLMOVE:
			x = theEvent.flags[2];
			y = theEvent.flags[3];

			newLine = "The ball moved to (" + x + ", " + y + ").";

			break;
		case Event.EVENT_GETBALL:
			player = myDataImpl.getPlayer(theEvent.flags[0]);

			result = ", but it got away from him.";

			if (theEvent.flags[2] == 1)
			{
				result = ", and succeeded.";
			}

			newLine = player.name + " tried to pick up the ball" + result;

			break;
		case Event.EVENT_HANDOFF:
			player = myDataImpl.getPlayer(theEvent.flags[0]);
			Player player2 = myDataImpl.getPlayer(theEvent.flags[1]);

			result = " handed the ball to " + player2.name + ".";

			if (theEvent.flags[2] == 1)
			{
				result = " hurled the ball into the air!";
			} else if (theEvent.flags[2] == -1)
			{
				result = " tried to give the ball to " + player2.name + ", who dropped it.";
			}

			newLine = player.name + result;

			break;
		case Event.EVENT_VICTORY:
			messagesText.setText("Team " + theEvent.flags[0] + " wins the game!");
			return;
		case Event.EVENT_CHECK:
			player = myDataImpl.getPlayer(theEvent.flags[0]);
			player2 = myDataImpl.getPlayer(theEvent.flags[1]);
			
			String reaction = "";
			result = "";

			if (theEvent.flags[3] == 1)
			{
				reaction = " reflex";
			}

			if (theEvent.flags[2] == Event.CHECK_CRITFAIL)
			{
				result = ", but was knocked down.";
			}
			else if (theEvent.flags[2] == Event.CHECK_DODGE)
			{
				result = ", who dodged the attack.";
			}
			else if (theEvent.flags[2] == Event.CHECK_FAIL)
			{
				result = ", without any success.";
			}
			else if (theEvent.flags[2] == Event.CHECK_DOUBLEFALL)
			{
				result = " and knocked him down, but fell in the process.";
			}
			else if (theEvent.flags[2] == Event.CHECK_PUSH)
			{
				result = ", pushing him backward.";
			}
			else if (theEvent.flags[2] == Event.CHECK_FALL)
			{
				result = ", knocking him down.";
			}
			else if (theEvent.flags[2] == Event.CHECK_PUSHFALL)
			{
				result = " violently, sending him flying backward in a heap.";
			}

			newLine = player.name + reaction + " checked " + player2.name + result;

			break;
		case Event.EVENT_EJECT:
			String[] abilities = {"AP", "CH", "ST", "TG", "RF", "JP", "HD", "DA"};
			
			player = myDataImpl.getPlayer(theEvent.flags[0]);
			result = " because the ref noticed his illegal equipment.";
			String weeksOut = "next week";
			String damage = ".";

			if (theEvent.flags[2] == 1)
			{
				result = " due to being blobbed.";
			} else if (theEvent.flags[2] == 2)
			{
				result = " with a trivial injury.";

			} else if (theEvent.flags[2] == 4)
			{
				result = " in a bag.  RIP.";
			} else if (theEvent.flags[2] == 3)
			{
				if (theEvent.flags[4] == 0 && theEvent.flags[6] == 0)
					result = " with a minor injury.";
				else 
					result = " in critical condition.";
			}

			if (theEvent.flags[7] == 1)
			{
				weeksOut = "in one week";
			} else if (theEvent.flags[7] > 1)
			{
				weeksOut = "in " + theEvent.flags[7] + " weeks";
			}
			
			if (theEvent.flags[4] > 0 || theEvent.flags[6] > 0)
			{
				damage = ", but suffered " + theEvent.flags[4] + " " + abilities[theEvent.flags[3]] + " damage and " + theEvent.flags[6] + " " + abilities[theEvent.flags[5]] + " damage.";
			}

			newLine = player.name + " had to leave the game" + result;
			
			if (theEvent.flags[2] != 4)
				newLine = newLine + "  He'll be ready to play again " + weeksOut + damage;

			break;
		}

		messagesText.setText(messagesText.getText() + "\n" + newLine);
	}

	@Override
	public void beginGame()
	{
		// do nothing
	}

	@Override
	public void endGame()
	{
		// do nothing
	}

	@Override
	public void closeGUI()
	{
		mainWindow.dispatchEvent(new WindowEvent(mainWindow, WindowEvent.WINDOW_CLOSING));
	}
}
