package parqueacuatico;

public class Chofer implements Runnable {

	private String nombre;
	private Transporte colectivo;
	
	public Chofer(String nombre, Transporte colectivoManeja)
	{
		this.nombre = nombre;
		this.colectivo = colectivoManeja;
	}

	@Override
	public void run() {
		while(true)
		{
		colectivo.esperarSubidaPasajeros();
		colectivo.viajar();
		colectivo.esperarBajadaPasajeros();
		}
	}
	
	
}
