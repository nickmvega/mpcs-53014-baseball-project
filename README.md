# MPCS 53013

# Baseball Project

The idea of this project was to create a web app where a user could query any MLB baseball game using a date, and the result would be the final score summary and the play by play information about the game. 

The system also includes a speed layer designed to simulate live incoming baseball plays for 2025, demonstrating how the application would behave during an actual game with real-time updates.

This project follows an Lambda Architecture with a batch layer, serving layer, and speed layer. 

The batch layer contains play by play data for each game from 2020 to 2024. The speed layer simulates data coming in from the 2025 season, and the serving layer uses HBase tables to support fast queries on a large play by play dataset. 

The web app allows a user to enter a specific date of an MLB baseball game or games. Then, the web app will show a list of all games that occured on that specific date. A simple game summary of each game will be shown. If you click on a specific game, then a full play by play summary of the game will be shown, meaning that every inning, out, and batter and pitcher match up will be shown. 

# Video

The video can be found within this github repo, labeled as RECORDING_nvega.mp4. The video shows the webapp being used. The video shows the web application being used. While the speed layer was not fully functional , the video explains its intended function, how the Kafka/Spark jobs run, and how they update HBase in real time.

# Architecture

Retrosheet CSVs → 
Thrift Serialization → Hive (Batch Layer)
                                 ↓
                      HBase (Serving Layer)
                                 ↑
                Kafka → Spark Streaming (Speed Layer)
                                 ↑
                        baseball_stream.csv (Held out data for the 2025 season)

**Batch Layer (Hive + HBase)**

In the batch layer, historical play-by-play data from the 2020–2024 seasons was stored. The data went form Retrosheet CSV to Thrift to Hive to HBase batch tables. The original CSV file was 9 GBs large.

**Serving Layer (HBase)**  

Provides quick lookups of game summaries and play-by-play events for each game on the web app.

**Speed Layer (Kafka + Spark Streaming + HBase)**  

Simulate “live” 2025 play by play for each game by streaming them into Kafka, processing with Spark Streaming, and writing the latest play into the `nvega_latest_baseball_play` HBase table.



# Challenges

A major challenge throughout the project was cluster resource contention. I waited large amount of times for slow job executions, which significantly slowed development and debugging.

If I were to redo this project, I would run heavy Hive and Spark jobs during low-traffic hours (e.g., late nights) to avoid this.

# Data Ingestion 

Source of data: https://www.retrosheet.org/downloads/csvcontents.html

All of the data is from retrosheet. There are 6 CSV files that were used. The plays.csv is 9 GB and the most significant data to this project. The idea was to have 

I ingested these raw files in HDFS:

hdfs dfs -ls /nvega_data/

/nvega_data/allplayers
/nvega_data/baseball_stream
/nvega_data/batting
/nvega_data/gameinfo
/nvega_data/kafka_data
/nvega_data/pitching
/nvega_data/plays
/nvega_data/teamstats
/nvega_data/thrift

The /nvega_data/allplayers, /nvega_data/batting, /nvega_data/game_info, /nvega_data/pitching, /nvega_data/plays, /nvega_data/teamstats is where all the CSV files live. The /nvega_data/thrift contains the thrift serializations of each of the CSV files. I used the script create_thrift_tables.hql which can be found in this github repo to create these thrift tables. 

hdfs dfs -ls /nvega_data/thrift/

/thrift/allplayers
/thrift/batting
/thrift/gameinfo
/thrift/pitching
/thrift/plays
/thrift/teamstats

Once all the data have been serialized using Thrift into /nvega_data/thrift/*, I created Hive tables using create_tables_from_thrift.hql, which can be found in this repo. I also created a custom table for joining plays, player data, and game information.

The HIVE tables created were the following:

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

## Batch Layer

The HIVE tables above are the following that were using to create hbase tables. The scripts baseball_write_to_hbase.hql and batch_layer.hql created these tables and filled them with the relevant data for our batch layer. These scripts write historical data (2020–2024) into the following HBase tables:

nvega_hb_game_batting_stats
nvega_hb_game_pitching_stats
nvega_hb_game_summary
nvega_hb_play_by_play_new
nvega_hb_play_by_play
nvega_hb_play_by_play_v2
nvega_latest_baseball_play  

nvega_hb_play_by_play_v2 is the final batch layer table in hbase, as I had many issues with the regional bug as mentioned on end. These HBase tables were used for the serving layer for the web application.

# Jars

The jars used for this project can be found in the hadoop cluster. 

/home/hadoop/nvega/jars

hdfs-ingest-baseball-archetype-1.0-SNAPSHOT.jar  
uber-hdfs-ingest-baseball-archetype-1.0-SNAPSHOT.jar
kafka-baseball-archetype-1.0-SNAPSHOT.jar        
uber-kafka-baseball-archetype-1.0-SNAPSHOT.jar                
uber-speed-layer-baseball-archetype-1.0-SNAPSHOT.jar
speed-layer-baseball-archetype-1.0-SNAPSHOT.jar

I included all jars relevant to thrift serialization, kafka, and the speed layer. Additioanlly, I've uploaded those jars to this github repo. 

# Scripts

The scripts that I used for this project to create tables, views, and writing to hbase are all on the hadoop cluster as well as in this github repo. 

/home/hadoop/nvega/scripts

baseball_write_to_hbase.hql  
create_baseball_tables.hql
batch_layer.hql              
create_tables_from_thrift.hq

# Code

All the code relating to thrift, kafka, speed layer, scripts for creating hive and updating hbase tables, 

# Run Web App
Commands to run my web app and speed layer. 

## Set up Kafka & Speed Layer

ssh hadoop@ec2-54-89-237-222.compute-1.amazonaws.com

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
