ADD JAR /home/hadoop/nvega/jars/uber-hdfs-ingest-baseball-archetype-1.0-SNAPSHOT.jar;
ADD JAR /home/hadoop/nvega/jars/hdfs-ingest-baseball-archetype-1.0-SNAPSHOT.jar

CREATE EXTERNAL TABLE IF NOT EXISTS nvega_plays
  ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.thrift.ThriftDeserializer'
    WITH SERDEPROPERTIES (
      'serialization.class' = 'edu.uchicago.mpcs53013.nvega.baseball.BaseballPlay',
      'serialization.format' = 'org.apache.thrift.protocol.TBinaryProtocol'
    )
  STORED AS SEQUENCEFILE
  LOCATION '/nvega_data/thrift/plays';

CREATE EXTERNAL TABLE IF NOT EXISTS nvega_gameinfo
  ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.thrift.ThriftDeserializer'
    WITH SERDEPROPERTIES (
      'serialization.class' = 'edu.uchicago.mpcs53013.nvega.baseball.GameSummary',
      'serialization.format' = 'org.apache.thrift.protocol.TBinaryProtocol'
    )
  STORED AS SEQUENCEFILE
  LOCATION '/nvega_data/thrift/gameinfo';

CREATE EXTERNAL TABLE IF NOT EXISTS nvega_allplayers
  ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.thrift.ThriftDeserializer'
    WITH SERDEPROPERTIES (
      'serialization.class' = 'edu.uchicago.mpcs53013.nvega.baseball.PlayerSeason',
      'serialization.format' = 'org.apache.thrift.protocol.TBinaryProtocol'
    )
  STORED AS SEQUENCEFILE
  LOCATION '/nvega_data/thrift/allplayers';

CREATE EXTERNAL TABLE IF NOT EXISTS nvega_batting
  ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.thrift.ThriftDeserializer'
    WITH SERDEPROPERTIES (
      'serialization.class' = 'edu.uchicago.mpcs53013.nvega.baseball.PlayerBatting',
      'serialization.format' = 'org.apache.thrift.protocol.TBinaryProtocol'
    )
  STORED AS SEQUENCEFILE
  LOCATION '/nvega_data/thrift/batting';

CREATE EXTERNAL TABLE IF NOT EXISTS nvega_pitching
  ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.thrift.ThriftDeserializer'
    WITH SERDEPROPERTIES (
      'serialization.class' = 'edu.uchicago.mpcs53013.nvega.baseball.PlayerPitching',
      'serialization.format' = 'org.apache.thrift.protocol.TBinaryProtocol'
    )
  STORED AS SEQUENCEFILE
  LOCATION '/nvega_data/thrift/pitching';

CREATE EXTERNAL TABLE IF NOT EXISTS nvega_teamstats
  ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.thrift.ThriftDeserializer'
    WITH SERDEPROPERTIES (
      'serialization.class' = 'edu.uchicago.mpcs53013.nvega.baseball.TeamStats',
      'serialization.format' = 'org.apache.thrift.protocol.TBinaryProtocol'
    )
  STORED AS SEQUENCEFILE
  LOCATION '/nvega_data/thrift/teamstats';
