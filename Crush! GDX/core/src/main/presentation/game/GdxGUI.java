package main.presentation.game;

import java.awt.Point;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import main.data.Event;
import main.data.entities.Player;
import main.logic.Client;
import main.logic.Server;
import main.presentation.audio.AudioManager;
import main.presentation.audio.SoundType;
import main.presentation.common.Logger;
import main.presentation.common.ScreenCommand;
import main.presentation.game.sprite.CrushAnimatedTile;
import main.presentation.game.sprite.CrushPlayerSprite;
import main.presentation.game.sprite.CrushSprite;
import main.presentation.screens.CrushEventScreen;
import main.presentation.screens.GameScreen;

public class GdxGUI extends GameRunnerGUI
{
	private static final int EVENT_POLL_RATE_MS = 50;
	
	private CrushEventScreen gameScreen;
	
	private EventTextureFactory eventTextureFactory = EventTextureFactory.getInstance();
	private EventButtonBarFactory eventButtonBarFactory = EventButtonBarFactory.getInstance();
	
	private Timer eventPoller;
	
	private List<CrushAnimatedTile> activeOverlayAnimations = new ArrayList<CrushAnimatedTile>();
	private List<CrushPlayerSprite> activeSpriteAnimations = new ArrayList<CrushPlayerSprite>();
	
	public GdxGUI(Client client, ActionListener gameEndListener)
	{
		this(client, null, gameEndListener);
	}
	
	public GdxGUI(Client theClient, Server theServer, ActionListener gameEndListener)
	{
		super(theClient, theServer, gameEndListener);
		
		resumeEventPoller();
	}
	
	@SuppressWarnings("incomplete-switch")
	public void handleCommand(ScreenCommand command)
	{
		System.out.println("GUI - command received: " + command);

		switch (command)
		{
		case GAME_MOVE_ACTION:
			currentAction = Action.ACTION_MOVE;
			clearHighlights();
			break;
		case GAME_CHECK_ACTION:
			currentAction = Action.ACTION_CHECK;
			setHighlightsForCheck();
			break;
		case GAME_JUMP_ACTION:
			currentAction = Action.ACTION_JUMP;
			setHighlightsForJump();
			break;
		case GAME_HANDOFF_ACTION:
			currentAction = Action.ACTION_HANDOFF;
			setHighlightsForHandoff();
			break;
		case GAME_END_TURN:
			clearHighlights();
			currentAction = Action.ACTION_END_TURN;
			int player = curTeamIndex + 1;
			if (player == 3)
				player = 0;
			curTeamIndex = player;
			
			sendCommand(Event.updateTurnPlayer(player));
			break;
		}

		if (command.isGameSelectPlayer())
			setActivePlayer(command.getCommandIndex());
	}
	
	@Override
	protected void setHighlightsForMove(Point destination)
	{
		super.setHighlightsForMove(destination);
		eventTextureFactory.setMoveTrack(movePossibilities);
		eventTextureFactory.setHighlights(highlights);
		refreshInterface();
	}
	
	@Override
	protected void setHighlightsForJump()
	{
		super.setHighlightsForJump();
		eventTextureFactory.setHighlights(highlights);
		refreshInterface();
	}
	
	@Override
	protected void setHighlightsForCheck()
	{
		super.setHighlightsForCheck();
		eventTextureFactory.setHighlights(highlights);
		refreshInterface();
	}
	
	@Override
	protected void setHighlightsForHandoff()
	{
		super.setHighlightsForHandoff();
		eventTextureFactory.setHighlights(highlights);
		refreshInterface();
	}
	
	@Override
	protected void setActivePlayer(int index)
	{
		int originalPlayer = curPlayerIndex;
		super.setActivePlayer(index);
		int newPlayer = curPlayerIndex;
		
		if (originalPlayer != newPlayer)
			clearHighlights();
	}

	@Override
	protected void refreshPlayerStatuses()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveEvent(Event event)
	{
		// TODO Auto-generated method stub
		System.out.println("\tGUI received event: " + event);
		
		activeSpriteAnimations.addAll(eventTextureFactory.animateEvent(event));
		
		if (event.getType() == Event.EVENT_TELE)
		{
			handleTeleportEvent(event);
		}
		else if (event.getType() == Event.EVENT_TURN)
		{
			currentAction = Action.ACTION_MOVE;
		}
		
		
		checkForNextEvents();
		gameScreen.refreshTextures();
	}
	
	@Override
	protected void handleTeleportEvent(Event event)
	{
		//actually, this might work out alright, and maybe it's even the reason why there are always two warp effect
		//animations.  do the first animation, move any sprite that's there to off-screen, move the warp animation off-screen,
		//move the player sprite warping in to that portal, move the warping animation back onscreen
		//also, if receiving an event where the source and destination portals are the same, pop up the mutation alert
		//and call it a day

		//TODO: don't call this for blob events, since there's no snap for them
		super.handleTeleportEvent(event);	//updates the current player and snaps to the tile
		
		//TODO: show the player departing their old portal (if this isn't their first entry into the game)
		//TODO: move warping out player offscreen; move warping in player to the portal
		
		//maybe for each teleport event, check if the sprite is on-screen.  if so, show the "teleport out" animation there and move it offscreen
		//the sprite WOULDN'T be on-screen if the player is warping in for the first time, or if they've been displaced
		
		Player playerWarpingHere = getData().getPlayer(event.flags[0]);
		Point portalCoords = getData().getArena().getPortal(event.flags[3]);
		teleportPlayerOutFromPortalTile(portalCoords);
			//I believe this is where we pop up the mutation alert for leaving players
			//however, this is tricky, because a player can also get blobbed if they're not being displaced, so somehow
			//check if the alert has already been shown
		teleportPlayerInToPortalTile(portalCoords, playerWarpingHere);
		//if the player warping in doesn't have a source portal (first entry into the game), check for equipment, and, if necessary, display the alert, then warp them back out
	}
	
	private void teleportPlayerOutFromPortalTile(Point portalCoords)
	{
		CrushPlayerSprite spriteToDisplace = eventTextureFactory.getPlayerSpriteAtCoords(portalCoords);
		
		if (spriteToDisplace == null)
			return;
		
		showWarpAnimation(portalCoords);
		waitForAnimationsToConclude();
		eventTextureFactory.updatePlayerSpriteCoords(spriteToDisplace, EventTextureFactory.OFFSCREEN_COORDS);
	}
	
	private void teleportPlayerInToPortalTile(Point portalCoords, Player playerWarpingHere)
	{
		showWarpAnimation(portalCoords);
		waitForAnimationsToConclude();
		eventTextureFactory.updatePlayerSpriteCoords(playerWarpingHere, portalCoords);
	}
	
	private void showWarpAnimation(Point portalCoords)
	{
		CrushAnimatedTile warpAnimation = CrushAnimatedTile.warpAnimation();
		warpAnimation.setArenaPosition(portalCoords);
		activeOverlayAnimations.add(warpAnimation);
		AudioManager.getInstance().playSound(SoundType.TP);
		refreshInterface();
	}

	@Override
	public void closeGUI()
	{
		// TODO Auto-generated method stub
		
	}
	
	private void clearHighlights()
	{
		highlights.clear();
		eventTextureFactory.clearHighlights();
		refreshInterface();
	}

	@Override
	public void refreshInterface()
	{
		gameScreen.refreshTextures();
	}

	public void setGameScreen(GameScreen screen)
	{
		gameScreen = (CrushEventScreen) screen;
		gameScreen.setGui(this);
	}

	public List<CrushSprite> getActiveSprites()
	{
		List<CrushSprite> sprites = new ArrayList<CrushSprite>();
		sprites.addAll(eventTextureFactory.getArenaSprite());
		sprites.addAll(eventTextureFactory.getTileSprites());
		sprites.addAll(eventTextureFactory.getCursorSprite(currentPlayer));
		sprites.addAll(eventTextureFactory.getPlayerSprites());
		sprites.addAll(eventTextureFactory.getTileHighlightSprites());
		sprites.addAll(activeOverlayAnimations);
		sprites.addAll(eventTextureFactory.getElevatedSprites());
		
		return sprites;
	}

	public List<GameText> getGameText()
	{
		return eventButtonBarFactory.getPlayerAndTeamName(currentPlayer);
	}

	public List<StaticImage> getStaticImages()
	{
		List<StaticImage> images = new ArrayList<StaticImage>();
		
		images.addAll(eventButtonBarFactory.getSelectedButton(currentAction));
		images.addAll(eventButtonBarFactory.getSelectedTeamAndPlayerIndicators(currentPlayer));
		images.addAll(eventButtonBarFactory.getPlayerStatuses());
		
		return images;
	}
	
	private void resumeEventPoller()
	{
		eventPoller = new Timer();

		TimerTask task = new TimerTask()
		{
			@Override
			public void run()
			{
				Logger.debug("Polling client for next event.");
				Event event = myClient.getNextEvent();
				if (event != null)
				{
					Logger.debug("\tEvent received: " + event);
					receiveEvent(event);
					eventPoller.cancel();
				}
			}
		};

		eventPoller.scheduleAtFixedRate(task, 0, EVENT_POLL_RATE_MS);
	}
	
	private void checkForNextEvents()
	{
		waitForAnimationsToConclude();
		
		myClient.processNextEvent();
		Event nextEvent = myClient.getNextEvent();
		
		if (nextEvent == null)
		{
//			snapToPlayer(getData().getPlayer(curPlayerIndex));	//TODO: seems like this would be a good idea but the original game doesn't do it
			resumeEventPoller();
		}
		else
		{
			receiveEvent(nextEvent);
		}
	}
	
	private void waitForAnimationsToConclude()
	{
		while (!activeOverlayAnimations.isEmpty() || !activeSpriteAnimations.isEmpty())
		{
			removeFinishedAnimations();
			refreshInterface();
		}
	}
	
	private void removeFinishedAnimations()
	{
		for (int i = 0; i < activeOverlayAnimations.size(); i++)
		{
			CrushAnimatedTile animation = activeOverlayAnimations.get(i);
			
			if (!animation.isActive() && !activeOverlayAnimations.isEmpty())
			{
				activeOverlayAnimations.remove(i);
				i--;
			}
		}
		
		for (int i = 0; i < activeSpriteAnimations.size(); i++)
		{
			CrushPlayerSprite animation = activeSpriteAnimations.get(i);
			
			if (!animation.isActive() && !activeSpriteAnimations.isEmpty())
			{
				eventTextureFactory.refreshPlayerSpriteCoords(animation);
				activeSpriteAnimations.remove(i);
				i--;
			}
		}
	}
	
	public void clickArenaLocation(int row, int col)
	{
		Player target = getData().getPlayerAtLocation(row, col);

		// clicking the current player rotates them
		if (currentAction != Action.ACTION_HANDOFF && currentPlayer.equals(target) && currentPlayer.status == Player.STS_OKAY)
		{
			currentAction = Action.ACTION_MOVE;
			CrushPlayerSprite spriteToRotate = eventTextureFactory.getPlayerSpriteAtCoords(row, col);
			spriteToRotate.rotateClockwise();
			clearHighlights();
			return;
		}

		// clicking another player on the same team switches to that player
		if (currentAction != Action.ACTION_HANDOFF
				&& getData().getTeamIndexOfPlayer(currentPlayer) == getData().getTeamIndexOfPlayer(target))
		{
			currentAction = Action.ACTION_MOVE;
			currentPlayer = target;
			curPlayerIndex = getData().getIndexOfPlayer(target);
			clearHighlights();
			snapToTile(new Point(row, col));
			return;
		}

		// check the current game state (is checking, is moving, etc) and act accordingly
		if (currentAction == Action.ACTION_MOVE)
		{
			for (Point possibility : movePossibilities)
			{
				if (row == possibility.x && col == possibility.y)
				{
					clearHighlights();
					Event moveEvent = Event.move(curPlayerIndex, row, col, false, false, false);
					sendCommand(moveEvent);
					return;
				}
			}

			if (currentPlayer.currentAP >= 10) // no move overlay if the player doesn't have the AP
			{
				setHighlightsForMove(new Point(row, col));
			}
		} else if (currentAction == Action.ACTION_JUMP)
		{
			Point playerCoords = getData().getLocationOfPlayer(currentPlayer);

			for (Point possibility : jumpPossibilities.values())
			{
				if (row == playerCoords.x + possibility.x && col == playerCoords.y + possibility.y)
				{
					Event moveEvent = Event.move(curPlayerIndex, row, col, false, true, false);
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

	@Override
	protected void snapToTile(Point tileCoords)
	{
		Logger.debug("zooming to location " + tileCoords);
		Point snapTargetXY = getOriginLocationWithTileAtCenter(tileCoords);
		gameScreen.setCameraPosition(snapTargetXY);
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
	
	private void snapToPlayer(Player player)
	{
		snapToTile(getData().getLocationOfPlayer(player));
	}
	
	private Point getOriginLocationWithTileAtCenter(Point tileMapCoords)
	{
		System.out.println("Tile map coords: " + tileMapCoords);
		int centerX = 36 + 36 * tileMapCoords.y; // getting y because it represents the column (so the X axis)
		int centerY = 30 + 30 * tileMapCoords.x; // getting x because it represents the row (so the Y axis)
		System.out.println("Center XY: (" + centerX + ", " + centerY + ")");
		
		int originX = centerX + 18;
		int originY = 906 - centerY;
		
		System.out.println("Camera XY: (" + originX + ", " + originY + ")");

		return new Point(originX, originY);
	}
	
	@Override
	public void beginGame()
	{
		eventTextureFactory.beginGame(getData());
		eventButtonBarFactory.beginGame(getData());
		super.beginGame();
	}
	
	public boolean inputOkay()
	{
		removeFinishedAnimations();
		
		if (activeOverlayAnimations.isEmpty() && activeSpriteAnimations.isEmpty())
			return true;
		
		return false;
		
//		return inputOkay;
	}
}
