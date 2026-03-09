package com.nk.securityfile;

import com.nk.excel.util.FileUtil;
import com.nk.excel.util.StringUtil;
import com.nk.security.BASE64;
import com.nk.security.ThreeDesUtil;
import com.nk.ticket.util.JsonUtils;
import com.nk.util.Closeables;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * Created by zhangyuyang1 on 2017/3/23.
 */
public class FileStorage {
    private final static long FILE_SIZE = 1024 * 1024 * 300;
    private final static int INT_SIZE = 4;
    private static String RELEASE_PATH = "release";
    private static final String DES_KEY = "1GE45D7J9C1N3Y5F78A0F23F56F8C0AB";//秘钥32位
    private static byte[] key = BASE64.decode(DES_KEY);

    public static File getTargetFileName(String targetFilePath) {
        if (StringUtils.isBlank(targetFilePath)) {
            throw new IllegalArgumentException("target file path must not be null!");
        }

        File target = new File(targetFilePath);
        if (!target.exists() || !target.isDirectory()) {
            throw new IllegalArgumentException("target file path is not illegal!");
        }

        Set<File> files = FileUtil.getFilesByCurrentPath(target);

        return getTargetFileName(targetFilePath, files);
    }

    private static File getTargetFileName(String targetFilePath, Set<File> files) {
        if (CollectionUtils.isEmpty(files))
            return createNewTargetFile(targetFilePath);

        List<File> list = checkFiles(files);

        if (CollectionUtils.isEmpty(list))
            return createNewTargetFile(targetFilePath);

        File[] fileNames = new File[list.size()];
        fileNames = list.toArray(fileNames);
        Arrays.sort(fileNames, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return (int) (Long.parseLong(o1.getName()) -  Long.parseLong(o2.getName()));
            }
        });

        File file = fileNames[fileNames.length - 1];//find the latest file
        if (file.length() > FILE_SIZE) {
            return createNewTargetFile(targetFilePath);//too large
        } else {
            return file;
        }
    }

    private static File createNewTargetFile(String targetFilePath) {
        File targetFile = new File(targetFilePath + "\\" + System.currentTimeMillis());
        try {
            if (targetFile.createNewFile()) {
                return targetFile;
            }else{
                throw new IOException("file create Exception");
            }
        } catch (IOException e) {
            throw new RuntimeException("file create Exception");
        }
    }

    private static List<File> checkFiles(Set<File> files) {
        List<File> list = new ArrayList<File>();
        for (File file : files) {
            String filename = file.getName();
            if (NumberUtils.isNumber(filename) && checkFilePattern(filename)) {
                list.add(file);
            }
        }
        return list;
    }

    private static boolean checkFilePattern(String filename) {
        return Long.parseLong(filename) > 1490251514112L && Long.parseLong(filename) <= System.currentTimeMillis();
    }

    public static boolean saveThreeDesFile(File file, String targetPath) throws IOException {
        File targetFile = getTargetFileName(targetPath);
        RandomAccessFile raf = new RandomAccessFile(targetFile.getAbsoluteFile(), "rw");
        System.out.println(raf.length());
        int ch1 = raf.read();
        int ch2 = raf.read();
        while (ch1 != -1 && ((ch1 << 8) + (ch2 << 0)) > 0) {
            raf.skipBytes((ch1 << 8) + (ch2 << 0));
            ch1 = raf.read();ch2 = raf.read();
        }
        raf.writeUTF(ThreeDesUtil.encrypt2HexFile(file));
        raf.close();
        return true;
    }

    public static void main(String[] args) throws IOException {
        releaseStorage("D:\\source\\data\\1492166122532");
//        storage("D:\\IdeaProjects\\ToolsProject","D:\\source\\data");
//        storage("D:\\IdeaProjects","D:\\lib");
    }

    private static boolean releaseStorage(String storageFile) {
        if (StringUtil.isEmpty(storageFile))
            throw new IllegalArgumentException("storage file is null");
        storageFile = storageFile.replaceAll("/", "\\\\");
        String storagePath = storageFile.substring(0, storageFile.lastIndexOf("\\"));
        String releasePath = initReleasePath(storagePath) + "\\";
        if(releasePath == null){
            throw new RuntimeException("init relaese path fail!");
        }

        return releaseStorage(storageFile, releasePath);
    }

    private static boolean releaseStorage(String storageFile, String releasePath) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(storageFile, "rw");
            Integer total = readInt(raf);//总长度
            System.out.println(total);
            Integer length;
            while ((length = readInt(raf)) != null) {
                byte[] bytes = new byte[length];
                raf.read(bytes);
                writeFile(bytes, releasePath, raf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (raf != null) {
                Closeables.close(raf);
            }
        }
        return true;
    }

    private static void writeFile(byte[] bytes, String releasePath, RandomAccessFile raf) throws IOException {
        FileMsg fileMsg = JsonUtils.stringToObj(new String(ByteDistube.unDisturbByte(bytes)), FileMsg.class);
        bytes = new byte[fileMsg.getSkip()];
        raf.read(bytes);
        String path = releasePath + (StringUtil.isEmpty(fileMsg.getRelativePath())
                ? String.valueOf(System.currentTimeMillis()) : fileMsg.getRelativePath());
        FileUtil.createFilePath(fileMsg.isDirectory() ? path : path.substring(0, path.lastIndexOf("\\")));
        if (fileMsg.isDirectory())
            return;

        File file = new File(path);
        file.createNewFile();
        FileUtil.writeByte(file, ByteDistube.unDisturbByte(bytes));
    }

    private static String initReleasePath(String storagePath) {
        File file = new File(storagePath + "\\" + RELEASE_PATH);
        if (file.exists())
            return file.getAbsolutePath();

        if (file.mkdirs())
            return file.getAbsolutePath();
        return null;
    }

    private static boolean storage(String filePath, String targetFilePath) {
        if (StringUtil.isEmpty(targetFilePath))
            throw new IllegalArgumentException("target file path can not be null");
        File targetFilePathFile = new File(targetFilePath);
        if (!targetFilePathFile.exists()) {
            targetFilePathFile.mkdirs();
        }

        Set<File> files = FileUtil.getFileAndPath(filePath);
        if (CollectionUtils.isEmpty(files))
            return true;

        boolean flag = true;
        for (File file : files) {
            flag = flag && storage(filePath, file, targetFilePath);
        }
        return flag;
    }

    private static boolean storage(String filePath, File file, String targetFilePath) {
        File targetFile = getTargetFileName(targetFilePath);
        return storage(filePath, file, targetFile);
    }

    private static boolean storage(String filePath, File file, File targetFile) {
        if (file == null || !file.exists())
            return false;

        byte[] bytes = file.isDirectory() ? null : FileUtil.readByte(file);
        bytes = ByteDistube.disturbByte(bytes);
        return storageByte(new FileMsg(filePath, file, bytes == null ? 0 : bytes.length), bytes, targetFile);
    }

    private static boolean storageByte(FileMsg fileMsg, byte[] bytes, File targetFile) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(targetFile.getAbsoluteFile(), "rw");
//            skipStorageFile(raf);//TODO 删除
            Integer length = readInt(raf);
            if (length == null) {
                raf.writeInt(0);
                length = 0;
            } else {
                raf.skipBytes(length);
            }
            byte[] jsonByte = ByteDistube.disturbByte(JsonUtils.objToString(fileMsg).getBytes());
            raf.writeInt(jsonByte.length);
            raf.write(jsonByte);
            length += INT_SIZE + jsonByte.length;
            if (bytes != null && bytes.length > 0) {
                raf.write(bytes);
                length += bytes.length;
            }
            raf.seek(0);
            raf.writeInt(length);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (raf != null) {
                Closeables.close(raf);
            }
        }
    }

//    private static boolean skipStorageFile(RandomAccessFile raf) throws IOException {
//        Integer length = readInt(raf);
//        if (length == null)
//            return true;
//
//        raf.skipBytes(length);
//        while ((length = readInt(raf)) != null) {
//            byte[] msgByte = new byte[length];
//            raf.read(msgByte);
//            FileMsg index = JsonUtils.stringToObj(new String(ByteDistube.unDisturbByte(msgByte)), FileMsg.class);
//            raf.skipBytes(index.getSkip());
//        }
//        return true;
//    }

    private static boolean storage(File file, String targetFilePath) {
        File targetFile = getTargetFileName(targetFilePath);
        return storage(file, targetFile);
    }

    private static boolean storage(File file, File targetFile) {
        if (file == null || !file.exists())
            return false;

        byte[] bytes = FileUtil.readByte(file);
        bytes = ByteDistube.disturbByte(bytes);
        return storageByte(new FileMsg(file.getAbsolutePath(), bytes.length), bytes, targetFile);
    }


    private static Integer readInt(RandomAccessFile raf) throws IOException {
        if (raf == null)
            return null;
        int r1 = raf.read();
        if (r1 == -1)
            return null;

        int r2 = raf.read();
        int r3 = raf.read();
        int r4 = raf.read();
        if (r2 == -1 || r3 == -1 || r4 == -1)
            return null;
        StringBuilder stringBuilder = new StringBuilder();
        if (Integer.toHexString(r1).length() < 2)
            stringBuilder.append( "0" );
        stringBuilder.append(Integer.toHexString(r1));
        if (Integer.toHexString(r2).length() < 2)
            stringBuilder.append( "0" );
        stringBuilder.append(Integer.toHexString(r2));
        if (Integer.toHexString(r3).length() < 2)
            stringBuilder.append( "0" );
        stringBuilder.append(Integer.toHexString(r3));
        if (Integer.toHexString(r4).length() < 2)
            stringBuilder.append( "0" );
        stringBuilder.append(Integer.toHexString(r4));
        return Integer.parseInt( stringBuilder.toString(), 16);
    }
}
