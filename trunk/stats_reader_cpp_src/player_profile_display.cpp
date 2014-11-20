//---------------------------------------------------------------------------

#include <vcl.h>
#pragma hdrstop

#include "player_profile_display.h"
#include "stats_display.h"
//---------------------------------------------------------------------------
#pragma package(smart_init)
#pragma resource "*.dfm"
Tplayer_profile *player_profile;

enum graph_type 
{
        none = 0,
	career,
	season
};

graph_type g_graph_type;

//---------------------------------------------------------------------------
__fastcall Tplayer_profile::Tplayer_profile(TComponent* Owner)
        : TForm(Owner)
{
	g_graph_type = none;
}
//---------------------------------------------------------------------------
void __fastcall Tplayer_profile::FormActivate(TObject *Sender)
{
        //player_image->Picture->LoadFromFile ("bug_big.bmp");
        //player_image->Picture->Bitmap

        if (player->m_player_color_container && player->m_player_color_container->m_color_bitmap)
		{
			col_bitmap->Picture->Bitmap = player->m_player_color_container->m_color_bitmap;
		}

        player_image->Canvas->Brush->Color = clBlack;
        player_image->Canvas->FillRect (TRect (0,0,player_image->Width,player_image->Height));
        image_list->GetBitmap (player->m_race, player_image->Picture->Bitmap);

        dead_image->Canvas->Brush->Color = clBlack;
        dead_image->Canvas->FillRect (TRect (0,0,dead_image->Width,dead_image->Height));

        if (player->m_dead)
                image_list->GetBitmap (8, dead_image->Picture->Bitmap);

        details_list->Items->Clear ();

        TListItem  *ListItem;
        ListItem = details_list->Items->Add();
        ListItem->Caption = "Name";
        ListItem->SubItems->Add(player->m_name.c_str ());

        ListItem = details_list->Items->Add();
        ListItem->Caption = "Team name";
        ListItem->SubItems->Add(player->m_team_name.c_str ());

        ListItem = details_list->Items->Add();
        ListItem->Caption = "Total value";
        ListItem->SubItems->Add(IntToStr (player->m_total_value));

        ListItem = details_list->Items->Add();
        ListItem->Caption = "Points to spend";
        ListItem->SubItems->Add(IntToStr (player->m_points_to_spend));

        ListItem = details_list->Items->Add();
        ListItem->Caption = "first week played";
        ListItem->SubItems->Add(IntToStr (player->m_first_crush_week));

        ListItem = details_list->Items->Add();
        ListItem->Caption = "first season played";
        ListItem->SubItems->Add(IntToStr (player->m_first_crush_season));

        ListItem = details_list->Items->Add();
        ListItem->Caption = "last week played";
        ListItem->SubItems->Add(IntToStr (player->m_last_crush_week));

        ListItem = details_list->Items->Add();
        ListItem->Caption = "last season played";
        ListItem->SubItems->Add(IntToStr (player->m_last_crush_season));

        ListItem = details_list->Items->Add();
        ListItem->Caption = "Action Points";
        ListItem->SubItems->Add(IntToStr (player->m_ap));

        ListItem = details_list->Items->Add();
        ListItem->Caption = "Reflexes";
        ListItem->SubItems->Add(IntToStr (player->m_reflexes));

        ListItem = details_list->Items->Add();
        ListItem->Caption = "Jump";
        ListItem->SubItems->Add(IntToStr (player->m_jump));

        ListItem = details_list->Items->Add();
        ListItem->Caption = "Checking";
        ListItem->SubItems->Add(IntToStr (player->m_checking));

        ListItem = details_list->Items->Add();
        ListItem->Caption = "Strength";
        ListItem->SubItems->Add(IntToStr (player->m_strength));

        ListItem = details_list->Items->Add();
        ListItem->Caption = "Toughness";
        ListItem->SubItems->Add(IntToStr (player->m_toughness));

        ListItem = details_list->Items->Add();
        ListItem->Caption = "Hands";
        ListItem->SubItems->Add(IntToStr (player->m_hands));

        ListItem = details_list->Items->Add();
        ListItem->Caption = "Dodge";
        ListItem->SubItems->Add(IntToStr (player->m_dodge));
		
		player_profile->graph_tabChange (player_profile);
}
//---------------------------------------------------------------------------

void __fastcall Tplayer_profile::okClick(TObject *Sender)
{
        Close ();
}
//---------------------------------------------------------------------------



void __fastcall Tplayer_profile::Button1Click(TObject *Sender)
{
        AnsiString message;

        if (!player->m_dead)
        {
                MessageDlg ("You are attempting to delete a player that is not dead,  you bastard", mtError, TMsgDlgButtons () << mbOK, 0);
                return;
        }

        message = message.sprintf ("Are you sure you want to delete the player %s", player->m_name.c_str ());
        if (MessageDlg (message, mtWarning, TMsgDlgButtons() << mbYes << mbNo, 0) == mrYes)
        {
                g_crush_statistics->m_crush_player_list.remove (player);
                frm_stats_manager->m_player_current_season_stats_handler.display_rushing (frm_stats_manager);
                frm_stats_manager->table_tab->TabIndex = rushing;
                Close ();
        }
}
//---------------------------------------------------------------------------

void __fastcall Tplayer_profile::btn_historyClick(TObject *Sender)
{
    switch (g_graph_type)
	{
		case none:
			g_graph_type = season;
        	player_profile->Width = player_profile->Width + 458;
        	btn_history->Caption = "Career";
			break;
		case season:
			g_graph_type = career;
        	btn_history->Caption = "History <<";
			break;
		case career:
			g_graph_type = none;
        	player_profile->Width = player_profile->Width - 458;
        	btn_history->Caption = "Season >>";
			break;
	}

	player_profile->graph_tabChange (player_profile);
}

//---------------------------------------------------------------------------

void __fastcall Tplayer_profile::graph_tabChange(TObject *Sender)
{
	if (!g_crush_statistics || g_graph_type == none)
	{
		return;
	}

	double current_value = 0;
	double last_value = -1;

	while (stats_graph->SeriesList->Count > 0)
	{
		TChartSeries * remove_series = stats_graph->Series [0];
		stats_graph->RemoveSeries(stats_graph->Series[0]);
		delete remove_series;
	}

	string chart_title;

	TLineSeries * current_series = new Series::TLineSeries (stats_graph);

	crush_player_historical_week_list::iterator it = player->m_crush_player_historical_week_list.begin ();

	int index = 0;

	while (it != player->m_crush_player_historical_week_list.end ())
	{
		crush_player_historical_week_ptr current_player_historical_week = *it;

		double value1, value2;

		switch (g_graph_type)
		{
			case season:
				switch (graph_tab->TabIndex)
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
				switch (graph_tab->TabIndex)
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

	current_series->Title = player->m_name.c_str ();
	current_series->LinePen->Width = 2;
	current_series->LinePen->Color = clWhite;

	current_series->SeriesColor = g_crush_statistics->get_color_for_crush_color (player->m_jersey_color);
	current_series->ParentChart = stats_graph;

	stats_graph->Title->Text->Clear ();
	stats_graph->Title->Text->Add (chart_title.c_str());

}
//---------------------------------------------------------------------------

