package parqueacuatico;

public class CamionetaBicis implements Runnable {

    private final int CANT_MIN_BICIS = 3;

    private CarreraGomones laBaseGomones;
    private String nombre = this.getClass().toString();
    private int cantidadBicis;
    private boolean estaOrigen, estaDestino;

    public CamionetaBicis(String nombre, CarreraGomones laBaseGomones) {
        cantidadBicis = 0;
        this.nombre = nombre;
        estaOrigen = true;
        estaDestino = false;
        this.laBaseGomones = laBaseGomones;
    }

    public void run() {
        while (true) {
            esperarEnOrigen();
            viajar();
            esperarEnDestino();
            viajar();
        }
    }

    public synchronized void subirBici() {
        while (!estaOrigen) {
            System.out.println("Un visitante esta esperando para devolver la bici a la camioneta");
            try {
                wait();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        cantidadBicis++;
        System.out.println("CAMIONETABICIS - Subieron una bici a la camioneta");
    }

    private synchronized void esperarEnOrigen() {
        //Tecnicamente esta esperando arriba

        estaOrigen = true;
        System.out.println("CAMIONETABICIS - Esperando que suban un par de bicis para poder bajarlas");
        notify();
        while (cantidadBicis < CANT_MIN_BICIS) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("CAMIONETABICIS - Subieron suficientes bicis, salgo para abajo a devolverlas");
        estaOrigen = false;
    }

    private void viajar() {
        System.out.println("CAMIONETABICIS - Viajando");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void esperarEnDestino() {
        estaDestino = true;
        System.out.println("CAMIONETABICIS - Bajando bicis");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        laBaseGomones.devolverBicis(cantidadBicis);
        estaDestino = false;
        System.out.print("CAMIONETABICIS - Esta volviendo a origen");
    }
}
