//---------------------------------------------------------------------------

#ifndef player_profile_displayH
#define player_profile_displayH
//---------------------------------------------------------------------------
#include <Classes.hpp>
#include <Controls.hpp>
#include <StdCtrls.hpp>
#include <Forms.hpp>
#include <ExtCtrls.hpp>
#include <ImgList.hpp>
#include <Graphics.hpp>
#include "crush_stats.h"
#include <ComCtrls.hpp>
#include <Chart.hpp>
#include <TeEngine.hpp>
#include <TeeProcs.hpp>
//---------------------------------------------------------------------------

enum player_graph_tab_values
{
	player_rushing_tiles = 0,
	player_goals_scored,
   	player_kills_for,
	player_injuries_for,
	player_checks_thrown,
	player_checks_landed,
	player_checking_average,
	player_rushing_attempts,
	player_sacks_for,
	player_value_changed,
	player_value
};

class Tplayer_profile : public TForm
{
__published:	// IDE-managed Components
        TImage *player_image;
        TButton *ok;
        TImageList *image_list;
        TListView *details_list;
        TImage *dead_image;
        TImage *col_bitmap;
        TButton *Button1;
	TButton *btn_history;
	TTabControl *graph_tab;
	TChart *stats_graph;
        void __fastcall FormActivate(TObject *Sender);
        void __fastcall okClick(TObject *Sender);
        void __fastcall Button1Click(TObject *Sender);
	void __fastcall btn_historyClick(TObject *Sender);
        void __fastcall graph_tabChange(TObject *Sender);
private:	// User declarations
public:		// User declarations
		crush_player_ptr player;
        __fastcall Tplayer_profile(TComponent* Owner);
};
//---------------------------------------------------------------------------
extern PACKAGE Tplayer_profile *player_profile;
//---------------------------------------------------------------------------
#endif
