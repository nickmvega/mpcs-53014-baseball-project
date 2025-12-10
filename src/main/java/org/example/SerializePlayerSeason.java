package org.example;

import edu.uchicago.mpcs53013.nvega.baseball.PlayerSeason;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.thrift.TSerializer;

public class SerializePlayerSeason extends BaseSerializer<PlayerSeason> {

    public static void main(String[] args) throws Exception {

        if (args.length == 0 || args.length > 2) {
            System.err.println("Usage: yarn jar jarname inputPath [outputDir]");
            System.exit(-1);
        }

        new SerializePlayerSeason().run(
                args[0],
                (args.length > 1) ? args[1] : "/nvega_data/thrift/allplayers"
        );
    }

    @Override
    protected BaseProcessor<PlayerSeason> createProcessor(Writer writer, TSerializer serializer) {
        return new PlayerSeasonProcessor() {
            @Override
            protected void processRecord(PlayerSeason ps, String sourceName) throws java.io.IOException {
                try {
                    writer.append(
                            new IntWritable(1),                        // constant key like your original
                            new BytesWritable(serializer.serialize(ps))
                    );
                } catch (Exception e) {
                    throw new java.io.IOException(e);
                }
            }
        };
    }
}
