package main.logic.ai.coach.player;

import java.util.ArrayList;
import java.util.List;

public class KurganSlithArdentDraftingAi implements DraftingAi
{
	// based on D2-4: Silent Death
	// standard therapy, enhanced others

	@Override
	public List<PlayerPersona> getDraftingOrder()
	{
		List<PlayerPersona> draftOrder = new ArrayList<PlayerPersona>();

		draftOrder.add(PlayerPersona.kurganSlithLead());
		draftOrder.add(PlayerPersona.kurganSlithRegularS());
		draftOrder.add(PlayerPersona.kurganSlithRegularK());
		draftOrder.add(PlayerPersona.kurganSlithRegularS());
		draftOrder.add(PlayerPersona.kurganSlithRegularK());
		draftOrder.add(PlayerPersona.kurganSlithRegularS());
		draftOrder.add(PlayerPersona.kurganSlithRegularK());
		draftOrder.add(PlayerPersona.kurganSlithRegularS());
		draftOrder.add(PlayerPersona.kurganSlithRegularK());
		draftOrder.add(PlayerPersona.kurganSlithRegularS());
		draftOrder.add(PlayerPersona.kurganSlithRegularK());
		draftOrder.add(PlayerPersona.kurganSlithRegularS());
		draftOrder.add(PlayerPersona.kurganSlithRegularK());
		draftOrder.add(PlayerPersona.kurganSlithRegularS());
		draftOrder.add(PlayerPersona.kurganSlithRegularK());

		return draftOrder;
	}
}
