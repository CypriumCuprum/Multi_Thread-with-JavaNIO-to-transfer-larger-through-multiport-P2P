/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lft;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Cuprum
 */
public class FileTransferUtils {

    public static void sendChunk(SocketChannel channel, File file, int chunkId, int chunkSize) throws IOException {
        System.out.println("Chunk:" + chunkId);
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        FileChannel fileChannel = raf.getChannel();

        long position = (long) chunkId * chunkSize;
        long remaining = file.length() - position;
        int length = (int) Math.min(chunkSize, remaining);

        ByteBuffer buffer = ByteBuffer.allocate(length);
        fileChannel.read(buffer, position);
        buffer.flip();

        // Gửi chunk header
        ByteBuffer headerBuffer = ByteBuffer.allocate(8);
        headerBuffer.putInt(chunkId);
        headerBuffer.putInt(length);
        headerBuffer.flip();
        channel.write(headerBuffer);

        // Gửi chunk data
        channel.write(buffer);
        System.out.println("Send ok: " + chunkId);

    }

    public static void receiveChunk(SocketChannel channel, FileChannel fileChannel, int chunkId, int chunkSize) throws IOException {
        // Đọc chunk header
        System.out.println("Chunk:" + chunkId);
        ByteBuffer headerBuffer = ByteBuffer.allocate(8);
        channel.read(headerBuffer);
        headerBuffer.flip();
        int chunkIdReceived = headerBuffer.getInt();
        int length = headerBuffer.getInt();

        // Đọc chunk data
        ByteBuffer buffer = ByteBuffer.allocate(length);
        channel.read(buffer);
        buffer.flip();

        // Lưu chunk vào file
        long position = (long) chunkIdReceived * chunkSize;
        fileChannel.position(position);
        fileChannel.write(buffer);

        System.out.println("Receive ok: " + chunkId);
    }

    public static void sendMetadata(SocketChannel channel, FileMetadata metadata) throws IOException {
        // Gửi metadata
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.putLong(metadata.getFileSize());
        buffer.put(metadata.getFileName().getBytes());
        buffer.flip();
        channel.write(buffer);
//        buffer.clear();
    }

    public static FileMetadata receiveMetadata(SocketChannel channel) throws IOException {
        // Nhận metadata
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        channel.read(buffer);
        buffer.flip();
        long fileSize = buffer.getLong();
        String fileName = StandardCharsets.UTF_8.decode(buffer).toString();
//        buffer.clear();
        return new FileMetadata(fileName, fileSize);
    }

    public static void sendCommand(SocketChannel channel, String command) throws IOException {
        // Gửi command
        ByteBuffer buffer = ByteBuffer.wrap(command.getBytes(StandardCharsets.UTF_8));
        System.out.println(command);
        System.out.println(buffer.remaining());
//        buffer.flip();
        channel.write(buffer);
//        buffer.clear();
    }

    public static String readCommand(SocketChannel channel) throws IOException {
        // Đọc command
        ByteBuffer buffer = ByteBuffer.allocate(1024);
//        buffer.flip();
        channel.read(buffer);
        buffer.flip();
        String rs = StandardCharsets.UTF_8.decode(buffer).toString();
//        buffer.clear();
        System.out.println(rs);
        return rs;
    }

    public static void sendFileName(SocketChannel channel, String fileName) throws IOException {
        // Gửi tên file
        ByteBuffer buffer = ByteBuffer.wrap(fileName.getBytes(StandardCharsets.UTF_8));
        buffer.put(fileName.getBytes());
        buffer.flip();
        channel.write(buffer);
//        buffer.clear();
    }

    public static String readFileName(SocketChannel channel) throws IOException {
        // Đọc tên file
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        channel.read(buffer);
        buffer.flip();
        String rs = StandardCharsets.UTF_8.decode(buffer).toString();
//        buffer.clear();
        return rs;
    }
}
