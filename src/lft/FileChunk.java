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
public class FileChunk implements Serializable {

    private static final long serialVersionUID = 1L;
    private int chunkId;
    private long startPosition;
    private int length;
    private byte[] data;
    private String fileName;
    private long totalFileSize;

    public FileChunk(int chunkId, long startPosition, int length, byte[] data, String fileName, long totalFileSize) {
        this.chunkId = chunkId;
        this.startPosition = startPosition;
        this.length = length;
        this.data = data;
        this.fileName = fileName;
        this.totalFileSize = totalFileSize;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getChunkId() {
        return chunkId;
    }

    public long getStartPosition() {
        return startPosition;
    }

    public int getLength() {
        return length;
    }

    public byte[] getData() {
        return data;
    }

    public String getFileName() {
        return fileName;
    }

    public long getTotalFileSize() {
        return totalFileSize;
    }

    public void setChunkId(int chunkId) {
        this.chunkId = chunkId;
    }

    public void setStartPosition(long startPosition) {
        this.startPosition = startPosition;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setTotalFileSize(long totalFileSize) {
        this.totalFileSize = totalFileSize;
    }

}
