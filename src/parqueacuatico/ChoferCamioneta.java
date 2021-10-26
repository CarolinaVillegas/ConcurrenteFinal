package parqueacuatico;

public class ChoferCamioneta implements Runnable{

	private Camioneta laCamioneta;
	
	public ChoferCamioneta(Camioneta unaCamioneta)
	{
		this.laCamioneta = unaCamioneta;
	}
	
	public void run() {
		while(true)
		{
		this.laCamioneta.esperarEnOrigen();
		this.laCamioneta.viajar();
		this.laCamioneta.esperarEnDestino();
		this.laCamioneta.viajar();
		}
	}
}
