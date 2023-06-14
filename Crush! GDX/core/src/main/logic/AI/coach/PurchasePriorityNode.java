package main.logic.ai.coach;

import main.data.entities.Equipment;
import main.data.entities.Race;

public class PurchasePriorityNode
{
	private PurchaseType type;
	
	private Race race;
	private Equipment equipment;
	private int docbot;
	
	public static PurchasePriorityNode draftPlayer(Race raceToDraft)
	{
		return new PurchasePriorityNode(PurchaseType.PLAYER, raceToDraft, null, -1);
	}
	
	public static PurchasePriorityNode purchaseEquipment(int equipmentToBuy)
	{
		return new PurchasePriorityNode(PurchaseType.EQUIPMENT, null, Equipment.getEquipment(equipmentToBuy), -1);
	}
	
	public static PurchasePriorityNode selectTreatment(int treatment)
	{
		return new PurchasePriorityNode(PurchaseType.DOCBOT, null, null, treatment);
	}
	
	private PurchasePriorityNode(PurchaseType type, Race race, Equipment equipment, int docbot)
	{
		this.type = type;
		this.race = race;
		this.equipment = equipment;
		this.docbot = docbot;
	}

	public PurchaseType getType()
	{
		return type;
	}
	
	public void setType(PurchaseType type)
	{
		this.type = type;
	}
	
	public Race getRace()
	{
		return race;
	}
	
	public void setRace(Race race)
	{
		this.race = race;
	}
	
	public Equipment getEquipment()
	{
		return equipment;
	}
	
	public void setEquipment(Equipment equipment)
	{
		this.equipment = equipment;
	}
	
	public int getDocbot()
	{
		return docbot;
	}
	
	public void setDocbot(int docbot)
	{
		this.docbot = docbot;
	}
}
