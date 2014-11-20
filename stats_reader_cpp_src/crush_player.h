//---------------------------------------------------------------------------
// crush player
//---------------------------------------------------------------------------

#ifndef CRUSH_PLAYER
#define CRUSH_PLAYER

#include "smart_pointer.h"

#include "stats_reader.h"

#include <vcl0.h>
#include <fstream>
#include <list>
using namespace std;

#include <string>

class player_color_container : public reference_count
{
public:
	player_color_container (TColor trim_color,  TColor jersey_color);
	~player_color_container ();

   	bool operator== (player_color_container compare_container)
   	{
   		return ((m_trim_tcolor == compare_container.m_trim_tcolor) &&
       			(m_jersey_tcolor == compare_container.m_jersey_tcolor));
	}

	int m_image_list_index;

   	TColor m_trim_tcolor;
   	TColor m_jersey_tcolor;

	Graphics::TBitmap * m_color_bitmap;
};

typedef smart_pointer<player_color_container> player_color_container_ptr;

class crush_player_historical_week : public reference_count
{
	public:
		crush_player_historical_week () {}

		crush_player_historical_week (const player & new_player, 
									  int  current_crush_week,
									  int  current_crush_season)
		{
			m_rushing_tiles = new_player.rushing_tiles;
			m_goals_scored = new_player.goals_scored;
			m_kills_for = new_player.kills_for;
			m_injuries_for = new_player.injuries_for;
			m_checks_thrown     = new_player.checks_thrown;
			m_checks_landed = new_player.checks_landed;
			m_rushing_attempts = new_player.rushing_attempts;
			m_sacks_for = new_player.sacks_for;

			m_total_rushing_tiles = new_player.total_rushing_tiles;
			m_total_goals_scored = new_player.total_goals_scored;
			m_total_kills_for = new_player.total_kills_for;
			m_total_injuries_for = new_player.total_injuries_for;
			m_total_checks_thrown = new_player.total_checks_thrown;
			m_total_checks_landed = new_player.total_checks_landed;
			m_total_rushing_attempts = new_player.total_rushing_attempts;
			m_total_sacks_for = new_player.total_sacks_for;

			m_total_value = new_player.total_value;

			m_current_crush_week = current_crush_week;
			m_current_crush_season = current_crush_season;
		}

		bool read (ifstream & file_stream);
		bool write (ofstream & file_stream);

		short m_total_value;
 
		short m_rushing_tiles;
		short m_goals_scored;
		short m_kills_for;
		short m_injuries_for;
		short m_checks_thrown;
		short m_checks_landed;
		short m_rushing_attempts;
		short m_sacks_for;

		short m_total_rushing_tiles;
		short m_total_goals_scored;
		short m_total_kills_for;
		short m_total_injuries_for;
		short m_total_checks_thrown;
		short m_total_checks_landed;
		short m_total_rushing_attempts;
		short m_total_sacks_for;

		int m_current_crush_week;
		int m_current_crush_season;
};

typedef smart_pointer<crush_player_historical_week> crush_player_historical_week_ptr;
typedef list<crush_player_historical_week_ptr> crush_player_historical_week_list;

class crush_player : public reference_count
{
public:
	// constructor for a new player
	crush_player (const player & new_player, 
				  const char * team_name, 
				  char jersey_color, 
				  char trim_color,
				  int  current_crush_week,
				  int  current_crush_season)
	{
		m_name = new_player.name;
		m_team_name = team_name;

		m_last_crush_week = current_crush_week;
		m_last_crush_season = current_crush_season;

		m_first_crush_week = current_crush_week;
		m_first_crush_season = current_crush_season;

		m_jersey_color = jersey_color;
		m_trim_color = trim_color;

		m_ap = new_player.player_stats [1];
		m_reflexes = new_player.player_stats [2];
		m_jump = new_player.player_stats [3];
		m_checking = new_player.player_stats [4];
		m_strength = new_player.player_stats [5];
		m_toughness = new_player.player_stats [6];
		m_hands = new_player.player_stats [7];
		m_dodge = new_player.player_stats [8];

		m_total_value = new_player.total_value;
		m_points_to_spend = new_player.points_to_spend;
		m_race = new_player.race;

		m_best_rushing_tiles = new_player.rushing_tiles;
		m_best_goals_scored = new_player.goals_scored;
		m_best_kills_for = new_player.kills_for;
		m_best_injuries_for = new_player.injuries_for;
		m_best_checks_thrown = new_player.checks_thrown;
		m_best_checks_landed = new_player.checks_landed;
		m_best_rushing_attempts = new_player.rushing_attempts;
		m_best_sacks_for = new_player.sacks_for;

		m_rushing_tiles = new_player.rushing_tiles;
		m_goals_scored = new_player.goals_scored;
		m_kills_for = new_player.kills_for;
		m_injuries_for = new_player.injuries_for;
		m_checks_thrown     = new_player.checks_thrown    ;
		m_checks_landed = new_player.checks_landed;
		m_rushing_attempts = new_player.rushing_attempts;
		m_sacks_for = new_player.sacks_for;

		m_total_rushing_tiles = new_player.total_rushing_tiles;
		m_total_goals_scored = new_player.total_goals_scored;
		m_total_kills_for = new_player.total_kills_for;
		m_total_injuries_for = new_player.total_injuries_for;
		m_total_checks_thrown = new_player.total_checks_thrown;
		m_total_checks_landed = new_player.total_checks_landed;
		m_total_rushing_attempts = new_player.total_rushing_attempts;
		m_total_sacks_for = new_player.total_sacks_for;

		m_player_color_container = NULL;

		crush_player_historical_week_ptr new_historical_week = new crush_player_historical_week (new_player, current_crush_week, current_crush_season ); 
		m_crush_player_historical_week_list.push_back (new_historical_week);

		m_dead = false;
	}

	int m_first_crush_week;
	int m_first_crush_season;
	int m_last_crush_week;
	int m_last_crush_season;
	bool m_dead;

	// constructor for a player to read from disk
	crush_player () {}

	~crush_player () {}

	void update (player & new_player_info, 
				  char jersey_color, 
				  char trim_color,
			int current_crush_week, int current_crush_season);

	bool write (ofstream & file_stream);
	bool read  (ifstream & file_stream);

	string m_name;
	string m_team_name;

  	char m_jersey_color;
  	char m_trim_color;
//private:
	byte m_ap;
	byte m_checking;
	byte m_strength;
	byte m_toughness;
	byte m_reflexes;
	byte m_jump;
	byte m_hands;
	byte m_dodge;

	crush_player_historical_week_list m_crush_player_historical_week_list;

	player_color_container_ptr m_player_color_container;

	short m_total_value;
	short m_points_to_spend;
	race_type m_race;

	short m_best_rushing_tiles;
	short m_best_goals_scored;
	short m_best_kills_for;
	short m_best_injuries_for;
	short m_best_checks_thrown;
	short m_best_checks_landed;
	short m_best_rushing_attempts;
	short m_best_sacks_for;

	short m_rushing_tiles;
	short m_goals_scored;
	short m_kills_for;
	short m_injuries_for;
	short m_checks_thrown    ;
	short m_checks_landed;
	short m_rushing_attempts;
	short m_sacks_for;

	short m_total_rushing_tiles;
	short m_total_goals_scored;
	short m_total_kills_for;
	short m_total_injuries_for;
	short m_total_checks_thrown;
	short m_total_checks_landed;
	short m_total_rushing_attempts;
	short m_total_sacks_for;
};

typedef smart_pointer<crush_player> crush_player_ptr;

#endif	
