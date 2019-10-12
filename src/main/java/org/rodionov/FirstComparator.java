package org.rodionov;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class FirstComparator extends WritableComparator {
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