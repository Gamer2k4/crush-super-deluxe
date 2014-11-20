//---------------------------------------------------------------------------

#include <vcl.h>
#pragma hdrstop

#include "crush_stats.h"
#include "stats_display.h"
#include "player_profile_display.h"
#include "options_display.h"

int g_last_tab_index = rushing;

enum graph_type 
{
	career = 0,
	season
};

graph_type g_graph_type;

//---------------------------------------------------------------------------
#pragma package(smart_init)
#pragma resource "*.dfm"
Tfrm_stats_manager *frm_stats_manager;

crush_statistics_ptr g_crush_statistics;

#define CUSTOM_COLOR_INDEX 9
#define DEAD_IMAGE_INDEX 9

//---------------------------------------------------------------------------
__fastcall Tfrm_stats_manager::Tfrm_stats_manager(TComponent* Owner)
        : TForm(Owner)
{
}
//---------------------------------------------------------------------------


void Tfrm_stats_manager::update_colors ()
{
	player_color_list::iterator it = g_crush_statistics->m_player_color_list.begin ();

        for (int image_list_index = CUSTOM_COLOR_INDEX + 1;image_list_index < ImageList1->Count; image_list_index++)
        {
                ImageList1->Delete (image_list_index);
        }

   	while (it != g_crush_statistics->m_player_color_list.end ())
	{
		player_color_container_ptr current_color = *it;

                current_color->m_image_list_index = ImageList1->Add (current_color->m_color_bitmap, NULL);

                it++;
	}
}

int Tfrm_stats_manager::adjust_team_colors (crush_player_ptr crush_player)
{
	if (crush_player->m_player_color_container && crush_player->m_player_color_container->m_color_bitmap)
	{
		return crush_player->m_player_color_container->m_image_list_index;
	}
	return CUSTOM_COLOR_INDEX;
}

void __fastcall Tfrm_stats_manager::table_tabChange(TObject *Sender)
{
		g_last_tab_index = table_tab->TabIndex;
        switch (table_tab->TabIndex)
        {
			case rushing:
				if (player_season_stats_button->Down)
				{
        			m_player_current_season_stats_handler.display_rushing (frm_stats_manager);
					break;
				}
				if (player_career_stats_button->Down)
				{
        			m_player_career_stats_handler.display_rushing (frm_stats_manager);
					break;
				}
				if (player_best_stats_button->Down)
				{
                			m_player_best_season_stats_handler.display_rushing (frm_stats_manager);
					break;
				}
				if (team_season_stats_button->Down)
				{
        			m_team_current_season_stats_handler.display_rushing (frm_stats_manager);
					break;
				}
				if (team_career_stats_button->Down)
				{
        			m_team_career_stats_handler.display_rushing (frm_stats_manager);        
					break;
				}
				if (team_best_stats_button->Down)
				{
        			m_team_best_season_stats_handler.display_rushing (frm_stats_manager);        
					break;
				}
				break;
			case checks:
				if (player_season_stats_button->Down)
				{
        			m_player_current_season_stats_handler.display_checks (frm_stats_manager);        
					break;
				}
				if (player_career_stats_button->Down)
				{
        			m_player_career_stats_handler.display_checks (frm_stats_manager);
					break;
				}
				if (player_best_stats_button->Down)
				{
        			m_player_best_season_stats_handler.display_checks (frm_stats_manager);        
					break;
				}
				if (team_season_stats_button->Down)
				{
        			m_team_current_season_stats_handler.display_checks (frm_stats_manager);        
					break;
				}
				if (team_career_stats_button->Down)
				{
        			m_team_career_stats_handler.display_checks (frm_stats_manager);        
					break;
				}
				if (team_best_stats_button->Down)
				{
        			m_team_best_season_stats_handler.display_checks (frm_stats_manager);        
					break;
				}

				break;
			case carnage:
				if (player_season_stats_button->Down)
				{
        			m_player_current_season_stats_handler.display_carnage (frm_stats_manager);
					break;
				}
				if (player_career_stats_button->Down)
				{
        			m_player_career_stats_handler.display_carnage (frm_stats_manager);
					break;
				}
				if (player_best_stats_button->Down)
				{
        			m_player_best_season_stats_handler.display_carnage (frm_stats_manager);        
					break;
				}
				if (team_season_stats_button->Down)
				{
        			m_team_current_season_stats_handler.display_carnage (frm_stats_manager);        
					break;
				}
				if (team_career_stats_button->Down)
				{
        			m_team_career_stats_handler.display_carnage (frm_stats_manager);        
					break;
				}
				if (team_best_stats_button->Down)
				{
        			m_team_best_season_stats_handler.display_carnage (frm_stats_manager);        
					break;
				}

				break;
			case all:
				if (player_season_stats_button->Down)
				{
        			m_player_current_season_stats_handler.display_all (frm_stats_manager);
					break;
				}
				if (player_career_stats_button->Down)
				{
        			m_player_career_stats_handler.display_all (frm_stats_manager);        
					break;
				}
				if (player_best_stats_button->Down)
				{
        			m_player_best_season_stats_handler.display_all (frm_stats_manager);
					break;
				}
				if (team_season_stats_button->Down)
				{
        			m_team_current_season_stats_handler.display_all (frm_stats_manager);        
					break;
				}
				if (team_career_stats_button->Down)
				{
        			m_team_career_stats_handler.display_all (frm_stats_manager);        
					break;
				}
				if (team_best_stats_button->Down)
				{
        			m_team_best_season_stats_handler.display_all (frm_stats_manager);        
					break;
				}

				break;
        }
}

void __fastcall Tfrm_stats_manager::graph_tabChange(TObject *Sender)
{
	if (!g_crush_statistics)
	{
		return;
	}

	graph_tab->Enabled = true;
	graph_tab->BringToFront ();
	table_tab->Enabled = false;

	short current_value = 0;

	crush_team_list::iterator team_it = g_crush_statistics->m_crush_team_list.begin ();

	while (stats_graph->SeriesList->Count > 0)
	{
		TChartSeries * remove_series = stats_graph->Series [0];
		stats_graph->RemoveSeries(stats_graph->Series[0]);
		delete remove_series;
	}

	string chart_title;

	while (team_it != g_crush_statistics->m_crush_team_list.end ())
	{
		TLineSeries * current_series = new Series::TLineSeries (stats_graph);
		crush_team_ptr current_team = *team_it;
		crush_team_historical_week_list::iterator it = current_team->m_crush_team_historical_week_list.begin ();

		while (it != current_team->m_crush_team_historical_week_list.end ())
		{
			crush_team_historical_week_ptr current_team_historical_week = *it;

			switch (g_graph_type)
			{
				case season:
					switch (graph_tab->TabIndex)
					{
						case rushing_tiles:
							current_value = current_team_historical_week->m_season_rushing_tiles;
							chart_title = "rushing_tiles";
							break;
						case kills_for:
							current_value = current_team_historical_week->m_season_kills_for;
							chart_title = "kills_for";
							break;
						case kills_against:
							current_value = current_team_historical_week->m_season_kills_against;
							chart_title = "kills_against";
							break;
						case injuries_for:
							current_value = current_team_historical_week->m_season_injuries_for;
							chart_title = "injuries_for";
							break;
						case injuries_against:
							current_value = current_team_historical_week->m_season_injuries_against;
							chart_title = "injuries_against";
							break;
						case checks_thrown:
							current_value = current_team_historical_week->m_season_checks_thrown;
							chart_title = "checks_thrown";
							break;
						case checks_landed:
							current_value = current_team_historical_week->m_season_checks_landed;
							chart_title = "checks_landed";
							break;
						case activated_pads:
							current_value = current_team_historical_week->m_season_activated_pads;
							chart_title = "activated_pads";
							break;
						case balls_fumbled:
							current_value = current_team_historical_week->m_season_balls_fumbled;
							chart_title = "balls_fumbled";
							break;
						case rushing_attempts:
							current_value = current_team_historical_week->m_season_rushing_attempts;
							chart_title = "rushing_attempts";
							break;
						case ball_control:
							current_value = current_team_historical_week->m_season_ball_control;
							chart_title = "ball_control";
							break;
						case sacks_against:
							current_value = current_team_historical_week->m_season_sacks_against;
							chart_title = "sacks_against";
							break;
						case sacks_for:
							current_value = current_team_historical_week->m_season_sacks_for;
							chart_title = "sacks_for";
							break;
						case wins:
							current_value = current_team_historical_week->m_season_wins;
							chart_title = "wins";
							break;
						case losses:
							current_value = current_team_historical_week->m_season_losses;
							chart_title = "losses";
							break;
						case ties:
							current_value = current_team_historical_week->m_season_ties;
							chart_title = "ties";
							break;
						case team_value:
							current_value = current_team_historical_week->m_current_team_value;
							chart_title = "value";
							break;
					}
					break;
				case career:
					switch (graph_tab->TabIndex)
					{
						case rushing_tiles:
							current_value = current_team_historical_week->m_career_rushing_tiles;
							chart_title = "rushing_tiles";
							break;
						case kills_for:
							current_value = current_team_historical_week->m_career_kills_for;
							chart_title = "kills_for";
							break;
						case kills_against:
							current_value = current_team_historical_week->m_career_kills_against;
							chart_title = "kills_against";
							break;
						case injuries_for:
							current_value = current_team_historical_week->m_career_injuries_for;
							chart_title = "injuries_for";
							break;
						case injuries_against:
							current_value = current_team_historical_week->m_career_injuries_against;
							chart_title = "injuries_against";
							break;
						case checks_thrown:
							current_value = current_team_historical_week->m_career_checks_thrown;
							chart_title = "checks_thrown";
							break;
						case checks_landed:
							current_value = current_team_historical_week->m_career_checks_landed;
							chart_title = "checks_landed";
							break;
						case activated_pads:
							current_value = current_team_historical_week->m_career_activated_pads;
							chart_title = "activated_pads";
							break;
						case balls_fumbled:
							current_value = current_team_historical_week->m_career_balls_fumbled;
							chart_title = "balls_fumbled";
							break;
						case rushing_attempts:
							current_value = current_team_historical_week->m_career_rushing_attempts;
							chart_title = "rushing_attempts";
							break;
						case ball_control:
							current_value = current_team_historical_week->m_career_ball_control;
							chart_title = "ball_control";
							break;
						case sacks_against:
							current_value = current_team_historical_week->m_career_sacks_against;
							chart_title = "sacks_against";
							break;
						case sacks_for:
							current_value = current_team_historical_week->m_career_sacks_for;
							chart_title = "sacks_for";
							break;
						case wins:
							current_value = current_team_historical_week->m_career_wins;
							chart_title = "wins";
							break;
						case losses:
							current_value = current_team_historical_week->m_career_losses;
							chart_title = "losses";
							break;
						case ties:
							current_value = current_team_historical_week->m_career_ties;
							chart_title = "ties";
							break;
						case team_value:
							current_value = current_team_historical_week->m_current_team_value;
							chart_title = "value";
							break;
					}
					break;
			}

			int index = current_team_historical_week->m_week_index ;

			current_series->AddXY ( index , current_value, IntToStr (index), clTeeColor);
			it++;
		}

		team_it++;
		current_series->Title = current_team->m_name.c_str ();
        current_series->LinePen->Width = 2;
        current_series->LinePen->Color = clWhite;

        if (stats_graph->View3D)
        {
        	current_series->LinePen->Width = 0;
        }
                
		current_series->SeriesColor = g_crush_statistics->get_color_for_crush_color (current_team->m_jersey_color);
		current_series->ParentChart = stats_graph;
	}

	stats_graph->Title->Text->Clear ();
	stats_graph->Title->Text->Add (chart_title.c_str());
}

//---------------------------------------------------------------------------

void team_season_graph_handler::display (Tfrm_stats_manager * main_form)
{
        main_form->graph_tab->Enabled = true;
		g_graph_type = season;
        main_form->graph_tab->BringToFront ();
        main_form->table_tab->Enabled = false;
		main_form->graph_tabChange(main_form);
}

void team_career_graph_handler::display (Tfrm_stats_manager * main_form)
{
        main_form->graph_tab->Enabled = true;
		g_graph_type = career;
        main_form->graph_tab->BringToFront ();
        main_form->table_tab->Enabled = false;
		main_form->graph_tabChange(main_form);
}

void player_best_season_stats_handler::display_all (Tfrm_stats_manager * main_form)
{
	if (!g_crush_statistics)
	{
			return;
	}

	main_form->ListView1->Visible = false;

	main_form->table_tab->Enabled = true;
	main_form->table_tab->BringToFront ();
	main_form->graph_tab->Enabled = false;
	main_form->table_image->Visible = true;

	main_form->ListView1->Columns->Clear ();
	main_form->ListView1->Items->Clear ();

   	crush_player_list::iterator it = g_crush_statistics->m_crush_player_list.begin ();

	float rushing_average;
	float checking_average ;

   	while (it != g_crush_statistics->m_crush_player_list.end ())
   	{
   		crush_player_ptr current_player = *it;

		TListItem  *ListItem;

		ListItem = main_form->ListView1->Items->Add();
		ListItem->Caption = g_crush_statistics->get_name_for_race (current_player->m_race);
		ListItem->ImageIndex = current_player->m_race;

		ListItem->SubItems->Add(current_player->m_name.c_str ());

		if (current_player->m_dead)
		{
			ListItem->SubItemImages[0] = DEAD_IMAGE_INDEX;
		}
		ListItem->SubItems->Add(current_player->m_team_name.c_str ());

       	ListItem->SubItemImages [1] = main_form->adjust_team_colors (current_player);
                
		ListItem->SubItems->Add (IntToStr (current_player->m_total_value));

		ListItem->SubItems->Add (IntToStr (current_player->m_points_to_spend));
		ListItem->SubItems->Add (IntToStr (current_player->m_best_rushing_attempts));
		ListItem->SubItems->Add (IntToStr (current_player->m_best_rushing_tiles));

		if (current_player->m_best_rushing_tiles != 0)
		{
				rushing_average = (float)current_player->m_best_rushing_tiles / current_player->m_best_rushing_attempts;
		}
		else
		{
				rushing_average = 0;
		}

		ListItem->SubItems->Add (FloatToStrF (rushing_average, ffGeneral, 5, 10));

		ListItem->SubItems->Add (IntToStr (current_player->m_best_goals_scored));
		ListItem->SubItems->Add (IntToStr (current_player->m_best_kills_for));
		ListItem->SubItems->Add (IntToStr (current_player->m_best_injuries_for));
		ListItem->SubItems->Add (IntToStr (current_player->m_best_injuries_for + current_player->m_best_kills_for));
		ListItem->SubItems->Add (IntToStr (current_player->m_best_checks_thrown));
		ListItem->SubItems->Add (IntToStr (current_player->m_best_checks_landed));

		if (current_player->m_best_checks_thrown != 0)
		{
				ListItem->SubItems->Add (FloatToStrF ((float)current_player->m_best_checks_landed / current_player->m_best_checks_thrown, ffGeneral, 5, 10));
		}
		else
		{
				ListItem->SubItems->Add (IntToStr (0));
		}

		ListItem->SubItems->Add (IntToStr (current_player->m_best_sacks_for));
		it++;
	}

	TListColumn * NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "race";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "team name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "value";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "points to spend";
	NewColumn->Tag = value;
    NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "rushing attempts";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "rushing tiles";
	NewColumn->Tag = value;
    NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "rushing average";
	NewColumn->Tag = float_value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "goals scored";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "kills for";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "injuries for";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "total for";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "checks thrown";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "checks landed";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "checking average";
	NewColumn->Tag = float_value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "sacks for";
	NewColumn->Tag = value;

	main_form->ListView1->Visible = true;
}

void player_career_stats_handler::display_all (Tfrm_stats_manager * main_form)
{
	if (!g_crush_statistics)
	{
			return;
	}

	main_form->table_tab->Enabled = true;
	main_form->table_tab->BringToFront ();
	main_form->graph_tab->Enabled = false;

	main_form->ListView1->Visible = false;

	main_form->ListView1->Columns->Clear ();
	main_form->ListView1->Items->Clear ();

   	crush_player_list::iterator it = g_crush_statistics->m_crush_player_list.begin ();

	float rushing_average;
	float checking_average ;

   	while (it != g_crush_statistics->m_crush_player_list.end ())
   	{
   		crush_player_ptr current_player = *it;

		TListItem  *ListItem;

		ListItem = main_form->ListView1->Items->Add();
		ListItem->Caption = g_crush_statistics->get_name_for_race (current_player->m_race);
		ListItem->ImageIndex = current_player->m_race;

		ListItem->SubItems->Add(current_player->m_name.c_str ());

		if (current_player->m_dead)
		{
			ListItem->SubItemImages[0] = DEAD_IMAGE_INDEX;
		}

		ListItem->SubItems->Add(current_player->m_team_name.c_str ());

        ListItem->SubItemImages [1] = main_form->adjust_team_colors (current_player);

		ListItem->SubItems->Add (IntToStr (current_player->m_total_value));
		ListItem->SubItems->Add (IntToStr (current_player->m_points_to_spend));
		ListItem->SubItems->Add (IntToStr (current_player->m_total_rushing_attempts));
		ListItem->SubItems->Add (IntToStr (current_player->m_total_rushing_tiles));

		if (current_player->m_total_rushing_tiles != 0)
		{
				rushing_average = (float)current_player->m_total_rushing_tiles / current_player->m_total_rushing_attempts;
		}
		else
		{
				rushing_average = 0;
		}

		ListItem->SubItems->Add (FloatToStrF (rushing_average, ffGeneral, 5, 10));

		ListItem->SubItems->Add (IntToStr (current_player->m_total_goals_scored));
		ListItem->SubItems->Add (IntToStr (current_player->m_total_kills_for));
		ListItem->SubItems->Add (IntToStr (current_player->m_total_injuries_for));
		ListItem->SubItems->Add (IntToStr (current_player->m_total_checks_thrown));
		ListItem->SubItems->Add (IntToStr (current_player->m_total_checks_landed));

		if (current_player->m_total_checks_thrown != 0)
		{
				ListItem->SubItems->Add (FloatToStrF ((float)current_player->m_total_checks_landed / current_player->m_total_checks_thrown, ffGeneral, 5, 10));
		}
		else
		{
				ListItem->SubItems->Add (IntToStr (0));
		}

		ListItem->SubItems->Add (IntToStr (current_player->m_total_sacks_for));
		it++;
	}

	TListColumn * NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "race";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "team name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "value";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "points to spend";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "rushing attempts";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "rushing tiles";
	NewColumn->Tag = value;
   	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "rushing average";
	NewColumn->Tag = float_value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "goals scored";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "kills for";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "injuries for";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "checks thrown";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "checks landed";
	NewColumn->Tag = value;
    NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "checking average";
	NewColumn->Tag = float_value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "sacks for";
	NewColumn->Tag = value;

	main_form->ListView1->Visible = true;
}

void player_current_season_stats_handler::display_all (Tfrm_stats_manager * main_form)
{
	if (!g_crush_statistics)
	{
			return;
	}

    main_form->table_tab->BringToFront ();

	main_form->ListView1->Visible = false;

	main_form->ListView1->Columns->Clear ();
	main_form->ListView1->Items->Clear ();

   	crush_player_list::iterator it = g_crush_statistics->m_crush_player_list.begin ();

	float rushing_average;
	float checking_average ;

   	while (it != g_crush_statistics->m_crush_player_list.end ())
   	{
   		crush_player_ptr current_player = *it;

		TListItem  *ListItem;

		ListItem = main_form->ListView1->Items->Add();
		ListItem->Caption = g_crush_statistics->get_name_for_race (current_player->m_race);
		ListItem->ImageIndex = current_player->m_race;

		ListItem->SubItems->Add(current_player->m_name.c_str ());

		if (current_player->m_dead)
		{
			ListItem->SubItemImages[0] = DEAD_IMAGE_INDEX;
		}
		ListItem->SubItems->Add(current_player->m_team_name.c_str ());

        ListItem->SubItemImages [1] = main_form->adjust_team_colors (current_player);

		ListItem->SubItems->Add (IntToStr (current_player->m_total_value));
		ListItem->SubItems->Add (IntToStr (current_player->m_points_to_spend));
		ListItem->SubItems->Add (IntToStr (current_player->m_rushing_attempts));
		ListItem->SubItems->Add (IntToStr (current_player->m_rushing_tiles));

		if (current_player->m_rushing_tiles != 0)
		{
				rushing_average = (float)current_player->m_rushing_tiles / current_player->m_rushing_attempts;
		}
		else
		{
				rushing_average = 0;
		}

		ListItem->SubItems->Add (FloatToStrF (rushing_average, ffGeneral, 5, 10));

		ListItem->SubItems->Add (IntToStr (current_player->m_goals_scored));
		ListItem->SubItems->Add (IntToStr (current_player->m_kills_for));
		ListItem->SubItems->Add (IntToStr (current_player->m_injuries_for));
		ListItem->SubItems->Add (IntToStr (current_player->m_checks_thrown));
		ListItem->SubItems->Add (IntToStr (current_player->m_checks_landed));

		if (current_player->m_checks_thrown != 0)
		{
				ListItem->SubItems->Add (FloatToStrF ((float)current_player->m_checks_landed / current_player->m_checks_thrown, ffGeneral, 5, 10));
		}
		else
		{
				ListItem->SubItems->Add (IntToStr (0));
		}

		ListItem->SubItems->Add (IntToStr (current_player->m_sacks_for));
		it++;
	}

	TListColumn * NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "race";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "team name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "value";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "points to spend";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "rushing attempts";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "rushing tiles";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "rushing average";
	NewColumn->Tag = float_value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "goals scored";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "kills for";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "injuries for";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "checks thrown";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "checks landed";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "checking average";
	NewColumn->Tag = float_value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "sacks for";
	NewColumn->Tag = value;

	main_form->ListView1->Visible = true;
}
void team_best_season_stats_handler::display_all (Tfrm_stats_manager * main_form)
{
	if (!g_crush_statistics)
	{
			return;
	}

	main_form->ListView1->Visible = false;

	main_form->table_tab->Enabled = true;
	main_form->table_tab->BringToFront ();
	main_form->graph_tab->Enabled = false;
	main_form->table_image->Visible = true;

	main_form->ListView1->Columns->Clear ();
	main_form->ListView1->Items->Clear ();

   	crush_team_list::iterator it = g_crush_statistics->m_crush_team_list.begin ();

	float rushing_average;
	float checking_average ;

   	while (it != g_crush_statistics->m_crush_team_list.end ())
   	{
   		crush_team_ptr current_team = *it;

		TListItem  *ListItem;

		ListItem = main_form->ListView1->Items->Add();
		ListItem->Caption = current_team->m_name.c_str ();
		ListItem->ImageIndex = 8;

		ListItem->SubItems->Add(current_team->m_coach.c_str ());

		ListItem->SubItems->Add(IntToStr (current_team->m_best_wins));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_losses));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_ties));

		ListItem->SubItems->Add(IntToStr (current_team->m_current_team_value));

		ListItem->SubItems->Add(IntToStr (current_team->m_best_rushing_attempts));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_rushing_tiles));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_activated_pads));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_ball_control));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_balls_fumbled));

		ListItem->SubItems->Add(IntToStr (current_team->m_best_kills_for));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_kills_against));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_injuries_for));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_injuries_against));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_injuries_for + current_team->m_best_kills_for));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_injuries_against + current_team->m_best_kills_against));

		ListItem->SubItems->Add(IntToStr (current_team->m_best_checks_thrown));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_checks_landed));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_sacks_against));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_sacks_for));

		it++;
	}

	TListColumn * NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "team name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "coach";
	NewColumn->Tag = text;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "wins";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "losses";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "ties";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "value";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "rushing attempts";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "rushing tiles";
	NewColumn->Tag = value;
	
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "activated pads";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "ball control";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "balls fumbled";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "kills for";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "kills against";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "injuries for";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "injuries against";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "total for";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "total against";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "checks thrown";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "checks landed";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "sacks against";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "sacks for";
	NewColumn->Tag = value;

	main_form->ListView1->Visible = true;
}

void team_career_stats_handler::display_all (Tfrm_stats_manager * main_form)
{
	if (!g_crush_statistics)
	{
			return;
	}

	main_form->ListView1->Visible = false;

	main_form->table_tab->Enabled = true;
	main_form->table_tab->BringToFront ();
	main_form->graph_tab->Enabled = false;
	main_form->table_image->Visible = true;

	main_form->ListView1->Columns->Clear ();
	main_form->ListView1->Items->Clear ();

   	crush_team_list::iterator it = g_crush_statistics->m_crush_team_list.begin ();

	float rushing_average;
	float checking_average ;

   	while (it != g_crush_statistics->m_crush_team_list.end ())
   	{
   		crush_team_ptr current_team = *it;

		TListItem  *ListItem;

		ListItem = main_form->ListView1->Items->Add();
		ListItem->Caption = current_team->m_name.c_str ();
		ListItem->ImageIndex = 8;

		ListItem->SubItems->Add(current_team->m_coach.c_str ());

		ListItem->SubItems->Add(IntToStr (current_team->m_total_wins));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_losses));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_ties));

		ListItem->SubItems->Add(IntToStr (current_team->m_current_team_value));

		ListItem->SubItems->Add(IntToStr (current_team->m_total_rushing_attempts));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_rushing_tiles));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_activated_pads));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_ball_control));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_balls_fumbled));

		ListItem->SubItems->Add(IntToStr (current_team->m_total_kills_for));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_kills_against));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_injuries_for));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_injuries_against));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_injuries_for + current_team->m_total_kills_for));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_injuries_against + current_team->m_total_kills_against));

		ListItem->SubItems->Add(IntToStr (current_team->m_total_checks_thrown));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_checks_landed));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_sacks_against));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_sacks_for));

		it++;
	}

	TListColumn * NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "team name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "coach";
	NewColumn->Tag = text;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "wins";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "losses";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "ties";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "value";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "rushing attempts";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "rushing tiles";
	NewColumn->Tag = value;
	
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "activated pads";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "ball control";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "balls fumbled";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "kills for";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "kills against";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "injuries for";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "injuries against";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "total for";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "total against";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "checks thrown";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "checks landed";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "sacks against";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "sacks for";
	NewColumn->Tag = value;

	main_form->ListView1->Visible = true;
}

void team_current_season_stats_handler::display_all (Tfrm_stats_manager * main_form)
{
	if (!g_crush_statistics)
	{
			return;
	}

	main_form->ListView1->Visible = false;

	main_form->table_tab->Enabled = true;
	main_form->table_tab->BringToFront ();
	main_form->graph_tab->Enabled = false;
	main_form->table_image->Visible = true;

	main_form->ListView1->Columns->Clear ();
	main_form->ListView1->Items->Clear ();

   	crush_team_list::iterator it = g_crush_statistics->m_crush_team_list.begin ();

	float rushing_average;
	float checking_average ;

   	while (it != g_crush_statistics->m_crush_team_list.end ())
   	{
   		crush_team_ptr current_team = *it;

		TListItem  *ListItem;

		ListItem = main_form->ListView1->Items->Add();
		ListItem->Caption = current_team->m_name.c_str ();
		ListItem->ImageIndex = 8;

		ListItem->SubItems->Add(current_team->m_coach.c_str ());

		ListItem->SubItems->Add(IntToStr (current_team->m_wins));
		ListItem->SubItems->Add(IntToStr (current_team->m_losses));
		ListItem->SubItems->Add(IntToStr (current_team->m_ties));

		ListItem->SubItems->Add(IntToStr (current_team->m_current_team_value));

		ListItem->SubItems->Add(IntToStr (current_team->m_rushing_attempts));
		ListItem->SubItems->Add(IntToStr (current_team->m_rushing_tiles));
		ListItem->SubItems->Add(IntToStr (current_team->m_activated_pads));
		ListItem->SubItems->Add(IntToStr (current_team->m_ball_control));
		ListItem->SubItems->Add(IntToStr (current_team->m_balls_fumbled));

		ListItem->SubItems->Add(IntToStr (current_team->m_kills_for));
		ListItem->SubItems->Add(IntToStr (current_team->m_kills_against));
		ListItem->SubItems->Add(IntToStr (current_team->m_injuries_for));
		ListItem->SubItems->Add(IntToStr (current_team->m_injuries_against));
		ListItem->SubItems->Add(IntToStr (current_team->m_injuries_for + current_team->m_kills_for));
		ListItem->SubItems->Add(IntToStr (current_team->m_injuries_against + current_team->m_kills_against));

		ListItem->SubItems->Add(IntToStr (current_team->m_checks_thrown));
		ListItem->SubItems->Add(IntToStr (current_team->m_checks_landed));
		ListItem->SubItems->Add(IntToStr (current_team->m_sacks_against));
		ListItem->SubItems->Add(IntToStr (current_team->m_sacks_for));

		it++;
	}

	TListColumn * NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "team name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "coach";
	NewColumn->Tag = text;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "wins";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "losses";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "ties";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "value";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "rushing attempts";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "rushing tiles";
	NewColumn->Tag = value;
	
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "activated pads";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "ball control";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "balls fumbled";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "kills for";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "kills against";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "injuries for";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "injuries against";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "total for";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "total against";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "checks thrown";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "checks landed";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "sacks against";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "sacks for";
	NewColumn->Tag = value;

	main_form->ListView1->Visible = true;
}


void team_best_season_stats_handler::display_rushing (Tfrm_stats_manager * main_form)
{
	if (!g_crush_statistics)
	{
			return;
	}

	main_form->ListView1->Visible = false;

	main_form->table_tab->Enabled = true;
	main_form->table_tab->BringToFront ();
	main_form->graph_tab->Enabled = false;
	main_form->table_image->Visible = true;

	main_form->ListView1->Columns->Clear ();
	main_form->ListView1->Items->Clear ();

   	crush_team_list::iterator it = g_crush_statistics->m_crush_team_list.begin ();

	float rushing_average;
	float checking_average ;

   	while (it != g_crush_statistics->m_crush_team_list.end ())
   	{
   		crush_team_ptr current_team = *it;

		TListItem  *ListItem;

		ListItem = main_form->ListView1->Items->Add();
		ListItem->Caption = current_team->m_name.c_str ();
		ListItem->ImageIndex = 8;

		ListItem->SubItems->Add(current_team->m_coach.c_str ());

		if (frm_stats_options->m_show_wins_losses_ties)
		{
		ListItem->SubItems->Add(IntToStr (current_team->m_best_wins));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_losses));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_ties));
		}

		ListItem->SubItems->Add(IntToStr (current_team->m_best_rushing_attempts));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_rushing_tiles));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_activated_pads));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_ball_control));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_balls_fumbled));

		it++;
	}

	TListColumn * NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "team name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "coach";
	NewColumn->Tag = text;

	if (frm_stats_options->m_show_wins_losses_ties)
	{
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "wins";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "losses";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "ties";
	NewColumn->Tag = value;
	}

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "attempts";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "tiles";
	NewColumn->Tag = value;
	
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "activated pads";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "ball control";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "balls fumbled";
	NewColumn->Tag = value;

	main_form->ListView1->Visible = true;
}

void team_career_stats_handler::display_rushing (Tfrm_stats_manager * main_form)
{
	if (!g_crush_statistics)
	{
			return;
	}

	main_form->ListView1->Visible = false;

	main_form->table_tab->Enabled = true;
	main_form->table_tab->BringToFront ();
	main_form->graph_tab->Enabled = false;
	main_form->table_image->Visible = true;

	main_form->ListView1->Columns->Clear ();
	main_form->ListView1->Items->Clear ();

   	crush_team_list::iterator it = g_crush_statistics->m_crush_team_list.begin ();

	float rushing_average;
	float checking_average ;

   	while (it != g_crush_statistics->m_crush_team_list.end ())
   	{
   		crush_team_ptr current_team = *it;

		TListItem  *ListItem;

		ListItem = main_form->ListView1->Items->Add();
		ListItem->Caption = current_team->m_name.c_str ();
		ListItem->ImageIndex = 8;

		ListItem->SubItems->Add(current_team->m_coach.c_str ());

		if (frm_stats_options->m_show_wins_losses_ties)
		{
		ListItem->SubItems->Add(IntToStr (current_team->m_total_wins));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_losses));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_ties));
		}

		ListItem->SubItems->Add(IntToStr (current_team->m_total_rushing_attempts));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_rushing_tiles));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_activated_pads));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_ball_control));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_balls_fumbled));

		it++;
	}

	TListColumn * NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "team name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "coach";
	NewColumn->Tag = text;

	if (frm_stats_options->m_show_wins_losses_ties)
	{
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "wins";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "losses";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "ties";
	NewColumn->Tag = value;
	}

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "attempts";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "tiles";
	NewColumn->Tag = value;
	
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "activated pads";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "ball control";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "balls fumbled";
	NewColumn->Tag = value;

	main_form->ListView1->Visible = true;
}

void team_current_season_stats_handler::display_rushing (Tfrm_stats_manager * main_form)
{
	if (!g_crush_statistics)
	{
			return;
	}

	main_form->ListView1->Visible = false;

	main_form->table_tab->Enabled = true;
	main_form->table_tab->BringToFront ();
	main_form->graph_tab->Enabled = false;
	main_form->table_image->Visible = true;

	main_form->ListView1->Columns->Clear ();
	main_form->ListView1->Items->Clear ();

   	crush_team_list::iterator it = g_crush_statistics->m_crush_team_list.begin ();

	float rushing_average;
	float checking_average ;

   	while (it != g_crush_statistics->m_crush_team_list.end ())
   	{
   		crush_team_ptr current_team = *it;

		TListItem  *ListItem;

		ListItem = main_form->ListView1->Items->Add();
		ListItem->Caption = current_team->m_name.c_str ();
		ListItem->ImageIndex = 8;

		ListItem->SubItems->Add(current_team->m_coach.c_str ());

		if (frm_stats_options->m_show_wins_losses_ties)
		{
		ListItem->SubItems->Add(IntToStr (current_team->m_wins));
		ListItem->SubItems->Add(IntToStr (current_team->m_losses));
		ListItem->SubItems->Add(IntToStr (current_team->m_ties));
		}

		ListItem->SubItems->Add(IntToStr (current_team->m_rushing_attempts));
		ListItem->SubItems->Add(IntToStr (current_team->m_rushing_tiles));
		ListItem->SubItems->Add(IntToStr (current_team->m_activated_pads));
		ListItem->SubItems->Add(IntToStr (current_team->m_ball_control));
		ListItem->SubItems->Add(IntToStr (current_team->m_balls_fumbled));

		it++;
	}

	TListColumn * NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "team name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "coach";
	NewColumn->Tag = text;

	if (frm_stats_options->m_show_wins_losses_ties)
	{
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "wins";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "losses";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "ties";
	NewColumn->Tag = value;
	}

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "attempts";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "tiles";
	NewColumn->Tag = value;
	
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "activated pads";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "ball control";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "balls fumbled";
	NewColumn->Tag = value;

	main_form->ListView1->Visible = true;
}

void player_best_season_stats_handler::display_rushing (Tfrm_stats_manager * main_form)
{
	if (!g_crush_statistics)
	{
			return;
	}

	main_form->ListView1->Visible = false;

	main_form->table_tab->Enabled = true;
	main_form->table_tab->BringToFront ();
	main_form->graph_tab->Enabled = false;
	main_form->table_image->Visible = true;

	main_form->ListView1->Columns->Clear ();
	main_form->ListView1->Items->Clear ();

   	crush_player_list::iterator it = g_crush_statistics->m_crush_player_list.begin ();

	float rushing_average;
	float checking_average ;

   	while (it != g_crush_statistics->m_crush_player_list.end ())
   	{
   		crush_player_ptr current_player = *it;

		TListItem  *ListItem;

		ListItem = main_form->ListView1->Items->Add();
		ListItem->Caption = g_crush_statistics->get_name_for_race (current_player->m_race);

		ListItem->ImageIndex = current_player->m_race;

		ListItem->SubItems->Add(current_player->m_name.c_str ());

		if (current_player->m_dead)
		{
			ListItem->SubItemImages[0] = DEAD_IMAGE_INDEX;
		}
		ListItem->SubItems->Add(current_player->m_team_name.c_str ());

        ListItem->SubItemImages [1] = main_form->adjust_team_colors (current_player);

		if (frm_stats_options->m_show_player_value)
		{	
			ListItem->SubItems->Add (IntToStr (current_player->m_total_value));
		}
		//ListItem->SubItems->Add (IntToStr (current_player->m_points_to_spend));

		ListItem->SubItems->Add (IntToStr (current_player->m_best_rushing_attempts));
		ListItem->SubItems->Add (IntToStr (current_player->m_best_rushing_tiles));

		if (current_player->m_best_rushing_attempts != 0)
		{
				rushing_average = (float)current_player->m_best_rushing_tiles / current_player->m_best_rushing_attempts;
				//rushing_average = 0.5;
		}
		else
		{
				rushing_average = 0;
		}

		ListItem->SubItems->Add (FloatToStrF (rushing_average, ffGeneral, 5, 10));

		ListItem->SubItems->Add (IntToStr (current_player->m_best_goals_scored));

		it++;
	}

	TListColumn * NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "race";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "team name";
	NewColumn->Tag = text;
	if (frm_stats_options->m_show_player_value)
	{	
		NewColumn = main_form->ListView1->Columns->Add();
		NewColumn->Width = -2;
		NewColumn->Caption = "value";
		NewColumn->Tag = value;
	}
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "attempts";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "tiles";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "average";
	NewColumn->Tag = float_value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "goals scored";
	NewColumn->Tag = value;

	main_form->ListView1->Visible = true;
}

void player_career_stats_handler::display_rushing (Tfrm_stats_manager * main_form)
{
	if (!g_crush_statistics)
	{
			return;
	}

	main_form->table_tab->Enabled = true;
	main_form->table_tab->BringToFront ();
	main_form->graph_tab->Enabled = false;

	main_form->ListView1->Visible = false;

	main_form->ListView1->Columns->Clear ();
	main_form->ListView1->Items->Clear ();

   	crush_player_list::iterator it = g_crush_statistics->m_crush_player_list.begin ();

	float rushing_average;
	float checking_average ;

   	while (it != g_crush_statistics->m_crush_player_list.end ())
   	{
   		crush_player_ptr current_player = *it;

		TListItem  *ListItem;

		ListItem = main_form->ListView1->Items->Add();
		ListItem->Caption = g_crush_statistics->get_name_for_race (current_player->m_race);
		ListItem->ImageIndex = current_player->m_race;

		ListItem->SubItems->Add(current_player->m_name.c_str ());

		if (current_player->m_dead)
		{
			ListItem->SubItemImages[0] = DEAD_IMAGE_INDEX;
		}
		ListItem->SubItems->Add(current_player->m_team_name.c_str ());

        ListItem->SubItemImages [1] = main_form->adjust_team_colors (current_player);

		if (frm_stats_options->m_show_player_value)
		{	
			ListItem->SubItems->Add (IntToStr (current_player->m_total_value));
		}
		ListItem->SubItems->Add (IntToStr (current_player->m_total_rushing_attempts));
		ListItem->SubItems->Add (IntToStr (current_player->m_total_rushing_tiles));

		if (current_player->m_total_rushing_tiles != 0)
		{
				rushing_average = (float)current_player->m_total_rushing_tiles / current_player->m_total_rushing_attempts;
				//rushing_average = 0.5;
		}
		else
		{
				rushing_average = 0;
		}

		ListItem->SubItems->Add (FloatToStrF (rushing_average, ffGeneral, 5, 10));

		ListItem->SubItems->Add (IntToStr (current_player->m_total_goals_scored));
		it++;
	}

	TListColumn * NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "race";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "team name";
	NewColumn->Tag = text;
	if (frm_stats_options->m_show_player_value)
	{	
		NewColumn = main_form->ListView1->Columns->Add();
		NewColumn->Width = -2;
		NewColumn->Caption = "value";
		NewColumn->Tag = value;
	}
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "attempts";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "tiles";
	NewColumn->Tag = value;
   	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "average";
	NewColumn->Tag = float_value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "goals scored";
	NewColumn->Tag = value;

	main_form->ListView1->Visible = true;
}


void player_current_season_stats_handler::display_rushing (Tfrm_stats_manager * main_form)
{
	if (!g_crush_statistics)
	{
			return;
	}

    main_form->table_tab->BringToFront ();

	main_form->table_tab->Enabled = true;
	main_form->ListView1->Visible = false;

	main_form->ListView1->Columns->Clear ();
	main_form->ListView1->Items->Clear ();

   	crush_player_list::iterator it = g_crush_statistics->m_crush_player_list.begin ();

	float rushing_average;
	float checking_average ;

   	while (it != g_crush_statistics->m_crush_player_list.end ())
   	{
   		crush_player_ptr current_player = *it;

		TListItem  *ListItem;

		ListItem = main_form->ListView1->Items->Add();
		ListItem->Caption = g_crush_statistics->get_name_for_race (current_player->m_race);
		ListItem->ImageIndex = current_player->m_race;

		ListItem->SubItems->Add(current_player->m_name.c_str ());

		if (current_player->m_dead)
		{
			ListItem->SubItemImages[0] = DEAD_IMAGE_INDEX;
		}
		ListItem->SubItems->Add(current_player->m_team_name.c_str ());

        ListItem->SubItemImages [1] = main_form->adjust_team_colors (current_player);

		if (frm_stats_options->m_show_player_value)
		{	
			ListItem->SubItems->Add (IntToStr (current_player->m_total_value));
		}
		ListItem->SubItems->Add (IntToStr (current_player->m_rushing_attempts));
		ListItem->SubItems->Add (IntToStr (current_player->m_rushing_tiles));

		if (current_player->m_rushing_tiles != 0)
		{
				rushing_average = (float)current_player->m_rushing_tiles / current_player->m_rushing_attempts;
		}
		else
		{
				rushing_average = 0;
		}

		ListItem->SubItems->Add (FloatToStrF (rushing_average, ffGeneral, 5, 10));

		ListItem->SubItems->Add (IntToStr (current_player->m_goals_scored));
		it++;
	}

	TListColumn * NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "race";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "team name";
	NewColumn->Tag = text;
	if (frm_stats_options->m_show_player_value)
	{	
		NewColumn = main_form->ListView1->Columns->Add();
		NewColumn->Width = -2;
		NewColumn->Caption = "value";
		NewColumn->Tag = value;
	}
    NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "attempts";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "tiles";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "average";
	NewColumn->Tag = float_value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "goals scored";
	NewColumn->Tag = value;

	main_form->ListView1->Visible = true;
}


void team_best_season_stats_handler::display_carnage (Tfrm_stats_manager * main_form)
{
	if (!g_crush_statistics)
	{
			return;
	}

	main_form->ListView1->Visible = false;

	main_form->table_tab->Enabled = true;
	main_form->table_tab->BringToFront ();
	main_form->graph_tab->Enabled = false;
	main_form->table_image->Visible = true;

	main_form->ListView1->Columns->Clear ();
	main_form->ListView1->Items->Clear ();

   	crush_team_list::iterator it = g_crush_statistics->m_crush_team_list.begin ();

	float rushing_average;
	float checking_average ;

   	while (it != g_crush_statistics->m_crush_team_list.end ())
   	{
   		crush_team_ptr current_team = *it;

		TListItem  *ListItem;

		ListItem = main_form->ListView1->Items->Add();
		ListItem->Caption = current_team->m_name.c_str ();
		ListItem->ImageIndex = 8;

		ListItem->SubItems->Add(current_team->m_coach.c_str ());

		if (frm_stats_options->m_show_wins_losses_ties)
		{
		ListItem->SubItems->Add(IntToStr (current_team->m_best_wins));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_losses));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_ties));
		}

		ListItem->SubItems->Add(IntToStr (current_team->m_best_kills_for));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_kills_against));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_injuries_for));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_injuries_against));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_injuries_for + current_team->m_best_kills_for));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_injuries_against + current_team->m_best_kills_against));

		it++;
	}

	TListColumn * NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "team name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "coach";
	NewColumn->Tag = text;

	if (frm_stats_options->m_show_wins_losses_ties)
	{
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "wins";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "losses";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "ties";
	NewColumn->Tag = value;
	}

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "kills for";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "kills against";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "injuries for";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "injuries against";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "total for";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "total against";
	NewColumn->Tag = value;

	main_form->ListView1->Visible = true;
}

void team_career_stats_handler::display_carnage (Tfrm_stats_manager * main_form)
{
	if (!g_crush_statistics)
	{
			return;
	}

	main_form->ListView1->Visible = false;

	main_form->table_tab->Enabled = true;
	main_form->table_tab->BringToFront ();
	main_form->graph_tab->Enabled = false;
	main_form->table_image->Visible = true;

	main_form->ListView1->Columns->Clear ();
	main_form->ListView1->Items->Clear ();

   	crush_team_list::iterator it = g_crush_statistics->m_crush_team_list.begin ();

	float rushing_average;
	float checking_average ;

   	while (it != g_crush_statistics->m_crush_team_list.end ())
   	{
   		crush_team_ptr current_team = *it;

		TListItem  *ListItem;

		ListItem = main_form->ListView1->Items->Add();
		ListItem->Caption = current_team->m_name.c_str ();
		ListItem->ImageIndex = 8;

		ListItem->SubItems->Add(current_team->m_coach.c_str ());

		if (frm_stats_options->m_show_wins_losses_ties)
		{
		ListItem->SubItems->Add(IntToStr (current_team->m_total_wins));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_losses));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_ties));
		}

		ListItem->SubItems->Add(IntToStr (current_team->m_total_kills_for));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_kills_against));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_injuries_for));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_injuries_against));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_injuries_for + current_team->m_total_kills_for));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_injuries_against + current_team->m_total_kills_against));

		it++;
	}

	TListColumn * NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "team name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "coach";
	NewColumn->Tag = text;

	if (frm_stats_options->m_show_wins_losses_ties)
	{
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "wins";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "losses";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "ties";
	NewColumn->Tag = value;
	}

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "kills for";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "kills against";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "injuries for";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "injuries against";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "total for";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "total against";
	NewColumn->Tag = value;

	main_form->ListView1->Visible = true;
}


void team_current_season_stats_handler::display_carnage (Tfrm_stats_manager * main_form)
{
	if (!g_crush_statistics)
	{
			return;
	}

	main_form->ListView1->Visible = false;

	main_form->table_tab->Enabled = true;
	main_form->table_tab->BringToFront ();
	main_form->graph_tab->Enabled = false;
	main_form->table_image->Visible = true;

	main_form->ListView1->Columns->Clear ();
	main_form->ListView1->Items->Clear ();

   	crush_team_list::iterator it = g_crush_statistics->m_crush_team_list.begin ();

	float rushing_average;
	float checking_average ;

   	while (it != g_crush_statistics->m_crush_team_list.end ())
   	{
   		crush_team_ptr current_team = *it;

		TListItem  *ListItem;

		ListItem = main_form->ListView1->Items->Add();
		ListItem->Caption = current_team->m_name.c_str ();
		ListItem->ImageIndex = 8;

		ListItem->SubItems->Add(current_team->m_coach.c_str ());

		if (frm_stats_options->m_show_wins_losses_ties)
		{
		ListItem->SubItems->Add(IntToStr (current_team->m_wins));
		ListItem->SubItems->Add(IntToStr (current_team->m_losses));
		ListItem->SubItems->Add(IntToStr (current_team->m_ties));
		}

		ListItem->SubItems->Add(IntToStr (current_team->m_kills_for));
		ListItem->SubItems->Add(IntToStr (current_team->m_kills_against));
		ListItem->SubItems->Add(IntToStr (current_team->m_injuries_for));
		ListItem->SubItems->Add(IntToStr (current_team->m_injuries_against));
		ListItem->SubItems->Add(IntToStr (current_team->m_injuries_for + current_team->m_kills_for));
		ListItem->SubItems->Add(IntToStr (current_team->m_injuries_against + current_team->m_kills_against));

		it++;
	}

	TListColumn * NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "team name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "coach";
	NewColumn->Tag = text;

	if (frm_stats_options->m_show_wins_losses_ties)
	{
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "wins";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "losses";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "ties";
	NewColumn->Tag = value;
	}

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "kills for";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "kills against";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "injuries for";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "injuries against";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "total for";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "total against";
	NewColumn->Tag = value;

	main_form->ListView1->Visible = true;
}

void player_best_season_stats_handler::display_carnage (Tfrm_stats_manager * main_form)
{
	if (!g_crush_statistics)
	{
			return;
	}

	main_form->ListView1->Visible = false;

	main_form->table_tab->Enabled = true;
	main_form->table_tab->BringToFront ();
	main_form->graph_tab->Enabled = false;
	main_form->table_image->Visible = true;

	main_form->ListView1->Columns->Clear ();
	main_form->ListView1->Items->Clear ();

   	crush_player_list::iterator it = g_crush_statistics->m_crush_player_list.begin ();

	float rushing_average;
	float checking_average ;

   	while (it != g_crush_statistics->m_crush_player_list.end ())
   	{
   		crush_player_ptr current_player = *it;

		TListItem  *ListItem;

		ListItem = main_form->ListView1->Items->Add();
		ListItem->Caption = g_crush_statistics->get_name_for_race (current_player->m_race);
		ListItem->ImageIndex = current_player->m_race;

		ListItem->SubItems->Add(current_player->m_name.c_str ());

		if (current_player->m_dead)
		{
			ListItem->SubItemImages[0] = DEAD_IMAGE_INDEX;
		}
		ListItem->SubItems->Add(current_player->m_team_name.c_str ());

        ListItem->SubItemImages [1] = main_form->adjust_team_colors (current_player);

		if (frm_stats_options->m_show_player_value)
		{	
			ListItem->SubItems->Add (IntToStr (current_player->m_total_value));
		}
		//ListItem->SubItems->Add (IntToStr (current_player->m_points_to_spend));

		ListItem->SubItems->Add (IntToStr (current_player->m_best_kills_for));
		ListItem->SubItems->Add (IntToStr (current_player->m_best_injuries_for));
		ListItem->SubItems->Add (IntToStr (current_player->m_best_injuries_for + current_player->m_best_kills_for));

		it++;
	}

	TListColumn * NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "race";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "team name";
	NewColumn->Tag = text;
	if (frm_stats_options->m_show_player_value)
	{	
		NewColumn = main_form->ListView1->Columns->Add();
		NewColumn->Width = -2;
		NewColumn->Caption = "value";
		NewColumn->Tag = value;
	}
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "kills for";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "injuries for";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "total for";
	NewColumn->Tag = value;

	main_form->ListView1->Visible = true;
}

void player_career_stats_handler::display_carnage (Tfrm_stats_manager * main_form)
{
	if (!g_crush_statistics)
	{
			return;
	}

	main_form->table_tab->Enabled = true;
	main_form->table_tab->BringToFront ();
	main_form->graph_tab->Enabled = false;

	main_form->ListView1->Visible = false;

	main_form->ListView1->Columns->Clear ();
	main_form->ListView1->Items->Clear ();

   	crush_player_list::iterator it = g_crush_statistics->m_crush_player_list.begin ();

	float rushing_average;
	float checking_average ;

   	while (it != g_crush_statistics->m_crush_player_list.end ())
   	{
   		crush_player_ptr current_player = *it;

		TListItem  *ListItem;

		ListItem = main_form->ListView1->Items->Add();
		ListItem->Caption = g_crush_statistics->get_name_for_race (current_player->m_race);
		ListItem->ImageIndex = current_player->m_race;

		ListItem->SubItems->Add(current_player->m_name.c_str ());

		if (current_player->m_dead)
		{
			ListItem->SubItemImages[0] = DEAD_IMAGE_INDEX;
		}
		ListItem->SubItems->Add(current_player->m_team_name.c_str ());

        ListItem->SubItemImages [1] = main_form->adjust_team_colors (current_player);

		if (frm_stats_options->m_show_player_value)
		{	
			ListItem->SubItems->Add (IntToStr (current_player->m_total_value));
		}
		//ListItem->SubItems->Add (IntToStr (current_player->m_points_to_spend));

		ListItem->SubItems->Add (IntToStr (current_player->m_total_kills_for));
		ListItem->SubItems->Add (IntToStr (current_player->m_total_injuries_for));
		ListItem->SubItems->Add (IntToStr (current_player->m_total_injuries_for + current_player->m_total_kills_for));

		it++;
	}

	TListColumn * NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "race";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "team name";
	NewColumn->Tag = text;
	if (frm_stats_options->m_show_player_value)
	{	
		NewColumn = main_form->ListView1->Columns->Add();
		NewColumn->Width = -2;
		NewColumn->Caption = "value";
		NewColumn->Tag = value;
	}
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "kills for";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "injuries for";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "total for";
	NewColumn->Tag = value;

	main_form->ListView1->Visible = true;
}


void player_current_season_stats_handler::display_carnage (Tfrm_stats_manager * main_form)
{
	if (!g_crush_statistics)
	{
			return;
	}

    main_form->table_tab->BringToFront ();

	main_form->ListView1->Visible = false;

	main_form->ListView1->Columns->Clear ();
	main_form->ListView1->Items->Clear ();

   	crush_player_list::iterator it = g_crush_statistics->m_crush_player_list.begin ();

	float rushing_average;
	float checking_average ;

   	while (it != g_crush_statistics->m_crush_player_list.end ())
   	{
   		crush_player_ptr current_player = *it;

		TListItem  *ListItem;

		ListItem = main_form->ListView1->Items->Add();
		ListItem->Caption = g_crush_statistics->get_name_for_race (current_player->m_race);
		ListItem->ImageIndex = current_player->m_race;

		ListItem->SubItems->Add(current_player->m_name.c_str ());

		if (current_player->m_dead)
		{
			ListItem->SubItemImages[0] = DEAD_IMAGE_INDEX;
		}
		ListItem->SubItems->Add(current_player->m_team_name.c_str ());

        ListItem->SubItemImages [1] = main_form->adjust_team_colors (current_player);

		if (frm_stats_options->m_show_player_value)
		{	
			ListItem->SubItems->Add (IntToStr (current_player->m_total_value));
		}
		//ListItem->SubItems->Add (IntToStr (current_player->m_points_to_spend));

		ListItem->SubItems->Add (IntToStr (current_player->m_kills_for));
		ListItem->SubItems->Add (IntToStr (current_player->m_injuries_for));
		ListItem->SubItems->Add (IntToStr (current_player->m_injuries_for + current_player->m_kills_for));
		it++;
	}

	TListColumn * NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "race";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "team name";
	NewColumn->Tag = text;
	if (frm_stats_options->m_show_player_value)
	{	
		NewColumn = main_form->ListView1->Columns->Add();
		NewColumn->Width = -2;
		NewColumn->Caption = "value";
		NewColumn->Tag = value;
	}
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "kills for";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "injuries for";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "total for";
	NewColumn->Tag = value;

	main_form->ListView1->Visible = true;
}

void team_best_season_stats_handler::display_checks (Tfrm_stats_manager * main_form)
{
	if (!g_crush_statistics)
	{
			return;
	}

	main_form->ListView1->Visible = false;

	main_form->table_tab->Enabled = true;
	main_form->table_tab->BringToFront ();
	main_form->graph_tab->Enabled = false;
	main_form->table_image->Visible = true;

	main_form->ListView1->Columns->Clear ();
	main_form->ListView1->Items->Clear ();

   	crush_team_list::iterator it = g_crush_statistics->m_crush_team_list.begin ();

	float rushing_average;
	float checking_average ;

   	while (it != g_crush_statistics->m_crush_team_list.end ())
   	{
   		crush_team_ptr current_team = *it;

		TListItem  *ListItem;

		ListItem = main_form->ListView1->Items->Add();
		ListItem->Caption = current_team->m_name.c_str ();
		ListItem->ImageIndex = 8;

		ListItem->SubItems->Add(current_team->m_coach.c_str ());

		if (frm_stats_options->m_show_wins_losses_ties)
		{
		ListItem->SubItems->Add(IntToStr (current_team->m_best_wins));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_losses));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_ties));
		}

		ListItem->SubItems->Add(IntToStr (current_team->m_best_checks_thrown));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_checks_landed));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_sacks_against));
		ListItem->SubItems->Add(IntToStr (current_team->m_best_sacks_for));

		it++;
	}

	TListColumn * NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "team name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "coach";
	NewColumn->Tag = text;

	if (frm_stats_options->m_show_wins_losses_ties)
	{
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "wins";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "losses";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "ties";
	NewColumn->Tag = value;
	}

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "thrown";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "landed";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "sacks against";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "sacks for";
	NewColumn->Tag = value;

	main_form->ListView1->Visible = true;
}

void team_career_stats_handler::display_checks (Tfrm_stats_manager * main_form)
{
	if (!g_crush_statistics)
	{
			return;
	}

	main_form->ListView1->Visible = false;

	main_form->table_tab->Enabled = true;
	main_form->table_tab->BringToFront ();
	main_form->graph_tab->Enabled = false;
	main_form->table_image->Visible = true;

	main_form->ListView1->Columns->Clear ();
	main_form->ListView1->Items->Clear ();

   	crush_team_list::iterator it = g_crush_statistics->m_crush_team_list.begin ();

	float rushing_average;
	float checking_average ;

   	while (it != g_crush_statistics->m_crush_team_list.end ())
   	{
   		crush_team_ptr current_team = *it;

		TListItem  *ListItem;

		ListItem = main_form->ListView1->Items->Add();
		ListItem->Caption = current_team->m_name.c_str ();
		ListItem->ImageIndex = 8;

		ListItem->SubItems->Add(current_team->m_coach.c_str ());

		if (frm_stats_options->m_show_wins_losses_ties)
		{
		ListItem->SubItems->Add(IntToStr (current_team->m_total_wins));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_losses));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_ties));
		}

		ListItem->SubItems->Add(IntToStr (current_team->m_total_checks_thrown));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_checks_landed));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_sacks_against));
		ListItem->SubItems->Add(IntToStr (current_team->m_total_sacks_for));

		it++;
	}

	TListColumn * NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "team name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "coach";
	NewColumn->Tag = text;

	if (frm_stats_options->m_show_wins_losses_ties)
	{
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "wins";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "losses";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "ties";
	NewColumn->Tag = value;
	}

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "thrown";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "landed";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "sacks against";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "sacks for";
	NewColumn->Tag = value;

	main_form->ListView1->Visible = true;
}

void team_current_season_stats_handler::display_checks (Tfrm_stats_manager * main_form)
{
	if (!g_crush_statistics)
	{
			return;
	}

	main_form->ListView1->Visible = false;

	main_form->table_tab->Enabled = true;
	main_form->table_tab->BringToFront ();
	main_form->graph_tab->Enabled = false;
	main_form->table_image->Visible = true;

	main_form->ListView1->Columns->Clear ();
	main_form->ListView1->Items->Clear ();

   	crush_team_list::iterator it = g_crush_statistics->m_crush_team_list.begin ();

	float rushing_average;
	float checking_average ;

   	while (it != g_crush_statistics->m_crush_team_list.end ())
   	{
   		crush_team_ptr current_team = *it;

		TListItem  *ListItem;

		ListItem = main_form->ListView1->Items->Add();
		ListItem->Caption = current_team->m_name.c_str ();
		ListItem->ImageIndex = 8;

		ListItem->SubItems->Add(current_team->m_coach.c_str ());

		if (frm_stats_options->m_show_wins_losses_ties)
		{
		ListItem->SubItems->Add(IntToStr (current_team->m_wins));
		ListItem->SubItems->Add(IntToStr (current_team->m_losses));
		ListItem->SubItems->Add(IntToStr (current_team->m_ties));
		}

		ListItem->SubItems->Add(IntToStr (current_team->m_checks_thrown));
		ListItem->SubItems->Add(IntToStr (current_team->m_checks_landed));
		ListItem->SubItems->Add(IntToStr (current_team->m_sacks_against));
		ListItem->SubItems->Add(IntToStr (current_team->m_sacks_for));

		it++;
	}

	TListColumn * NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "team name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "coach";
	NewColumn->Tag = text;

	if (frm_stats_options->m_show_wins_losses_ties)
	{
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "wins";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "losses";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "ties";
	NewColumn->Tag = value;
	}

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "thrown";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "landed";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "sacks against";
	NewColumn->Tag = value;

	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "sacks for";
	NewColumn->Tag = value;

	main_form->ListView1->Visible = true;
}

void player_best_season_stats_handler::display_checks (Tfrm_stats_manager * main_form)
{
	if (!g_crush_statistics)
	{
			return;
	}

	main_form->ListView1->Visible = false;

	main_form->table_tab->Enabled = true;
	main_form->table_tab->BringToFront ();
	main_form->graph_tab->Enabled = false;
	main_form->table_image->Visible = true;

	main_form->ListView1->Columns->Clear ();
	main_form->ListView1->Items->Clear ();

   	crush_player_list::iterator it = g_crush_statistics->m_crush_player_list.begin ();

	float rushing_average;
	float checking_average ;

   	while (it != g_crush_statistics->m_crush_player_list.end ())
   	{
   		crush_player_ptr current_player = *it;

		TListItem  *ListItem;

		ListItem = main_form->ListView1->Items->Add();
		ListItem->Caption = g_crush_statistics->get_name_for_race (current_player->m_race);
		ListItem->ImageIndex = current_player->m_race;

		ListItem->SubItems->Add(current_player->m_name.c_str ());

		if (current_player->m_dead)
		{
			ListItem->SubItemImages[0] = DEAD_IMAGE_INDEX;
		}
		ListItem->SubItems->Add(current_player->m_team_name.c_str ());

        ListItem->SubItemImages [1] = main_form->adjust_team_colors (current_player);

		if (frm_stats_options->m_show_player_value)
		{	
			ListItem->SubItems->Add (IntToStr (current_player->m_total_value));
		}

		ListItem->SubItems->Add (IntToStr (current_player->m_best_checks_thrown));
		ListItem->SubItems->Add (IntToStr (current_player->m_best_checks_landed));

		if (current_player->m_best_checks_thrown != 0)
		{
				ListItem->SubItems->Add (FloatToStrF ((float)current_player->m_best_checks_landed / current_player->m_best_checks_thrown, ffGeneral, 5, 10));
		}
		else
		{
				ListItem->SubItems->Add (IntToStr (0));
		}

		ListItem->SubItems->Add (IntToStr (current_player->m_best_sacks_for));
		it++;
	}

	TListColumn * NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "race";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "team name";
	NewColumn->Tag = text;
	if (frm_stats_options->m_show_player_value)
	{	
		NewColumn = main_form->ListView1->Columns->Add();
		NewColumn->Width = -2;
		NewColumn->Caption = "value";
		NewColumn->Tag = value;
	}
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "thrown";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "landed";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "checking average";
	NewColumn->Tag = float_value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "sacks for";
	NewColumn->Tag = value;

	main_form->ListView1->Visible = true;
}

void player_career_stats_handler::display_checks (Tfrm_stats_manager * main_form)
{
	if (!g_crush_statistics)
	{
			return;
	}

	main_form->table_tab->Enabled = true;
	main_form->table_tab->BringToFront ();
	main_form->graph_tab->Enabled = false;

	main_form->ListView1->Visible = false;

	main_form->ListView1->Columns->Clear ();
	main_form->ListView1->Items->Clear ();

   	crush_player_list::iterator it = g_crush_statistics->m_crush_player_list.begin ();

	float rushing_average;
	float checking_average ;

   	while (it != g_crush_statistics->m_crush_player_list.end ())
   	{
   		crush_player_ptr current_player = *it;

		TListItem  *ListItem;

		ListItem = main_form->ListView1->Items->Add();
		ListItem->Caption = g_crush_statistics->get_name_for_race (current_player->m_race);
		ListItem->ImageIndex = current_player->m_race;

		ListItem->SubItems->Add(current_player->m_name.c_str ());

		if (current_player->m_dead)
		{
			ListItem->SubItemImages[0] = DEAD_IMAGE_INDEX;
		}
		ListItem->SubItems->Add(current_player->m_team_name.c_str ());

        ListItem->SubItemImages [1] = main_form->adjust_team_colors (current_player);

		if (frm_stats_options->m_show_player_value)
		{	
			ListItem->SubItems->Add (IntToStr (current_player->m_total_value));
		}

		ListItem->SubItems->Add (IntToStr (current_player->m_total_checks_thrown));
		ListItem->SubItems->Add (IntToStr (current_player->m_total_checks_landed));

		if (current_player->m_total_checks_thrown != 0)
		{
				ListItem->SubItems->Add (FloatToStrF ((float)current_player->m_total_checks_landed / current_player->m_total_checks_thrown, ffGeneral, 5, 10));
		}
		else
		{
				ListItem->SubItems->Add (IntToStr (0));
		}

		ListItem->SubItems->Add (IntToStr (current_player->m_total_sacks_for));
		it++;
	}

	TListColumn * NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "race";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "team name";
	NewColumn->Tag = text;
	if (frm_stats_options->m_show_player_value)
	{	
		NewColumn = main_form->ListView1->Columns->Add();
		NewColumn->Width = -2;
		NewColumn->Caption = "value";
		NewColumn->Tag = value;
	}
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "thrown";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "landed";
	NewColumn->Tag = value;
    NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "checking average";
	NewColumn->Tag = float_value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "sacks for";
	NewColumn->Tag = value;

	main_form->ListView1->Visible = true;
}

void player_current_season_stats_handler::display_checks (Tfrm_stats_manager * main_form)
{
	if (!g_crush_statistics)
	{
			return;
	}

    main_form->table_tab->BringToFront ();

	main_form->ListView1->Visible = false;

	main_form->ListView1->Columns->Clear ();
	main_form->ListView1->Items->Clear ();

   	crush_player_list::iterator it = g_crush_statistics->m_crush_player_list.begin ();

	float rushing_average;
	float checking_average ;

   	while (it != g_crush_statistics->m_crush_player_list.end ())
   	{
   		crush_player_ptr current_player = *it;

		TListItem  *ListItem;

		ListItem = main_form->ListView1->Items->Add();
		ListItem->Caption = g_crush_statistics->get_name_for_race (current_player->m_race);
		ListItem->ImageIndex = current_player->m_race;

		ListItem->SubItems->Add(current_player->m_name.c_str ());

		if (current_player->m_dead)
		{
			ListItem->SubItemImages[0] = DEAD_IMAGE_INDEX;
		}
		ListItem->SubItems->Add(current_player->m_team_name.c_str ());

        ListItem->SubItemImages [1] = main_form->adjust_team_colors (current_player);

		if (frm_stats_options->m_show_player_value)
		{	
			ListItem->SubItems->Add (IntToStr (current_player->m_total_value));
		}

		ListItem->SubItems->Add (IntToStr (current_player->m_checks_thrown));
		ListItem->SubItems->Add (IntToStr (current_player->m_checks_landed));

		if (current_player->m_checks_thrown != 0)
		{
				ListItem->SubItems->Add (FloatToStrF ((float)current_player->m_checks_landed / current_player->m_checks_thrown, ffGeneral, 5, 10));
		}
		else
		{
				ListItem->SubItems->Add (IntToStr (0));
		}

		ListItem->SubItems->Add (IntToStr (current_player->m_sacks_for));
		it++;
	}

	TListColumn * NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "race";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "name";
	NewColumn->Tag = text;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -1;
	NewColumn->Caption = "team name";
	NewColumn->Tag = text;
	if (frm_stats_options->m_show_player_value)
	{	
		NewColumn = main_form->ListView1->Columns->Add();
		NewColumn->Width = -2;
		NewColumn->Caption = "value";
		NewColumn->Tag = value;
	}
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "thrown";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "landed";
	NewColumn->Tag = value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "checking average";
	NewColumn->Tag = float_value;
	NewColumn = main_form->ListView1->Columns->Add();
	NewColumn->Width = -2;
	NewColumn->Caption = "sacks for";
	NewColumn->Tag = value;

	main_form->ListView1->Visible = true;
}

void __fastcall Tfrm_stats_manager::Exit1Click(TObject *Sender)
{
        Close ();
}

struct sort_information
{
        int m_sort_order;
        int m_column;
};

//---------------------------------------------------------------------------
int __stdcall CustomSortProc(long Item1, long Item2, long ParamSort)
{
	sort_information * sort  = (sort_information*)ParamSort;

	int var1;
	int var2;
	float float_var1;
	float float_var2;

	switch (sort->m_column)
	{
		case 0:
			switch ((frm_stats_manager->ListView1->Columns->Items[sort->m_column]->Tag))
			{
				case text:
					return (sort->m_sort_order) * CompareText(((TListItem *)Item1)->Caption, ((TListItem *)Item2)->Caption);
				case value:
					var1 = StrToInt (((TListItem *)Item1)->Caption);
					var2 = StrToInt (((TListItem *)Item2)->Caption);
					if (var1 == var2)
					{
						return 0;
					}
					else
					{
						if (var1 > var2)
							return (sort->m_sort_order) * 1;
						else
							return (sort->m_sort_order) * -1;
					}
				case float_value:
					float_var1 = StrToFloat (((TListItem *)Item1)->Caption);
					float_var2 = StrToFloat (((TListItem *)Item2)->Caption);
					if ((float_var1 < float_var2 + 0.0001) && (float_var1 > float_var2 - 0.0001 ))
					{
						return 0;
					}
					else
					{
						if (float_var1 > float_var2)
							return (sort->m_sort_order) * 1;
						else
							return (sort->m_sort_order) * -1;
					}
								

			}
		default:
			switch ((frm_stats_manager->ListView1->Columns->Items[sort->m_column]->Tag))
			{
				case text:
					if (((TListItem*)Item1)->SubItemImages[sort->m_column] == ((TListItem*)Item2)->SubItemImages[sort->m_column])
					{
						return (sort->m_sort_order) * CompareText(((TListItem *)Item1)->SubItems->Strings[sort->m_column-1], ((TListItem *)Item2)->SubItems->Strings[sort->m_column-1]);
					}
					else
					{
						return (((TListItem*)Item1)->SubItemImages[sort->m_column] < (((TListItem*)Item2)->SubItemImages[sort->m_column]));
					}
				case value:
					var1 = StrToInt (((TListItem *)Item1)->SubItems->Strings[sort->m_column-1]);
					var2 = StrToInt (((TListItem *)Item2)->SubItems->Strings[sort->m_column-1]);
					if (var1 == var2)
					{
						return 0;
					}
					else
					{
						if (var1 > var2)
							return (sort->m_sort_order) * 1;
						else
							return (sort->m_sort_order) * -1;
					}
				case float_value:
                                        float_var1 = StrToFloat (((TListItem *)Item1)->SubItems->Strings[sort->m_column-1]);
					float_var2 = StrToFloat (((TListItem *)Item2)->SubItems->Strings[sort->m_column-1]);
					if ((float_var1 < float_var2 + 0.0001) && (float_var1 > float_var2 - 0.0001 ))
					{
						return 0;
					}
					else
					{
						if (float_var1 > float_var2)
							return (sort->m_sort_order) * 1;
						else
							return (sort->m_sort_order) * -1;
					}
			}
	}
}

void __fastcall Tfrm_stats_manager::ListView1ColumnClick(TObject *Sender,
      TListColumn *Column)
{
	static int sort_order = 1;
	sort_order = -sort_order;

	sort_information * sort = new sort_information;
  	sort->m_sort_order = sort_order;
  	sort->m_column = Column->Index;

  	ListView1->CustomSort(CustomSortProc, (int)sort);

  	delete sort;
}
//--------------------------------------------------------------------------

void __fastcall Tfrm_stats_manager::FormCreate(TObject *Sender)
{
        g_crush_statistics = NULL;
}
//---------------------------------------------------------------------------



void __fastcall Tfrm_stats_manager::import_buttonClick(TObject *Sender)
{
	show_rotates (false);
	if (!g_crush_statistics)
	{
		g_crush_statistics = new crush_statistics;
		open_dialog->Execute ();
		if (!g_crush_statistics->read_crush_statistics (open_dialog->FileName.c_str ()))
		{
			Application->MessageBox("Failed to import stats file", NULL, MB_OK);
			return;
		}
		Caption = Caption.sprintf ("Crush Stats Manager - Season %d Week %d (%s)", 
						g_crush_statistics->m_current_crush_season, 
						g_crush_statistics->m_display_crush_week, 
						open_dialog->FileName.c_str ());
		update_colors ();
		m_player_current_season_stats_handler.display_rushing (frm_stats_manager);
		g_last_tab_index = rushing;
		table_tab->TabIndex = rushing;
	}
	else
	{
		if (Application->MessageBox("Already imported crush stats.  Overwrite ?", NULL, MB_YESNO) == IDYES)
		{
			g_crush_statistics->clear_crush_statistics ();
			open_dialog->Execute ();
			if (!g_crush_statistics->read_crush_statistics (open_dialog->FileName.c_str ()))
			{
				Application->MessageBox("Failed to import stats file", NULL, MB_OK);
				return;
			}
			Caption = Caption.sprintf ("Crush Stats Manager - Season %d Week %d (%s)", 
						g_crush_statistics->m_current_crush_season, 
						g_crush_statistics->m_display_crush_week, 
						open_dialog->FileName.c_str ());
			update_colors ();
			m_player_current_season_stats_handler.display_rushing (frm_stats_manager);
			g_last_tab_index = rushing;
			table_tab->TabIndex = rushing;
		}
	}
}
//---------------------------------------------------------------------------

void __fastcall Tfrm_stats_manager::export_buttonClick(TObject *Sender)
{
	show_rotates (false);
	if (g_crush_statistics)
   	{
		save_dialog->Execute ();
	   	if (!g_crush_statistics->write_crush_statistics (save_dialog->FileName.c_str ()))
		{
			Application->MessageBox("Failed to export stats file", NULL, MB_OK);
		}
	}
	else
   	{
		Application->MessageBox("Invalid Stats Handle", NULL, MB_OK);
	}
}

//---------------------------------------------------------------------------

void __fastcall Tfrm_stats_manager::add_buttonClick(TObject *Sender)
{
    show_rotates (false);
	if (g_crush_statistics)
	{
		add_dialog->Execute ();
		if (!g_crush_statistics->read_team_information (add_dialog->FileName.c_str ()))
		{
			Application->MessageBox("Failed to add stats file", NULL, MB_OK);
			return;
		}
		Caption = Caption.sprintf ("Crush Stats Manager - Season %d Week %d (%s)", 
				g_crush_statistics->m_current_crush_season, 
				g_crush_statistics->m_display_crush_week,
				open_dialog->FileName.c_str ());
		update_colors ();
		m_player_current_season_stats_handler.display_rushing (frm_stats_manager);
        g_last_tab_index = rushing;
		table_tab->TabIndex = rushing;
	}
	else
	{
 		g_crush_statistics = new crush_statistics;
       	add_dialog->Execute ();
		if (!g_crush_statistics->read_team_information (add_dialog->FileName.c_str ()))
		{
			Application->MessageBox("Failed to add stats file", NULL, MB_OK);
			return;
		}
		Caption = Caption.sprintf ("Crush Stats Manager - Season %d Week %d (%s)", 
					g_crush_statistics->m_current_crush_season, 
					g_crush_statistics->m_display_crush_week, 
					open_dialog->FileName.c_str ());
		update_colors ();
		m_player_current_season_stats_handler.display_rushing (frm_stats_manager);
        g_last_tab_index = rushing;
		table_tab->TabIndex = rushing;
	}        
}
//---------------------------------------------------------------------------

void __fastcall Tfrm_stats_manager::team_season_graph_buttonClick(
      TObject *Sender)
{
        show_rotates (true);
        m_team_season_graph_handler.display (frm_stats_manager);
}
//---------------------------------------------------------------------------

void __fastcall Tfrm_stats_manager::team_career_graph_buttonClick(
      TObject *Sender)
{
        show_rotates (true);
        m_team_career_graph_handler.display (frm_stats_manager);
}
//---------------------------------------------------------------------------

void __fastcall Tfrm_stats_manager::player_best_stats_buttonClick(
      TObject *Sender)
{
        show_rotates (false);
        table_tab->TabIndex = g_last_tab_index;
        table_tabChange(Sender);
}
//---------------------------------------------------------------------------

void __fastcall Tfrm_stats_manager::player_career_stats_buttonClick(
      TObject *Sender)
{
        show_rotates (false);
        table_tab->TabIndex = g_last_tab_index;
        table_tabChange(Sender);
}
//---------------------------------------------------------------------------

void __fastcall Tfrm_stats_manager::player_season_stats_buttonClick(
      TObject *Sender)
{
        show_rotates (false);
        table_tab->TabIndex = g_last_tab_index;
        table_tabChange(Sender);
}
//---------------------------------------------------------------------------

void __fastcall Tfrm_stats_manager::team_season_stats_buttonClick(
      TObject *Sender)
{
        show_rotates (false);
        table_tab->TabIndex = g_last_tab_index;
		table_tabChange(Sender);
}
//---------------------------------------------------------------------------

void __fastcall Tfrm_stats_manager::team_best_stats_buttonClick(
      TObject *Sender)
{
        show_rotates (false);
        table_tab->TabIndex = g_last_tab_index;
        table_tabChange(Sender);
}
//---------------------------------------------------------------------------

void __fastcall Tfrm_stats_manager::team_career_stats_buttonClick(
      TObject *Sender)
{
        show_rotates (false);
        table_tab->TabIndex = g_last_tab_index;
        table_tabChange(Sender);
}
//---------------------------------------------------------------------------


void __fastcall Tfrm_stats_manager::ListView1SelectItem(TObject *Sender,
      TListItem *Item, bool Selected)
{
        if (!ListView1->Selected)
        {
                return;
        }

        AnsiString temp;
        temp = temp.sprintf ("%s (%d)",Item->SubItems->Strings[0],Item->Index+1);
        status_bar->Panels->Items[0]->Text = temp;
}
//---------------------------------------------------------------------------

void __fastcall Tfrm_stats_manager::ListView1DblClick(TObject *Sender)
{
        if (!ListView1->Selected)
        {
                return;
        }

        status_bar->Panels->Items[0]->Text = ListView1->Selected->SubItems->Strings[0];

		crush_player_list::iterator it = g_crush_statistics->m_crush_player_list.begin ();

		crush_player_ptr found_player = NULL;

		while (it != g_crush_statistics->m_crush_player_list.end ())
		{
			crush_player_ptr current_player = *it;

			string check = ListView1->Selected->SubItems->Strings[0].c_str ();
                   	string team_check = ListView1->Selected->SubItems->Strings[1].c_str ();
			if (current_player->m_name == check &&
                current_player->m_race == ListView1->Selected->ImageIndex &&
                current_player->m_team_name == team_check)
			{
				found_player = current_player;
				break;
			}
            it++;
		}

		if (!found_player)
		{
			return;
		}

        player_profile->player = found_player;
        player_profile->Show ();
}
//---------------------------------------------------------------------------

void __fastcall Tfrm_stats_manager::stats_graphDblClick(TObject *Sender)
{
        stats_graph->View3D = !(stats_graph->View3D);
        graph_tabChange(Sender);
}
//---------------------------------------------------------------------------

void __fastcall Tfrm_stats_manager::rotate_leftClick(TObject *Sender)
{
        stats_graph->View3DOptions->Orthogonal = false;

        if (stats_graph->View3DOptions->Rotation > 270)
        {
                stats_graph->View3DOptions->Rotation -= 10;
        }
        player_graph->View3DOptions->Orthogonal = false;

        if (player_graph->View3DOptions->Rotation > 270)
        {
                player_graph->View3DOptions->Rotation -= 10;
        }
}
//---------------------------------------------------------------------------

void __fastcall Tfrm_stats_manager::rotate_rightClick(TObject *Sender)
{
        stats_graph->View3DOptions->Orthogonal = false;

        if (stats_graph->View3DOptions->Rotation < 360)
        {
                stats_graph->View3DOptions->Rotation += 10;
        }
       player_graph->View3DOptions->Orthogonal = false;

        if (player_graph->View3DOptions->Rotation < 360)
        {
                player_graph->View3DOptions->Rotation += 10;
        }
}

//---------------------------------------------------------------------------

void Tfrm_stats_manager::show_rotates (bool show)
{
        rotate_right->Visible = show;
        rotate_left->Visible = show;
}

void __fastcall Tfrm_stats_manager::options_buttonClick(TObject *Sender)
{
        frm_stats_options->ShowModal ();
}

//---------------------------------------------------------------------------

void __fastcall Tfrm_stats_manager::FormKeyUp(TObject *Sender, WORD &Key,
      TShiftState Shift)
{
        if (Key == VK_F2)
        {
                import_buttonClick(Sender);
        }
        if (Key == VK_F3)
        {
                export_buttonClick(Sender);
        }
        if (Key == VK_F4)
        {
                add_buttonClick(Sender);
        }
}

//---------------------------------------------------------------------------

void __fastcall Tfrm_stats_manager::select_deselect_buttonClick(
      TObject *Sender)
{
        static bool current_state = false;

        for (int item_index = 0; item_index < ListView1->Items->Count; item_index++)
        {
                ListView1->Items->Item[item_index]->Checked = current_state;
        }

        if (current_state)
		{
                current_state = false;
		}
        else
		{
                current_state = true;
		}
}

//---------------------------------------------------------------------------

void __fastcall Tfrm_stats_manager::player_season_graph_buttonClick(
      TObject *Sender)
{
        show_rotates (true);
		g_graph_type = season;
        player_graph_tab->BringToFront ();

        player_graph_tab->Enabled = true;
        
		graph_tab->Enabled = false;
        table_tab->Enabled = false;

        player_graph_tabChange (Sender);
}

//---------------------------------------------------------------------------

void __fastcall Tfrm_stats_manager::player_graph_tabChange(TObject *Sender)
{
	crush_player_list temp_player_list;

	for (int item_index = 0; item_index < ListView1->Items->Count; item_index++)
	{
			if (ListView1->Items->Item[item_index]->Selected)
			{
				crush_player_list::iterator it = g_crush_statistics->m_crush_player_list.begin ();

				while (it != g_crush_statistics->m_crush_player_list.end ())
				{
					crush_player_ptr current_player = *it;

					string check = ListView1->Items->Item[item_index]->SubItems->Strings[0].c_str ();
					string team_check = ListView1->Items->Item[item_index]->SubItems->Strings[1].c_str ();

					if (current_player->m_name == check &&
						current_player->m_race == ListView1->Items->Item[item_index]->ImageIndex &&
						current_player->m_team_name == team_check)
					{
						temp_player_list.push_back (current_player);
						break;
					}
					it++;
				}
			}
	}


	while (player_graph->SeriesList->Count > 0)
	{
		TChartSeries * remove_series = player_graph->Series [0];
		player_graph->RemoveSeries(player_graph->Series[0]);
		delete remove_series;
	}

	string chart_title;

	crush_player_list::iterator player_it = temp_player_list.begin ();

	static double color_offset = 0;

	while (player_it != temp_player_list.end ())
	{
		TLineSeries * current_series = new Series::TLineSeries (player_graph);
		crush_player_ptr current_player = *player_it;
		crush_player_historical_week_list::iterator it = current_player->m_crush_player_historical_week_list.begin ();

		double last_value = -1;
		double current_value = 0;
		double value1, value2;
		int index = current_player->m_first_crush_week;

		while (it != current_player->m_crush_player_historical_week_list.end ())
		{
			crush_player_historical_week_ptr current_player_historical_week = *it;

			switch (g_graph_type)
			{
				case season:
					switch (player_graph_tab->TabIndex)
					{
						case player_value_changed:
							if (last_value == -1)
							{
								current_value = current_player_historical_week->m_total_value;
								last_value = current_value;	
							}
							else
							{
								current_value = current_player_historical_week->m_total_value - last_value;
								last_value = current_player_historical_week->m_total_value;
							}
							chart_title = "Season - value changed";
							break;
						case player_checking_average:
							if (current_player_historical_week->m_checks_thrown != 0)
							{
								value1 = current_player_historical_week->m_checks_landed;
								value2 = current_player_historical_week->m_checks_thrown;
								current_value = (value1 / value2);
							}
							else
							{
								current_value = 0;
							}
							chart_title = "Season - checking average";
							break;
						case player_value:
							current_value = current_player_historical_week->m_total_value;
							chart_title = "Season - value";
							break;
						case player_rushing_tiles:
							current_value = current_player_historical_week->m_rushing_tiles;
							chart_title = "Season - rushing_tiles";
							break;
						case player_goals_scored:
							current_value = current_player_historical_week->m_goals_scored;
							chart_title = "Season - goals_scored";
							break;
						case player_kills_for:
							current_value = current_player_historical_week->m_kills_for;
							chart_title = "Season - kills_for";
							break;
						case player_injuries_for:
							current_value = current_player_historical_week->m_injuries_for;
							chart_title = "Season - injuries_for";
							break;
						case player_checks_thrown:
							current_value = current_player_historical_week->m_checks_thrown;
							chart_title = "Season - checks_thrown";
							break;
						case player_checks_landed:
							current_value = current_player_historical_week->m_checks_landed;
							chart_title = "Season - checks_landed";
							break;
						case player_rushing_attempts:
							current_value = current_player_historical_week->m_rushing_attempts;
							chart_title = "Season - rushing_attempts";
							break;
						case player_sacks_for:
							current_value = current_player_historical_week->m_sacks_for;
							chart_title = "Season - sacks_for";
							break;
					}
					break;
				case career:
					switch (player_graph_tab->TabIndex)
					{
						case player_value_changed:
							if (last_value == -1)
							{
								current_value = current_player_historical_week->m_total_value;
								last_value = current_value;	
							}
							else
							{
								current_value = current_player_historical_week->m_total_value - last_value;
								last_value = current_player_historical_week->m_total_value;
							}
							chart_title = "Career - value changed";
							break;
						case player_checking_average:
							if (current_player_historical_week->m_total_checks_thrown != 0)
							{

								value1 = current_player_historical_week->m_total_checks_landed;
								value2 = current_player_historical_week->m_total_checks_thrown;
								current_value = (value1 / value2);
							}
							else
							{
								current_value = 0;
							}
							chart_title = "Career - checking average";
							break;
						case player_value:
							current_value = current_player_historical_week->m_total_value;
							chart_title = "Career - value";
							break;
						case player_rushing_tiles:
							current_value = current_player_historical_week->m_total_rushing_tiles;
							chart_title = "Career - total_rushing_tiles";
							break;
						case player_goals_scored:
							current_value = current_player_historical_week->m_total_goals_scored;
							chart_title = "Career - total_goals_scored";
							break;
						case player_kills_for:
							current_value = current_player_historical_week->m_total_kills_for;
							chart_title = "Career - total_kills_for";
							break;
						case player_injuries_for:
							current_value = current_player_historical_week->m_total_injuries_for;
							chart_title = "Career - total_injuries_for";
							break;
						case player_checks_thrown:
							current_value = current_player_historical_week->m_total_checks_thrown;
							chart_title = "Career - total_checks_thrown";
							break;
						case player_checks_landed:
							current_value = current_player_historical_week->m_total_checks_landed;
							chart_title = "Career - total_checks_landed";
							break;
						case player_rushing_attempts:
							current_value = current_player_historical_week->m_total_rushing_attempts;
							chart_title = "Career - total_rushing_attempts";
							break;
						case player_sacks_for:
							current_value = current_player_historical_week->m_total_sacks_for;
							chart_title = "Career - total_sacks_for";
							break;
					}
					break;
			}

			AnsiString  xy_label;

			xy_label.sprintf ("%d/%d", current_player_historical_week->m_current_crush_week, current_player_historical_week->m_current_crush_season);

			current_series->AddXY ( index, current_value, xy_label, clTeeColor);

			index ++;
			it++;
		}

		current_series->Title = current_player->m_name.c_str ();
		current_series->LinePen->Width = 2;
		current_series->LinePen->Color = clWhite;

		current_series->SeriesColor = g_crush_statistics->get_color_for_crush_color (current_player->m_jersey_color) + color_offset;
		current_series->ParentChart = player_graph;

		player_graph->Title->Text->Clear ();
		player_graph->Title->Text->Add (chart_title.c_str());

		color_offset += 0.5;

		player_it++;
	}

	color_offset = 0;

	player_graph->Title->Text->Clear ();
	player_graph->Title->Text->Add (chart_title.c_str());

	temp_player_list.clear ();
}

//---------------------------------------------------------------------------

void __fastcall Tfrm_stats_manager::player_career_graph_buttonClick(
      TObject *Sender)
{
        show_rotates (true);
		g_graph_type = career;
        player_graph_tab->BringToFront ();

        player_graph_tab->Enabled = true;
        
		graph_tab->Enabled = false;
        table_tab->Enabled = false;

        player_graph_tabChange (Sender);
}

//---------------------------------------------------------------------------

void __fastcall Tfrm_stats_manager::player_graphDblClick(TObject *Sender)
{
    player_graph->View3D = !(player_graph->View3D);
    player_graph_tabChange(Sender);
}

//---------------------------------------------------------------------------

