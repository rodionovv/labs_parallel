package org.rodionov;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class FirstPartitioner extends Partitioner<Main.TextPair, Text> {
    @Override
    public int getPartition(Main.TextPair textPair, Text text, int numPartitions) {
        if (Integer.parseInt(textPair.first.toString()) < 13000) return 0;
        else return 1 % numPartitions;
    }
}