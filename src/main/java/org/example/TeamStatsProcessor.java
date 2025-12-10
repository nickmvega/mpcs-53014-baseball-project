package org.example;

import edu.uchicago.mpcs53013.nvega.baseball.TeamStats;

public abstract class TeamStatsProcessor extends BaseProcessor<TeamStats> {

    @Override
    protected TeamStats parse(String line) {
        String[] raw = line.split(",", -1);

        TeamStats ts = new TeamStats();

        ts.setGid(get(raw, "gid"));
        ts.setTeam(get(raw, "team"));

        // sum inning runs inn1..inn28
        int totalRuns = 0;
        for (int i = 1; i <= 28; i++) {
            totalRuns += toInt(get(raw, "inn" + i));
        }
        ts.setRunsTotal(totalRuns);

        ts.setLob(toInt(get(raw, "lob")));

        ts.setHits(toInt(get(raw, "b_h")));
        ts.setDoubles(toInt(get(raw, "b_d")));
        ts.setTriples(toInt(get(raw, "b_t")));
        ts.setHomeRuns(toInt(get(raw, "b_hr")));
        ts.setAtBats(toInt(get(raw, "b_ab")));
        ts.setPlateAppearances(toInt(get(raw, "b_pa")));
        ts.setWalks(toInt(get(raw, "b_w")));
        ts.setStrikeouts(toInt(get(raw, "b_k")));
        ts.setStolenBases(toInt(get(raw, "b_sb")));
        ts.setCaughtStealing(toInt(get(raw, "b_cs")));

        ts.setPitcherOutsRecorded(toInt(get(raw, "p_ipouts")));
        ts.setRunsAllowed(toInt(get(raw, "p_r")));
        ts.setEarnedRunsAllowed(toInt(get(raw, "p_er")));

        ts.setFieldingErrors(toInt(get(raw, "d_e")));

        ts.setDate(get(raw, "date"));
        ts.setOpponent(get(raw, "opp"));
        ts.setWin(toBool(get(raw, "win")));

        return ts;
    }
}
