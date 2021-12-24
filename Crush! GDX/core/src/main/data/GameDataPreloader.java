package main.data;

import java.util.List;

import main.data.entities.Team;
import main.presentation.game.sprite.CrushAnimatedTile;
import main.presentation.game.sprite.CrushArenaImageManager;
import main.presentation.game.sprite.PlayerAnimationManager;

public class GameDataPreloader
{
	public static void preloadGameData(List<Team> gameTeams)
	{
		Team homeTeam = gameTeams.get(0);
		
		long startTime = System.currentTimeMillis();
		
		for (int i = 0; i < 20; i++)	//TODO: define "total arenas" somewhere
			CrushArenaImageManager.getInstance().getArenaForHomeTeam(i, homeTeam);
		
		System.out.println("Arena colors updated (for home team) in " + (System.currentTimeMillis() - startTime) + "ms.");
		
		//cursor manager and audio manager should already be loaded
		
		for (Team team : gameTeams)
			PlayerAnimationManager.getInstance().generateAndMapPlayerSprites(team);
		
		CrushAnimatedTile.warpAnimation();
	}
}
