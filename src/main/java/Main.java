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
import java.util.Iterator;


public class Main {

    public static class TextPair implements WritableComparable<TextPair> {
        Text first;
        Text second;

        TextPair() {
            this.first = new Text();
            this.second = new Text();
        }

        TextPair(String key1, String key2) {
            this.first = new Text(key1);
            this.second = new Text(key2);
        }

        @Override
        public boolean equals(Object obj) {
            TextPair tp = (TextPair) obj;
            if (this.first.equals(tp.first)){
                if (this.second.equals(tp.second)){
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
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


            int ff = Integer.parseInt(this.first.toString());
            int fs = Integer.parseInt(this.second.toString());
            int sf = Integer.parseInt(tp.second.toString());
            int ss = Integer.parseInt(tp.second.toString());
            if (ff == sf){
                if (fs == ss) return 0;
                else if (fs > ss) {
                    return -1;
                } else {
                    return 1;
                }
            } else if ( ff > sf ) {
                return -1;
            } else {
                return 1;
            }
        }

        public static class FirstPartitioner extends Partitioner<TextPair, Text>{
            @Override
            public int getPartition(TextPair textPair, Text text, int numPartitions) {
                if (Integer.parseInt(textPair.first.toString()) < 13000) return 0;
                else return 1 % numPartitions;
            }
        }

        public static class FirstComparator extends WritableComparator{
            public FirstComparator() {
                super(TextPair.class, true);
            }

            @Override
            public int compare(WritableComparable o1, WritableComparable o2) {
                TextPair tp1 = (TextPair) o1;
                TextPair tp2 = (TextPair) o2;
                return tp1.first.compareTo(tp2.first);
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
            String[] parts = record.split(",", 2);
            context.write(new TextPair(parts[0].split("\"")[1], "0"), new Text(parts[1].split("\"")[1].split("\"")[0]));
        }
    }

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
            context.write(new Text(airportName), new Text("max = " + maxDelay + ", min = " + minDelay + ", averageDelay = " + allDelay / delaysNum));
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
