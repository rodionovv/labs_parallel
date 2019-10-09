import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.DataOutput;
import java.io.DataInput;
import java.io.IOException;


public class Main {

    public static class TextPair implements WritableComparable<TextPair> {
        Text first;
        Text second;
        TextPair(String key1, String key2) {
            this.first = new Text(key1);
            this.second = new Text(key2);
        }

        @Override
        public boolean equals(Object obj) {
            TextPair tp = (TextPair) obj;
            return first == tp.first;
        }

        @Override
        public int hashCode() {
            return Integer.parseInt(first.toString());
        }

        @Override
        public void write(DataOutput out) throws IOException {
            first.write(out);
            second.write(out);
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            first.readFields(in);
            second.readFields(in);
        }

        @Override
        public int compareTo(TextPair tp) {
            int f = Integer.parseInt(first.toString());
            int s = Integer.parseInt(tp.first.toString());
            return (f < s ? -1 : (f == s ? 0 : 1));
        }

        public static class FirstPartitioner extends Partitioner<TextPair, Text>{
            @Override
            public int getPartition(TextPair textPair, Text text, int numPartitions) {
                if (Integer.parseInt(textPair.first.toString()) < 13000) return 0;
                else return 1 % numPartitions;
            }
        }

        public static class FirstComparator extends RawComparator{
            @Override
            public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
                return 0;
            }

            @Override
            public int compare(Object o1, Object o2) {
                return 0;
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        }
    }

    public static class CallsJoinMapper extends Mapper<LongWritable, Text, TextPair, Text> {
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

    public static class SystemJoinMapper extends Mapper<LongWritable, Text, TextPair, Text> {
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            if (value.toString().startsWith("Code,Description")){
                return;
            }

            String record = value.toString();
            String[] parts = record.split(",");
            context.write(new TextPair(parts[0], "0"), new Text(parts[1]));


        }
    }

    public static class FirstPartitioner extends Partitioner<TextPair, Text>{
        @Override
        public int getPartition(TextPair textPair, Text text, int numPartitions) {
            if (Integer.parseInt(textPair.first.toString()) < 13000) return 0;
            else return 1 % numPartitions;
        }
    }

    public static void main(String[] args) throws Exception{
        Job job = Job.getInstance();
        job.setJarByClass(Main.class);
        job.setJobName("Reduce side join");
        MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, CallsJoinMapper.class);
        MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, SystemJoinMapper.class);
        FileOutputFormat.setOutputPath(job, new Path(args[2]));
        job.setPartitionerClass(TextPair.FirstPartitioner.class);
        job.setGroupingComparatorClass(TextPair.FirstComparator.class);
        job.setReducerClass(JoinReducer.class);
        job.setMapOutputKeyClass(TextPair.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setNumReduceTasks(2);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
