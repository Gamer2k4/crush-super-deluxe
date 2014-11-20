package main.presentation.teameditor.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import main.data.TeamLoader;
import main.data.entities.Player;
import main.data.entities.Team;

public class TeamUpdater
{
	private Team team;
	private TeamImages teamImages;

	public TeamUpdater()
	{
		this(new Team());
	}
	
	public TeamUpdater(Team existingTeam)
	{
		team = existingTeam;
		teamImages = new TeamImages(team.teamColors[0], team.teamColors[1]);
	}

	public TeamUpdater(File loadPath)
	{
		loadTeam(loadPath);
	}

	public void loadTeam(File loadPath)
	{
		team = TeamLoader.loadTeamFromFile(loadPath);
		teamImages.updateColors(getMainColor(), getTrimColor());
	}

	public void saveTeam(File savePath)
	{
		TeamLoader.saveTeamToFile(team, savePath);
	}
	
	public Team getTeam()
	{
		return team;
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
		teamImages.updateColors(getMainColor(), getTrimColor());
	}

	public Color getTrimColor()
	{
		return team.teamColors[1];
	}

	public void setTrimColor(Color color)
	{
		team.teamColors[1] = color;
		teamImages.updateColors(getMainColor(), getTrimColor());
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
		return team.getValue();
	}
	
	public int getDocbotCost()
	{
		return team.getDocbotCost();
	}

	public boolean hasSensei()
	{
		for (int i = 0; i < 35; i++)
		{
			Player p = team.getPlayer(i);
			if (p != null && p.hasSkill(Player.SKILL_SENSEI))
				return true;
		}

		return false;
	}

	public BufferedImage getPlayerImage(int race)
	{
		return teamImages.getPlayerImage(race);
	}

	public BufferedImage getEquipmentImage(int equipment)
	{
		return teamImages.getEquipmentImage(equipment);
	}
	
	public void addEquipment(int equipmentIndex)
	{
		team.unassignedGear.add(equipmentIndex);
	}
	
	public Integer getEquipment(int equipmentIndex)
	{
		if (equipmentIndex < 0 || equipmentIndex > team.unassignedGear.size() - 1)
			return -1;
		
		return team.unassignedGear.get(equipmentIndex);
	}
	
	public Integer removeEquipment(int equipmentIndex)
	{
		if (equipmentIndex < 0 || equipmentIndex > team.unassignedGear.size() - 1)
			return -1;
		
		return team.unassignedGear.remove(equipmentIndex);
	}
	
	public void setDocbotTreatment(int index, boolean value)
	{
		team.docbot[index] = value;
	}

	public boolean pushPlayersForDraft(int startingIndex)
	{
		if (team.getPlayer(Team.MAX_TEAM_SIZE - 1) != null)
			return false;
		
		if (team.getPlayer(startingIndex) == null)
			return true;
		
		for (int i = Team.MAX_TEAM_SIZE - 1; i > startingIndex; i--)
		{
			Player playerToMove = team.getPlayer(i - 1);
			team.setPlayer(i, playerToMove);
		}
		
		team.setPlayer(startingIndex, null);
		
		return true;
	}
}
