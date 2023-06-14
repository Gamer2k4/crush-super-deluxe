package main.logic.ai.coach.player;

import java.util.ArrayList;
import java.util.List;

public class NynaxStealthSurgeDraftingAi implements DraftingAi
{
	//based on D3-3: Freak Show
	//standard therapy, enhanced others
	
	@Override
	public List<PlayerPersona> getDraftingOrder()
	{
		List<PlayerPersona> draftOrder = new ArrayList<PlayerPersona>();

		draftOrder.add(PlayerPersona.nynaxStealthSurgeLead());
		draftOrder.add(PlayerPersona.nynaxStealthSurgeLead());
		draftOrder.add(PlayerPersona.nynaxStealthSurgeLead());
		draftOrder.add(PlayerPersona.nynaxStealthSurgeLead());
		draftOrder.add(PlayerPersona.nynaxStealthSurgeLead());
		draftOrder.add(PlayerPersona.nynaxStealthSurgeLead());
		draftOrder.add(PlayerPersona.nynaxStealthSurgeRegular());
		draftOrder.add(PlayerPersona.nynaxStealthSurgeRegular());
		draftOrder.add(PlayerPersona.nynaxStealthSurgeRegular());
		draftOrder.add(PlayerPersona.nynax());
		draftOrder.add(PlayerPersona.nynax());
		draftOrder.add(PlayerPersona.nynax());
		draftOrder.add(PlayerPersona.nynax());
		draftOrder.add(PlayerPersona.nynax());
		draftOrder.add(PlayerPersona.nynax());
		draftOrder.add(PlayerPersona.nynax());
		draftOrder.add(PlayerPersona.nynax());
		draftOrder.add(PlayerPersona.nynax());
		draftOrder.add(PlayerPersona.nynax());
		
		return draftOrder;
	}
}
