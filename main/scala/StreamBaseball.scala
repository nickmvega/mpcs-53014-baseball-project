import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{ConnectionFactory, Put}
import org.apache.hadoop.hbase.util.Bytes

case class KafkaBaseballRecord(
                                rowKey: String,
                                gameDate: String,
                                homeTeam: String,
                                awayTeam: String,
                                inning: Int,
                                playSeq: Int,
                                batterId: String,
                                batterFirstName: String,
                                batterLastName: String,
                                batterTeam: String,
                                batterHand: String,
                                pitcherId: String,
                                pitcherFirstName: String,
                                pitcherLastName: String,
                                pitcherTeam: String,
                                pitcherHand: String,
                                singleHit: Int,
                                doubleHit: Int,
                                tripleHit: Int,
                                hrHit: Int,
                                walk: Int,
                                strikeout: Int,
                                rbiProduced: Int,
                                runsScored: Int,
                                outsOnPlay: Int
                              )

object StreamBaseball {
  private val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  private val hbaseConf: Configuration = HBaseConfiguration.create()
  private val hbaseConnection = ConnectionFactory.createConnection(hbaseConf)
  private val table = hbaseConnection.getTable(TableName.valueOf("nvega_latest_baseball_play"))

  def main(args: Array[String]) {

    if (args.length < 1) {
      System.err.println("Usage: StreamBaseball <brokers>")
      System.exit(1)
    }

    val Array(brokers) = args
    val sparkConf = new SparkConf().setAppName("BaseballSpeedLayer")
    val ssc = new StreamingContext(sparkConf, Seconds(2))

    val topicsSet = Set("nvega-baseball-plays")

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> brokers,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "baseball-speed-group",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean),
      "security.protocol" -> "SASL_SSL",
      "sasl.mechanism" -> "SCRAM-SHA-512",
      "sasl.jaas.config" -> ("org.apache.kafka.common.security.scram.ScramLoginModule required " +
        "username=\"mpcs53014-2025\" password=\"A3v4rd4@ujjw\";")
    )

    val stream = KafkaUtils.createDirectStream[String, String](
      ssc, PreferConsistent,
      Subscribe[String, String](topicsSet, kafkaParams)
    )

    val parsed = stream.map(_.value).map(json =>
      mapper.readValue(json, classOf[KafkaBaseballRecord])
    )

    parsed.foreachRDD(rdd => {
      rdd.foreach { rec =>
        val put = new Put(Bytes.toBytes(rec.rowKey))

        put.addColumn(Bytes.toBytes("g"), Bytes.toBytes("gameDate"), Bytes.toBytes(rec.gameDate))
        put.addColumn(Bytes.toBytes("g"), Bytes.toBytes("homeTeam"), Bytes.toBytes(rec.homeTeam))
        put.addColumn(Bytes.toBytes("g"), Bytes.toBytes("awayTeam"), Bytes.toBytes(rec.awayTeam))
        put.addColumn(Bytes.toBytes("g"), Bytes.toBytes("inning"), Bytes.toBytes(rec.inning))
        put.addColumn(Bytes.toBytes("g"), Bytes.toBytes("playSeq"), Bytes.toBytes(rec.playSeq))

        put.addColumn(Bytes.toBytes("b"), Bytes.toBytes("batterId"), Bytes.toBytes(rec.batterId))
        put.addColumn(Bytes.toBytes("b"), Bytes.toBytes("batterFirstName"), Bytes.toBytes(rec.batterFirstName))
        put.addColumn(Bytes.toBytes("b"), Bytes.toBytes("batterLastName"), Bytes.toBytes(rec.batterLastName))
        put.addColumn(Bytes.toBytes("b"), Bytes.toBytes("batterTeam"), Bytes.toBytes(rec.batterTeam))
        put.addColumn(Bytes.toBytes("b"), Bytes.toBytes("batterHand"), Bytes.toBytes(rec.batterHand))

        put.addColumn(Bytes.toBytes("p"), Bytes.toBytes("pitcherId"), Bytes.toBytes(rec.pitcherId))
        put.addColumn(Bytes.toBytes("p"), Bytes.toBytes("pitcherFirstName"), Bytes.toBytes(rec.pitcherFirstName))
        put.addColumn(Bytes.toBytes("p"), Bytes.toBytes("pitcherLastName"), Bytes.toBytes(rec.pitcherLastName))
        put.addColumn(Bytes.toBytes("p"), Bytes.toBytes("pitcherTeam"), Bytes.toBytes(rec.pitcherTeam))
        put.addColumn(Bytes.toBytes("p"), Bytes.toBytes("pitcherHand"), Bytes.toBytes(rec.pitcherHand))

        put.addColumn(Bytes.toBytes("r"), Bytes.toBytes("singleHit"), Bytes.toBytes(rec.singleHit))
        put.addColumn(Bytes.toBytes("r"), Bytes.toBytes("doubleHit"), Bytes.toBytes(rec.doubleHit))
        put.addColumn(Bytes.toBytes("r"), Bytes.toBytes("tripleHit"), Bytes.toBytes(rec.tripleHit))
        put.addColumn(Bytes.toBytes("r"), Bytes.toBytes("hrHit"), Bytes.toBytes(rec.hrHit))
        put.addColumn(Bytes.toBytes("r"), Bytes.toBytes("walk"), Bytes.toBytes(rec.walk))
        put.addColumn(Bytes.toBytes("r"), Bytes.toBytes("strikeout"), Bytes.toBytes(rec.strikeout))
        put.addColumn(Bytes.toBytes("r"), Bytes.toBytes("rbiProduced"), Bytes.toBytes(rec.rbiProduced))
        put.addColumn(Bytes.toBytes("r"), Bytes.toBytes("runsScored"), Bytes.toBytes(rec.runsScored))
        put.addColumn(Bytes.toBytes("r"), Bytes.toBytes("outsOnPlay"), Bytes.toBytes(rec.outsOnPlay))

        table.put(put)
      }
    })

    parsed.print()

    ssc.start()
    ssc.awaitTermination()
  }
}
