package com.zjs.bwcx.bigdata.mr.flowsum;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class FlowCountSort {
    
    static class FlowCountSortMapper extends Mapper<LongWritable, Text, FlowBean, Text>{
        
        FlowBean bean = new FlowBean();
        Text v = new Text();
        
        @Override
        protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, FlowBean, Text>.Context context)
                throws IOException, InterruptedException {
            
            //拿到上一个统计程序的输出结果 ，已经是个手机号 的总流量信息
            String line = value.toString();
            
            String[] fileds = line.split("\t");
            
            String phoneNb = fileds[0];
            
            long upFlow = Long.parseLong(fileds[1]);
            long dFlow = Long.parseLong(fileds[2]);
            
            bean.set(upFlow, dFlow);
            v.set(phoneNb);
            
            context.write(bean, v);
            
        }
        
    }
    
    static class FlowCountSortReducer extends Reducer<FlowBean, Text, Text, FlowBean>{
        
        //<bean(),phonenbr>
        @Override
        protected void reduce(FlowBean bean, Iterable<Text> values, Reducer<FlowBean, Text, Text, FlowBean>.Context context)
                throws IOException, InterruptedException {
            
            context.write(values.iterator().next(), bean);
        }
        
    }
    
    public static void main(String[] args) throws Exception {
        
        Configuration configuration = new Configuration();
        /*configuration.set("marreduce.framework.name", "yarn");
        configuration.set("yarn.resourcemanager.hostname", "mini1");*/
        Job instance = Job.getInstance(configuration);
        
        //instance.setJar("/home/hadoop/wc.jar");
        //指定本程序的jar包所在的本地路径
        instance.setJarByClass(FlowCountSort.class);

        // 指定业务job要使用的mapper/Reducer业务类
        instance.setMapperClass(FlowCountSortMapper.class);
        instance.setReducerClass(FlowCountSortReducer.class);
        
        //指定我们自定义的数据分区器
        //instance.setPartitionerClass(ProvincePartitioner.class);
        //同时指定相应"分区"数量的reducetask
        //instance.setNumReduceTasks(5);
        
        // 指定mapper输出数据的kv类型
        instance.setMapOutputKeyClass(FlowBean.class);
        instance.setMapOutputValueClass(Text.class);

        // 指定最终输出数据的kv类型
        instance.setOutputKeyClass(Text.class);
        instance.setOutputValueClass(FlowBean.class);

        // 指定instance的输入原始文件所在目录
        FileInputFormat.setInputPaths(instance, new Path(args[0]));
        
        Path outPath = new Path(args[1]);
        org.apache.hadoop.fs.FileSystem fs = org.apache.hadoop.fs.FileSystem.get(configuration);
        
        if (fs.exists(outPath)) {
            fs.delete(outPath, true);
        }
        // 指定instance的输出结果文件所在目录
        FileOutputFormat.setOutputPath(instance, outPath);
        
        //将instance中配置的相关参数，以及instance所用的java类所在的jar ,提交给yarn 去运行
        //instance.submit();
        boolean res = instance.waitForCompletion(true);
        System.exit(res?0:1);
        
    }
    
}
