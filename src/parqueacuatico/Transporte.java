package parqueacuatico;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Transporte {

    protected String nombreTransporte = this.getClass().toString();
    protected int cantAsientos;
    protected int cantPasajeros = 0;
    protected int cantMinimaGente = 1;
    protected boolean estaEstacion, estaDestino;
    protected Lock lock = new ReentrantLock();
    protected Condition subirse = lock.newCondition();
    protected Condition bajarse = lock.newCondition();
    protected Condition arrancar = lock.newCondition();

    public Transporte(String nro, int cantAsientosLibres) {
        this.nombreTransporte = nro;
        this.cantAsientos = cantAsientosLibres;
        estaEstacion = false;
        estaDestino = false;
    }

    public Transporte(String nro, int cantAsientosLibres, int cantMinimaGente) {
        this.nombreTransporte = nro;
        this.cantAsientos = cantAsientosLibres;
        estaEstacion = false;
        estaDestino = false;
        this.cantMinimaGente = cantMinimaGente;
    }

    public void esperarSubidaPasajeros() {//llamado desde hilo chofer

        System.out.println(nombreTransporte + " - Comienza esperarSubidaPasajero");
        lock.lock();
        estaEstacion = true;
        while (cantPasajeros < cantMinimaGente) {
            System.out.println(nombreTransporte + "  - Espero a que se suban " + cantMinimaGente + " pasajeros. PASAJEROS ACTUALES: " + cantPasajeros);
            try {
                subirse.signalAll();
                arrancar.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(nombreTransporte + " Se está yendo de la estacion");
        estaEstacion = false;
        lock.unlock();

    }

    public void viajar() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void esperarBajadaPasajeros() {
        lock.lock();
        System.out.println(nombreTransporte + " - LLEGAMOS al DESTINO. Esperando que se bajen todos. Pasajeros: " + cantPasajeros);

        estaDestino = true;
        while (cantPasajeros > 0) {
            System.out.println(nombreTransporte + "  - Todavia le QUEDAN pasajeros. CANT de PASAJEROS RESTANTES: " + cantPasajeros);
            try {
                bajarse.signalAll(); // que se bajen todos
                arrancar.await(); //que no arranquen si aún quedan pasajeros
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(nombreTransporte.toUpperCase() + "  - Se bajaron todos de acá. VUELVOOO al INICIO");
        estaDestino = false;
        lock.unlock();
    }

    public void subirPasajero(Visitante pasajero) {
        lock.lock();
        System.out.println(pasajero.getNombreCompleto() + " - Me QUIERO SUBIR al transporte " + this.getNombre().toUpperCase());
        //Si el transporte no esta en la estación o si no hay más asientos disponibles
        while (!estaEstacion || cantPasajeros >= cantAsientos) {//estacion es true por esperarSubida
            try {
                subirse.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        cantPasajeros++;
        System.out.println(pasajero.getNombreCompleto() + " - Se SUBIÓ al TRANSPORTE: " + this.nombreTransporte.toUpperCase());
        arrancar.signal();
        lock.unlock();
    }

    public void bajarPasajero(Visitante pasajero) {
        lock.lock();
        System.out.println(pasajero.getNombreCompleto() + " - Me quiero bajar!!!!!");
        while (!estaDestino) {
            try {
                System.out.println(pasajero.getNombreCompleto() + " NO PUEDE BAJAR porque NO se LLEGÓ a DESTINO!!!");
                bajarse.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        cantPasajeros--;
        System.out.println(pasajero.getNombreCompleto() + " Se pudo bajar re contento!!!! == PASAJEROS RESTANTES: " + cantPasajeros);
        if (cantPasajeros == 0) {
            arrancar.signal();
        }
        lock.unlock();
    }

    public String getNombre() {
        return this.nombreTransporte;
    }

}
