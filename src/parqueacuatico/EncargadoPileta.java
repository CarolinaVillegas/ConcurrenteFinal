package parqueacuatico;

public class EncargadoPileta implements Runnable{

	private NadoDelfines natatorio;
	private Reloj elReloj;
	
	public EncargadoPileta(NadoDelfines unNadoConDelfines, Reloj unReloj)
	{
		this.natatorio = unNadoConDelfines;
		this.elReloj = unReloj;
	}

	@Override
	public void run() {
		while(true)
		{
			while(elReloj.getHoraActual() >= 9 || elReloj.getHoraActual() < 18)
			{
				this.natatorio.comenzarShow();
			}
			//Una vez que cierra el parque, espera hasta que vuelva a abrir
			elReloj.esperarUnaHora();
		}
	}
	
	
}
