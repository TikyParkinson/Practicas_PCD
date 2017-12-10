/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica8;

import static java.lang.Math.abs;
import static java.lang.Thread.sleep;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author almu
 */
public class Bus extends Thread {

    private int id;
    private CanvasParking cv;
    private ReentrantLock[] RLock;
    Queue<Integer> BusQueue;
    Condition empty;

    public Bus(int id, CanvasParking cv, ReentrantLock[] RL, Queue<Integer> BusQueue) {
        this.id = id;
        this.cv = cv;
        RLock = RL;
        this.BusQueue = BusQueue;
        //empty = RLock.newCondition();
    }

    public void run() {
        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());
        try {
            cv.inserta(2, id);
            BusQueue.offer(id);
            sleep(abs(rand.nextInt()%3000) + 1000);
            
            Boolean free1 = false;
            Boolean free2 = false;
            
            while (!free1 || !free2) {
                free1 = RLock[0].tryLock();
                free2 = RLock[1].tryLock();
                
                if(!free1 && free2){
                    RLock[1].unlock();
                    free2 = false;
                }
                else if(free1 && !free2){
                    RLock[0].unlock();
                    free1 = false;
                }
            }
            
            try {
                cv.quita(2, id);
                sleep(1000);
                cv.aparcabus(id);
                sleep(abs(rand.nextInt() % 3000));
                BusQueue.remove(id);
                
            } finally {
                cv.salebus();
                RLock[0].unlock();
                RLock[1].unlock();
            }
            
        }   catch (InterruptedException ex) {
            Logger.getLogger(Bus.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
