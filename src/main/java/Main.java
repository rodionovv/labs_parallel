import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;


public class Main {

    public static class CallsJoinMapper extends Mapper<LongWritable, Text, TextPair, Text> {
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            ServiceCall call  = new ServiceCall(value);
            context.write(new TextPair(call.getSystemA().toString(), "1"),
                    new Text(call.toString()));
        }
    }

    public static class SystemJoinMapper extends Mapper<LongWritable, Text, TextPair, Text> {
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            if (value.toString().startsWith(""))


//            SystemInfo system = new SystemInfo(value);
//            context.write(new TextPair(system.getSystemCode().toString(), "0"), new Text(system.toString()));
        }
    }

    public static void main(String[] args) throws Exception{
        Job job = Job.getInstance();
        job.setJarByClass(Main.class);
        job.setJobName("Reduce side join");
        MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, CallsJoinMapper.class);
        MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, SystemJoinMapper.class);
        FileOutputFormat.setOutputPath(job, new Path(args[2]));
        job.setPartitionerClass(TextPair.FirstaPartitioner.class);
        job.setGroupingComparatorClass(TextPair.FirstComparator.class);
        job.setReducerClass(JoinReducer.class);
        job.setMapOutputKeyClass(TextPair.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setNumReduceTasks(2);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
