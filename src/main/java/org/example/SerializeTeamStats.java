package org.example;

import edu.uchicago.mpcs53013.nvega.baseball.TeamStats;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.thrift.TSerializer;

public class SerializeTeamStats extends BaseSerializer<TeamStats> {

    public static void main(String[] args) throws Exception {

        if (args.length == 0 || args.length > 2) {
            System.err.println("Usage: yarn jar jarname inputPath [outputDir]");
            System.exit(-1);
        }

        new SerializeTeamStats().run(
                args[0],
                (args.length > 1) ? args[1] : "/nvega_data/thrift/teamstats"
        );
    }

    @Override
    protected BaseProcessor<TeamStats> createProcessor(Writer writer, TSerializer serializer) {
        return new TeamStatsProcessor() {
            @Override
            protected void processRecord(TeamStats ts, String sourceName) throws java.io.IOException {
                try {
                    writer.append(
                            new IntWritable(1), // constant key like original
                            new BytesWritable(serializer.serialize(ts))
                    );
                } catch (Exception e) {
                    throw new java.io.IOException(e);
                }
            }
        };
    }
}
