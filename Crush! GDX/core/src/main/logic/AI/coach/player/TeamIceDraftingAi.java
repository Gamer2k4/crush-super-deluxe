package main.logic.ai.coach.player;

import java.util.ArrayList;
import java.util.List;

public class TeamIceDraftingAi implements DraftingAi
{
	@Override
	public List<PlayerPersona> getDraftingOrder()
	{
		List<PlayerPersona> draftOrder = new ArrayList<PlayerPersona>();
		
		draftOrder.add(PlayerPersona.captain());
		draftOrder.add(PlayerPersona.leadBlocker());
		draftOrder.add(PlayerPersona.leadBlocker());
		draftOrder.add(PlayerPersona.slayer());
		draftOrder.add(PlayerPersona.slayer());
		draftOrder.add(PlayerPersona.guard());
		draftOrder.add(PlayerPersona.guard());
		draftOrder.add(PlayerPersona.cleanUp());
		draftOrder.add(PlayerPersona.cleanUp());
		draftOrder.add(PlayerPersona.dragoran());
		draftOrder.add(PlayerPersona.gronk());
		draftOrder.add(PlayerPersona.kurgan());
		draftOrder.add(PlayerPersona.slith());
		draftOrder.add(PlayerPersona.human());
		draftOrder.add(PlayerPersona.curmian());
		
		return draftOrder;
	}
}
