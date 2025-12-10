CREATE OR REPLACE VIEW nvega_game_summary AS
SELECT
    gid,
    gameinfo_date as game_date,
    hometeam as home_team,
    visteam as away_team,
    innings,
    gametype,
    vruns as away_runs,
    hruns as home_runs,
    wteam as winning_team,
    lteam as losing_team
FROM nvega_gameinfo;

CREATE OR REPLACE VIEW nvega_plays_by_inning AS
SELECT
    gid,
    inning,
    top_bot,
    pn as play_number,
    score_v,
    score_h,

    batter,
    bathand,
    lp as lineup_pos,

    pitcher,
    pithand,

    count,
    pitches,

    outs_pre,
    br1_pre,
    br2_pre,
    br3_pre,

    ab,
    single,
    double,
    triple,
    hr,
    walk,
    k,
    rbi,
    runs,

    outs_post,
    br1_post,
    br2_post,
    br3_post,

    hittype,
    loc,
    gametype
FROM nvega_plays
ORDER BY gid, inning, play_number;

CREATE OR REPLACE VIEW nvega_player_summary AS 
SELECT 
    id, 
    last, 
    first,
    bat AS bat_side, 
    throw as throw_side,
    season
FROM nvega_allplayers;