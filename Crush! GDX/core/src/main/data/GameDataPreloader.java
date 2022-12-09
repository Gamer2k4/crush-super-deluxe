package main.data;

import java.util.List;

import main.data.entities.Team;
import main.presentation.TeamColorsManager;
import main.presentation.common.Logger;
import main.presentation.game.sprite.CrushAnimatedTile;
import main.presentation.game.sprite.CrushArenaImageManager;
import main.presentation.game.sprite.Facing;
import main.presentation.game.sprite.PlayerAnimation;
import main.presentation.game.sprite.PlayerAnimationManager;
import main.presentation.game.sprite.PlayerState;

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
	
	public static void preloadGameData(List<Team> gameTeams, int arenaIndex)
	{
		long startTime = System.currentTimeMillis();
		
		for (Team team: gameTeams)
			TeamColorsManager.getInstance().refresh(team);
		
		Logger.debug("Team colors loaded in " + (System.currentTimeMillis() - startTime) + "ms.");
		
		CrushArenaImageManager.getInstance().getArenaForHomeTeam(arenaIndex, gameTeams.get(0));
		Logger.debug("Load time including arena recoloring was " + (System.currentTimeMillis() - startTime) + "ms.");
		
		for (Team team : gameTeams)
			PlayerAnimationManager.getInstance().generateAndMapPlayerSprites(team);
		
		Logger.debug("Load time including sprite coloring was " + (System.currentTimeMillis() - startTime) + "ms.");
		
		CrushAnimatedTile.warpAnimation();		//this should generate all the static animations
		
		for (PlayerState state : PlayerState.values())
		{
			if (state == PlayerState.INJURY || state == PlayerState.DOWN || state == PlayerState.SIT
					|| state == PlayerState.BALL_GIVE)		//TODO: remove this line once the animation is defined
				continue;	//no animations defined for these
			
			PlayerAnimation.getAnimation(state, Facing.N);
		}
		
		Logger.debug("Total load time was " + (System.currentTimeMillis() - startTime) + "ms.");
	}
}
