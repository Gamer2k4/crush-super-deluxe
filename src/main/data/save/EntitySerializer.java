package main.data.save;

import java.awt.Color;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import main.data.entities.Equipment;
import main.data.entities.Player;
import main.data.entities.Stats;
import main.data.entities.Team;

public class EntitySerializer
{
	private static final int MAX_SKILLS = 30;
	private static final int MAX_PLAYER_NAME_LENGTH = 8;
	private static final int MAX_TEAM_NAME_LENGTH = 15;
	private static final int MAX_EQUIPMENT = 36;
	
	public static DataOutputStream serializePlayerToStream(DataOutputStream dos, Player player) throws IOException
	{
		if (player == null)
		{
			ByteFileWriter.padBytes(dos, 165);
			ByteFileWriter.writeByte(dos, 255);
			ByteFileWriter.writeByte(dos, 255);
			ByteFileWriter.writeByte(dos, 255);
			ByteFileWriter.writeByte(dos, 255);
			ByteFileWriter.padBytes(dos, 3);
			return dos;
		}
		
		ByteFileWriter.writeShortBytes(dos, player.getXP());
		ByteFileWriter.writeShortBytes(dos, player.getSkillPoints());
		ByteFileWriter.padBytes(dos, 2);
		ByteFileWriter.writeByte(dos, 1);	//TODO: unknown, but always appears as 1 in the save files
		ByteFileWriter.writeByte(dos, player.race);
		ByteFileWriter.padBytes(dos, 4);
		ByteFileWriter.writeByte(dos, player.getAttributeWithoutModifiers(Player.ATT_AP));	//currentAP, but the legacy files have this match the stat
		ByteFileWriter.writeByte(dos, player.getAttributeWithoutModifiers(Player.ATT_AP));
		ByteFileWriter.writeByte(dos, player.getAttributeWithoutModifiers(Player.ATT_RF));
		ByteFileWriter.writeByte(dos, player.getAttributeWithoutModifiers(Player.ATT_JP));
		ByteFileWriter.writeByte(dos, player.getAttributeWithoutModifiers(Player.ATT_CH));
		ByteFileWriter.writeByte(dos, player.getAttributeWithoutModifiers(Player.ATT_ST));
		ByteFileWriter.writeByte(dos, player.getAttributeWithoutModifiers(Player.ATT_TG));
		ByteFileWriter.writeByte(dos, player.getAttributeWithoutModifiers(Player.ATT_HD));
		ByteFileWriter.writeByte(dos, player.getAttributeWithoutModifiers(Player.ATT_DA));
		ByteFileWriter.padBytes(dos, 5);
		ByteFileWriter.writeString(dos, padString(player.name, MAX_PLAYER_NAME_LENGTH));
		ByteFileWriter.padBytes(dos, 3);
		ByteFileWriter.writeByte(dos, convertToLegacyWeeksOut(player.getWeeksOut()));
		ByteFileWriter.padBytes(dos, 16);
		ByteFileWriter.writeByte(dos, 1);	//TODO: seasons
		ByteFileWriter.padBytes(dos, 16);
		ByteFileWriter.writeByte(dos, 0);	//TODO: average rating
		ByteFileWriter.writeShortBytes(dos, player.getCareerStats().getStat(Stats.STATS_HIGHEST_RATING));
		
		ByteFileWriter.writeShortBytes(dos, player.getSeasonStats().getStat(Stats.STATS_RUSHING_YARDS));
		ByteFileWriter.writeShortBytes(dos, player.getSeasonStats().getStat(Stats.STATS_GOALS_SCORED));
		ByteFileWriter.writeShortBytes(dos, player.getSeasonStats().getStat(Stats.STATS_KILLS_FOR));
		ByteFileWriter.writeShortBytes(dos, player.getSeasonStats().getStat(Stats.STATS_INJURIES_FOR));
		ByteFileWriter.writeShortBytes(dos, player.getSeasonStats().getStat(Stats.STATS_INJURIES_AGAINST));
		ByteFileWriter.writeShortBytes(dos, player.getSeasonStats().getStat(Stats.STATS_CHECKS_THROWN));
		ByteFileWriter.writeShortBytes(dos, player.getSeasonStats().getStat(Stats.STATS_CHECKS_LANDED));
		ByteFileWriter.writeShortBytes(dos, player.getSeasonStats().getStat(Stats.STATS_PADS_ACTIVATED));
		ByteFileWriter.writeShortBytes(dos, player.getSeasonStats().getStat(Stats.STATS_FUMBLES));
		ByteFileWriter.writeShortBytes(dos, player.getSeasonStats().getStat(Stats.STATS_RUSHING_ATTEMPTS));
		ByteFileWriter.writeShortBytes(dos, player.getSeasonStats().getStat(Stats.STATS_SACKS_AGAINST));
		ByteFileWriter.writeShortBytes(dos, player.getSeasonStats().getStat(Stats.STATS_SACKS_FOR));
		
		ByteFileWriter.writeShortBytes(dos, player.getCareerStats().getStat(Stats.STATS_RUSHING_YARDS));
		ByteFileWriter.writeShortBytes(dos, player.getCareerStats().getStat(Stats.STATS_GOALS_SCORED));
		ByteFileWriter.writeShortBytes(dos, player.getCareerStats().getStat(Stats.STATS_KILLS_FOR));
		ByteFileWriter.writeShortBytes(dos, player.getCareerStats().getStat(Stats.STATS_INJURIES_FOR));
		ByteFileWriter.writeShortBytes(dos, player.getCareerStats().getStat(Stats.STATS_INJURIES_AGAINST));
		ByteFileWriter.writeShortBytes(dos, player.getCareerStats().getStat(Stats.STATS_CHECKS_THROWN));
		ByteFileWriter.writeShortBytes(dos, player.getCareerStats().getStat(Stats.STATS_CHECKS_LANDED));
		ByteFileWriter.writeShortBytes(dos, player.getCareerStats().getStat(Stats.STATS_PADS_ACTIVATED));
		ByteFileWriter.writeShortBytes(dos, player.getCareerStats().getStat(Stats.STATS_FUMBLES));
		ByteFileWriter.writeShortBytes(dos, player.getCareerStats().getStat(Stats.STATS_RUSHING_ATTEMPTS));
		ByteFileWriter.writeShortBytes(dos, player.getCareerStats().getStat(Stats.STATS_SACKS_AGAINST));
		ByteFileWriter.writeShortBytes(dos, player.getCareerStats().getStat(Stats.STATS_SACKS_FOR));
		
		writePlayerSkills(dos, player);
		writePlayerQuirks(dos, player);
		ByteFileWriter.writeByte(dos, convertToLegacyInjuryLevel(player.getInjuryType()));
		writePlayerEquipment(dos, player);
		
		ByteFileWriter.padBytes(dos, 3);
		
		return dos;
	}
	
	public static DataOutputStream serializeTeamToStream(DataOutputStream dos, Team team) throws IOException
	{
		for (int i = 0; i < Team.MAX_TEAM_SIZE; i++)
		{
			serializePlayerToStream(dos, team.getPlayer(i));
		}
		
		ByteFileWriter.writeByte(dos, convertToLegacyColorIndex(team.teamColors[0]));
		ByteFileWriter.writeByte(dos, convertToLegacyColorIndex(team.teamColors[1]));
		ByteFileWriter.padBytes(dos, 23);
		ByteFileWriter.writeString(dos, padString(team.teamName, MAX_TEAM_NAME_LENGTH));
		ByteFileWriter.writeString(dos, padString(team.coachName, MAX_TEAM_NAME_LENGTH));
		ByteFileWriter.padBytes(dos, 59);

		ByteFileWriter.writeShortBytes(dos, team.getSeasonStats().getStat(Stats.STATS_RUSHING_YARDS));
		ByteFileWriter.writeShortBytes(dos, team.getSeasonStats().getStat(Stats.STATS_KILLS_FOR));
		ByteFileWriter.writeShortBytes(dos, team.getSeasonStats().getStat(Stats.STATS_KILLS_AGAINST));
		ByteFileWriter.writeShortBytes(dos, team.getSeasonStats().getStat(Stats.STATS_INJURIES_FOR));
		ByteFileWriter.writeShortBytes(dos, team.getSeasonStats().getStat(Stats.STATS_INJURIES_AGAINST));
		ByteFileWriter.writeShortBytes(dos, team.getSeasonStats().getStat(Stats.STATS_CHECKS_THROWN));
		ByteFileWriter.writeShortBytes(dos, team.getSeasonStats().getStat(Stats.STATS_CHECKS_LANDED));
		ByteFileWriter.writeShortBytes(dos, team.getSeasonStats().getStat(Stats.STATS_PADS_ACTIVATED));
		ByteFileWriter.writeShortBytes(dos, 0);	//UNKNOWN
		ByteFileWriter.writeShortBytes(dos, team.getSeasonStats().getStat(Stats.STATS_FUMBLES));
		ByteFileWriter.writeShortBytes(dos, team.getSeasonStats().getStat(Stats.STATS_RUSHING_ATTEMPTS));
		ByteFileWriter.writeShortBytes(dos, team.getSeasonStats().getStat(Stats.STATS_BALL_CONTROL));
		ByteFileWriter.writeShortBytes(dos, team.getSeasonStats().getStat(Stats.STATS_SACKS_AGAINST));
		ByteFileWriter.writeShortBytes(dos, team.getSeasonStats().getStat(Stats.STATS_SACKS_FOR));
		ByteFileWriter.writeShortBytes(dos, team.getSeasonStats().getStat(Stats.STATS_EJECTIONS));
		ByteFileWriter.writeShortBytes(dos, team.getSeasonStats().getStat(Stats.STATS_MUTATIONS));
		ByteFileWriter.writeShortBytes(dos, team.getSeasonStats().getStat(Stats.STATS_WINS));
		ByteFileWriter.writeShortBytes(dos, team.getSeasonStats().getStat(Stats.STATS_LOSSES));
		ByteFileWriter.writeShortBytes(dos, team.getSeasonStats().getStat(Stats.STATS_TIES));

		ByteFileWriter.writeShortBytes(dos, team.getCareerStats().getStat(Stats.STATS_RUSHING_YARDS));
		ByteFileWriter.writeShortBytes(dos, team.getCareerStats().getStat(Stats.STATS_KILLS_FOR));
		ByteFileWriter.writeShortBytes(dos, team.getCareerStats().getStat(Stats.STATS_KILLS_AGAINST));
		ByteFileWriter.writeShortBytes(dos, team.getCareerStats().getStat(Stats.STATS_INJURIES_FOR));
		ByteFileWriter.writeShortBytes(dos, team.getCareerStats().getStat(Stats.STATS_INJURIES_AGAINST));
		ByteFileWriter.writeShortBytes(dos, team.getCareerStats().getStat(Stats.STATS_CHECKS_THROWN));
		ByteFileWriter.writeShortBytes(dos, team.getCareerStats().getStat(Stats.STATS_CHECKS_LANDED));
		ByteFileWriter.writeShortBytes(dos, team.getCareerStats().getStat(Stats.STATS_PADS_ACTIVATED));
		ByteFileWriter.writeShortBytes(dos, 0);	//UNKNOWN
		ByteFileWriter.writeShortBytes(dos, team.getCareerStats().getStat(Stats.STATS_FUMBLES));
		ByteFileWriter.writeShortBytes(dos, team.getCareerStats().getStat(Stats.STATS_RUSHING_ATTEMPTS));
		ByteFileWriter.writeShortBytes(dos, team.getCareerStats().getStat(Stats.STATS_BALL_CONTROL));
		ByteFileWriter.writeShortBytes(dos, team.getCareerStats().getStat(Stats.STATS_SACKS_AGAINST));
		ByteFileWriter.writeShortBytes(dos, team.getCareerStats().getStat(Stats.STATS_SACKS_FOR));
		ByteFileWriter.writeShortBytes(dos, team.getCareerStats().getStat(Stats.STATS_EJECTIONS));
		ByteFileWriter.writeShortBytes(dos, team.getCareerStats().getStat(Stats.STATS_MUTATIONS));
		ByteFileWriter.writeShortBytes(dos, team.getCareerStats().getStat(Stats.STATS_WINS));
		ByteFileWriter.writeShortBytes(dos, team.getCareerStats().getStat(Stats.STATS_LOSSES));
		ByteFileWriter.writeShortBytes(dos, team.getCareerStats().getStat(Stats.STATS_TIES));
		
		ByteFileWriter.padBytes(dos, 2);
		
		writeTeamDocbot(dos, team);
		writeTeamEquipment(dos, team);
		
		ByteFileWriter.writeByte(dos, team.homeField / 4);
		ByteFileWriter.writeByte(dos, team.homeField % 4);
		
		ByteFileWriter.padBytes(dos, 2);
		
		return dos;
	}
	
	private static void writePlayerSkills(DataOutputStream dos, Player player) throws IOException
	{
		List<Integer> skills = player.getSkillIndexList();
		skills.add(0, player.race + 100);	//TODO: racial ability hack: pass in the player's race and get back the skill of the racial ability
		
		for (int i = 0; i < MAX_SKILLS; i++)
		{
			int index = 0;
			
			if (i < skills.size())
				index = skills.get(i);
			
			ByteFileWriter.writeByte(dos, convertToLegacySkillIndex(index));
		}
	}
	
	//TODO: update when quirks are implemented
	private static void writePlayerQuirks(DataOutputStream dos, Player player) throws IOException
	{
		ByteFileWriter.padBytes(dos, 12);
	}

	private static void writePlayerEquipment(DataOutputStream dos, Player player) throws IOException
	{
		ByteFileWriter.writeByte(dos, convertToLegacyEquipmentIndex(player.getEquipment(Equipment.EQUIP_ARMOR)));
		ByteFileWriter.writeByte(dos, convertToLegacyEquipmentIndex(player.getEquipment(Equipment.EQUIP_BELT)));
		ByteFileWriter.writeByte(dos, convertToLegacyEquipmentIndex(player.getEquipment(Equipment.EQUIP_GLOVES)));
		ByteFileWriter.writeByte(dos, convertToLegacyEquipmentIndex(player.getEquipment(Equipment.EQUIP_BOOTS)));
	}
	
	private static void writeTeamDocbot(DataOutputStream dos, Team team) throws IOException
	{
		for (int i = 0; i < 4; i++)
		{
			ByteFileWriter.writeBoolean(dos, team.docbot[i]);
			ByteFileWriter.padBytes(dos, 3);
		}
	}
	
	private static void writeTeamEquipment(DataOutputStream dos, Team team) throws IOException
	{
		List<Integer> gear = team.getEquipment();
		
		for (int i = 0; i < MAX_EQUIPMENT; i++)
		{
			int index = 255;
			
			if (i < gear.size())
				index = gear.get(i);
			
			ByteFileWriter.writeByte(dos, index);
		}
	}
	
	private static String padString(String value, int maxLength)
	{
		String returnValue = value;
		
		while (returnValue.length() < maxLength)
		{
			returnValue = returnValue + (char)0;
		}
		
		return returnValue.substring(0, maxLength);
	}
	
	private static Integer convertToLegacyWeeksOut(int weeksOut)
	{
		if (weeksOut < 0)
			return 0;
		
		return weeksOut;
	}
	
	private static Integer convertToLegacySkillIndex(int skillIndex)
	{
		switch (skillIndex)
		{
		case 0:
			return 0;
		//if a race is passed in, return the index corresponding to their racial ability
		case Player.RACE_SLITH + 100:
			return 1;
		case Player.RACE_DRAGORAN + 100:
			return 2;
		case Player.RACE_CURMIAN + 100:
			return 3;
		case Player.RACE_GRONK + 100:
			return 4;
		case Player.RACE_KURGAN + 100:
			return 5;
		case Player.RACE_NYNAX + 100:
			return 6;
		case Player.RACE_XJS9000 + 100:
			return 7;
		case Player.RACE_HUMAN + 100:
			return 8;
		//otherwise just return the corresponding skill
		case Player.SKILL_TERROR:
			return 9;
		case Player.SKILL_JUGGERNAUT:
			return 10;
		case Player.SKILL_TACTICS:
			return 11;
		case Player.SKILL_VICIOUS:
			return 12;
		case Player.SKILL_BRUTAL:
			return 13;
		case Player.SKILL_CHECKMASTER:
			return 14;
		case Player.SKILL_STALWART:
			return 15;
		case Player.SKILL_GUARD:
			return 16;
		case Player.SKILL_RESILIENT:
			return 17;
		case Player.SKILL_CHARGE:
			return 18;
		case Player.SKILL_BOXING:
			return 19;
		case Player.SKILL_COMBO:
			return 20;
		case Player.SKILL_QUICKENING:
			return 21;
		case Player.SKILL_GYMNASTICS:
			return 22;
		case Player.SKILL_JUGGLING:
			return 23;
		case Player.SKILL_SCOOP:
			return 24;
		case Player.SKILL_STRIP:
			return 25;
		case Player.SKILL_JUDO:
			return 26;
		case Player.SKILL_FIST_OF_IRON:
			return 27;
		case Player.SKILL_DOOMSTRIKE:
			return 28;
		case Player.SKILL_AWE:
			return 29;
		case Player.SKILL_STOIC:
			return 30;
		case Player.SKILL_LEADER:
			return 31;
		case Player.SKILL_SENSEI:
			return 32;
		case Player.SKILL_SLY:
			return 33;
		case Player.SKILL_INTUITION:
			return 34;
		case Player.SKILL_HEALER:
			return 35;
		case Player.SKILL_KARMA:
			return 26;
		case Player.SKILL_NINJA_MASTER:
			return 37;
		}

		return -1;
	}
	
	private static Integer convertToLegacyInjuryLevel(int injuryLevel)
	{
		switch (injuryLevel)
		{
		case Player.INJURY_NONE:
			return 0;
		case Player.INJURY_TRIVIAL:
			return 1;
		case Player.INJURY_MINOR:
			return 2;
		}
		
		//multiple levels of crippling injury, so just return crippling regardless (since we've covered the other cases)
		return 3;
	}
	
	private static Integer convertToLegacyEquipmentIndex(int index)
	{
		if (index < 0)
			return 255;
		
		return index;
	}
	
	//TODO: update this with actual color matching (as best as possible)
	private static Integer convertToLegacyColorIndex(Color color)
	{
		return 3;	//yellow
	}
}
