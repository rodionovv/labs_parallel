package org.rodionov;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public static class JoinReducer extends Reducer<TextPair, Text, Text, Text> {
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