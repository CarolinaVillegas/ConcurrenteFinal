package parqueacuatico;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;

public class Gomon extends Transporte {

    private boolean noPudoSalir;
    private AtomicInteger laPos;
    private int posicionFinal = -1;
    private boolean estaListo = false;
    private CarreraGomones laBaseGomones;
    private Carrera carreraParticipa;
    private int listaEspera = 0;
    protected Reloj elReloj;
    Condition esperarGenteLista = lock.newCondition();

    public Gomon(String nro, int cantAsientosLibres, int cantMinimaGente, CarreraGomones baseGomones, Reloj unReloj) {
        super(nro, cantAsientosLibres, cantMinimaGente);
        laBaseGomones = baseGomones;
        elReloj = unReloj;
    }

    //método para más de un pasajero
    public boolean esperarSubidaPasajerosGomon() {
        lock.lock();
        //Un chequeo para que espere que se bajen todos si el gomon no pudo salir exitosamente ANTES
        while (cantPasajeros > 0) {
            System.out.println(this.getNombre() + " Esta esperando que se bajen los pasajeros ya que la CARRERA NO COMENZÓ");
            bajarse.signalAll();
            try {
                arrancar.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while (listaEspera == 0) {
            System.out.println(this.getNombre() + " NO hay nadie ESPERANDO por SUBIR");
            try {
                esperarGenteLista.await();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        noPudoSalir = false;
        System.out.println(nombreTransporte + " - Comienza esperarSubidaPasajero");
        estaEstacion = true;

        while (cantPasajeros < cantMinimaGente && (this.elReloj.getHoraActual() >= 9 && this.elReloj.getHoraActual() <= 18 || listaEspera > 0)) {
            System.out.println(nombreTransporte + "  - Espero a que se suban " + cantMinimaGente + " pasajeros. Pasajeros actuales: " + cantPasajeros);
            try {
                subirse.signalAll();
                arrancar.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //si se lleno el gomon, continua normalmente
        if (this.estaLleno()) {
            System.out.println(nombreTransporte + " - Me estoy yendo de la estacion");
            estaEstacion = false;
            estaListo = true;
            //No devuelve seSubieronPasajeros = true hasta que no se largue la carrera.
            lock.unlock();
            laBaseGomones.ponerEnSalidaGomon(this);
        } else {
            //Si no se pudo llenar, avisa que no pudo salir. En la siguiente iteracion espera que se bajen.
            System.out.println(this.getNombre() + " - No se alcanzo a llenar. No entre a la carrera");
            noPudoSalir = true;
            lock.unlock();
        }
        //Si pudo salir, devuelve true, si no, false
        return !noPudoSalir;
    }

    public boolean bajarPasajeroGomon(Visitante pasajero) {
        lock.lock();
        try {
            System.out.println(pasajero.getNombreCompleto() + " - Me quiero bajar");
            while (!estaDestino && !noPudoSalir) {//significa que pudo salir
                try {
                    bajarse.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            cantPasajeros--;
            System.out.println(pasajero.getNombreCompleto() + " - Me pude bajar re contento. Pasajeros restantes: " + cantPasajeros);
            if (cantPasajeros <= 0) {
                arrancar.signal();
            }
        } finally {
            lock.unlock();
        }

        return !noPudoSalir;
    }
    //método para un solo pasajero
    public void subirPasajero(Visitante pasajero) {
        lock.lock();
        try {
            System.out.println(pasajero.getNombreCompleto() + " - Me estoy queriendo subir al transporte " + this.getNombre());

            listaEspera++;
            esperarGenteLista.signal();

            while ((!estaEstacion || cantPasajeros >= cantAsientos) && this.elReloj.elParqueEstaAbierto()) {
                try {
                    subirse.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            listaEspera--;
            cantPasajeros++;
            System.out.println(pasajero.getNombreCompleto() + " - Se subio al transporte " + this.nombreTransporte);
            arrancar.signal();
        } finally {
            lock.unlock();
        }
    }

    public void viajar() {
        super.viajar();
        if (this.estaLleno()) {
            posicionFinal = carreraParticipa.getPosicion();
        } else {
            posicionFinal = -1;
        }
        System.out.println(this.nombreTransporte + " - Llego en " + posicionFinal);
    }

    public void setEstaListo(boolean estado) {
        this.estaListo = false;
    }

    public boolean getEstaListo() {
        return estaListo;
    }

    public void setCarreraParticipa(Carrera unaCarrera) {
        carreraParticipa = unaCarrera;
    }

    public boolean estaLleno() {
        return (this.cantPasajeros == this.cantMinimaGente);
    }

    public synchronized void agregarAListaEspera() {
        this.listaEspera++;
    }

    public boolean hayEspera() {
        return listaEspera > 0;
    }

    //Este metodo esta parque que cuando cierran el parque no se quede el gomon esperando la nada
    public void avisarCierreParque() {
        lock.lock();
        try {
            arrancar.signal();
        } finally {
            lock.unlock();
        }

    }

}
