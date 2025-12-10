ADD JAR /home/hadoop/nvega/jars/hdfs-ingest-baseball-archetype-1.0-SNAPSHOT.jar;

DROP TABLE IF EXISTS nvega_play_by_play_for_game;

CREATE TABLE nvega_play_by_play_for_game (
  gameId string,
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
STORED AS ORC;

INSERT OVERWRITE TABLE nvega_play_by_play_for_game
SELECT
    p.gid,
    g.gamedate,
    g.hometeam,
    g.awayteam,
    g.gametype,
    p.inning,
    p.playnumber,
    p.batter,
    pl.firstname,
    pl.lastname,
    pl.teamid,
    pl.bathand,
    p.pitcher,
    pp.firstname,
    pp.lastname,
    pp.teamid,
    pp.throwhand,
    p.single,
    p.doublehit,
    p.triplehit,
    p.hrhit,
    p.walk,
    p.strikeout,
    p.rbi,
    p.runs,
    p.outspost
FROM nvega_plays p
JOIN nvega_gameinfo g ON p.gid = g.gid
LEFT JOIN nvega_allplayers pl ON p.batter = pl.playerid
LEFT JOIN nvega_allplayers pp ON p.pitcher = pp.playerid
WHERE g.gamedate < '20250101';

DROP TABLE IF EXISTS nvega_game_summary;

CREATE TABLE nvega_game_summary (
  gameId string,
  gameDate string,
  homeTeam string,
  awayTeam string,
  gameType string,
  winningTeam string,
  homeRuns smallint,
  awayRuns smallint
)
STORED AS ORC;

INSERT OVERWRITE TABLE nvega_game_summary
SELECT
    g.gid,
    g.gamedate,
    g.hometeam,
    g.awayteam,
    g.gametype,
    g.winningteam,
    g.homeruns,
    g.awayruns
FROM nvega_gameinfo g
WHERE g.gamedate < '20250101';

DROP TABLE IF EXISTS nvega_game_batting_player_stats;

CREATE TABLE nvega_game_batting_player_stats (
  gameId string,
  gameDate string,
  playerId string,
  firstName string,
  lastName string,
  teamId string,
  plateAppearances int,
  walks int,
  strikeouts int,
  singles int,
  doubles int,
  triples int,
  homeRuns int,
  RBIs int,
  hits int
)
STORED AS ORC;

INSERT OVERWRITE TABLE nvega_game_batting_player_stats
SELECT
    gp.gameId,
    gp.gameDate,
    gp.batterId,
    gp.batterFirstName,
    gp.batterLastName,
    gp.batterTeam,
    COUNT(*),
    SUM(gp.walk),
    SUM(gp.strikeout),
    SUM(gp.single),
    SUM(gp.doublehit),
    SUM(gp.triplehit),
    SUM(gp.hrhit),
    SUM(gp.rbiProduced),
    SUM(gp.single + gp.doublehit + gp.triplehit + gp.hrhit)
FROM nvega_play_by_play_for_game gp
GROUP BY gp.gameId, gp.gameDate, gp.batterId, gp.batterFirstName, gp.batterLastName, gp.batterTeam;


DROP TABLE IF EXISTS nvega_game_pitching_player_stats;

CREATE TABLE nvega_game_pitching_player_stats (
  gameId string,
  gameDate string,
  playerId string,
  firstName string,
  lastName string,
  teamId string,
  battersFaced int,
  walksAllowed int,
  strikeouts int,
  homeRunsAllowed int,
  hitsAllowed int,
  runsAllowed int,
  outsRecorded int
)
STORED AS ORC;

INSERT OVERWRITE TABLE nvega_game_pitching_player_stats
SELECT
    gp.gameId,
    gp.gameDate,
    gp.pitcherId,
    gp.pitcherFirstName,
    gp.pitcherLastName,
    gp.pitcherTeam,
    COUNT(*),
    SUM(gp.walk),
    SUM(gp.strikeout),
    SUM(gp.hrhit),
    SUM(gp.single + gp.doublehit + gp.triplehit + gp.hrhit),
    SUM(gp.runsScored),
    SUM(gp.outsOnPlay)
FROM nvega_play_by_play_for_game gp
GROUP BY gp.gameId, gp.gameDate, gp.pitcherId, gp.pitcherFirstName, gp.pitcherLastName, gp.pitcherTeam;