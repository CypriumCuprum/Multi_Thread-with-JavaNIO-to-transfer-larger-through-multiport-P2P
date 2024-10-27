/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lft;

import java.io.IOException;

/**
 *
 * @author Cuprum
 */
public class ClientDemo {

    public static void main(String[] args) throws InterruptedException {
        FileClient client = new FileClient();

        // Demo upload file
        try {
            System.out.println("Starting file upload...");
            String filePath = "D:\\CUPRUM\\PTIT\\Term 7\\Network Programming\\mid-project-232052\\Large File Transfer\\Genfile\\file.txt"; // Đường dẫn đến file cần upload
            client.uploadFile(filePath);
            System.out.println("File upload completed!");

//            // Demo download file
//            System.out.println("\nStarting file download...");
//            String fileName = "file.mp4"; // Tên file cần download
//            String savePath = "D:\\CUPRUM\\PTIT\\Term 7\\Network Programming\\mid-project-232052\\Large File Transfer\\Genfile\\file.txt"; // Đường dẫn lưu file download
//            client.downloadFile(fileName, savePath);
//            System.out.println("File download completed!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
