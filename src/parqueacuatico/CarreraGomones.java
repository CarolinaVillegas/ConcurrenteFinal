    package parqueacuatico;
import java.util.Random;

class CarreraGomones {

    private Random random = new Random();
    private final int CANT_ASIENTOS_TREN = 15;
    private final int CANT_ESPACIO_CAMIONETA = 20;
    private final int CANT_GOMONES = 5;
    private final int CANT_MIN_ARRANCAR = 5;

    private int cantBicis = 10;
    private int cantGomonesSolo = CANT_GOMONES;
    private int cantGomonesDuo = CANT_GOMONES;
    private int ultPosDuo = -1;
    private int ultPosSolo = 0;
    private boolean yaHabiaAlguienDuo = true;

    private Reloj elReloj;
    private Carrera carreraAux = new Carrera(CANT_MIN_ARRANCAR);
    private Camioneta camioneta;
    private CamionetaBicis camionetaBicis;
    private Transporte trencito;
    private Gomon[] gomonesSingle = new Gomon[cantGomonesSolo];
    private boolean[] estaParaSalirSolo = new boolean[cantGomonesSolo];
    private Gomon[] gomonesDobles = new Gomon[cantGomonesDuo];

    private Chofer choferTrencito;

    public CarreraGomones(Reloj unReloj) {
        elReloj = unReloj;
        trencito = new Transporte("TREN 01", CANT_ASIENTOS_TREN);
        choferTrencito = new Chofer("CHOFER_TREN 01", trencito);
        (new Thread(choferTrencito)).start();

        camioneta = new Camioneta("CAMIONETA 01", CANT_ESPACIO_CAMIONETA, elReloj);
        this.elReloj.setCamioneta(camioneta);
        (new Thread(new ChoferCamioneta(camioneta))).start();

        camionetaBicis = new CamionetaBicis("CamionetaBicis 01", this);
        (new Thread(camionetaBicis)).start();
        inicializarGomones();
    }

    private void inicializarGomones() {

        // Podria haber inicializado los dos en el mismo for
        for (int i = 0; i < cantGomonesSolo; i++) {
            gomonesSingle[i] = new Gomon("GomonSolo" + i, 1, 1, this, this.elReloj);
            new Thread(new InstructorGomon("INSTRUCTOR GOMON 1" + i, gomonesSingle[i], elReloj)).start();
        }
        for (int i = 0; i < cantGomonesDuo; i++) {
            gomonesDobles[i] = new Gomon("GomonDoble" + i, 2, 2, this, this.elReloj);
            new Thread(new InstructorGomon("INSTRUCTOR GOMON 2" + i, gomonesDobles[i], elReloj)).start();
        }
    }

    public void realizarCarreraGomones(Visitante unVisitante) {
        System.out.println(unVisitante.getNombreCompleto() + " - Llego a la base de gomones --- EMPIEZA GOMONES ---");
        boolean subeEnBici = random.nextBoolean();
        boolean vaEnDoble = random.nextBoolean();// Subida, carrera (adentro de gomones) y descenso de gomones
        Gomon elGomon;

        // Metodo para subir
        if (subeEnBici) {
            System.out.println(unVisitante.getNombreCompleto() + " - Decide usar la bici - subeEnBici es: "+subeEnBici);
            subirEnBici(unVisitante);
        } else {
            System.out.println(unVisitante.getNombreCompleto() + " - Decide usar el trencito!!! - subeEnBici es: "+subeEnBici);
            subirEnTrencito(unVisitante);
        }
        // Guarda bolso del visitante
        if (unVisitante.getTieneMochila()) {
            System.out.println(unVisitante.getNombreCompleto() + " - Esta INTENTANDO DEJAR la mochila en la camioneta");
            camioneta.guardarBolso(unVisitante);
            System.out.println(unVisitante.getNombreCompleto() + " - ACABA DE DEJAR la mochila en la camioneta");
        }       
        // Hay muchas variables que tienen que cuidar, por eso el synchronized this
        if (vaEnDoble) {
            System.out.println(unVisitante.getNombreCompleto() + " - vaEnDoble: "+vaEnDoble);
            synchronized (this) {
               // System.out.println(unVisitante.getNombreCompleto() + " - NEGACION !yaHabíaAlguienDuo: "+!yaHabiaAlguienDuo);
                if (yaHabiaAlguienDuo) {
                    ultPosDuo = (ultPosDuo + 1) % cantGomonesDuo;
                    this.elReloj.agregarGomon(gomonesDobles[ultPosDuo]);
                }
                elGomon = gomonesDobles[ultPosDuo];
            }
        } else {
            System.out.println(unVisitante.getNombreCompleto() + " - vaEnDoble: "+vaEnDoble);
            synchronized (this) {
                estaParaSalirSolo[ultPosSolo] = true;
                ultPosSolo = (ultPosSolo + 1) % cantGomonesSolo;
                elGomon = gomonesSingle[ultPosSolo];
            }
        }
        if (this.elReloj.getHoraActual() >= 9 && this.elReloj.getHoraActual() <= 18) {
            elGomon.subirPasajero(unVisitante);
            // La carrera la hacen los gomones por su cuenta entre ellos, compartiendo la
            // cyclicbarrier.
            if (!elGomon.bajarPasajeroGomon(unVisitante)) {
                System.out.println("**********/////==============///////***********");
                System.out.println(unVisitante.getNombreCompleto() + " - Su gomon no pudo salir porque no vino mas gente. Se esta yendo sin hacer la actividad :(");
            }
        }
        if (unVisitante.getDejoMochila()) {
            camioneta.recuperarBolso(unVisitante);
        }
        System.out.println(unVisitante.getNombreCompleto() + " - Se esta yendo de la base de gomones <-- FIN Gomones");
    }

    public void subirEnBici(Visitante unVisitante) {
        System.out.println(unVisitante.getNombreCompleto() + " - Esta por subir en bici");
        synchronized (this) {
            while (cantBicis == 0) {
                System.out.println(unVisitante.getNombreCompleto() + " - No pudo salir porque no habia bicis");
                try {
                    wait();//se despierta cuando devuelven las bicis
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        System.out.println(unVisitante.getNombreCompleto() + " - Empezo a subir en bici");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(unVisitante.getNombreCompleto() + " - SUBIÓ en bici");
        camionetaBicis.subirBici();
        System.out.println(unVisitante.getNombreCompleto() + " - Termino de subir hasta los gomones y dejo la bici");
    }

    public void subirEnTrencito(Visitante unVisitante) {
        // Funciona igual que en el Colectivo
        trencito.subirPasajero(unVisitante);
        trencito.bajarPasajero(unVisitante);
    }

    public void ponerEnSalidaGomon(Gomon elGomon) {
        // metodo llamado por el gomon
        System.out.println(elGomon.getNombre() + " - Esta esperando para ponerse en la salida");
        synchronized (this) {
            if (carreraAux.getYaComenzo()) {
                // Si la carrera ya comenzo, la descarto (sigue de fondo) y comienzo una nueva
                carreraAux = new Carrera(CANT_MIN_ARRANCAR);
                System.out.println("Generada otra carrera Nueva");
            }
            // tiene que soltar el Lock antes de entrar a este metodo porque si no se va a quedar con el lock
        }
        carreraAux.agregarGomonASalida(elGomon);
    }

    public synchronized void devolverBicis(int cantidadBicis) {
        System.out.println("CARRERAGOMONES - Devolvieron " + cantidadBicis + " bicis");
        this.cantBicis += cantidadBicis;
        notifyAll();
    }
}
