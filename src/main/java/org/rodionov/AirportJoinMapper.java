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
        String[] parts = ParseCSV.splitComma(value, 2);
        String airportID = ParseCSV.getKey(parts);
        String airportName = ParseCSV.getValue(parts);
        context.write(new TextPair(airportID, "0"), new Text(airportName));
    }
}