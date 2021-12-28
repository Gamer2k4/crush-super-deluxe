package main.presentation.game;

import java.awt.Point;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import main.data.Event;
import main.data.entities.Player;
import main.data.entities.Race;
import main.logic.Client;
import main.logic.Server;
import main.presentation.audio.AudioManager;
import main.presentation.audio.SoundType;
import main.presentation.common.Logger;
import main.presentation.common.PlayerTextFactory;
import main.presentation.common.ScreenCommand;
import main.presentation.game.ejectionalert.EjectionAlert;
import main.presentation.game.ejectionalert.EquipmentEjectionAlert;
import main.presentation.game.ejectionalert.FatalityEjectionAlert;
import main.presentation.game.ejectionalert.InjuryEjectionAlert;
import main.presentation.game.ejectionalert.MutationEjectionAlert;
import main.presentation.game.sprite.CrushAnimatedTile;
import main.presentation.game.sprite.CrushPlayerSprite;
import main.presentation.game.sprite.CrushSprite;
import main.presentation.game.sprite.Facing;
import main.presentation.screens.CrushEventScreen;
import main.presentation.screens.GameScreen;

public class GdxGUI extends GameRunnerGUI
{
	private static final int EVENT_POLL_RATE_MS = 50;
	
	private CrushEventScreen gameScreen;
	
	private EventTextureFactory eventTextureFactory = EventTextureFactory.getInstance();
	private EventButtonBarFactory eventButtonBarFactory = EventButtonBarFactory.getInstance();
	private AudioManager audioManager = AudioManager.getInstance();
	
	private Timer eventPoller;
	private Timer delayTimer;
	
	private boolean delayTimerRunning = false;
	
	private boolean jumpQueued = false;
	
	private List<CrushAnimatedTile> activeOverlayAnimations = new ArrayList<CrushAnimatedTile>();
	private List<CrushPlayerSprite> activeSpriteAnimations = new ArrayList<CrushPlayerSprite>();
	
	private EjectionAlert activeEjectionAlert = null;
	private Event activeEjectEvent = null;
	
	public GdxGUI(Client client, ActionListener gameEndListener)
	{
		this(client, null, gameEndListener);
	}
	
	public GdxGUI(Client theClient, Server theServer, ActionListener gameEndListener)
	{
		super(theClient, theServer, gameEndListener);
		PlayerTextFactory.setGameDataImpl(getData());
		
		resumeEventPoller();
	}

	public void handleMinimapClick(Point minimapRowCol)
	{
		snapToTile(minimapRowCol);
	}
	
	@SuppressWarnings("incomplete-switch")
	public void handleCommand(ScreenCommand command)
	{
		Logger.debug("GUI - command received: " + command);

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
		System.out.println("\tGUI received event: " + event);
		
		if (event.getType() == Event.EVENT_TELE)
			handleTeleportEvent(event);
		else if (event.getType() == Event.EVENT_TURN)
			handleTurnEvent(event);
		else if (event.getType() == Event.EVENT_EJECT)
			handleEjectionEvent(event);
		else if (event.getType() == Event.EVENT_BIN)
			handleBinEvent(event);
		else if (event.getType() == Event.EVENT_MOVE)
			handleMoveEvent(event);
		else if (event.getType() == Event.EVENT_SHOCK)
			handleShockEvent(event);
		else if (event.getType() == Event.EVENT_STS)
			handleStatusEvent(event);
		else if (event.getType() == Event.EVENT_RECVR)
			handleRecoverEvent(event);
		else if (event.getType() == Event.EVENT_CHECK)
			handleCheckEvent(event);
		
		checkForNextEvents();
		gameScreen.refreshTextures();
	}

	private void handleTurnEvent(Event event)
	{
		if (event.getType() != Event.EVENT_TURN)
			return;
		
		currentAction = Action.ACTION_MOVE;
		curPlayerIndex = getFirstEligiblePlayer();
		snapToPlayer(getData().getPlayer(curPlayerIndex));
	}
	
	private void handleRecoverEvent(Event event)
	{
		if (event.getType() != Event.EVENT_RECVR)
			return;
		
		Player player = getData().getPlayer(event.flags[0]);
		int nextStatus = getData().getNextStateForRecoveringPlayer(player);
		
		if (player.status != nextStatus)
		{
			snapToPlayer(player);
			delay(500);
		}
		
		handleStatusEvent(Event.setStatus(event.flags[0], nextStatus));
		
		if (player.status != nextStatus)
			delay(500);
	}
	
	private void handleStatusEvent(Event event)
	{
		if (event.getType() != Event.EVENT_STS)
			return;
		
		Player player = getData().getPlayer(event.flags[0]);
		
		CrushPlayerSprite playerSprite = eventTextureFactory.getPlayerSprite(player);
		playerSprite.changeStatus(event);
	}

	private void handleShockEvent(Event event)
	{
		if (event.getType() != Event.EVENT_SHOCK)
			return;
		
		Player player = getData().getPlayer(event.flags[0]);
		
		CrushPlayerSprite playerSprite = eventTextureFactory.getPlayerSprite(player);
		playerSprite.shock();
		activeSpriteAnimations.add(playerSprite);
	}

	private void handleMoveEvent(Event event)
	{
		if (event.getType() != Event.EVENT_MOVE)
			return;
		
		Player player = getData().getPlayer(event.flags[0]);
		
		CrushPlayerSprite playerSprite = eventTextureFactory.getPlayerSprite(player);
		
		if (event.flags[4] == 0 && event.flags[5] == 0 && event.flags[6] == 0)
			playerSprite.walk(event);
		else if (event.flags[4] == 1 && event.flags[6] == 1)	//slide, knockdown
			playerSprite.knockbackKo(event);
		else if (event.flags[4] == 1)	//slide
			playerSprite.slide(event);
		else if (event.flags[5] == 1)	//jump
		{
			handleJump(event, playerSprite);
			return;
		}
		
		activeSpriteAnimations.add(playerSprite);
	}

	private void handleJump(Event event, CrushPlayerSprite playerSprite)
	{
		if (!jumpQueued)
		{
			jumpQueued = true;
			return;
		}
		
		jumpQueued = false;
		eventTextureFactory.elevateSprite(playerSprite);
		refreshInterface();
		playerSprite.jump(event);
		activeSpriteAnimations.add(playerSprite);
		waitForAnimationsToConclude();
		eventTextureFactory.lowerSprite(playerSprite);
		refreshInterface();
	}

	private void handleEjectionEvent(Event event)
	{
		if (event.getType() != Event.EVENT_EJECT)
			return;
		
		Player player = getData().getPlayer(event.flags[0]);
		CrushPlayerSprite playerSprite = eventTextureFactory.getPlayerSprite(player);
		
		if (event.flags[2] != Event.EJECT_BLOB && event.flags[2] != Event.EJECT_REF)
		{
			playerSprite.hurt();
			waitForDeathSoundToFinish(player.getRace());
		}
		
		showEjectionAlert(event);
	}

	private void waitForDeathSoundToFinish(Race race)
	{
		int delayMs = 0;
		
		if (race == Race.CURMIAN)
			delayMs = 450;
		else if (race == Race.DRAGORAN)
			delayMs = 1800;
		else if (race == Race.GRONK)
			delayMs = 1650;
		else if (race == Race.HUMAN)
			delayMs = 650;
		else if (race == Race.KURGAN)
			delayMs = 3350;
		else if (race == Race.NYNAX)
			delayMs = 800;
		else if (race == Race.SLITH)
			delayMs = 570;
		else if (race == Race.XJS9000)
			delayMs = 1350;
		
		delay(delayMs);
	}

	private void handleCheckEvent(Event event)
	{
		if (event.getType() != Event.EVENT_CHECK)
			return;
		
		//no need to go further if the "check" came from a trap or something
		if (event.flags[0] < 0)
			return;
		
		Player attacker = getData().getPlayer(event.flags[0]);
		Player defender = getData().getPlayer(event.flags[1]);
		
		Point attackerLocation = getData().getLocationOfPlayer(attacker);
		Point defenderLocation = getData().getLocationOfPlayer(defender);
		
		CrushPlayerSprite attackerSprite = eventTextureFactory.getPlayerSprite(attacker);
		CrushPlayerSprite defenderSprite = eventTextureFactory.getPlayerSprite(defender);
				
		attackerSprite.turnTowardArenaLocation(defenderLocation);
		defenderSprite.turnTowardArenaLocation(attackerLocation);
		
		eventTextureFactory.elevateSprite(attackerSprite);
		refreshInterface();
		
		if (event.flags[2] == Event.CHECK_DODGE)
		{
			defenderSprite.dodge();
			activeSpriteAnimations.add(defenderSprite);
		}
		
		attackerSprite.check();
		activeSpriteAnimations.add(attackerSprite);
		waitForAnimationsToConclude();
		
		eventTextureFactory.lowerSprite(attackerSprite);
		refreshInterface();
		
		//that should be it, since knockbacks, shocks, etc. are handled as separate events
	}

	@Override
	protected void handleTeleportEvent(Event event)
	{
		if (event.getType() != Event.EVENT_TELE)
			return;
		
		boolean isBlobEvent = (event.flags[2] == event.flags[3]);
		
		Player playerWarpingHere = getData().getPlayer(event.flags[0]);
		Point newPortalCoords = getData().getArena().getPortal(event.flags[3]);
		Point oldPortalCoords = new Point(-1, -1);
		
		if (event.flags[2] != -1)
			oldPortalCoords = getData().getArena().getPortal(event.flags[2]);
		
		
		System.out.println(playerWarpingHere.name + " is warping.  Current player at original portal is " + getData().getPlayerAtLocation(oldPortalCoords) + ", current player at new portal is " + getData().getPlayerAtLocation(newPortalCoords));
		System.out.println("\tCurrent sprite at original portal is " + eventTextureFactory.getPlayerSpriteAtCoords(oldPortalCoords) + ", current sprite at new portal is " + eventTextureFactory.getPlayerSpriteAtCoords(newPortalCoords));
		
		//if a player wasn't displaced but was teleporting normally, show a leaving animation from his original portal
		if (playerWarpingHere == getData().getPlayerAtLocation(oldPortalCoords))
			teleportPlayerOutFromTile(oldPortalCoords);
		
		if (!isBlobEvent)
			super.handleTeleportEvent(event);	//updates the current player and snaps to the tile
			
		teleportPlayerOutFromTile(newPortalCoords);

		if (!isBlobEvent)	//this is not a blob teleport
			teleportPlayerInToTile(newPortalCoords, playerWarpingHere);
		else
		{
			eventTextureFactory.updatePlayerSpriteCoords(playerWarpingHere, EventTextureFactory.OFFSCREEN_COORDS);
			Player playerWhoDisplacedTheBlobbingPlayer = getData().getPlayerAtLocation(oldPortalCoords);
			
			if (playerWhoDisplacedTheBlobbingPlayer != null && playerWhoDisplacedTheBlobbingPlayer != playerWarpingHere)
				eventTextureFactory.updatePlayerSpriteCoords(playerWhoDisplacedTheBlobbingPlayer, oldPortalCoords);
			//we need the second line because if the blob came as the result of a displacement, the displacing player's sprite
			//(the one who didn't blob, but forced the blob) is off in the ether someplace, so we need to relocate it to the arena
		}
		
		//TODO: if the player warping in doesn't have a source portal (first entry into the game), check for equipment, and, if necessary, display the alert, then warp them back out
		//	note that warping them out should be a generic "ejection" portal animation, because it happens for injuries too
		
		
		if (isBlobEvent)	//OR the player was ejected; basically if the player teleporting isn't eligible
			setActivePlayer(getFirstEligiblePlayer());
	}
	
	private void teleportPlayerOutFromTile(Point portalCoords)
	{
		CrushPlayerSprite spriteToDisplace = eventTextureFactory.getPlayerSpriteAtCoords(portalCoords);
		
		if (spriteToDisplace == null)
			return;
		
		showWarpAnimation(portalCoords);
		waitForAnimationsToConclude();
		eventTextureFactory.updatePlayerSpriteCoords(spriteToDisplace, EventTextureFactory.OFFSCREEN_COORDS);
	}
	
	private void teleportPlayerInToTile(Point portalCoords, Player playerWarpingHere)
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
		audioManager.playSound(SoundType.TP);
		refreshInterface();
	}
	
	private void handleBinEvent(Event event)
	{
		if (event.getType() != Event.EVENT_BIN)
			return;
		
		Player player = getData().getPlayer(event.flags[0]);
		int binIndex = event.flags[2];
		int result = event.flags[3];
		
		Point playerLocation = getData().getLocationOfPlayer(player);
		Point binLocation = getData().getArena().getBinLocation(binIndex);
		
		CrushPlayerSprite playerSprite = eventTextureFactory.getPlayerSpriteAtCoords(playerLocation);
		playerSprite.turnTowardArenaLocation(binLocation);
		
		//TODO: this is such an easy way to avoid the grey bin at the end of the animation cycle, but DON'T MODIFY DATA DIRECTLY!!!
		getData().getArena().setBinStatus(binIndex, result + 1);
		eventTextureFactory.refreshTileSprites();
		
		animateBin(binLocation);
		
		if (result == 1)
		{
			eventButtonBarFactory.setBallFound();
			audioManager.playSound(SoundType.SIREN);
			delay(250);
			playerSprite.receiveBall(event);
		}
		else
		{
			audioManager.playSound(SoundType.HORN);
			delay(1500);
		}
	}

	private void animateBin(Point binLocation)
	{
		Facing binFacing = eventTextureFactory.getBinSpriteFacing(binLocation);
		
		if (binFacing == null)
			return;
		
		animateBinPhase(binLocation, binFacing, 1);
		animateBinPhase(binLocation, binFacing, 2);
		animateBinPhase(binLocation, binFacing, 3);
	}
	
	private void animateBinPhase(Point binLocation, Facing facing, int phase)
	{
		CrushAnimatedTile binAnimation = CrushAnimatedTile.binAnimation(facing, phase);
		binAnimation.setArenaPosition(binLocation);
		activeOverlayAnimations.add(binAnimation);
		playBinDings(phase);
		refreshInterface();
		waitForAnimationsToConclude();
	}
	
	private void playBinDings(int phase)
	{
		final int frames = 12 / phase;
		final long frameDuration = (long)(1000 / frames);
		
		final Timer dingTimer = new Timer();
		
		TimerTask task = new TimerTask() {
	        int dingSoundsPlayed = 0;
			
			@Override
			public void run() {
	        	audioManager.playSound(SoundType.DING);
	        	dingSoundsPlayed++;
	        	
	        	if (dingSoundsPlayed >= frames)
	        		dingTimer.cancel();
	        }
	    };
	    
		dingTimer.scheduleAtFixedRate(task, 0, frameDuration);
	}

	private void showEjectionAlert(Event event)
	{
		if (event.getType() != Event.EVENT_EJECT)
			return;
		
		activeEjectEvent = event;
		
		if (event.flags[2] == Event.EJECT_BLOB)
		{
			audioManager.playSound(SoundType.MUTATE);
			activeEjectionAlert = new MutationEjectionAlert(getData(), event);
		}
		else if (event.flags[2] == Event.EJECT_DEATH)
		{
			audioManager.playSound(SoundType.FATAL);
			activeEjectionAlert = new FatalityEjectionAlert(getData(), event);
		}
		else if (event.flags[2] == Event.EJECT_SERIOUS || event.flags[2] == Event.EJECT_TRIVIAL)
		{
			audioManager.playSound(SoundType.INJVOC);
			activeEjectionAlert = new InjuryEjectionAlert(getData(), event);
		}
		else if (event.flags[2] == Event.EJECT_REF)
		{
			audioManager.playSound(SoundType.WHISTLE);
			activeEjectionAlert = new EquipmentEjectionAlert(getData(), event);
		}
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
		sprites.addAll(eventTextureFactory.getBallSprite());
		sprites.addAll(eventTextureFactory.getCursorSprite(currentPlayer));
		sprites.addAll(eventTextureFactory.getPlayerSprites());
		sprites.addAll(eventTextureFactory.getBallCarrierIndicator());
		sprites.addAll(eventTextureFactory.getHoveringSprites());
		sprites.addAll(eventTextureFactory.getTileHighlightSprites());
		sprites.addAll(activeOverlayAnimations);
		sprites.addAll(eventTextureFactory.getElevatedSprites());
		
		return sprites;
	}

	public List<GameText> getGameText()
	{
		List<GameText> texts = new ArrayList<GameText>();
		
		texts.addAll(eventButtonBarFactory.getPlayerTextInfo(currentPlayer));
		texts.addAll(eventButtonBarFactory.getPlayerAttributes(currentPlayer));
		texts.addAll(eventButtonBarFactory.getPlayerApStatuses(getData().getCurrentTeam()));
		
		if (showingEjectionAlert())
			texts.addAll(activeEjectionAlert.getInfoText());
		
		return texts;
	}

	public List<StaticImage> getStaticImages()
	{
		List<StaticImage> images = new ArrayList<StaticImage>();
		
		images.addAll(eventButtonBarFactory.getSelectedButton(currentAction));
		images.addAll(eventButtonBarFactory.getMinimap());
		images.addAll(eventButtonBarFactory.getTeamBanners());
		images.addAll(eventButtonBarFactory.getSelectedTeamAndPlayerIndicators(currentPlayer));
		images.addAll(eventButtonBarFactory.getPlayerStatuses());
		
		if (showingEjectionAlert())
		{
			images.add(activeEjectionAlert.getImage());
			images.add(activeEjectionAlert.getTextBox());
		}
			
		
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
				Logger.info("Polling client for next event.");
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
		
		eventTextureFactory.refreshTileSprites();
		refreshInterface();
		
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
		while (!activeOverlayAnimations.isEmpty() || !activeSpriteAnimations.isEmpty() || showingEjectionAlert() || delayTimerRunning)
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
			
			if (!animation.isActive())
			{
				try {
					activeOverlayAnimations.remove(i);
				} catch (IndexOutOfBoundsException ioobe)
				{
					Logger.warn("Index out of bounds exception when removing overlay animation!");
				}
				
				i--;
			}
		}
		
		for (int i = 0; i < activeSpriteAnimations.size(); i++)
		{
			CrushPlayerSprite animation = activeSpriteAnimations.get(i);
			
			if (!animation.isActive())
			{
				eventTextureFactory.refreshPlayerSpriteCoords(animation);
				
				try {
					activeSpriteAnimations.remove(i);
				} catch (IndexOutOfBoundsException ioobe)
				{
					Logger.warn("Index out of bounds exception when removing player animation!");
				}
				
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
		if (tileCoords == null)
			return;
		
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
		if (player == null)
			return;
		
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
	
	public void confirmEjectionAlert()
	{
		int playerIndex = activeEjectEvent.flags[0];
		int ejectType = activeEjectEvent.flags[2];
		activeEjectionAlert = null;
		activeEjectEvent = null;
		
		//don't need to teleport out a blobbed player, since he's already one with the void
		if (ejectType == Event.EJECT_BLOB)
			return;
		
		Player player = getData().getPlayer(playerIndex);
		CrushPlayerSprite spriteToEject = eventTextureFactory.getPlayerSprite(player);
		//TODO: the proper thing is to teleport the player out, but I think the animation gets screwy because the screen is already hung up waiting for the input to confirm the ejection
//		Point playerCoords = spriteToEject.getArenaPosition();
//		teleportPlayerOutFromTile(playerCoords);
		eventTextureFactory.updatePlayerSpriteCoords(spriteToEject, EventTextureFactory.OFFSCREEN_COORDS);
	}
	
	public boolean showingEjectionAlert()
	{
		if (activeEjectionAlert != null)
			return true;
		
		return false;
	}
	
	public boolean inputOkay()
	{
		removeFinishedAnimations();
		
		if (activeOverlayAnimations.isEmpty() && activeSpriteAnimations.isEmpty() && !delayTimerRunning)
			return true;
		
		return false;
		
//		return inputOkay;
	}
	
	private void delay(int durationInMs)
	{
		TimerTask task = new TimerTask() {
	        @Override
			public void run() {
	        	delayTimerRunning = false;
	        }
	    };
	    
		delayTimer = new Timer();
		delayTimer.schedule(task, durationInMs);
		delayTimerRunning = true;
		
		while (delayTimerRunning)
			refreshInterface();
	}
}
