/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parqueacuatico;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Carolina
 */
public class Parque {

    private final int CANT_RESTAURANTES = 3;
    private Random random = new Random();
    private Reloj reloj;
    private Semaphore molinetes = new Semaphore(6, true);
    private Shop elShop = new Shop();
    private Restaurante[] restaurantes = new Restaurante[CANT_RESTAURANTES];
    private NadoSnorkel actSnorkel = new NadoSnorkel();
    private Asistente[] asistentesSnorkel = new Asistente[3];
    private FaroMirador elFaroTobogan;
    private CarreraGomones laCarreraGomones;
    private MundoAventura elMundoAventura;
    private NadoDelfines nadoDelfines;

    public Parque(Reloj unReloj) {
        reloj = unReloj;
        int[] horarios = {11, 14, 16};
        nadoDelfines = new NadoDelfines(reloj, horarios);
        elMundoAventura = new MundoAventura(reloj);
        elFaroTobogan = new FaroMirador(reloj);
        laCarreraGomones = new CarreraGomones(reloj);
        //Inicializo los asistentes y los ejecuto (Esto tendria que ir adentro de snorkel)
        for (int i = 0; i < asistentesSnorkel.length; i++) {
            asistentesSnorkel[i] = new Asistente("" + i, actSnorkel);
            (new Thread(asistentesSnorkel[i])).start();
        }
        //Inicializo los restaurantes
        for (int i = 0; i < CANT_RESTAURANTES; i++) {
            restaurantes[i] = new Restaurante(i);
        }
    }

    public void realizarCarreraGomones(Visitante unVisitante) {
        this.laCarreraGomones.realizarCarreraGomones(unVisitante);
    }

    public void realizarMundoAventura(Visitante unVisitante) {
        this.elMundoAventura.realizarMundoAventura(unVisitante);
    }

    public void realizarFaroMirador(Visitante unVisitante) {
        this.elFaroTobogan.realizarFaroMirador(unVisitante);
    }

    public void realizarShop(Visitante unVisitante) {
        this.elShop.realizarShop(unVisitante);
    }

    public void realizarNadoDelfines(Visitante unVisitante) {
        System.out.println(unVisitante.getNombreCompleto()+"Voy a ver si puedo ir a nadar con los delfines pero no voy a poder hacer otra actividad hasta no realizar esta");
        this.nadoDelfines.realizarNadoDelfines(unVisitante);
    }

    public void realizarNadoSnorkel(Visitante unVisitante) {
        this.actSnorkel.realizarNadoSnorkel(unVisitante);
    }

    public boolean estaAbierto() {
        int hora = this.reloj.getHoraActual();
        return (hora >= 9 && hora < 17);
    }

    public Reloj getReloj() {
        return reloj;
    }
    public void darVueltas(Visitante unVisitante){
        System.out.println(unVisitante.getNombreCompleto()+" esta chusmeando las actividades en general por el parque");
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ex) {
            Logger.getLogger(Parque.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }


    public void comerRestaurante(Visitante unVisitante) {
        /*el visitante elije de manera aleatoria uno de los 3 restaurantes, es decir, un restaurante
        con id 0, 1 o 2*/
        int numRestaurante = random.nextInt(CANT_RESTAURANTES);
        //inicialmente el visitante tiene 2 tickets, uno para merienda y otro para almuerzo
        if (unVisitante.getCantTickets() > 0 && estaAbierto()) {
            /*si el visitante no comió en este restaurante, el restaurante lo deja pasar
            sino, el restaurante no lo debe dejar pasar y se le asigna otro restaurante para comer
            se supone que solo comió una vez...*/
            if (restaurantes[numRestaurante].comioEnRestaurante(unVisitante)) {
                numRestaurante = (numRestaurante + 1) % CANT_RESTAURANTES;
                //el % sirve para que pase del restaurante 2 al 0 ya que se trata de pasar "al siguiente" restaurante
            }
            restaurantes[numRestaurante].comerRestaurante(unVisitante);//verifica con var tipoComida
        }
    }

    public void entrarParque(Visitante visitante) {
        System.out.println(visitante.getNombreCompleto() + " - Esta INGRESANDOOO al parque por los molinetes");
        try {
            molinetes.acquire();
            System.out.println(visitante.getNombreCompleto() + " - VA PASANDO por molinete LENTAMENTE");
            Thread.sleep(2500);
            System.out.println(visitante.getNombreCompleto() + " - VA TERMINANDO DE PASAR por molinete WUJUU");
            molinetes.release();
            System.out.println(visitante.getNombreCompleto() + " - PASOOOOOOO por molinete!!!!!!");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println(visitante.getNombreCompleto() + " - INGRESÓ BIEN por los molinetes!!!!!!");

    }
}
