package org.rodionov;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Partitioner;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class TextPair implements WritableComparable<TextPair> {
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
        return Objects.hash(first, second);
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
}