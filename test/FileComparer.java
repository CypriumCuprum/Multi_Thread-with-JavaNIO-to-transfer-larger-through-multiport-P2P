/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Cuprum
 */
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileComparer {

    public static void main(String[] args) {
        String filePath1 = "D:\\CUPRUM\\PTIT\\Term 7\\Network Programming\\LFT\\server_storage\\received_file.txt";
        String filePath2 = "D:\\CUPRUM\\PTIT\\Term 7\\Network Programming\\LFT\\server_storage\\test_text.txt";
        try {
            boolean areEqual = compareFiles(filePath1, filePath2);
            if (areEqual) {
                System.out.println("2 files are same.");
            } else {
                System.out.println("2 files are different.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean compareFiles(String path1, String path2) throws IOException {
        Path file1 = Paths.get(path1);
        Path file2 = Paths.get(path2);

        // So sánh kích thước file
        if (Files.size(file1) != Files.size(file2)) {
            return false;
        }

        // So sánh nội dung của file
        byte[] file1Bytes = Files.readAllBytes(file1);
        byte[] file2Bytes = Files.readAllBytes(file2);

        for (int i = 0; i < file1Bytes.length; i++) {
            if (file1Bytes[i] != file2Bytes[i]) {
                return false; // Nếu phát hiện sự khác biệt
            }
        }

        return true; // Hai file giống nhau
    }
}
