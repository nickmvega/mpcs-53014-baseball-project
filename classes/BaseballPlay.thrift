namespace java edu.uchicago.mpcs53013.nvega.baseball

struct BaseballPlay {
  1:  required string gid;
  2:  required i32 inning;
  3:  required i32 topBot;
  4:  required i32 playNumber;

  5:  required i32 scoreV;
  6:  required i32 scoreH;

  7:  required string batter;
  8:  required string batHand;
  9:  required i32 lineupPos;

  10: required string pitcher;
  11: required string pitHand;

  12: required string count;
  13: required string pitches;

  14: required i32 outsPre;
  15: required bool runner1Pre;
  16: required bool runner2Pre;
  17: required bool runner3Pre;

  18: required i32 ab;
  19: required i32 single;
  20: required i32 doubleHit;
  21: required i32 tripleHit;
  22: required i32 hrHit;
  23: required i32 walk;
  24: required i32 strikeout;

  25: required i32 rbi;
  26: required i32 runs;

  27: required i32 outsPost;
  28: required bool runner1Post;
  29: required bool runner2Post;
  30: required bool runner3Post;

  31: required string hitType;
  32: required string location;
  33: required string gameType;
}
