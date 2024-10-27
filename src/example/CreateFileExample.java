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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateFileExample {

    public static void main(String[] args) {
        // Đường dẫn đến thư mục server_storage
        Path directoryPath = Paths.get("server_storage");
        // Đường dẫn đến file trong thư mục server_storage
        Path filePath = directoryPath.resolve("test_text.txt");

        try {
            // Tạo thư mục nếu nó chưa tồn tại
            if (Files.notExists(directoryPath)) {
                Files.createDirectories(directoryPath);
                System.out.println("Create directory: " + directoryPath);
            } else {
                System.out.println("Directory is exist: " + directoryPath);
            }

            // Tạo file nếu nó chưa tồn tại
            if (Files.notExists(filePath)) {
                // Viết các ký tự bất kỳ vào file
                String randomChars = generateRandomCharacters(3000000); // Ví dụ: tạo 100 ký tự ngẫu nhiên
                Files.write(filePath, randomChars.getBytes(StandardCharsets.UTF_8));
                System.out.println("File " + filePath + " is created and write to.");
            } else {
                String randomChars = generateRandomCharacters(3000000); // Ví dụ: tạo 100 ký tự ngẫu nhiên
                System.out.println("File is exist" + filePath);
                Files.write(filePath, randomChars.getBytes(StandardCharsets.UTF_8));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Hàm để tạo chuỗi ký tự ngẫu nhiên
    private static String generateRandomCharacters(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char randomChar = (char) ('a' + Math.random() * 26); // Ký tự ngẫu nhiên từ 'a' đến 'z'
            sb.append(randomChar);
        }
        return sb.toString();
    }
}
