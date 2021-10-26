package parqueacuatico;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NadoDelfines {

    private final int CANT_PILETAS = 3;
    private final int CANT_ESPACIO_PILETA = 5; // cantidad gente que entra en una pileta
    private final int MINIMA_GENTE_EMPEZAR = 11;// generico sería CANT_ESPACIO_PILETA * (CANT_PILETAS - 1) porque ésta no necesita
    //estar llena, igual no se deja como genérico porque son grupos de 10 persona por pileta
    private final int MAXIMA_GENTE_PILETA = 15;//CANT_ESPACIO_PILETA*CANT_PILETAS
    private final int RESETEO = -1;
    private int ultimoShow = -1;
    private int cantHorarios = 0;
    private int[] horarioParaNadar;
    private CountDownLatch latchMinGente = new CountDownLatch(MINIMA_GENTE_EMPEZAR);
    private Reloj reloj;
    boolean showEstaSucediendo = false;
    private boolean showNoComenzo = true;
    private AtomicInteger[] cantGenteAnotadaPorTurno;

    public NadoDelfines(Reloj reloj, int[] horarios) {
        this.reloj = reloj;
        this.horarioParaNadar = horarios;
        this.cantHorarios = horarios.length;
        this.cantGenteAnotadaPorTurno = new AtomicInteger[horarios.length];
        new Thread(new EncargadoPileta(this, reloj)).start();
        inicializarAtomicInteger();
    }

    private void inicializarAtomicInteger() {
        for (int i = 0; i < cantHorarios; i++) {
            cantGenteAnotadaPorTurno[i] = new AtomicInteger(0);
        }
    }

    /* public void realizarNadoDelfines(Visitante unVisitante) {
        /* El encargado maneja los turnos para que se anote en algun show.
        (hace 5 listas con 5 horarios y el usa el mismo numero para meterse en uno y en otro)
        Se anota en una lista y se va de este metodo. Cuando sea su turno, se le avisa con un notify o
        se cambia una variable y hago que vuelva despues de su actividad actual
        Se mete a este mismo metodo, pero en vez de venir por aca, pasa derecho a su evento.*/
 /* if (!unVisitante.tieneTurnoDelfines()) {
            System.out.println(unVisitante.getNombreCompleto() + " Se está yendo a anotar <--- INICIO Nado Delfines");
            // Lo intento anotar en el nado con delfines
            int pos = 0;//se usa para iterar por el arreglo de turnos
          
            //Verifica que el visitante no se anote en un horario que ya pasó
            //y que exista uno dentro de los horarios que todavía no pasó
            //Verifica si el visitante se puede meter en algun horario
            while (pos < cantHorarios && (horarioParaNadar[pos] <= reloj.getHoraActual() || this.cantGenteAnotadaPorTurno[pos].get() >= MAXIMA_GENTE_PILETA)) {
                //Si hay mas de 40 personas, paso al siguiente. Si todos los turnos estan llenos
                //en el siguiente if no va a entrar y no va a modificar la variable del visitante
                pos++;
            }
            synchronized (this) {
                /*Esto esta sincronizado porque pueden llegar a meterse 2 o mas hilos
			al if e incrementar en +1 ambos, lo cual en un caso muy particular
			puede llegar a dejar la cant de gente anotada en 41 o mas.
     */
 /*  if (pos < cantHorarios) {//significa que quedan horarios en los que se puede anotar
                    unVisitante.setTurnoDelfines(pos);//le seteteo el turno al visitante que tiene para nadar con delfines
                    unVisitante.setHoraDelfines(horarioParaNadar[pos]);//le seteo el horario que tiene para nadar con delfines
                    this.cantGenteAnotadaPorTurno[pos].incrementAndGet();
                    System.out.println(unVisitante.getNombreCompleto() + " - Se anoto exitosamente en el turno de las " + horarioParaNadar[pos]);
                } else {
                    System.out.println(unVisitante.getNombreCompleto() + " - No se pudo anotar entonces se fue");
                }
            }
        } else {
            // Vino porque lo llamaron o por su cuenta para ver si empezo el show
            int horarioTurnoVisitante = unVisitante.getHoraDelfines();
            if (horarioTurnoVisitante == reloj.getHoraActual() && this.cantGenteAnotadaPorTurno[unVisitante.getTurnoDelfines()].get() >= MINIMA_GENTE_EMPEZAR) {
                // Si es la hora de su nado, se mete por lo que decrementa en uno la cantidad de gente que entro
                this.latchMinGente.countDown();
                System.out.println(unVisitante.getNombreCompleto() + " - Entro a la pileta");
                synchronized (this) {
                    while (showNoComenzo) {
                        try {
                            System.out.println(unVisitante.getNombreCompleto() + " - En un rato empieza el show");
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    while (showEstaSucediendo) {
                        try {
                            wait();
                            System.out.println(unVisitante.getNombreCompleto() + " - Me quiero ir del nado de delfines ");
                        } catch (InterruptedException e) {
                        }
                    }
                }
                System.out.println(unVisitante.getNombreCompleto() + " SE ESTA YENDO del nado de delfines ");
                //Le cambio las variables para que no siga entrando a nadar
                this.cantGenteAnotadaPorTurno[unVisitante.getTurnoDelfines()].decrementAndGet();//Lo saco
                unVisitante.setTurnoDelfines(RESETEO);
                unVisitante.setHoraDelfines(RESETEO);
                System.out.println(unVisitante.getNombreCompleto() + " - Por fin se pudo ir del nado de delfines <------ FIN NadoDelfines");

            } else {
                //entro a ver el show
                if (horarioTurnoVisitante < reloj.getHoraActual()) {
                    //Si no hay un minimo de gente y pasa de largo en if de arriba, va a pasar una hora y va a entrar aca y se va a ir.
                    System.out.println(unVisitante.getNombreCompleto() + " - Se le paso su horario del show y se fue. Puede haber sido que no habia gente suficiente para el show anterior tambien");
                    this.cantGenteAnotadaPorTurno[unVisitante.getTurnoDelfines()].decrementAndGet();// se saca de la lista en la que estaba y despues se cambia el ticket
                    unVisitante.setTurnoDelfines(RESETEO);
                    unVisitante.setHoraDelfines(RESETEO);
                } else {
                    // entró, miró y se fue..
                    System.out.println(unVisitante.getNombreCompleto() + " - Se fue porque no era su turno de delfines");
                }
            }
        }
    } */
    public void realizarNadoDelfines(Visitante unVisitante) {

        if (!unVisitante.tieneTurnoDelfines()) {
            System.out.println(unVisitante.getNombreCompleto() + " Se está yendo a anotar <--- INICIO Nado Delfines");
            int pos = 0;//se usa para iterar por el arreglo de turnos
            synchronized (this) {
            while (pos < cantHorarios && (horarioParaNadar[pos] <= reloj.getHoraActual()
            || this.cantGenteAnotadaPorTurno[pos].get() >= MAXIMA_GENTE_PILETA)) {
                
                System.out.println("_____________________Cantidad de gente en este turno : ____________________ "+this.cantGenteAnotadaPorTurno[pos]);
                System.out.println("***************** HORA **************"+horarioParaNadar[pos]);
                pos++;
            }
           // System.out.println("Cantidad de gente en este turno "+this.cantGenteAnotadaPorTurno[pos]);
            
                if (pos < cantHorarios) {//significa que quedan horarios en los que se puede anotar
                    unVisitante.setTurnoDelfines(pos);//le seteteo el turno al visitante que tiene para nadar con delfines
                    unVisitante.setHoraDelfines(horarioParaNadar[pos]);//le seteo el horario que tiene para nadar con delfines
                    this.cantGenteAnotadaPorTurno[pos].incrementAndGet();
                    System.out.println(unVisitante.getNombreCompleto()+" Ahora cantidad de gente en este turno "+this.cantGenteAnotadaPorTurno[pos]+" - Se anoto exitosamente en el turno de las " + horarioParaNadar[pos]);
                    System.out.println(unVisitante.getNombreCompleto() + " - Se anoto exitosamente en el turno de las " + horarioParaNadar[pos]);
                } else {
                    System.out.println(unVisitante.getNombreCompleto() + " - No se pudo anotar entonces se fue");
                }
            }
        }
        
        int horarioTurnoVisitante = unVisitante.getHoraDelfines();
        while (horarioTurnoVisitante > reloj.getHoraActual()) {
            System.out.println(unVisitante.getNombreCompleto() + " se queda esperando porque no es su hora "+horarioTurnoVisitante+ " pero son las "+reloj.getHoraActual());
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(NadoDelfines.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (horarioTurnoVisitante == reloj.getHoraActual() && this.cantGenteAnotadaPorTurno[unVisitante.getTurnoDelfines()].get() >= MINIMA_GENTE_EMPEZAR) {
            // Si es la hora de su nado, se mete por lo que decrementa en uno la cantidad de gente que entro
            this.latchMinGente.countDown();
            System.out.println(unVisitante.getNombreCompleto() + " - Entro a la pileta");
            synchronized (this) {
                while (showNoComenzo) {
                    try {
                        System.out.println(unVisitante.getNombreCompleto() + " - En un rato empieza el show");
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while (showEstaSucediendo) {
                    try {
                        wait();
                        System.out.println(unVisitante.getNombreCompleto() + " - Me quiero ir del nado de delfines ");
                    } catch (InterruptedException e) {
                    }
                }
            }
            System.out.println(unVisitante.getNombreCompleto() + " SE ESTA YENDO del nado de delfines ");
            //Le cambio las variables para que no siga entrando a nadar
            this.cantGenteAnotadaPorTurno[unVisitante.getTurnoDelfines()].decrementAndGet();//Lo saco
            unVisitante.setTurnoDelfines(RESETEO);
            unVisitante.setHoraDelfines(RESETEO);
            System.out.println(unVisitante.getNombreCompleto() + " - Por fin se pudo ir del nado de delfines <------ FIN NadoDelfines");

        } else {
            //entro a ver el show
            if (horarioTurnoVisitante < reloj.getHoraActual()) {
                //Si no hay un minimo de gente y pasa de largo en if de arriba, va a pasar una hora y va a entrar aca y se va a ir.
                System.out.println(unVisitante.getNombreCompleto() + " - Se le paso su horario del show y se fue. Puede haber sido que no habia gente suficiente para el show anterior tambien");
                this.cantGenteAnotadaPorTurno[unVisitante.getTurnoDelfines()].decrementAndGet();// se saca de la lista en la que estaba y despues se cambia el ticket
                unVisitante.setTurnoDelfines(RESETEO);
                unVisitante.setHoraDelfines(RESETEO);
            } else {
                // su turno no llegó a la cantidad de gente mínima
                System.out.println(unVisitante.getNombreCompleto() + " - Se fue porque su turno no llegó a la cantidad de gente mínima");
                unVisitante.setTurnoDelfines(RESETEO);
                unVisitante.setHoraDelfines(RESETEO);
            }
        }
    }

    //llamado desde EncargadoPileta
    public void comenzarShow() {
        while (reloj.getHoraActual() == ultimoShow || !esHorarioShow()) {
            reloj.esperarUnaHora();
        }
        ultimoShow = reloj.getHoraActual();
        System.out.println("NADO CON DELFIN - Esperando que llegue más gente para iniciar la actividad //CountDownLatch//");
        //Latch para asegurar que pasa la gente
        //this.latchMinGente = new CountDownLatch(MINIMA_GENTE_EMPEZAR);
        // Primero tiene que cambiar esta variable asi cuando la gente llega al show queda en el wait;
        showEstaSucediendo = true;
        showNoComenzo = false;
        synchronized (this) {
            notifyAll();
        }
        // espera un minimo y despues sale si no llegaron todos
        try {
            latchMinGente.await(1000, TimeUnit.MILLISECONDS);
            System.out.println("NADO DELFINES - Empezando un show");
            reloj.utilizarTiempoEvento();	//simulacion el tiempo
        } catch (InterruptedException e) {
            System.out.println("NADO DELFINES - No llego la suficiente gente en tiempo y se cancelo el show");
        }
        showNoComenzo = true;
        this.showEstaSucediendo = false;
        System.out.println("NADO DELFINES - Termino el show");
        synchronized (this) {
            notifyAll();
        }
    }

    public boolean esHorarioShow() {
        int posicion = 0;	//apenas entra al horario pasa a ser 0.
        boolean esHorario = false;
        while (!esHorario && posicion < cantHorarios) {
            esHorario = horarioParaNadar[posicion] == reloj.getHoraActual();
            posicion++;
        }
        return esHorario;
    }
}
