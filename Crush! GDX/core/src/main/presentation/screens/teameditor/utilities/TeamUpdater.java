package main.presentation.screens.teameditor.utilities;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import main.data.TeamLoader;
import main.data.entities.Player;
import main.data.entities.Race;
import main.data.entities.Skill;
import main.data.entities.Team;
import main.presentation.TeamColorsManager;
import main.presentation.common.Logger;

public class TeamUpdater
{
	public static final String UPDATER_NEW_TEAM = "UPDATER_NEW_TEAM";
	public static final String UPDATER_PLAYER_SELECTION_CHANGED = "UPDATER_NEW_PLAYER_SELECTED";
	public static final String UPDATER_PLAYERS_CHANGED = "UPDATER_PLAYERS_CHANGED";
	public static final String UPDATER_EQUIPMENT_CHANGED = "UPDATER_EQUIPMENT_CHANGED";
	public static final String UPDATER_DOCBOT_CHANGED = "UPDATER_DOCBOT_CHANGED";
	
	private Team team;
	private TeamColorsManager teamColorsManager = TeamColorsManager.getInstance();

	private int currentPlayerIndex = 0;
	
	private JButton newTeamUpdater;
	private JButton playerSelectionUpdater;
	private JButton playersChangedUpdater;
	private JButton equipmentChangedUpdater;
	private JButton docbotChangedUpdater;

	public TeamUpdater()
	{
		this(new Team());
	}
	
	public TeamUpdater(Team existingTeam)
	{
		team = existingTeam;
		newTeamUpdater = new JButton();
		newTeamUpdater.setActionCommand(UPDATER_NEW_TEAM);
		playerSelectionUpdater = new JButton();
		playerSelectionUpdater.setActionCommand(UPDATER_PLAYER_SELECTION_CHANGED);
		playersChangedUpdater = new JButton();
		playersChangedUpdater.setActionCommand(UPDATER_PLAYERS_CHANGED);
		equipmentChangedUpdater = new JButton();
		equipmentChangedUpdater.setActionCommand(UPDATER_EQUIPMENT_CHANGED);
		docbotChangedUpdater = new JButton();
		docbotChangedUpdater.setActionCommand(UPDATER_DOCBOT_CHANGED);
	}

	public TeamUpdater(File loadPath)
	{
		loadTeam(loadPath);
	}

	public void loadTeam(File loadPath)
	{
		loadTeam(TeamLoader.loadTeamFromFile(loadPath));
	}
	
	public void loadTeam(Team newTeam)
	{
		long startTime = System.nanoTime();
		team = newTeam;
		newTeamUpdater.doClick();
		long elapsedTime = System.nanoTime() - startTime;
		Logger.info("Total execution time to load team in millis: " + elapsedTime/1000000);
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
		playersChangedUpdater.doClick();
	}
	
	public Player firePlayer(int index)
	{
		return firePlayer(index, false);
	}
	
	public Player firePlayer(int index, boolean playerKeepsEquipment)
	{
		Player player = getPlayer(index);

		// doesn't do anything if there's no one to fire
		if (player == null)
			return null;
		
		// clear the slot
		setPlayer(index, null);
		
		if (playerKeepsEquipment)
			return player;
		
		// give all the equipment back to the team
		for (int i = 0; i < 4; i++)
		{
			if (player.getEquipment(i) >= 0)
			{
				addEquipment(player.unequipItem(i));
			}
		}
		
		return player;
	}
	
	public boolean hirePlayer(int playerIndex, Player player, int budgetLimit)
	{
		int budget = budgetLimit - team.getValue();
		int playerCost = player.getSalary();

		if (budget < playerCost)
			return false;

		boolean canDraft = pushPlayersForDraft(playerIndex);

		if (canDraft)
			setPlayer(playerIndex, player);
		
		return canDraft;
	}
	
	public boolean hirePlayerAndAdvanceSlot(Player player, int budgetLimit)
	{
		boolean playerDrafted = hirePlayer(currentPlayerIndex, player, budgetLimit);
		
		if (playerDrafted)
			selectNextPlayer();
		
		return playerDrafted;
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
			if (p != null && p.hasSkill(Skill.SENSEI))
				return true;
		}

		return false;
	}

	public Texture getPlayerImage(Race race)
	{
		return teamColorsManager.getPlayerImage(team, race);
	}

	public Drawable getEquipmentImage(int equipment)
	{
		return teamColorsManager.getEquipmentImage(team, equipment);
	}
	
	public Texture getHelmetImage()
	{
		return teamColorsManager.getHelmetImage(team);
	}
	
	public void addEquipment(int equipmentIndex)
	{
		team.getEquipment().add(equipmentIndex);
		equipmentChangedUpdater.doClick();
	}
	
	public Integer getEquipment(int equipmentIndex)
	{
		if (equipmentIndex < 0 || equipmentIndex > team.getEquipment().size() - 1)
			return -1;
		
		return team.getEquipment().get(equipmentIndex);
	}
	
	public Integer removeEquipment(int equipmentIndex)
	{
		if (equipmentIndex < 0 || equipmentIndex > team.getEquipment().size() - 1)
			return -1;
		
		Integer returnedEquipment = team.getEquipment().remove(equipmentIndex);
		
		equipmentChangedUpdater.doClick();
		
		return returnedEquipment;
	}
	
	public void setDocbotTreatment(int index, boolean value)
	{
		team.docbot[index] = value;
		docbotChangedUpdater.doClick();
	}

	public boolean pushPlayersForDraft(int startingIndex)
	{
		if (team.getPlayer(startingIndex) == null)
			return true;
		
		if (team.getPlayer(Team.MAX_TEAM_SIZE - 1) != null)
			return false;
		
		for (int i = Team.MAX_TEAM_SIZE - 1; i > startingIndex; i--)
		{
			Player playerToMove = team.getPlayer(i - 1);
			team.setPlayer(i, playerToMove);
		}
		
		team.setPlayer(startingIndex, null);
		
		return true;
	}

	public void swapPlayers(int firstIndex, int secondIndex)
	{
		Player player1 = getPlayer(firstIndex);
		Player player2 = getPlayer(secondIndex);

		setPlayer(firstIndex, player2);
		setPlayer(secondIndex, player1);
	}
	
	public void addUpdateListener(ActionListener listener)
	{
		newTeamUpdater.addActionListener(listener);
		playerSelectionUpdater.addActionListener(listener);
		playersChangedUpdater.addActionListener(listener);
		equipmentChangedUpdater.addActionListener(listener);
		docbotChangedUpdater.addActionListener(listener);
	}

	public int getCurrentPlayerIndex()
	{
		return currentPlayerIndex;
	}

	public void setCurrentPlayerIndex(int currentPlayerIndex)
	{
		this.currentPlayerIndex = currentPlayerIndex;
		playerSelectionUpdater.doClick();
	}
	
	public void selectPreviousPlayer()
	{
		if (currentPlayerIndex <= 0)
			return;
		
		currentPlayerIndex--;
		playerSelectionUpdater.doClick();
	}
	
	public void selectNextPlayer()
	{
		if (currentPlayerIndex >= Team.MAX_TEAM_SIZE - 1)
			return;
		
		currentPlayerIndex++;
		playerSelectionUpdater.doClick();
	}
}
