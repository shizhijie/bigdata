package com.zjs.bwcx.bigdata.mr.rjoin;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class RJoin {

    public static void main(String[] args) throws Exception {

        Configuration configuration = new Configuration();

        Job instance = Job.getInstance(configuration);

        instance.setJar("F:/zx/jar/rjoin.jar");

        // 指定业务job要使用的mapper/Reducer业务类
        instance.setMapperClass(RJoinMapper.class);
        instance.setReducerClass(RJoinReducer.class);

        // 指定mapper输出数据的kv类型
        instance.setMapOutputKeyClass(Text.class);
        instance.setMapOutputValueClass(InfoBean.class);

        // 指定最终输出数据的kv类型
        instance.setOutputKeyClass(InfoBean.class);
        instance.setOutputValueClass(NullWritable.class);

        // 指定instance的输入原始文件所在目录
        FileInputFormat.setInputPaths(instance, new Path(args[0]));
        // 指定instance的输出结果文件所在目录
        FileOutputFormat.setOutputPath(instance, new Path(args[1]));

        // 将instance中配置的相关参数，以及instance所用的java类所在的jar ,提交给yarn 去运行
        // instance.submit();
        boolean res = instance.waitForCompletion(true);
        System.exit(res ? 0 : 1);

    }

    static class RJoinMapper extends Mapper<LongWritable, Text, Text, InfoBean> {

        InfoBean bean = new InfoBean();
        Text k = new Text();

        @Override
        protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, InfoBean>.Context context)
                throws IOException, InterruptedException {

            String line = value.toString();

            FileSplit inputSplit = (FileSplit) context.getInputSplit();
            String name = inputSplit.getPath().getName();

            String pid = "";

            // 通过文件名确定是那种数据
            if (name.startsWith("order")) {

                String[] fields = line.split(",");

                pid = fields[2];

                bean.set(Integer.parseInt(fields[0]), fields[1], pid, Integer.parseInt(fields[3]), "", 0, 0, "0");

            } else {

                String[] fields = line.split(",");
                pid = fields[0];
                bean.set(0, "", pid, 0, fields[1], Integer.parseInt(fields[2]), Float.parseFloat(fields[2]), "1");

            }
            k.set(pid);
            context.write(k, bean);
        }

    }

    static class RJoinReducer extends Reducer<Text, InfoBean, InfoBean, NullWritable> {

        InfoBean pdBean = new InfoBean();
        java.util.ArrayList<InfoBean> orderBeans = new ArrayList<InfoBean>();

        @Override
        protected void reduce(Text pid, Iterable<InfoBean> beans, Context context)
                throws IOException, InterruptedException {

            for (InfoBean bean : beans) {

                if ("1".equals(bean.getFlag())) {
                    try {
                        BeanUtils.copyProperties(pdBean, bean);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                    InfoBean odBean = new InfoBean();

                    try {

                        BeanUtils.copyProperties(odBean, bean);
                        orderBeans.add(odBean);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            // 拼接两类数据形成最终数据
            for (InfoBean bean : orderBeans) {

                bean.setPname(pdBean.getPname());
                bean.setCategory_id(pdBean.getCategory_id());
                bean.setPrice(pdBean.getPrice());

                context.write(bean, NullWritable.get());
            }

        }

    }
}
