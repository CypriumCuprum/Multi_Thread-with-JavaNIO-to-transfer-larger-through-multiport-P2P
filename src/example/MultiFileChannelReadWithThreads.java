/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package example;

/**
 *
 * @author Cuprum
 */
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiFileChannelReadWithThreads {

    private static final int PART_SIZE = 1024 * 1024 * 1024 * 3; // Kích thước từng phần (bytes)
    private static final int BUFFER_SIZE = 1024 * 128;

    public static void main(String[] args) {
        Path path = Path.of("server_storage\\file.txt");
        System.out.println("Absolute path: " + path.toAbsolutePath());
        // Đọc đơn giản kiểu tạo nhiều luồng để đọc file for test là
//        ExecutorService executor = Executors.newFixedThreadPool(3); // Tạo pool với 3 luồng
//
//        // Tạo và khởi động các luồng
//        for (int i = 0; i < 3; i++) {
//            final int partIndex = i;
//            executor.submit(() -> readFilePart(path, partIndex * PART_SIZE));
//        }
//
//        executor.shutdown(); // Ngừng nhận thêm công việc
//
//
        //số cổng bằng số port ( đơn giản là chia cho mỗi cổng nhận một part)
        String[] hosts = {"localhost:5001", "localhost:5002", "localhost:5003", "localhost:5004"}; // Các cổng khác nhau
        ExecutorService executor = Executors.newFixedThreadPool(hosts.length); // Tạo pool với số lượng luồng bằng số lượng cổng
        for (int i = 0; i < 1; i++) {
            final int partIndex = i;
            executor.submit(() -> sendFilePart(path, partIndex * PART_SIZE, hosts[partIndex]));
        }

        // phần này là số tạo số cổng khác với số port (ở phía gửi thì được rồi nhưng còn phía nhận)
//        String[] hosts = {"localhost:5001", "localhost:5002"}; // Sử dụng chỉ hai cổng
//        ExecutorService executor = Executors.newFixedThreadPool(4); // Tạo pool với số lượng luồng bằng số lượng cổng
//
//        for (int i = 0; i < 4; i++) { // Chia sẻ 4 phần cho 2 cổng
//            final int partIndex = i;
//            final String hostPort = hosts[partIndex % hosts.length]; // Luân phiên cổng
//            executor.submit(() -> sendFilePart(path, partIndex * PART_SIZE, hostPort));
//        }
        executor.shutdown(); // Ngừng nhận thêm công việc
    }

    private static void readFilePart(Path path, long position) {
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
            channel.position(position); // Đặt vị trí đọc
            ByteBuffer buffer = ByteBuffer.allocate(PART_SIZE);

            int bytesRead = channel.read(buffer);
            if (bytesRead > 0) {
                buffer.flip();
                String result = new String(buffer.array(), 0, bytesRead);
                System.out.println("Read from position " + position + ": " + result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private static void sendFilePart(Path path, long position, String hostPort) {
//        try (SocketChannel socketChannel = SocketChannel.open(new java.net.InetSocketAddress(hostPort.split(":")[0], Integer.parseInt(hostPort.split(":")[1])))) {
//            try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
//                channel.position(position); // Đặt vị trí đọc
//                ByteBuffer buffer = ByteBuffer.allocate(PART_SIZE);
//
//                int bytesRead = channel.read(buffer);
//                if (bytesRead > 0) {
//                    buffer.flip();
//                    socketChannel.write(buffer); // Gửi dữ liệu qua SocketChannel
//                    System.out.println("Sent part from position " + position + ": " + new String(buffer.array(), 0, bytesRead));
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    private static void sendFilePart(Path path, long position, String hostPort) {
        try (SocketChannel socketChannel = SocketChannel.open(new java.net.InetSocketAddress(hostPort.split(":")[0], Integer.parseInt(hostPort.split(":")[1])))) {
//            synchronized (socketChannel) { // Đồng bộ hóa trên socketChannel
            try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
                channel.position(position); // Đặt vị trí đọc
                ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                int noOfBytesRead = 0;
                long counter = 0;
                do {
                    noOfBytesRead = channel.read(buffer);
                    if (noOfBytesRead <= 0) {
                        break;
                    }
                    if (counter == PART_SIZE) {
                        break;
                    }
                    counter += noOfBytesRead;
                    buffer.flip();
                    do {
                        noOfBytesRead -= socketChannel.write(buffer);
                    } while (noOfBytesRead > 0);
                    buffer.clear();
                } while (true);
                System.out.println("Sent part from position " + position + " to " + hostPort);
//                    int bytesRead = channel.read(buffer);
//                    if (bytesRead > 0) {
//                        buffer.flip();
//                        socketChannel.write(buffer);
//                        System.out.println("Sent part from position " + position + " to " + hostPort + ": " + new String(buffer.array(), 0, bytesRead));
//                    }
            }
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
