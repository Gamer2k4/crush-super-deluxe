package main.logic.ai.coach.player;

import java.util.ArrayList;
import java.util.List;

public class CurmianRushDraftingAi implements DraftingAi
{
	//based on D1-4: Knights
	//full docbot
	
	@Override
	public List<PlayerPersona> getDraftingOrder()
	{
		List<PlayerPersona> draftOrder = new ArrayList<PlayerPersona>();
		
		draftOrder.add(PlayerPersona.curmianLead());
		draftOrder.add(PlayerPersona.curmianLead());
		draftOrder.add(PlayerPersona.curmianLead());
		draftOrder.add(PlayerPersona.curmianRegular());
		draftOrder.add(PlayerPersona.curmianRegular());
		draftOrder.add(PlayerPersona.curmianRegular());
		draftOrder.add(PlayerPersona.curmianRegular());
		draftOrder.add(PlayerPersona.curmianRegular());
		draftOrder.add(PlayerPersona.curmianRegular());
		draftOrder.add(PlayerPersona.curmian());
		draftOrder.add(PlayerPersona.curmian());
		draftOrder.add(PlayerPersona.curmian());
		draftOrder.add(PlayerPersona.curmian());
		draftOrder.add(PlayerPersona.curmian());
		draftOrder.add(PlayerPersona.curmian());
		draftOrder.add(PlayerPersona.curmian());
		draftOrder.add(PlayerPersona.curmian());
		draftOrder.add(PlayerPersona.curmian());
		
		return draftOrder;
	}
}
