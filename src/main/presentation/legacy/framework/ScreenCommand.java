package main.presentation.legacy.framework;

import java.awt.event.ActionEvent;

public enum ScreenCommand
{	
	MAIN_SCREEN,
	EXHIBITION_TEAM_SELECT,
	TOURNAMENT_TEAM_SELECT,
	LEAGUE_TEAM_SELECT,
	EXIT_TEAM_EDITOR_BACK,
	EXIT_TEAM_EDITOR_DONE,
	BEGIN_GAME,
	
	EDIT_TEAM_0,
	EDIT_TEAM_1,
	EDIT_TEAM_2,
	EDIT_TEAM_3,
	EDIT_TEAM_4,
	EDIT_TEAM_5,
	EDIT_TEAM_6,
	EDIT_TEAM_7,
	EDIT_TEAM_8,
	EDIT_TEAM_9,
	EDIT_TEAM_10,
	EDIT_TEAM_11,
	
	SHOW_RUSHING,
	SHOW_CHECKING,
	SHOW_CHECK_TAB,
	SHOW_SACK_TAB,
	SHOW_CARNAGE,
	SHOW_OVERVIEW,
	SHOW_MISC_TAB,
	SHOW_MVP_TAB,
	DONE,
	CANCEL,
	EXIT,
	
	SETTINGS_VIEW,
	ACQUIRE_VIEW,
	OUTFIT_VIEW,
	TOGGLE_ROSTER_VIEW,
	STATS_VIEW,
	DRAFT_VIEW,
	DOCBOT_VIEW,
	POWER_VIEW,
	AGILITY_VIEW,
	PSYCHE_VIEW,
	SCHEDULE_VIEW,
	
	RENAME_TEAM,
	RENAME_COACH,
	
	ARENA_NAME_0,
	ARENA_NAME_1,
	ARENA_NAME_2,
	ARENA_NAME_3,
	
	ARENA_SET_0,
	ARENA_SET_1,
	ARENA_SET_2,
	ARENA_SET_3,
	ARENA_SET_4,
	
	SCROLL_UP,
	SCROLL_DOWN,

	TEAM_SAVE,
	TEAM_LOAD,
	
	TEAM_MAIN_COLOR,
	TEAM_TRIM_COLOR,
	
	TEAM_COLOR_00,
	TEAM_COLOR_01,
	TEAM_COLOR_02,
	TEAM_COLOR_10,
	TEAM_COLOR_11,
	TEAM_COLOR_12,
	TEAM_COLOR_20,
	TEAM_COLOR_21,
	TEAM_COLOR_22,
	TEAM_COLOR_30,
	TEAM_COLOR_31,
	TEAM_COLOR_32,
	TEAM_COLOR_40,
	TEAM_COLOR_41,
	TEAM_COLOR_42,
	TEAM_COLOR_50,
	TEAM_COLOR_51,
	TEAM_COLOR_52,

	SWAP_PLAYERS,
	
	SELECT_PLAYER_0,
	SELECT_PLAYER_1,
	SELECT_PLAYER_2,
	SELECT_PLAYER_3,
	SELECT_PLAYER_4,
	SELECT_PLAYER_5,
	SELECT_PLAYER_6,
	SELECT_PLAYER_7,
	SELECT_PLAYER_8,
	SELECT_PLAYER_9,
	SELECT_PLAYER_10,
	SELECT_PLAYER_11,
	SELECT_PLAYER_12,
	SELECT_PLAYER_13,

	RENAME_PLAYER_0,
	RENAME_PLAYER_1,
	RENAME_PLAYER_2,
	RENAME_PLAYER_3,
	RENAME_PLAYER_4,
	RENAME_PLAYER_5,
	RENAME_PLAYER_6,
	RENAME_PLAYER_7,
	RENAME_PLAYER_8,
	RENAME_PLAYER_9,
	RENAME_PLAYER_10,
	RENAME_PLAYER_11,
	RENAME_PLAYER_12,
	RENAME_PLAYER_13,
	
	TOGGLE_SWAP,
	
	STOCK_DRAFT_SELECT_HUMAN,
	STOCK_DRAFT_SELECT_GRONK,
	STOCK_DRAFT_SELECT_CURMIAN,
	STOCK_DRAFT_SELECT_DRAGORAN,
	STOCK_DRAFT_SELECT_NYNAX,
	STOCK_DRAFT_SELECT_SLITH,
	STOCK_DRAFT_SELECT_KURGAN,
	STOCK_DRAFT_SELECT_XJS9000,
	
	HIRE_PLAYER,
	FIRE_PLAYER,
	
	MINIMAP_CLICK;
	
	public static ScreenCommand fromActionEvent(ActionEvent ae)
	{
		for (ScreenCommand command : values())
		{
			if (command.name().equals(ae.getActionCommand()))
				return command;
		}
		
		throw new IllegalArgumentException("No ScreenCommand exists for action command [" + ae.getActionCommand() + "]");
	}
	
	public ActionEvent asActionEvent()
	{
		return new ActionEvent(this, 0, name());
	}
	
	public boolean isEditorViewChange()
	{
		return (this.name().endsWith("VIEW") || this.equals(ScreenCommand.TOGGLE_ROSTER_VIEW));
	}
	
	public boolean isEditTeam()
	{
		return this.name().startsWith("EDIT_TEAM_");
	}
	
	public int getCommandIndex()
	{
		int lastUnderscoreIndex = this.name().lastIndexOf('_');
		String indexString = this.name().substring(lastUnderscoreIndex + 1);
		
		try {
			return Integer.parseInt(indexString);
		} catch (NumberFormatException nfe)
		{
			return -1;
		}
	}
}
