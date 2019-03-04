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

import com.zjs.bwcx.bigdata.mr.provinceflow.ProvincePartitioner;

public class FlowCount {
    
    
    static class FlowCountMapper extends Mapper<LongWritable, Text, Text, FlowBean>{
        
        @Override
        protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, FlowBean>.Context context)
                throws IOException, InterruptedException {
            
            //将一行内容转成String
            String line = value.toString();
            //切分字段
            String[] fileds = line.split("\t");
            //取出手机号
            String phoneNum = fileds[1];
            //取出上行流量和下行流量
            long upFlow = Long.parseLong(fileds[fileds.length - 3]);
            long dFlow = Long.parseLong(fileds[fileds.length - 2]);
            
            context.write(new Text(phoneNum), new FlowBean(upFlow, dFlow));
        }
        
    }
    
    static class FlowCountReducer extends Reducer<Text, FlowBean, Text, FlowBean>{
        
        
        @Override
        protected void reduce(Text arg0, Iterable<FlowBean> arg1, Reducer<Text, FlowBean, Text, FlowBean>.Context arg2)
                throws IOException, InterruptedException {
            
            long sum_upFlow = 0;
            long sum_dFlow = 0;
            
            //便利所有的bean 将其中的上行流量和下行流量分别累加
            
            for (FlowBean flowBean : arg1) {
                
                sum_upFlow += flowBean.getUpFlow();
                sum_dFlow += flowBean.getdFlow();
                
            }
            
            FlowBean flowBean = new FlowBean(sum_upFlow, sum_dFlow);
            
            arg2.write(arg0, flowBean);
            
        }
        
    }
    
    
    
    
    
    
    
    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        /*configuration.set("marreduce.framework.name", "yarn");
        configuration.set("yarn.resourcemanager.hostname", "mini1");*/
        Job instance = Job.getInstance(configuration);
        
        //instance.setJar("/home/hadoop/wc.jar");
        //指定本程序的jar包所在的本地路径
        instance.setJarByClass(FlowCount.class);

        // 指定业务job要使用的mapper/Reducer业务类
        instance.setMapperClass(FlowCountMapper.class);
        instance.setReducerClass(FlowCountReducer.class);
        
        //指定我们自定义的数据分区器
        instance.setPartitionerClass(ProvincePartitioner.class);
        //同时指定相应"分区"数量的reducetask
        instance.setNumReduceTasks(5);
        
        // 指定mapper输出数据的kv类型
        instance.setMapOutputKeyClass(Text.class);
        instance.setMapOutputValueClass(FlowBean.class);

        // 指定最终输出数据的kv类型
        instance.setOutputKeyClass(Text.class);
        instance.setOutputValueClass(FlowBean.class);

        // 指定instance的输入原始文件所在目录
        FileInputFormat.setInputPaths(instance, new Path(args[0]));
        // 指定instance的输出结果文件所在目录
        FileOutputFormat.setOutputPath(instance, new Path(args[1]));
        
        //将instance中配置的相关参数，以及instance所用的java类所在的jar ,提交给yarn 去运行
        //instance.submit();
        boolean res = instance.waitForCompletion(true);
        System.exit(res?0:1);
    }
    
}
