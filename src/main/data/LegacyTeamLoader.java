package main.data;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.data.entities.Equipment;
import main.data.entities.Player;
import main.data.entities.Team;
import main.data.factory.PlayerFactory;

public class LegacyTeamLoader
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

			//System.out.println("Total file size to read (in bytes) : " + fis.available());

			for (int i = 0; i < MAX_TEAM_PLAYERS; i++)
			{
				Player player = loadLegacyPlayer(dis);
				playersOnTeam.add(player);
			}

			team = loadLegacyTeam(dis);
			
			for (Player player: playersOnTeam)
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

	private static Player loadLegacyPlayer(DataInputStream dis) throws IOException
	{
		int PLAYER_NAME_LENGTH = 8;
		int MAX_SKILLS = 30;
		int MAX_QUIRKS = 12;

		int[] raceMap = { Player.RACE_HUMAN, Player.RACE_GRONK, Player.RACE_CURMIAN, Player.RACE_DRAGORAN, Player.RACE_NYNAX,
				Player.RACE_SLITH, Player.RACE_KURGAN, Player.RACE_XJS9000 };
		int[] statMap = { Player.ATT_AP, Player.ATT_RF, Player.ATT_JP, Player.ATT_CH, Player.ATT_ST, Player.ATT_TG, Player.ATT_HD,
				Player.ATT_DA };
		int[] injuryMap = { Player.INJURY_NONE, Player.INJURY_TRIVIAL, Player.INJURY_MINOR, Player.INJURY_CRIPPLING };

		Player player = PlayerFactory.createEmptyPlayer();

		int totalValue = readShortBytes(dis);
		int skillPointsAvailable = readShortBytes(dis);

		scanBytes(dis, 3);

		int race = raceMap[readUnsignedByte(dis)];

		scanBytes(dis, 4);
		readUnsignedByte(dis); // current AP

		// TODO: These are already enhanced by skills - that is, a Dragoran with Quickening will have his AP listed as 80 here.
		// however, worn equipment does NOT add to these skills
		for (int i = 0; i < 8; i++)
		{
			int stat = readUnsignedByte(dis);
			
			if (stat == 0)	//lowest possible stat is 1, so 0 means the player doesn't exist
			{
				scanBytes(dis, 158);	//scan through the rest of the player entry and return null
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

		readShortBytes(dis); // game played
		readShortBytes(dis); // seasons played

		scanBytes(dis, 18); // likely in-game stats

		// likely current season stats
		readShortBytes(dis); // rushing tiles
		readShortBytes(dis); // goals scored
		readShortBytes(dis); // kills for
		readShortBytes(dis); // injuries for
		readShortBytes(dis); // UNKNOWN
		readShortBytes(dis); // checks thrown
		readShortBytes(dis); // checks landed
		readShortBytes(dis); // pads tried
		readShortBytes(dis); // UNKNOWN
		readShortBytes(dis); // rushing attempts
		readShortBytes(dis); // UNKNOWN
		readShortBytes(dis); // sacks for

		// career stats
		readShortBytes(dis); // total rushing tiles
		readShortBytes(dis); // total goals scored
		readShortBytes(dis); // total kills for
		readShortBytes(dis); // total injuries for
		readShortBytes(dis); // UNKNOWN
		readShortBytes(dis); // total checks thrown
		readShortBytes(dis); // total checks landed
		readShortBytes(dis); // total pads tried
		readShortBytes(dis); // UNKNOWN
		readShortBytes(dis); // total rushing attempts
		readShortBytes(dis); // UNKNOWN
		readShortBytes(dis); // total sacks for

		// skills
		for (int i = 0; i < MAX_SKILLS; i++)
		{
			int skillIndex = readUnsignedByte(dis);
			int skill = getLegacySkills(skillIndex);

			if (skill != -1)
			{
				player.gainSkill(skill);
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

		if (injuryLevel == 0)
			weeksOut = -1;

		// equipment
		for (int i = 0; i < 4; i++)
		{
			int equipIndex = getLegacyEquipment(readUnsignedByte(dis));
			player.equipItem(equipIndex);
		}

		scanBytes(dis, 3); // UNKNOWN

		// TODO: testing start
//		for (int i = 0; i < 172; i++)
//		{
//			int integer = readUnsignedByte(dis);
//			// String integer = readHexByte(dis);
//			System.out.print(integer + ",");
//		}
//
//		System.out.println();

		player.race = race;
		player.name = name;
		player.setWeeksOut(weeksOut);
		player.setInjuryType(injuryLevel);

		//System.out.println("\n\tPlayer loaded: " + player.saveAsText());

		return player;
	}

	private static Team loadLegacyTeam(DataInputStream dis) throws IOException
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

		scanBytes(dis, 137);

		for (int i = 0; i < 4; i++)
		{
			boolean docbot = dis.readBoolean();
			scanBytes(dis, 3);
			team.docbot[i] = docbot;
		}
		
		//TODO: showing full list of blank equipment; only need to show the one item
		for (int i = 0; i < MAX_EQUIPMENT; i++)
		{
			int equipmentIndex = readUnsignedByte(dis);

			if (equipmentIndex != -1)
				team.unassignedGear.add(getLegacyEquipment(equipmentIndex));
		}

		int fieldSet = readUnsignedByte(dis);
		int fieldNum = readUnsignedByte(dis);

		team.homeField = getLegacyArena(fieldSet, fieldNum);

		// two additional bytes: 6 and 0

		team.teamName = teamName;
		team.coachName = coachName;

		//System.out.println("\n" + team.saveAsText());

		return team;
	}

	private static void scanBytes(DataInputStream dis, int bytesToScan) throws IOException
	{
		for (int i = 0; i < bytesToScan; i++)
			dis.readByte();
	}

	private static int readUnsignedByte(DataInputStream dis) throws IOException
	{
		return dis.readByte() & 0xFF;
	}

	private static char readCharByte(DataInputStream dis) throws IOException
	{
		return (char) readUnsignedByte(dis);
	}

	private static short readShortBytes(DataInputStream dis) throws IOException
	{
		int addend1 = readUnsignedByte(dis);
		int addend2 = 256 * readUnsignedByte(dis);

		return (short) (addend1 + addend2);
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
			return new Color(224, 192, 60); // beige
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
			return Equipment.EQUIP_FIELD_INTEGRITY_BELT;	//TODO: see if 9 and 10 need to be switched (integrity is showing up as hologram)
		case 10:
			return Equipment.EQUIP_HOLOGRAM_BELT;
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

	// TODO: correct the skill indexes in Player.java
	private static Integer getLegacySkills(int skillIndex)
	{
		switch (skillIndex)
		{
		case 0: // no skill has an index of 0
		case 1:
			// return Equipment.EQUIP_REINFORCED_ARMOR; Death Reek
		case 2:
			// return Equipment.EQUIP_REPULSOR_ARMOR; Pop Up
		case 3:
			// return Equipment.EQUIP_SPIKED_ARMOR; High Jump
		case 4:
			// return Equipment.EQUIP_SURGE_ARMOR; Regenerate
		case 5:
			// return Equipment.EQUIP_VORTEX_ARMOR; Blood Lust
		case 6:
			// return Equipment.EQUIP_BACKFIRE_BELT; Hive Mind
		case 7:
			// return Equipment.EQUIP_BOOSTER_BELT; Gyro Stabilizer
		case 8:
			// return Equipment.EQUIP_CLOAKING_BELT; Uncommon Valor
			return -1;
		case 9:
			return Player.SKILL_TERROR;
		case 10:
			return Player.SKILL_JUGGERNAUT;
		case 11:
			return Player.SKILL_TACTICS;
		case 12:
			return Player.SKILL_VICIOUS;
		case 13:
			return Player.SKILL_BRUTAL;
		case 14:
			return Player.SKILL_CHECKMASTER;
		case 15:
			return Player.SKILL_STALWART;
		case 16:
			return Player.SKILL_GUARD;
		case 17:
			return Player.SKILL_RESILIENT;
		case 18:
			return Player.SKILL_CHARGE;
		case 19:
			return Player.SKILL_BOXING;
		case 20:
			return Player.SKILL_COMBO;
		case 21:
			return Player.SKILL_QUICKENING;
		case 22:
			return Player.SKILL_GYMNASTICS;
		case 23:
			return Player.SKILL_JUGGLING;
		case 24:
			return Player.SKILL_SCOOP;
		case 25:
			return Player.SKILL_STRIP;
		case 26:
			return Player.SKILL_JUDO;
		case 27:
			return Player.SKILL_FIST_OF_IRON;
		case 28:
			return Player.SKILL_DOOMSTRIKE;
		case 29:
			return Player.SKILL_AWE;
		case 30:
			return Player.SKILL_STOIC;
		case 31:
			return Player.SKILL_LEADER;
		case 32:
			return Player.SKILL_SENSEI;
		case 33:
			return Player.SKILL_SLY;
		case 34:
			return Player.SKILL_INTUITION;
		case 35:
			return Player.SKILL_HEALER;
		case 36:
			return Player.SKILL_KARMA;
		case 37:
			return Player.SKILL_NINJA_MASTER;
		}

		return -1;
	}
}
