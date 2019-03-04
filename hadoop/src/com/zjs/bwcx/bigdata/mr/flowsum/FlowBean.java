package com.zjs.bwcx.bigdata.mr.flowsum;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class FlowBean implements WritableComparable<FlowBean>{
    
    private long upFlow;
    private long dFlow;
    
    private long sumFlow;
    
    
    //反序列化时  需要反射调用空参构造函数  所以要显示定义一个
    public FlowBean() {}

    


    public FlowBean(long upFlow, long dFlow) {
        this.upFlow = upFlow;
        this.dFlow = dFlow;
        this.sumFlow = upFlow + dFlow;
    }
    
    public void set(long upFlow, long dFlow) {
        this.upFlow = upFlow;
        this.dFlow = dFlow;
        this.sumFlow = upFlow + dFlow;
    }
    

    
    public long getSumFlow() {
        return sumFlow;
    }



    
    public void setSumFlow(long sumFlow) {
        this.sumFlow = sumFlow;
    }



    public long getUpFlow() {
        return upFlow;
    }
    
    public void setUpFlow(long upFlow) {
        this.upFlow = upFlow;
    }
    
    public long getdFlow() {
        return dFlow;
    }
    
    public void setdFlow(long dFlow) {
        this.dFlow = dFlow;
    }
    
    //序列化方法
    @Override
    public void write(DataOutput out) throws IOException {
        
        out.writeLong(upFlow);
        out.writeLong(dFlow);
        out.writeLong(sumFlow);
        
    }
    
    
    //反序列化方法
    //注意序列化 和反序列化的顺序完全一致
    @Override
    public void readFields(DataInput in) throws IOException {
        
        this.upFlow = in.readLong();
        this.dFlow = in.readLong();
        this.sumFlow = in.readLong();
    }




    @Override
    public String toString() {
        return upFlow + "\t" + dFlow + "\t" + sumFlow;
    }




    @Override
    public int compareTo(FlowBean o) {
        return this.sumFlow>o.getSumFlow()?-1:1;
    }
    
    
}
