package parqueacuatico;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Carolina
 */
public class Tirolesa implements Runnable {

    protected String nombreTirolesa = this.getClass().toString();
    protected final int cantLugares = 2;
    protected int cantPasajerosSubidos = 0;
    protected int cantMinimaGente = 1;
    protected int genteEste = 0;
    protected int genteOeste = 0;
    protected boolean estaOeste;
    protected boolean sePuedeSubir;

    public Tirolesa(String nombre) {
        this.nombreTirolesa = nombre;
        estaOeste = true;
        this.sePuedeSubir = true;
    }

    public Tirolesa(String nro, int cantMinimaGente) {
        this.nombreTirolesa = nro;

        this.cantMinimaGente = cantMinimaGente;
    }

    public synchronized boolean tirolesaOeste() {
        return this.estaOeste;
    }


    public synchronized int cantidadPasajerosSubidos() {
        return this.cantPasajerosSubidos;
    }

    public synchronized int cantidadMaxima() {
        return this.cantLugares;
    }

    public synchronized void viajarDesdeOeste() {
        //si se da que la tirolesa tiene dos pasajeros o uno
        //entonces viajo de un lado al otro
        //sino me duermo hasta que alguien me notifique
        while (this.estaOeste == true && this.genteOeste == 0 && this.cantidadPasajerosSubidos() == 0) {
            try {
                System.out.println("1");
                // this.wait(2500);
                this.wait();
                System.out.println("despues de WAIT 1");
                //Thread.sleep(800);
            } catch (InterruptedException ex) {
                Logger.getLogger(Tirolesa.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            System.out.println("Veo si viene alguien más para subirse a la tirolesa");
            this.wait(2500);
            this.sePuedeSubir = false;
        } catch (InterruptedException ex) {
            Logger.getLogger(Tirolesa.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            System.out.println("La tirolesa esta viajando desde el OESTE ");
            Thread.sleep(500);//simular que viaja
            System.out.println("La tirolesa ya llegó del OESTE, ahora está en el ESTE");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.estaOeste = false;
        this.notifyAll();
        while (this.cantPasajerosSubidos != 0) {
            try {
                this.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(Tirolesa.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.sePuedeSubir = true;
        this.notifyAll();
        //despertar a todos
    }

    public synchronized void viajarDesdeEste() {
        //si se da que la tirolesa tiene dos pasajeros o uno
        //entonces viajo de un lado al otro
        //sino me duermo hasta que alguien me notifique
        while (this.genteEste == 0 && this.cantidadPasajerosSubidos() == 0 && this.estaOeste == false) {
            try {
                System.out.println("2");
                // this.wait(2500);
                this.wait();
                System.out.println("después de WAIT 2");
                //Thread.sleep(800);
            } catch (InterruptedException ex) {
                Logger.getLogger(Tirolesa.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            this.wait(2500);
            this.sePuedeSubir = false;
        } catch (InterruptedException ex) {
            Logger.getLogger(Tirolesa.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            System.out.println("La tirolesa esta viajando el ESTE");
            Thread.sleep(500);//simular que viaja
            System.out.println("La tirolesa ya llegó del ESTE, esta en el OESTE");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.estaOeste = true;
        this.notifyAll();
        while (this.cantPasajerosSubidos != 0) {
            try {
                this.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(Tirolesa.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.sePuedeSubir = true;
        this.notifyAll();
        //despertar a todos
    }
//por ahí debería synchronized hasta.. si un hilo se duerme en un método, ningún otro hilo puede ejecutarlo??

    public synchronized void subirPasajeroOeste(Visitante pasajero) {
        System.out.println(pasajero.getNombreCompleto() + " - Me QUIERO SUBIR a la tirolesa en el lado _OESTE_ ");
        //Si el transporte no esta en la estación o si no hay más asientos disponibles
        this.genteOeste++;
        //System.out.println("ANTES del while en subirPasajeroOeste " + pasajero.getNombreCompleto());
        while (estaOeste == false || sePuedeSubir == false || cantPasajerosSubidos >= cantLugares) {//estacion es true por esperarSubida
            try {
                if (estaOeste == false) {
                    System.out.println(" La TIROLESA NO esta en el OESTE " + pasajero.getNombreCompleto());
                }
                if (cantPasajerosSubidos >= cantLugares) {
                    System.out.println(" NO hay LUGAR en la tirolesa ");
                }
                this.wait();
                //subirse.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //System.out.println("DESPUES del while en subirPasajeroOeste " + pasajero.getNombreCompleto());

        if (this.estaOeste == true  && this.cantPasajerosSubidos <= cantLugares) {
            System.out.println(pasajero.getNombreCompleto() + " se puede subir a la tirolesa en el lado OESTE");
            cantPasajerosSubidos++;
            System.out.println(pasajero.getNombreCompleto() + " - Se SUBIÓ a la TIROLESA desde el lado OESTE ");
        }
       // System.out.println("CANTIDAD de pasajeros subidos en la tirolesa " + this.cantPasajerosSubidos + " junto a " + pasajero.getNombreCompleto());
        //arrancar.signal();
        this.notifyAll();

    }

    public synchronized void subirPasajeroEste(Visitante pasajero) {
        System.out.println(pasajero.getNombreCompleto() + " - Me QUIERO SUBIR a la tirolesa ");
        //Si el transporte no esta en la estación o si no hay más asientos disponibles
        this.genteEste++;
        //System.out.println("ANTES del while en subirPasajeroEste " + pasajero.getNombreCompleto());

        while (estaOeste == true || sePuedeSubir == false || cantPasajerosSubidos >= cantLugares) {//estacion es true por esperarSubida
            try {
                if (estaOeste == true) {
                    System.out.println(" La TIROLESA NO esta en el ESTE " + pasajero.getNombreCompleto());
                }
                if (cantPasajerosSubidos >= cantLugares) {
                    System.out.println(" NO hay LUGAR en la tirolesa " + pasajero.getNombreCompleto());
                }
                this.wait();
                //subirse.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
       // System.out.println("DESPUES del while en subirPasajeroEste " + pasajero.getNombreCompleto());

        if (this.estaOeste == false && this.cantPasajerosSubidos < cantLugares) {
            System.out.println(pasajero.getNombreCompleto() + " se puede subir a la tirolesa en el ESTE ");
            cantPasajerosSubidos++;
            System.out.println(pasajero.getNombreCompleto() + " - Se SUBIÓ a la TIROLESA desde el lado ESTE ");
        }
        this.notifyAll();

    }

    public synchronized void bajarPasajeroEnEste(Visitante pasajero) {

        while (this.estaOeste == true) {
            try {
                System.out.println(pasajero.getNombreCompleto() + " NO PUEDE BAJAR porque NO se LLEGÓ al ESTE!!!");
                this.wait();
                //this.notifyAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (this.cantPasajerosSubidos > 0) {
            System.out.println(pasajero.getNombreCompleto() + " se esta bajando de la tirolesa");
            try {
                Thread.sleep(800);
            } catch (InterruptedException ex) {
                Logger.getLogger(Tirolesa.class.getName()).log(Level.SEVERE, null, ex);
            }
            cantPasajerosSubidos--;
            System.out.println(pasajero.getNombreCompleto() + " se bajó de la tirolesa, ahora quedan: " + this.cantPasajerosSubidos);

        }

        this.notifyAll();
    }

    public synchronized void bajarPasajeroEnOeste(Visitante pasajero) {

        while (this.estaOeste == false) {
            try {
                System.out.println(pasajero.getNombreCompleto() + " NO PUEDE BAJAR porque NO se LLEGÓ al OESTE!!!");
                this.wait();
                // this.notifyAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (this.cantPasajerosSubidos > 0) {
            System.out.println(pasajero.getNombreCompleto() + " se esta bajando de la tirolesa");
            try {
                Thread.sleep(800);
            } catch (InterruptedException ex) {
                Logger.getLogger(Tirolesa.class.getName()).log(Level.SEVERE, null, ex);
            }
            cantPasajerosSubidos--;
            System.out.println(pasajero.getNombreCompleto() + " se bajó de la tirolesa, ahora quedan: " + this.cantPasajerosSubidos);

        }
        
            this.notifyAll();
        
    }

    public void run() {
        while (true) {
           // System.out.println("RUN ANTES de viajarDesdeOeste");
            viajarDesdeOeste();
           // System.out.println("RUN DESPUES de viajarDesdeOeste");

          //  System.out.println("RUN ANTES de viajarDesdeEste");
            viajarDesdeEste();
           // System.out.println("RUN DESPUES de viajarDesdeEste");

        }
    }

}
