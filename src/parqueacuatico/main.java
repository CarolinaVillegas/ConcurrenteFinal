package parqueacuatico;
/**
 *
 * @author Carolina, Kevin
 */
public class main {

    public static void main(String[] args) {
        Reloj unReloj = new Reloj();
        new Thread(unReloj).start();

        Parque elParque = new Parque(unReloj);

        int cantColectivos = 2; //podrían haber más coles        
        //Son 30 como minimo mas que nada para que el nado de delfines pueda llegar a suceder en algun momento.
        //int cantVisitantes = (int) (Math.random() * 70) + 30; 
        int min=30;
        //desde min hasta 50 números más, es decir hasta 79
        int cantVisitantes = (int)((Math.random() * 50) + min);//el máximo va a ser 79
        System.out.println("EL RANDOOOOM MEEEE DIOOOOO "+cantVisitantes+" la cantidad de visitantes es de "+(cantVisitantes-min));
        //Cargo Colectivos y colectiveros
        int cantAsientosColectivo = 25;
        TransporteHora[] colectivos = new TransporteHora[cantColectivos];
        Thread[] colectiveros = new Thread[cantColectivos];
        //Horarios de salida de los colectivos
        int[] horarios = {10, 11, 12, 14, 16};
        
        //Inicializo los colectivos
        for (int i = 0; i < cantColectivos; i++) {
            colectivos[i] = new TransporteHora("COLECTIVO " + i, cantAsientosColectivo, unReloj, horarios);
            colectiveros[i] = new Thread(new Chofer("Chofer_de_Colectivo " + i, colectivos[i]));
            colectiveros[i].start();
        }
        //Cargo los visitantes en threads
        Thread[] hilosVisitantes = new Thread[cantVisitantes];
        Visitante[] losVisitantes = new Visitante[cantVisitantes];

        for (int i = min; i < cantVisitantes; i++) {
            //Cargo los visitantes con sus variables
            losVisitantes[i] = new Visitante("" + i, elParque, colectivos[i % cantColectivos]);
            hilosVisitantes[i] = new Thread(losVisitantes[i]);
            hilosVisitantes[i].start();
        }
    }

}
