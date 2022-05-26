package main.presentation.game;

import java.awt.Point;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.data.Event;
import main.data.entities.Player;
import main.data.entities.Race;
import main.data.entities.Skill;
import main.logic.Client;
import main.logic.Server;
import main.presentation.common.Logger;

public abstract class GameRunnerGUI extends GameGUI
{
	protected Player currentPlayer = null; // note that this is only in the mind of GUI; the data layer doesn't care which player is selected
	protected boolean gameStarted = false;

	protected List<HighlightIcon> highlights;
	protected List<Point> movePossibilities;
	protected Map<Integer, Point> jumpPossibilities;
	
	protected int curPlayerIndex = -1;
	protected int curTeamIndex = 0;
	protected Action currentAction = Action.ACTION_MOVE;
	
	protected boolean ballCarrierHasActedAfterReceivingBall = false;
	
	protected ActionListener gameEndListener;

	public GameRunnerGUI(Client theClient, Server theServer, ActionListener gameEndListener)
	{
		super(theClient, theServer);
		
		highlights = new ArrayList<HighlightIcon>();
		movePossibilities = new ArrayList<Point>();
		jumpPossibilities = new HashMap<Integer, Point>();
		
		this.gameEndListener = gameEndListener;
	}
	
	protected void setHighlightsForMove(Point destination)
	{
		highlights.clear();
		movePossibilities.clear();
		
		Player plyr = getData().getPlayer(curPlayerIndex);
		Point origin = getData().getLocationOfPlayer(plyr);
		
		if (origin == null)
			return;
		
		int x = origin.x;
		int y = origin.y;
		
		int endX = destination.x;
		int endY = destination.y;
		
		int apCost = 0;
		
		while (x != endX || y != endY)
		{
			if (x < endX) x++;
			if (x > endX) x--;
			if (y < endY) y++;
			if (y > endY) y--;
			
			if (getData().getPlayerAtLocation(x, y) != null)
				return;
			
			if (getData().getArena().isObstructedForPlayer(x, y))
				return;
			
			movePossibilities.add(new Point(x, y));
			
			apCost += 10;
			
			if (apCost > plyr.currentAP)
				highlights.add(new HighlightIcon(x, y, HighlightIcon.HI_MOVE_BAD));
			else
				highlights.add(new HighlightIcon(x, y, HighlightIcon.HI_MOVE_GOOD));
		}
	}
	
	protected void setHighlightsForJump()
	{
		highlights.clear();
		jumpPossibilities.clear();

		boolean canJump[][] = new boolean[5][5]; // might make this global, then clear it after each jump

		Player plyr = getData().getPlayer(curPlayerIndex);
		Point curLocation = getData().getLocationOfPlayer(plyr);

		int centerX = curLocation.x;
		int centerY = curLocation.y;

		// make every tile initially jumpable to (unless it's obstructed)
		for (int i = 0; i < 5; i++)
		{
			for (int j = 0; j < 5; j++)
			{
				canJump[i][j] = !getData().getArena().isObstructedForPlayer(centerX + i - 2, centerY + j - 2);

				// if there's a player there, obviously you can't jump there
				if (getData().getPlayerAtLocation(centerX + i - 2, centerY + j - 2) != null)
					canJump[i][j] = false;
			}
		}

		for (int i = -1; i <= 1; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				canJump[i + 2][j + 2] = false; // can't jump immediately adjacent to the player

				if (i == 0 && j == 0)
					continue; // calculations don't work at (0, 0)

				boolean playerInWay = false;
				Player playerToJump = getData().getPlayerAtLocation(centerX + i, centerY + j);

				// there's a player in the way
				if (playerToJump != null)
				{
					int playerStatus = playerToJump.getStatus();

					// if the player in the way is down, stunned, or if the jumper is a curmian (which can jump standing players), there's nothing in the way
					if (playerStatus == Player.STS_DOWN || playerStatus == Player.STS_STUN_DOWN || playerStatus == Player.STS_STUN_SIT || plyr.getRace() == Race.CURMIAN)
					{
						playerInWay = false;
					} else
					{
						playerInWay = true;
					}
				}

				if (!getData().getArena().isObstructedForPlayer(centerX + i, centerY + j) && !playerInWay)
					continue; // nothing is blocking

				if (Math.abs(i) == Math.abs(j)) // on a diagonal
				{
					canJump[(i * 2) + 2][j + 2] = false;
					canJump[(i * 2) + 2][(j * 2) + 2] = false;
					canJump[i + 2][(j * 2) + 2] = false;
				} else if (i == 0) // horizontal
				{
					canJump[1][(j * 2) + 2] = false;
					canJump[2][(j * 2) + 2] = false;
					canJump[3][(j * 2) + 2] = false;
				} else if (j == 0) // vertical
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
					highlights.add(new HighlightIcon(centerX - 2 + i, centerY - 2 + j, HighlightIcon.HI_JUMP));

					Point coords = new Point(i - 2, j - 2);
					Integer letter = highlights.size() + 64;
					jumpPossibilities.put(letter, coords);
				}
			}
		}
	}

	protected void setHighlightsForCheck()
	{
		highlights.clear();

		Player plyr = getData().getPlayer(curPlayerIndex);
		Point curLocation = getData().getLocationOfPlayer(plyr);

		int centerX = curLocation.x;
		int centerY = curLocation.y;

		for (int i = -1; i <= 1; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				Player target = getData().getPlayerAtLocation(centerX + i, centerY + j);

				if (target != null)
				{
					if (getData().getTeamIndexOfPlayer(target) != getData().getTeamIndexOfPlayer(plyr)
							&& target.getStatus() == Player.STS_OKAY)
					{
						int checkOdds = HighlightIcon.HI_CHECK_EVEN;

						int atk_base_CH = plyr.getAttributeWithModifiers(Player.ATT_CH);
						int def_base_CH = target.getAttributeWithModifiers(Player.ATT_CH);
						
						if (def_base_CH < atk_base_CH && target.hasSkill(Skill.JUDO))
							def_base_CH = atk_base_CH;
						
						int dif = (getAssistBonus(plyr, target) + atk_base_CH)
								- (getAssistBonus(target, plyr) + def_base_CH);

						if (dif >= 20)
							checkOdds = HighlightIcon.HI_CHECK_GOOD;

						if (dif <= -20)
							checkOdds = HighlightIcon.HI_CHECK_BAD;

						highlights.add(new HighlightIcon(centerX + i, centerY + j, checkOdds));
					}
				}
			}
		}

		// no targets are valid if the player doesn't have enough AP
		if (plyr.currentAP < 10 || (plyr.currentAP < 20 && !plyr.hasSkill(Skill.CHARGE)))
			highlights.clear();

		// switch back to moving if there are no valid targets
		if (highlights.size() == 0)
			currentAction = Action.ACTION_MOVE;
	}
	
	protected void setHighlightsForHandoff()
	{
		highlights.clear();

		Player plyr = getData().getPlayer(curPlayerIndex);
		Point curLocation = getData().getLocationOfPlayer(plyr);

		int centerX = curLocation.x;
		int centerY = curLocation.y;

		for (int i = -1; i <= 1; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				Player target = getData().getPlayerAtLocation(centerX + i, centerY + j);

				if (target != null)
				{
					if (getData().getTeamIndexOfPlayer(target) == getData().getTeamIndexOfPlayer(plyr)
							&& target.getStatus() == Player.STS_OKAY)
					{
						highlights.add(new HighlightIcon(centerX + i, centerY + j, HighlightIcon.HI_HANDOFF));
					}
				}
			}
		}

		// switch back to moving if there are no valid targets
		if (highlights.size() == 0)
			currentAction = Action.ACTION_MOVE;
	}

	protected boolean canCurrentPlayerAct()
	{
		// make sure a player is selected
		// get the team of the player
		// make sure that it's that teams turn, that this client controls that team, and that the player has AP
		Logger.info("Act Check 0");
		Logger.info(currentPlayer.toString());
		if (currentPlayer == null)
			return false;
		Logger.info("Act Check 1");
		int playersTeam = getData().getTeamIndexOfPlayer(currentPlayer);
		Logger.info("Act Check 2");

		Logger.info("\tTEAM: " + playersTeam + " | " + getData().getCurrentTeam());
		Logger.info("\tCONTROL? " + myClient.controlsTeam(playersTeam));
		Logger.info("\tAP: " + currentPlayer.currentAP);

		if (playersTeam == getData().getCurrentTeam() && myClient.controlsTeam(playersTeam) && currentPlayer.currentAP >= 10)
			return true;
		Logger.info("Act Check 3");
		return false;
	}

	protected boolean canCurrentPlayerJump()
	{
		Logger.info("Jump Check 0");
		if (!canCurrentPlayerAct())
			return false;
		Logger.info("Jump Check 1");

		if (currentPlayer.canJump())
			return true;
		Logger.info("Jump Check 2");
		return false;
	}

	protected boolean canCurrentPlayerCheck()
	{
		if (!canCurrentPlayerAct())
			return false;
		if (currentPlayer.canThrowCheck())
			return true;
		return false;
	}

	protected boolean canCurrentPlayerHandoff()
	{
		if (!canCurrentPlayerAct())
			return false;

		System.out.println("CLIENT - HANDOFF CHECK: acted bool has a value of " + ballCarrierHasActedAfterReceivingBall);
		if (!ballCarrierHasActedAfterReceivingBall && getData().getBallCarrier() == currentPlayer && currentPlayer.currentAP >= 10)
			return true;
		return false;
	}
	
	protected int getFirstEligiblePlayer()
	{
		Logger.debug("GameRunnerGUI - getting first eligible player for team index " + curTeamIndex);
		int startingIndex = curTeamIndex * 9;

		for (int i = startingIndex; i < startingIndex + 9; i++)
		{
			Player p = getData().getPlayer(i);

			if (p == null)
				continue;

			if (p.status == Player.STS_OKAY || p.status == Player.STS_STUN_DOWN || p.status == Player.STS_DOWN)
			{
				Logger.debug("\tCurrent player set to " + p.name);
				currentPlayer = p;
				return i;
			}
		}

		return -1;
	}

	//TODO: this is restricted to the curses GUI and should probably be there
	protected void handleDirection(int rowChange, int colChange)
	{
		if (curPlayerIndex == -1)
			return;

		Player plyr = getData().getPlayer(curPlayerIndex);
		Point curLocation = getData().getLocationOfPlayer(plyr);

		if (currentAction == Action.ACTION_MOVE)
		{
			Event moveEvent = Event.move(curPlayerIndex, curLocation.x + rowChange, curLocation.y + colChange, false, false, false);
			sendCommand(moveEvent);
		}

		if (currentAction == Action.ACTION_CHECK)
		{
			System.out.println("DISPLAY - INITIATING CHECK");

			Player target = getData().getPlayerAtLocation(curLocation.x + rowChange, curLocation.y + colChange);

			// check if something is in the given direction
			if (target != null)
			{
				System.out.println("DISPLAY - CHECK: TARGET FOUND");

				// check if that "something" is a valid check target
				if (getData().getTeamIndexOfPlayer(target) != getData().getTeamIndexOfPlayer(plyr)
						&& target.getStatus() == Player.STS_OKAY)
				{
					System.out.println("DISPLAY - CHECK: VALID TARGET");

					highlights.clear();
					currentAction = Action.ACTION_MOVE;
					int targetIndex = getData().getIndexOfPlayer(target);
					Event checkEvent = Event.check(curPlayerIndex, targetIndex, -2, false); // -2 just means we don't have a result yet
					sendCommand(checkEvent);
				}
			}
		}

		if (currentAction == Action.ACTION_HANDOFF)
		{
			System.out.println("DISPLAY - HANDING OFF BALL");

			Player target = getData().getPlayerAtLocation(curLocation.x + rowChange, curLocation.y + colChange);

			// check if something is in the given direction
			if (target != null)
			{
				System.out.println("DISPLAY - HANDING OFF BALL: TARGET FOUND");

				// check if that "something" is a valid handoff target
				if (getData().getTeamIndexOfPlayer(target) == getData().getTeamIndexOfPlayer(plyr)
						&& target.getStatus() == Player.STS_OKAY)
				{
					System.out.println("DISPLAY - HANDING OFF BALL: VALID TARGET");

					highlights.clear();
					currentAction = Action.ACTION_MOVE;
					int targetIndex = getData().getIndexOfPlayer(target);
					Event handoffEvent = Event.handoff(curPlayerIndex, targetIndex, 0);
					sendCommand(handoffEvent);
				}
			}
		}
	}

	//TODO: see which player should be selected after teleport events; I'm currently selecting the first player on the team, but zooming to the player who teleported
	protected void handleTeleportEvent(Event e)
	{
		Logger.debug("Display - Teleporting from " + e.flags[2] + " to " + e.flags[3]);

		Point pnt = getData().getArena().getPortal(e.flags[3]);

		if (e.flags[2] == -1)
		{
			curPlayerIndex = e.flags[0];
			currentPlayer = getData().getPlayer(curPlayerIndex);
		} else if (e.flags[2] == e.flags[3])
		{
			curPlayerIndex = getFirstEligiblePlayer();
		}

		refreshInterface();
		snapToTile(pnt);
	}

	protected void setActivePlayer(int index)
	{
		Logger.debug("GameRunnerGui - Setting Active Player to " + index);
		Logger.debug(" getData() returns data object with ID: " + getData().toString());

		highlights.clear();
		currentAction = Action.ACTION_MOVE;

		int tempIndex = curPlayerIndex;
		curPlayerIndex = curTeamIndex * 9 + index - 1;
		Logger.debug("\tCurrent team index is " + curTeamIndex);
		Logger.debug("\tCurrent player index is " + curPlayerIndex);
		Player p = getData().getPlayer(curPlayerIndex);
		
		if (p == null)
		{
			Logger.debug("\tPlayer is null; setting current player index back to " + tempIndex);
			curPlayerIndex = tempIndex;
			return;
		}
		
		Logger.debug("\tCurrent player name is " + p.name);
		
		if (p.status == Player.STS_DEAD || p.status == Player.STS_LATE || p.status == Player.STS_DECK || p.status == Player.STS_BLOB
				|| p.status == Player.STS_HURT || p.status == Player.STS_OUT)
		{
			Logger.debug("\tPlayer is unable to act; setting current player index back to " + tempIndex);
			curPlayerIndex = tempIndex;
			return;
		}

		Logger.debug("\tPlayer is able to act");

		Point pnt = getData().getLocationOfPlayer(p);
		currentPlayer = p;

		Logger.debug("\tCurrent player has been set.");
		Logger.debug("\tCurrent player index: " + curPlayerIndex);
		Logger.debug("\tTarget zoom point: " + pnt);

		refreshInterface();
		snapToTile(pnt);
	}
	
	@Override
	public void beginGame()
	{
		gameStarted = true;
	}
	
	@Override
	public void endGame()
	{
		gameStarted = false;
	}
	
	@Override
	public boolean gameStarted()
	{
		return gameStarted;
	}
	
	protected abstract void snapToTile(Point location);
	
	protected abstract void refreshPlayerStatuses();
}
