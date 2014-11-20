//---------------------------------------------------------------------------
// player characteristics
//---------------------------------------------------------------------------

#include "smart_pointer.h"

#include "stats_reader.h"

#include <fstream>
using namespace std;

#include <string>

class player_characteristics : public reference_count
{
public:
        player_characteristics (const player & new_player)
        {
			m_ap = new_player.player_stats [1];
			m_reflexes = new_player.player_stats [2];
			m_jump = new_player.player_stats [3];
			m_checking = new_player.player_stats [4];
			m_strength = new_player.player_stats [5];
			m_toughness = new_player.player_stats [6];
			m_hands = new_player.player_stats [7];
			m_dodge = new_player.player_stats [8];
        }

        ~player_characteristics () {}

        bool write (ofstream & file_stream)
        {
        }
private:
        byte m_ap;
        byte m_checking;
        byte m_strength;
        byte m_toughness;
        byte m_reflexes;
        byte m_jump;
        byte m_hands;
        byte m_dodge;
};

typedef smart_pointer<player_characteristics> player_characteristics_ptr;
