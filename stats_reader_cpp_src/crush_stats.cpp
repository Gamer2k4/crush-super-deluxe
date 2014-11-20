//---------------------------------------------------------------------------
// crush stats
//---------------------------------------------------------------------------

#include "crush_stats.h"

TColor crush_statistics::get_color_for_crush_color (crush_color color)
{
	switch (color)
	{
		case crush_red:
			return clRed;
		case crush_light_green:
			return clLime;
		case crush_blue:
			return clNavy;
		case crush_pink:
			return clFuchsia;
		case crush_light_blue:
			return clBlue;
		case crush_beige:
			return 0xb3dbf0;
		case crush_orange:
			return 0x0e8cfe;
		case crush_aqua:
			return clAqua;
		case crush_purple:
			return clPurple;
		case crush_red_brown:
			return 0x2d449f;
		case crush_dark_brown:
			return 0x354466;
		case crush_light_brown:
			return 0x7387b7;
		case crush_gold:
			return 0x3db9d8;
		case crush_light_purple:
			return 0xf4b0f2;
		case crush_white:
			return clWhite;
		case crush_yellow:
			return clYellow;
		case crush_green:
			return clGreen;
		case crush_black:
			return clBlack;
	}
}

const char * crush_statistics::get_name_for_race (race_type race)
{
	switch (race)
	{	
		case human:
			return "human";
		case gronk:
			return "gronk";
		case frog:
			return "frog";
		case dragoran:
			return "dragoran";
		case bug:
			return "bug";
		case slith:
			return "slith";
		case kurgan:
			return "kurgan";
		case bot:
			return "bot";
	}
}

#define CURRENT_FILE_VERSION 0xabcdF9AA 

bool crush_statistics::read_crush_statistics (const char * crush_statistics_file)
{
	player_color_list::iterator color_it;
	TColor trim, jersey;

	m_in_file_stream.open (crush_statistics_file, ios::in | ios::binary);

	if (m_in_file_stream.is_open () == 0)
	{
		return false;

	}

	int crush_file_version;

   	m_in_file_stream.read ((char*) &crush_file_version, 4);

	if (crush_file_version != CURRENT_FILE_VERSION)
	{
		m_in_file_stream.close ();
		return false;
	}

	int current_crush_week;
	m_in_file_stream.read ((char*) &current_crush_week, 4);
	m_current_crush_week = current_crush_week;
	int display_crush_week;
	m_in_file_stream.read ((char*) &display_crush_week, 4);
	m_display_crush_week = display_crush_week;
	int current_crush_season;
	m_in_file_stream.read ((char*) &current_crush_season, 4);
	m_current_crush_season = current_crush_season;

	int player_list_size = 0;
   	int team_list_size = 0;

   	m_in_file_stream.read ((char*) &player_list_size, 4);
   	m_in_file_stream.read ((char*) &team_list_size, 4);

	player_color_container_ptr current_player_color_container;
	player_color_container_ptr search_player_color_container;

	for (int player_list_index = 0; player_list_index < player_list_size; player_list_index++)
	{
		crush_player_ptr current_player = new crush_player;

		if (!current_player->read (m_in_file_stream))
		{
			m_in_file_stream.close ();
			return false;
		}

		trim = get_color_for_crush_color ( current_player->m_trim_color);
		jersey = get_color_for_crush_color ( current_player->m_jersey_color);

		search_player_color_container = new player_color_container ( trim, jersey );

		color_it = find(m_player_color_list.begin(), m_player_color_list.end(), search_player_color_container);
		if (color_it == m_player_color_list.end())
		{
			current_player->m_player_color_container = add_color (trim, jersey);
		}
		else
		{
			current_player->m_player_color_container = *color_it;
		}

    	m_crush_player_list.push_back (current_player);
	}

   	for (int team_list_index = 0; team_list_index < team_list_size; team_list_index++)
  	{
   		crush_team_ptr current_team = new crush_team;

       	if (!current_team->read (m_in_file_stream))
      	{
			m_in_file_stream.close ();
      		return false;
      	}

    	m_crush_team_list.push_back (current_team);
	}

	m_in_file_stream.close ();

	return true;
}

bool crush_statistics::write_crush_statistics (const char * crush_statistics_file)
{
	m_out_file_stream.open (crush_statistics_file, ios::out | ios::binary);

	if (m_out_file_stream.is_open () == 0)
	{
		return false;
	}

	int crush_file_version = CURRENT_FILE_VERSION;

   	m_out_file_stream.write ((char*) &crush_file_version, 4);
	m_out_file_stream.write ((char*) &m_current_crush_week, 4);
	m_out_file_stream.write ((char*) &m_display_crush_week, 4);
	m_out_file_stream.write ((char*) &m_current_crush_season, 4);

	int player_list_size = m_crush_player_list.size ();
   	int team_list_size = m_crush_team_list.size ();

   	m_out_file_stream.write ((char*) &player_list_size, 4);
   	m_out_file_stream.write ((char*) &team_list_size, 4);

   	crush_player_list::iterator it = m_crush_player_list.begin ();

   	while (it != m_crush_player_list.end ())
   	{
   		crush_player_ptr current_player = *it;

       	if (!current_player->write (m_out_file_stream))
       {
			m_out_file_stream.close ();
       		return false;
      	}

       	it++;
	}

   	crush_team_list::iterator team_it = m_crush_team_list.begin ();

  	while (team_it != m_crush_team_list.end ())
  	{
   		crush_team_ptr current_team = *team_it;

       	if (!current_team->write (m_out_file_stream))
      	{
			m_out_file_stream.close ();
      		return false;
      	}

       	team_it++;
	}

	m_out_file_stream.close ();

   	return true;
}

void crush_statistics::clear_crush_statistics ()
{
	m_crush_player_list.clear ();
	m_crush_team_list.clear ();
	m_player_color_list.clear ();
}

player_color_container_ptr crush_statistics::add_color ( TColor trim, TColor jersey)
{
	player_color_container_ptr new_player_color_container;
	new_player_color_container = new player_color_container ( trim , jersey );
	m_player_color_list.push_back (new_player_color_container);
	return new_player_color_container;
}

bool player_compare (crush_player_ptr player1, crush_player_ptr player2)
{
	return (player1->m_total_value < player2->m_total_value);
}

bool crush_statistics::update_crush_statistics (const team_container * league_array, int size)
{
	player_color_list::iterator color_it;
	crush_player_ptr new_crush_player;
	crush_team_ptr   new_crush_team;

	bool season_changed = false;

	TColor trim, jersey;

	player_color_container_ptr search_player_color_container;

	m_current_crush_week++;
	m_display_crush_week++;

	for (int team_index = 0; team_index < size; team_index++)
	{
		int current_team_value = 0;

		for (int player_index = 0; player_index < PLAYER_COUNT; player_index++)
		{
			if (!strlen ( league_array[team_index].player_array [player_index].name ))
			{
				continue;
			}

			crush_player_list::iterator it = m_crush_player_list.begin ();

			while (it != m_crush_player_list.end ())
			{
				crush_player_ptr current_player = *it;

				if (!strcmp (league_array[team_index].player_array [player_index].name, current_player->m_name.c_str()) &&
				   (!strcmp (league_array[team_index].team_info->name, current_player->m_team_name.c_str())))
				{
					// already in the player table, update it
					current_player->update (league_array[team_index].player_array [player_index], 
												league_array [team_index].team_info->jersey_color,
												league_array [team_index].team_info->trim_color,
												m_current_crush_week, m_current_crush_season);

					current_team_value += current_player->m_total_value;

					goto done;
				}

				it++;
			}
			// not in player table, add it
			new_crush_player = new crush_player (league_array[team_index].player_array [player_index],
												league_array [team_index].team_info->name,
												league_array [team_index].team_info->jersey_color,
												league_array [team_index].team_info->trim_color,
												m_current_crush_week,
												m_current_crush_season
												);


			trim = get_color_for_crush_color ( league_array [team_index].team_info->trim_color);
			jersey = get_color_for_crush_color ( league_array [team_index].team_info->jersey_color);

			search_player_color_container = new player_color_container ( trim, jersey );

			color_it = find(m_player_color_list.begin(), m_player_color_list.end(), search_player_color_container);
			if (color_it == m_player_color_list.end())
			{
				new_crush_player->m_player_color_container = add_color (trim, jersey);
			}
			else
			{
				new_crush_player->m_player_color_container = *color_it;
			}

			m_crush_player_list.push_back (new_crush_player);

			current_team_value += new_crush_player->m_total_value;

			done:
		}

		crush_team_list::iterator team_it = m_crush_team_list.begin ();

		while (team_it != m_crush_team_list.end ())
		{
			crush_team_ptr current_team = *team_it;

			if (!strcmp (league_array[team_index].team_info->name, current_team->m_name.c_str()) &&
				!strcmp (league_array[team_index].team_info->coach, current_team->m_coach.c_str ()))
			{
				// update it
				season_changed = current_team->update ( *league_array [team_index].team_info, m_current_crush_week, current_team_value );

				goto team_done;
			}

			team_it++;
		}

		// not in team table ... add it
		new_crush_team = new crush_team (*league_array [team_index].team_info, m_current_crush_week, current_team_value);

		m_crush_team_list.push_back (new_crush_team);

		team_done:
	}

	// kill dead players
	crush_player_list::iterator it = m_crush_player_list.begin ();

	while (it != m_crush_player_list.end ())
	{
		crush_player_ptr current_player = *it;

		if (current_player->m_last_crush_week != m_current_crush_week)
		{
			current_player->m_dead = true;
		}

		it++;
	}

	if (season_changed)
	{
		m_current_crush_season++;
		m_display_crush_week = 0;
	}

	return true;
}

bool crush_statistics::read_team_information (const char * league_information)
{
	ifstream file_stream;

	team_container league_array [TEAM_COUNT];

	file_stream.open (league_information, ios::in | ios::binary);

	if (file_stream.is_open () == 0)
	{
		return false;
	}

	int x = sizeof (player);
	int y = sizeof (team);

	for (int team_index = 0; team_index < TEAM_COUNT; team_index++)
	{
		file_stream.read ((char*)league_array [team_index].player_array, PLAYER_COUNT * sizeof (player));
		league_array [team_index].team_info = new team;
		file_stream.read ((char*)league_array [team_index].team_info, sizeof (team));
	}

	file_stream.close ();

	update_crush_statistics (league_array, TEAM_COUNT);

	for (int team_index = 0; team_index < TEAM_COUNT; team_index++)
	{
		delete league_array [team_index].team_info;
	}

	return true;
}

