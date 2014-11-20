//----------------------------------------------------------------------
// stats reader
//----------------------------------------------------------------------

#ifndef STATS_READER
#define STATS_READER

enum crush_color
{
	crush_red = 0,
	crush_light_green = 5,
	crush_blue = 9,
	crush_pink = 14,
	crush_light_blue = 18,
	crush_beige = 26,
	crush_orange = 1,
	crush_aqua = 7,
	crush_purple = 11,
	crush_red_brown = 17,
	crush_dark_brown = 29,
	crush_light_brown = 28,
	crush_gold = 2,
	crush_light_purple = 12,
	crush_white = 21,
	crush_yellow = 3,
	crush_green= 13,
	crush_black = 19
};

enum race_type
{
        human = 0x0,
        gronk = 0x1,
        frog = 0x2,
        dragoran = 0x3,
        bug = 0x4,
        slith = 0x5,
        kurgan = 0x6,
        bot = 0x7
};

#define PLAYER_NAME_LEN 8
#define TEAM_NAME_LEN 15

struct player
{
	short total_value;
	short points_to_spend;

	char blah1 [3];

	race_type race;

	char blah2 [4];

	char player_stats [9];

	char blah3 [5];

	char name [PLAYER_NAME_LEN];

	char blah4 [28];

	short blah5;
	short blah6;
	short blah7;
	short blah8;
	short blah9;
	short blah10;

	short rushing_tiles;
	short goals_scored;
	short kills_for;
	short injuries_for;

	short blah11     ;

	short checks_thrown    ;
	short checks_landed;
	short blah12   ;
	short blah13  ;
	short rushing_attempts;
	short blah14;

	short sacks_for;

	short total_rushing_tiles;
	short total_goals_scored;
	short total_kills_for;
	short total_injuries_for;
	short blah15;
	short total_checks_thrown;
	short total_checks_landed;
	short blah16 ;
	short blah17;
	short total_rushing_attempts;
	short blah18;
	short total_sacks_for;

	char blah19[42];
	char blah_footer [8];
};

struct team
{
	char jersey_color;
	char trim_color;

	char thing1 [23];

	char name [TEAM_NAME_LEN];
	char coach [TEAM_NAME_LEN];

	char thing2 [12];
	char thing3 [16];
	char thing4;
	char thing5 [28];
	short thing6;

	short rushing_tiles ;
	short kills_for ;
	short kills_against ;
	short injuries_for ;
	short injuries_against ;
	short checks_thrown ;
	short checks_landed ;
	short activated_pads ;

	short thing7 ;

	short balls_fumbled ;
	short rushing_attempts ;
	short ball_control ;
	short sacks_against ;
	short sacks_for ;

	short thing8;
	short thing9;

	short wins ;
	short losses ;
	short ties ;

	short total_rushing_tiles;
	short total_kills_for;
	short total_kills_against ;
	short total_injuries_for ;
	short total_injuries_against ;
	short total_checks_thrown;
	short total_checks_landed;
	short total_activated_pads;

	short thing10;

	short total_balls_fumbled;
	short total_rushing_attempts;
	short total_ball_control;
	short total_sacks_against;
	short total_sacks_for;

	short thing11;
	short thing12;

	short total_wins;
	short total_losses;
	short total_ties;

	char thing_footer [58];
};

#define PLAYER_COUNT 35
#define TEAM_COUNT 12

struct team_container
{
	team    * team_info;
	player  player_array [PLAYER_COUNT];
};

#endif
