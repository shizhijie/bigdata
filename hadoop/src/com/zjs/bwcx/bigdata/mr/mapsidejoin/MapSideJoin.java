package com.zjs.bwcx.bigdata.mr.mapsidejoin;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MapSideJoin {
    
    
    static class MapSideJoinMapper extends Mapper<LongWritable, Text, Text, NullWritable>{
        //用一个hashmap来保存产品信息表
        Map<String, String> pdInfoMap = new HashMap<>();
        
        Text k = new Text();
        
        /**
         * 通过mapper的源码，发现
         * setup方法是在maptask处理数据之前调用一次
         * 可以用来做一些 初始化工作
         * 
         */
        @Override
        protected void setup(Mapper<LongWritable, Text, Text, NullWritable>.Context context)
                throws IOException, InterruptedException {
            
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("pdts.txt")));
            String line;
            while (StringUtils.isNotEmpty(line = br.readLine())) {
                String[] fields = line.split(",");
                pdInfoMap.put(fields[0], fields[1]);
            }
            br.close();
            
        }
        
        //由于已经持有完整的产品信息表，所以在map方法中就能实现join了
        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            String orderLine = value.toString();
            
            String[] fileds = orderLine.split("\t");
            String pdName = pdInfoMap.get(fileds[1]);
            
            k.set(orderLine + "\t" +pdName);
            
            context.write(k, NullWritable.get());
        }
        
    }
    
    
    public static void main(String[] args) throws Exception {
        
        Configuration conf = new Configuration();
        
        Job job = Job.getInstance(conf);
        
        job.setJarByClass(MapSideJoin.class);
        
        job.setMapperClass(MapSideJoinMapper.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);
        
        FileInputFormat.setInputPaths(job, new Path("F:/zx/wordcount/mapjoininput"));
        FileOutputFormat.setOutputPath(job, new Path("F:/zx/wordcount/mapjoinoutput"));
        
        //指定需要缓存一个文件到所有的maptask运行节点工作目录
        /*job.addArchiveToClassPath(archive);*/   //缓存jar到task运行节点的classpath中
        /*job.addFileToClassPath(file);*/  //缓存文件到task工作节点的classpath中
        /*job.addCacheArchive(uri);*/   //缓存压缩包文件到task运行节点的工作目录
        /*job.addCacheFile(uri);*/  //缓存普通文件到task运行节点的工作目录
        
        //将产品表的文件缓存到task工作节点的工作目录中去
        job.addCacheFile(new URI("file:///F:/zx/pdts.txt"));
        
        //map端join的逻辑不需要reduce阶段，设置reducetask数量为0
        job.setNumReduceTasks(0);
        
        boolean res = job.waitForCompletion(true);
        System.exit(res?0:1);
    }
    
}
