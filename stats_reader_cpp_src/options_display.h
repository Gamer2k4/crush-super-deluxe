//---------------------------------------------------------------------------

#ifndef options_displayH
#define options_displayH
//---------------------------------------------------------------------------
#include <Classes.hpp>
#include <Controls.hpp>
#include <StdCtrls.hpp>
#include <Forms.hpp>
//---------------------------------------------------------------------------
class Tfrm_stats_options : public TForm
{
__published:	// IDE-managed Components
        TGroupBox *GroupBox1;
        TGroupBox *GroupBox2;
        TButton *Button1;
        TCheckBox *cb_show_wins_losses_ties;
        TCheckBox *cb_show_player_value;
        void __fastcall Button1Click(TObject *Sender);
private:	// User declarations
public:		// User declarations
        __fastcall Tfrm_stats_options(TComponent* Owner);
        bool m_show_wins_losses_ties;
        bool m_show_player_value;
};
//---------------------------------------------------------------------------
extern PACKAGE Tfrm_stats_options *frm_stats_options;
//---------------------------------------------------------------------------
#endif
