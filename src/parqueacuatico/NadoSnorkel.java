package parqueacuatico;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Carolina
 */
class NadoSnorkel {

    private int cantGenteFila = 0;
    private int cantEquiposDisponibles = 10;
    private Lock lock = new ReentrantLock(true); // para darle fairness
    private Condition entregarEquipo = lock.newCondition();
    private Condition agarrarEquipo = lock.newCondition();

    public void entregarEquipo(Asistente unAsistente) {
        System.out.println(unAsistente.getNombreCompleto() + " - Estoy esperando para entregar un Snorkel");
        lock.lock();
        do {
            try {
                entregarEquipo.await();// es como que el asistente esta a disposicion p/entregar equipo
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } while (cantEquiposDisponibles <= 0 || cantGenteFila == 0);

        System.out.println(unAsistente.getNombreCompleto() + " - Estoy entregando un Snorkel a un visitante");
        cantEquiposDisponibles--;
        System.out.println("EQUIPOS SNORKEL RESTANTES " + cantEquiposDisponibles);
        agarrarEquipo.signal();
        System.out.println(unAsistente.getNombreCompleto() + " - Listo, entregue el equipo");
        lock.unlock();
    }

    public void solicitarEquipo(Visitante unVisitante) {
        lock.lock();
        cantGenteFila++;
        System.out.println(unVisitante.getNombreCompleto() + " - Ya se anoto a la lista de espera. Esperando recibir equipo");
        entregarEquipo.signal();
        try {
            agarrarEquipo.await();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        lock.unlock();

        System.out.println(unVisitante.getNombreCompleto() + " - Ya obtuvo su equipo de snorkel");
    }

    public void devolverEquipo(Visitante unVisitante) {
        lock.lock();
        try {
            cantEquiposDisponibles++;
            cantGenteFila--;
            System.out.println(unVisitante.getNombreCompleto() + " - Se esta retirando. Deja el equipo de snorkel");
            System.out.println("EQUIPOS SNORKEL RESTANTES " + cantEquiposDisponibles);
            entregarEquipo.signal();
        } finally {
            lock.unlock();
        }
    }

    public void realizarNadoSnorkel(Visitante unVisitante) {
        System.out.println(unVisitante.getNombreCompleto() + " - Llego al Stand de nado con snorkel, quiere un equipo <-- INICIO Nado Snorkel");

        solicitarEquipo(unVisitante);

        System.out.println(unVisitante.getNombreCompleto() + " - Ya recibio su equipo. Comenzo a nadar");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        System.out.println(unVisitante.getNombreCompleto() + " - ya terminó de hacer Snorkel y va a devolver el equipo");

        devolverEquipo(unVisitante);

        System.out.println(unVisitante.getNombreCompleto() + " - ya devolvió el equipo. <-- FIN Nado con Snorkel");

    }
}
