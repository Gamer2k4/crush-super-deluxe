package main.presentation.common;

import java.awt.event.ActionEvent;

import main.data.entities.Race;
import main.data.entities.Skill;
import main.presentation.screens.ScreenType;

public enum ScreenCommand
{	
	MAIN_SCREEN,
	EXHIBITION_TEAM_SELECT,
	TOURNAMENT_TEAM_SELECT,
	LEAGUE_TEAM_SELECT,
	EXIT_TEAM_EDITOR_BACK,
	EXIT_TEAM_EDITOR_DONE,
	BEGIN_GAME,
	END_GAME,
	
	EXHIBITION_PREGAME,
	TOURNAMENT_PREGAME,
	LEAGUE_PREGAME,
	
	EXHIBITION_VICTORY,
	TOURNAMENT_VICTORY,
	LEAGUE_VICTORY,
	
	BLOCK_MOUSE_CLICKS,
	UNBLOCK_MOUSE_CLICKS,

	CHANGE_WIN_REQUIREMENT,
	CHANGE_BUDGET,
	CHANGE_PACE,
	CHANGE_TURNS,
	
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
	SHOW_SACKING,
	SHOW_CARNAGE,
	SHOW_OVERVIEW,
	SHOW_MVP,
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

	SCROLL_ROSTER_UP,
	SCROLL_ROSTER_DOWN,

	CONTROLLER_HUMAN,
	CONTROLLER_AI,
	
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
	
	EQUIP_SCROLL_TEAM_GEAR_UP,
	EQUIP_SCROLL_TEAM_GEAR_DOWN,
	EQUIP_SELECT_PADS,
	EQUIP_SELECT_BELTS,
	EQUIP_SELECT_BOOTS,
	EQUIP_SELECT_GLOVES,
	EQUIP_SCROLL_LEFT,
	EQUIP_SCROLL_RIGHT,
	EQUIP_BUY,
	EQUIP_SELL,
	EQUIP_SELECT_0,
	EQUIP_SELECT_1,
	EQUIP_SELECT_2,
	EQUIP_SELECT_3,
	EQUIP_SCROLL_UP,
	EQUIP_SCROLL_DOWN,
	
	DOCBOT_UP,
	DOCBOT_DOWN,
	
	DOCBOT_00,
	DOCBOT_01,
	DOCBOT_10,
	DOCBOT_11,
	DOCBOT_20,
	DOCBOT_21,
	DOCBOT_30,
	DOCBOT_31,
	
	GAIN_SKILL_TERROR,
	GAIN_SKILL_JUGGERNAUT,
	GAIN_SKILL_TACTICS,
	GAIN_SKILL_VICIOUS,
	GAIN_SKILL_BRUTAL,
	GAIN_SKILL_CHECKMASTER,
	GAIN_SKILL_STALWART,
	GAIN_SKILL_GUARD,
	GAIN_SKILL_RESILIENT,
	GAIN_SKILL_CHARGE,
	GAIN_SKILL_BOXING,
	GAIN_SKILL_COMBO,
	GAIN_SKILL_QUICKENING,
	GAIN_SKILL_GYMNASTICS,
	GAIN_SKILL_JUGGLING,
	GAIN_SKILL_SCOOP,
	GAIN_SKILL_STRIP,
	GAIN_SKILL_JUDO,
	GAIN_SKILL_FIST_OF_IRON,
	GAIN_SKILL_DOOMSTRIKE,
	GAIN_SKILL_AWE,
	GAIN_SKILL_STOIC,
	GAIN_SKILL_LEADER,
	GAIN_SKILL_SENSEI,
	GAIN_SKILL_SLY,
	GAIN_SKILL_INTUITION,
	GAIN_SKILL_HEALER,
	GAIN_SKILL_KARMA,

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
	
	POPUP_YES,
	POPUP_NO,
	
	GAME_MAIN_PANEL_CLICK,
	GAME_MINIMAP_CLICK,
	GAME_SELECT_PLAYER_1,
	GAME_SELECT_PLAYER_2,
	GAME_SELECT_PLAYER_3,
	GAME_SELECT_PLAYER_4,
	GAME_SELECT_PLAYER_5,
	GAME_SELECT_PLAYER_6,
	GAME_SELECT_PLAYER_7,
	GAME_SELECT_PLAYER_8,
	GAME_SELECT_PLAYER_9,
	GAME_TOGGLE_STATS_PANEL,
	GAME_TOGGLE_HELP_PANEL,
	GAME_MOVE_ACTION,
	GAME_CHECK_ACTION,
	GAME_JUMP_ACTION,
	GAME_HANDOFF_ACTION,
	GAME_PREV_PLAYER,
	GAME_NEXT_PLAYER,
	GAME_STATS_CST,
	GAME_STATS_ARH,
	GAME_END_TURN,
	GAME_TIMEOUT,

	POOL_DRAFT_GENERAL_VIEW,
	POOL_DRAFT_DETAILED_VIEW,
	DRAFT_SCROLL_UP,
	DRAFT_SCROLL_DOWN,
	DRAFT_SELECT_PLAYER_0,
	DRAFT_SELECT_PLAYER_1,
	DRAFT_SELECT_PLAYER_2,
	DRAFT_SELECT_PLAYER_3,
	DRAFT_SELECT_PLAYER_4,
	DRAFT_SELECT_PLAYER_5,
	DRAFT_SELECT_PLAYER_6,
	DRAFT_SELECT_PLAYER_7,
	DRAFT_SELECT_PLAYER_8,
	DRAFT_SELECT_PLAYER_9,
	DRAFT_SELECT_PLAYER_10,
	
	STATS_SCROLL_PREV,
	STATS_SCROLL_NEXT,
	STATS_BACK,
	STATS_TCARNAGE,
	STATS_TCHECK,
	STATS_TMISC,
	STATS_TRUSH,
	STATS_SORT_COLUMN_0,
	STATS_SORT_COLUMN_1,
	STATS_SORT_COLUMN_2,
	STATS_SORT_COLUMN_3;
	
	public static ScreenCommand fromValue(String string)
	{
		for (ScreenCommand command : values())
		{
			if (command.name().equals(string))
				return command;
		}
		
		throw new IllegalArgumentException("No ScreenCommand exists for value [" + string + "]");
	}
	
	public static ScreenCommand fromActionEvent(ActionEvent ae)
	{
		return fromValue(ae.getActionCommand());
	}

	public static ScreenCommand fromScreenType(ScreenType screenType)
	{
		return fromValue(screenType.name());
	}
	
	public static ScreenCommand editTeam(int teamIndex)
	{
		return ScreenCommand.fromValue("EDIT_TEAM_" + teamIndex);
	}
	
	public static ScreenCommand changeDocbotOption(int option, int level)
	{
		return ScreenCommand.valueOf("DOCBOT_" + option + "" + level);
	}
	
	public static ScreenCommand gainSkill(Skill skill)
	{
		return ScreenCommand.valueOf("GAIN_SKILL_" + skill.name());
	}
	
	public static ScreenCommand stockDraftSelect(Race race)
	{
		return ScreenCommand.valueOf("STOCK_DRAFT_SELECT_" + race.name());
	}
	
	public ActionEvent asActionEvent()
	{
		return new ActionEvent(this, 0, name());
	}
	
	public boolean isEditorViewChange()
	{
		return (this.name().endsWith("VIEW") || this.equals(ScreenCommand.TOGGLE_ROSTER_VIEW));
	}
	
	public boolean isTeamSelect()
	{
		return this.name().endsWith("_TEAM_SELECT");
	}
	
	public boolean isEditTeam()
	{
		return this.name().startsWith("EDIT_TEAM_");
	}
	
	public boolean isEventPregame()
	{
		return this.name().endsWith("_PREGAME");
	}
	
	public boolean isGameSelectPlayer()
	{
		return this.name().startsWith("GAME_SELECT_PLAYER_");
	}
	
	public boolean isGainSkill()
	{
		return this.name().startsWith("GAIN_SKILL_");
	}
	
	public boolean isStockDraftSelect()
	{
		return this.name().startsWith("STOCK_DRAFT_SELECT_");
	}
	
	public boolean isEquipSelect()
	{
		return this.name().startsWith("EQUIP_SELECT_");
	}
	
	public boolean isStatColumnSort()
	{
		return this.name().startsWith("STATS_SORT_COLUMN_");
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
	
	public Skill getSkillToGain()
	{
		if (!isGainSkill())
			return null;
		
		String skillName = this.name().substring(11);
		return Skill.valueOf(skillName);
	}
	
	public Race getRaceToDraft()
	{
		if (!isStockDraftSelect())
			return null;
		
		String raceName = this.name().substring(19);
		return Race.valueOf(raceName);
	}
}
