//---------------------------------------------------------------------------
// season statistics
//---------------------------------------------------------------------------

#include "smart_pointer.h"

#include "stats_reader.h"

#include <fstream>
using namespace std;

#include <string>

class team_season_statistics : public reference_count
{
public:
        team_season_statistics (const team & new_team)
        {
			m_wins = new_team.wins  ;
			m_losses = new_team.losses  ;
			m_ties = new_team.ties  ;

			m_rushing_attempts = new_team.rushing_attempts
			m_rushing_tiles = new_team.rushing_tiles
			m_activated_pads; = new_team.activated_pads
			m_balls_fumbled = new_team.balls_fumbled
			m_ball_control = new_team.ball_control
			m_sacks_against = new_team.sacks_against
			m_sacks_for = new_team.sacks_for

			m_kills_for = new_team.kills_for;
			m_kills_against = new_team.kills_against  ;
			m_injuries_for = new_team.injuries_for  ;
			m_injuries_against = new_team.injuries_against  ;

			m_checks_thrown = new_team.checks_thrown;
			m_checks_landed = new_team.checks_landed;
        }

        ~team_season_statistics () {}

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

class player_season_statistics : public reference_count
{
public:
        player_season_statistics (const player & new_player)
        {
			m_rushing_attemps = new_player.rushing_attemps;
			m_rushing_tiles = new_player.rushing_tiles;
			m_sacks_for = new_player.sacks_for;

			m_goals_scored = new_player.goals_scored;
			m_kills_for = new_player.kills_for;
			m_injuries_for = new_player.injuries_for;

			m_checks_thrown = new_player.checks_thrown;
			m_checks_landed = new_player.checks_landed;
        }

        ~player_season_statistics () {}

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

typedef smart_pointer<team_season_statistics> team_season_statistics_ptr;
typedef smart_pointer<player_season_statistics> player_season_statistics_ptr;
