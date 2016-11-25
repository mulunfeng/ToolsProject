package com.nk.file.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;

/**
 * Created by zhangkepeng on 15-8-24.
 */
public class FileOperate {

    private static final Logger logger = LoggerFactory.getLogger(FileOperate.class);
    /**
     * 安全的写文件
     */
    public static final void string2File(final String str, final String fileName) throws IOException {
        // 先写入临时文件
        String tmpFile = fileName + ".tmp";
        string2FileNotSafe(str, tmpFile);

        // 备份之前的文件
        String bakFile = fileName + ".bak";
        String prevContent = file2String(fileName);
        if (prevContent != null) {
            string2FileNotSafe(prevContent, bakFile);
        }

        // 删除正式文件
        File file = createFile(fileName);
        file.delete();

        // 临时文件改为正式文件
        file = createFile(tmpFile);
        File formalFile = new File(fileName);
        file.renameTo(formalFile);
    }

    /**
     * 写文件
     *
     * @param str
     * @param fileName
     * @throws IOException
     */
    public static final void string2FileNotSafe(final String str, final String fileName) throws IOException {
        File file = createFile(fileName);
        FileChannel fileChannel = null;
        try{
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(0);
            fileChannel = randomAccessFile.getChannel();

            ByteBuffer byteBuffer = ByteBuffer.wrap(str.getBytes(Charset.forName("UTF-8")));
            fileChannel.write(byteBuffer);
        }catch (FileNotFoundException e) {
            throw e;
        }catch (IOException e) {
            throw e;
        }finally {
            if (fileChannel != null) {
                try {
                    fileChannel.close();
                    fileChannel = null;
                } catch (IOException e){
                    logger.error("closes fileChannel exception", e);
                }
            }
        }
    }


    /**
     * 读文件
     *
     * @param fileName
     * @return
     */
    public static final String file2String(final String fileName) {
        File file = createFile(fileName);
        return file2String(file);
    }

    public static final String file2String(final File file) {
        String result = null;
        FileChannel fileChannel = null;
        StringBuilder stringBuilder = new StringBuilder();
        int count;
        try{
            FileInputStream fileInputStream = new FileInputStream(file);
            fileChannel = fileInputStream.getChannel();

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            CharBuffer charBuffer;
            String content;
            while (true) {
                byteBuffer.clear();
                count = fileChannel.read(byteBuffer);
                if (-1 == count) {
                    break;
                }
                byteBuffer.flip();
                charBuffer = Charset.forName("UTF-8").decode(byteBuffer.asReadOnlyBuffer());

                if (charBuffer != null) {
                    content = charBuffer.toString();
                    stringBuilder.append(content);
                }
            }
            if (0 != stringBuilder.length()) {
                result = stringBuilder.toString();
            }
        } catch (IOException e){
            logger.error("read bytebuffer error",e);
        } finally {
            if (fileChannel != null) {
                try {
                    fileChannel.close();
                    fileChannel = null;
                } catch (IOException e){
                    logger.error("closes fileChannel exception", e);
                }
            }
        }
        return result;
    }

    /**
     * 创建文件
     *
     * @param destFileName
     * @return
     */
    public static File createFile(String destFileName) {
        File file = new File(destFileName);
        if(file.exists()) {
            return file;
        }
        if (destFileName.endsWith(File.separator)) {
            return file;
        }
        //判断目标文件所在的目录是否存在
        if(!file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            if(!file.getParentFile().mkdirs()) {
                return file;
            }
        }
        //创建目标文件
        try {
            if (file.createNewFile()) {
                return file;
            } else {
                return file;
            }
        } catch (IOException e) {
            logger.error("creates new file exception", e);
            return file;
        }
    }

    public static void lockFile(String path) throws Exception{
        FileChannel channel = null;
        FileLock lock = null;
        createFile(path);
        try {
            RandomAccessFile raf = new RandomAccessFile(path,"rw");
            raf.seek(raf.length());//raf在文件末尾追加内容的处理
            channel = raf.getChannel();
            lock = channel.tryLock();
            if (lock == null) {
                throw new Exception("file has been locked");
            }
        } catch (FileNotFoundException e) {
            logger.error("file not found exception", e);
        } catch (IOException e) {
            logger.error("IO exception", e);
        }
    }

}
