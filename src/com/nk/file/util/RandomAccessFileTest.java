package com.nk.file.util;

import com.nk.excel.util.FileUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Set;

/**
 * Created by zhangyuyang1 on 2017/3/21.
 */
public class RandomAccessFileTest {

    private final static long FILE_SIZE = 1024 * 1024 * 300;
    public static void main(String[] args) throws Exception {
        write();
//        read();
//        readFile("D:\\Program Files\\360\\360WangPan");
//        RandomAccessFile file = new RandomAccessFile("D:/360PAN", "rw");
//        file.seek(file.readShort());
//        System.out.println(file.readShort());
    }
    private static void readFile(String filePathStr) throws IOException {
        if (StringUtils.isBlank(filePathStr))
            return;
        File filePath = new File(filePathStr);
        if (!filePath.exists() || !filePath.isDirectory())
            return;

        Set<File> files = FileUtil.getFilesByPath(filePath);//获取到所有文件
        for (File file : files) {
            byte[] tempByte = FileUtil.readByte(file);
            writeFile("D:/360PAN", tempByte);
            System.out.println(file.getAbsolutePath());
        }
    }

    private static void writeFile(String path, byte[] tempByte) throws IOException {
        RandomAccessFile file = new RandomAccessFile(path, "rw");
        file.setLength(FILE_SIZE); // 预分配的文件空间
        file.close();

        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(path, "rw");
            short skip = raf.readShort();
            int totalSkip = 0;
            raf.seek(0);
            while (skip != 0){
                raf.skipBytes(skip);
                totalSkip += skip;
                skip = raf.readShort();
            }
            raf.seek(totalSkip);
            raf.write(tempByte);
            raf.seek(0);
            raf.skipBytes(totalSkip);
            System.out.println("共写入长度"+raf.readShort());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                raf.close();
            } catch (Exception e) {
            }
        }

//        new FileWriteThread(path,0,tempByte).start();

    }


    private static void read() throws IOException {
        RandomAccessFile file = new RandomAccessFile("file", "rw");
        // 以下向file文件中写数据
        file.writeInt(20);// 占4个字节
        file.writeDouble(8.236598);// 占8个字节
        file.writeUTF("这是一个UTF字符串");// 这个长度写在当前文件指针的前两个字节处，可用readShort()读取
        file.writeBoolean(true);// 占1个字节
        file.writeShort(395);// 占2个字节
        file.writeLong(2325451l);// 占8个字节
        file.writeUTF("哇哈123df中文字符解析...");
        file.writeFloat(35.5f);// 占4个字节
        file.writeChar('a');// 占2个字节

        file.seek(0);// 把文件指针位置设置到文件起始处

        // 以下从file文件中读数据，要注意文件指针的位置
        System.out.println("——————从file文件指定位置读数据——————");
        System.out.println(file.readInt());
        System.out.println(file.readDouble());
        System.out.println(file.readUTF());

        file.skipBytes(3);// 将文件指针跳过3个字节，本例中即跳过了一个boolean值和short值。
        System.out.println(file.readLong());

//        file.read
        byte[] bb = new byte[file.readShort()];
        file.read(bb);
        System.out.println(new String(bb));
//        file.skipBytes(file.readShort()); // 跳过文件中“又是一个UTF字符串”所占字节，注意readShort()方法会移动文件指针，所以不用加2。
        System.out.println(file.readFloat());

        //以下演示文件复制操作
        System.out.println("——————文件复制（从file到fileCopy）——————");
        file.seek(0);
        RandomAccessFile fileCopy=new RandomAccessFile("fileCopy","rw");
        int len=(int)file.length();//取得文件长度（字节数）
        byte[] b=new byte[len];
        file.readFully(b);
        fileCopy.write(b);
        System.out.println("复制完成！");
    }

    private static void write() throws IOException {
        // 预分配文件所占的磁盘空间，磁盘中会创建一个指定大小的文件
        RandomAccessFile raf = new RandomAccessFile("D://1.txt", "rw");
        raf.setLength(1024*1024*100); // 预分配 1M 的文件空间
        raf.close();

        // 所要写入的文件内容
        String s1 = "第一个字符串";
        String s2 = "第二个字符串1";
        String s3 = "第三个字符串11";
        String s4 = "第四个字符串111";
        String s5 = "第五个字符串1111";

        // 利用多线程同时写入一个文件
        new FileWriteThread("D://1.txt",0,"第0个字符串".getBytes()).start(); // 从文件的1024字节之后开始写入数据
        new FileWriteThread("D://1.txt",1024*1,s1.getBytes()).start(); // 从文件的1024字节之后开始写入数据
        new FileWriteThread("D://1.txt",1024*2,s2.getBytes()).start(); // 从文件的2048字节之后开始写入数据
        new FileWriteThread("D://1.txt",1024*3,s3.getBytes()).start(); // 从文件的3072字节之后开始写入数据
        new FileWriteThread("D://1.txt",1024*4,s4.getBytes()).start(); // 从文件的4096字节之后开始写入数据
        new FileWriteThread("D://1.txt",1024*5,s5.getBytes()).start(); // 从文件的5120字节之后开始写入数据
    }

    // 利用线程在文件的指定位置写入指定数据
    static class FileWriteThread extends Thread{
        private String path;
        private int skip;
        private byte[] content;

        public FileWriteThread(String path, int skip,byte[] content){
            this.path = path;
            this.skip = skip;
            this.content = content;
        }

        public void run(){
            RandomAccessFile raf = null;
            try {
                raf = new RandomAccessFile(path, "rw");
                raf.seek(skip);
                raf.write(content);
                raf.seek(0);
                System.out.println("共写入长度"+raf.readShort());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    raf.close();
                } catch (Exception e) {
                }
            }
        }
    }
}