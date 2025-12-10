namespace java edu.uchicago.mpcs53013.nvega.baseball

struct GameSummary {
  1: required string gid;
  2: required string gameDate;
  3: required string homeTeam;
  4: required string awayTeam;
  5: required i16 innings;
  6: required string gameType;
  7: required i16 awayRuns;
  8: required i16 homeRuns;
  9: required string winningTeam;
  10: required string losingTeam;
}