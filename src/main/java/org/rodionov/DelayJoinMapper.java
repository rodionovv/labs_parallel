package org.rodionov;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class DelayJoinMapper extends Mapper<LongWritable, Text, TextPair, Text> {

    public static final int AIRPORT_COLUMN = 14;

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        if (value.toString().startsWith("\"YEAR\",\"QUARTER\"")){
            return;
        }
        String[] parts = ParseCSV.splitComma(value);
        String airportID = ParseCSV.getKey(parts, AIRPORT_COLUMN);
        String delay = ParseCSV.getValue(parts, 17);
        context.write(new TextPair(airportID, "1"), new Text(delay));

    }
}