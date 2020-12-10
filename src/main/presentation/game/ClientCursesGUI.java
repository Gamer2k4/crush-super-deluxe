package main.presentation.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import main.data.Event;
import main.data.entities.Arena;
import main.data.entities.Player;
import main.data.entities.Stats;
import main.logic.Client;
import main.presentation.common.AbstractScreenPanel;
import main.presentation.common.Logger;
import main.presentation.curses.terminal.CursesTerminal;
import main.presentation.curses.terminal.CursesTerminalAsciiPanelImpl;
import main.presentation.legacy.framework.AbstractLegacyScreen;
import main.presentation.startupscreen.CursesGameInProgressPanel;

public class ClientCursesGUI extends GameRunnerGUI implements KeyListener, ActionListener
{
	private static final Color LIGHT_GREY = new Color(192, 192, 192);
	private static final Color DARK_GREY = new Color(64, 64, 64);
	private static final Color BROWN = new Color(128, 128, 0);
	private static final Color GREEN = new Color(0, 128, 0);
	private static final Color BRIGHT_GREEN = new Color(0, 255, 0);
	private static final Color BLUE = new Color(0, 0, 255);
	private static final Color DENIM = new Color(21, 96, 189);
	private static final Color AMBER = new Color(255, 191, 0);
	private static final Color RED = new Color(255, 0, 0);
	private static final Color DARK_RED = new Color(128, 0, 0);
	private static final Color PURPLE = new Color(128, 0, 128);
	private static final Color ORANGE = new Color(255, 200, 0);
	private static final Color WHITE = new Color(255, 255, 255);
	private static final Color BLACK = new Color(0, 0, 0);
	private static final Color GOLD = new Color(212, 175, 55);
	private static final Color CYAN = new Color(0, 255, 255);
	private static final Color YELLOW = new Color(255, 255, 0);
	
	//	private WSwingConsoleInterface terminal;
	private CursesTerminal terminal;

	private boolean showTopHalf;
	
	private Timer delayTimer;
	private boolean isSleeping = false;

	public ClientCursesGUI(Client theClient, ActionListener gameEndListener)
	{
		super(theClient, null, gameEndListener);

		showTopHalf = true;

//		terminal = new WSwingConsoleInterface("Crush! Super Deluxe", new Font("Crush Font", Font.BOLD, 8));
		terminal = new CursesTerminalAsciiPanelImpl("Crush! Super Deluxe", WindowConstants.DISPOSE_ON_CLOSE);	//TODO: this creates a new frame; no need to do that anymore
		terminal.addKeyListener(this);
		refreshInterface();
	}

	@Override
	public void keyPressed(KeyEvent arg0)
	{
		Player plyr = getData().getPlayer(curPlayerIndex);
		Point curLocation = getData().getLocationOfPlayer(plyr);

		int code = arg0.getKeyCode();

		if (code == KeyEvent.VK_PAGE_UP && !showTopHalf && highlights.isEmpty())
		{
			showTopHalf = true;
			updateMap();
		} else if (code == KeyEvent.VK_PAGE_DOWN && showTopHalf && highlights.isEmpty())
		{
			showTopHalf = false;
			updateMap();
		} else if (code == KeyEvent.VK_ENTER)
		{
			int player = curTeamIndex + 1;
			if (player == 3)
				player = 0;

			sendCommand(Event.updateTurnPlayer(player));
		} else if (code == KeyEvent.VK_1)
		{
			setActivePlayer(1);
		} else if (code == KeyEvent.VK_2)
		{
			setActivePlayer(2);
		} else if (code == KeyEvent.VK_3)
		{
			setActivePlayer(3);
		} else if (code == KeyEvent.VK_4)
		{
			setActivePlayer(4);
		} else if (code == KeyEvent.VK_5)
		{
			setActivePlayer(5);
		} else if (code == KeyEvent.VK_6)
		{
			setActivePlayer(6);
		} else if (code == KeyEvent.VK_7)
		{
			setActivePlayer(7);
		} else if (code == KeyEvent.VK_8)
		{
			setActivePlayer(8);
		} else if (code == KeyEvent.VK_9)
		{
			setActivePlayer(9);
		}

		// commands that require an active player below this point
		if (curPlayerIndex == -1)
			return;

		Action prevCommand = currentAction;

		if (code == KeyEvent.VK_M && currentAction != Action.ACTION_JUMP)
		{
			currentAction = Action.ACTION_MOVE;
			highlights.clear();
		} else if (code == KeyEvent.VK_C && canCurrentPlayerCheck() && currentAction != Action.ACTION_JUMP)
		{
			currentAction = Action.ACTION_CHECK;
			snapToTile(curLocation);
			setHighlightsForCheck();
		} else if (code == KeyEvent.VK_J && canCurrentPlayerJump() && currentAction != Action.ACTION_JUMP)
		{
			currentAction = Action.ACTION_JUMP;
			snapToTile(curLocation);
			setHighlightsForJump();
		} else if (code == KeyEvent.VK_H && canCurrentPlayerHandoff() && currentAction != Action.ACTION_JUMP)
		{
			currentAction = Action.ACTION_HANDOFF;
			snapToTile(curLocation);
			setHighlightsForHandoff();

		} else if (code == KeyEvent.VK_ESCAPE)
		{
			if (currentAction != Action.ACTION_MOVE)
			{
				highlights.clear();
				currentAction = Action.ACTION_MOVE;
			} else
			{
				// menu or something
			}
		} else if (code == KeyEvent.VK_NUMPAD1)
		{
			handleDirection(1, -1);
		} else if (code == KeyEvent.VK_NUMPAD2)
		{
			handleDirection(1, 0);
		} else if (code == KeyEvent.VK_NUMPAD3)
		{
			handleDirection(1, 1);
		} else if (code == KeyEvent.VK_NUMPAD4)
		{
			handleDirection(0, -1);
		} else if (code == KeyEvent.VK_NUMPAD5)
		{
			handleDirection(0, 0);
		} else if (code == KeyEvent.VK_NUMPAD6)
		{
			handleDirection(0, 1);
		} else if (code == KeyEvent.VK_NUMPAD7)
		{
			handleDirection(-1, -1);
		} else if (code == KeyEvent.VK_NUMPAD8)
		{
			handleDirection(-1, 0);
		} else if (code == KeyEvent.VK_NUMPAD9)
		{
			handleDirection(-1, 1);
		} else if (currentAction == Action.ACTION_JUMP) // user pressed something while the jump overlays were set
		{
			System.out.println("CLIENT - KEY PRESSED WITH JUMP OVERLAYS: The key's numerical value is " + code);

			Point target = jumpPossibilities.get(code);

			if (target != null) // there was a point corresponding to the key pressed
			{
				System.out.println("\tValid key pressed.");

				Event moveEvent = Event.move(curPlayerIndex, curLocation.x + target.x, curLocation.y + target.y, false, true, false);
				sendCommand(moveEvent);

				highlights.clear();
				currentAction = Action.ACTION_MOVE;
			}
		}

		if (currentAction != prevCommand) // time to display or clear some overlays
		{
			refreshInterface();
			updateMap();
		}
	}

	//TODO: the sleep kind of works, but the interface isn't actually refreshing until it's all done
	private synchronized void sleep(int milliseconds)
	{
		Logger.debug("Sleeping for " + milliseconds + " ms.");
		
		try
		{
			synchronized(terminal)
			{
				terminal.wait(milliseconds);
			}
		} catch (InterruptedException e)
		{
			//nothing to do here
		}
		
		System.out.println("Done sleeping!");
	}
	
	//TODO: Delay has the same problem
//	private void delay(int milliseconds)
//	{
//		try
//		{
//			Thread.sleep(milliseconds);
//		} catch (InterruptedException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	@Override
	public void receiveEvent(Event e)
	{
		System.out.println("Client event received: " + e);
		
		if (e.getType() == Event.EVENT_VICTORY)
		{
			JOptionPane.showMessageDialog(null, "Team " + e.flags[0] + " wins the game!", "Victory!", JOptionPane.INFORMATION_MESSAGE);

			closeGUI();

			System.out.print("Waiting for gameData to save teams...");

			while (!getData().isGameDone())
			{
				// loop until data is saved
			}

			System.out.println("Done!");

			gameEndListener.actionPerformed(new ActionEvent(this, 0, Client.ACTION_GAME_END));
		} else if (e.getType() == Event.EVENT_TURN)
		{
			highlights.clear();
			currentAction = Action.ACTION_MOVE;
			ballCarrierHasActedAfterReceivingBall = false;

			curTeamIndex = getData().getCurrentTeam();
		} else if (e.getType() == Event.EVENT_RECVR)
		{
			System.out.println("RECOVER: Team " + curTeamIndex + ", Player " + (e.flags[0] + 1 - (9 * curTeamIndex)));

			int startingIndex = curTeamIndex * 9;
			int zoomRow = 0;
			boolean initialPlayerFound = false;

			for (int i = startingIndex; i < startingIndex + 9; i++)
			{
				Player p = getData().getPlayer(i);

				if (p != null)
				{
					if (p.status == Player.STS_OKAY && !initialPlayerFound)
					{
						initialPlayerFound = true;
						curPlayerIndex = i;
						currentPlayer = p;
						zoomRow = getData().getLocationOfPlayer(p).x;
					}
				}
			}
			
			refreshInterface();
			snapToTile(new Point(zoomRow, 0));
			//sleep(1500);		//TODO: only sleep if the player is doing more than getting his AP back
		} else if (e.getType() == Event.EVENT_EJECT)
		{
			// first of all, alert the user
			Player plyr = getData().getPlayer(e.flags[0]);
			int duration = e.flags[7];
			int type = e.flags[2];

			String name = plyr.name;
			int team = getData().getTeamIndexOfPlayer(plyr);

			int messageType = JOptionPane.INFORMATION_MESSAGE;
			String title = "";
			String message = "";

			String[] attributes = { " Action Points (AP)", " Checking (CH)", " Strength (ST)", " Toughness (TG)", " Reflexes (RF)",
					" Jumping (JP)", " Hands (HD)", " Dodge (DA)" };

			if (type == Event.EJECT_BLOB)
			{
				title = "Mutation!";
				message = name + " on Team " + team + " has been mutated by a teleported accident.";
				messageType = JOptionPane.QUESTION_MESSAGE;
			} else if (type == Event.EJECT_REF)
			{
				List<String> ejectItems = plyr.getEquipListForDisplay();

				title = "Ejection!";
				message = name + " on Team " + team + " has been ejected for the following items:";
				messageType = JOptionPane.INFORMATION_MESSAGE;

				for (String item : ejectItems)
				{
					if (!item.equals("(none)"))
						message = message + "\n" + item;
				}
			} else if (type == Event.EJECT_TRIVIAL)
			{
				title = "Injury!";
				message = name + " on Team " + team + " has been injured.\n\n" + "Injury Status:\n" + "Trivial";
				messageType = JOptionPane.WARNING_MESSAGE;
			} else if (type == Event.EJECT_SERIOUS)
			{
				title = "Injury!";
				message = name + " on Team " + team + " has been injured.\n\n" + "Injury Status:\n" + "Serious Injury\n\n" + "Out for "
						+ duration + " games.\n" + "-" + e.flags[4] + attributes[e.flags[3]] + "\n" + "-" + e.flags[6]
						+ attributes[e.flags[5]];
				messageType = JOptionPane.WARNING_MESSAGE;
			} else if (type == Event.EJECT_DEATH)
			{
				title = "Fatality!";
				message = name + " on Team " + team + " has been killed.";
				messageType = JOptionPane.ERROR_MESSAGE;
			}

			System.out.println("GUI Ejection - type is " + type);

			JOptionPane.showMessageDialog(null, message, title, messageType);

			// refresh the interface to show dead statuses and such
			curPlayerIndex = getFirstEligiblePlayer(); // TODO does this not work?
														// Note that this is only necessary if the injury is on the current team

			// if there is no eligible player, skip to the next turn and return
			if (curPlayerIndex == -1)
			{
				int nextTeamIndex = curTeamIndex + 1;
				if (nextTeamIndex == 3)
					nextTeamIndex = 0;

				sendCommand(Event.updateTurnPlayer(nextTeamIndex));
				return;
			}

			plyr = getData().getPlayer(curPlayerIndex);
			Point pnt = getData().getLocationOfPlayer(plyr);
			setActivePlayer(curPlayerIndex);
			updateMap();
			refreshInterface();
			snapToTile(pnt);
		} else if (e.getType() == Event.EVENT_TELE)
		{
			handleTeleportEvent(e);
		}
		// checking is in here because if the player gets hurt, it'll be handled by an eject event anyway
		else if (e.getType() == Event.EVENT_MOVE || e.getType() == Event.EVENT_BIN || e.getType() == Event.EVENT_GETBALL
				|| e.getType() == Event.EVENT_CHECK || e.getType() == Event.EVENT_STS)
		{
			Player plyr = getData().getPlayer(e.flags[0]);
			Point pnt = getData().getLocationOfPlayer(plyr);

			if (plyr == getData().getBallCarrier() && e.getType() != Event.EVENT_GETBALL)
				ballCarrierHasActedAfterReceivingBall = true;

			System.out.println("CLIENT - RECEIVE EVENT: Event received of type " + e.getType());

			updateMap(); // make sure stun highlights are displayed
			refreshInterface();
			snapToTile(pnt);
		} else if (e.getType() == Event.EVENT_BALLMOVE)
		{
			refreshInterface();
			snapToTile(new Point(e.flags[2], e.flags[3]));
		}
	}

	@Override
	protected void snapToTile(Point location)
	{
		if (location.x > 18 && showTopHalf)
			showTopHalf = false;

		if (location.x < 11 && !showTopHalf)
			showTopHalf = true;

		updateMap();
	}

	@Override
	public void refreshInterface()
	{
		// server frame
		terminal.print(0, 0, "+------------------+", LIGHT_GREY);
		terminal.print(0, 20, "+------------------+", LIGHT_GREY);

		for (int i = 1; i < 20; i++)
		{
			terminal.print(0, i, "|", LIGHT_GREY);
			terminal.print(19, i, "|", LIGHT_GREY);
		}

		terminal.print(1, 1, "Host:", LIGHT_GREY);
		terminal.print(1, 4, "Clients:", LIGHT_GREY);

		List<String> connectedClients = myClient.getHost().getIpAddresses();

		for (int i = 0; i < connectedClients.size(); i++)
		{
			terminal.print(1, 5 + i, connectedClients.get(i), LIGHT_GREY);
		}

		// info frame
		for (int i = 1; i < 24; i++)
		{
			terminal.print(50, i, "|", LIGHT_GREY);
			terminal.print(79, i, "|", LIGHT_GREY);
		}

		Color[] commandHighlights = new Color[4];

		for (int i = 0; i < 4; i++)
		{
			commandHighlights[i] = LIGHT_GREY;

			if (i == currentAction.getIndex())
			{
				commandHighlights[i] = GREEN;
			}
		}

		terminal.print(50, 0, "+----------------------------+", LIGHT_GREY);
		terminal.print(50, 2, "+----------------------------+", LIGHT_GREY);
		terminal.print(50, 13, "+----------------------------+", LIGHT_GREY);
		terminal.print(50, 24, "+----------------------------+", LIGHT_GREY);
		terminal.print(51, 14, "[M] Move", commandHighlights[0]);
		terminal.print(51, 15, "[C] Check", commandHighlights[1]);
		terminal.print(51, 16, "[J] Jump", commandHighlights[2]);
		terminal.print(51, 17, "[H] Handoff", commandHighlights[3]);
		terminal.print(51, 18, "[PgUp] Show Top of Map", LIGHT_GREY);
		terminal.print(51, 19, "[PgDn] Show Bottom of Map", LIGHT_GREY);
		terminal.print(51, 20, "[1-9] Select Player", LIGHT_GREY);
		terminal.print(51, 21, "[Tab] Timeout", LIGHT_GREY);
		terminal.print(51, 22, "[ENTER] End Turn", LIGHT_GREY);
		terminal.print(51, 23, "[ESC] Menu", LIGHT_GREY);

		// team frame
		terminal.print(0, 21, "| |             |[  ][  ][  ][  ][  ][  ][  ]|", DARK_GREY);
		terminal.print(0, 22, "|  |[    ][    ]|[  ][  ][  ][  ][  ][  ][  ]|", DARK_GREY);
		terminal.print(0, 23, "[   ][   ][   ][   ][   ][   ][   ][   ][   ]|", DARK_GREY);
		terminal.print(0, 24, "|    |    |    |    |    |    |    |    |    |", DARK_GREY);
		terminal.print(18, 21, "CH", DARK_RED);
		terminal.print(22, 21, "ST", DARK_RED);
		terminal.print(26, 21, "TG", DARK_RED);
		terminal.print(30, 21, "RF", DARK_RED);
		terminal.print(34, 21, "JP", DARK_RED);
		terminal.print(38, 21, "HD", DARK_RED);
		terminal.print(42, 21, "DA", DARK_RED);
		terminal.print(46, 21, "TEAM", DENIM);
		terminal.print(6, 22, "AP", DARK_RED);

		int pNum = 1;
		for (int i = 2; i < 44; i += 5)
		{
			terminal.print(i, 23, String.valueOf(pNum++), DENIM);
		}

		String name = "";
		int selectedIndex = -1;

		int[] baseAtt = new int[8];
		int[] modAtt = new int[8];
		Color[] clrAtt = new Color[8];

		for (int i = 0; i < 8; i++)
		{
			baseAtt[i] = 0;
			modAtt[i] = 0;
			clrAtt[i] = DENIM;
		}

		int totAp = 0;
		int curAp = 0;
		List<String> equipNames = new ArrayList<String>();
		equipNames.add("(none)");
		equipNames.add("(none)");
		equipNames.add("(none)");
		equipNames.add("(none)");

		Stats playerStats = new Stats();

		if (curPlayerIndex > -1 && curPlayerIndex >= curTeamIndex * 9) // don't update this stuff if the current player isn't on the current team
		{
			selectedIndex = curPlayerIndex - (curTeamIndex * 9) + 1;

			Player p = getData().getPlayer(curPlayerIndex);

			name = p.name;
			curAp = p.currentAP;
			totAp = p.getAttributeWithModifiers(Player.ATT_AP);
			equipNames = p.getEquipListForDisplay();
			playerStats = getData().getStatsOfPlayer(p);

			for (int i = 1; i < 8; i++)
			{
				baseAtt[i] = p.getAttributeWithoutModifiers(i);
				modAtt[i] = p.getAttributeWithModifiers(i);

				if (baseAtt[i] < modAtt[i])
					clrAtt[i] = ORANGE;
				else if (baseAtt[i] > modAtt[i])
					clrAtt[i] = DARK_RED;
			}

			terminal.print(5 * (curPlayerIndex - 9 * curTeamIndex), 23, "[", WHITE);
			terminal.print(5 * (curPlayerIndex - 9 * curTeamIndex) + 4, 23, "]", WHITE);
			terminal.print(5 * (curPlayerIndex - 9 * curTeamIndex), 24, "|    |", WHITE);
		} else
		{
			selectedIndex = 0;
		}

		terminal.print(1, 21, String.valueOf(selectedIndex), WHITE);
		terminal.print(3, 21, name, WHITE);
		terminal.print(1, 22, String.valueOf(totAp), DENIM);
		terminal.print(12, 22, String.valueOf(curAp), BRIGHT_GREEN);

		for (int i = 1; i < 8; i++)
		{
			terminal.print(14 + (4 * i), 22, String.valueOf(modAtt[i]), clrAtt[i]);
		}

		refreshPlayerStatuses();

		// teams
		terminal.print(46, 22, " ###", RED);
		terminal.print(46, 23, " ###", BRIGHT_GREEN);
		terminal.print(46, 24, " ###", BLUE);

		terminal.print(46, 22 + curTeamIndex, ">", WHITE);

		// individual player info
		terminal.print(65, 1, "              ", WHITE);
		terminal.print(51, 1, padName(name), WHITE);
		terminal.print(51, 3, "EQUIPMENT:", DENIM);

		for (int i = 0; i < 4; i++)
		{
			terminal.print(52, 4 + i, "                           ", LIGHT_GREY);
			terminal.print(52, 4 + i, equipNames.get(i), LIGHT_GREY);
		}

		// TODO: show total rating as well

		int checksThrown = playerStats.getStat(Stats.STATS_CHECKS_THROWN);
		int checksLanded = playerStats.getStat(Stats.STATS_CHECKS_LANDED);
		String checkingAverage = "0.00";

		if (checksThrown > 0)
		{
			double average = checksLanded / (double) checksThrown;
			DecimalFormat myFormatter = new DecimalFormat("0.00");
			checkingAverage = myFormatter.format(average);
		}

		int rushingYards = playerStats.getStat(Stats.STATS_RUSHING_YARDS);
		int rushingAttempts = playerStats.getStat(Stats.STATS_RUSHING_ATTEMPTS);
		String rushingAverage = "00.0";

		if (rushingAttempts > 0)
		{
			double average = rushingYards / (double) rushingAttempts;
			DecimalFormat myFormatter = new DecimalFormat("00.0");
			rushingAverage = myFormatter.format(average);
		}

		int sacks = playerStats.getStat(Stats.STATS_SACKS_FOR);
		int injuries = playerStats.getStat(Stats.STATS_INJURIES_FOR);
		int kills = playerStats.getStat(Stats.STATS_KILLS_FOR);

		int padsAttempted = playerStats.getStat(Stats.STATS_PADS_ACTIVATED);

		terminal.print(66, 3, "RATING:", DENIM);
		terminal.print(74, 3, playerStats.getXP() + "  ", LIGHT_GREY);

		terminal.print(51, 9, "CHECKING:      RUSHING:", DENIM);
		terminal.print(52, 10, checksLanded + "/" + checksThrown + " (" + checkingAverage + ")    ", LIGHT_GREY);
		terminal.print(67, 10, rushingAttempts + "/" + rushingYards + " (" + rushingAverage + ") ", LIGHT_GREY);
		terminal.print(52, 11, sacks + "S, " + injuries + "I, " + kills + "K   ", LIGHT_GREY);
		terminal.print(67, 11, "Pads: " + padsAttempted, LIGHT_GREY);
		
		terminal.refresh();
		System.out.println("refeshed");
	}

	@Override
	protected void refreshPlayerStatuses()
	{
		int startingIndex = curTeamIndex * 9;

		for (int i = 0; i < 9; i++)
		{
			Player p = getData().getPlayer(startingIndex + i);

			if (p != null)
			{
				int status = p.status;
				String statusStrings[] = { "LATE", "DECK", "OKAY", "DOWN", "STUN", "BLOB", "HURT", "DEAD", "OUT " };

				String statusString = statusStrings[status];

				Color toColor = BRIGHT_GREEN;

				if (status == Player.STS_OKAY || status == Player.STS_DOWN)
					statusString = " " + String.valueOf(p.currentAP);
				else if (status == Player.STS_LATE)
					toColor = WHITE;
				else if (status == Player.STS_STUN)
					toColor = YELLOW;
				else if (status == Player.STS_HURT)
					toColor = ORANGE;
				else if (status == Player.STS_DEAD)
					toColor = DARK_RED;
				else if (status == Player.STS_BLOB)
					toColor = PURPLE;

				terminal.print((5 * i) + 1, 24, statusString, toColor);
			}
		}
	}

	private String padName(String name)
	{
		String paddedName = name;

		int padding = 28 - name.length();
		int halfPadding = padding / 2;

		for (int i = 1; i <= halfPadding; i++)
		{
			paddedName = " " + paddedName;
		}

		return paddedName;
	}

	private void updateMap()
	{
		for (int i = 0; i < 30; i++)
		{
			for (int j = 0; j < 30; j++)
			{
				updateTile(new Point(i, j));
			}
		}

		// display highlights if they exist
		for (int i = 0; i < highlights.size(); i++)
		{
			HighlightIcon hi = highlights.get(i);

			Color bg = BLACK;
			Color fg = WHITE;
			String icon = "?";

			if (hi.isJumpTarget())
			{
				bg = GREEN;
				fg = BLACK;
				icon = "" + ((char) (i + 65));
			} else
			// everything else only updates the background, so set the foreground here
			{
				// note that both checking and handoffs only go over standing players, so this is pretty easy

				Point playerCoords = new Point(hi.x, hi.y);

				PlayerIcon colors = getPlayerIconAtPoint(playerCoords);

				fg = colors.fg;
				icon = colors.icon;
			}

			if (hi.isHandoffTarget() || hi.isGoodCheckTarget())
			{
				bg = GREEN;
			} else if (hi.isEvenCheckTarget())
			{
				bg = YELLOW;
			} else if (hi.isBadCheckTarget())
			{
				bg = DARK_RED;
			}

			int rowAdjust = 0;
			int colAdjust = 20;

			if (!showTopHalf)
				rowAdjust = -9; // or whatever

			int row = hi.x + rowAdjust;
			int col = hi.y + colAdjust;

			// System.out.println("CLIENT - UPDATE MAP: Values are " + col + ", " + row + ", " + icon + ", " + fg + ", " + bg + ".");

			terminal.print(col, row, icon, fg, bg);
		}

		terminal.refresh();
	}

	private void updateTile(Point coords)
	{
		Arena localMap = getData().getArena();

		int rowAdjust = 0;
		int colAdjust = 20;

		if (!showTopHalf)
			rowAdjust = -9; // or whatever

		int row = coords.x + rowAdjust;
		int col = coords.y + colAdjust;

		if (row < 0 || row > 20)
			return;

		Player p = getData().getPlayerAtLocation(coords);

		Color fg = WHITE;
		Color bg = BLACK;
		String icon = "%"; // something that will never be used

		// no player here
		if (p == null)
		{
			int tile = localMap.getTile(coords.x, coords.y);

			if (coords.x == getData().getBallLocation().x && coords.y == getData().getBallLocation().y)
			{
//				icon = "•";
				icon = "" + (char)233;
				fg = DENIM;
			} else if (tile == 0)
			{
				fg = DARK_GREY;
				icon = "#";
			} else if (tile == 1)
			{
				fg = LIGHT_GREY;
				icon = ".";

				if ((coords.x < 5 && (coords.y < 5 || coords.y > 24)) || (coords.x > 24 && (coords.y < 5 || coords.y > 24)))
				{
					fg = BROWN;
					icon = "$";
				}
			} else if (tile == 2)
			{
				// change to be hosting team's colors
				// fg = RICH_CARMINE;
				fg = GOLD;
				icon = "$";
			} else if (tile == 3)
			{
				fg = CYAN;
				icon = "o";
			} else if (tile == 4)
			{
				fg = BROWN;
				icon = "+";
			} else if (tile == 5)
			{
				fg = DENIM;
				icon = "@";

				int binIndex = getData().getArena().getBinIndex(coords.x, coords.y);
				int binStatus = getData().getArena().getBinStatus(binIndex);

				if (binStatus == Arena.STATE_FAILED)
					fg = DARK_RED;
				if (binStatus == Arena.STATE_SUCCESS)
					fg = GREEN;
			} else if (tile == 6)
			{
				fg = YELLOW;
				icon = "*";
			}
			// etc.
		} else
		// player here
		{
			PlayerIcon colors = getPlayerIconAtPoint(coords);
			fg = colors.fg;
			bg = colors.bg;

			icon = colors.icon;
		}

		terminal.print(col, row, icon, fg, bg);
	}

	// set team color, status color, and player symbol
	private PlayerIcon getPlayerIconAtPoint(Point coords)
	{
		PlayerIcon toRet = null;

		Player p = getData().getPlayerAtLocation(coords);

		if (p != null)
		{
			String[] iconList = { "H", "G", "C", "D", "N", "S", "K", "X" };

			Color fg = WHITE;
			Color bg = BLACK;
			String icon = iconList[p.getRace().getIndex()];

			// set team color
			int team = getData().getTeamIndexOfPlayer(p);

			if (team == 0)
				fg = RED;
			if (team == 1)
				fg = BRIGHT_GREEN;
			if (team == 2)
				fg = BLUE;

			// current player should be indicated by white coloring regardless of team
			if (getData().getPlayer(curPlayerIndex) == p)
				fg = WHITE;

			// additional statuses
			int status = p.getStatus();
			if (status == Player.STS_DOWN || status == Player.STS_STUN)
			{
				icon = icon.toLowerCase();
			}
			if (status == Player.STS_STUN)
			{
				bg = AMBER;
			}
			if (getData().getBallCarrier() == p)
			{
				bg = DENIM;
			}

			toRet = new PlayerIcon(fg, bg, icon);
		}

		return toRet;
	}

	@Override
	public void keyReleased(KeyEvent arg0)
	{
		return;
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
		return;
	}

	// inner class for general icon data
	class PlayerIcon
	{
		public Color fg = WHITE;
		public Color bg = BLACK;
		public String icon = "%"; // something that will never be used

		public PlayerIcon(Color foreground, Color background, String theIcon)
		{
			fg = foreground;
			bg = background;
			icon = theIcon;
		}
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
		terminal.close();
	}

	@Override
	public AbstractLegacyScreen getDisplayScreen()
	{
		return new CursesGameInProgressPanel();
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		Logger.debug("Action received: " + event);
		
		refreshInterface();
	}
}
