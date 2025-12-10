package org.example;

import edu.uchicago.mpcs53013.nvega.baseball.PlayerBatting;

public abstract class PlayerBattingProcessor extends BaseProcessor<PlayerBatting> {

    @Override
    protected PlayerBatting parse(String line) {
        String[] raw = line.split(",", -1);

        PlayerBatting pb = new PlayerBatting();

        pb.setGid(get(raw, "gid"));
        pb.setPlayerId(get(raw, "id"));
        pb.setTeam(get(raw, "team"));

        pb.setLineupOrder(get(raw, "b_lp"));

        pb.setPlateAppearances(toInt(get(raw, "b_pa")));
        pb.setAtBats(toInt(get(raw, "b_ab")));
        pb.setRuns(toInt(get(raw, "b_r")));
        pb.setHits(toInt(get(raw, "b_h")));
        pb.setDoubles(toInt(get(raw, "b_d")));
        pb.setTriples(toInt(get(raw, "b_t")));
        pb.setHomeRuns(toInt(get(raw, "b_hr")));
        pb.setRbi(toInt(get(raw, "b_rbi")));
        pb.setWalks(toInt(get(raw, "b_w")));
        pb.setStrikeouts(toInt(get(raw, "b_k")));
        pb.setStolenBases(toInt(get(raw, "b_sb")));
        pb.setCaughtStealing(toInt(get(raw, "b_cs")));

        pb.setDate(get(raw, "date"));

        pb.setWin(toBool(get(raw, "win")));

        return pb;
    }
}
