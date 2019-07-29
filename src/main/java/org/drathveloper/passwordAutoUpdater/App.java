package org.drathveloper.passwordAutoUpdater;

public class App 
{

    public static void main(String[] args) {
        Thread t = new Thread(new PasswordAutoUpdater());
        t.start();
        synchronized (t){
            try {
                t.wait();
            } catch(InterruptedException ex){
                System.out.println("Thread error");
            }
        }
    }
}
