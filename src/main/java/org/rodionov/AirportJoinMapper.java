package org.rodionov;


import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class AirportJoinMapper extends Mapper<LongWritable, Text, TextPair, Text> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        if (value.toString().startsWith("Code,Description")){
            return;
        }
        String record = value.toString();
        String[] parts = record.split(",", 2);
        context.write(new TextPair(parts[0].split("\"")[1], "0"), new Text(parts[1].split("\"")[1].split("\"")[0]));
    }
}