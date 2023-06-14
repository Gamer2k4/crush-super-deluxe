package main.logic.ai.coach;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import main.data.entities.Arena;
import main.data.entities.Equipment;
import main.data.entities.Player;
import main.data.entities.Race;
import main.data.entities.Skill;
import main.data.entities.SkillComparator;
import main.data.entities.Team;
import main.data.factory.PlayerFactory;
import main.logic.RandomGeneratorSingletonImpl;
import main.logic.ai.coach.player.DraftingAi;
import main.logic.ai.coach.player.DraftingAiType;
import main.logic.ai.coach.player.PlayerPersona;
import main.logic.ai.coach.skill.RandomSkillSelectionAi;
import main.logic.ai.coach.skill.SkillSelectionAi;
import main.presentation.common.Logger;

public class Coach
{
	protected EquipmentPackageAi equipmentAi;
	protected DocbotAi docbotAi;
	protected DraftingAi draftingAi;
	protected int preferredArena;
	
	public Coach()
	{
		equipmentAi = null;
		docbotAi = null;
		
		DraftingAiType[] draftTypes = DraftingAiType.values();
		int draftTypeIndex = RandomGeneratorSingletonImpl.getInstance().getRandomInt(0, draftTypes.length - 1);
		draftingAi = draftTypes[draftTypeIndex].getAi();
		preferredArena = Arena.ARENA_SAVANNA;
	}
	
	public Coach(DraftingAi draftingAi)
	{
		this();
		docbotAi = new DocbotAi();
		this.draftingAi = draftingAi;
	}
	
	public Team draftForTeam(Team team, int budget)
	{
		List<PlayerPersona> draftingOrder = draftingAi.getDraftingOrder();
		
		for (int i = 0; i < draftingOrder.size(); i++)
		{
			PlayerPersona persona = draftingOrder.get(i);
			
			if (persona.getRacePriority().isEmpty())
				persona.getRacePriority().addAll(Arrays.asList(Race.values()));
			
			Player draftedPlayer = PlayerFactory.getInstance().createPlayerWithRandomName(persona.getRacePriority().get(0));
			
			int[] idealEquipment = persona.getIdealEquipment();
			
			for (int j = 0; j < 4; j++)
			{
				int equipmentIndexToBuy = idealEquipment[j];
				
				if (equipmentIndexToBuy == Equipment.EQUIP_BLANK)
					continue;
				
				if (amountOfEquipmentTypeOwnedByTeam(Equipment.getType(equipmentIndexToBuy), team) < 9)
					team.getEquipment().add(equipmentIndexToBuy);
			}
			
			
			team.setPlayer(i, draftedPlayer);
		}
		
		return adjustTeamToMeetBudget(team, budget);
	}
	
	private int amountOfEquipmentTypeOwnedByTeam(int equipmentType, Team team)
	{
		List<Integer> ownedEquipment = team.getEquipment();
		int ownedAmountOfType = 0;
		
		for (int i = 0; i < ownedEquipment.size(); i++)
		{
			if (Equipment.getType(ownedEquipment.get(i)) == equipmentType)
				ownedAmountOfType++;
		}
		
		return ownedAmountOfType;
	}

	public Team adjustTeamToMeetBudget(Team team, int budget)
	{
		int overBudget = team.getValue() - budget;
		
		while (overBudget > 0)
		{
			overBudget -= fireBottomPlayer(team);
		}
		
		//TODO: maybe something more nuanced than just firing the bottom players - selling equipment, adjusting docbot, etc.
		
		return team;
	}
	
	private int fireBottomPlayer(Team team)
	{		
		for (int i = Team.MAX_TEAM_SIZE - 1; i >= 0; i--)
		{
			Player player = team.getPlayer(i);
			
			if (player == null)
				continue;
			
			team.unequipItemsFromPlayer(player);
			team.setPlayer(i, null);
			return player.getSalary();
		}
		
		Logger.warn("Team [" + team.teamName + "] has no players to fire!");
		return 900;	//avoid an infinite loop from the calling function: this had BETTER get the budget under control
	}
	
	public Team setLineup(Team team)
	{
		List<LineupEntry> lineup = extractLineupFromTeam(team);
		List<PlayerPersona> lineupOrder = draftingAi.getDraftingOrder();
		
		//TODO: find the best Egomaniac and put them in slot 1
		//		otherwise ignore egomaniacs completely (treat them as injured)
		
		//try to fill the lineup as specified first
		for (int i = 0; i < lineupOrder.size(); i++)
		{
			PlayerPersona persona = lineupOrder.get(i);
			List<Race> racePriority = persona.getRacePriority();
			int[] idealEquipment = persona.getIdealEquipment();
			
			Player player = getPlayerForLineupSlot(lineup, racePriority);
			//TODO: check for QUIRK_TECHNOPHOBIA
			
			for (int j = 0; j < 4; j++)
			{				
				if (player == null)
					break;
				
				if (player.getWeeksOut() > 0)
					break;
				
				int equipmentToLookFor = idealEquipment[j];
				
				if (equipmentToLookFor == Equipment.EQUIP_BLANK)
					continue;
				
				List<Integer> availableEquipment = team.getEquipment();
				
				for (int k = 0; k < availableEquipment.size(); k++)
				{
					if (equipmentToLookFor == availableEquipment.get(k))
					{
						player.equipItem(availableEquipment.remove(k));
						break;
					}
				}
			}
			
			team.setPlayer(i, player);	//everything should be blank at this point anyway, so filling a null with a null is okay too
		}
		
		//fill anything that's left
		for (int i = 0; i < Team.MAX_TEAM_SIZE; i++)
		{
			if (lineup.isEmpty())
				break;
			
			if (team.getPlayer(i) != null)
				continue;
			
			team.setPlayer(i, lineup.remove(0).getPlayer());	//lineup is sorted by XP, so the best player is filling each slot
		}
		
		condenseLineup(team);
		distributeUnusedEquipment(team);
		
		return team;
	}
	
	private void condenseLineup(Team team)
	{
		for (int i = 0; i < 9; i++)
		{
			Player player = team.getPlayer(i);
			
			//no need to replace a healthy player
			if (player != null && player.getWeeksOut() == 0)
				continue;
			
			//everyone should be already sorted in order of usefulness (more experienced players first, etc.), so this should be fine
			for (int j = i + 1; j < Team.MAX_TEAM_SIZE; j++)
			{
				Player replacementPlayer = team.getPlayer(j);
				
				if (replacementPlayer == null)
					continue;
				
				if (replacementPlayer.getWeeksOut() > 0)
					continue;
				
				team.setPlayer(i, replacementPlayer);
				team.setPlayer(j, player);
				break;
			}
		}
	}

	private void distributeUnusedEquipment(Team team)
	{
		for (int i = 0; i < 9; i++)
		{
			Player player = team.getPlayer(i);
			
			if (player == null)
				continue;
			
			if (player.getWeeksOut() > 0)
				continue;
			
			//TODO: check for QUIRK_TECHNOPHOBIA
			
			for (int j = 0; j < 4; j++)
			{				
				if (player.getEquipment(j) != Equipment.EQUIP_NONE)
					continue;
				
				List<Integer> availableEquipment = team.getEquipment();
				
				for (int k = 0; k < availableEquipment.size(); k++)
				{
					if (Equipment.getType(availableEquipment.get(k)) != j)
						continue;
					
					player.equipItem(availableEquipment.remove(k));
					break;
				}
			}
		}
	}

	private List<LineupEntry> extractLineupFromTeam(Team team)
	{
		List<LineupEntry> lineup = new ArrayList<LineupEntry>();
		
		for (int i = 0; i < Team.MAX_TEAM_SIZE; i++)
		{
			if (team.getPlayer(i) == null)
				continue;
			
			lineup.add(new LineupEntry(team.getPlayer(i)));
			team.setPlayer(i, null);
		}
		
		Collections.sort(lineup);
		return lineup;
	}
	
	private Player getPlayerForLineupSlot(List<LineupEntry> lineup, List<Race> racePriority)
	{
		for (Race race : racePriority)
		{
			for (LineupEntry entry : lineup)
			{
				Player player = entry.getPlayer();

				//injured players don't count
				if (player.getWeeksOut() > 0)
					continue;
				
				if (player.getRace() == race)
				{
					lineup.remove(entry);
					return player;
				}
			}
		}
		
		return null;
	}
	
	public Team spendPlayerXp(Team team)
	{
		List<PlayerPersona> draftingOrder = draftingAi.getDraftingOrder();
		
		for (int i = 0; i < Team.MAX_TEAM_SIZE; i++)
		{
			if (team.getPlayer(i) == null)
				continue;
			
			SkillSelectionAi skillsAi = new RandomSkillSelectionAi();
			
//			System.out.println("i: " + i + ", draftingOrder.size: " + draftingOrder.size());
			
			if (draftingOrder.size() > i)
				skillsAi = draftingOrder.get(i).getSkillAi();
			
//			System.out.println(skillsAi.getClass());
			
			spendXpOnSkills(team.getPlayer(i), skillsAi);
		}
		
		return team;
	}
	
	private void spendXpOnSkills(Player player, SkillSelectionAi skillsAi)
	{
		Skill nextSkill = null;
		
		do
		{
			nextSkill = skillsAi.getNextSkillToGain(player);
			player.purchaseSkill(nextSkill);
		} while (nextSkill != null);
		
		Collections.sort(player.getSkills(), new SkillComparator());
	}

	private class LineupEntry implements Comparable<LineupEntry>
	{
		private Player player;
		
		public LineupEntry(Player player)
		{
			this.player = player;
		}
		
		public Player getPlayer()
		{
			return player;
		}
		
		@Override
		public int compareTo(LineupEntry otherSlot)
		{
			Player otherPlayer = otherSlot.getPlayer();
			
			//first compare injuries; healthy players should always come first
			if (player.getWeeksOut() > otherPlayer.getWeeksOut())
				return 1;
			
			if (player.getWeeksOut() < otherPlayer.getWeeksOut())
				return -1;
			
			//next compare XP; reversed so the higher XP players are at the start of the list
			if (player.getXP() > otherPlayer.getXP())
				return -1;
			
			if (player.getXP() < otherPlayer.getXP())
				return 1;
			
			//compare race after that
			if (player.getRace().getIndex() < otherPlayer.getRace().getIndex())
				return -1;
			
			if (player.getRace().getIndex() > otherPlayer.getRace().getIndex())
				return 1;
			
			//finally compare name
			return player.name.compareTo(otherPlayer.name);
		}
		
	}
}
