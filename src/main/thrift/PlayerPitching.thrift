namespace java edu.uchicago.mpcs53013.nvega.baseball

struct PlayerPitching {
  1: string gid,
  2: string playerId,
  3: string team,
  4: i32 outsRecorded,
  5: i32 battersFaced,
  6: i32 hitsAllowed,
  7: i32 runsAllowed,
  8: i32 earnedRunsAllowed,
  9: i32 walksAllowed,
  10: i32 strikeouts,
  11: i32 homeRunsAllowed,
  12: i32 wildPitches,
  13: bool win,
  14: bool loss,
  15: bool save,
  16: string date
}
