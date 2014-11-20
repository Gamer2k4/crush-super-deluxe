//---------------------------------------------------------------------------
// career statistics
//---------------------------------------------------------------------------

#include "smart_pointer.h"

#include "stats_reader.h"

#include <fstream>
using namespace std;

#include <string>

class team_career_statistics : public reference_count
{
public:
        team_career_statistics (const team & new_team)
        {
			m_wins = new_team.total_wins  ;
			m_losses = new_team.total_losses  ;
			m_ties = new_team.total_ties  ;

			m_rushing_attempts = new_team.total_rushing_attempts
			m_rushing_tiles = new_team.total_rushing_tiles
			m_activated_pads; = new_team.total_activated_pads
			m_balls_fumbled = new_team.total_balls_fumbled
			m_ball_control = new_team.total_ball_control
			m_sacks_against = new_team.total_sacks_against
			m_sacks_for = new_team.total_sacks_for

			m_kills_for = new_team.total_kills_for;
			m_kills_against = new_team.total_kills_against  ;
			m_injuries_for = new_team.total_injuries_for  ;
			m_injuries_against = new_team.total_injuries_against  ;

			m_checks_thrown = new_team.total_checks_thrown;
			m_checks_landed = new_team.total_checks_landed;
        }

        ~team_career_statistics () {}

        bool write (ofstream & file_stream)
        {
        }
private:
        short m_wins ;
        short m_losses ;
        short m_ties ;

        short m_rushing_attempts;
        short m_rushing_tiles;
        short m_activated_pads;
        short m_balls_fumbled;
        short m_ball_control;
        short m_sacks_against;
        short m_sacks_for;

        short m_kills_for;
        short m_kills_against ;
        short m_injuries_for ;
        short m_injuries_against ;

        short m_checks_thrown;
        short m_checks_landed;
};

class player_career_statistics : public reference_count
{
public:
        player_career_statistics (const player & new_player)
        {
			m_rushing_attemps = new_player.total_rushing_attemps;
			m_rushing_tiles = new_player.total_rushing_tiles;
			m_sacks_for = new_player.total_sacks_for;

			m_goals_scored = new_player.total_goals_scored;
			m_kills_for = new_player.total_kills_for;
			m_injuries_for = new_player.total_injuries_for;

			m_checks_thrown = new_player.total_checks_thrown;
			m_checks_landed = new_player.total_checks_landed;
        }

        ~player_career_statistics () {}

        bool write (ofstream & file_stream)
        {
        }
private:
        short m_rushing_attemps;
        short m_rushing_tiles;
        short m_sacks_for;

        short m_goals_scored;
        short m_kills_for;
        short m_injuries_for;

        short m_checks_thrown;
        short m_checks_landed;
};

typedef smart_pointer<team_career_statistics> team_career_statistics_ptr;
typedef smart_pointer<player_career_statistics> player_career_statistics_ptr;
