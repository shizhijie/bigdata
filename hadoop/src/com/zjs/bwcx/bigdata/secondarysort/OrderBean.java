package com.zjs.bwcx.bigdata.secondarysort;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

/**
 * @ClassName:  OrderBean   
 * @Description:TODO(这里用一句话描述这个类的作用)   
 * @author: zjs
 * @date: 2019年3月4日 下午5:25:05     
 * @Copyright: 2019 com.zjs.bwcx All rights reserved.
 */
public class OrderBean implements WritableComparable<OrderBean>{

	private Text itemid;
	private DoubleWritable amount;

	public OrderBean() {
	}

	public OrderBean(Text itemid, DoubleWritable amount) {
		set(itemid, amount);

	}

	public void set(Text itemid, DoubleWritable amount) {

		this.itemid = itemid;
		this.amount = amount;

	}



	public Text getItemid() {
		return itemid;
	}

	public DoubleWritable getAmount() {
		return amount;
	}



	@Override
	public int compareTo(OrderBean o) {
		int cmp = this.itemid.compareTo(o.getItemid());
		if (cmp == 0) {
			cmp = -this.amount.compareTo(o.getAmount());
		}
		return cmp;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(itemid.toString());
		out.writeDouble(amount.get());
		
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		String readUTF = in.readUTF();
		double readDouble = in.readDouble();
		
		this.itemid = new Text(readUTF);
		this.amount= new DoubleWritable(readDouble);
	}


	@Override
	public String toString() {

		return itemid.toString() + "\t" + amount.get();
		
	}

}
