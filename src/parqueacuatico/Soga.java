/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parqueacuatico;

import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Carolina
 */
public class Soga{

    private Semaphore lugarSoga = new Semaphore(2);
    private Semaphore mutex1 = new Semaphore(1);
    private Semaphore mutex2 = new Semaphore(1);

    private boolean sogaLadoOeste = false;
    private Visitante[] visitantesConSoga = new Visitante[2];
    private int lugar = 0;
    private String nombreSoga;

    public Soga(String nombre) {
        this.nombreSoga = nombre;

    }
    public void saltarSolo(Visitante unVisitante){
        String id = unVisitante.getNombreCompleto();
        try {           
            System.out.println("El " + id + " llegó a la plataforma y se fija si puede tomar la soga (SOLO) o si hay más gente esperando");
            this.mutex1.acquire();
            this.lugarSoga.acquire(2);
            System.out.println("El " + id + " esta tomando la soga y QUIERE IR SOLO");            
            Thread.sleep(1000);
            visitantesConSoga[this.lugar] = unVisitante;
            
            System.out.println("El " + id + " ya posee la soga");
           // this.mutex1.release();
            if(lugarSoga.availablePermits()==0){
                this.lugar=0;
                this.irEsteOeste(unVisitante.getQuierePareja());
            }this.mutex1.release();
        } catch (InterruptedException ex) {
        }
    }
    
    
    public void saltar(Visitante unVisitante) {//van a hacer los saltos desde el este al oeste
        String id = unVisitante.getNombreCompleto();
        try {
           
            System.out.println("El " + id + " llegó a la plataforma y se fija si puede tomar la soga (CON ALGUIEN MAS) o si hay más gente esperando");
            this.mutex1.acquire();
            this.lugarSoga.acquire();
            System.out.println("El " + id + " esta tomando la soga");            
            Thread.sleep(1000);
            visitantesConSoga[this.lugar] = unVisitante;
            this.lugar++;
            System.out.println("El " + id + " ya posee la soga");

            if(lugarSoga.availablePermits()==0){
                this.lugar=0;
                this.irEsteOeste(unVisitante.getQuierePareja());
            }this.mutex1.release();
        } catch (InterruptedException ex) {
        }
    }

    private void irEsteOeste(boolean enPareja) {
        try {
          //  this.moverse.release();
            if (enPareja==false) {
                System.out.println("La SOGA esta cruzando AL OESTE con " + visitantesConSoga[0].getNombreCompleto());
                Thread.sleep(2500);
                System.out.println("La SOGA ha llegado AL OESTE con " + visitantesConSoga[0].getNombreCompleto());
            } else {
                System.out.println("La SOGA esta cruzando AL OESTE con " + visitantesConSoga[0].getNombreCompleto() + " y " + visitantesConSoga[1].getNombreCompleto());
                Thread.sleep(2500);
                System.out.println("La SOGA ha llegado AL OESTE con " + visitantesConSoga[0].getNombreCompleto() + " y " + visitantesConSoga[1].getNombreCompleto());

            }
            this.bajar(enPareja);
        } catch (InterruptedException ex) {
            Logger.getLogger(MundoAventura.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

   

    public void bajar(boolean enPareja) {
        try {            
            this.mutex2.acquire();
            if (enPareja==false) {
                System.out.println("El visitante " + visitantesConSoga[this.lugar].getNombreCompleto() + "esta bajando");
                Thread.sleep(1000);
                System.out.println("El visitante " + visitantesConSoga[this.lugar].getNombreCompleto() + " ha bajado");
                //this.lugarSoga.release(2);
            } else {
                System.out.println("El visitante " + visitantesConSoga[this.lugar].getNombreCompleto() + " y "+ visitantesConSoga[this.lugar+1].getNombreCompleto()+" estan bajando");
                Thread.sleep(1000);
                System.out.println("El visitante " + visitantesConSoga[this.lugar].getNombreCompleto() +" y "+ visitantesConSoga[this.lugar+1].getNombreCompleto()+"  han bajado");
            }
         
           this.lugarSoga.release(2);
            if (lugarSoga.availablePermits()==2) {
                this.volverEste();
            }
            this.lugar=0;
            this.mutex2.release();
        } catch (InterruptedException ex) {
            Logger.getLogger(MundoAventura.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void volverEste() {
        try {
            System.out.println("La soga esta volviendo sin visitantes al lado ESTE");
            Thread.sleep(500);
            System.out.println("La soga ha vuelto sin visitantes al lado ESTE");

        } catch (InterruptedException ex) {
            Logger.getLogger(MundoAventura.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
