package org.example;

import edu.uchicago.mpcs53013.nvega.baseball.PlayerPitching;

public abstract class PlayerPitchingProcessor extends BaseProcessor<PlayerPitching> {

    @Override
    protected PlayerPitching parse(String line) {
        String[] raw = line.split(",", -1);

        PlayerPitching pp = new PlayerPitching();

        pp.setGid(get(raw, "gid"));
        pp.setPlayerId(get(raw, "id"));
        pp.setTeam(get(raw, "team"));

        pp.setOutsRecorded(toInt(get(raw, "p_out")));
        pp.setBattersFaced(toInt(get(raw, "p_bf")));
        pp.setHitsAllowed(toInt(get(raw, "p_h")));
        pp.setRunsAllowed(toInt(get(raw, "p_r")));
        pp.setEarnedRunsAllowed(toInt(get(raw, "p_er")));
        pp.setWalksAllowed(toInt(get(raw, "p_w")));
        pp.setStrikeouts(toInt(get(raw, "p_k")));
        pp.setHomeRunsAllowed(toInt(get(raw, "p_hr")));
        pp.setWildPitches(toInt(get(raw, "p_wp")));

        pp.setWin(toBool(get(raw, "win")));
        pp.setLoss(toBool(get(raw, "loss")));
        pp.setSave(toBool(get(raw, "save")));

        pp.setDate(get(raw, "date"));

        return pp;
    }
}