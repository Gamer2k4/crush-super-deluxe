package main.presentation.legacy.game;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.util.List;

import main.data.Event;
import main.data.entities.Player;
import main.data.entities.Team;
import main.logic.Client;
import main.logic.Server;
import main.presentation.common.AbstractScreenPanel;
import main.presentation.common.Logger;
import main.presentation.game.Action;
import main.presentation.game.GameRunnerGUI;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.legacy.game.sprites.SpriteFactory;

public class LegacyGraphicsGUI extends GameRunnerGUI implements ActionListener
{
	public static final int MAX_VIEWPORT_X = LegacyUiConstants.MAP_IMAGE_WIDTH - GamePanel.VIEWPORT_WIDTH;
	public static final int MAX_VIEWPORT_Y = LegacyUiConstants.MAP_IMAGE_HEIGHT - GamePanel.VIEWPORT_HEIGHT;
	
	private GamePanel gamePanel;
	
	private LegacyViewportImageFactory viewportImageFactory;
	private LegacyButtonBarImageFactory buttonBarImageFactory;
	
	private BufferedImage scrollImage;
	
	private int xScroll = 0;
	private int yScroll = 0;
	
	private static final int SCROLL_INCREMENT = 5;
	
	private Point currentViewportOrigin = new Point(0, 0);
//	private Point scrollTarget = new Point(0, 0);
	
	public LegacyGraphicsGUI(Client theClient, ActionListener gameEndListener)
	{
		this(theClient, null, gameEndListener);
	}
	
	public LegacyGraphicsGUI(Client theClient, Server theServer, ActionListener gameEndListener)
	{
		super(theClient, theServer, gameEndListener);
		
		viewportImageFactory = LegacyViewportImageFactory.getInstance();
		buttonBarImageFactory = LegacyButtonBarImageFactory.getInstance();

		gamePanel = new GamePanel(GamePanel.GAME_WINDOW_WIDTH, GamePanel.GAME_WINDOW_HEIGHT, this);
		
		this.gameEndListener = gameEndListener;
	}

	@Override
	public void beginGame()
	{
		List<Team> teams = getData().getTeams();
		
		for (int i = 0; i < 3; i++)
		{
			Team team = teams.get(i);
			SpriteFactory.getInstance().setTeamColors(i, team.teamColors[0], team.teamColors[1]);
		}
		
		List<Player> players = getData().getAllPlayers();
		
		for (Player player : players)
		{
			viewportImageFactory.updatePlayerState(player, "PASSIVE");
			viewportImageFactory.updatePlayerFacing(player, "S");
		}
		
		super.beginGame();
	}

	@Override
	public void closeGUI()
	{
		// TODO Auto-generated method stub
	}

	@Override
	protected void snapToTile(Point tileCoords)
	{
		Logger.debug("zooming to location " + tileCoords);
		
		Point snapTarget = getOriginLocationWithTileAtCenter(tileCoords);
		
//		initiateScrolling(); 		//TODO: correct scrolling later
		snapToLocation(snapTarget);
	}
	
	private void snapToBallTile()
	{
		Point ballLocation = getData().getBallLocation();
		Player ballCarrier = getData().getBallCarrier();
		
		if (ballLocation.x != -1 && ballLocation.y != -1)
			snapToTile(ballLocation);
		else if (ballCarrier != null)
			snapToTile(getData().getLocationOfPlayer(ballCarrier));
		else
			return;
	}
	
	private void snapToLocation(Point location)
	{
		currentViewportOrigin = new Point(location.x, location.y);

		if (currentViewportOrigin.x < 0)
			currentViewportOrigin.x = 0;
		if (currentViewportOrigin.y < 0)
			currentViewportOrigin.y = 0;
		if (currentViewportOrigin.x > MAX_VIEWPORT_X)
			currentViewportOrigin.x = MAX_VIEWPORT_X;
		if (currentViewportOrigin.y > MAX_VIEWPORT_Y)
			currentViewportOrigin.y = MAX_VIEWPORT_Y;
		
	}

	@Override
	public void receiveEvent(Event event)
	{
		if (event.getType() == Event.EVENT_VICTORY)
		{
			endGame(event);
		} else if (event.getType() == Event.EVENT_TURN)
		{
			startNewTurn();
		} else if (event.getType() == Event.EVENT_RECVR)
		{
			recoverPlayer(event);
		} else if (event.getType() == Event.EVENT_TELE)
		{
			handleTeleportEvent(event);
		} else if (event.getType() == Event.EVENT_MOVE)
		{
			updatePlayerFacingFromMoveEvent(event);
//			snapToTile(new Point(event.flags[2], event.flags[3]));
			//TODO: scroll to the location if it's offscreen and it's the last move (perhaps trigger after animation has concluded?)
		} else if (event.getType() == Event.EVENT_CHECK)
		{
			updatePlayerFacingFromTwoPlayerEvent(event);
		} else if (event.getType() == Event.EVENT_HANDOFF)
		{
			if (event.flags[2] != 1)	//if hurling, don't change facing
				updatePlayerFacingFromTwoPlayerEvent(event);
			
			snapToBallTile();
		} else if (event.getType() == Event.EVENT_STS)
		{
			Player player = getData().getPlayer(event.flags[0]);
			int status = event.flags[2];
			
			if (status == Player.STS_DOWN || status == Player.STS_STUN)
				viewportImageFactory.updatePlayerState(player, "DOWN");
		} else if (event.getType() == Event.EVENT_BIN)
		{
			updatePlayerFacingFromBinEvent(event);
		} else if (event.getType() == Event.EVENT_BALLMOVE)
		{
			snapToBallTile();
		}
		
		checkForBallCarrierAction(event);
	}

	private void checkForBallCarrierAction(Event event)
	{
		if (event.getType() == Event.EVENT_HANDOFF)
			ballCarrierHasActedAfterReceivingBall = true;	//variable name is "wrong" now, but this is true to the original game - only one handoff per turn
		
		if (event.getType() != Event.EVENT_MOVE && event.getType() != Event.EVENT_CHECK)
			return;
		
		Player player = getData().getPlayer(event.flags[0]);

		if (player == getData().getBallCarrier())
			ballCarrierHasActedAfterReceivingBall = true;
	}

	private void updatePlayerFacingFromTwoPlayerEvent(Event event)
	{
		Player origin = getData().getPlayer(event.flags[0]);
		Player target = getData().getPlayer(event.flags[1]);
		
		Point originCoords = getData().getLocationOfPlayer(origin);
		Point targetCoords = getData().getLocationOfPlayer(target);
		
		String attackerFacing = "";
		String defenderFacing = "";
		
		if (originCoords.x < targetCoords.x)
		{
			attackerFacing = attackerFacing + "S";
			defenderFacing = defenderFacing + "N";
		}
		if (originCoords.x > targetCoords.x)
		{
			attackerFacing = attackerFacing + "N";
			defenderFacing = defenderFacing + "S";
		}
		if (originCoords.y < targetCoords.y)
		{
			attackerFacing = attackerFacing + "E";
			defenderFacing = defenderFacing + "W";
		}
		if (originCoords.y > targetCoords.y)
		{
			attackerFacing = attackerFacing + "W";
			defenderFacing = defenderFacing + "E";
		}
		
		viewportImageFactory.updatePlayerFacing(origin, attackerFacing);
		viewportImageFactory.updatePlayerFacing(target, defenderFacing);
	}

	private void updatePlayerFacingFromMoveEvent(Event event)
	{
		if (event.flags[4] == 1)	//sliding doesn't change facing
			return;
		
		String[] direction = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};	//starting at S so the default is facing down, like when players warp in
		
		Player player = getData().getPlayer(event.flags[0]);
		String facing = direction[event.flags[1]];
		viewportImageFactory.updatePlayerFacing(player, facing);
	}

	private void updatePlayerFacingFromBinEvent(Event event)
	{
		Player player = getData().getPlayer(event.flags[0]);
		Point playerCoords = getData().getLocationOfPlayer(player);
		Point binLocation = getData().getArena().getBinLocation(event.flags[2]);
		
		String playerFacing = "";
		
		if (playerCoords.x < binLocation.x)
			playerFacing = playerFacing + "S";
		if (playerCoords.x > binLocation.x)
			playerFacing = playerFacing + "N";
		if (playerCoords.y < binLocation.y)
			playerFacing = playerFacing + "E";
		if (playerCoords.y > binLocation.y)
			playerFacing = playerFacing + "W";
		
		viewportImageFactory.updatePlayerFacing(player, playerFacing);
	}

	//TODO: account for ties, quitting (do this by updating EVENT_VICTORY to EVENT_GAME_END)
	private void endGame(Event endGameEvent)
	{
		//TODO: display victory message
		
		while (!getData().isGameDone());	//wait for the game data to register the game is concluded
		closeGUI();
		gameEndListener.actionPerformed(new ActionEvent(this, 0, Client.ACTION_GAME_END));
	}
	
	private void startNewTurn()
	{
		highlights.clear();
		currentAction = Action.ACTION_MOVE;
		ballCarrierHasActedAfterReceivingBall = false;

		curTeamIndex = getData().getCurrentTeam();
	}
	
	//TODO: see if this can be abstracted up
	private void recoverPlayer(Event recoverEvent)
	{
		updatePlayerStatusOnScreen(recoverEvent);
		
		//TODO: remember, data has already handled the AP changes and such, so we just need to zoom to the player, animate, and update the AP
		
		int startingIndex = curTeamIndex * 9;
		int zoomRow = 0;
		int zoomCol = 0;
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
					zoomCol = getData().getLocationOfPlayer(p).y;
				}
			}
		}
		
		refreshInterface();
		snapToTile(new Point(zoomRow, zoomCol));
		//sleep(1500);		//TODO: only sleep if the player is doing more than getting his AP back
	}

	private void updatePlayerStatusOnScreen(Event recoverEvent)
	{
		Player player = getData().getPlayer(recoverEvent.flags[0]);
		if (player.getStatus() == Player.STS_OKAY)
			viewportImageFactory.updatePlayerState(player, "PASSIVE");
	}

	@Override
	public void refreshInterface()
	{
		if (!gameStarted)
			return;
		
		Logger.info("Legacy Graphics GUI - Refreshing.");
		buttonBarImageFactory.setCurrentAction(currentAction);
		gamePanel.updateButtonBar(buttonBarImageFactory.generateImage(getData(), currentPlayer));
		refreshViewportImage();
		// TODO: also update stats bar if there is one
		// perhaps update the view panel here, too?
	}
	
	private void refreshViewportImage()
	{
		viewportImageFactory.setHighlights(highlights);
		scrollImage = viewportImageFactory.generateImage(getData(), currentPlayer);
		
		try {
			gamePanel.updateViewportImage(scrollImage.getSubimage(currentViewportOrigin.x, currentViewportOrigin.y, GamePanel.VIEWPORT_WIDTH, GamePanel.VIEWPORT_HEIGHT));
		} catch (RasterFormatException rfe) 
		{
			return;
		}
	}

	@Override
	protected void refreshPlayerStatuses()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public AbstractScreenPanel getDisplayPanel()
	{
		return gamePanel;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Player plyr = getData().getPlayer(curPlayerIndex);
		Point curLocation = getData().getLocationOfPlayer(plyr);
		
		//don't accept inputs if the map is scrolling 
		if (xScroll != 0 || yScroll != 0)
			return;
		
		String command = e.getActionCommand();
		System.out.println("LegacyGraphicsGUI - Action performed: " + command);
		
		//TODO: implement scrolling
//		if (command.startsWith("SCROLL_"))
//			scrollMapFromScrollCommand(command);
		if (command.startsWith("SELECT_PLAYER_"))
			setActivePlayer(Integer.valueOf(command.substring(14)));
		else if (command.startsWith("VIEWPORT_CLICK_"))
			handleViewportClick(command);
		else if (command.startsWith("MINIMAP_CLICK_"))
			handleMinimapClick(command);
		else if (command.equals("END_TURN"))
		{
			int player = curTeamIndex + 1;
			if (player == 3)
				player = 0;

			sendCommand(Event.updateTurnPlayer(player));
		}
		else if (command.equals("MOVE"))
		{
			currentAction = Action.ACTION_MOVE;
			clearHighlights();
		}
		else if (command.equals("CHECK"))
		{
			currentAction = Action.ACTION_CHECK;
			setHighlightsForCheck();
		} else if (command.equals("JUMP") && canCurrentPlayerJump())
		{
			currentAction = Action.ACTION_JUMP;
			setHighlightsForJump();
		} else if (command.equals("HANDOFF") && canCurrentPlayerHandoff())
		{
			currentAction = Action.ACTION_HANDOFF;
			setHighlightsForHandoff();
		}
	}
	
	private void handleViewportClick(String command)
	{
		int mouseX = Integer.parseInt(command.substring(15, 18));
		int mouseY = Integer.parseInt(command.substring(19, 22));
		clickArenaLocation(getMapCoordsFromCursorLocation(mouseX, mouseY));
	}
	
	private void handleMinimapClick(String command)
	{
		int mouseX = Integer.parseInt(command.substring(14, 16));
		int mouseY = Integer.parseInt(command.substring(17, 19));
		snapToTile(new Point(mouseY, mouseX));	//row, column
	}

	private void clickArenaLocation(Point flippedCoords)
	{
		Point mapCoords = new Point(flippedCoords.y, flippedCoords.x);
		Logger.debug("Clicking arena location: " + mapCoords);
		
		Player target = getData().getPlayerAtLocation(mapCoords.x, mapCoords.y);
		
		//clicking the current player rotates them
		if (currentAction != Action.ACTION_HANDOFF && currentPlayer.equals(target) && currentPlayer.status == Player.STS_OKAY)
		{
			currentAction = Action.ACTION_MOVE;
			String currentFacing = viewportImageFactory.getPlayerFacing(currentPlayer);
			String newFacing = getNextFacing(currentFacing);
			viewportImageFactory.updatePlayerFacing(currentPlayer, newFacing);
			clearHighlights();
			return;
		}
		
		//clicking another player on the same team switches to that player
		if (currentAction != Action.ACTION_HANDOFF && getData().getTeamIndexOfPlayer(currentPlayer) == getData().getTeamIndexOfPlayer(target))
		{
			currentAction = Action.ACTION_MOVE;
			currentPlayer = target;
			curPlayerIndex = getData().getIndexOfPlayer(target);
			clearHighlights();
			snapToTile(mapCoords);
			return;
		}
		
		// check the current game state (is checking, is moving, etc) and act accordingly
		if (currentAction == Action.ACTION_MOVE)
		{
			for (Point possibility : movePossibilities)
			{
				if (mapCoords.x == possibility.x && mapCoords.y == possibility.y)
				{
					clearHighlights();
					Event moveEvent = Event.move(curPlayerIndex, mapCoords.x, mapCoords.y, false, false, false);
					sendCommand(moveEvent);
					return;
				}
			}
			
			if (currentPlayer.currentAP >= 10)	//no move overlay if the player doesn't have the AP
			{
				setHighlightsForMove(new Point(mapCoords.x, mapCoords.y));
				viewportImageFactory.setMoveTrack(movePossibilities);
			}
		} else if (currentAction == Action.ACTION_JUMP)
		{
			Point playerCoords = getData().getLocationOfPlayer(currentPlayer);
			
			for (Point possibility : jumpPossibilities.values())
			{
				if (mapCoords.x == playerCoords.x + possibility.x && mapCoords.y == playerCoords.y + possibility.y)
				{
					Event moveEvent = Event.move(curPlayerIndex, mapCoords.x, mapCoords.y, false, true, false);
					sendCommand(moveEvent);
					break;
				}
			}

			clearHighlights();
			currentAction = Action.ACTION_MOVE;
		} else if (currentAction == Action.ACTION_CHECK)
		{
			// check if a player is in the given direction
			if (target != null)
			{
				// check if that player is a valid check target
				if (getData().getTeamIndexOfPlayer(target) != getData().getTeamIndexOfPlayer(currentPlayer)
						&& target.getStatus() == Player.STS_OKAY)
				{
					int targetIndex = getData().getIndexOfPlayer(target);
					Event checkEvent = Event.check(curPlayerIndex, targetIndex, -2, false); // -2 just means we don't have a result yet
					sendCommand(checkEvent);
				}
			}
			
			clearHighlights();
			currentAction = Action.ACTION_MOVE;
		} else if (currentAction == Action.ACTION_HANDOFF)
		{
			if (target != null)
			{
				// check if that "something" is a valid handoff target
				if (getData().getTeamIndexOfPlayer(target) == getData().getTeamIndexOfPlayer(currentPlayer) 
						&& target.getStatus() == Player.STS_OKAY)
				{
					int targetIndex = getData().getIndexOfPlayer(target);
					Event handoffEvent = Event.handoff(curPlayerIndex, targetIndex, 0);
					sendCommand(handoffEvent);
				}
			}
			
			clearHighlights();
			currentAction = Action.ACTION_MOVE;
		}
	}

	private Point getMapCoordsFromCursorLocation(int mouseX, int mouseY)
	{
		System.out.println("Translating coordinates from (" + mouseX + ", " + mouseY + ")");
		
		// TODO: returns indices from 0 to 29 for both X and Y of the actual game grid, taking into account the viewport offset
		// a tile is 36x30 pixels, starting at 72, 60 (that is, there are two tiles worth of padding around the main map)
		
		int screenOriginX = mouseX + currentViewportOrigin.x;
		int screenOriginY = mouseY + currentViewportOrigin.y;

		int tileRow = screenOriginY / LegacyUiConstants.TILE_IMAGE_HEIGHT;
		int tileColumn = screenOriginX / LegacyUiConstants.TILE_IMAGE_WIDTH;
		
		return new Point(tileColumn - 1, tileRow - 1);		//column, row is X, Y...though the Y value is inverted (increasing as it gets lower on the screen)
	}
	
	private Point getOriginLocationWithTileAtCenter(Point tileMapCoords)
	{
		int centerX = 0 + 36 * tileMapCoords.y;	//getting y because it represents the column (so the X axis)		//TODO: not sure why this is 0 + 36, but okay
		int centerY = 30 + 30 * tileMapCoords.x;	//getting x because it represents the row (so the Y axis)
		
		//upper left corner of center tile should be at 302, 145
		int originX = centerX - 302 + 115;	//need to account for the sidebar, which is width 115		//TODO: the column zoom is close, but off by 1
		int originY = centerY - 145;
		
		if (originX < 0)
			originX = 0;
		if (originY < 0)
			originY = 0;
		if (originX > MAX_VIEWPORT_X)
			originX = MAX_VIEWPORT_X;
		if (originY > MAX_VIEWPORT_Y)
			originY = MAX_VIEWPORT_Y;
		
		return new Point(originX, originY);
	}
	
	private void clearHighlights()
	{
		highlights.clear();
		viewportImageFactory.clearHighlights();
	}
	
	private String getNextFacing(String facing)
	{
		if ("N".equals(facing))
			return "NE";
		if ("NE".equals(facing))
			return "E";
		if ("E".equals(facing))
			return "SE";
		if ("SE".equals(facing))
			return "S";
		if ("S".equals(facing))
			return "SW";
		if ("SW".equals(facing))
			return "W";
		if ("W".equals(facing))
			return "NW";
		if ("NW".equals(facing))
			return "N";
		
		return facing;
	}
	
	//TODO: reimplement scrolling later; the current way doesn't work well 
	/*
	private void scrollMapFromScrollCommand(String command)
	{
		boolean currentlyScrolling = (xScroll != 0 || yScroll != 0);
		
		if (command.equals("SCROLL_X-"))
			xScroll = -1;
		else if (command.equals("SCROLL_X0"))
			xScroll = 0;
		else if (command.equals("SCROLL_X+"))
			xScroll = 1;
		else if (command.equals("SCROLL_Y-"))
			yScroll = -1;
		else if (command.equals("SCROLL_Y0"))
			yScroll = 0;
		else if (command.equals("SCROLL_Y+"))
			yScroll = 1;
		
		if ((xScroll != 0 || yScroll != 0) && !currentlyScrolling)
			scrollMap();
	}
	
	private void scrollMap()
	{
		initiateScrolling();
		new ScrollTask().execute();
	}
	
	private void initiateScrolling()
	{
		scrollImage = viewportImageFactory.generateImage(getData(), currentPlayer);
//		 update viewport with appropriate section of that map as scrolling occurs
	}
		 
	private void stopScrolling()
	{
//		 take a snapshot of map base image at the current viewport coordinates (note that there might be a tile of overlap, and some sprites will be printed off-screen)
//		 print visible sprites on it as normal, trimming them as necessary
	}
	
	private class ScrollTask extends SwingWorker<Void, Void>
	{
		@Override
		protected Void doInBackground() throws Exception
		{
			while (!isCancelled()) {
				if ((currentViewportOrigin.x == 0 && xScroll < 0) || (currentViewportOrigin.x == MAX_VIEWPORT_X && xScroll > 0))
					xScroll = 0;
				if ((currentViewportOrigin.y == 0 && yScroll < 0) || (currentViewportOrigin.y == MAX_VIEWPORT_Y && yScroll > 0))
					yScroll = 0;
				
//				if ((currentViewportOrigin.x < scrollTarget.x && xScroll < 0) || (currentViewportOrigin.x > scrollTarget.x && xScroll > 0))
//					xScroll = 0;
//				if ((currentViewportOrigin.y < scrollTarget.y && yScroll < 0) || (currentViewportOrigin.y > scrollTarget.y && yScroll > 0))
//					yScroll = 0;
				
				if (xScroll == 0 && yScroll == 0)
				{
//					scrollTarget = new Point(0, 0);
					this.cancel(true);
				}
				
				snapToLocation(new Point(currentViewportOrigin.x + (xScroll * SCROLL_INCREMENT), currentViewportOrigin.y + (yScroll * SCROLL_INCREMENT)));
            }
			return null;

		}
		
		@Override
		protected void done()
		{
			stopScrolling();
		}
	}
	*/
}
