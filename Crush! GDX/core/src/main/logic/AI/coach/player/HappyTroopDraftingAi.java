package main.logic.ai.coach.player;

import java.util.ArrayList;
import java.util.List;

public class HappyTroopDraftingAi implements DraftingAi
{
	// based on D3-2: Happy Troop
	// standard therapy, enhanced others
	// gear exists, but I'm not sure who gets what

	@Override
	public List<PlayerPersona> getDraftingOrder()
	{
		List<PlayerPersona> draftOrder = new ArrayList<PlayerPersona>();

		draftOrder.add(PlayerPersona.curmian());
		draftOrder.add(PlayerPersona.gronk());
		draftOrder.add(PlayerPersona.curmian());
		draftOrder.add(PlayerPersona.gronk());
		draftOrder.add(PlayerPersona.kurgan());
		draftOrder.add(PlayerPersona.kurgan());
		draftOrder.add(PlayerPersona.kurgan());
		draftOrder.add(PlayerPersona.human());
		draftOrder.add(PlayerPersona.human());
		draftOrder.add(PlayerPersona.curmian());
		draftOrder.add(PlayerPersona.gronk());
		draftOrder.add(PlayerPersona.kurgan());
		draftOrder.add(PlayerPersona.human());

		return draftOrder;
	}
}
