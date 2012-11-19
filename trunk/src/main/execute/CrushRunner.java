package main.execute;

import java.util.ArrayList;
import java.util.List;

import main.data.entities.Player;
import main.data.entities.Team;
import main.logic.Client;
import main.logic.Server;

public class CrushRunner
{
	private static Server host;
	
	public static void main(String[] args)
	{
		host = new Server();
		new Client(host);
		
		host.newGame(createTeams(), 0);
	}
	
	private static List<Team> createTeams()
	{
		List<Team> theTeams = new ArrayList<Team>();
		
		Team team1 = new Team();
		Team team2 = new Team();
		Team team3 = new Team();
		
		team1.addPlayer(new Player(Player.RACE_HUMAN));
		team1.addPlayer(new Player(Player.RACE_HUMAN));
		
		team2.addPlayer(new Player(Player.RACE_CURMIAN));
		team2.addPlayer(new Player(Player.RACE_GRONK));
		
		
		team3.addPlayer(new Player(Player.RACE_XJS9000));
		
		team1.addPlayer(new Player(Player.RACE_XJS9000));
		team1.addPlayer(new Player(Player.RACE_XJS9000));
		team1.addPlayer(new Player(Player.RACE_XJS9000));
		team2.addPlayer(new Player(Player.RACE_XJS9000));
		team2.addPlayer(new Player(Player.RACE_XJS9000));
		team2.addPlayer(new Player(Player.RACE_XJS9000));
		team3.addPlayer(new Player(Player.RACE_XJS9000));
		team3.addPlayer(new Player(Player.RACE_XJS9000));

		team1.addPlayer(new Player(Player.RACE_CURMIAN));
		team1.addPlayer(new Player(Player.RACE_CURMIAN));
		team2.addPlayer(new Player(Player.RACE_CURMIAN));
		team2.addPlayer(new Player(Player.RACE_CURMIAN));
		team3.addPlayer(new Player(Player.RACE_CURMIAN));
		team3.addPlayer(new Player(Player.RACE_CURMIAN));
		
		theTeams.add(team1);
		theTeams.add(team2);
		theTeams.add(team3);
		
		return theTeams;
	}
}
