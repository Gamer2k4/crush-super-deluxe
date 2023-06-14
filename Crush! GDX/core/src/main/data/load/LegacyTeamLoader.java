package main.data.load;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.data.entities.Equipment;
import main.data.entities.Player;
import main.data.entities.Race;
import main.data.entities.Skill;
import main.data.entities.Stats;
import main.data.entities.Team;
import main.data.factory.PlayerFactory;

public class LegacyTeamLoader extends ByteFileReader
{
	// still need to find the following:
	// budget, but it's trivial, since the budget is calculated by the team editor and irrelevant elsewhere
	// stats too, but i think the league takes care of the team-level ones
	public static Team loadTeam(String fullPath)
	{
		Team team = null;
		List<Player> playersOnTeam = new ArrayList<Player>();

		int MAX_TEAM_PLAYERS = 35;

		File file = new File(fullPath);
		FileInputStream fis = null;
		DataInputStream dis = null;

		try
		{
			fis = new FileInputStream(file);
			dis = new DataInputStream(fis);

			// System.out.println("Total file size to read (in bytes) : " + fis.available());

			for (int i = 0; i < MAX_TEAM_PLAYERS; i++)
			{
				Player player = loadLegacyPlayer(dis);
				playersOnTeam.add(player);
			}

			team = loadLegacyTeam(dis);

			for (Player player : playersOnTeam)
			{
				team.addPlayer(player);
			}

			dis.close();
			fis.close();

		} catch (IOException e)
		{
			e.printStackTrace();
			System.out.println("Exception when reading legacy team file at " + fullPath);
		}

		return team;
	}

	public static Player loadLegacyPlayer(DataInputStream dis) throws IOException
	{
		int PLAYER_NAME_LENGTH = 8;
		int MAX_SKILLS = 30;
		int MAX_QUIRKS = 12;

		int[] statMap = { Player.ATT_AP, Player.ATT_RF, Player.ATT_JP, Player.ATT_CH, Player.ATT_ST, Player.ATT_TG, Player.ATT_HD,
				Player.ATT_DA };
		int[] injuryMap = { Player.INJURY_NONE, Player.INJURY_TRIVIAL, Player.INJURY_MINOR, Player.INJURY_CRIPPLING };

		Player player = PlayerFactory.getInstance().createEmptyPlayer();

		int totalValue = readShortBytes(dis);
		int skillPointsAvailable = readShortBytes(dis);

		scanBytes(dis, 3);

		int race = readUnsignedByte(dis);

		scanBytes(dis, 4);
		readUnsignedByte(dis); // current AP

		// These are already enhanced by skills - that is, a Dragoran with Quickening will have his AP listed as 80 here.
		// However, worn equipment does NOT add to these skills.
		// All skills will be adjusted appropriately when the skills are read in.
		for (int i = 0; i < 8; i++)
		{
			int stat = readUnsignedByte(dis);

			if (stat == 0) // lowest possible stat is 1, so 0 means the player doesn't exist
			{
				scanBytes(dis, 158); // scan through the rest of the player entry and return null
				return null;
			}

			player.setAttribute(statMap[i], stat);
		}

		scanBytes(dis, 5);

		String name = "";

		for (int i = 0; i < PLAYER_NAME_LENGTH; i++)
		{
			char c = readCharByte(dis);

			if (c == 0)
				c = ' ';

			name = name + c;
		}

		name = name.trim();

		scanBytes(dis, 3); // UNKNOWN

		int weeksOut = readUnsignedByte(dis);

		scanBytes(dis, 14); // UNKNOWN

		int gamesPlayed = readShortBytes(dis);
		int totalSeasons = readUnsignedByte(dis);

		scanBytes(dis, 16); // likely in-game stats
		
		readUnsignedByte(dis); // average rating
		player.getCareerStats().setStat(Stats.STATS_HIGHEST_RATING, readShortBytes(dis)); // highest rating

		// season stats
		player.getSeasonStats().setStat(Stats.STATS_RUSHING_YARDS, readShortBytes(dis));
		player.getSeasonStats().setStat(Stats.STATS_GOALS_SCORED, readShortBytes(dis));	
		player.getSeasonStats().setStat(Stats.STATS_KILLS_FOR, readShortBytes(dis));
		player.getSeasonStats().setStat(Stats.STATS_INJURIES_FOR, readShortBytes(dis));
		player.getSeasonStats().setStat(Stats.STATS_INJURIES_AGAINST, readShortBytes(dis));
		player.getSeasonStats().setStat(Stats.STATS_CHECKS_THROWN, readShortBytes(dis));
		player.getSeasonStats().setStat(Stats.STATS_CHECKS_LANDED, readShortBytes(dis));
		player.getSeasonStats().setStat(Stats.STATS_PADS_ACTIVATED, readShortBytes(dis));
		player.getSeasonStats().setStat(Stats.STATS_FUMBLES, readShortBytes(dis));
		player.getSeasonStats().setStat(Stats.STATS_RUSHING_ATTEMPTS, readShortBytes(dis));
		player.getSeasonStats().setStat(Stats.STATS_SACKS_AGAINST, readShortBytes(dis));
		player.getSeasonStats().setStat(Stats.STATS_SACKS_FOR, readShortBytes(dis));
		
		// career stats
		player.getCareerStats().setStat(Stats.STATS_RUSHING_YARDS, readShortBytes(dis));
		player.getCareerStats().setStat(Stats.STATS_GOALS_SCORED, readShortBytes(dis));	
		player.getCareerStats().setStat(Stats.STATS_KILLS_FOR, readShortBytes(dis));
		player.getCareerStats().setStat(Stats.STATS_INJURIES_FOR, readShortBytes(dis));
		player.getCareerStats().setStat(Stats.STATS_INJURIES_AGAINST, readShortBytes(dis));
		player.getCareerStats().setStat(Stats.STATS_CHECKS_THROWN, readShortBytes(dis));
		player.getCareerStats().setStat(Stats.STATS_CHECKS_LANDED, readShortBytes(dis));
		player.getCareerStats().setStat(Stats.STATS_PADS_ACTIVATED, readShortBytes(dis));
		player.getCareerStats().setStat(Stats.STATS_FUMBLES, readShortBytes(dis));
		player.getCareerStats().setStat(Stats.STATS_RUSHING_ATTEMPTS, readShortBytes(dis));
		player.getCareerStats().setStat(Stats.STATS_SACKS_AGAINST, readShortBytes(dis));
		player.getCareerStats().setStat(Stats.STATS_SACKS_FOR, readShortBytes(dis));

		// skills
		for (int i = 0; i < MAX_SKILLS; i++)
		{
			int skillIndex = readUnsignedByte(dis);

			if (skillIndex != 0)
			{
				Skill skill = Skill.fromLegacyIndex(skillIndex);
				player.gainSkill(skill);
				adjustAttributeForSkill(player, skill);		//This is needed because the player is saved with attributes permanently modified by skills, but the code here doesn't do that.
			}
		}

		player.setXpAndSkillPoints(totalValue, skillPointsAvailable);

		// quirks
		for (int i = 0; i < MAX_QUIRKS; i++)
		{
			readUnsignedByte(dis); // TODO: give the player the quirk
		}

		// injuries
		int injuryLevel = injuryMap[readUnsignedByte(dis)];

		if (injuryLevel == Player.INJURY_NONE)
			weeksOut = -1;

		// equipment
		for (int i = 0; i < 4; i++)
		{
			int equipIndex = getLegacyEquipment(readUnsignedByte(dis));
			player.equipItem(equipIndex);
		}

		scanBytes(dis, 3); // UNKNOWN

		player.setRace(Race.getRace(race));
		player.name = name;
		player.setWeeksOut(weeksOut);
		player.setInjuryType(injuryLevel);
		player.setSeasons(totalSeasons);

		// System.out.println("\n\tPlayer loaded: " + player.saveAsText());

		return player;
	}

	private static void adjustAttributeForSkill(Player player, Skill skill)
	{
		if (skill == Skill.BRUTAL)
			player.changeAttribute(Player.ATT_ST, -10);
		else if (skill == Skill.STALWART)
			player.changeAttribute(Player.ATT_TG, -10);
		else if (skill == Skill.CHECKMASTER)
			player.changeAttribute(Player.ATT_CH, -10);
		else if (skill == Skill.GYMNASTICS)
			player.changeAttribute(Player.ATT_JP, -10);
		else if (skill == Skill.GYMNASTICS)
			player.changeAttribute(Player.ATT_DA, -10);
		else if (skill == Skill.BOXING)
			player.changeAttribute(Player.ATT_RF, -10);
		else if (skill == Skill.JUGGLING)
			player.changeAttribute(Player.ATT_HD, -10);
		else if (skill == Skill.QUICKENING)
			player.changeAttribute(Player.ATT_AP, -10);
	}

	public static Team loadLegacyTeam(DataInputStream dis) throws IOException
	{
		int TEAM_NAME_LENGTH = 15;
		int MAX_EQUIPMENT = 36;

		Team team = new Team();

		team.teamColors[0] = getLegacyColor(readUnsignedByte(dis));
		team.teamColors[1] = getLegacyColor(readUnsignedByte(dis));

		scanBytes(dis, 23);

		String teamName = "";
		String coachName = "";

		for (int i = 0; i < TEAM_NAME_LENGTH; i++)
		{
			char c = readCharByte(dis);

			if (c == 0)
				c = ' ';

			teamName = teamName + c;
		}

		for (int i = 0; i < TEAM_NAME_LENGTH; i++)
		{
			char c = readCharByte(dis);

			if (c == 0)
				c = ' ';

			coachName = coachName + c;
		}

		//scanBytes(dis, 137);
		
		// possibly last game stats (not saved, but the game probably just dumps the object as a series of bytes)
		scanBytes(dis, 59);

		// season stats
		team.getSeasonStats().setStat(Stats.STATS_RUSHING_YARDS, readShortBytes(dis));
		team.getSeasonStats().setStat(Stats.STATS_KILLS_FOR, readShortBytes(dis));
		team.getSeasonStats().setStat(Stats.STATS_KILLS_AGAINST, readShortBytes(dis));
		team.getSeasonStats().setStat(Stats.STATS_INJURIES_FOR, readShortBytes(dis));
		team.getSeasonStats().setStat(Stats.STATS_INJURIES_AGAINST, readShortBytes(dis));
		team.getSeasonStats().setStat(Stats.STATS_CHECKS_THROWN, readShortBytes(dis));
		team.getSeasonStats().setStat(Stats.STATS_CHECKS_LANDED, readShortBytes(dis));
		team.getSeasonStats().setStat(Stats.STATS_PADS_ACTIVATED, readShortBytes(dis));
		readShortBytes(dis);	//UNKNOWN
		team.getSeasonStats().setStat(Stats.STATS_FUMBLES, readShortBytes(dis));
		team.getSeasonStats().setStat(Stats.STATS_RUSHING_ATTEMPTS, readShortBytes(dis));
		team.getSeasonStats().setStat(Stats.STATS_BALL_CONTROL, readShortBytes(dis));
		team.getSeasonStats().setStat(Stats.STATS_SACKS_AGAINST, readShortBytes(dis));
		team.getSeasonStats().setStat(Stats.STATS_SACKS_FOR, readShortBytes(dis));
		team.getSeasonStats().setStat(Stats.STATS_EJECTIONS, readShortBytes(dis));
		team.getSeasonStats().setStat(Stats.STATS_MUTATIONS, readShortBytes(dis));
		team.getSeasonStats().setStat(Stats.STATS_WINS, readShortBytes(dis));
		team.getSeasonStats().setStat(Stats.STATS_LOSSES, readShortBytes(dis));
		team.getSeasonStats().setStat(Stats.STATS_TIES, readShortBytes(dis));

		// career stats
		team.getCareerStats().setStat(Stats.STATS_RUSHING_YARDS, readShortBytes(dis));
		team.getCareerStats().setStat(Stats.STATS_KILLS_FOR, readShortBytes(dis));
		team.getCareerStats().setStat(Stats.STATS_KILLS_AGAINST, readShortBytes(dis));
		team.getCareerStats().setStat(Stats.STATS_INJURIES_FOR, readShortBytes(dis));
		team.getCareerStats().setStat(Stats.STATS_INJURIES_AGAINST, readShortBytes(dis));
		team.getCareerStats().setStat(Stats.STATS_CHECKS_THROWN, readShortBytes(dis));
		team.getCareerStats().setStat(Stats.STATS_CHECKS_LANDED, readShortBytes(dis));
		team.getCareerStats().setStat(Stats.STATS_PADS_ACTIVATED, readShortBytes(dis));
		readShortBytes(dis);	//UNKNOWN
		team.getCareerStats().setStat(Stats.STATS_FUMBLES, readShortBytes(dis));
		team.getCareerStats().setStat(Stats.STATS_RUSHING_ATTEMPTS, readShortBytes(dis));
		team.getCareerStats().setStat(Stats.STATS_BALL_CONTROL, readShortBytes(dis));
		team.getCareerStats().setStat(Stats.STATS_SACKS_AGAINST, readShortBytes(dis));
		team.getCareerStats().setStat(Stats.STATS_SACKS_FOR, readShortBytes(dis));
		team.getCareerStats().setStat(Stats.STATS_EJECTIONS, readShortBytes(dis));
		team.getCareerStats().setStat(Stats.STATS_MUTATIONS, readShortBytes(dis));
		team.getCareerStats().setStat(Stats.STATS_WINS, readShortBytes(dis));
		team.getCareerStats().setStat(Stats.STATS_LOSSES, readShortBytes(dis));
		team.getCareerStats().setStat(Stats.STATS_TIES, readShortBytes(dis));
		
		// unknown
		scanBytes(dis, 2);

		for (int i = 0; i < 4; i++)
		{
			boolean docbot = dis.readBoolean();
			scanBytes(dis, 3);
			team.docbot[i] = docbot;
		}

		// TODO: showing full list of blank equipment; only need to show the one item
		for (int i = 0; i < MAX_EQUIPMENT; i++)
		{
			int equipmentIndex = readNumByte(dis);

			if (equipmentIndex != -1)
				team.getEquipment().add(getLegacyEquipment(equipmentIndex));
		}

		int fieldSet = readUnsignedByte(dis);
		int fieldNum = readUnsignedByte(dis);

		team.homeField = getLegacyArena(fieldSet, fieldNum);

		// two additional bytes: 6 and 0

		team.teamName = teamName;
		team.coachName = coachName;

		// System.out.println("\n" + team.saveAsText());

		return team;
	}

	private static Integer getLegacyArena(int set, int field)
	{
		return (4 * set) + field;
	}

	private static Color getLegacyColor(int colorIndex)
	{
		switch (colorIndex)
		{
		case 0:
			return new Color(224, 0, 0); // red
		case 1:
			return new Color(224, 96, 0); // orange
		case 3:
			return new Color(224, 224, 0); // yellow
		case 5:
			return new Color(0, 224, 0); // light green
		case 7:
			return new Color(0, 224, 224); // aqua
		case 9:
			return new Color(0, 0, 224); // blue
		case 11:
			return new Color(160, 0, 224); // purple
		case 12:
			return new Color(224, 0, 224); // light purple
		case 13:
			return new Color(0, 128, 0); // green
		case 14:
			return new Color(224, 0, 96); // pink
		case 17:
			return new Color(160, 64, 64); // red brown
		case 18:
			return new Color(160, 192, 224); // light blue
		case 19:
			return new Color(0, 0, 32); // black
		case 21:
			return new Color(192, 192, 192); // white
		case 26:
			return new Color(224, 192, 160); // beige
		case 28:
			return new Color(128, 96, 64); // light brown
		case 29:
			return new Color(128, 64, 32); // dark brown
		}

		// this is case 2 - gold
		return new Color(224, 160, 0);
	}

	// TODO: correct the equipment indexes in Equipment.java
	private static Integer getLegacyEquipment(int equipmentIndex)
	{
		switch (equipmentIndex)
		{
		case 0:
			return Equipment.EQUIP_HEAVY_ARMOR;
		case 1:
			return Equipment.EQUIP_REINFORCED_ARMOR;
		case 2:
			return Equipment.EQUIP_REPULSOR_ARMOR;
		case 3:
			return Equipment.EQUIP_SPIKED_ARMOR;
		case 4:
			return Equipment.EQUIP_SURGE_ARMOR;
		case 5:
			return Equipment.EQUIP_VORTEX_ARMOR;
		case 6:
			return Equipment.EQUIP_BACKFIRE_BELT;
		case 7:
			return Equipment.EQUIP_BOOSTER_BELT;
		case 8:
			return Equipment.EQUIP_CLOAKING_BELT;
		case 9:
			return Equipment.EQUIP_HOLOGRAM_BELT;
		case 10:
			return Equipment.EQUIP_FIELD_INTEGRITY_BELT;
		case 11:
			return Equipment.EQUIP_MEDICAL_BELT;
		case 12:
			return Equipment.EQUIP_SCRAMBLER_BELT;
		case 13:
			return Equipment.EQUIP_SPIKED_BOOTS;
		case 14:
			return Equipment.EQUIP_BOUNDER_BOOTS;
		case 15:
			return Equipment.EQUIP_INSULATED_BOOTS;
		case 16:
			return Equipment.EQUIP_MAGNETIC_BOOTS;
		case 17:
			return Equipment.EQUIP_SAAI_BOOTS;
		case 18:
			return Equipment.EQUIP_MAGNETIC_GLOVES;
		case 19:
			return Equipment.EQUIP_REPULSOR_GLOVES;
		case 20:
			return Equipment.EQUIP_SAAI_GLOVES;
		case 21:
			return Equipment.EQUIP_SPIKED_GLOVES;
		case 22:
			return Equipment.EQUIP_SURGE_GLOVES;
		}

		return Equipment.EQUIP_NONE;
	}
}
