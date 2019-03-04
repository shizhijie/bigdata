package com.zjs.bwcx.bigdata.mr.provinceflow;

import java.util.HashMap;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

import com.zjs.bwcx.bigdata.mr.flowsum.FlowBean;

/**
 * K2 V2 队应的是map输出的类型
 * 
 * @ClassName ProvincePartitioner
 * @Description TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @Date 2018年6月12日 上午10:51:57
 * @version 1.0.0
 */

public class ProvincePartitioner extends Partitioner<Text, FlowBean>{
    
    public static HashMap<String, Integer> proviceDict = new HashMap<>();
    static{
        proviceDict.put("136", 0);
        proviceDict.put("137", 1);
        proviceDict.put("138", 2);
        proviceDict.put("139", 3);
    }

    @Override
    public int getPartition(Text key, FlowBean value, int numPartitions) {
        String prefix = key.toString().substring(0, 3);
        Integer proviceId = proviceDict.get(prefix);
        
        return proviceId == null ? 4 : proviceId;
    }

    

}
