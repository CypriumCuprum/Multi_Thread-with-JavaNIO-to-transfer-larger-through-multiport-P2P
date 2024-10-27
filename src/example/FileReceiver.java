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
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileReceiver {

    private static final int PART_SIZE = 1024 * 1024; // Kích thước từng phần (bytes)

    public static void main(String[] args) {
        int[] ports = {5001, 5002, 5003, 5004}; // Các cổng khác nhau
        long[] positions = {0, PART_SIZE, PART_SIZE * 2, PART_SIZE * 3}; // Các vị trí tương ứng để ghi vào file
        System.out.println(ports.length);

        /*
        Phía nhận đang hoạt động theo kiểu mỗi luồng port sẽ write vào file tới
        1 ví trị nhất định trong file (position), position sẽ được cài đặt vào
        fileChannel phía sau để viết (hiện đang fix cứng số port với từng vị trí
        trong file với nhau)
         */
        ExecutorService executor = Executors.newFixedThreadPool(ports.length); // Tạo pool với số lượng luồng bằng số lượng cổng

        for (int i = 0; i < ports.length; i++) {
            final int port = ports[i];
            final long position = positions[i];
            executor.submit(() -> startServer(port, position));
        }

        executor.shutdown(); // Ngừng nhận thêm công việc
        /*
        Cần giải quyết sao cho một luồng dựa vào từng cái socket mà client gửi
        có thể biết được luôn vị trí cần write vào file
         */
    }

    private static void startServer(int port, long position) {
        try (ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
            serverChannel.bind(new java.net.InetSocketAddress(port));
            System.out.println("Server is listening on port: " + port);

            while (true) {
                SocketChannel clientChannel = serverChannel.accept(); // Chấp nhận kết nối
                System.out.println("Accepted connection from: " + clientChannel.getRemoteAddress());
                receiveFilePart(clientChannel, position);
                clientChannel.close(); // Đóng kết nối
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void receiveFilePart(SocketChannel socketChannel, long position) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(PART_SIZE);
            Path outputPath = Path.of("server_storage\\received_file.txt");
            OpenOption[] options = {StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING};

            // Mở FileChannel để ghi dữ liệu
            try (FileChannel fileChannel = FileChannel.open(outputPath, options)) {
                // Đặt vị trí ghi
                fileChannel.position(position);

                int bytesRead;
                while ((bytesRead = socketChannel.read(buffer)) > 0) {
                    buffer.flip(); // Đặt buffer về vị trí đọc
                    fileChannel.write(buffer); // Ghi vào file
                    buffer.clear(); // Xóa buffer để chuẩn bị cho lần đọc tiếp theo
                }
                System.out.println("Received and wrote part from: " + socketChannel.getRemoteAddress());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
