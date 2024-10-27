/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lft;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Cuprum
 */
public class FileClient {

    private static final int CHUNK_SIZE = 1024 * 1024; // 1MB chunks

    public void uploadFile(String filePath) throws IOException, InterruptedException {
        // Gửi command UPLOAD
        try (SocketChannel channel = SocketChannel.open(new InetSocketAddress("localhost", 9999))) {
            // Gửi command UPLOAD
            FileTransferUtils.sendCommand(channel, "UPLOAD");
            String res = FileTransferUtils.readCommand(channel);
            if (!"200".equals(res)) {
                System.out.println("Byebye");
                return;
            }
            File file = new File(filePath);
            FileMetadata metadata = new FileMetadata(file.getName(), file.length());
            FileTransferUtils.sendMetadata(channel, metadata);

            // Chia file thành chunks và gửi
//            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            long fileSize = file.length();
            int totalChunks = (int) Math.ceil(fileSize / (double) CHUNK_SIZE);
            System.out.println(totalChunks);
//            CompletableFuture<?>[] futures = new CompletableFuture[totalChunks];
//
//            for (int i = 0; i < totalChunks; i++) {
//                final int chunkId = i;
//                futures[i] = CompletableFuture.runAsync(() -> {
//                    try {
//                        FileTransferUtils.sendChunk(channel, file, chunkId, CHUNK_SIZE);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }, executor);
//            }

            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            CompletionService<Void> completionService = new ExecutorCompletionService<>(executor);
            Semaphore semaphore = new Semaphore(10);  // Giới hạn 10 chunks đồng thời

            for (int i = 0; i < totalChunks; i++) {
                final int chunkId = i;
                semaphore.acquire();  // Chờ nếu đạt giới hạn

                completionService.submit(() -> {
                    try {
                        FileTransferUtils.sendChunk(channel, file, chunkId, CHUNK_SIZE);
                    } finally {
                        semaphore.release();
                    }
                    return null;
                });
            }

            for (int i = 0; i < totalChunks; i++) {
                completionService.take();  // Đợi đến khi tất cả các task hoàn thành
            }

//            CompletableFuture.allOf(futures).join();
            executor.shutdown();
        }
    }

    public void downloadFile(String fileName, String savePath) throws IOException {
        SocketChannel channel = SocketChannel.open(new InetSocketAddress("localhost", 9999));

        // Gửi command DOWNLOAD
        FileTransferUtils.sendCommand(channel, "DOWNLOAD");
        FileTransferUtils.sendFileName(channel, fileName);

        // Nhận metadata
        FileMetadata metadata = FileTransferUtils.receiveMetadata(channel);
        long fileSize = metadata.getFileSize();
        int totalChunks = (int) Math.ceil(fileSize / (double) CHUNK_SIZE);

        // Tạo file để lưu
        RandomAccessFile file = new RandomAccessFile(savePath, "rw");
        FileChannel fileChannel = file.getChannel();

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        CompletableFuture<?>[] futures = new CompletableFuture[totalChunks];

        for (int i = 0; i < totalChunks; i++) {
            final int chunkId = i;
            futures[i] = CompletableFuture.runAsync(() -> {
                try {
                    FileTransferUtils.receiveChunk(channel, fileChannel, chunkId, CHUNK_SIZE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, executor);
        }

        CompletableFuture.allOf(futures).join();
        executor.shutdown();
        fileChannel.close();
        file.close();
        channel.close();
    }
}
