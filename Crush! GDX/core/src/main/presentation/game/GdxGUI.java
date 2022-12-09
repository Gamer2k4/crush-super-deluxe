package main.presentation.game;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import main.data.Event;
import main.data.entities.Player;
import main.data.entities.Race;
import main.execute.DebugConstants;
import main.logic.Client;
import main.logic.Randomizer;
import main.logic.Server;
import main.presentation.audio.AudioManager;
import main.presentation.audio.SoundType;
import main.presentation.common.Logger;
import main.presentation.common.PlayerTextFactory;
import main.presentation.common.ScreenCommand;
import main.presentation.game.ejectionalert.EquipmentEjectionAlert;
import main.presentation.game.ejectionalert.FatalityEjectionAlert;
import main.presentation.game.ejectionalert.InjuryEjectionAlert;
import main.presentation.game.ejectionalert.MutationEjectionAlert;
import main.presentation.game.ejectionalert.NewTurnAlert;
import main.presentation.game.ejectionalert.PopupAlert;
import main.presentation.game.ejectionalert.VictoryAlert;
import main.presentation.game.sprite.CrushAnimatedTile;
import main.presentation.game.sprite.CrushPlayerSprite;
import main.presentation.game.sprite.CrushSprite;
import main.presentation.game.sprite.Facing;
import main.presentation.screens.CrushEventScreen;
import main.presentation.screens.GameScreen;

public class GdxGUI extends GameRunnerGUI implements ActionListener
{
	private static final int EVENT_POLL_RATE_MS = 50;
	
	private CrushEventScreen gameScreen;
	
	private EventTextureFactory eventTextureFactory = EventTextureFactory.getInstance();
	private EventButtonBarFactory eventButtonBarFactory = EventButtonBarFactory.getInstance();
	
	private Timer eventPoller;
	private Timer delayTimer;
	private javax.swing.Timer bgNoiseTimer = new javax.swing.Timer(15000, this);		//TODO: stop this when victory happens
	
	private boolean delayTimerRunning = false;
	private boolean jumpQueued = false;
	
	private List<CrushAnimatedTile> activeOverlayAnimations = new ArrayList<CrushAnimatedTile>();
	private List<CrushPlayerSprite> activeSpriteAnimations = new ArrayList<CrushPlayerSprite>();
	
	private PopupAlert activeAlert = null;
	private Event activeEjectEvent = null;
	private Player lastPlayerCameraSnappedTo = null;
	
	private NewTurnAlert[] newTurnAlerts = new NewTurnAlert[3];
	private boolean newTurnAlertReady = false;
	
	public GdxGUI(Client client, ActionListener gameEndListener)
	{
		this(client, null, gameEndListener);
	}
	
	public GdxGUI(Client theClient, Server theServer, ActionListener gameEndListener)
	{
		super(theClient, theServer, gameEndListener);
		PlayerTextFactory.setGameDataImpl(getData());
		bgNoiseTimer.stop();
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
		Logger.debug(" getData() returns data object with ID: " + getData().toString());

		switch (command)
		{
		case GAME_MOVE_ACTION:
			currentAction = Action.ACTION_MOVE;
			clearHighlights();
			break;
		case GAME_CHECK_ACTION:
			if (!canCurrentPlayerCheck())
				break;
			
			currentAction = Action.ACTION_CHECK;
			setHighlightsForCheck();
			break;
		case GAME_JUMP_ACTION:
			if (!canCurrentPlayerJump())
				break;
			
			currentAction = Action.ACTION_JUMP;
			setHighlightsForJump();
			break;
		case GAME_HANDOFF_ACTION:
			if (!canCurrentPlayerHandoff())
				break;
			
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
			ballCarrierHasActedAfterReceivingBall = false;
			
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
		Logger.debug("GUI received event: " + event);
		
		//don't process events if there's no active game
		//this freezes debugging and the like (since the event handler can't catch any exceptions, i think), so it's commented out for now
//		if (!gameStarted)
//			return;
		
		if (event.getType() != Event.EVENT_MOVE)
			lastPlayerCameraSnappedTo = null;
		
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
		else if (event.getType() == Event.EVENT_HANDOFF)
			handleHandoffEvent(event);
		else if (event.getType() == Event.EVENT_GETBALL)
			handleBallPickupEvent(event);
		else if (event.getType() == Event.EVENT_VICTORY)
			handleVictoryEvent(event);
		
		checkForNextEvents();
		gameScreen.refreshTextures();
	}
	
	private void handleVictoryEvent(Event event)
	{
		getData().processEvent(event);
		activeAlert = new VictoryAlert(getData(), event);
		
		if (event.flags[0] != -1)	//don't play the victory theme if it was a tie
			playSound(SoundType.VICTORY);
		
		startGameEndTimer();
	}

	private void handleTurnEvent(Event event)
	{
		if (event.getType() != Event.EVENT_TURN)
			return;
		
		curTeamIndex = event.flags[0];
		currentAction = Action.ACTION_MOVE;
		curPlayerIndex = getFirstEligiblePlayer();
		snapToPlayer(getData().getPlayer(curPlayerIndex));
		newTurnAlertReady = true;
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
		
		if (playerSprite == null)
		{
			Logger.warn("Cannot change status of sprite for player " + player + "; EventTextureFactory returned null sprite.");
			return;
		}
		
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
		
		//only snap to the first move; otherwise it's unpleasantly jarring
		if (player != lastPlayerCameraSnappedTo)
		{
			snapToPlayer(player);
			lastPlayerCameraSnappedTo = player;
		}
		
		if (player == getData().getBallCarrier() && event.flags[4] == 0 && event.flags[5] == 0 && event.flags[6] == 0)
			ballCarrierHasActedAfterReceivingBall = true;
		
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
		
		snapToPlayer(attacker);
		
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

	private void handleHandoffEvent(Event event)
	{
		if (event.getType() != Event.EVENT_HANDOFF)
			return;
		
		if (event.flags[2] == Event.HANDOFF_HURL)
		{
			handleHurlEvent(event.flags[0]);
			return;
		}
		
		Player giver = getData().getPlayer(event.flags[0]);
		Player getter = getData().getPlayer(event.flags[1]);
		
		Point giverLocation = getData().getLocationOfPlayer(giver);
		Point getterLocation = getData().getLocationOfPlayer(getter);
		
		CrushPlayerSprite giverSprite = eventTextureFactory.getPlayerSprite(giver);
		CrushPlayerSprite getterSprite = eventTextureFactory.getPlayerSprite(getter);
				
		giverSprite.turnTowardArenaLocation(getterLocation);
		getterSprite.turnTowardArenaLocation(giverLocation);
		
		refreshInterface();
		
		getterSprite.receiveBall();
		giverSprite.setHasBall(false);			//TODO: not working?
		activeSpriteAnimations.add(getterSprite);
		waitForAnimationsToConclude();
		
		if (event.flags[2] == Event.HANDOFF_FAIL)
			getterSprite.setHasBall(false);
		
		refreshInterface();
	}

	private void handleHurlEvent(int playerIndex)
	{
		Player player = getData().getPlayer(playerIndex);
		CrushPlayerSprite sprite = eventTextureFactory.getPlayerSprite(player);
		sprite.hurlBall();
		activeSpriteAnimations.add(sprite);
		waitForAnimationsToConclude();
		refreshInterface();
	}

	private void handleBallPickupEvent(Event event)
	{
		if (event.getType() != Event.EVENT_GETBALL)
			return;
		
		if (event.flags[2] == 0)
			return;
		
		Player player = getData().getPlayer(event.flags[0]);
		snapToPlayer(player);
		
		CrushPlayerSprite playerSprite = eventTextureFactory.getPlayerSprite(player);
		playerSprite.setHasBall(true);
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
		
		
		Logger.debug(playerWarpingHere.name + " is warping.  Current player at original portal is " + getData().getPlayerAtLocation(oldPortalCoords) + ", current player at new portal is " + getData().getPlayerAtLocation(newPortalCoords));
		Logger.debug("\tCurrent sprite at original portal is " + eventTextureFactory.getPlayerSpriteAtCoords(oldPortalCoords) + ", current sprite at new portal is " + eventTextureFactory.getPlayerSpriteAtCoords(newPortalCoords));
		
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
		if (DebugConstants.ABSTRACT_SIMULATION)
			return;
		
		CrushAnimatedTile warpAnimation = CrushAnimatedTile.warpAnimation();
		warpAnimation.setArenaPosition(portalCoords);
		activeOverlayAnimations.add(warpAnimation);
		playSound(SoundType.TP);
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
		
		snapToPlayer(player);
		
		CrushPlayerSprite playerSprite = eventTextureFactory.getPlayerSpriteAtCoords(playerLocation);
		playerSprite.turnTowardArenaLocation(binLocation);
		
		//TODO: this is such an easy way to avoid the grey bin at the end of the animation cycle, but DON'T MODIFY DATA DIRECTLY!!!
		getData().getArena().setBinStatus(binIndex, result + 1);
		eventTextureFactory.refreshTileSprites();
		
		animateBin(binLocation);
		
		if (result == 1)
		{
			eventButtonBarFactory.setBallFound();
			playSound(SoundType.SIREN);
			delay(250);
			playerSprite.receiveBall();
		}
		else
		{
			playSound(SoundType.HORN);
			delay(1500);
		}
	}

	private void animateBin(Point binLocation)
	{
		Facing binFacing = eventTextureFactory.getBinSpriteFacing(binLocation);
		
		if (binFacing == null)
			return;
		
		if (DebugConstants.ABSTRACT_SIMULATION)
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
	        	playSound(SoundType.DING);
	        	dingSoundsPlayed++;
	        	
	        	if (dingSoundsPlayed >= frames)
	        		dingTimer.cancel();
	        }
	    };
	    
		dingTimer.scheduleAtFixedRate(task, 0, frameDuration);
	}
	
	private void showNewTurnAlert(int currentTeamIndex)
	{
		activeAlert = newTurnAlerts[currentTeamIndex];
	}

	private void showEjectionAlert(Event event)
	{
		if (event.getType() != Event.EVENT_EJECT)
			return;
		
		//don't show alerts in an abstract simulation
		if (DebugConstants.ABSTRACT_SIMULATION)
			return;
		
		activeEjectEvent = event;
		
		if (event.flags[2] == Event.EJECT_BLOB)
		{
			playSound(SoundType.MUTATE);
			activeAlert = new MutationEjectionAlert(getData(), event);
		}
		else if (event.flags[2] == Event.EJECT_DEATH)
		{
			playSound(SoundType.FATAL);
			activeAlert = new FatalityEjectionAlert(getData(), event);
		}
		else if (event.flags[2] == Event.EJECT_SERIOUS || event.flags[2] == Event.EJECT_TRIVIAL)
		{
			playSound(SoundType.INJVOC);
			activeAlert = new InjuryEjectionAlert(getData(), event);
		}
		else if (event.flags[2] == Event.EJECT_REF)
		{
			playSound(SoundType.WHISTLE);
			activeAlert = new EquipmentEjectionAlert(getData(), event);
		}
	}

	@Override
	public void closeGUI()
	{
		eventTextureFactory.endGame();
		eventButtonBarFactory.endGame();
		refreshInterface();
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
		//don't refresh textures if the game isn't going
		if (!gameStarted)
			return;
		
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
		
		if (!gameStarted)
		{
			Logger.warn("GdxGUI - Game is not active; no game texts will be generated.");
			return texts;
		}
		
		texts.addAll(eventButtonBarFactory.getPlayerTextInfo(currentPlayer));
		texts.addAll(eventButtonBarFactory.getPlayerAttributes(currentPlayer));
		texts.addAll(eventButtonBarFactory.getPlayerApStatuses(getData().getCurrentTeam()));
		
		if (showingAlert())
			texts.addAll(activeAlert.getInfoText());
		
		return texts;
	}

	public List<StaticImage> getStaticImages()
	{
		List<StaticImage> images = new ArrayList<StaticImage>();
		
		if (!gameStarted || !gameScreen.isActive() || !eventButtonBarFactory.isActive())
		{
			Logger.warn("GdxGUI - Event is not active; no static images will be generated.");
			return images;
		}
		
		images.addAll(eventButtonBarFactory.getSelectedButton(currentAction));
		images.addAll(eventButtonBarFactory.getMinimap());
		images.addAll(eventButtonBarFactory.getTeamBanners());
		images.addAll(eventButtonBarFactory.getSelectedTeamAndPlayerIndicators(currentPlayer));
		images.addAll(eventButtonBarFactory.getPlayerStatuses());
		
		if (showingAlert())
		{
			images.add(activeAlert.getImage());
			images.add(activeAlert.getTextBox());
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
					
					try {
						receiveEvent(event);
					} catch (RuntimeException re)
					{
						if (re.getMessage() != null && re.getMessage().contains("No OpenGL context found in the current thread"))
						{
							Logger.warn("GdxGUI - Hiding OpenGL exception and repolling event.");
							return;
						}
						
						if (!DebugConstants.HIDE_EVENT_EXCEPTIONS)
							throw re;
						
						Logger.error("Runtime exception caught when polling for events; exception message was: " + re.getMessage());
						Logger.error("Event will be repolled.");
						return;
					}
					
					
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
			resumeEventPoller();
			
			if (!DebugConstants.ABSTRACT_SIMULATION && getData().isCurrentTeamHumanControlled() && newTurnAlertReady)
			{
				newTurnAlertReady = false;
				showNewTurnAlert(getData().getCurrentTeam());
			}
		}
		else
		{
			receiveEvent(nextEvent);
		}
	}
	
	private void waitForAnimationsToConclude()
	{
		while (!activeOverlayAnimations.isEmpty() || !activeSpriteAnimations.isEmpty() || showingAlert() || delayTimerRunning)
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
			CrushPlayerSprite animation = null;
			
			try {
				animation = activeSpriteAnimations.get(i);
			} catch (IndexOutOfBoundsException ioobe)
			{
				Logger.warn("Index out of bounds exception when removing player animation!");
				return;
			}
			
			if (animation == null)
			{
				Logger.warn("Null animation found when removing finished animations; removing the null value.");
				animation = new CrushPlayerSprite(null, null);	//this SHOULD cause it to hit the next if block, removing it
				System.out.println(animation.isActive());
			}
			
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
		Logger.debug("Tile map coords: " + tileMapCoords);
		int centerX = 36 + 36 * tileMapCoords.y; // getting y because it represents the column (so the X axis)
		int centerY = 30 + 30 * tileMapCoords.x; // getting x because it represents the row (so the Y axis)
		Logger.debug("Center XY: (" + centerX + ", " + centerY + ")");
		
		int originX = centerX + 18;
		int originY = 906 - centerY;
		
		Logger.debug("Camera XY: (" + originX + ", " + originY + ")");

		return new Point(originX, originY);
	}
	
	@Override
	public void beginGame()
	{
		generateNewTurnAlerts();
		eventTextureFactory.beginGame(getData());
		eventButtonBarFactory.beginGame(getData());
		super.beginGame();
		bgNoiseTimer.start();
	}
	
	@Override
	public void endGame()
	{
		super.endGame();
		bgNoiseTimer.stop();
		closeGUI();
		
		//this is intended to give the GUI enough time to close before sending the game end listener the command to end the game
		sendEndGameActionAfterDelay(50);
	}
	
	private void sendEndGameActionAfterDelay(int durationInMs)
	{
		TimerTask task = new TimerTask() {
	        @Override
			public void run()
	        {
	        	gameEndListener.actionPerformed(ScreenCommand.END_GAME.asActionEvent());
	        	System.out.println("Done!");
	        }
	    };
	    
	    System.out.println("Closing GUI...");
	    Timer endGameDelayTimer = new Timer();
		endGameDelayTimer.schedule(task, durationInMs);
	}
	
	//auto-click the game end alert after the music concludes
	private void startGameEndTimer()
	{
		long timerDuration = 12000;	//victory music lasts 11 seconds
		
		if (DebugConstants.ABSTRACT_SIMULATION)
			timerDuration = 500;	//but the screen transitions quite quickly during abstract simulation
		
		final Timer gameEnd = new Timer();
		
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
	        	//no need to end the game if it's not currently running
				if (!gameStarted)
	        		return;
				
				gameEnd.cancel();
				confirmAlert();
	        }
	    };
	    
	    gameEnd.schedule(task, timerDuration);
	}
	
	private void generateNewTurnAlerts()
	{
		for (int i = 0; i < 3; i++)
		{
			NewTurnAlert alert = new NewTurnAlert(getData().getTeam(i).teamName);
			newTurnAlerts[i] = alert;
		}
	}

	public void confirmAlert()
	{
		if (activeAlert.getClass() == VictoryAlert.class)
		{
			endGame();
			activeAlert = null;
			return;
		}
		
		activeAlert = null;
		
		//if we're only showing a new turn popup, we're done here
		if (activeEjectEvent == null)
			return;
		
		int playerIndex = activeEjectEvent.flags[0];
		int ejectType = activeEjectEvent.flags[2];
		
		activeAlert = null;
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
	
	public boolean showingAlert()
	{
		if (activeAlert != null)
			return true;
		
		return false;
	}
	
	public boolean inputOkay()
	{
		removeFinishedAnimations();
		
		if (activeOverlayAnimations.isEmpty() && activeSpriteAnimations.isEmpty() && !delayTimerRunning && getData().isCurrentTeamHumanControlled())
			return true;
		
		if (showingAlert())
			return true;
		
		return false;
		
//		return inputOkay;
	}
	
	private void delay(int durationInMs)
	{
		if (DebugConstants.ABSTRACT_SIMULATION)
			return;
		
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
	
	private void playSound(SoundType sound)
	{
		if (DebugConstants.ABSTRACT_SIMULATION)
			return;
		
		AudioManager.getInstance().playSound(sound);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		//this makes the sound play approximately every 45 seconds (the timers fires every 15 seconds), so there's some variance
		if (Randomizer.getRandomInt(1, 3) != 1)
			return;
		
		Logger.info("Playing background sound.");
		int backgroundTrack = Randomizer.getRandomInt(1, 6);
		
		if (backgroundTrack == 1)
			playSound(SoundType.ORGAN1);
		else if (backgroundTrack == 2)
			playSound(SoundType.ORGAN2);
		else if (backgroundTrack == 3)
			playSound(SoundType.ORGAN3);
		else if (backgroundTrack == 4)
			playSound(SoundType.HOTDOGS);
		else if (backgroundTrack == 5)
			playSound(SoundType.DRINKS);
		else if (backgroundTrack == 6)
			playSound(SoundType.CLAP);
	}
}
