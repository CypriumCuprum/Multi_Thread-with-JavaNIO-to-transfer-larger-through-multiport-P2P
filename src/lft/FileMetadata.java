/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lft;

import java.io.*;

/**
 *
 * @author Cuprum
 */
public class FileMetadata implements Serializable {

    private static final long serialVersionUID = 1L;
    private String fileName;
    private long fileSize;

    public FileMetadata(String fileName, long fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }
}
