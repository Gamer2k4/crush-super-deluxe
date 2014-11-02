package main.data.entities;

import java.util.ArrayList;
import java.util.List;

public class Equipment
{
	public int type;
	public String name;
	public int index;
	public int detection;
	public int cost;
	
	private int[] attMod;
	private int[] attFix;
	
	private static List<Equipment> allEquipment = null;
	
	public static final int EQUIP_NONE = -1;
	
	public static final int EQUIP_REINFORCED_ARMOR = 0;
	public static final int EQUIP_HEAVY_ARMOR = 1;
	public static final int EQUIP_SPIKED_ARMOR = 2;
	public static final int EQUIP_SURGE_ARMOR = 3;
	public static final int EQUIP_VORTEX_ARMOR = 4;
	public static final int EQUIP_REPULSOR_ARMOR = 5;
	
	public static final int EQUIP_SAAI_GLOVES = 6;
	public static final int EQUIP_REPULSOR_GLOVES = 7;
	public static final int EQUIP_MAGNETIC_GLOVES = 8;
	public static final int EQUIP_SURGE_GLOVES = 9;
	public static final int EQUIP_SPIKED_GLOVES = 10;
	
	public static final int EQUIP_SAAI_BOOTS = 11;
	public static final int EQUIP_BOUNDER_BOOTS = 12;
	public static final int EQUIP_MAGNETIC_BOOTS = 13;
	public static final int EQUIP_SPIKED_BOOTS = 14;
	public static final int EQUIP_INSULATED_BOOTS = 15;
	
	public static final int EQUIP_MEDICAL_BELT = 16;
	public static final int EQUIP_FIELD_INTEGRITY_BELT = 17;
	public static final int EQUIP_BOOSTER_BELT = 18;
	public static final int EQUIP_BACKFIRE_BELT = 19;
	public static final int EQUIP_CLOAKING_BELT = 20;
	public static final int EQUIP_HOLOGRAM_BELT = 21;
	public static final int EQUIP_SCRAMBLER_BELT = 22;
	
	public static final int EQUIP_ARMOR = 0;
	public static final int EQUIP_GLOVES = 1;
	public static final int EQUIP_BELT = 2;
	public static final int EQUIP_BOOTS = 3;
	
	
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
		
		//create the equipment types
		if (index == EQUIP_HEAVY_ARMOR)
		{
			name = "Heavy Armor";
			type = EQUIP_ARMOR;
			detection = 10;
			attMod[Player.ATT_TG] = 10;
			cost = 20;
		}
		else if (index == EQUIP_REINFORCED_ARMOR)
		{
			name = "Reinforced Armor";
			type = EQUIP_ARMOR;
			detection = 5;
			attMod[Player.ATT_TG] = 5;
			cost = 10;
		}
		else if (index == EQUIP_SPIKED_ARMOR)
		{
			name = "Spiked Armor";
			type = EQUIP_ARMOR;
			detection = 10;
			attMod[Player.ATT_ST] = 5;
			cost = 10;
		}
		else if (index == EQUIP_SURGE_ARMOR)
		{
			name = "Surge Armor";
			type = EQUIP_ARMOR;
			detection = 20;
			// TODO Shock Effect
			cost = 30;
		}
		else if (index == EQUIP_VORTEX_ARMOR)
		{
			name = "Vortex Armor";
			type = EQUIP_ARMOR;
			detection = 15;
			// TODO Vortex Effect
			cost = 30;
		}
		else if (index == EQUIP_REPULSOR_ARMOR)
		{
			name = "Repulsor Armor";
			type = EQUIP_ARMOR;
			detection = 10;
			// TODO Vortex Effect
			cost = 20;
		}
		else if (index == EQUIP_SAAI_GLOVES)
		{
			name = "SAAI Gauntlets";
			type = EQUIP_GLOVES;
			detection = 15;
			attFix[Player.ATT_CH] = 60;
			cost = 20;
		}
		else if (index == EQUIP_REPULSOR_GLOVES)
		{
			name = "Repulsor Gauntlets";
			type = EQUIP_GLOVES;
			detection = 10;
			// TODO Vortex Effect
			cost = 20;
		}
		else if (index == EQUIP_MAGNETIC_GLOVES)
		{
			name = "Magnetic Gauntlets";
			type = EQUIP_GLOVES;
			detection = 5;
			attFix[Player.ATT_HD] = 99;
			cost = 10;
		}
		else if (index == EQUIP_SURGE_GLOVES)
		{
			name = "Surge Gauntlets";
			type = EQUIP_GLOVES;
			detection = 15;
			// TODO Shock Effect
			cost = 30;
		}
		else if (index == EQUIP_SPIKED_GLOVES)
		{
			name = "Spiked Gauntlets";
			type = EQUIP_GLOVES;
			detection = 10;
			attMod[Player.ATT_ST] = 5;
			cost = 10;
		}
		else if (index == EQUIP_SAAI_BOOTS)
		{
			name = "SAAI Boots";
			type = EQUIP_BOOTS;
			detection = 10;
			attFix[Player.ATT_RF] = 40;
			attFix[Player.ATT_DA] = 40;
			cost = 20;
		}
		else if (index == EQUIP_BOUNDER_BOOTS)
		{
			name = "Bounder Boots";
			type = EQUIP_BOOTS;
			detection = 5;
			attFix[Player.ATT_JP] = 99;
			cost = 10;
		}
		else if (index == EQUIP_MAGNETIC_BOOTS)
		{
			name = "Magnetic Boots";
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
			type = EQUIP_BOOTS;
			detection = 10;
			attMod[Player.ATT_ST] = 5;
			cost = 10;
		}
		else if (index == EQUIP_INSULATED_BOOTS)
		{
			name = "Insulated Boots";
			type = EQUIP_BOOTS;
			detection = 15;
			cost = 30;
		}
		else if (index == EQUIP_MEDICAL_BELT)
		{
			name = "Medical Belt";
			type = EQUIP_BELT;
			detection = 15;
			// TODO Injury Effect
			cost = 30;
		}
		else if (index == EQUIP_FIELD_INTEGRITY_BELT)
		{
			name = "Field Integrity Belt";
			type = EQUIP_BELT;
			detection = 10;
			// TODO Blob Effect
			cost = 10;
		}
		else if (index == EQUIP_BOOSTER_BELT)
		{
			name = "Booster Belt";
			type = EQUIP_BELT;
			detection = 15;
			attMod[Player.ATT_AP] = 20;
			// TODO Injury Effect
			cost = 20;
		}
		else if (index == EQUIP_BACKFIRE_BELT)
		{
			name = "Backfire Belt";
			type = EQUIP_BELT;
			detection = 10;
			// TODO Injury Effect
			cost = 10;
		}
		else if (index == EQUIP_CLOAKING_BELT)
		{
			name = "Cloaking Belt";
			type = EQUIP_BELT;
			detection = 15;
			// TODO Appearance Effect
			cost = 30;
		}
		else if (index == EQUIP_HOLOGRAM_BELT)
		{
			name = "Hologram Belt";
			type = EQUIP_BELT;
			detection = 10;
			// TODO Appearance Effect
			cost = 10;
		}
		else if (index == EQUIP_SCRAMBLER_BELT)
		{
			name = "Scrambler Belt";
			type = EQUIP_BELT;
			detection = 15;
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
		if (allEquipment == null)
			defineEquipment();
		
		return allEquipment.get(index);
	}
	
	
	//note that this doesn't need to be serialized, since it can't change
	//really generalize this in the future; for now, just have concrete effects based on the index number.
}