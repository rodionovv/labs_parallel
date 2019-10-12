package org.rodionov;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class DelayJoinMapper extends Mapper<LongWritable, Text, TextPair, Text> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        if (value.toString().startsWith("\"YEAR\",\"QUARTER\"")){
            return;
        }
        String record = value.toString();
        String[] parts = record.split(",");
        context.write(new TextPair(parts[14], "1"), new Text(parts[17]));

    }
}