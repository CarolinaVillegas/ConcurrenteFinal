package parqueacuatico;

//import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Carrera {

    private Random random = new Random();
    private double nombre = random.nextDouble();
    private int cantMinArrancar = 3;

    private AtomicBoolean yaComenzo = new AtomicBoolean(false);
    private AtomicInteger posicionCarrera = new AtomicInteger(0);
    private CyclicBarrier barrera = new CyclicBarrier(cantMinArrancar);

    public Carrera(int cantMin) {
        barrera = new CyclicBarrier(cantMin);
    }

    public void agregarGomonASalida(Gomon unGomon) {
        //Se tiene que fijar afuera del metodo de que la carrera no haya comenzado.		
        System.out.println(unGomon.getNombre() + " - Llego al inicio de la carrera " + nombre);
        unGomon.setCarreraParticipa(this);
        try {
            barrera.await(3000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            System.out.println("Llegaron todos a la barrera y salieron " + nombre);
        } catch (TimeoutException e) {
            System.out.println("Se cansaron de esperar y se largo la carrera! <---------------" + nombre);
        }
        yaComenzo.set(true);
        System.out.println(unGomon.getNombre() + " - Arranco la carrera! " + nombre);
    }

    // este método se puede obviar y usar el get propio entonces
    public boolean getYaComenzo() {
        return yaComenzo.get();
    }

    public int getPosicion() {
        return posicionCarrera.addAndGet(1);
    }
}
