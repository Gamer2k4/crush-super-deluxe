package main.presentation;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import net.slashie.libjcsi.CSIColor;
import net.slashie.libjcsi.wswing.WSwingConsoleInterface;

import main.data.Event;
import main.data.entities.Field;
import main.data.entities.Player;
import main.logic.Client;

public class ClientCursesGUI extends GUI implements KeyListener
{
	private WSwingConsoleInterface csi;
	
	int curPlayerIndex;
	int curTeamIndex;
	int activeCommand;
	boolean showTopHalf;
	
	Player currentPlayer;	//note that this is only in the mind of GUI; the data layer doesn't care which player is selected
	
	List<HighlightIcon> highlights;
	
	public ClientCursesGUI(Client theClient)
	{
		super(theClient, null);
		
		activeCommand = 0;
		curPlayerIndex = -1;
		curTeamIndex = 0;
		showTopHalf = true;
		
		currentPlayer = null;
		
		highlights = new ArrayList<HighlightIcon>();
				
		csi = new WSwingConsoleInterface("Crush! Deluxe", new Font("Crush Font", Font.BOLD, 8));
		csi.setAutoRefresh(false);
		csi.addKeyListener(this);
		refreshInterface();
	}


	public void keyPressed(KeyEvent arg0)
	{
		int code = arg0.getKeyCode();
		
		if (code == KeyEvent.VK_PAGE_UP && !showTopHalf)
		{
			showTopHalf = true;
			updateMap();
		}
		else if (code == KeyEvent.VK_PAGE_DOWN && showTopHalf)
		{
			showTopHalf = false;
			updateMap();
		}
		else if (code == KeyEvent.VK_ENTER)
		{
			int player = curTeamIndex + 1;
			if (player == 3)
				player = 0;
			
			sendCommand(Event.updateTurnPlayer(player));
		}
		else if (code == KeyEvent.VK_1)
		{
			setActivePlayer(1);
		}
		else if (code == KeyEvent.VK_2)
		{
			setActivePlayer(2);
		}
		else if (code == KeyEvent.VK_3)
		{
			setActivePlayer(3);
		}
		else if (code == KeyEvent.VK_4)
		{
			setActivePlayer(4);
		}
		else if (code == KeyEvent.VK_5)
		{
			setActivePlayer(5);
		}
		else if (code == KeyEvent.VK_6)
		{
			setActivePlayer(6);
		}
		else if (code == KeyEvent.VK_7)
		{
			setActivePlayer(7);
		}
		else if (code == KeyEvent.VK_8)
		{
			setActivePlayer(8);
		}
		else if (code == KeyEvent.VK_9)
		{
			setActivePlayer(9);
		}
		
		//commands that require an active player below this point
		if (curPlayerIndex == -1)
			return;
		
		int prevCommand = activeCommand;
		
		if (code == KeyEvent.VK_M)
		{
			activeCommand = 0;
			highlights.clear();
		}
		else if (code == KeyEvent.VK_C && canCurrentPlayerCheck())
		{
			activeCommand = 1;
			setHighlightsForCheck();
		}		
		else if (code == KeyEvent.VK_J && canCurrentPlayerJump())
		{
			System.out.println("PLAYER IS JUMPING");
			
			activeCommand = 2;
			setHighlightsForJump();
		}
		else if (code == KeyEvent.VK_H && canCurrentPlayerHandoff())
		{
			activeCommand = 3;
			setHighlightsForHandoff();
			
		}
		else if (code == KeyEvent.VK_ESCAPE)
		{
			if (activeCommand != 0)
			{
				highlights.clear();
				activeCommand = 0;
			}
			else
			{
				//menu or something
			}
		}
		else if (code == KeyEvent.VK_NUMPAD1)
		{
			handleDirection(1, -1);
		}
		else if (code == KeyEvent.VK_NUMPAD2)
		{
			handleDirection(1, 0);
		}
		else if (code == KeyEvent.VK_NUMPAD3)
		{
			handleDirection(1, 1);
		}
		else if (code == KeyEvent.VK_NUMPAD4)
		{
			handleDirection(0, -1);
		}
		else if (code == KeyEvent.VK_NUMPAD5)
		{
			handleDirection(0, 0);
		}
		else if (code == KeyEvent.VK_NUMPAD6)
		{
			handleDirection(0, 1);
		}
		else if (code == KeyEvent.VK_NUMPAD7)
		{
			handleDirection(-1, -1);
		}
		else if (code == KeyEvent.VK_NUMPAD8)
		{
			handleDirection(-1, 0);
		}
		else if (code == KeyEvent.VK_NUMPAD9)
		{
			handleDirection(-1, 1);
		}
		
		if (activeCommand != prevCommand)	//time to display or clear some overlays
		{
			refreshInterface();
			updateMap();
		}
	}

	private void setHighlightsForHandoff()
	{
		highlights.clear();
		
		Player plyr = myClient.getData().getPlayer(curPlayerIndex);
		Point curLocation = myClient.getData().getLocationOfPlayer(plyr);
		
		int centerX = curLocation.x;
		int centerY = curLocation.y;
		
		for (int i = -1; i <= 1; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				Player target = myClient.getData().getPlayerAtLocation(centerX + i, centerY + j);
				
				if (target != null)
				{
					if (myClient.getData().getTeamOfPlayer(target) == myClient.getData().getTeamOfPlayer(plyr) && target.getStatus() == Player.STS_OKAY)
					{
						highlights.add(new HighlightIcon(centerX + i, centerY + j, HI_HANDOFF));
					}
				}
			}
		}
		
		//switch back to moving if there are no valid targets
		if (highlights.size() == 0)
			activeCommand = 0;
	}

	private void setHighlightsForJump()
	{
		highlights.clear();
		
		boolean canJump[][] = new boolean[5][5];	//might make this global, then clear it after each jump
		
		Player plyr = myClient.getData().getPlayer(curPlayerIndex);
		Point curLocation = myClient.getData().getLocationOfPlayer(plyr);
		
		int centerX = curLocation.x;
		int centerY = curLocation.y;
		
		//make every tile initially jumpable to (unless it's obstructed)
		for (int i = 0; i < 5; i++)
		{
			for (int j = 0; j < 5; j++)
			{
				canJump[i][j] = !myClient.getData().getArena().isObstructed(centerX + i - 2, centerY + j - 2);
				
				//if there's a player there, obviously you can't jump there
				if (myClient.getData().getPlayerAtLocation(centerX + i - 2, centerY + j - 2) != null)
					canJump[i][j] = false;
			}
		}
		
		for (int i = -1; i <= 1; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				canJump[i + 2][j + 2] = false; //can't jump immediately adjacent to the player
				
				if (i == 0 && j == 0) continue;		//calculations don't work at (0, 0)
				
				boolean playerInWay = false;
				Player playerToJump = myClient.getData().getPlayerAtLocation(centerX + i, centerY + j);
				
				//there's a player in the way
				if (playerToJump != null)
				{
					int playerStatus = playerToJump.getStatus();
					
					//if the player in the way is down, stunned, or if the jumper is a curmian (which can jump standing players), there's nothing in the way
					if (playerStatus == Player.STS_DOWN || playerStatus == Player.STS_STUN || plyr.getRace() == Player.RACE_CURMIAN)
					{
						playerInWay = false;
					}
					else
					{
						playerInWay = true;
					}
				}
				
				if (!myClient.getData().getArena().isObstructed(centerX + i, centerY + j) &&  !playerInWay) continue;	//nothing is blocking
				
				if (Math.abs(i) == Math.abs(j))	//on a diagonal
				{
					canJump[(i * 2) + 2][j + 2] = false;
					canJump[(i * 2) + 2][(j * 2) + 2] = false;
					canJump[i + 2][(j * 2) + 2] = false;
				}
				else if (i == 0)				//horizontal
				{
					canJump[1][(j * 2) + 2] = false;
					canJump[2][(j * 2) + 2] = false;
					canJump[3][(j * 2) + 2] = false;
				}
				else if (j == 0)				//vertical
				{
					canJump[(i * 2) + 2][1] = false;
					canJump[(i * 2) + 2][2] = false;
					canJump[(i * 2) + 2][3] = false;
				}
			}
		}
		
		for (int i = 0; i < 5; i++)
		{
			for (int j = 0; j < 5; j++)
			{
				if (canJump[i][j])
				{
					highlights.add(new HighlightIcon(centerX - 2 + i, centerY - 2 + j, HI_JUMP));
				}
			}
		}
	}

	private void setHighlightsForCheck()
	{
		highlights.clear();
		
		Player plyr = myClient.getData().getPlayer(curPlayerIndex);
		Point curLocation = myClient.getData().getLocationOfPlayer(plyr);
		
		int centerX = curLocation.x;
		int centerY = curLocation.y;
		
		for (int i = -1; i <= 1; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				Player target = myClient.getData().getPlayerAtLocation(centerX + i, centerY + j);
				
				if (target != null)
				{
					if (myClient.getData().getTeamOfPlayer(target) != myClient.getData().getTeamOfPlayer(plyr) && target.getStatus() == Player.STS_OKAY)
					{
						int checkOdds = HI_CHECK_EVEN;
						
						int dif = plyr.getAttributeWithModifiers(Player.ATT_CH) - target.getAttributeWithModifiers(Player.ATT_CH);
						
						if (dif >= 20)
							checkOdds = HI_CHECK_GOOD;
						
						if (dif <= -20)
							checkOdds = HI_CHECK_BAD;
						
						highlights.add(new HighlightIcon(centerX + i, centerY + j, checkOdds));
					}
				}
			}
		}
		
		//no targets are valid if the player doesn't have enough AP
		if (plyr.currentAP < 10 || (plyr.currentAP < 20 && !plyr.hasSkill[Player.SKILL_CHARGE]))
			highlights.clear();
		
		//switch back to moving if there are no valid targets
		if (highlights.size() == 0)
			activeCommand = 0;
	}


	public void receiveEvent(Event e)
	{
		if (e.getType() == Event.EVENT_VICTORY)
		{
			JOptionPane.showMessageDialog(null, "Team " + e.flags[0] + " wins the game!", "Victory!", JOptionPane.INFORMATION_MESSAGE);
			
			// TODO Replace this with returning to CrushRunner.
			csi.removeKeyListener(this);
			csi.close();
			
			//remove this eventually
			System.exit(0);
		}		
		else if (e.getType() == Event.EVENT_TURN)
		{
			highlights.clear();
			activeCommand = 0;
			
			curTeamIndex = myClient.getData().getCurrentTeam();
		}
		else if (e.getType() == Event.EVENT_RECVR)
		{
			System.out.println("RECOVER: Team " + curTeamIndex + ", Player " + (e.flags[0] + 1 - (9 * curTeamIndex)));
			
			int startingIndex = curTeamIndex * 9;
			int zoomRow = 0;
			boolean initialPlayerFound = false;
			
			for (int i = startingIndex; i < startingIndex + 9; i++)
			{
				Player p = myClient.getData().getPlayer(i);
				
				if (p != null)
				{
					if (p.status == Player.STS_OKAY && !initialPlayerFound)
					{
						initialPlayerFound = true;
						curPlayerIndex = i;
						currentPlayer = p;
						zoomRow = myClient.getData().getLocationOfPlayer(p).x;
					}
				}
			}
			
			refreshInterface();
			zoomToLocation(zoomRow);
		}
		else if (e.getType() == Event.EVENT_EJECT)
		{
			//for now, just refresh the interface to show dead statuses and such
			curPlayerIndex = getFirstEligiblePlayer();
			refreshInterface();
		}
		else if (e.getType() == Event.EVENT_TELE)
		{
			System.out.println("Display - Teleporting from " + e.flags[2] + " to " + e.flags[3]);
			
			Point pnt = myClient.getData().getArena().getPortal(e.flags[3]);
			
			if (e.flags[2] == -1)
			{
				curPlayerIndex = e.flags[0];
				currentPlayer = myClient.getData().getPlayer(curPlayerIndex);
			}
			else if (e.flags[2] == e.flags[3])
			{
				curPlayerIndex = getFirstEligiblePlayer();
			}
			
			refreshInterface();
			zoomToLocation(pnt.x);
		}
		//checking is in here because if the player gets hurt, it'll be handled by an eject event anyway
		else if (e.getType() == Event.EVENT_MOVE || e.getType() == Event.EVENT_BIN || e.getType() == Event.EVENT_GETBALL || e.getType() == Event.EVENT_CHECK)
		{
			Player plyr = myClient.getData().getPlayer(e.flags[0]);
			Point pnt = myClient.getData().getLocationOfPlayer(plyr);
			
			refreshInterface();
			zoomToLocation(pnt.x);
		}
		else if (e.getType() == Event.EVENT_BALLMOVE)
		{
			refreshInterface();
			zoomToLocation(e.flags[2]);
		}
	}
	
	private void zoomToLocation(int row)
	{
		if (row > 19 && showTopHalf)
			showTopHalf = false;
		
		if (row < 9 && !showTopHalf)
			showTopHalf = true;
		
		updateMap();
	}
	
	protected void refreshInterface()
	{
		//server frame
		csi.print(0, 0, "+------------------+", CSIColor.LIGHT_GRAY);
		csi.print(0, 20, "+------------------+", CSIColor.LIGHT_GRAY);
		
		for (int i = 1; i < 20; i++)
		{
			csi.print(0, i, "|", CSIColor.LIGHT_GRAY);
			csi.print(19, i, "|", CSIColor.LIGHT_GRAY);
		}
		
		csi.print(1, 1, "Host:", CSIColor.LIGHT_GRAY);
		csi.print(1, 4, "Clients:", CSIColor.LIGHT_GRAY);
	
		List<String> connectedClients = myClient.getHost().getIpAddresses();
		
		for (int i = 0; i < connectedClients.size(); i++)
		{
			csi.print(1, 5 + i, connectedClients.get(i), CSIColor.LIGHT_GRAY);
		}
		
		//info frame
		for (int i = 1; i < 24; i++)
		{
			csi.print(50, i, "|", CSIColor.LIGHT_GRAY);
			csi.print(79, i, "|", CSIColor.LIGHT_GRAY);
		}
		
		CSIColor[] commandHighlights = new CSIColor[4];
		
		for (int i = 0; i < 4; i++)
		{
			commandHighlights[i] = CSIColor.LIGHT_GRAY;
			
			if (i == activeCommand)
			{
				commandHighlights[i] = CSIColor.GREEN;
			}
		}
		
		csi.print(50, 0, "+----------------------------+", CSIColor.LIGHT_GRAY);
		csi.print(50, 2, "+----------------------------+", CSIColor.LIGHT_GRAY);
		csi.print(50, 13, "+----------------------------+", CSIColor.LIGHT_GRAY);
		csi.print(50, 24, "+----------------------------+", CSIColor.LIGHT_GRAY);
		csi.print(51, 14, "[M] Move", commandHighlights[0]);
		csi.print(51, 15, "[C] Check", commandHighlights[1]);
		csi.print(51, 16, "[J] Jump", commandHighlights[2]);
		csi.print(51, 17, "[H] Handoff", commandHighlights[3]);
		csi.print(51, 18, "[PgUp] Show Top of Map", CSIColor.LIGHT_GRAY);
		csi.print(51, 19, "[PgDn] Show Bottom of Map", CSIColor.LIGHT_GRAY);
		csi.print(51, 20, "[1-9] Select Player", CSIColor.LIGHT_GRAY);
		csi.print(51, 21, "[Tab] Timeout", CSIColor.LIGHT_GRAY);
		csi.print(51, 22, "[ENTER] End Turn", CSIColor.LIGHT_GRAY);
		csi.print(51, 23, "[ESC] Menu", CSIColor.LIGHT_GRAY);
		
		//team frame
		csi.print(0, 21, "| |             |[  ][  ][  ][  ][  ][  ][  ]|", CSIColor.DARK_GRAY);
		csi.print(0, 22, "|  |[    ][    ]|[  ][  ][  ][  ][  ][  ][  ]|", CSIColor.DARK_GRAY);
		csi.print(0, 23, "[   ][   ][   ][   ][   ][   ][   ][   ][   ]|", CSIColor.DARK_GRAY);
		csi.print(0, 24, "|    |    |    |    |    |    |    |    |    |", CSIColor.DARK_GRAY);
		csi.print(18, 21, "CH", CSIColor.DARK_RED);
		csi.print(22, 21, "ST", CSIColor.DARK_RED);
		csi.print(26, 21, "TG", CSIColor.DARK_RED);
		csi.print(30, 21, "RF", CSIColor.DARK_RED);
		csi.print(34, 21, "JP", CSIColor.DARK_RED);
		csi.print(38, 21, "HD", CSIColor.DARK_RED);
		csi.print(42, 21, "DA", CSIColor.DARK_RED);
		csi.print(46, 21, "TEAM", CSIColor.DENIM);
		csi.print(6, 22, "AP", CSIColor.DARK_RED);
		
		int pNum = 1;
		for (int i = 2; i < 44; i += 5)
		{
			csi.print(i, 23, String.valueOf(pNum++), CSIColor.DENIM);
		}
				
		String name = "";
		int selectedIndex = -1;
		
		int ch = 0;
		int st = 0;
		int tg = 0;
		int rf = 0;
		int jp = 0;
		int hd = 0;
		int da = 0;
		int ap = 0;
		int curAp = 0;
		
		if (curPlayerIndex > -1 && curPlayerIndex >= curTeamIndex * 9)	//don't update this stuff if the current player isn't on the current team
		{
			selectedIndex = curPlayerIndex - (curTeamIndex * 9) + 1;
			
			Player p = myClient.getData().getPlayer(curPlayerIndex);
			ch = p.getAttributeWithModifiers(Player.ATT_CH);
			st = p.getAttributeWithModifiers(Player.ATT_ST);
			tg = p.getAttributeWithModifiers(Player.ATT_TG);
			rf = p.getAttributeWithModifiers(Player.ATT_RF);
			jp = p.getAttributeWithModifiers(Player.ATT_JP);
			hd = p.getAttributeWithModifiers(Player.ATT_HD);
			da = p.getAttributeWithModifiers(Player.ATT_DA);
			ap = p.getAttributeWithModifiers(Player.ATT_AP);
			
			name = p.name;
			curAp = p.currentAP;
			
			//System.out.println("refreshInterface(): Team is " + curTeamIndex + " and player is " + curPlayerIndex);
			
			csi.print(5 * (curPlayerIndex - 9 * curTeamIndex), 23, "[", CSIColor.WHITE);
			csi.print(5 * (curPlayerIndex - 9 * curTeamIndex) + 4, 23, "]", CSIColor.WHITE);
			csi.print(5 * (curPlayerIndex - 9 * curTeamIndex), 24, "|    |", CSIColor.WHITE);
		}
		else
		{
			selectedIndex = 0;
		}
		
		//System.out.println("refreshInterface(): selectedIndex is " + selectedIndex);
		
		csi.print(1, 21, String.valueOf(selectedIndex), CSIColor.WHITE);
		csi.print(3, 21, name, CSIColor.WHITE);
		csi.print(18, 22, String.valueOf(ch), CSIColor.DENIM);
		csi.print(22, 22, String.valueOf(st), CSIColor.DENIM);
		csi.print(26, 22, String.valueOf(tg), CSIColor.DENIM);
		csi.print(30, 22, String.valueOf(rf), CSIColor.DENIM);
		csi.print(34, 22, String.valueOf(jp), CSIColor.DENIM);
		csi.print(38, 22, String.valueOf(hd), CSIColor.DENIM);
		csi.print(42, 22, String.valueOf(da), CSIColor.DENIM);
		csi.print(1, 22, String.valueOf(ap), CSIColor.DENIM);
		csi.print(12, 22, String.valueOf(curAp), CSIColor.BRIGHT_GREEN);
		
		
		int startingIndex = curTeamIndex * 9;
		
		for (int i = 0; i < 9; i++)
		{
			Player p = myClient.getData().getPlayer(startingIndex + i);
			
			if (p != null)
			{
				int status = p.status;
				String statusStrings[] = {"LATE", "DECK", "OKAY", "DOWN", "STUN", "BLOB", "HURT", "DEAD", "OUT "};
				
				String statusString = statusStrings[status];
				
				CSIColor toColor = CSIColor.BRIGHT_GREEN;
				
				if (status == Player.STS_OKAY || status == Player.STS_DOWN)
					statusString = " " + String.valueOf(p.currentAP);
				else if (status == Player.STS_LATE)
					toColor = CSIColor.WHITE;
				else if (status == Player.STS_STUN)
					toColor = CSIColor.WHEAT;
				else if (status == Player.STS_HURT)
					toColor = CSIColor.ORANGE;
				else if (status == Player.STS_DEAD)
					toColor = CSIColor.DARK_RED;
				else if (status == Player.STS_BLOB)
					toColor = CSIColor.PURPLE;
				
				csi.print((5 * i) + 1, 24, statusString, toColor);
			}
		}
		
		//teams
		csi.print(46, 22, " ###", CSIColor.RED);
		csi.print(46, 23, " ###", CSIColor.BRIGHT_GREEN);
		csi.print(46, 24, " ###", CSIColor.BLUE);
		
		csi.print(46, 22 + curTeamIndex, ">", CSIColor.WHITE);
		
		csi.refresh();
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
		
		//display highlights if they exist
		for (int i = 0; i < highlights.size(); i++)
		{
			HighlightIcon hi = highlights.get(i);
			
			CSIColor bg = CSIColor.BLACK;
			CSIColor fg = CSIColor.WHITE;
			String icon = "?";
			
			if (hi.isJumpTarget())
			{
				bg = CSIColor.GREEN;
				fg = CSIColor.BLACK;
				icon = "" + ((char)(i + 65));
			}
			else //everything else only updates the background, so set the foreground here
			{
				//note that both checking and handoffs only go over standing players, so this is pretty easy
				
				Point playerCoords = new Point(hi.x, hi.y);
				
				PlayerIcon colors = getPlayerIconAtPoint(playerCoords);
				
				fg = colors.fg;
				icon = colors.icon;
			}
			
			if (hi.isHandoffTarget() || hi.isGoodCheckTarget())
			{
				bg = CSIColor.GREEN;
			}
			else if (hi.isEvenCheckTarget())
			{
				bg = CSIColor.YELLOW;
			}
			else if (hi.isBadCheckTarget())
			{
				bg = CSIColor.DARK_RED;
			}
			
			int rowAdjust = 0;
			int colAdjust = 20;
			
			if (!showTopHalf) rowAdjust = -9;	//or whatever
			
			int row = hi.x + rowAdjust;
			int col = hi.y + colAdjust;
			
			csi.print(col, row, icon, fg, bg);
		}
		
		csi.refresh();
	}

	
	private void updateTile(Point coords)
	{
		Field localMap = myClient.getData().getArena();
		
		int rowAdjust = 0;
		int colAdjust = 20;
		
		if (!showTopHalf) rowAdjust = -9;	//or whatever
		
		int row = coords.x + rowAdjust;
		int col = coords.y + colAdjust;
		
		if (row < 0 || row > 20) return;
			
		Player p = myClient.getData().getPlayerAtLocation(coords);
		
		CSIColor fg = CSIColor.WHITE;
		CSIColor bg = CSIColor.BLACK;
		String icon = "%";		//something that will never be used
		
		//no player here
		if (p == null)
		{
			int tile = localMap.getTile(coords.x, coords.y);
			
			if (coords.x == myClient.getData().getBallLocation().x && coords.y == myClient.getData().getBallLocation().y)
			{
				icon = "•";
				fg = CSIColor.DENIM;
			}
			else if (tile == 0)
			{
				fg = CSIColor.DARK_GRAY;
				icon = "#";
			}
			else if (tile == 1)
			{
				fg = CSIColor.LIGHT_GRAY;
				icon = ".";
				
				if ((coords.x < 5 && (coords.y < 5 || coords.y > 24)) || (coords.x > 24 && (coords.y < 5 || coords.y > 24)))
				{
					fg = CSIColor.BROWN;
					icon = "$";
				}
			}
			else if (tile == 2)
			{
				//change to be hosting team's colors
				//fg = CSIColor.RICH_CARMINE;
				fg = CSIColor.GOLD;
				icon = "$";
			}
			else if (tile == 3)
			{
				fg = CSIColor.CYAN;
				icon = "o";
			}
			else if (tile == 4)
			{
				fg = CSIColor.BROWN;
				icon = "+";
			}
			else if (tile == 5)
			{
				fg = CSIColor.DENIM;
				icon = "@";
				
				int binIndex = myClient.getData().getArena().getBinIndex(coords.x, coords.y);
				int binStatus = myClient.getData().getArena().getBinStatus(binIndex);
				
				if (binStatus == Field.STATE_FAILED)
					fg = CSIColor.DARK_RED;
				if (binStatus == Field.STATE_SUCCESS)
					fg = CSIColor.GREEN;
			}
			//etc.
		}
		else	//player here
		{	
			PlayerIcon colors = getPlayerIconAtPoint(coords);
			fg = colors.fg;
			bg = colors.bg;
			
			icon = colors.icon;
		}
		
		csi.print(col, row, icon, fg, bg);
	}
	
	//set team color, status color, and player symbol
	private PlayerIcon getPlayerIconAtPoint(Point coords)
	{
		PlayerIcon toRet = null;
		
		Player p = myClient.getData().getPlayerAtLocation(coords);
		
		if (p != null)
		{
			String[] iconList = {"C", "D", "G", "H", "K", "N", "S", "X"};
			
			CSIColor fg = CSIColor.WHITE;
			CSIColor bg = CSIColor.BLACK;
			String icon = iconList[p.getRace()];
			
			//set team color
			int team = myClient.getData().getTeamOfPlayer(p);
			
			if (team == 0)
				fg = CSIColor.RED;
			if (team == 1)
				fg = CSIColor.BRIGHT_GREEN;
			if (team == 2)
				fg = CSIColor.BLUE;
			
			//current player should be indicated by white coloring regardless of team
			if (myClient.getData().getPlayer(curPlayerIndex) == p)
				fg = CSIColor.WHITE;
			
			//additional statuses
			int status = p.getStatus();
			if (status == Player.STS_DOWN || status == Player.STS_STUN)
			{
				icon = icon.toLowerCase();
			}
			if (status == Player.STS_STUN)
			{
				bg = CSIColor.AMBER;
			}
			if (myClient.getData().getBallCarrier() == p)
			{
				bg = CSIColor.DENIM;
			}
			
			toRet = new PlayerIcon(fg, bg, icon);
		}
		
		return toRet;
	}
	
	private void handleDirection(int rowChange, int colChange)
	{
		if (curPlayerIndex == -1)
			return;
		
		Player plyr = myClient.getData().getPlayer(curPlayerIndex);
		Point curLocation = myClient.getData().getLocationOfPlayer(plyr);
		
		if (activeCommand == 0)	//move
		{
			Event moveEvent = Event.move(curPlayerIndex, curLocation.x + rowChange, curLocation.y + colChange, false);
			sendCommand(moveEvent);
		}
		
		if (activeCommand == 1)	//check
		{
			System.out.println("DISPLAY - INITIATING CHECK");
			
			Player target = myClient.getData().getPlayerAtLocation(curLocation.x + rowChange, curLocation.y + colChange);
			
			//check if something is in the given direction
			if (target != null)
			{
				System.out.println("DISPLAY - CHECK: TARGET FOUND");
				
				//check if that "something" is a valid handoff target
				if (myClient.getData().getTeamOfPlayer(target) != myClient.getData().getTeamOfPlayer(plyr) && target.getStatus() == Player.STS_OKAY)
				{
					System.out.println("DISPLAY - CHECK: VALID TARGET");
					
					highlights.clear();
					activeCommand = 0;
					int targetIndex = myClient.getData().getIndexOfPlayer(target);
					Event checkEvent = Event.check(curPlayerIndex, targetIndex, -2);	//-2 just means we don't have a result yet
					sendCommand(checkEvent);
				}
			}
		}
		
		if (activeCommand == 3) //handoff
		{
			System.out.println("DISPLAY - HANDING OFF BALL");
			
			Player target = myClient.getData().getPlayerAtLocation(curLocation.x + rowChange, curLocation.y + colChange);
			
			//check if something is in the given direction
			if (target != null)
			{
				System.out.println("DISPLAY - HANDING OFF BALL: TARGET FOUND");
				
				//check if that "something" is a valid handoff target
				if (myClient.getData().getTeamOfPlayer(target) == myClient.getData().getTeamOfPlayer(plyr) && target.getStatus() == Player.STS_OKAY)
				{
					System.out.println("DISPLAY - HANDING OFF BALL: VALID TARGET");
					
					highlights.clear();
					activeCommand = 0;
					int targetIndex = myClient.getData().getIndexOfPlayer(target);
					Event handoffEvent = Event.handoff(curPlayerIndex, targetIndex, 0);
					sendCommand(handoffEvent);
				}
			}
		}
	}
	
	private int getFirstEligiblePlayer()
	{
		System.out.println("FIRST: team index is " + curTeamIndex);
		int startingIndex = curTeamIndex * 9;
		
		for (int i = startingIndex; i < startingIndex + 9; i++)
		{
			Player p = myClient.getData().getPlayer(i);
			
			if (p == null) continue;
			
			if (p.status == Player.STS_OKAY || p.status == Player.STS_STUN || p.status == Player.STS_DOWN)
			{
				System.out.println("SETTING CURRENT PLAYER");
				currentPlayer = p;
				return i;
			}
		}
		
		return -1;
	}
	
	private void setActivePlayer(int index)
	{
		System.out.println("Setting Active Player...");
		
		highlights.clear();
		activeCommand = 0;
		
		int tempIndex = curPlayerIndex;
		curPlayerIndex = curTeamIndex * 9 + index - 1;
		System.out.println("Active 1");
		Player p = myClient.getData().getPlayer(curPlayerIndex);
		System.out.println("Active 2");
		if (p == null)
		{
			System.out.println("Active 3a");
			curPlayerIndex = tempIndex;
			return;
		}
		
		if (p.status == Player.STS_DEAD || p.status == Player.STS_LATE || p.status == Player.STS_DECK || p.status == Player.STS_BLOB || p.status == Player.STS_HURT)
		{
			System.out.println("Active 3b");
			curPlayerIndex = tempIndex;
			return;
		}
		
		System.out.println("Active 4");
		
		Point pnt = myClient.getData().getLocationOfPlayer(p);
		currentPlayer = p;
		
		System.out.println("Active 5");
		
		refreshInterface();
		zoomToLocation(pnt.x);
	}
	
	private boolean canCurrentPlayerAct()
	{
		//make sure a player is selected
		//get the team of the player
		//make sure that it's that teams turn, that this client controls that team, and that the player has AP
		System.out.println("Act Check 0");
		System.out.println(currentPlayer);
		if (currentPlayer == null) return false;
		System.out.println("Act Check 1");
		int playersTeam = myClient.getData().getTeamOfPlayer(currentPlayer);
		System.out.println("Act Check 2");
		
		System.out.println("\tTEAM: " + playersTeam + " | " + myClient.getData().getCurrentTeam());
		System.out.println("\tCONTROL? " + myClient.controlsTeam(playersTeam));
		System.out.println("\tAP: " + currentPlayer.currentAP);
		
		if (playersTeam == myClient.getData().getCurrentTeam() && myClient.controlsTeam(playersTeam) && currentPlayer.currentAP >= 10)
			return true;
		System.out.println("Act Check 3");
		return false;
	}
	
	private boolean canCurrentPlayerJump()
	{
		System.out.println("Jump Check 0");
		if (!canCurrentPlayerAct()) return false;
		System.out.println("Jump Check 1");
		
		if (currentPlayer.currentAP >= 30 || (currentPlayer.currentAP >= 20 && currentPlayer.race == Player.RACE_CURMIAN))
			return true;
		System.out.println("Jump Check 2");
		return false;
	}
	
	private boolean canCurrentPlayerCheck()
	{
		if (!canCurrentPlayerAct()) return false;
		if (currentPlayer.currentAP >= 20 || (currentPlayer.currentAP >= 10 && currentPlayer.hasSkill[Player.SKILL_CHARGE]))
			return true;
		return false;
	}
	
	private boolean canCurrentPlayerHandoff() {
		if (!canCurrentPlayerAct()) return false;
		
		//still need a check if the player has just gotten up; this can happen if they get up this turn and get the ball handed to them
		
		if (currentPlayer.currentAP == currentPlayer.getAttributeWithModifiers(Player.ATT_AP) && myClient.getData().getBallCarrier() == currentPlayer)
			return true;
		return false;
	}
	


	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	//inner class for general icon data
	class PlayerIcon
	{
		public CSIColor fg = CSIColor.WHITE;
		public CSIColor bg = CSIColor.BLACK;
		public String icon = "%";		//something that will never be used
		
		public PlayerIcon(CSIColor foreground, CSIColor background, String theIcon)
		{
			fg = foreground;
			bg = background;
			icon = theIcon;
		}
	}
	
	//inner class for highlights
	class HighlightIcon
	{
		public int x;
		public int y;
		private int type;
		
		public HighlightIcon(int x, int y, int type)
		{
			this.x = x;
			this.y = y;
			this.type = type;
		}
		
		public boolean isHandoffTarget()
		{
			return (type == HI_HANDOFF);
		}
		
		public boolean isJumpTarget()
		{
			return (type == HI_JUMP);
		}
		
		public boolean isBadCheckTarget()
		{
			return (type == HI_CHECK_BAD);
		}
		
		public boolean isEvenCheckTarget()
		{
			return (type == HI_CHECK_EVEN);
		}
		
		public boolean isGoodCheckTarget()
		{
			return (type == HI_CHECK_GOOD);
		}
	}
	
	private static final int HI_HANDOFF = 0;
	private static final int HI_JUMP = 1;
	private static final int HI_CHECK_BAD = 2;
	private static final int HI_CHECK_EVEN = 3;
	private static final int HI_CHECK_GOOD = 4;
}
