package org.rodionov;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;

import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.DataOutput;
import java.io.DataInput;
import java.io.IOException;
import java.util.Iterator;


public class Main {




    public static class JoinReducer extends Reducer<TextPair, Text, Text, Text>{
        @Override
        protected void reduce(TextPair key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            Iterator<Text> iter = values.iterator();
            Text airportName = new Text(iter.next());
            float maxDelay = 0, minDelay = Integer.MAX_VALUE, allDelay = 0, delaysNum = 0;
            while(iter.hasNext()) {
                Text delayText = iter.next();
                float delay;
                if (delayText.toString().length() > 0){
                    delay = Float.parseFloat(delayText.toString());
                } else {
                    continue;
                }
                if (delay > 0) {
                    if (delay > maxDelay) maxDelay = delay;
                    if (delay < minDelay) minDelay = delay;
                    delaysNum++;
                    allDelay += delay;
                }
            }
            if (delaysNum != 0) {
                context.write(new Text(airportName), new Text("max = " + maxDelay + ", min = " + minDelay + ", averageDelay = " + allDelay / delaysNum));
            }
        }
    }

    public static void main(String[] args) throws Exception{
        Job job = Job.getInstance();
        job.setJarByClass(Main.class);
        job.setJobName("Reduce side join");
        MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, CallsJoinMapper.class);
        MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, SystemJoinMapper.class);
        FileOutputFormat.setOutputPath(job, new Path(args[2]));
        job.setPartitionerClass(FirstPartitioner.class);
        job.setGroupingComparatorClass(FirstComparator.class);
        job.setReducerClass(JoinReducer.class);
        job.setMapOutputKeyClass(TextPair.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setNumReduceTasks(2);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
