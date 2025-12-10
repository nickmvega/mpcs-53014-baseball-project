-- CREATE EXTERNAL TABLE hb_game_summary (
--   rowkey string,
--   gameDate string,
--   homeTeam string,
--   awayTeam string,
--   gameType string,
--   winningTeam string,
--   homeRuns smallint,
--   awayRuns smallint
-- )
-- STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
-- WITH SERDEPROPERTIES (
--   'hbase.columns.mapping' = ':key,g:gameDate,g:homeTeam,g:awayTeam,g:gameType,g:winningTeam,g:homeRuns,g:awayRuns'
-- )
-- TBLPROPERTIES ('hbase.table.name' = 'nvega_hb_game_summary');

INSERT OVERWRITE TABLE hb_game_summary
SELECT
  gameId,
  gameDate,
  homeTeam,
  awayTeam,
  gameType,
  winningTeam,
  homeRuns,
  awayRuns
FROM nvega_game_summary;

-- CREATE EXTERNAL TABLE hb_game_batting_stats (
--   rowkey string,
--   gameDate string,
--   playerId string,
--   firstName string,
--   lastName string,
--   teamId string,
--   plateAppearances int,
--   walks int,
--   strikeouts int,
--   singles int,
--   doubles int,
--   triples int,
--   homeRuns int,
--   RBIs int,
--   hits int
-- )
-- STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
-- WITH SERDEPROPERTIES (
--   'hbase.columns.mapping' = ':key,b:gameDate,b:playerId,b:firstName,b:lastName,b:teamId,b:plateAppearances,b:walks,b:strikeouts,b:singles,b:doubles,b:triples,b:homeRuns,b:RBIs,b:hits'
-- )
-- TBLPROPERTIES ('hbase.table.name' = 'nvega_hb_game_batting_stats');

INSERT OVERWRITE TABLE hb_game_batting_stats
SELECT
  concat(gameId,'|',playerId),
  gameDate,
  playerId,
  firstName,
  lastName,
  teamId,
  plateAppearances,
  walks,
  strikeouts,
  singles,
  doubles,
  triples,
  homeRuns,
  RBIs,
  hits
FROM nvega_game_batting_player_stats;

-- CREATE EXTERNAL TABLE hb_game_pitching_stats (
--   rowkey string,
--   gameDate string,
--   playerId string,
--   firstName string,
--   lastName string,
--   teamId string,
--   battersFaced int,
--   walksAllowed int,
--   strikeouts int,
--   homeRunsAllowed int,
--   hitsAllowed int,
--   runsAllowed int,
--   outsRecorded int
-- )
-- STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
-- WITH SERDEPROPERTIES (
--   'hbase.columns.mapping' = ':key,p:gameDate,p:playerId,p:firstName,p:lastName,p:teamId,p:battersFaced,p:walksAllowed,p:strikeouts,p:homeRunsAllowed,p:hitsAllowed,p:runsAllowed,p:outsRecorded'
-- )
-- TBLPROPERTIES ('hbase.table.name' = 'nvega_hb_game_pitching_stats');

INSERT OVERWRITE TABLE hb_game_pitching_stats
SELECT
  concat(gameId,'|',playerId),
  gameDate,
  playerId,
  firstName,
  lastName,
  teamId,
  battersFaced,
  walksAllowed,
  strikeouts,
  homeRunsAllowed,
  hitsAllowed,
  runsAllowed,
  outsRecorded
FROM nvega_game_pitching_player_stats;

CREATE EXTERNAL TABLE hb_play_by_play_v2 (
  rowkey string,
  gameDate string,
  homeTeam string,
  awayTeam string,
  gameType string,
  inning int,
  playSeq int,
  batterId string,
  batterFirstName string,
  batterLastName string,
  batterTeam string,
  batterHand string,
  pitcherId string,
  pitcherFirstName string,
  pitcherLastName string,
  pitcherTeam string,
  pitcherHand string,
  single int,
  doublehit int,
  triplehit int,
  hrhit int,
  walk int,
  strikeout int,
  rbiProduced int,
  runsScored int,
  outsOnPlay int
)
STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
WITH SERDEPROPERTIES (
  'hbase.columns.mapping' = ':key,g:gameDate,g:homeTeam,g:awayTeam,g:gameType,g:inning,g:playSeq,b:batterId,b:batterFirstName,b:batterLastName,b:batterTeam,b:batterHand,p:pitcherId,p:pitcherFirstName,p:pitcherLastName,p:pitcherTeam,p:pitcherHand,s:single,s:doublehit,s:triplehit,s:hrhit,s:walk,s:strikeout,s:rbiProduced,s:runsScored,s:outsOnPlay'
)
TBLPROPERTIES ('hbase.table.name'='nvega_hb_play_by_play_v2');

INSERT INTO TABLE hb_play_by_play_v2
SELECT
  concat(gameId,'|',inning,'|',playSeq),
  gameDate,
  homeTeam,
  awayTeam,
  gameType,
  inning,
  playSeq,
  batterId,
  batterFirstName,
  batterLastName,
  batterTeam,
  batterHand,
  pitcherId,
  pitcherFirstName,
  pitcherLastName,
  pitcherTeam,
  pitcherHand,
  single,
  doublehit,
  triplehit,
  hrhit,
  walk,
  strikeout,
  rbiProduced,
  runsScored,
  outsOnPlay
FROM nvega_play_by_play_for_game
WHERE gameDate BETWEEN '20200101' AND '20250101';
