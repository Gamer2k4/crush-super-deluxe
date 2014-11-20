#include "crush_team.h"

bool crush_team::update (team & new_team_info, int current_crush_week, int current_team_value)
{
	bool new_season = false;

	m_jersey_color = new_team_info.jersey_color;
	m_trim_color = new_team_info.trim_color;

	m_current_team_value = current_team_value;

	if (new_team_info.rushing_tiles > m_best_rushing_tiles )
	{
		m_best_rushing_tiles = new_team_info.rushing_tiles ;
	}
	if (new_team_info.kills_for > m_best_kills_for )
	{
		m_best_kills_for = new_team_info.kills_for ;
	}
	if (new_team_info.kills_against > m_best_kills_against )
	{
		m_best_kills_against = new_team_info.kills_against ;
	}
	if (new_team_info.injuries_for > m_best_injuries_for )
	{
		m_best_injuries_for = new_team_info.injuries_for ;
	}
	if (new_team_info.injuries_against > m_best_injuries_against )
	{
		m_best_injuries_against = new_team_info.injuries_against ;
	}
	if (new_team_info.checks_thrown > m_best_checks_thrown )
	{
		m_best_checks_thrown = new_team_info.checks_thrown ;
	}
	if (new_team_info.checks_landed > m_best_checks_landed )
	{
		m_best_checks_landed = new_team_info.checks_landed ;
	}
	if (new_team_info.activated_pads > m_best_activated_pads )
	{
		m_best_activated_pads = new_team_info.activated_pads ;
	}
	if (new_team_info.balls_fumbled > m_best_balls_fumbled )
	{
		m_best_balls_fumbled = new_team_info.balls_fumbled ;
	}
	if (new_team_info.rushing_attempts > m_best_rushing_attempts )
	{
		m_best_rushing_attempts = new_team_info.rushing_attempts ;
	}
	if (new_team_info.ball_control > m_best_ball_control )
	{
		m_best_ball_control = new_team_info.ball_control ;
	}
	if (new_team_info.sacks_against > m_best_sacks_against )
	{
		m_best_sacks_against = new_team_info.sacks_against ;
	}
	if (new_team_info.sacks_for > m_best_sacks_for )
	{
		m_best_sacks_for = new_team_info.sacks_for ;
	}

	if (new_team_info.wins > m_best_wins )
	{
		m_best_wins = new_team_info.wins ;
	}
	if (new_team_info.losses > m_best_losses )
	{
		m_best_losses = new_team_info.losses ;
	}
	if (new_team_info.ties > m_best_ties )
	{
		m_best_ties = new_team_info.ties ;
	}

	if (new_team_info.rushing_tiles < m_rushing_tiles)
	{
		new_season = true;
	}

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

	return new_season;
}

bool crush_team::write (ofstream & file_stream)
{
	int string_size = m_coach.size ();
   	file_stream.write ((char*) &string_size, 4);
   	file_stream.write ((char*) m_coach.c_str (), string_size);

	string_size = m_name.size ();
   	file_stream.write ((char*) &string_size, 4);
   	file_stream.write ((char*) m_name.c_str (), string_size);

	file_stream.write ((char*) &m_jersey_color, 1);
	file_stream.write ((char*) &m_trim_color, 1);

	file_stream.write ((char*) &m_current_team_value, 4);

	file_stream.write ((char*) &m_best_rushing_tiles, 2);
	file_stream.write ((char*) &m_best_kills_for, 2);
	file_stream.write ((char*) &m_best_kills_against, 2);
	file_stream.write ((char*) &m_best_injuries_for, 2);
	file_stream.write ((char*) &m_best_injuries_against, 2);
	file_stream.write ((char*) &m_best_checks_thrown, 2);
	file_stream.write ((char*) &m_best_checks_landed, 2);
	file_stream.write ((char*) &m_best_activated_pads, 2);
	file_stream.write ((char*) &m_best_balls_fumbled, 2);
	file_stream.write ((char*) &m_best_rushing_attempts, 2);
	file_stream.write ((char*) &m_best_ball_control, 2);
	file_stream.write ((char*) &m_best_sacks_against, 2);
	file_stream.write ((char*) &m_best_sacks_for, 2);
	file_stream.write ((char*) &m_best_wins, 2);
	file_stream.write ((char*) &m_best_losses, 2);
	file_stream.write ((char*) &m_best_ties, 2);
	file_stream.write ((char*) &m_rushing_tiles, 2);
	file_stream.write ((char*) &m_kills_for, 2);
	file_stream.write ((char*) &m_kills_against, 2);
	file_stream.write ((char*) &m_injuries_for, 2);
	file_stream.write ((char*) &m_injuries_against, 2);
	file_stream.write ((char*) &m_checks_thrown, 2);
	file_stream.write ((char*) &m_checks_landed, 2);
	file_stream.write ((char*) &m_activated_pads, 2);
	file_stream.write ((char*) &m_balls_fumbled, 2);
	file_stream.write ((char*) &m_rushing_attempts, 2);
	file_stream.write ((char*) &m_ball_control, 2);
	file_stream.write ((char*) &m_sacks_against, 2);
	file_stream.write ((char*) &m_sacks_for, 2);
	file_stream.write ((char*) &m_wins, 2);
	file_stream.write ((char*) &m_losses, 2);
	file_stream.write ((char*) &m_ties, 2);
	file_stream.write ((char*) &m_total_rushing_tiles, 2);
	file_stream.write ((char*) &m_total_kills_for, 2);
	file_stream.write ((char*) &m_total_kills_against, 2);
	file_stream.write ((char*) &m_total_injuries_for, 2);
	file_stream.write ((char*) &m_total_injuries_against, 2);
	file_stream.write ((char*) &m_total_checks_thrown, 2);
	file_stream.write ((char*) &m_total_checks_landed, 2);
	file_stream.write ((char*) &m_total_activated_pads, 2);
	file_stream.write ((char*) &m_total_balls_fumbled, 2);
	file_stream.write ((char*) &m_total_rushing_attempts, 2);
	file_stream.write ((char*) &m_total_ball_control, 2);
	file_stream.write ((char*) &m_total_sacks_against, 2);
	file_stream.write ((char*) &m_total_sacks_for, 2);
	file_stream.write ((char*) &m_total_wins, 2);
	file_stream.write ((char*) &m_total_losses, 2);
	file_stream.write ((char*) &m_total_ties, 2);

	int historical_week_list_size = m_crush_team_historical_week_list.size ();
   	file_stream.write ((char*) &historical_week_list_size, 4);

   	crush_team_historical_week_list::iterator it = m_crush_team_historical_week_list.begin ();

   	while (it != m_crush_team_historical_week_list.end ())
   	{
   		crush_team_historical_week_ptr current_team_week = *it;
       	current_team_week->write (file_stream);
		it++;
	}

	return true;
}

bool crush_team::read (ifstream & file_stream)
{
	if (file_stream.is_open () == 0)
	{
		return false;
	}

	int string_size = 0;

	char coach [TEAM_NAME_LEN];
	char team_name [TEAM_NAME_LEN];

	memset (coach, 0, TEAM_NAME_LEN);
	memset (team_name, 0, TEAM_NAME_LEN);

   	file_stream.read ((char*) &string_size, 4);

	if (string_size > TEAM_NAME_LEN)
	{
		return false;
	}

	file_stream.read ((char*) coach, string_size);
	m_coach = coach;
    m_coach.resize (string_size);

	file_stream.read ((char*) &string_size, 4);

	if (string_size > TEAM_NAME_LEN)
	{
		return false;
	}

	file_stream.read ((char*) team_name, string_size);

	m_name = team_name;
   	m_name.resize (string_size);

   	file_stream.read ((char*) &m_jersey_color, 1);
   	file_stream.read ((char*) &m_trim_color, 1);

	file_stream.read ((char*) &m_current_team_value, 4);

	file_stream.read ((char*) &m_best_rushing_tiles, 2);
	file_stream.read ((char*) &m_best_kills_for, 2);
	file_stream.read ((char*) &m_best_kills_against, 2);
	file_stream.read ((char*) &m_best_injuries_for, 2);
	file_stream.read ((char*) &m_best_injuries_against, 2);
	file_stream.read ((char*) &m_best_checks_thrown, 2);
	file_stream.read ((char*) &m_best_checks_landed, 2);
	file_stream.read ((char*) &m_best_activated_pads, 2);
	file_stream.read ((char*) &m_best_balls_fumbled, 2);
	file_stream.read ((char*) &m_best_rushing_attempts, 2);
	file_stream.read ((char*) &m_best_ball_control, 2);
	file_stream.read ((char*) &m_best_sacks_against, 2);
	file_stream.read ((char*) &m_best_sacks_for, 2);
	file_stream.read ((char*) &m_best_wins, 2);
	file_stream.read ((char*) &m_best_losses, 2);
	file_stream.read ((char*) &m_best_ties, 2);
	file_stream.read ((char*) &m_rushing_tiles, 2);
	file_stream.read ((char*) &m_kills_for, 2);
	file_stream.read ((char*) &m_kills_against, 2);
	file_stream.read ((char*) &m_injuries_for, 2);
	file_stream.read ((char*) &m_injuries_against, 2);
	file_stream.read ((char*) &m_checks_thrown, 2);
	file_stream.read ((char*) &m_checks_landed, 2);
	file_stream.read ((char*) &m_activated_pads, 2);
	file_stream.read ((char*) &m_balls_fumbled, 2);
	file_stream.read ((char*) &m_rushing_attempts, 2);
	file_stream.read ((char*) &m_ball_control, 2);
	file_stream.read ((char*) &m_sacks_against, 2);
	file_stream.read ((char*) &m_sacks_for, 2);
	file_stream.read ((char*) &m_wins, 2);
	file_stream.read ((char*) &m_losses, 2);
	file_stream.read ((char*) &m_ties, 2);
	file_stream.read ((char*) &m_total_rushing_tiles, 2);
	file_stream.read ((char*) &m_total_kills_for, 2);
	file_stream.read ((char*) &m_total_kills_against, 2);
	file_stream.read ((char*) &m_total_injuries_for, 2);
	file_stream.read ((char*) &m_total_injuries_against, 2);
	file_stream.read ((char*) &m_total_checks_thrown, 2);
	file_stream.read ((char*) &m_total_checks_landed, 2);
	file_stream.read ((char*) &m_total_activated_pads, 2);
	file_stream.read ((char*) &m_total_balls_fumbled, 2);
	file_stream.read ((char*) &m_total_rushing_attempts, 2);
	file_stream.read ((char*) &m_total_ball_control, 2);
	file_stream.read ((char*) &m_total_sacks_against, 2);
	file_stream.read ((char*) &m_total_sacks_for, 2);
	file_stream.read ((char*) &m_total_wins, 2);
	file_stream.read ((char*) &m_total_losses, 2);
	file_stream.read ((char*) &m_total_ties, 2);

	int historical_week_list_size;
   	file_stream.read ((char*) &historical_week_list_size, 4);

   	for (int historical_week_index = 0; historical_week_index < historical_week_list_size; historical_week_index++) 
   	{
   		crush_team_historical_week_ptr current_week = new crush_team_historical_week;
       	current_week->read (file_stream);
		m_crush_team_historical_week_list.push_back (current_week);
	}

	return true;
}

bool crush_team_historical_week::read (ifstream & file_stream)
{
	file_stream.read ((char*) &m_week_index, 4);

	file_stream.read ((char*) &m_current_team_value, 4);

	file_stream.read ((char*) &m_season_rushing_tiles, 2);
	file_stream.read ((char*) &m_season_kills_for, 2);
	file_stream.read ((char*) &m_season_kills_against, 2);
	file_stream.read ((char*) &m_season_injuries_for, 2);
	file_stream.read ((char*) &m_season_injuries_against, 2);
	file_stream.read ((char*) &m_season_checks_thrown, 2);
	file_stream.read ((char*) &m_season_checks_landed, 2);
	file_stream.read ((char*) &m_season_activated_pads, 2);
	file_stream.read ((char*) &m_season_balls_fumbled, 2);
	file_stream.read ((char*) &m_season_rushing_attempts, 2);
	file_stream.read ((char*) &m_season_ball_control, 2);
	file_stream.read ((char*) &m_season_sacks_against, 2);
	file_stream.read ((char*) &m_season_sacks_for, 2);
	file_stream.read ((char*) &m_season_wins, 2);
	file_stream.read ((char*) &m_season_losses, 2);
	file_stream.read ((char*) &m_season_ties, 2);
	file_stream.read ((char*) &m_career_rushing_tiles, 2);
	file_stream.read ((char*) &m_career_kills_for, 2);
	file_stream.read ((char*) &m_career_kills_against, 2);
	file_stream.read ((char*) &m_career_injuries_for, 2);
	file_stream.read ((char*) &m_career_injuries_against, 2);
	file_stream.read ((char*) &m_career_checks_thrown, 2);
	file_stream.read ((char*) &m_career_checks_landed, 2);
	file_stream.read ((char*) &m_career_activated_pads, 2);
	file_stream.read ((char*) &m_career_balls_fumbled, 2);
	file_stream.read ((char*) &m_career_rushing_attempts, 2);
	file_stream.read ((char*) &m_career_ball_control, 2);
	file_stream.read ((char*) &m_career_sacks_against, 2);
	file_stream.read ((char*) &m_career_sacks_for, 2);
	file_stream.read ((char*) &m_career_wins, 2);
	file_stream.read ((char*) &m_career_losses, 2);
	file_stream.read ((char*) &m_career_ties, 2);
 
	return true;
}

bool crush_team_historical_week::write (ofstream & file_stream)
{
	file_stream.write ((char*) &m_week_index, 4);

	file_stream.write ((char*) &m_current_team_value, 4);

	file_stream.write ((char*) &m_season_rushing_tiles, 2);
	file_stream.write ((char*) &m_season_kills_for, 2);
	file_stream.write ((char*) &m_season_kills_against, 2);
	file_stream.write ((char*) &m_season_injuries_for, 2);
	file_stream.write ((char*) &m_season_injuries_against, 2);
	file_stream.write ((char*) &m_season_checks_thrown, 2);
	file_stream.write ((char*) &m_season_checks_landed, 2);
	file_stream.write ((char*) &m_season_activated_pads, 2);
	file_stream.write ((char*) &m_season_balls_fumbled, 2);
	file_stream.write ((char*) &m_season_rushing_attempts, 2);
	file_stream.write ((char*) &m_season_ball_control, 2);
	file_stream.write ((char*) &m_season_sacks_against, 2);
	file_stream.write ((char*) &m_season_sacks_for, 2);
	file_stream.write ((char*) &m_season_wins, 2);
	file_stream.write ((char*) &m_season_losses, 2);
	file_stream.write ((char*) &m_season_ties, 2);
	file_stream.write ((char*) &m_career_rushing_tiles, 2);
	file_stream.write ((char*) &m_career_kills_for, 2);
	file_stream.write ((char*) &m_career_kills_against, 2);
	file_stream.write ((char*) &m_career_injuries_for, 2);
	file_stream.write ((char*) &m_career_injuries_against, 2);
	file_stream.write ((char*) &m_career_checks_thrown, 2);
	file_stream.write ((char*) &m_career_checks_landed, 2);
	file_stream.write ((char*) &m_career_activated_pads, 2);
	file_stream.write ((char*) &m_career_balls_fumbled, 2);
	file_stream.write ((char*) &m_career_rushing_attempts, 2);
	file_stream.write ((char*) &m_career_ball_control, 2);
	file_stream.write ((char*) &m_career_sacks_against, 2);
	file_stream.write ((char*) &m_career_sacks_for, 2);
	file_stream.write ((char*) &m_career_wins, 2);
	file_stream.write ((char*) &m_career_losses, 2);
	file_stream.write ((char*) &m_career_ties, 2);


	return true;
}
