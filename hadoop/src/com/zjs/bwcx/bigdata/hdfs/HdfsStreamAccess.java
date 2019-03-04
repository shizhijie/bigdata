package com.zjs.bwcx.bigdata.hdfs;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;

/**
 * @ClassName HdfsStreamAccess
 * @Description TODO(这里用一句话描述这个类的作用) 用流的方式操作hdfs上的文件  可以实现读取指定偏移量范围的数据
 * @author zjs
 * @Date 2018年6月5日 下午4:48:23
 * @version 1.0.0
 */
public class HdfsStreamAccess {
    
    
    FileSystem fileSystem = null;
    Configuration configuration = null;
    
    @Before
    public void init() throws Exception{
        
        configuration = new Configuration();
        
        //configuration.set("fs.defaultFS", "hdfs://mini1:9000");
        fileSystem = FileSystem.get(new URI("hdfs://mini1:9000"), configuration, "hadoop");
        
    }
    
    /**
     * 
     * @Description (TODO这里用一句话描述这个方法的作用)通过流的方式上传文件到dfs
     * @throws Exception
     */
    @Test
    public void testUpload() throws Exception{
        
        FSDataOutputStream create = fileSystem.create(new Path("/angelababy.love"), true);
        
        FileInputStream fileInputStream = new FileInputStream("c:/angelababy.love");
        IOUtils.copy(fileInputStream, create);
    }
    
    /**
     * 
     * @Description (TODO这里用一句话描述这个方法的作用)用流 的方式下载hdfs上的数据
     * @throws Exception
     */
    @Test
    public void testDownload() throws Exception{
        FSDataInputStream open = fileSystem.open(new Path("/angelababy.love"));
        FileOutputStream fos = new FileOutputStream("e:/angelababy.love");
        IOUtils.copy(open, fos);
    }
    
    /**
     * 
     * @Description (TODO这里用一句话描述这个方法的作用)
     * @throws Exception
     */
    @Test
    public void testRandomAccess() throws Exception{
        
        FSDataInputStream open = fileSystem.open(new Path("/angelababy.love"));
        //open.seek(12);
        //FileOutputStream fos = new FileOutputStream("e:/angelababy2.love");
        FileOutputStream fos = new FileOutputStream("e:/angelababy4.love");
        byte[] b = new byte[1024];
        
        int len = 0;
        
        while ((len=open.read(b))!=-1) {
            //type type = (type) en.nextElement();
            fos.write(b, 0, len);
            fos.flush();
        }
        fos.close();
        open.close();
        //IOUtils.copy(open, fos);
        //IOUtils.copy(input, output)
    }
    
    /**
     * 
     * @Description (TODO这里用一句话描述这个方法的作用)显示hdfs上文件的内容
     * @throws Exception
     */
    @Test
    public void testCat() throws Exception{
        FSDataInputStream in = fileSystem.open(new Path("/angelababy.love"));
        //IOUtils.copy(in, System.out);
        org.apache.hadoop.io.IOUtils.copyBytes(in, System.out, 1024);
    }
}
