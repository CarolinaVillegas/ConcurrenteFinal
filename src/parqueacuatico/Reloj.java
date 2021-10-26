package parqueacuatico;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Reloj implements Runnable {

    private LinkedBlockingQueue<Gomon> listaGomones = new LinkedBlockingQueue<Gomon>();
    private Camioneta camioneta;
    private int horaActual;
    private Lock lock = new ReentrantLock();
    private Condition dormir = lock.newCondition();
    private Condition cierreParque = lock.newCondition();

    public Reloj() {
        horaActual = 8;
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(5000);
                lock.lock();
                if (horaActual == 23) {
                    horaActual = 0;
                } else {
                    horaActual++;
                }
                if (horaActual >= 9 && horaActual <= 18) {
                    dormir.signalAll();
                } else {
                    cierreParque.signalAll();
                    camioneta.avisarCierreParque();
                    while (!listaGomones.isEmpty()) {
                        listaGomones.poll().avisarCierreParque();
                    }
                }
            } catch (InterruptedException ex) {
            } finally {
                lock.unlock();
            }
            System.out.println("______________________-----------------____________________");
            System.out.println("===========================================================================");
            System.out.println("====================== HORA ACTUAL: " + horaActual + " hs =====================");
            System.out.println("===========================================================================");
            System.out.println("______________________-----------------____________________");
        }

    }

    public int getHoraActual() {
        return horaActual;
    }

    public int esperarUnaHora() {
        try {
            lock.lock();
            dormir.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return horaActual;
    }

    public int utilizarTiempoEvento() {
        /*La diferencia entre este metodo y esperar una hora es que este metodo espera un tiempo, 
		pero si se pasa de las 18, se despierta y sale*/
        try {
            lock.lock();
            cierreParque.await(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return horaActual;
    }

    public boolean elParqueEstaAbierto() {
        /*
		 * Cuidado que no toma en cuenta que a las 17 no pueden subirse mas a los juegos
         */
        return (this.horaActual >= 9 && this.horaActual < 18);
    }

    public void setCamioneta(Camioneta unaCamioneta) {
        this.camioneta = unaCamioneta;
    }

    public void agregarGomon(Gomon unGomon) {
        this.listaGomones.add(unGomon);
    }

}
