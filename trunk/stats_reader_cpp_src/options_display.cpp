//---------------------------------------------------------------------------

#include <vcl.h>
#pragma hdrstop

#include "options_display.h"
//---------------------------------------------------------------------------
#pragma package(smart_init)
#pragma resource "*.dfm"
Tfrm_stats_options *frm_stats_options;
//---------------------------------------------------------------------------
__fastcall Tfrm_stats_options::Tfrm_stats_options(TComponent* Owner)
        : TForm(Owner)
{
        m_show_wins_losses_ties = true;
        m_show_player_value = true;
}
//---------------------------------------------------------------------------
void __fastcall Tfrm_stats_options::Button1Click(TObject *Sender)
{
        m_show_wins_losses_ties = cb_show_wins_losses_ties->Checked;
        m_show_player_value = cb_show_player_value->Checked;
        Close ();        
}
//---------------------------------------------------------------------------
