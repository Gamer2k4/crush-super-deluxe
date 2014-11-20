#include "crush_player.h"

bool crush_player_historical_week::write (ofstream & file_stream)
{
	if (file_stream.is_open () == 0)
	{
		return false;
	}

	file_stream.write ((char*) &m_rushing_tiles, 2);
	file_stream.write ((char*) &m_goals_scored, 2);
	file_stream.write ((char*) &m_kills_for, 2);
	file_stream.write ((char*) &m_injuries_for, 2);
	file_stream.write ((char*) &m_checks_thrown, 2);
	file_stream.write ((char*) &m_checks_landed, 2);
	file_stream.write ((char*) &m_rushing_attempts, 2);
	file_stream.write ((char*) &m_sacks_for, 2);

	file_stream.write ((char*) &m_total_rushing_tiles, 2);
	file_stream.write ((char*) &m_total_goals_scored, 2);
	file_stream.write ((char*) &m_total_kills_for, 2);
	file_stream.write ((char*) &m_total_injuries_for, 2);
	file_stream.write ((char*) &m_total_checks_thrown, 2);
	file_stream.write ((char*) &m_total_checks_landed, 2);
	file_stream.write ((char*) &m_total_rushing_attempts, 2);
	file_stream.write ((char*) &m_total_sacks_for, 2);

	file_stream.write ((char*) &m_total_value, 2);

	file_stream.write ((char*) &m_current_crush_week, 4);
	file_stream.write ((char*) &m_current_crush_season, 4);

	return true;
}

bool crush_player_historical_week::read  (ifstream & file_stream)
{
	if (file_stream.is_open () == 0)
	{
		return false;
	}

	file_stream.read ((char*) &m_rushing_tiles, 2);
	file_stream.read ((char*) &m_goals_scored, 2);
	file_stream.read ((char*) &m_kills_for, 2);
	file_stream.read ((char*) &m_injuries_for, 2);
	file_stream.read ((char*) &m_checks_thrown, 2);
	file_stream.read ((char*) &m_checks_landed, 2);
	file_stream.read ((char*) &m_rushing_attempts, 2);
	file_stream.read ((char*) &m_sacks_for, 2);

	file_stream.read ((char*) &m_total_rushing_tiles, 2);
	file_stream.read ((char*) &m_total_goals_scored, 2);
	file_stream.read ((char*) &m_total_kills_for, 2);
	file_stream.read ((char*) &m_total_injuries_for, 2);
	file_stream.read ((char*) &m_total_checks_thrown, 2);
	file_stream.read ((char*) &m_total_checks_landed, 2);
	file_stream.read ((char*) &m_total_rushing_attempts, 2);
	file_stream.read ((char*) &m_total_sacks_for, 2);

	file_stream.read ((char*) &m_total_value, 2);

	file_stream.read ((char*) &m_current_crush_week, 4);
	file_stream.read ((char*) &m_current_crush_season, 4);

	return true;
}

bool crush_player::write (ofstream & file_stream)
{
	if (file_stream.is_open () == 0)
	{
		return false;
	}

	int string_size = m_name.size ();
   	file_stream.write ((char*) &string_size, 4);
   	file_stream.write ((char*) m_name.c_str (), string_size);

	string_size = m_team_name.size ();
   	file_stream.write ((char*) &string_size, 4);
   	file_stream.write ((char*) m_team_name.c_str (), string_size);

   	file_stream.write ((char*) &m_last_crush_week, 4);
   	file_stream.write ((char*) &m_last_crush_season, 4);
   	file_stream.write ((char*) &m_dead, 4);
   	file_stream.write ((char*) &m_first_crush_week, 4);
   	file_stream.write ((char*) &m_first_crush_season, 4);

   	file_stream.write ((char*) &m_jersey_color, 1);
   	file_stream.write ((char*) &m_trim_color, 1);

   	file_stream.write ((char*) &m_ap, 1);
   	file_stream.write ((char*) &m_checking, 1);
   	file_stream.write ((char*) &m_strength, 1);
   	file_stream.write ((char*) &m_toughness, 1);
   	file_stream.write ((char*) &m_reflexes, 1);
   	file_stream.write ((char*) &m_jump, 1);
   	file_stream.write ((char*) &m_hands, 1);
   	file_stream.write ((char*) &m_dodge, 1);

	file_stream.write ((char*) &m_total_value, 2);
	file_stream.write ((char*) &m_points_to_spend, 2);
	file_stream.write ((char*) &m_race, sizeof (race_type));

	file_stream.write ((char*) &m_best_rushing_tiles, 2);
	file_stream.write ((char*) &m_best_goals_scored, 2);
	file_stream.write ((char*) &m_best_kills_for, 2);
	file_stream.write ((char*) &m_best_injuries_for, 2);
	file_stream.write ((char*) &m_best_checks_thrown, 2);
	file_stream.write ((char*) &m_best_checks_landed, 2);
	file_stream.write ((char*) &m_best_rushing_attempts, 2);
	file_stream.write ((char*) &m_best_sacks_for, 2);

	file_stream.write ((char*) &m_rushing_tiles, 2);
	file_stream.write ((char*) &m_goals_scored, 2);
	file_stream.write ((char*) &m_kills_for, 2);
	file_stream.write ((char*) &m_injuries_for, 2);
	file_stream.write ((char*) &m_checks_thrown, 2);
	file_stream.write ((char*) &m_checks_landed, 2);
	file_stream.write ((char*) &m_rushing_attempts, 2);
	file_stream.write ((char*) &m_sacks_for, 2);

	file_stream.write ((char*) &m_total_rushing_tiles, 2);
	file_stream.write ((char*) &m_total_goals_scored, 2);
	file_stream.write ((char*) &m_total_kills_for, 2);
	file_stream.write ((char*) &m_total_injuries_for, 2);
	file_stream.write ((char*) &m_total_checks_thrown, 2);
	file_stream.write ((char*) &m_total_checks_landed, 2);
	file_stream.write ((char*) &m_total_rushing_attempts, 2);
	file_stream.write ((char*) &m_total_sacks_for, 2);

	int historical_week_list_size = m_crush_player_historical_week_list.size ();
   	file_stream.write ((char*) &historical_week_list_size, 4);

   	crush_player_historical_week_list::iterator it = m_crush_player_historical_week_list.begin ();

   	while (it != m_crush_player_historical_week_list.end ())
   	{
   		crush_player_historical_week_ptr current_player_week = *it;
       	current_player_week->write (file_stream);
		it++;
	}

	return true;
}

bool crush_player::read  (ifstream & file_stream)
{
	if (file_stream.is_open () == 0)
	{
		return false;
	}

	int string_size = 0;

	char name [PLAYER_NAME_LEN];
	char team_name [TEAM_NAME_LEN];

	memset (name, 0, PLAYER_NAME_LEN);
	memset (team_name, 0, TEAM_NAME_LEN);

   	file_stream.read ((char*) &string_size, 4);

	if (string_size > PLAYER_NAME_LEN)
	{
		return false;
	}

	file_stream.read ((char*) name, string_size);
	m_name = name;
    m_name.resize (string_size);

	file_stream.read ((char*) &string_size, 4);

	if (string_size > TEAM_NAME_LEN)
	{
		return false;
	}

	file_stream.read ((char*) team_name, string_size);

	m_team_name = team_name;
   	m_team_name.resize (string_size);

   	file_stream.read ((char*) &m_last_crush_week, 4);
   	file_stream.read ((char*) &m_last_crush_season, 4);
   	file_stream.read ((char*) &m_dead, 4);
   	file_stream.read ((char*) &m_first_crush_week, 4);
   	file_stream.read ((char*) &m_first_crush_season, 4);

   	file_stream.read ((char*) &m_jersey_color, 1);
   	file_stream.read ((char*) &m_trim_color, 1);

   	file_stream.read ((char*) &m_ap, 1);
   	file_stream.read ((char*) &m_checking, 1);
   	file_stream.read ((char*) &m_strength, 1);
   	file_stream.read ((char*) &m_toughness, 1);
   	file_stream.read ((char*) &m_reflexes, 1);
   	file_stream.read ((char*) &m_jump, 1);
   	file_stream.read ((char*) &m_hands, 1);
   	file_stream.read ((char*) &m_dodge, 1);

	file_stream.read ((char*) &m_total_value, 2);
	file_stream.read ((char*) &m_points_to_spend, 2);
	file_stream.read ((char*) &m_race, sizeof (race_type));
	
	file_stream.read ((char*) &m_best_rushing_tiles, 2);
	file_stream.read ((char*) &m_best_goals_scored, 2);
	file_stream.read ((char*) &m_best_kills_for, 2);
	file_stream.read ((char*) &m_best_injuries_for, 2);
	file_stream.read ((char*) &m_best_checks_thrown, 2);
	file_stream.read ((char*) &m_best_checks_landed, 2);
	file_stream.read ((char*) &m_best_rushing_attempts, 2);
	file_stream.read ((char*) &m_best_sacks_for, 2);

	file_stream.read ((char*) &m_rushing_tiles, 2);
	file_stream.read ((char*) &m_goals_scored, 2);
	file_stream.read ((char*) &m_kills_for, 2);
	file_stream.read ((char*) &m_injuries_for, 2);
	file_stream.read ((char*) &m_checks_thrown, 2);
	file_stream.read ((char*) &m_checks_landed, 2);
	file_stream.read ((char*) &m_rushing_attempts, 2);
	file_stream.read ((char*) &m_sacks_for, 2);

	file_stream.read ((char*) &m_total_rushing_tiles, 2);
	file_stream.read ((char*) &m_total_goals_scored, 2);
	file_stream.read ((char*) &m_total_kills_for, 2);
	file_stream.read ((char*) &m_total_injuries_for, 2);
	file_stream.read ((char*) &m_total_checks_thrown, 2);
	file_stream.read ((char*) &m_total_checks_landed, 2);
	file_stream.read ((char*) &m_total_rushing_attempts, 2);
	file_stream.read ((char*) &m_total_sacks_for, 2);

	int historical_week_list_size;
   	file_stream.read ((char*) &historical_week_list_size, 4);

   	for (int historical_week_index = 0; historical_week_index < historical_week_list_size; historical_week_index++) 
   	{
   		crush_player_historical_week_ptr current_week = new crush_player_historical_week;
       	current_week->read (file_stream);
		m_crush_player_historical_week_list.push_back (current_week);
	}

	return true;
}

void crush_player::update (player & new_player_info, 
				  char jersey_color, 
				  char trim_color,
		int current_crush_week, int current_crush_season)
{
	m_total_value = new_player_info.total_value;
	m_points_to_spend = new_player_info.points_to_spend;

	m_jersey_color = jersey_color;
	m_trim_color = trim_color;

	m_last_crush_week = current_crush_week;
	m_last_crush_season = current_crush_season;

	m_race = new_player_info.race;

	if (new_player_info.rushing_tiles > m_best_rushing_tiles)
	{
		m_best_rushing_tiles = new_player_info.rushing_tiles;
	}
	if (new_player_info.goals_scored > m_goals_scored)
	{
		m_best_goals_scored = new_player_info.goals_scored;
	}
	if (new_player_info.kills_for > m_best_kills_for)
	{
		m_best_kills_for = new_player_info.kills_for;
	}
	if (new_player_info.injuries_for > m_best_injuries_for)
	{
		m_best_injuries_for = new_player_info.injuries_for;
	}
	if (new_player_info.checks_thrown > m_best_checks_thrown)
	{
		m_best_checks_thrown = new_player_info.checks_thrown;
	}
	if (new_player_info.checks_landed > m_best_checks_landed)
	{
		m_best_checks_landed = new_player_info.checks_landed;
	}
	if (new_player_info.rushing_attempts > m_best_rushing_attempts)
	{
		m_best_rushing_attempts = new_player_info.rushing_attempts;
	}
	if (new_player_info.sacks_for > m_best_sacks_for)
	{
		m_best_sacks_for = new_player_info.sacks_for;
	}

	m_rushing_tiles = new_player_info.rushing_tiles;
	m_goals_scored = new_player_info.goals_scored;
	m_kills_for = new_player_info.kills_for;
	m_injuries_for = new_player_info.injuries_for;
	m_checks_thrown = new_player_info.checks_thrown;
	m_checks_landed = new_player_info.checks_landed;
	m_rushing_attempts = new_player_info.rushing_attempts;
	m_sacks_for = new_player_info.sacks_for;

	m_total_rushing_tiles = new_player_info.total_rushing_tiles;
	m_total_goals_scored = new_player_info.total_goals_scored;
	m_total_kills_for = new_player_info.total_kills_for;
	m_total_injuries_for = new_player_info.total_injuries_for;
	m_total_checks_thrown = new_player_info.total_checks_thrown;
	m_total_checks_landed = new_player_info.total_checks_landed;
	m_total_rushing_attempts = new_player_info.total_rushing_attempts;
	m_total_sacks_for = new_player_info.total_sacks_for;

	crush_player_historical_week_ptr new_historical_week = new crush_player_historical_week (new_player_info, current_crush_week, current_crush_season ); 
	m_crush_player_historical_week_list.push_back (new_historical_week);
}

player_color_container::player_color_container (TColor trim_color,  TColor jersey_color)
{
	m_color_bitmap = new Graphics::TBitmap ();
	m_trim_tcolor = trim_color;
	m_jersey_tcolor = jersey_color;

	m_color_bitmap->Width = 32;
	m_color_bitmap->Height = 32;

	m_color_bitmap->Canvas->Brush->Color = clBlack;
	m_color_bitmap->Canvas->FillRect (Rect (0,0,32,32));
	m_color_bitmap->Canvas->Brush->Color = m_trim_tcolor;
	m_color_bitmap->Canvas->Ellipse (Rect (0,0,32,32));
	m_color_bitmap->Canvas->Brush->Color = m_jersey_tcolor;
	m_color_bitmap->Canvas->Ellipse (Rect (6,6,26,26));
}

player_color_container::~player_color_container ()
{
	if (m_color_bitmap)
	{
		delete m_color_bitmap;
	}
}
