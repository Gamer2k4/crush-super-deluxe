package main.presentation.screens.teameditor;

import main.presentation.ImageType;
import main.presentation.common.ScreenCommand;
import main.presentation.game.StaticImage;

public class TeamEditorSettingsScreen extends AbstractTeamEditorSubScreen
{
	private int arenaSet = 0;
	private int arenaIndexInSet = 0;
	private boolean updatingPrimaryColor = true;
	
	private static final int[][] teamColors = { { 0, 9, 18 },
											  { 1, 11, 29 },
											  { 2, 12, 21 },
											  { 3, 13, 19 },
											  { 5, 14, 26 },
											  { 7, 17, 28 }};
	
	private static final String[][] arenaNames = {{ "BRIDGES", "JACKAL'S LAIR", "CRISSICK", "WHIRLWIND"},
												  {"THE VOID", "OBSERVATORY", "ABYSS", "GADEL SPYRE"},
												  {"FULCRUM", "SAVANNA", "BARROW", "MAELSTROM"},
												  {"VAULT", "NEXUS", "DARKSUN", "BADLANDS"},
												  {"LIGHTWAY", "EYES", "DARKSTAR", "SPACECOM"}};
	
	protected TeamEditorSettingsScreen(TeamEditorParentScreen parentScreen)
	{
		super(parentScreen);
		subScreenImage = new StaticImage(ImageType.SCREEN_TEAM_EDITOR_SETTINGS, screenOrigin);
		refreshContent();
	}

	@Override
	public void handleCommand(ScreenCommand command)
	{
		// TODO Auto-generated method stub
		
	}
	
	public void updateArenaSet(int set)
	{
		arenaSet = set;
	}
	
	public void refreshArena()
	{
//		mapPanel.setArena(teamUpdater.getHomeField());
	}
	
	private void setArenaIndex()
	{
		int set = teamUpdater.getHomeField() / 4;
		arenaIndexInSet = teamUpdater.getHomeField() - (4 * set);
	}

	private void swapToCorrectArenaSet()
	{
		int set = teamUpdater.getHomeField() / 4;
		handleCommand(ScreenCommand.valueOf("ARENA_SET_" + set));
	}
	
	private void refreshTeam()
	{
		setArenaIndex();		
		swapToCorrectArenaSet();
		refreshArena();
	}

	@Override
	protected void refreshContent()
	{
		updatingPrimaryColor = true;
		refreshTeam();
	}
}
