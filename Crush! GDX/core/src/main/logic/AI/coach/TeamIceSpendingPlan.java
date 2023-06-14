package main.logic.ai.coach;

import main.data.entities.Equipment;
import main.data.entities.Race;
import main.data.entities.Team;

public class TeamIceSpendingPlan extends AbstractSpendingPlan
{
	public TeamIceSpendingPlan()
	{
		addNode(PurchasePriorityNode.selectTreatment(Team.DOCBOT_EMERGENCY));
		addNode(PurchasePriorityNode.selectTreatment(Team.DOCBOT_SURGERY));
		addNode(PurchasePriorityNode.selectTreatment(Team.DOCBOT_RECOVERY));
		addNode(PurchasePriorityNode.draftPlayer(Race.DRAGORAN));
		addNode(PurchasePriorityNode.purchaseEquipment(Equipment.EQUIP_FIELD_INTEGRITY_BELT));
		addNode(PurchasePriorityNode.draftPlayer(Race.GRONK));
		addNode(PurchasePriorityNode.draftPlayer(Race.GRONK));
		addNode(PurchasePriorityNode.draftPlayer(Race.KURGAN));
		addNode(PurchasePriorityNode.draftPlayer(Race.KURGAN));
		addNode(PurchasePriorityNode.purchaseEquipment(Equipment.EQUIP_INSULATED_BOOTS));
		addNode(PurchasePriorityNode.purchaseEquipment(Equipment.EQUIP_REINFORCED_ARMOR));
		addNode(PurchasePriorityNode.draftPlayer(Race.SLITH));
		addNode(PurchasePriorityNode.draftPlayer(Race.SLITH));
		addNode(PurchasePriorityNode.draftPlayer(Race.HUMAN));
		addNode(PurchasePriorityNode.draftPlayer(Race.HUMAN));
		addNode(PurchasePriorityNode.purchaseEquipment(Equipment.EQUIP_REPULSOR_GLOVES));
		addNode(PurchasePriorityNode.purchaseEquipment(Equipment.EQUIP_REPULSOR_GLOVES));
		addNode(PurchasePriorityNode.purchaseEquipment(Equipment.EQUIP_SPIKED_GLOVES));
		addNode(PurchasePriorityNode.purchaseEquipment(Equipment.EQUIP_SPIKED_GLOVES));
		addNode(PurchasePriorityNode.purchaseEquipment(Equipment.EQUIP_SPIKED_ARMOR));
		addNode(PurchasePriorityNode.purchaseEquipment(Equipment.EQUIP_SPIKED_ARMOR));
		addNode(PurchasePriorityNode.purchaseEquipment(Equipment.EQUIP_HEAVY_ARMOR));
		addNode(PurchasePriorityNode.purchaseEquipment(Equipment.EQUIP_HEAVY_ARMOR));
		addNode(PurchasePriorityNode.purchaseEquipment(Equipment.EQUIP_SPIKED_BOOTS));
		addNode(PurchasePriorityNode.purchaseEquipment(Equipment.EQUIP_SPIKED_BOOTS));
		addNode(PurchasePriorityNode.draftPlayer(Race.CURMIAN));
		addNode(PurchasePriorityNode.draftPlayer(Race.CURMIAN));
	}
}
