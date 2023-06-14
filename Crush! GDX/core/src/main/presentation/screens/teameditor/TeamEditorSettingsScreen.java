package main.presentation.screens.teameditor;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import main.presentation.ImageType;
import main.presentation.common.ScreenCommand;
import main.presentation.game.StaticImage;
import main.presentation.screens.teameditor.utilities.TeamUpdater;

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
	
	private List<ImageButton> buttons;
	
	protected TeamEditorSettingsScreen(TeamEditorParentScreen parentScreen)
	{
		super(parentScreen);
		subScreenImage = new StaticImage(ImageType.SCREEN_TEAM_EDITOR_SETTINGS, screenOrigin);
		defineButtons();
		refreshContent();
	}
	
	private void defineButtons()
	{
		buttons = new ArrayList<ImageButton>();
		
		buttons.add(parentScreen.addClickZone(246, 86, 37, 17, ScreenCommand.CONTROLLER_HUMAN));
		buttons.add(parentScreen.addClickZone(287, 86, 37, 17, ScreenCommand.CONTROLLER_AI));
	}

	@Override
	public void handleCommand(ScreenCommand command)
	{
//		//delegate these two to the parent screen, since they potentially involve a popup
//		if (!areTeamsLockedForEditing() && (command == ScreenCommand.CONTROLLER_HUMAN || command == ScreenCommand.CONTROLLER_AI))
//			parentScreen.actionPerformed(command.asActionEvent());
//		//if the teams are locked for editing, there's no need to do a popup; the teams aren't replaced and control simply switches
//		else if (command == ScreenCommand.CONTROLLER_HUMAN)
//			teamUpdater.getTeam().humanControlled = true;
//		else if (command == ScreenCommand.CONTROLLER_AI)
//			teamUpdater.getTeam().humanControlled = false;
		
		//delegate these two to the parent screen, since they potentially involve a popup
		if (command == ScreenCommand.CONTROLLER_HUMAN || command == ScreenCommand.CONTROLLER_AI)
			parentScreen.actionPerformed(command.asActionEvent());
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
	
	@Override
	public void actionPerformed(ActionEvent event)
	{
		if (event.getActionCommand().equals(TeamUpdater.UPDATER_NEW_TEAM))
			refreshContent();
	}
	
	@Override
	public List<Actor> getActors()
	{
		List<Actor> actors = super.getActors();
		
		actors.add(getControllerPressedButton());
		
		return actors;
	}
	
	@Override
	public List<ImageButton> getScreenButtons()
	{
		List<ImageButton> screenButtons = super.getScreenButtons();
		
		screenButtons.addAll(buttons);
		
		return screenButtons;
	}
	
	private Image getControllerPressedButton()
	{
		Point coords = new Point(246, 297);
		if (!teamUpdater.getTeam().humanControlled)
			coords.x = 287;
		
		StaticImage controllerPressedButton = new StaticImage(ImageType.BUTTON_37x17_CLICKED, coords);
		return controllerPressedButton.getImage();
	}
}
