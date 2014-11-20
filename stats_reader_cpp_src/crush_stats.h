//---------------------------------------------------------------------------
// stats writer
//---------------------------------------------------------------------------

#ifndef CRUSH_STATS
#define CRUSH_STATS

#include "stats_reader.h"

#include "crush_player.h"
#include "crush_team.h"
#include <vcl0.h>

#include <list>
#include <algorithm.h>
#include <fstream>
using namespace std;

typedef list<crush_player_ptr> crush_player_list;
typedef list<crush_team_ptr> crush_team_list;
typedef list<player_color_container_ptr> player_color_list;

class crush_statistics : public reference_count
{
public:
        crush_statistics ()
        {
			m_current_crush_week = -1;
			m_display_crush_week = -1;
			m_current_crush_season = 1;
        }
        ~crush_statistics () 
		{
		}

		int m_current_crush_week;
		int m_display_crush_week;
		int m_current_crush_season;

        bool read_team_information (const char * league_information);

        bool read_crush_statistics (const char * crush_statistics_file);
        bool write_crush_statistics (const char * crush_statistics_file);

        void clear_crush_statistics ();
        bool update_crush_statistics (const team_container * league_array, int size);

        const char * get_name_for_race (race_type race);
        TColor get_color_for_crush_color (crush_color color);
		player_color_container_ptr add_color ( TColor trim, TColor jersey);

        crush_player_list m_crush_player_list;
        crush_team_list m_crush_team_list;
		player_color_list m_player_color_list;

private:

        ofstream m_out_file_stream;
        ifstream m_in_file_stream;
};

typedef smart_pointer<crush_statistics> crush_statistics_ptr;

#endif
