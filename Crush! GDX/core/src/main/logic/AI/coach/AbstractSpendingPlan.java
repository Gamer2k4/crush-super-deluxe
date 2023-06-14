package main.logic.ai.coach;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSpendingPlan implements SpendingPlan
{
	private List<PurchasePriorityNode> purchaseOrder = new ArrayList<PurchasePriorityNode>();
	
	@Override
	public List<PurchasePriorityNode> getPurchasePriorities()
	{
		return purchaseOrder;
	}
	
	protected void addNode(PurchasePriorityNode node)
	{
		purchaseOrder.add(node);
	}
}
