//---------------------------------------------------------------------------

#ifndef stats_displayH
#define stats_displayH
//---------------------------------------------------------------------------
#include <Classes.hpp>
#include <Controls.hpp>
#include <StdCtrls.hpp>
#include <Forms.hpp>
#include <OleCtrls.hpp>
#include <vcfi.hpp>
#include <ComCtrls.hpp>
#include <Chart.hpp>
#include <ExtCtrls.hpp>
#include <Graphics.hpp>
#include <Series.hpp>
#include <TeEngine.hpp>
#include <TeeProcs.hpp>
#include <Menus.hpp>
#include <OleServer.hpp>
#include <Grids.hpp>
#include <ImgList.hpp>
#include <Dialogs.hpp>
#include <ToolWin.hpp>
#include <Buttons.hpp>
#include <Series.hpp>

extern crush_statistics_ptr g_crush_statistics;

class Tfrm_stats_manager;

enum column_data_types
{
	text = 0,
	value,
        float_value
};

enum graph_tab_values
{
        rushing_tiles = 0,
        kills_for,
        kills_against,
	injuries_for,
	injuries_against,
	checks_thrown,
	checks_landed,
	activated_pads,
	balls_fumbled,
	rushing_attempts,
	ball_control,
	sacks_against,
	sacks_for,
	wins,
	losses,
	ties,
	team_value
};

enum table_tab_values
{
        rushing = 0,
        checks,
        carnage,
        all
};

class team_season_graph_handler
{
public:
	void display (Tfrm_stats_manager  * main_form);
};

class team_career_graph_handler
{
public:
	void display (Tfrm_stats_manager * main_form);
};

class player_best_season_stats_handler
{
public:
	void display_all (Tfrm_stats_manager * main_form);
	void display_rushing (Tfrm_stats_manager * main_form);
	void display_checks (Tfrm_stats_manager * main_form);
	void display_carnage (Tfrm_stats_manager * main_form);
};

class player_current_season_stats_handler
{
public:
	void display_all (Tfrm_stats_manager * main_form);
	void display_rushing (Tfrm_stats_manager * main_form);
	void display_checks (Tfrm_stats_manager * main_form);
	void display_carnage (Tfrm_stats_manager * main_form);
};

class player_career_stats_handler
{
public:
	void display_all (Tfrm_stats_manager * main_form);
	void display_rushing (Tfrm_stats_manager * main_form);
	void display_checks (Tfrm_stats_manager * main_form);
	void display_carnage (Tfrm_stats_manager * main_form);
};

class team_best_season_stats_handler
{
public:
	void display_all (Tfrm_stats_manager * main_form);
	void display_rushing (Tfrm_stats_manager * main_form);
	void display_checks (Tfrm_stats_manager * main_form);
	void display_carnage (Tfrm_stats_manager * main_form);
};

class team_current_season_stats_handler
{
public:
	void display_all (Tfrm_stats_manager * main_form);
	void display_rushing (Tfrm_stats_manager * main_form);
	void display_checks (Tfrm_stats_manager * main_form);
	void display_carnage (Tfrm_stats_manager * main_form);
};

class team_career_stats_handler
{
public:
	void display_all (Tfrm_stats_manager * main_form);
	void display_rushing (Tfrm_stats_manager * main_form);
	void display_checks (Tfrm_stats_manager * main_form);
	void display_carnage (Tfrm_stats_manager * main_form);
};

//---------------------------------------------------------------------------
class Tfrm_stats_manager : public TForm
{
__published:	// IDE-managed Components
        TImageList *ImageList1;
        TOpenDialog *open_dialog;
        TTabControl *graph_tab;
        TChart *stats_graph;
        TTabControl *table_tab;
    TListView *ListView1;
        TStatusBar *status_bar;
        TCoolBar *CoolBar1;
        TSpeedButton *import_button;
        TSpeedButton *add_button;
        TSpeedButton *export_button;
        TOpenDialog *add_dialog;
        TSaveDialog *save_dialog;
        TSpeedButton *player_season_stats_button;
        TSpeedButton *player_best_stats_button;
        TSpeedButton *player_career_stats_button;
        TSpeedButton *team_season_stats_button;
        TSpeedButton *team_best_stats_button;
        TSpeedButton *team_career_stats_button;
        TSpeedButton *team_season_graph_button;
        TSpeedButton *team_career_graph_button;
        TImage *table_image;
        TSpeedButton *options_button;
        TSpeedButton *rotate_right;
        TSpeedButton *rotate_left;
        TTabControl *player_graph_tab;
        TChart *player_graph;
        TSpeedButton *player_season_graph_button;
        TSpeedButton *player_career_graph_button;
    TSpeedButton *select_deselect_button;
        void __fastcall table_tabChange(TObject *Sender);
        void __fastcall graph_tabChange(TObject *Sender);
        void __fastcall Exit1Click(TObject *Sender);
        void __fastcall ListView1ColumnClick(TObject *Sender,
          TListColumn *Column);
        void __fastcall FormCreate(TObject *Sender);
        void __fastcall import_buttonClick(TObject *Sender);
        void __fastcall export_buttonClick(TObject *Sender);
        void __fastcall add_buttonClick(TObject *Sender);
        void __fastcall team_season_graph_buttonClick(TObject *Sender);
        void __fastcall team_career_graph_buttonClick(TObject *Sender);
        void __fastcall player_best_stats_buttonClick(TObject *Sender);
        void __fastcall player_career_stats_buttonClick(TObject *Sender);
        void __fastcall player_season_stats_buttonClick(TObject *Sender);
        void __fastcall team_season_stats_buttonClick(TObject *Sender);
        void __fastcall team_best_stats_buttonClick(TObject *Sender);
        void __fastcall team_career_stats_buttonClick(TObject *Sender);
        void __fastcall ListView1SelectItem(TObject *Sender,
                 TListItem *Item, bool Selected);
        void __fastcall ListView1DblClick(TObject *Sender);
        void __fastcall stats_graphDblClick(TObject *Sender);
        void __fastcall rotate_leftClick(TObject *Sender);
        void __fastcall rotate_rightClick(TObject *Sender);
        void __fastcall options_buttonClick(TObject *Sender);
        void __fastcall FormKeyUp(TObject *Sender, WORD &Key,
          TShiftState Shift);
        void __fastcall select_deselect_buttonClick(TObject *Sender);
        void __fastcall player_season_graph_buttonClick(TObject *Sender);
    void __fastcall player_graph_tabChange(TObject *Sender);
    void __fastcall player_career_graph_buttonClick(TObject *Sender);
    void __fastcall player_graphDblClick(TObject *Sender);
private:	// User declarations
public:		// User declarations

        int adjust_team_colors (crush_player_ptr crush_player);
        void update_colors ();
        void show_rotates (bool show);

		team_season_graph_handler m_team_season_graph_handler;
		team_career_graph_handler m_team_career_graph_handler;
		player_best_season_stats_handler m_player_best_season_stats_handler;
		player_current_season_stats_handler m_player_current_season_stats_handler;
		player_career_stats_handler m_player_career_stats_handler;
		team_best_season_stats_handler m_team_best_season_stats_handler;
		team_current_season_stats_handler m_team_current_season_stats_handler;
		team_career_stats_handler m_team_career_stats_handler;

        __fastcall Tfrm_stats_manager(TComponent* Owner);
};


//---------------------------------------------------------------------------
extern PACKAGE Tfrm_stats_manager *frm_stats_manager;
//---------------------------------------------------------------------------
#endif
