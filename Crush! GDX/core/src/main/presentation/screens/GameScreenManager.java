package main.presentation.screens;

import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Game;

public class GameScreenManager
{
	private Map<ScreenType, GameScreen> allScreens = new HashMap<ScreenType, GameScreen>();
	
	private static GameScreenManager instance = null;
	
	private GameScreenManager() {}
	
	public static GameScreenManager getInstance()
	{
		if (instance == null)
			instance = new GameScreenManager();
		
		return instance;
	}
	
	public GameScreen getScreen(ScreenType screenType)
	{
		return allScreens.get(screenType);
	}
	
	public void initializeScreens(Game game)
	{
		allScreens.clear();
		allScreens.put(ScreenType.GAME_SELECT, new GameSelectScreen(game, (ActionListener) game));
		allScreens.put(ScreenType.EXHIBITION_TEAM_SELECT, new ExhibitionTeamSelectScreen(game));
		allScreens.put(ScreenType.TOURNAMENT_TEAM_SELECT, new TournamentTeamSelectScreen(game));
		allScreens.put(ScreenType.LEAGUE_TEAM_SELECT, new LeagueTeamSelectScreen(game));
		allScreens.put(ScreenType.GAME_PLAY, new CrushEventScreen(game));
	}
}
