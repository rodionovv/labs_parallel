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
        context.write(new TextPair(Main.getLine(value, 0), "0"), new Text(Main.getLine(value, 1)));
    }
}