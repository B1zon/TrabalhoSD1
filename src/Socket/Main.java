/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Socket;

import java.net.UnknownHostException;

/**
 *
 * @author Gabriel Garcia - Eduardo Nardi
 */
public class Main {
       public static void main (String [] arg) throws UnknownHostException{
           Controlador c = new Controlador();
           c.jogo();
       }
}
