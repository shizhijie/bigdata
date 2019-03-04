package com.zjs.bwcx.bigdata.mr.wcdemo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 相当于yarn的客户端 需要在此封装我们的mr程序的相关运行参数，指定jar 最后提交给yarn
 * 
 * @ClassName WordCountDriver
 * @Description TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @Date 2018年6月11日 上午9:27:29
 * @version 1.0.0
 */
public class WordCountDriver {

    public static void main(String[] args) throws Exception {

        Configuration configuration = new Configuration();
        
        //本地模式运行mr程序时，输入输出的数据可以在本地，也可以在hdfs上
        //到底在哪里，就看以下两行配置你用哪行
        /*configuration.set("marreduce.framework.name", "local");*/
        /*configuration.set("yarn.resourcemanager.hostname", "mini1");*/
        /*configuration.set("fs.defaultFS", "file:///");*/
        
        
       /* configuration.set("marreduce.framework.name", "yarn");
        configuration.set("yarn.resourcemanager.hostname", "mini1");
        configuration.set("fs.defaultFS", "hdfs://mini1:9000");*/
        Job instance = Job.getInstance(configuration);
        
        instance.setJar("F:\\zx\\代码和依赖\\wc.jar");
        //指定本程序的jar包所在的本地路径
        //instance.setJarByClass(WordCountDriver.class);

        // 指定业务job要使用的mapper/Reducer业务类
        instance.setMapperClass(WordcountMapper.class);
        instance.setReducerClass(WordcountReducer.class);
        
        //指定需要使用combiner,以及使用哪个类作为combiner的逻辑
        //instance.setCombinerClass(WordCountCombiner.class);
        
        //如果不设置InputFormat,它默认用的是TextInputForformat.class
        /*instance.setInputFormatClass(CombineTextInputFormat.class);
        CombineTextInputFormat.setMaxInputSplitSize(instance, 4194304);//4m
        CombineTextInputFormat.setMinInputSplitSize(instance, 2097152);//2m
*/        
        // 指定mapper输出数据的kv类型
        instance.setMapOutputKeyClass(Text.class);
        instance.setMapOutputValueClass(IntWritable.class);

        // 指定最终输出数据的kv类型
        instance.setOutputKeyClass(Text.class);
        instance.setOutputValueClass(IntWritable.class);
        
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
