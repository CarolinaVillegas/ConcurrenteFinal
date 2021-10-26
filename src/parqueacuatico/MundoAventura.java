package parqueacuatico;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class MundoAventura {

    Semaphore filaCuerdas;
    Tirolesa laTirolesa = new Tirolesa("Tirolesa");

    Random random = new Random();
    Reloj reloj;
    Soga unaSoga = new Soga("La Soga");
    private boolean sogaLadoOeste = false;
    private Visitante[] visitantesConSoga = new Visitante[2];
    private int lugar = 0;

    public MundoAventura(Reloj elReloj) {
        this.filaCuerdas = new Semaphore(1, true);
        (new Thread(laTirolesa)).start();
        boolean esperaPareja = false;
        String name = "";
        reloj = elReloj;
    }

    public void realizarMundoAventura(Visitante unVisitante) {
        boolean salioForzado = true;
        System.out.println(unVisitante.getNombreCompleto() + " - Comenzo Mundo Aventura <-- INICIO MundoAventura");
        // Siempre verifica la hora que es para no pasarse de las 18
        if (reloj.getHoraActual() >= 9 && reloj.getHoraActual() < 17) {
            // this.hacerCuerdas(unVisitante);
            if (reloj.getHoraActual() >= 9 && reloj.getHoraActual() < 18) {
                // this.tirarseTirolesa(unVisitante);

                if (reloj.getHoraActual() >= 9 && reloj.getHoraActual() < 18) {
                    // System.out.println("PASOOOOOO " + unVisitante.getNombreCompleto());
                    this.hacerSaltos(unVisitante);
                    salioForzado = false;
                }
            }
        }
        if (salioForzado) {
            System.out.println(unVisitante.getNombreCompleto()
                    + " - Fue hechado del Mundo Aventura porque estan cerrando el parque");
        }
        System.out.println(unVisitante.getNombreCompleto() + " - Termino el mundo aventura <-- FIN MundoAventura");
    }

    private void hacerCuerdas(Visitante unVisitante) {
        System.out.println(unVisitante.getNombreCompleto() + " - Esta haciendo fila para las cuerdas de Mundo Aventura");
        try {
            filaCuerdas.acquire();
            if (reloj.getHoraActual() >= 9 && reloj.getHoraActual() < 18) {
                System.out.println(unVisitante.getNombreCompleto() + " - Esta haciendo las cuerdas en Mundo Aventura");
                Thread.sleep(1000);
            } else {
                System.out.println(unVisitante.getNombreCompleto() + " - No pudo hacer las cuerdas en Mundo Aventura");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        filaCuerdas.release();
        System.out.println(unVisitante.getNombreCompleto() + " - Termino la actividad de las cuerdas");
    }

    private void tirarseTirolesa(Visitante unVisitante) {
        boolean seTiraPorLadoEste = random.nextBoolean();
        System.out.println(unVisitante.getNombreCompleto() + " - Comienza a hacer fila para las tirolesas. Se quiere tirar por el ESTE?? " + seTiraPorLadoEste);
        if (seTiraPorLadoEste) {
            synchronized (this) {
                System.out.println(unVisitante.getNombreCompleto() + " decide ir a tirarse por el lado ESTE");
            }
            tirarseLadoEste(unVisitante);
        } else {
            synchronized (this) {
                System.out.println(unVisitante.getNombreCompleto() + " decide ir a tirarse por el lado OESTE");
            }
            tirarseLadoOeste(unVisitante);
        }
    }

    private synchronized void tirarseLadoOeste(Visitante unVisitante) {

        laTirolesa.subirPasajeroOeste(unVisitante);
        laTirolesa.bajarPasajeroEnEste(unVisitante);

    }

    private void tirarseLadoEste(Visitante unVisitante) {
        laTirolesa.subirPasajeroEste(unVisitante);
        laTirolesa.bajarPasajeroEnOeste(unVisitante);
    }

    public void hacerSaltos(Visitante unVisitante) {//van a hacer los saltos desde el este al oeste
        if (unVisitante.getQuierePareja() == false) {
            this.unaSoga.saltarSolo(unVisitante);
        } else {
            this.unaSoga.saltar(unVisitante);
        }

    }

}
