package org.rodionov;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.StringTokenizer;

public class WordCountApp {
    public static class WordMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

        private final static IntWritable one = new IntWritable(1);

        @Override
        protected void map(LongWritable key, Text value, Context context
        ) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString().replaceAll("[^a-zA-Z0-9а-яА-Я\\s+]", "").toLowerCase());

            while(itr.hasMoreTokens()){
                Text word = new Text();
                word.set(itr.nextToken());
                context.write(word, one);
            }

        }
    }

    public static class WordReducer extends Reducer<Text, IntWritable, Text, LongWritable> {

        private LongWritable res = new LongWritable();

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws
                IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values){
                sum += val.get();
            }
            res.set(sum);
            context.write(key, res);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: WordCountApp <input path> <output path>");
            System.exit(-1);
        }
        Job job = Job.getInstance();
        job.setJarByClass(WordCountApp.class);
        job.setJobName("Word count");
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.setMapperClass(WordMapper.class);
        job.setReducerClass(WordReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setNumReduceTasks(2);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
