package main.data.entities;

import java.util.ArrayList;
import java.util.List;

public class Equipment
{
	public int type;
	public String name;
	public String description;
	public int index;
	public int detection;
	public int cost;
	
	private int[] attMod;
	private int[] attFix;
	
	private static List<Equipment> allEquipment = null;
	
	public static final int EQUIP_NONE = -2;
	public static final int EQUIP_BLANK = -1;

	public static final int EQUIP_HEAVY_ARMOR = 0;
	public static final int EQUIP_REINFORCED_ARMOR = 1;
	public static final int EQUIP_REPULSOR_ARMOR = 2;
	public static final int EQUIP_SPIKED_ARMOR = 3;
	public static final int EQUIP_SURGE_ARMOR = 4;
	public static final int EQUIP_VORTEX_ARMOR = 5;
	
	public static final int EQUIP_BACKFIRE_BELT = 6;
	public static final int EQUIP_BOOSTER_BELT = 7;
	public static final int EQUIP_CLOAKING_BELT = 8;
	public static final int EQUIP_HOLOGRAM_BELT = 9;
	public static final int EQUIP_FIELD_INTEGRITY_BELT = 10;
	public static final int EQUIP_MEDICAL_BELT = 11;
	public static final int EQUIP_SCRAMBLER_BELT = 12;

	public static final int EQUIP_SPIKED_BOOTS = 13;
	public static final int EQUIP_BOUNDER_BOOTS = 14;
	public static final int EQUIP_INSULATED_BOOTS = 15;
	public static final int EQUIP_MAGNETIC_BOOTS = 16;
	public static final int EQUIP_SAAI_BOOTS = 17;

	public static final int EQUIP_MAGNETIC_GLOVES = 18;
	public static final int EQUIP_REPULSOR_GLOVES = 19;
	public static final int EQUIP_SAAI_GLOVES = 20;
	public static final int EQUIP_SPIKED_GLOVES = 21;
	public static final int EQUIP_SURGE_GLOVES = 22;
	
	public static final int EQUIP_NO_TYPE = -1;
	public static final int EQUIP_ARMOR = 0;
	public static final int EQUIP_GLOVES = 1;
	public static final int EQUIP_BELT = 2;
	public static final int EQUIP_BOOTS = 3;
	
	public static final int EQUIP_TYPE_COUNT = 23;
	
	
	private Equipment(int index)
	{
		this.index = index;
		attMod = new int[8];
		attFix = new int[8];
		
		for (int i = 0; i < 8; i++)
		{
			attMod[i] = 0;
			attFix[i] = 0;
		}
		
		detection = 0;
		cost = 0;
		name = "(none)";
		description = "";
		type = EQUIP_BLANK;
		
		//create the equipment types
		if (index == EQUIP_NONE)
		{
			description = "Your team has no loose equipment.";
			type = -2;
		}
		if (index == EQUIP_HEAVY_ARMOR)
		{
			name = "Heavy Armor";
			description = "+10 Toughness";
			type = EQUIP_ARMOR;
			detection = 10;
			attMod[Player.ATT_TG] = 10;
			cost = 20;
		}
		else if (index == EQUIP_REINFORCED_ARMOR)
		{
			name = "Reinforced Armor";
			description = "+5 Toughness";
			type = EQUIP_ARMOR;
			detection = 5;
			attMod[Player.ATT_TG] = 5;
			cost = 10;
		}
		else if (index == EQUIP_SPIKED_ARMOR)
		{
			name = "Spiked Armor";
			description = "+5 Strength";
			type = EQUIP_ARMOR;
			detection = 10;
			attMod[Player.ATT_ST] = 5;
			cost = 10;
		}
		else if (index == EQUIP_SURGE_ARMOR)
		{
			name = "Surge Armor";
			description = "50% chance of zapping opponents when they block you";
			type = EQUIP_ARMOR;
			detection = 10;
			// TODO Shock Effect
			cost = 30;
		}
		else if (index == EQUIP_VORTEX_ARMOR)
		{
			name = "Vortex Armor";
			description = "Pulls in opposing players";
			type = EQUIP_ARMOR;
			detection = 15;
			// TODO Vortex Effect
			cost = 30;
		}
		else if (index == EQUIP_REPULSOR_ARMOR)
		{
			name = "Repulsor Armor";
			description = "Pushes away opposing players";
			type = EQUIP_ARMOR;
			detection = 10;
			// TODO Vortex Effect
			cost = 20;
		}
		else if (index == EQUIP_SAAI_GLOVES)
		{
			name = "SAAI Gauntlets";
			description = "Gives player 60 CH";
			type = EQUIP_GLOVES;
			detection = 10;
			attFix[Player.ATT_CH] = 60;
			cost = 20;
		}
		else if (index == EQUIP_REPULSOR_GLOVES)
		{
			name = "Repulsor Gauntlets";
			description = "Pushes opponents when player checks them";
			type = EQUIP_GLOVES;
			detection = 10;
			// TODO Vortex Effect
			cost = 20;
		}
		else if (index == EQUIP_MAGNETIC_GLOVES)
		{
			name = "Magnetic Gauntlets";
			description = "Player always picks up the ball";
			type = EQUIP_GLOVES;
			detection = 10;
			attFix[Player.ATT_HD] = 99;
			cost = 10;
		}
		else if (index == EQUIP_SURGE_GLOVES)
		{
			name = "Surge Gauntlets";
			description = "50% chance of zapping opponents when checking them";
			type = EQUIP_GLOVES;
			detection = 15;
			// TODO Shock Effect
			cost = 30;
		}
		else if (index == EQUIP_SPIKED_GLOVES)
		{
			name = "Spiked Gauntlets";
			description = "+5 Strength";
			type = EQUIP_GLOVES;
			detection = 10;
			attMod[Player.ATT_ST] = 5;
			cost = 10;
		}
		else if (index == EQUIP_SAAI_BOOTS)
		{
			name = "SAAI Boots";
			description = "Gives player 40 RF and 40 DA";
			type = EQUIP_BOOTS;
			detection = 10;
			attFix[Player.ATT_RF] = 40;
			attFix[Player.ATT_DA] = 40;
			cost = 20;
		}
		else if (index == EQUIP_BOUNDER_BOOTS)
		{
			name = "Bounder Boots";
			description = "Player cannot fail a jump";
			type = EQUIP_BOOTS;
			detection = 5;
			attFix[Player.ATT_JP] = 99;
			cost = 10;
		}
		else if (index == EQUIP_MAGNETIC_BOOTS)
		{
			name = "Magnetic Boots";
			description = "+5 CH, player can't be pushed or pulled";
			type = EQUIP_BOOTS;
			detection = 10;
			attMod[Player.ATT_CH] = 5;
			attMod[Player.ATT_RF] = -10;
			attMod[Player.ATT_DA] = -10;
			// TODO Vortex Effect
			cost = 10;
		}
		else if (index == EQUIP_SPIKED_BOOTS)
		{
			name = "Spiked Boots";
			description = "+5 Strength";
			type = EQUIP_BOOTS;
			detection = 10;
			attMod[Player.ATT_ST] = 5;
			cost = 10;
		}
		else if (index == EQUIP_INSULATED_BOOTS)
		{
			name = "Insulated Boots";
			description = "Player is immune to all electrical effects";
			type = EQUIP_BOOTS;
			detection = 15;
			cost = 30;
		}
		else if (index == EQUIP_MEDICAL_BELT)
		{
			name = "Medical Belt";
			description = "Prevents injuries and death 33% of the time";
			type = EQUIP_BELT;
			detection = 15;
			// TODO Injury Effect
			cost = 30;
		}
		else if (index == EQUIP_FIELD_INTEGRITY_BELT)
		{
			name = "Field Integrity Belt";
			description = "Keeps player from being blobbed";
			type = EQUIP_BELT;
			detection = 5;
			// TODO Blob Effect
			cost = 10;
		}
		else if (index == EQUIP_BOOSTER_BELT)
		{
			name = "Booster Belt";
			description = "+10 AP, 6% chance of injury every turn";
			type = EQUIP_BELT;
			detection = 15;
			attMod[Player.ATT_AP] = 10;
			// TODO Injury Effect
			cost = 20;
		}
		else if (index == EQUIP_BACKFIRE_BELT)
		{
			name = "Backfire Belt";
			description = "Explodes when the player gets checked";
			type = EQUIP_BELT;
			detection = 15;
			// TODO Injury Effect
			cost = 10;
		}
		else if (index == EQUIP_CLOAKING_BELT)
		{
			name = "Cloaking Belt";
			description = "Player is invisible except when carrying the ball";
			type = EQUIP_BELT;
			detection = 15;
			// TODO Appearance Effect
			cost = 30;
		}
		else if (index == EQUIP_HOLOGRAM_BELT)
		{
			name = "Hologram Belt";
			description = "Disguises the true species of the player";
			type = EQUIP_BELT;
			detection = 5;
			// TODO Appearance Effect
			cost = 10;
		}
		else if (index == EQUIP_SCRAMBLER_BELT)
		{
			name = "Scrambler Belt";
			description = "Causes teleporter accidents 25% of the time";
			type = EQUIP_BELT;
			detection = 10;
			// TODO Blob Effect
			cost = 30;
		}
	}
	
	public Equipment clone(Equipment e)
	{
		return new Equipment(e.index);
	}
	
	public int getAttributeWithEquipment(int attribute, int value)
	{
		int toRet = value + attMod[attribute];
		
		if (attFix[attribute] != 0)
			toRet = attFix[attribute];
		
		return toRet;
	}
	
	public static void defineEquipment()
	{
		allEquipment = new ArrayList<Equipment>();
		
		for (int i = 0; i <= 22; i++)
		{
			allEquipment.add(new Equipment(i));
		}
	}
	
	public static Equipment getEquipment(int index)
	{
		if (index < -2 || index >= EQUIP_TYPE_COUNT)
			return new Equipment(EQUIP_NONE);
		else if (index < 0)
			return new Equipment(index);
		
		if (allEquipment == null)
			defineEquipment();
		
		return allEquipment.get(index);
	}
	
	public static int getType(int index)
	{
		return getEquipment(index).type;
	}
	
	
	//note that this doesn't need to be serialized, since it can't change
	//really generalize this in the future; for now, just have concrete effects based on the index number.
}