package main.data.load;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.data.entities.Equipment;
import main.data.entities.Player;
import main.data.entities.Team;

public class LegacyLeagueLoader extends ByteFileReader
{
	public static int MAX_TEAM_PLAYERS = 35;
	public static int TOTAL_DIVISIONS = 3;
	public static int TEAMS_PER_DIVISION = 4;
	
	public static List<Team> loadLeague(String fullPath)
	{
		List<Team> allTeams = new ArrayList<Team>();
		Team[][] teamsByDivision = new Team[3][4];
		
		File file = new File(fullPath);
		FileInputStream fis = null;
		DataInputStream dis = null;
		
		try
		{
			fis = new FileInputStream(file);
			dis = new DataInputStream(fis);
			
			for (int i = 0; i < TOTAL_DIVISIONS; i++)
			{
				for (int j = 0; j < TEAMS_PER_DIVISION; j++)
				{
					Team team = loadTeam(dis);
					allTeams.add(team);
					teamsByDivision[i][j] = team;
					
					scanBytes(dis, 2);
				}
			}
			
			dis.close();
			fis.close();

		} catch (IOException e)
		{
			e.printStackTrace();
			System.out.println("Exception when reading legacy team file at " + fullPath);
		}
		
		return allTeams;
	}
	
	public static Team loadTeam(DataInputStream dis) throws IOException
	{
		Team team = null;
		List<Player> playersOnTeam = new ArrayList<Player>();

		// System.out.println("Total file size to read (in bytes) : " + fis.available());

		for (int i = 0; i < MAX_TEAM_PLAYERS; i++)
		{
			Player player = LegacyTeamLoader.loadLegacyPlayer(dis);
			playersOnTeam.add(player);
		}

		team = LegacyTeamLoader.loadLegacyTeam(dis);

		for (Player player : playersOnTeam)
		{
			team.addPlayer(player);
		}
		
		assignGearToPlayers(team);

		return team;
	}
	
	//note that this takes a very uninformed approach to assigning equipment
	//it could end up giving a curmian bounder boots and vortex armor, which of course is asinine
	//ultimately, have a playerEquipmentAi that assigns gear based on personas
	private static void assignGearToPlayers(Team team)
	{
		int[] firstPlayerWithoutEquipment = {0, 0, 0, 0};
		
		for (Integer equipmentIndex : team.getEquipment())
		{
			Equipment equipment = Equipment.getEquipment(equipmentIndex);
			
			team.getPlayer(firstPlayerWithoutEquipment[equipment.type]).equipItem(equipment);
			firstPlayerWithoutEquipment[equipment.type]++;
		}
		
		team.getEquipment().clear();
	}
}
