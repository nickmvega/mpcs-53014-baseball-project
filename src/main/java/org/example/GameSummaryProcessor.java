package org.example;

import edu.uchicago.mpcs53013.nvega.baseball.GameSummary;
import java.io.IOException;

public abstract class GameSummaryProcessor extends BaseProcessor<GameSummary> {

    @Override
    protected GameSummary parse(String line) {
        String[] raw = line.split(",", -1);

        GameSummary gs = new GameSummary();

        gs.setGid(get(raw, "gid"));

        String date = get(raw, "date").trim();
        gs.setGameDate(date.isEmpty() ? "00000000" : date);

        gs.setAwayTeam(get(raw, "visteam"));
        gs.setHomeTeam(get(raw, "hometeam"));

        String innings = get(raw, "innings").replaceAll("[^0-9]", "");
        gs.setInnings((short) toInt(innings));

        gs.setGameType(get(raw, "gametype"));

        String awayRuns = get(raw, "vruns").replaceAll("[^0-9]", "");
        gs.setAwayRuns((short) toInt(awayRuns));

        String homeRuns = get(raw, "hruns").replaceAll("[^0-9]", "");
        gs.setHomeRuns((short) toInt(homeRuns));

        gs.setWinningTeam(get(raw, "wteam"));
        gs.setLosingTeam(get(raw, "lteam"));

        return gs;
    }
}
