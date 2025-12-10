package org.example;

import java.io.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

public abstract class BaseSerializer<T> {

    protected abstract BaseProcessor<T> createProcessor(SequenceFile.Writer writer, TSerializer serializer);

    public void run(String inputPath, String outputDir) throws Exception {

        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        Path outputPath = new Path(outputDir);
        fs.mkdirs(outputPath);

        String className = this.getClass().getSimpleName().replace("Serialize", "").toLowerCase();
        Path outputFile = new Path(outputDir, className + ".seq");

        if (fs.exists(outputFile)) {
            fs.delete(outputFile, true);
        }

        TSerializer ser = new TSerializer(new TBinaryProtocol.Factory());

        SequenceFile.Writer writer =
                SequenceFile.createWriter(
                        conf,
                        SequenceFile.Writer.file(new Path(outputFile.toUri())),
                        SequenceFile.Writer.keyClass(IntWritable.class),
                        SequenceFile.Writer.valueClass(BytesWritable.class),
                        SequenceFile.Writer.compression(CompressionType.NONE));

        BaseProcessor<T> processor = createProcessor(writer, ser);

        Iterable<InputStream> inputFiles;

        if (inputPath.startsWith("hdfs://")) {
            inputFiles = new InputStreamsForHdfsDirectory(fs, inputPath);
        } else if (inputPath.startsWith("s3://")) {
            inputFiles = new InputStreamsForS3Folder(inputPath);
        } else {
            inputFiles = new InputStreamsForLocalDirectory(inputPath);
        }

        for (InputStream is : inputFiles) {
            processor.processFile(is, "unused");
        }

        writer.close();
    }
}
