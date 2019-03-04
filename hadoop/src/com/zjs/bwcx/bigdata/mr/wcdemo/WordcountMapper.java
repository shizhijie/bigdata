package com.zjs.bwcx.bigdata.mr.wcdemo;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * KEYIN : 默认情况下，是mr程序框架所读到的一行文本的起始偏移量 Long,但是在hadoop中有自己更加精简的序列化接口， 所以不直接用Long ,而用LongWritable VALUEIN : 默认情况下 ,
 * 是mr框架所读到的一行文本的内容 String 同上，用text KEYOUT : 用户自定义逻辑处理完成之后输出数据的key ,在此处是单词 String 同上，用text VALUEOUT :
 * 用户自定义逻辑处理完成之后输出数据的中的value 在此处是次数，数字类型的，Integer or Long 同上，用IntWritable
 * 
 * @ClassName WordcountMapper
 * @Description TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @Date 2018年6月8日 下午4:38:48
 * @version 1.0.0
 */

public class WordcountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    /**
     * map阶段的业务逻辑 就卸载自定义的map()方法中
     */
    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, IntWritable>.Context context)
            throws IOException, InterruptedException {

        String line = value.toString();

        String[] words = line.split(" ");

        for (String word : words) {
            context.write(new Text(word), new IntWritable(1));
        }
    }

}
