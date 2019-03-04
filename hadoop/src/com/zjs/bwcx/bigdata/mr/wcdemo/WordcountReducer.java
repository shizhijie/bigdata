package com.zjs.bwcx.bigdata.mr.wcdemo;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class WordcountReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
    
    
    @Override
    protected void reduce(Text arg0, Iterable<IntWritable> arg1,
            Reducer<Text, IntWritable, Text, IntWritable>.Context arg2) throws IOException, InterruptedException {
        
        int count = 0;
        for (IntWritable intWritable : arg1) {
            count += intWritable.get();
        } 
        
        arg2.write(arg0, new IntWritable(count));
        
    }
    
}
