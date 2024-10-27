/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package example;

/**
 *
 * @author Cuprum
 */
public class Run implements Runnable {

    int id;

    public Run(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("Process is running: " + id);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Process is finished: " + id);
    }
}
