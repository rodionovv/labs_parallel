package org.rodionov;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

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