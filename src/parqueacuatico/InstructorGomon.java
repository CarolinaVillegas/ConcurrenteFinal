package parqueacuatico;

public class InstructorGomon implements Runnable{

	String nombre = "";
	Gomon elGomon;
	Reloj elReloj;
	
	public InstructorGomon(String nombre, Gomon unGomon, Reloj unReloj)
	{
		this.nombre = nombre;
		this.elGomon = unGomon;
		this.elReloj = unReloj;
	}
	
	public void run()
	{
		while(true)
		{
			//Si se pudo subir el/los pasajeros arranca
			
		if(this.elGomon.esperarSubidaPasajerosGomon())
		{
			this.elGomon.viajar();
			this.elGomon.esperarBajadaPasajeros();
		}
	}
	}
}
