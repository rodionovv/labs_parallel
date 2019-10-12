package org.rodionov;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Partitioner;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class TextPair implements WritableComparable<Main.TextPair> {
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
        Main.TextPair tp = (Main.TextPair) obj;
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
    public int compareTo(Main.TextPair tp) {
        int ff = Integer.parseInt(this.first.toString());
        int fs = Integer.parseInt(this.second.toString());
        int sf = Integer.parseInt(tp.first.toString());
        int ss = Integer.parseInt(tp.second.toString());
        if (ff == sf){
            if (fs == ss) return 0;
            else if (fs > ss) {
                return 1;
            } else {
                return -1;
            }
        } else if ( ff > sf ) {
            return 1;
        } else {
            return -1;
        }
    }

    public static class FirstPartitioner extends Partitioner<Main.TextPair, Text> {
        @Override
        public int getPartition(Main.TextPair textPair, Text text, int numPartitions) {
            if (Integer.parseInt(textPair.first.toString()) < 13000) return 0;
            else return 1 % numPartitions;
        }
    }

    public static class FirstComparator extends WritableComparator {
        public FirstComparator() {
            super(Main.TextPair.class, true);
        }

        @Override
        public int compare(WritableComparable o1, WritableComparable o2) {
            Main.TextPair tp1 = (Main.TextPair) o1;
            Main.TextPair tp2 = (Main.TextPair) o2;
            return tp1.first.compareTo(tp2.first);
        }
    }
}