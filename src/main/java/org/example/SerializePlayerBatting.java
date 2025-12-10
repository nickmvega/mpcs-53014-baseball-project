package org.example;

import edu.uchicago.mpcs53013.nvega.baseball.PlayerBatting;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.thrift.TSerializer;

public class SerializePlayerBatting extends BaseSerializer<PlayerBatting> {
    public static void main(String[] args) throws Exception {
        if (args.length == 0 || args.length > 2) {
            System.err.println("Usage:  yarn jar jarname inputPath [hdfsOutputDir]");
            System.exit(-1);
        }

        new SerializePlayerBatting().run(
                args[0],
                (args.length > 1) ? args[1] : "/nvega_data/thrift/batting"
        );
    }

    @Override
    protected BaseProcessor<PlayerBatting> createProcessor(
            SequenceFile.Writer writer,
            TSerializer serializer) {
        return new PlayerBattingProcessor() {
            @Override
            protected void processRecord(PlayerBatting pb, String sourceName) throws java.io.IOException {
                try {
                    writer.append(
                            new IntWritable(1),
                            new BytesWritable(serializer.serialize(pb))
                    );
                } catch (Exception e) {
                    throw new java.io.IOException(e);
                }
            }
        };
    }
}
