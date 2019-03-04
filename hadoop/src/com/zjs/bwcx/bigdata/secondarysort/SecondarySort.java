package com.zjs.bwcx.bigdata.secondarysort;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * @ClassName:  SecondarySort   
 * @Description:TODO(这里用一句话描述这个类的作用)   
 * @author: zjs
 * @date: 2019年3月4日 下午5:24:31     
 * @Copyright: 2019 com.zjs.bwcx All rights reserved.
 */
public class SecondarySort {
	
	static class SecondarySortMapper extends Mapper<LongWritable, Text, OrderBean, NullWritable>{
		
		OrderBean bean = new OrderBean();
		
		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

			String line = value.toString();
			String[] fields = StringUtils.split(line, ",");
			
			bean.set(new Text(fields[0]), new DoubleWritable(Double.parseDouble(fields[2])));
			
			context.write(bean, NullWritable.get());
			
		}
		
	}
	
	static class SecondarySortReducer extends Reducer<OrderBean, NullWritable, OrderBean, NullWritable>{
		
		
		//到达reduce时，相同id的所有bean已经被看成一组，且金额最大的那个一排在第一位
		@Override
		protected void reduce(OrderBean key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
			context.write(key, NullWritable.get());
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);
		
		job.setJarByClass(SecondarySort.class);
		
		job.setMapperClass(SecondarySortMapper.class);
		job.setReducerClass(SecondarySortReducer.class);
		
		
		job.setOutputKeyClass(OrderBean.class);
		job.setOutputValueClass(NullWritable.class);
		
		FileInputFormat.setInputPaths(job, new Path("F:/zx/wordcount/gpinput"));
		FileOutputFormat.setOutputPath(job, new Path("F:/zx/wordcount/gpoutput"));
		
		//在此设置自定义的Groupingcomparator类 
		job.setGroupingComparatorClass(ItemidGroupingComparator.class);
		//在此设置自定义的partitioner类
		job.setPartitionerClass(ItemIdPartitioner.class);
		
		job.setNumReduceTasks(2);
		
		job.waitForCompletion(true);
		
	}

}
