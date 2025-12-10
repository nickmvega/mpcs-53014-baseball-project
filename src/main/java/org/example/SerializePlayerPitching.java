package org.example;

import edu.uchicago.mpcs53013.nvega.baseball.PlayerPitching;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.thrift.TSerializer;

public class SerializePlayerPitching extends BaseSerializer<PlayerPitching> {

    public static void main(String[] args) throws Exception {

        if (args.length == 0 || args.length > 2) {
            System.err.println("Usage: yarn jar jarname inputPath [outputDir]");
            System.exit(-1);
        }

        new SerializePlayerPitching().run(
                args[0],
                (args.length > 1) ? args[1] : "/nvega_data/thrift/pitching"
        );
    }

    @Override
    protected BaseProcessor<PlayerPitching> createProcessor(Writer writer, TSerializer serializer) {
        return new PlayerPitchingProcessor() {
            @Override
            protected void processRecord(PlayerPitching pp, String sourceName) throws java.io.IOException {
                try {
                    writer.append(
                            new IntWritable(1),
                            new BytesWritable(serializer.serialize(pp))
                    );
                } catch (Exception e) {
                    throw new java.io.IOException(e);
                }
            }
        };
    }
}
