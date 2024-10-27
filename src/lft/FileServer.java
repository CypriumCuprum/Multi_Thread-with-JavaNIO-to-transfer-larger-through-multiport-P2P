/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lft;

import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Cuprum
 */
public class FileServer {

    private static final int PORT = 9999;
    private static final int CHUNK_SIZE = 1024 * 1024; // 1MB chunks
    private static final String STORAGE_DIR = "D:\\CUPRUM\\PTIT\\Term 7\\Network Programming\\LFT\\server_storage\\";

    public void start() throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(PORT));
        System.out.println("Server started on port " + PORT);

        while (true) {
            SocketChannel clientChannel = serverChannel.accept();
            new Thread(() -> handleClient(clientChannel)).start();
        }
    }

    private void handleClient(SocketChannel clientChannel) {
        try {
//            ByteBuffer buffer = ByteBuffer.allocate(8192);
            // Đọc command từ client (UPLOAD/DOWNLOAD)
            String command = FileTransferUtils.readCommand(clientChannel);
            System.out.println("command:" + command);
            FileTransferUtils.sendCommand(clientChannel, "200");

            if ("UPLOAD".equals(command)) {
                handleFileUpload(clientChannel);
            } else if ("DOWNLOAD".equals(command)) {
                handleFileDownload(clientChannel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleFileUpload(SocketChannel clientChannel) throws IOException {
        // Nhận metadata của file
        System.out.println("Start handle file upload.");
        FileMetadata metadata = FileTransferUtils.receiveMetadata(clientChannel);
        String fileName = metadata.getFileName();
        long fileSize = metadata.getFileSize();
        int totalChunks = (int) Math.ceil(fileSize / (double) CHUNK_SIZE);
        System.out.println(fileName);
        // Tạo ExecutorService để xử lý các chunk song song
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Semaphore semaphore = new Semaphore(5);  // Giới hạn số luồng nhận đồng thời
        CompletableFuture<?>[] futures = new CompletableFuture[totalChunks];
        Path pathfile = Paths.get(STORAGE_DIR + fileName);
//        RandomAccessFile file = new RandomAccessFile(STORAGE_DIR + fileName, "rw");
        FileChannel fileChannel = FileChannel.open(pathfile, EnumSet.of(StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE));

//        for (int i = 0; i < totalChunks; i++) {
//            final int chunkId = i;
//            futures[i] = CompletableFuture.runAsync(() -> {
//                try {
//                    FileTransferUtils.receiveChunk(clientChannel, fileChannel, chunkId, CHUNK_SIZE);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }, executor);
//        }
        for (int i = 0; i < totalChunks; i++) {
            final int chunkId = i;

            futures[i] = CompletableFuture.runAsync(() -> {
                try {
                    semaphore.acquire();  // Giới hạn luồng nhận đồng thời
                    FileTransferUtils.receiveChunk(clientChannel, fileChannel, chunkId, CHUNK_SIZE);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();  // Giải phóng khi hoàn tất
                }
            }, executor);
        }
        // Đợi tất cả chunk được nhận
        CompletableFuture.allOf(futures).join();
        fileChannel.close();
        executor.shutdown();
    }

    private void handleFileDownload(SocketChannel clientChannel) throws IOException {
        // Đọc tên file từ client
        String fileName = FileTransferUtils.readFileName(clientChannel);
        File file = new File(STORAGE_DIR + fileName);

        if (!file.exists()) {
            System.out.println("File is existed!");
            return;
        }

        // Gửi metadata
        FileMetadata metadata = new FileMetadata(fileName, file.length());
        FileTransferUtils.sendMetadata(clientChannel, metadata);

        // Chia file thành chunks và gửi
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        long fileSize = file.length();
        int totalChunks = (int) Math.ceil(fileSize / (double) CHUNK_SIZE);

        for (int i = 0; i < totalChunks; i++) {
            final int chunkId = i;
            executor.submit(() -> {
                try {
                    FileTransferUtils.sendChunk(clientChannel, file, chunkId, CHUNK_SIZE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
    }
}
