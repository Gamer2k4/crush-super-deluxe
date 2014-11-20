//---------------------------------------------------------------------------
// crush team
//---------------------------------------------------------------------------

#ifndef CRUSH_TEAM
#define CRUSH_TEAM

#include "smart_pointer.h"
#include "stats_reader.h"

#include <string>
#include <fstream>
#include <list>
using namespace std;

class crush_team_historical_week : public reference_count
{
public:

	crush_team_historical_week () {}

	crush_team_historical_week (const team & new_team_info, int week_index, int current_team_value)
	{
		m_week_index = week_index;

		m_current_team_value = current_team_value;

		m_season_rushing_tiles = new_team_info.rushing_tiles ;
		m_season_kills_for = new_team_info.kills_for ;
		m_season_kills_against = new_team_info.kills_against ;
		m_season_injuries_for = new_team_info.injuries_for ;
		m_season_injuries_against = new_team_info.injuries_against ;
		m_season_checks_thrown = new_team_info.checks_thrown ;
		m_season_checks_landed = new_team_info.checks_landed ;
		m_season_activated_pads = new_team_info.activated_pads ;
		m_season_balls_fumbled = new_team_info.balls_fumbled ;
		m_season_rushing_attempts = new_team_info.rushing_attempts ;
		m_season_ball_control = new_team_info.ball_control ;
		m_season_sacks_against = new_team_info.sacks_against ;
		m_season_sacks_for = new_team_info.sacks_for ;

		m_season_wins = new_team_info.wins ;
		m_season_losses = new_team_info.losses ;
		m_season_ties = new_team_info.ties ;

		m_career_rushing_tiles = new_team_info.total_rushing_tiles ;
		m_career_kills_for = new_team_info.total_kills_for ;
		m_career_kills_against = new_team_info.total_kills_against ;
		m_career_injuries_for = new_team_info.total_injuries_for ;
		m_career_injuries_against = new_team_info.total_injuries_against ;
		m_career_checks_thrown = new_team_info.total_checks_thrown ;
		m_career_checks_landed = new_team_info.total_checks_landed ;
		m_career_activated_pads = new_team_info.total_activated_pads ;
		m_career_balls_fumbled = new_team_info.total_balls_fumbled ;
		m_career_rushing_attempts = new_team_info.total_rushing_attempts ;
		m_career_ball_control = new_team_info.total_ball_control ;
		m_career_sacks_against = new_team_info.total_sacks_against ;
		m_career_sacks_for = new_team_info.total_sacks_for ;

		m_career_wins = new_team_info.total_wins ;
		m_career_losses = new_team_info.total_losses ;
		m_career_ties = new_team_info.total_ties ;

	}

	~crush_team_historical_week () {}

	bool read (ifstream & file_stream);
	bool write (ofstream & file_stream);

	int	m_week_index;

	int m_current_team_value;
	
	short m_season_rushing_tiles;
	short m_season_kills_for;
	short m_season_kills_against ;
	short m_season_injuries_for ;
	short m_season_injuries_against ;
	short m_season_checks_thrown;
	short m_season_checks_landed;
	short m_season_activated_pads;
	short m_season_balls_fumbled;
	short m_season_rushing_attempts;
	short m_season_ball_control;
	short m_season_sacks_against;
	short m_season_sacks_for;

	short m_season_wins;
	short m_season_losses;
	short m_season_ties;

	short m_career_rushing_tiles;
	short m_career_kills_for;
	short m_career_kills_against ;
	short m_career_injuries_for ;
	short m_career_injuries_against ;
	short m_career_checks_thrown;
	short m_career_checks_landed;
	short m_career_activated_pads;
	short m_career_balls_fumbled;
	short m_career_rushing_attempts;
	short m_career_ball_control;
	short m_career_sacks_against;
	short m_career_sacks_for;

	short m_career_wins;
	short m_career_losses;
	short m_career_ties;
};

typedef smart_pointer<crush_team_historical_week> crush_team_historical_week_ptr;
typedef list<crush_team_historical_week_ptr> crush_team_historical_week_list;

class crush_team : public reference_count
{
public:
	crush_team (const team & new_team_info, int current_crush_week, int current_team_value)
	{
		m_current_team_value = current_team_value;

		m_name = new_team_info.name;
		m_coach = new_team_info.coach;
		m_jersey_color = new_team_info.jersey_color;
		m_trim_color = new_team_info.trim_color;

		m_best_rushing_tiles = new_team_info.rushing_tiles ;
		m_best_kills_for = new_team_info.kills_for ;
		m_best_kills_against = new_team_info.kills_against ;
		m_best_injuries_for = new_team_info.injuries_for ;
		m_best_injuries_against = new_team_info.injuries_against ;
		m_best_checks_thrown = new_team_info.checks_thrown ;
		m_best_checks_landed = new_team_info.checks_landed ;
		m_best_activated_pads = new_team_info.activated_pads ;
		m_best_balls_fumbled = new_team_info.balls_fumbled ;
		m_best_rushing_attempts = new_team_info.rushing_attempts ;
		m_best_ball_control = new_team_info.ball_control ;
		m_best_sacks_against = new_team_info.sacks_against ;
		m_best_sacks_for = new_team_info.sacks_for ;

		m_best_wins = new_team_info.wins ;
		m_best_losses = new_team_info.losses ;
		m_best_ties = new_team_info.ties ;
		
		m_rushing_tiles = new_team_info.rushing_tiles ;
		m_kills_for = new_team_info.kills_for ;
		m_kills_against = new_team_info.kills_against ;
		m_injuries_for = new_team_info.injuries_for ;
		m_injuries_against = new_team_info.injuries_against ;
		m_checks_thrown = new_team_info.checks_thrown ;
		m_checks_landed = new_team_info.checks_landed ;
		m_activated_pads = new_team_info.activated_pads ;
		m_balls_fumbled = new_team_info.balls_fumbled ;
		m_rushing_attempts = new_team_info.rushing_attempts ;
		m_ball_control = new_team_info.ball_control ;
		m_sacks_against = new_team_info.sacks_against ;
		m_sacks_for = new_team_info.sacks_for ;

		m_wins = new_team_info.wins ;
		m_losses = new_team_info.losses ;
		m_ties = new_team_info.ties ;

		m_total_rushing_tiles = new_team_info.total_rushing_tiles;
		m_total_kills_for = new_team_info.total_kills_for;
		m_total_kills_against = new_team_info.total_kills_against ;
		m_total_injuries_for = new_team_info.total_injuries_for ;
		m_total_injuries_against = new_team_info.total_injuries_against ;
		m_total_checks_thrown = new_team_info.total_checks_thrown;
		m_total_checks_landed = new_team_info.total_checks_landed;
		m_total_activated_pads = new_team_info.total_activated_pads;
		m_total_balls_fumbled = new_team_info.total_balls_fumbled;
		m_total_rushing_attempts = new_team_info.total_rushing_attempts;
		m_total_ball_control = new_team_info.total_ball_control;
		m_total_sacks_against = new_team_info.total_sacks_against;
		m_total_sacks_for = new_team_info.total_sacks_for;

		m_total_wins = new_team_info.total_wins;
		m_total_losses = new_team_info.total_losses;
		m_total_ties = new_team_info.total_ties;

		crush_team_historical_week_ptr new_historical_week = new crush_team_historical_week (new_team_info, current_crush_week, current_team_value); 
		m_crush_team_historical_week_list.push_back (new_historical_week);
	}

	crush_team () {}
	~crush_team () {}

	string m_name;
	string m_coach;

	int m_current_team_value;

	char m_jersey_color;
	char m_trim_color;

	crush_team_historical_week_list m_crush_team_historical_week_list;

	bool update (team & new_team_info, int current_crush_week, int current_team_value);

	bool write (ofstream & file_stream); 
	bool read (ifstream & file_stream); 

//private:
	short m_rushing_tiles ;
	short m_kills_for ;
	short m_kills_against ;
	short m_injuries_for ;
	short m_injuries_against ;
	short m_checks_thrown ;
	short m_checks_landed ;
	short m_activated_pads ;
	short m_balls_fumbled ;
	short m_rushing_attempts ;
	short m_ball_control ;
	short m_sacks_against ;
	short m_sacks_for ;

	short m_wins ;
	short m_losses ;
	short m_ties ;

	short m_total_rushing_tiles;
	short m_total_kills_for;
	short m_total_kills_against ;
	short m_total_injuries_for ;
	short m_total_injuries_against ;
	short m_total_checks_thrown;
	short m_total_checks_landed;
	short m_total_activated_pads;
	short m_total_balls_fumbled;
	short m_total_rushing_attempts;
	short m_total_ball_control;
	short m_total_sacks_against;
	short m_total_sacks_for;

	short m_total_wins;
	short m_total_losses;
	short m_total_ties;

	short m_best_rushing_tiles;
	short m_best_kills_for;
	short m_best_kills_against ;
	short m_best_injuries_for ;
	short m_best_injuries_against ;
	short m_best_checks_thrown;
	short m_best_checks_landed;
	short m_best_activated_pads;
	short m_best_balls_fumbled;
	short m_best_rushing_attempts;
	short m_best_ball_control;
	short m_best_sacks_against;
	short m_best_sacks_for;

	short m_best_wins;
	short m_best_losses;
	short m_best_ties;

};

typedef smart_pointer<crush_team> crush_team_ptr;

#endif
