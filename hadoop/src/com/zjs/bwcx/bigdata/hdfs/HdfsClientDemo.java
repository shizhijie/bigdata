package com.zjs.bwcx.bigdata.hdfs;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.junit.Before;
import org.junit.Test;

public class HdfsClientDemo {
    
    FileSystem fileSystem = null;
    Configuration configuration = null;
    
    @Before
    public void init() throws Exception{
        
        configuration = new Configuration();
        
        //configuration.set("fs.defaultFS", "hdfs://mini1:9000");
        fileSystem = FileSystem.get(new URI("hdfs://mini1:9000"), configuration, "hadoop");
        
    }
    
    @Test
    public void TestConf(){
        
        Iterator<Entry<String, String>> it = configuration.iterator();
        
        while (it.hasNext()) {
            
            Entry<String, String> en = it.next();
            
            System.out.println(en.getKey() + " : " + en.getValue());
            
        }
        
    }
    
    /**
     * 
     * @Description (TODO这里用一句话描述这个方法的作用)创建文件夹
     * @throws Exception
     */
    @Test
    public void testMkdir() throws Exception{
        boolean mkdirs = fileSystem.mkdirs(new Path("/testmkdir/aaa/bbb"));
        System.out.println(mkdirs);
    }
    
    /**
     * 
     * @Description (TODO这里用一句话描述这个方法的作用)删除文件夹
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception{
        
        boolean delete = fileSystem.delete(new Path("/testmkdir/aaa"), true);
        System.out.println(delete);
    }
    
    /**
     * 
     * @Description (TODO这里用一句话描述这个方法的作用) 递归查看指定目录下的文件的信息
     * @throws Exception
     */
    @Test
    public void testLs() throws Exception{
        
        RemoteIterator<LocatedFileStatus> listFiles = fileSystem.listFiles(new Path("/"), true);
        
        while (listFiles.hasNext()) {
            LocatedFileStatus next = listFiles.next();
            
            System.out.println("getBlockSize "+next.getBlockSize());
            System.out.println("next.getOwner() "+next.getOwner());
            System.out.println("next.getReplication() "+next.getReplication());
            System.out.println("next.getPermission() "+next.getPermission());
            System.out.println("next.getPath().getName() "+next.getPath().getName());
            System.out.println("--------------------------------");
            
            BlockLocation[] blockLocations = next.getBlockLocations();
            
            for(BlockLocation bl:blockLocations){
                System.out.println("bl.getLength()"+bl.getLength());
                System.out.println("起始偏移量"+bl.getOffset());
                //System.out.println("bl.getCachedHosts()"+bl.getHosts());
                String[] hosts = bl.getHosts();
                for(String s:hosts){
                    System.out.println("datanodes:"+s);
                }
                
                String[] names = bl.getNames();
                
                for(String s:names){
                    System.out.println("name:"+s);
                }
                
            }
            
        }
        
    }
    
    /**
     * 
     * @Description (TODO这里用一句话描述这个方法的作用) no递归列车指定目录下的文件信息
     * @throws Exception
     */
    @Test
    public void testLs2() throws Exception{
        FileStatus[] listStatus = fileSystem.listStatus(new Path("/"));
        for (FileStatus next:listStatus) {
            System.out.println("getBlockSize "+next.getBlockSize());
            System.out.println("next.getOwner() "+next.getOwner());
            System.out.println("next.getReplication() "+next.getReplication());
            System.out.println("next.getPermission() "+next.getPermission());
            System.out.println("next.getPath().getName() "+next.getPath().getName());
            System.out.println(next.isFile()?"file":"directory");
            System.out.println("--------------------------------");
        }
    }
    
    /**
     * 
     * @Description (TODO这里用一句话描述这个方法的作用)上传文件
     * @throws Exception
     */
    @Test
    public void testUpload() throws Exception{
        fileSystem.copyFromLocalFile(new Path("c:/access.log"), new Path("/access.log.copy"));
        fileSystem.close();
    }
    
    /**
     * 
     * @Description (TODO这里用一句话描述这个方法的作用)下载文件
     * @throws IOException
     */
    @Test
    public void testDownload() throws IOException{
        fileSystem.copyToLocalFile(new Path("/access.log.copy"), new Path("d:/"));
        fileSystem.close();
    }
}
