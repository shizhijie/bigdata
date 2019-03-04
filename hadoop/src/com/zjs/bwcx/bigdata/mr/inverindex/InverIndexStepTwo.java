package com.zjs.bwcx.bigdata.mr.inverindex;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class InverIndexStepTwo {
    
    static class InverIndexStepTwoMapper extends Mapper<LongWritable, Text, Text, Text>{
        
        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            
            String line = value.toString();
            
            String[] files = line.split("--");
            
            context.write(new Text(files[0]), new Text(files[1]));
        }
        
    }
    
    static class InverIndexStepTwoReducer extends Reducer<Text, Text, Text, Text>{
        
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            
            context.write(new Text("单词key值"), new Text("\t文件名称以及在该文件中出现的次数"));
            
            StringBuffer sb = new StringBuffer();
            
            for (Text value : values) {
                
                sb.append(value.toString().replace("\t", "-->") + "\t");
                
            }
            
            context.write(key, new Text(sb.toString()));
            
        }
        
    }
    
    public static void main(String[] args) throws Exception {
        
        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration);
        
        job.setJarByClass(InverIndexStepTwo.class);
        
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        
        job.setMapperClass(InverIndexStepTwoMapper.class);
        job.setReducerClass(InverIndexStepTwoReducer.class);
        
        FileInputFormat.setInputPaths(job, new org.apache.hadoop.fs.Path("F:/zx/wordcount/inverindexoutput"));
        FileOutputFormat.setOutputPath(job, new org.apache.hadoop.fs.Path("F:/zx/wordcount/inverindexoutput_step_two"));
        
        boolean res = job.waitForCompletion(true);
        
        System.exit(res?0:1);
    }
    
}
