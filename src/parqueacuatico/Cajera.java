package parqueacuatico;

public class Cajera implements Runnable {

    private String nombre;
    private Shop shopQueAtiende;

    public Cajera(String nombre, Shop unShop) {
        this.nombre = nombre;
        this.shopQueAtiende = unShop;
    }

    public void run() {
        while (true) {
            shopQueAtiende.atenderCaja(this);
        }
    }

    public String getNombreCompleto() {
        return "CAJERA " + this.nombre;
    }

}
