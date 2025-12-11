package org.example;

public class KafkaBaseballRecord {

    public String rowKey;
    public String gameDate;
    public String homeTeam;
    public String awayTeam;
    public int inning;
    public int playSeq;
    public String batterId;
    public String batterFirstName;
    public String batterLastName;
    public String batterTeam;
    public String batterHand;
    public String pitcherId;
    public String pitcherFirstName;
    public String pitcherLastName;
    public String pitcherTeam;
    public String pitcherHand;
    public int singleHit;
    public int doubleHit;
    public int tripleHit;
    public int hrHit;
    public int walk;
    public int strikeout;
    public int rbiProduced;
    public int runsScored;
    public int outsOnPlay;

    public KafkaBaseballRecord(
            String rk, String gd, String ht, String at, int inn, int ps,
            String bid, String bfn, String bln, String bt, String bh,
            String pid, String pfn, String pln, String pt, String ph,
            int sh, int dh, int th, int hr, int wal, int so, int rbi, int runs, int outs) {

        rowKey = rk;
        gameDate = gd;
        homeTeam = ht;
        awayTeam = at;
        inning = inn;
        playSeq = ps;
        batterId = bid;
        batterFirstName = bfn;
        batterLastName = bln;
        batterTeam = bt;
        batterHand = bh;
        pitcherId = pid;
        pitcherFirstName = pfn;
        pitcherLastName = pln;
        pitcherTeam = pt;
        pitcherHand = ph;
        singleHit = sh;
        doubleHit = dh;
        tripleHit = th;
        hrHit = hr;
        walk = wal;
        strikeout = so;
        rbiProduced = rbi;
        runsScored = runs;
        outsOnPlay = outs;
    }
}
