package parqueacuatico;
/**
 *
 * @author Carolina
 */
public class Asistente implements Runnable {
    private String id;
    private NadoSnorkel actSnorkel;

    public Asistente(String nombre, NadoSnorkel actSnorkel) {
        this.actSnorkel = actSnorkel;
        this.id = nombre;
    }

    public void run() {
        System.out.println(this.getNombreCompleto() + " - Se esta por parar en el stand de Snorkel para entregar cosas");
        while (true) {
            actSnorkel.entregarEquipo(this);
        }
    }

    public String getNombreCompleto() {
        return "AsistenteSnorkel " + id;
    }
}
