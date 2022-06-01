package main.data;

import java.awt.Point;
import java.util.List;

import main.data.entities.Arena;
import main.data.entities.Player;
import main.data.entities.Stats;
import main.data.entities.Team;

public interface Data
{

	public Data clone();

	public void newGame(List<Team> allThreeTeams);

	public void newGame(List<Team> allThreeTeams, Integer fieldNum);
	
	public void endGame(int winningTeam);

	public int getNextStateForRecoveringPlayer(Player player);
	
	public void processEvent(Event theEvent);
	
	public boolean playersAreOpponents(Player player1, Player player2);

	public Stats getStatsOfPlayer(Player p);

	public Point getLocationOfPlayer(Player p);

	public Player getPlayerAtLocation(Point p);

	public Player getPlayerAtLocation(int x, int y);

	public int getTeamIndexOfPlayer(Player p);

	public Team getTeamOfPlayer(Player p);

	public int getIndexOfPlayer(Player p);

	public int getNumberOfPlayer(Player player);

	public int getCurrentTeam();

	public Arena getArena();

	public Player getPlayer(int index);
	
	public List<Player> getAllPlayers();

	public Team getTeam(int index);

	boolean isCurrentTeamHumanControlled();

	public Player getBallCarrier();

	public Point getBallLocation();

	public boolean isStateBallNotFound();

	public boolean isStateBallLoose();

	public boolean isStateOwnTeamHasBall();

	public boolean isStateOpponentHasBall();

	public boolean isGameDone();

	public List<Team> getTeams();

	public int getWinningTeamIndex();

	public int getTurnBallFound();

	public int getTurnBallScored();
	
	public boolean isGameActive();

	// TODO: DEBUG
	public void printMap();
}