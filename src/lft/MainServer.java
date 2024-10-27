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
public class MainServer {

    public static void main(String[] args) {
        try {
            FileServer server = new FileServer();
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
