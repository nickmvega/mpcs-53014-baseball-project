package org.example;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.fasterxml.jackson.databind.ObjectMapper;

public class StreamIntoKafkaBaseball {

    static class Task extends TimerTask {
        BufferedReader reader;
        KafkaProducer<String,String> producer;
        ObjectMapper mapper = new ObjectMapper();
        String TOPIC = "nvega-baseball-plays";

        public Task() throws FileNotFoundException {
            reader = new BufferedReader(
                    new FileReader("/home/hadoop/nvega/baseball_stream.csv"));

            Properties props = new Properties();
            props.put("bootstrap.servers", bootstrapServers);
            props.put("acks", "all");
            props.put("retries", 0);
            props.put("batch.size", 16384);
            props.put("linger.ms", 1);
            props.put("buffer.memory", 33554432);
            props.put("key.serializer",
                    "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer",
                    "org.apache.kafka.common.serialization.StringSerializer");
            props.put("security.protocol", "SASL_SSL");
            props.put("sasl.mechanism", "SCRAM-SHA-512");
            props.put("sasl.jaas.config",
                    "org.apache.kafka.common.security.scram.ScramLoginModule required " +
                            "username=\"mpcs53014-2025\" password=\"A3v4rd4@ujjw\";");

            producer = new KafkaProducer<>(props);
        }

        private int safeParseInt(String value) {
            try {
                if (value == null || value.equals("\\N") || value.trim().isEmpty()) {
                    return 0;
                }
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        @Override
        public void run() {
            try {
                // Read next line from CSV
                String line = reader.readLine();
                if (line == null)
                    return;

                if(line.startsWith("rowKey") || line.contains("gameDate")) {
                    return;
                }

                String[] c = line.split(",");

                KafkaBaseballRecord rec = new KafkaBaseballRecord(
                        c[0], c[1], c[2], c[3],
                        safeParseInt(c[4]),
                        safeParseInt(c[5]),
                        c[6], c[7], c[8], c[9], c[10],
                        c[11], c[12], c[13], c[14], c[15],
                        safeParseInt(c[16]),
                        safeParseInt(c[17]),
                        safeParseInt(c[18]),
                        safeParseInt(c[19]),
                        safeParseInt(c[20]),
                        safeParseInt(c[21]),
                        safeParseInt(c[22]),
                        safeParseInt(c[23]),
                        safeParseInt(c[24])
                );

                String jsonRecord = mapper.writeValueAsString(rec);

                ProducerRecord<String,String> data =
                        new ProducerRecord<>(TOPIC, rec.rowKey, jsonRecord);

                producer.send(data);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static String bootstrapServers =
            "boot-public-byg.mpcs53014kafka.2siu49.c2.kafka.us-east-1.amazonaws.com:9196";

    public static void main(String[] args) throws FileNotFoundException {
        if(args.length > 0)
            bootstrapServers = args[0];

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new Task(), 0, 60 * 1000);
    }
}