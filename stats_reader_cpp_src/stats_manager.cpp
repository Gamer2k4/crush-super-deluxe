//---------------------------------------------------------------------------

#include <vcl.h>
#pragma hdrstop
USERES("stats_manager.res");
USEFORM("stats_display.cpp", frm_stats_manager);
USEUNIT("crush_stats.cpp");
USEUNIT("smart_pointer.cpp");
USEUNIT("ref_count.cpp");
USEUNIT("crush_player.cpp");
USEUNIT("crush_team.cpp");
USEFORM("player_profile_display.cpp", player_profile);
USEFORM("options_display.cpp", frm_stats_options);
//---------------------------------------------------------------------------
WINAPI WinMain(HINSTANCE, HINSTANCE, LPSTR, int)
{
        try
        {
                 Application->Initialize();
                 Application->CreateForm(__classid(Tfrm_stats_manager), &frm_stats_manager);
                 Application->CreateForm(__classid(Tplayer_profile), &player_profile);
                 Application->CreateForm(__classid(Tfrm_stats_options), &frm_stats_options);
                 Application->Run();
        }
        catch (Exception &exception)
        {
                 Application->ShowException(&exception);
        }
        return 0;
}
//---------------------------------------------------------------------------
