'use strict';
const http = require('http');
var assert = require('assert');
const express= require('express');
const app = express();
const mustache = require('mustache');
const fs = require('fs');
require('dotenv').config()
const port = Number(process.argv[2]);
const hbase = require('hbase')

const url = new URL(process.argv[3]);
console.log(url)
var hclient = hbase({
    host: url.hostname,
    port: url.port,
    protocol: url.protocol.slice(0, -1),
    path: "/",
    timeout: 5000,
    encoding: 'utf8'
});

function cellValue(cell) {
    return cell['$'] ? cell['$'].toString() : "";
}

function groupCellsByRow(cells) {
    const grouped = {};
    (cells || []).forEach(c => {
        const rk = c.key.toString();
        if (!grouped[rk]) grouped[rk] = [];
        grouped[rk].push(c);
    });
    return grouped;
}

function cellsToMap(cells) {
    const m = {};
    (cells || []).forEach(c => {
        m[c.column] = cellValue(c);
    });
    return m;
}

function num(v) {
    if (v === undefined || v === null || v === "") return 0;
    const n = Number(v);
    return isNaN(n) ? 0 : n;
}

function scanPrefix(table, prefix, callback) {
    hclient.table(table).scan(
        {
            filter: { type: "PrefixFilter", value: prefix },
            maxVersions: 1
        },
        callback
    );
}

function buildResult(map, statPrefix) {
    const parts = [];

    if (num(map[`${statPrefix}:single`]) === 1) parts.push("Single");
    if (num(map[`${statPrefix}:doublehit`]) === 1) parts.push("Double");
    if (num(map[`${statPrefix}:triplehit`]) === 1) parts.push("Triple");
    if (num(map[`${statPrefix}:hrhit`]) === 1) parts.push("Home run");
    if (num(map[`${statPrefix}:walk`]) === 1) parts.push("Walk");
    if (num(map[`${statPrefix}:strikeout`]) === 1) parts.push("Strikeout");

    const outsOnPlay = num(map[`${statPrefix}:outsOnPlay`]);
    const runsScored = num(map[`${statPrefix}:runsScored`]);
    const rbiProduced = num(map[`${statPrefix}:rbiProduced`]);

    if (outsOnPlay > 0) parts.push(`${outsOnPlay} out(s) on play`);
    if (runsScored > 0) parts.push(`${runsScored} run(s) scored`);
    if (rbiProduced > 0) parts.push(`${rbiProduced} RBI`);

    if (parts.length === 0) return "Unknown result";
    return parts.join(", ");
}

app.use(express.static('public'));

app.get('/', function(req, res) {
    res.sendFile(__dirname + '/public/index.html');
});

app.get('/games.html', (req, res) => {
    let raw = req.query['date'];
    if (!raw) return res.send("No date selected.");

    let date = raw.replace(/[-\/]/g, '');
    if (date.length !== 8) {
        return res.send("Date must be in YYYYMMDD or MM/DD/YYYY or YYYY-MM-DD format.");
    }

    hclient.table('nvega_hb_game_summary').scan(
        { maxVersions: 1 },
        (err, cells) => {
            if (err) {
                console.error("HBase SCAN ERROR:", err);
                return res.send("HBase SCAN ERROR: " + JSON.stringify(err));
            }

            const byRow = groupCellsByRow(cells);
            const games = [];

            Object.keys(byRow).forEach(rk => {
                const m = cellsToMap(byRow[rk]);
                if (m['g:gameDate'] === date) {
                    games.push({
                        gid: rk,
                        away: m['g:awayTeam'],
                        home: m['g:homeTeam'],
                        awayRuns: m['g:awayRuns'],
                        homeRuns: m['g:homeRuns']
                    });
                }
            });

            if (games.length === 0) {
                return res.send(`
                    No games exist historically for ${raw}.<br/>
                    If this date is in the future relative to your streaming layer,<br/>
                    live data may not have arrived yet.
                `);
            }

            const template = fs.readFileSync('games.mustache').toString();
            const html = mustache.render(template, { date: raw, games: games });
            res.send(html);
        }
    );
});

app.get('/game.html', (req, res) => {
    const gid = req.query['gid'];
    if (!gid) return res.send("No game selected.");

    console.log("Looking up game:", gid);

    hclient.table('nvega_hb_game_summary').row(gid).get((err, cells) => {
        if (err || !cells || cells.length === 0) {
            console.error(err);
            return res.send("Game not found in historical dataset.");
        }

        const summary = cellsToMap(cells);
        const homeTeam = summary['g:homeTeam'];
        const awayTeam = summary['g:awayTeam'];

        scanPrefix('nvega_latest_baseball_play', gid, (err2, liveCells) => {
            let homeLiveRuns = 0;
            let awayLiveRuns = 0;

            if (!err2 && liveCells && liveCells.length > 0) {
                const liveByRow = groupCellsByRow(liveCells);
                Object.keys(liveByRow).forEach(rk => {
                    const m = cellsToMap(liveByRow[rk]);
                    const runs = num(m['r:runsScored']);
                    if (runs > 0) {
                        const batterTeam =
                            m['p:batterTeam'] ||
                            m['b:batterTeam'] || "";

                        if (batterTeam === homeTeam) homeLiveRuns += runs;
                        else if (batterTeam === awayTeam) awayLiveRuns += runs;
                    }
                });
            }

            const totalHomeRuns = num(summary['g:homeRuns']) + homeLiveRuns;
            const totalAwayRuns = num(summary['g:awayRuns']) + awayLiveRuns;

            const template = fs.readFileSync('game.mustache').toString();
            const html = mustache.render(template, {
                gid: gid,
                date: summary['g:gameDate'],
                home_team: homeTeam,
                away_team: awayTeam,
                home_runs: totalHomeRuns,
                away_runs: totalAwayRuns
            });

            res.send(html);
        });
    });
});

app.get('/plays.html', (req, res) => {
    const gid = req.query['gid'];
    if (!gid) return res.send("No game selected.");

    const plays = [];

    scanPrefix('nvega_hb_play_by_play_v2', gid, (err1, histCells) => {
        if (!err1 && histCells && histCells.length > 0) {
            const histByRow = groupCellsByRow(histCells);
            Object.keys(histByRow).forEach(rk => {
                const m = cellsToMap(histByRow[rk]);
                plays.push({
                    inning: m['g:inning'],
                    batter: ((m['b:batterFirstName'] || "") + " " + (m['b:batterLastName'] || "")).trim(),
                    pitcher: ((m['p:pitcherFirstName'] || "") + " " + (m['p:pitcherLastName'] || "")).trim(),
                    result: buildResult(m, 's'),
                    source: "Historical"
                });
            });
        }

        scanPrefix('nvega_latest_baseball_play', gid, (err2, liveCells) => {
            if (!err2 && liveCells && liveCells.length > 0) {
                const liveByRow = groupCellsByRow(liveCells);
                Object.keys(liveByRow).forEach(rk => {
                    const m = cellsToMap(liveByRow[rk]);
                    plays.push({
                        inning: m['g:inning'],
                        batter: ((m['p:batterFirstName'] || "") + " " + (m['p:batterLastName'] || "")).trim(),
                        pitcher: ((m['b:pitcherFirstName'] || "") + " " + (m['b:pitcherLastName'] || "")).trim(),
                        result: buildResult(m, 'r'),
                        source: "LIVE"
                    });
                });
            }

            if (plays.length === 0) {
                return res.send("Game exists historically but no play-by-play found yet (stream may not have reached it).");
            }

            plays.sort((a, b) => num(a.inning) - num(b.inning));

            const template = fs.readFileSync('plays.mustache').toString();
            const html = mustache.render(template, { plays: plays });
            res.send(html);
        });
    });
});

app.listen(port);
