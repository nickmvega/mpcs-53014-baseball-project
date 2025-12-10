package org.example;

import edu.uchicago.mpcs53013.nvega.baseball.BaseballPlay;
import java.io.IOException;

public abstract class BaseballPlayProcessor extends BaseProcessor<BaseballPlay> {

    @Override
    protected BaseballPlay parse(String line) {
        String[] raw = line.split(",", -1);
        BaseballPlay play = new BaseballPlay();

        play.setGid(get(raw, "gid"));

        play.setInning(toInt(get(raw, "inning")));
        play.setTopBot(toInt(get(raw, "top_bot")));
        play.setPlayNumber(toInt(get(raw, "pn")));

        play.setScoreV(toInt(get(raw, "score_v")));
        play.setScoreH(toInt(get(raw, "score_h")));

        play.setBatter(get(raw, "batter"));
        play.setBatHand(get(raw, "bathand"));
        play.setLineupPos(toInt(get(raw, "lp")));

        play.setPitcher(get(raw, "pitcher"));
        play.setPitHand(get(raw, "pithand"));

        play.setCount(get(raw, "count"));
        play.setPitches(get(raw, "pitches"));

        play.setOutsPre(toInt(get(raw, "outs_pre")));

        play.setRunner1Pre(toBool(get(raw, "br1_pre")));
        play.setRunner2Pre(toBool(get(raw, "br2_pre")));
        play.setRunner3Pre(toBool(get(raw, "br3_pre")));

        play.setAb(toInt(get(raw, "ab")));
        play.setSingle(toInt(get(raw, "single")));
        play.setDoubleHit(toInt(get(raw, "double")));
        play.setTripleHit(toInt(get(raw, "triple")));
        play.setHrHit(toInt(get(raw, "hr")));
        play.setWalk(toInt(get(raw, "walk")));
        play.setStrikeout(toInt(get(raw, "k")));

        play.setRbi(toInt(get(raw, "rbi")));
        play.setRuns(toInt(get(raw, "runs")));

        play.setOutsPost(toInt(get(raw, "outs_post")));

        play.setRunner1Post(toBool(get(raw, "br1_post")));
        play.setRunner2Post(toBool(get(raw, "br2_post")));
        play.setRunner3Post(toBool(get(raw, "br3_post")));

        play.setHitType(get(raw, "hittype"));
        play.setLocation(get(raw, "loc"));
        play.setGameType(get(raw, "gametype"));

        return play;
    }
}
