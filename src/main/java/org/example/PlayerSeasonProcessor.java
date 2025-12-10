package org.example;

import edu.uchicago.mpcs53013.nvega.baseball.PlayerSeason;

public abstract class PlayerSeasonProcessor extends BaseProcessor<PlayerSeason> {

    @Override
    protected PlayerSeason parse(String line) {
        String[] raw = line.split(",", -1);

        PlayerSeason ps = new PlayerSeason();

        ps.setPlayerId(get(raw, "id"));
        ps.setLastName(get(raw, "last"));
        ps.setFirstName(get(raw, "first"));
        ps.setBatHand(get(raw, "bat"));
        ps.setThrowHand(get(raw, "throw"));
        ps.setTeamId(get(raw, "team"));

        ps.setGames(toInt(get(raw, "g")));
        ps.setGamesPitcher(toInt(get(raw, "g_p")));
        ps.setGamesStarterPitcher(toInt(get(raw, "g_sp")));
        ps.setGamesReliefPitcher(toInt(get(raw, "g_rp")));
        ps.setGamesCatcher(toInt(get(raw, "g_c")));
        ps.setGamesFirstBase(toInt(get(raw, "g_1b")));
        ps.setGamesSecondBase(toInt(get(raw, "g_2b")));
        ps.setGamesThirdBase(toInt(get(raw, "g_3b")));
        ps.setGamesShortstop(toInt(get(raw, "g_ss")));
        ps.setGamesLeftField(toInt(get(raw, "g_lf")));
        ps.setGamesCenterField(toInt(get(raw, "g_cf")));
        ps.setGamesRightField(toInt(get(raw, "g_rf")));

        ps.setGamesDesignatedHitter(toInt(get(raw, "g_dh")));
        ps.setGamesPinchHitter(toInt(get(raw, "g_ph")));
        ps.setGamesPinchRunner(toInt(get(raw, "g_pr")));

        ps.setFirstGame(get(raw, "first_g"));
        ps.setLastGame(get(raw, "last_g"));

        ps.setSeason(toInt(get(raw, "season")));

        return ps;
    }
}
