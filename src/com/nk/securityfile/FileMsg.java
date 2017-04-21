package com.nk.securityfile;

import java.io.File;

/**
 * Created by zhangyuyang1 on 2017/4/1.
 */
public class FileMsg {
    private String filePath;
    private String relativePath;
    private String fileName;
    private Integer skip;
    private boolean directory;

    public FileMsg() {
    }

    public FileMsg(String filePath) {
        if (filePath == null || filePath.isEmpty())
            throw new RuntimeException("file path error");
        filePath = filePath.replaceAll("/", "\\");
        this.filePath = filePath;
        this.fileName = filePath.substring(filePath.lastIndexOf("\\") + 2, filePath.length());
    }

    public FileMsg(String filePath, Integer skip) {
        if (filePath == null || filePath.isEmpty())
            throw new RuntimeException("file path error");
        filePath = filePath.replaceAll("/", "\\");
        this.filePath = filePath;
        this.fileName = filePath.substring(filePath.lastIndexOf("\\") + 2, filePath.length());
        this.skip = skip;
    }

    public FileMsg(String rootPath, File file, Integer skip) {
        String filePath = file.getAbsolutePath();
        if (filePath == null || filePath.isEmpty() || !filePath.startsWith(rootPath))
            throw new RuntimeException("file path error");
        filePath = filePath.replaceAll("/", "\\\\");
        rootPath = rootPath.replaceAll("/", "\\\\");
        this.filePath = filePath;
        this.fileName = filePath.substring(filePath.lastIndexOf("\\") + 1, filePath.length());
        this.relativePath = filePath.substring(rootPath.length() + 1);
        this.skip = skip;
        this.directory = file.isDirectory();
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getSkip() {
        return skip;
    }

    public void setSkip(Integer skip) {
        this.skip = skip;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }
}
