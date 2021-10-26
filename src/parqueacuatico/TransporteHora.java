package parqueacuatico;

public class TransporteHora extends Transporte {

    private int[] horarios = new int[5];
    private int ultimaSalida = -1;//indicaría una hora
    private Reloj elReloj;

    public TransporteHora(String nro, int cantAsientosLibres, Reloj unReloj, int[] horarios) {
        super(nro, cantAsientosLibres);
        this.horarios = horarios;
        elReloj = unReloj;
    }

    public void esperarSubidaPasajeros() {
        System.out.println(nombreTransporte + " - Comienza esperarSubidaPasajero");
        lock.lock();
        estaEstacion = true;

        System.out.println(nombreTransporte + "  - Espero a que se suba alguien. Pasajeros actuales: " + cantPasajeros);
        //Chequeo que ya no haya salido en esta hora para que durante la hora de salida no salga y venga constantemente
        while (cantPasajeros == 0 || (ultimaSalida == elReloj.getHoraActual() || !esHoraSalir())) {
            //cuando sea hora de salir (además de que si ya hay pasasjeros), entonces será la negación 
            //y no entraría al while
            subirse.signalAll();
            lock.unlock();
            elReloj.esperarUnaHora();//capaz que no había nadie esperando por subir
            lock.lock();
        }
        System.out.println(nombreTransporte + " - Me estoy yendo de la estacion");
        estaEstacion = false;
        ultimaSalida = elReloj.getHoraActual(); //setea la ultima hora que salió el cole para luego comparar de vuelta
        lock.unlock();
    }

    private boolean esHoraSalir() {
        boolean valor = false;
        int i = 0;
        while (!valor && i < horarios.length) {
            valor = (horarios[i] == elReloj.getHoraActual());
            i++;
        }
        return valor;
    }
}
