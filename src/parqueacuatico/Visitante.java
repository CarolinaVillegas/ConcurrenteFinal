package parqueacuatico;

import java.util.Random;

/**
 *
 * @author Carolina
 */
public class Visitante implements Runnable {

    //  private Reloj elReloj;
    private String nombre;
    private boolean vaEnColectivo = false;
    private Parque elParque;
    private TransporteHora elColectivo;
    private int llave, ultimoRestaurante, ticketsRestaurante, turnoDelfines;
    private boolean tieneMochila, dejoMochila, tieneEquipoSnorkel, esPrimeroFila, estaEnCaja,quierePareja;
    private Random random = new Random();
    private int horaDelfines = -1;

    
    public Visitante(String nom, Parque ecopcs, TransporteHora unColectivo) {

        ultimoRestaurante = -1;
        ticketsRestaurante = 2;
        turnoDelfines = -1;
        this.nombre = nom;
        this.elParque = ecopcs;
        //elReloj = ecopcs.getReloj();
        tieneMochila = random.nextBoolean();
        dejoMochila = false;
        tieneEquipoSnorkel = false;
        esPrimeroFila = false;
        this.estaEnCaja = false;
        quierePareja=random.nextBoolean();
        this.vaEnColectivo = random.nextBoolean();
        this.elColectivo = unColectivo;
    }

    public void run() {
        while (true) {
            while (!elParque.estaAbierto()) {
                System.out.println("********* ES LA HORA " + elParque.getReloj().getHoraActual() + " *********");
                System.out.println(this.getNombreCompleto() + " - El parque esta cerrado, vuelvo mañana");
                elParque.getReloj().esperarUnaHora();
            }
            System.out.println(this.getNombreCompleto() + " - ES HORA DE IR AL PARQUE ACUATICO!!! === COMIENZO ===");
            if (vaEnColectivo && this.elColectivo != null) {
                elColectivo.subirPasajero(this);
                elColectivo.bajarPasajero(this);
            } else {
                System.out.println(this.getNombreCompleto() + " - Fue por su propio medio");
            }
            System.out.println(this.getNombreCompleto() + " - Llego al parque");
           // elParque.entrarParque(this);
           // realizarActividades(this);
            elParque.realizarMundoAventura(this);
           // elParque.realizarNadoDelfines(this);
            /*método para irse*/
//           if(!elParque.estaAbierto()){
//               elParque.irse(this);
//           }

            //Reseteo el ultimo restaurante en el que comio
            ultimoRestaurante = -1;
            this.ticketsRestaurante = 2;
            turnoDelfines = -1;
        }
    }

    public void realizarActividades(Visitante unVisitante) {
        int numActividad;
        while (elParque.estaAbierto()) {
            if (unVisitante.getHoraDelfines() == elParque.getReloj().getHoraActual() || unVisitante.getHoraDelfines() == elParque.getReloj().getHoraActual() - 1) {
                //El segundo caso es para que lleguen un rato antes, nada mas. Igual funciona si llegan a tiempoo;
                numActividad = 2;
            }
            //numActividad = 6;		//Debug
            numActividad = random.nextInt(7);
            switch (numActividad) {
                case 0: //Shop
                    elParque.realizarShop(unVisitante);
                    break;
                case 1: //Restaurante
                    elParque.comerRestaurante(unVisitante);
                    break;
                case 2: //Nado con delfines
                    elParque.realizarNadoDelfines(unVisitante);
                    break;
                case 3: //Snorkel
                    elParque.realizarNadoSnorkel(unVisitante);
                    break;
                case 4:
                    elParque.realizarMundoAventura(unVisitante);
                    break;
                case 5: //Faro-Mirador
                    elParque.realizarFaroMirador(unVisitante);
                    break;
                case 6: //Carrera Gomones
                    elParque.realizarCarreraGomones(unVisitante);
                    break;
                default: //No deberia pasar pero siempre tiene que ver un default
                    elParque.darVueltas(unVisitante);
                    break;
            }
        }
    }

    public String getNombreCompleto() {
        return "VISITANTE " + nombre;
    }

    public void setTransporte(TransporteHora unColectivo) {
        this.elColectivo = unColectivo;
    }

    public boolean getVaEnCole() {
        return this.vaEnColectivo;
    }

    public void dejarEquipamiento(int numLlave) {
        tieneMochila = false;
        dejoMochila = true;
        this.llave = numLlave;
    }

    public boolean getTieneMochila() {
        return tieneMochila;
    }

    public int getLlave() {
        return llave;
    }

    public void recuperarBolso() {
        tieneMochila = true;
        dejoMochila = false;
        this.llave = -1;
    }

    public boolean getDejoMochila() {
        return this.dejoMochila;
    }

    public boolean getTieneEquipoSnorkel() {
        return tieneEquipoSnorkel;
    }

    public void setTieneEquipoSnorkel(boolean valor) {
        this.tieneEquipoSnorkel = valor;
    }

    //Restaurantes
    public int getCantTickets() {
        return this.ticketsRestaurante;
    }

    public int getUltimoRestaurante() {
        return this.ultimoRestaurante;
    }

    public void setUltimoRestaurante(int numero) {
        if (numero > -1) {
            this.ticketsRestaurante--;
            this.ultimoRestaurante = numero;
        } else {
            System.err.println("No se pudo modificar el numero de restaurante");
        }
    }

    //NadoDelfines
    public void setTurnoDelfines(int turno) {
        turnoDelfines = turno;
    }

    public int getTurnoDelfines() {
        return this.turnoDelfines;
    }

    public boolean tieneTurnoDelfines() {
        return this.turnoDelfines >= 0;
    }

    public void setHoraDelfines(int valor) {
        this.horaDelfines = valor;
    }

    public int getHoraDelfines() {
        return this.horaDelfines;
    }

    public boolean getEsPrimeroFila() {
        return esPrimeroFila;
    }

    public void setEsPrimeroFila(boolean valor) {
        esPrimeroFila = valor;
    }

    public void setEstaEnCaja(boolean valor) {
        this.estaEnCaja = valor;
    }

    public boolean getEstaEnCaja() {
        return this.estaEnCaja;
    }
    public boolean getQuierePareja(){
        return this.quierePareja;
    }
}
