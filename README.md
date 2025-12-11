# MPCS 53013

# Baseball Project

The idea of this project was to create a web app where a user could query any MLB baseball game using a date, and the result would be the final score summary and the play by play information about the game. The real time data would come into play during live games, where live data updating the play by play table in real time. 

This project follows an Lambda Architecture with a batch layer, serving layer, and speed layer. The batch layer contains play by play data for each from 2020 to 2024. The speed layer simulates data coming in from 2025. 

# Challenges

One of the challenges that I faced with this project were the long wait time for cluster resources on various queries. I felt that a lot of my progress was hindered throug this and if I was able to give my past self any advice, it would be to try and run jobs when the cluster is less contested (times like 2 AM, etc). 

# Data Ingestion 

Source of data: https://www.retrosheet.org/downloads/csvcontents.html

All of the data is from retrosheet. There are 6 CSV files that were used. The plays.csv is 9 GB and the most significant data to this project. The idea was to have 

I ingested these raw files in HDFS:

[hadoop@ip-172-31-81-29 ~]$ hdfs dfs -ls /nvega_data/

Found 9 items
drwxr-xr-x   - hadoop hdfsadmingroup          0 2025-12-08 00:21 /nvega_data/allplayers
drwxr-xr-x   - hadoop hdfsadmingroup          0 2025-12-10 22:09 /nvega_data/baseball_stream
drwxr-xr-x   - hadoop hdfsadmingroup          0 2025-12-08 00:21 /nvega_data/batting
drwxr-xr-x   - hadoop hdfsadmingroup          0 2025-12-08 00:21 /nvega_data/gameinfo
drwxr-xr-x   - hadoop hdfsadmingroup          0 2025-12-10 22:42 /nvega_data/kafka_data
drwxr-xr-x   - hadoop hdfsadmingroup          0 2025-12-08 00:21 /nvega_data/pitching
drwxr-xr-x   - hadoop hdfsadmingroup          0 2025-12-08 00:21 /nvega_data/plays
drwxr-xr-x   - hadoop hdfsadmingroup          0 2025-12-08 00:21 /nvega_data/teamstats
drwxr-xr-x   - hadoop hdfsadmingroup          0 2025-12-08 21:13 /nvega_data/thrift

The /nvega_data/allplayers, /nvega_data/batting, /nvega_data/game_info, /nvega_data/pitching, /nvega_data/plays, /nvega_data/teamstats is where all the CSV files live. The /nvega_data/thrift contains the thrift serializations of each of the CSV files. 

[hadoop@ip-172-31-81-29 ~]$ hdfs dfs -ls /nvega_data/thrift/

Found 6 items
drwxr-xr-x   - hadoop hdfsadmingroup          0 2025-12-08 21:21 /nvega_data/thrift/allplayers
drwxr-xr-x   - hadoop hdfsadmingroup          0 2025-12-08 21:20 /nvega_data/thrift/batting
drwxr-xr-x   - hadoop hdfsadmingroup          0 2025-12-08 21:19 /nvega_data/thrift/gameinfo
drwxr-xr-x   - hadoop hdfsadmingroup          0 2025-12-08 21:20 /nvega_data/thrift/pitching
drwxr-xr-x   - hadoop hdfsadmingroup          0 2025-12-08 21:17 /nvega_data/thrift/plays
drwxr-xr-x   - hadoop hdfsadmingroup          0 2025-12-08 21:21 /nvega_data/thrift/teamstats

hive> SHOW TABLES LIKE 'nvega_*';
nvega_allplayers
nvega_baseball_stream
nvega_batting
nvega_game_batting_player_stats
nvega_game_pitching_player_stats
nvega_game_summary
nvega_gameinfo
nvega_pitching
nvega_play_by_play_for_game
nvega_plays
nvega_teamstats
Time taken: 0.217 seconds, Fetched: 11 row(s)
hive> 

The HIVE tables above are the following that were using to create hbase tables. The 

HBase Tables:
"nvega_hb_game_batting_stats", "nvega_hb_game_pitching_stats", "nvega_hb_game_summary", "nvega_hb_play_by_play_new", "nvega_hb_play_by_play", "nvega_hb_play_by_play_v2", "nvega_latest_baseball_play"


# Run Web App
Commands to run my web app and speed layer. 

## Set up Kafka & Speed Layer

ssh hadoop@ec2-34-230-47-10.compute-1.amazonaws.com

cd ~/nvega/jars

nohup java -cp uber-kafka-baseball-archetype-1.0-SNAPSHOT.jar \
  org.example.StreamIntoKafkaBaseball \
  boot-public-byg.mpcs53014kafka.2siu49.c2.kafka.us-east-1.amazonaws.com:9196 &


cd ~/nvega/jars

nohup spark-submit \
 --driver-java-options "-Dlog4j.configuration=file:///home/hadoop/log4j.properties" \
 --master local[2] \
 --class StreamBaseball \
 uber-speed-layer-baseball-archetype-1.0-SNAPSHOT.jar \
 boot-public-byg.mpcs53014kafka.2siu49.c2.kafka.us-east-1.amazonaws.com:9196 &


cd ~/kafka_2.12-3.9.1/bin

./kafka-console-consumer.sh \
  --bootstrap-server boot-public-byg.mpcs53014kafka.2siu49.c2.kafka.us-east-1.amazonaws.com:9196 \
  --topic nvega-baseball-plays \
  --from-beginning \
  --consumer.config ~/kafka.client.properties

## Check that Kafka is streaming 

hbase shell

scan 'nvega_latest_baseball_play', {LIMIT => 5}

## Load App

cd ~/nvega_baseball_app

ssh ec2-user@ec2-52-20-203-80.compute-1.amazonaws.com

node app.js 3092 http://ec2-54-89-237-222.compute-1.amazonaws.com:8070/

http://ec2-52-20-203-80.compute-1.amazonaws.com:3092

## Kill jobs after

pkill -f StreamBaseball

pkill -f StreamIntoKafkaBaseball

## Recreate hbase tables and repeat to simualate streaming

hbase shell
disable 'nvega_latest_baseball_play'
drop 'nvega_latest_baseball_play'
create 'nvega_latest_baseball_play', 'g', 'b', 'p', 'r'
exit
