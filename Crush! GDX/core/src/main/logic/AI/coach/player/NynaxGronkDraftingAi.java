package main.logic.ai.coach.player;

import java.util.ArrayList;
import java.util.List;

public class NynaxGronkDraftingAi implements DraftingAi
{
	// based on D3-1: Bad Guys
	// standard therapy, enhanced others

	@Override
	public List<PlayerPersona> getDraftingOrder()
	{
		List<PlayerPersona> draftOrder = new ArrayList<PlayerPersona>();

		draftOrder.add(PlayerPersona.nynaxGronkLead());
		draftOrder.add(PlayerPersona.nynaxGronkLead());
		draftOrder.add(PlayerPersona.nynaxGronkHeavy());
		draftOrder.add(PlayerPersona.nynaxGronkHeavy());
		draftOrder.add(PlayerPersona.nynaxGronkRegular());
		draftOrder.add(PlayerPersona.nynaxGronkRegular());
		draftOrder.add(PlayerPersona.nynaxGronkRegular());
		draftOrder.add(PlayerPersona.nynaxGronkRegular());
		draftOrder.add(PlayerPersona.nynax());
		draftOrder.add(PlayerPersona.nynax());
		draftOrder.add(PlayerPersona.gronk());
		draftOrder.add(PlayerPersona.nynax());
		draftOrder.add(PlayerPersona.nynax());
		draftOrder.add(PlayerPersona.gronk());

		return draftOrder;
	}
}
