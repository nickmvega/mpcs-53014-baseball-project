The app has a batch layer, speed layer and serving layer. 






Commands to run my web app and speed layer. 


//Set up Kafka

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

//Check that Kafka is streaming 

hbase shell

scan 'nvega_latest_baseball_play', {LIMIT => 5}

ssh ec2-user@ec2-52-20-203-80.compute-1.amazonaws.com

//Load App

cd ~/nvega_baseball_app

node app.js 3092 http://ec2-54-89-237-222.compute-1.amazonaws.com:8070/

http://ec2-52-20-203-80.compute-1.amazonaws.com:3092

//Kill everything after

pkill -f StreamBaseball

pkill -f StreamIntoKafkaBaseball

//Recreate a repeat to simualate streaming

hbase shell
disable 'nvega_latest_baseball_play'
drop 'nvega_latest_baseball_play'
create 'nvega_latest_baseball_play', 'g', 'b', 'p', 'r'
exit
