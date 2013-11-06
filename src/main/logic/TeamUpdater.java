package main.logic;

import java.awt.Color;
import java.io.File;

import main.data.TeamLoader;
import main.data.entities.Equipment;
import main.data.entities.Player;
import main.data.entities.Team;

public class TeamUpdater
{
	private Team team;

	public TeamUpdater()
	{
		team = new Team();
		
		/*
		
		Player awesomeGronk = new Player(Player.RACE_GRONK);
		awesomeGronk.XP = 500;
		
		team.addPlayer(new Player(Player.RACE_DRAGORAN));
		team.addPlayer(new Player(Player.RACE_GRONK));
		team.addPlayer(awesomeGronk);
		team.addPlayer(new Player(Player.RACE_HUMAN));
		team.addPlayer(new Player(Player.RACE_KURGAN));
		team.addPlayer(new Player(Player.RACE_KURGAN));
		team.addPlayer(new Player(Player.RACE_SLITH));
		team.addPlayer(new Player(Player.RACE_SLITH));
		team.addPlayer(new Player(Player.RACE_HUMAN));
		team.addPlayer(new Player(Player.RACE_HUMAN));
		team.addPlayer(new Player(Player.RACE_XJS9000));
		team.addPlayer(new Player(Player.RACE_XJS9000));
		team.addPlayer(new Player(Player.RACE_XJS9000));
		team.addPlayer(new Player(Player.RACE_NYNAX));
		team.addPlayer(new Player(Player.RACE_NYNAX));
		team.addPlayer(new Player(Player.RACE_GRONK));
		team.addPlayer(new Player(Player.RACE_GRONK));
		team.addPlayer(new Player(Player.RACE_GRONK));
		
		team.teamColors[0] = Color.WHITE;
		team.teamColors[1] = Color.BLUE;
		
		team.coachName = "JARED B";
		team.teamName = "TEAM ICE";
		
		*/
	}
	
	public TeamUpdater(File loadPath)
	{
		loadTeam(loadPath);
	}
	
	public void loadTeam(File loadPath)
	{
		team = TeamLoader.loadTeamFromFile(loadPath);
	}
	
	public void saveTeam(File savePath)
	{
		TeamLoader.saveTeamToFile(team, savePath);
	}
	
	public String getTeamName()
	{
		return team.teamName;
	}
	
	public void setTeamName(String name)
	{
		team.teamName = name;
	}
	
	public String getTeamCoach()
	{
		return team.coachName;
	}
	
	public void setTeamCoach(String name)
	{
		team.coachName = name;
	}
	
	public Color getMainColor()
	{
		return team.teamColors[0];
	}
	
	public void setMainColor(Color color)
	{
		team.teamColors[0] = color;
	}
	
	public Color getTrimColor()
	{
		return team.teamColors[1];
	}
	
	public void setTrimColor(Color color)
	{
		team.teamColors[1] = color;
	}
	
	public int getHomeField()
	{
		return team.homeField;
	}
	
	public void setHomeField(int homeField)
	{
		team.homeField = homeField;
	}
	
	public Player getPlayer(int index)
	{
		return team.getPlayer(index);
	}
	
	public void setPlayer(int index, Player player)
	{
		team.setPlayer(index, player);
	}
	
	public int getTeamValue()
	{
		int teamValue = 0;
		
		for (int i = 0; i < 35; i++)
		{
			Player p = team.getPlayer(i);
			if (p != null)
			{
				teamValue += p.getSalary();
				
				for (int j = 0; j < 4; j++)
				{
					if (p.getEquipment(j) != Equipment.EQUIP_NONE)
					{
						Equipment eq = Equipment.getEquipment(p.getEquipment(j));
						teamValue += eq.cost;
					}
				}
			}
		}
		
		if (team.docbot[0])
			teamValue += 50;
		if (team.docbot[1])
			teamValue += 40;
		if (team.docbot[2])
			teamValue += 30;
		if (team.docbot[3])
			teamValue += 30;
		
		for (Integer equipmentIndex : team.unassignedGear)
		{
			teamValue += Equipment.getEquipment(equipmentIndex.intValue()).cost;
		}
		
		return teamValue;
	}
}
