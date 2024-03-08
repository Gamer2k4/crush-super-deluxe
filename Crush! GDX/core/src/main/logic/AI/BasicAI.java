package main.logic.ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import main.data.Data;
import main.data.Event;
import main.data.entities.Arena;
import main.data.entities.Player;
import main.logic.pathfinding.Pathfinder;
import main.presentation.common.Logger;

public class BasicAI implements AI
{
	private Data data;
	
	private List<Player> playersThatCannotAct = new ArrayList<Player>();
	private Event lastEvent = null;
	
	public BasicAI(Data data)
	{
		this.data = data;
	}
	
	@Override
	public Event generateEvent()
	{
		int currentPlayerIndex = getIndexOfNextPlayerToAct();
			
		if (currentPlayerIndex == -1)
			return endTurn();
		
		Player player = data.getPlayer(currentPlayerIndex);
		Point destination = determineDestinationTile(player);
		
		if (destination == null || (destination.x == -1 && destination.y == -1))
		{
			//TODO: i believe this comes up if the ballcarrier is killed
			Logger.error("BasicAI - Destination was " + destination + ", which should NEVER happen - LOOK INTO THIS AND FIX IT.");
			System.out.println("\tBall coords: " + data.getBallLocation());
			System.out.println("\tBallcarrier: " + data.getBallCarrier().saveAsText());
			
			if (data.getBallCarrier() != null)
				System.out.println("\tBallcarrier location: " + data.getLocationOfPlayer(data.getBallCarrier()));
				
			destination = new Point(1, 1);
		}
		
		Point target = getNextActionTarget(player, destination);
		Event event = determineActionForTargetTile(player, target);
		
		//if we would ever repeat a move event, something went wrong, so just kill the turn and move along
		if (event != null && event.getType() == Event.EVENT_MOVE && event.equals(lastEvent))
		{
			Logger.warn("Move event would be repeated; ending turn instead.");
			lastEvent = null;
			return endTurn();
		}
		
		lastEvent = event;
		return event;
	}

	protected Event endTurn()
	{
		playersThatCannotAct.clear();
		
		int curTeamIndex = data.getCurrentTeam();
		
		int player = curTeamIndex + 1;
		if (player == 3)
			player = 0;
		
		return Event.updateTurnPlayer(player);
	}

	private Event move(Player player, Point target)
	{
		if (target == null)
		{
			Logger.warn("Cannot generate a move event with a null target for Player [" + player.name + "].  Returning null event instead.");
			return null;
		}
		
		int playerIndex = data.getIndexOfPlayer(player);
		return Event.move(playerIndex, target.x, target.y, false, false, false);
	}

	private Event check(Player player, Player targetPlayer)
	{
		int playerIndex = data.getIndexOfPlayer(player);
		int targetIndex = data.getIndexOfPlayer(targetPlayer);
		return Event.check(playerIndex, targetIndex, -2, false);
	}
	
	protected int getIndexOfNextPlayerToAct()
	{
		int curTeamIndex = data.getCurrentTeam();
		int ballCarrierIndex = -1;
		
		Logger.debug("\tGetting index of next player to act for team index " + curTeamIndex);
		
		for (int i = 0; i < 9; i++)
		{
			int currentPlayerIndex = (9 * curTeamIndex) + i;
			Player player = data.getPlayer(currentPlayerIndex);
			
			if (player == null)
				continue;
			
			if (playersThatCannotAct.contains(player))
				continue;
			
			if (!player.isInGame())
				continue;
			
			if (player.currentAP < 10)
				continue;
			
			Logger.debug("\tLooks like " + player.name + " is ready to go.");
			
			if (player.equals(data.getBallCarrier()))
				ballCarrierIndex = currentPlayerIndex;
			else
				return currentPlayerIndex;
		}
		
		return ballCarrierIndex;	//ball carrier acts last
	}
	
	private Point determineDestinationTile(Player player)
	{
		Point playerCoords = data.getLocationOfPlayer(player);
		Arena arena = data.getArena();
		
		//TODO: this shouldn't happen - if a player has no location, why are they getting AI requests?
		if (playerCoords == null)
		{
			System.out.println("Null player coords!");
			System.out.println(player);
		}
		
		if (!arena.isBallFound())
			return getNearestPadCoords(arena, playerCoords);
		
		if (data.getBallCarrier() == null)
			return data.getBallLocation();
		
		if (data.getBallCarrier() == player)
			return arena.getGoalFarCorner();
		
		if (ballCarrierIsOpponent(player))
			return data.getLocationOfPlayer(data.getBallCarrier());
		
		return getNearestOpponentCoords(player);	//if nothing else applies, just run full speed at the nearest opponent
	}
	
	private boolean ballCarrierIsOpponent(Player player)
	{
		return data.playersAreOpponents(player, data.getBallCarrier());
	}
	
	//TODO: this CAN be correct, but often the pad with the shortest crow-flies distance actually requires one heck of a walk to get to
	private Point getNearestPadCoords(Arena arena, Point playerCoords)
	{
		int shortestDistance = Arena.ARENA_DIMENSIONS;
		Point nearestPadCoords = null;
		
		for (int i = 0; i < 8; i++)
		{
			int binStatus = arena.getBinStatus(i);
			
			if (binStatus == Arena.STATE_FAILED || binStatus == Arena.STATE_SUCCESS)
				continue;
			
			Point padLocation = arena.getPadLocation(i);
			int padDistance = distance(playerCoords, padLocation);
			
			if (padDistance > shortestDistance)
				continue;
			
			shortestDistance = padDistance;
			nearestPadCoords = new Point(padLocation.x, padLocation.y);
		}
		
		return nearestPadCoords;
	}
	
	private Point getNearestOpponentCoords(Player player)
	{
		int shortestDistance = Arena.ARENA_DIMENSIONS;
		Point nearestOpponentCoords = data.getArena().getGoalFarCorner();		//in case we can't find an opponent, default to running toward the goal instead
		Point playerCoords = data.getLocationOfPlayer(player);
		
		for (Player opponent : data.getAllPlayers())
		{
			if (opponent == null)
				continue;
			
			if (!data.playersAreOpponents(player, opponent))
				continue;
			
			if (!opponent.canBeChecked())
				continue;
			
			Point opponentCoords = data.getLocationOfPlayer(opponent);
			
			if (opponentCoords == null)
			{
				Logger.warn("BasicAI.getNearestOpponentCoords() - Opponent [" + opponent.name + "] has no location; skipping.");
				continue;
			}
			
			int opponentDistance = distance(playerCoords, opponentCoords);
			
			//TODO: potentially adjust opponent distance based on stats, rank, etc.
			//		that is, if two opponents are equally close, go for the weaker one (treat the stronger one as farther away)
			
			if (opponentDistance > shortestDistance)
				continue;
			
			shortestDistance = opponentDistance;
			nearestOpponentCoords = new Point(opponentCoords.x, opponentCoords.y);
		}
		
		Logger.debug("BasicAI.getNearestOpponentCoords() - No opponents found; moving to SE corner instead.");
		return nearestOpponentCoords;
	}

	private Point getNextActionTarget(Player player, Point destination)
	{
		boolean avoidPlayers = false;
		
		//avoid players if you're moving toward the ball
		if (destination.equals(data.getBallLocation()))
			avoidPlayers = true;
		
		//avoid players if you're the ball carrier
		if (player.equals(data.getBallCarrier()))
			avoidPlayers = true;
		
		//avoid players if you're just trying to get to a ball bin
		if (!data.getArena().isBallFound())
			avoidPlayers = true;
		
		return getNextActionTarget(player, destination, avoidPlayers);
	}
	
	private Point getNextActionTarget(Player player, Point destination, boolean avoidPlayers)
	{
		Point playerLocation = data.getLocationOfPlayer(player);
		
		if (playerLocation == null)
		{
			Logger.error("Cannot get next action target for Player [" + data.getTeamOfPlayer(player).teamName + "/" + player.name + "] due to a null player location; returning null point.");
			return null;
		}
		
		Pathfinder.setData(data);
		List<Point> path = Pathfinder.findPath(data.getArena(), data.getLocationOfPlayer(player), destination, avoidPlayers);
		
		if (path.isEmpty())
			return null;
		
		return path.get(0);
	}

	private Event determineActionForTargetTile(Player player, Point target)
	{
		Logger.debug("AI - Player " + player.name + " has a target of " + target);
		Logger.debug("\tPlayer is currently at " + data.getLocationOfPlayer(player));
		
		if (target == null)
		{
			Logger.warn("AI - no destination found for player " + player.name + "; adding player to ignore list.");
			playersThatCannotAct.add(player);
			return null;
		}
		
		Player targetPlayer = data.getPlayerAtLocation(target);
		
		if (targetPlayer == null)
			return move(player, target);
		
		if (player.canThrowCheck() && targetPlayer.canBeChecked() && data.playersAreOpponents(player, targetPlayer))
			return check(player, targetPlayer);
		
		playersThatCannotAct.add(player);
		return defaultMoveTowardGoal(player);	//if nothing else to do, move toward the goal	
	}
	
	private Event defaultMoveTowardGoal(Player player)
	{
		Point targetLocation = getNextActionTarget(player, data.getArena().getGoalFarCorner(), true);
		return move(player, targetLocation);
	}

	private int distance(Point point1, Point point2)
	{
		return distance(point1.x, point1.y, point2.x, point2.y);
	}
	
	private int distance(int x1, int y1, int x2, int y2)
	{
	    return (int)Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}
}
