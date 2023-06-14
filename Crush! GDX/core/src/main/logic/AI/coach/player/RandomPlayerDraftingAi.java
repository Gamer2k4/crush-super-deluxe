package main.logic.ai.coach.player;

import java.util.ArrayList;
import java.util.List;

import main.data.entities.Team;

public class RandomPlayerDraftingAi implements DraftingAi
{
	@Override
	public List<PlayerPersona> getDraftingOrder()
	{
		ArrayList<PlayerPersona> randomOrder = new ArrayList<PlayerPersona>();
		
		for (int i = 0; i < Team.MAX_TEAM_SIZE; i++)
		{
			randomOrder.add(PlayerPersona.randomPersona());
		}
		
		return randomOrder;
	}
}
