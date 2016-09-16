/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 *
 * @author Gabriel Garcia - Eduardo Nardi
 */
public class Controlador {

    private MulticastSocket s;
    private DatagramPacket messageIn, messageOut;
    private InetAddress group;
    private int jogadas;
    private Integer id;
    private int numMsg;
    private int flag;
    private String senha;
    private String tenta;
    private int qtd;
    private int contaErros;
    private HashMap<Integer, String> lista = null;

    public void jogo() throws UnknownHostException {

        Scanner sc = new Scanner(System.in); // scanner para escrita
        group = InetAddress.getByName("229.8.7.6"); //Ip para o multicast
        jogadas = 0;                // numero de jogadas
        id = 0;                     // id para cada processo
        numMsg = 0;                 // numero de mensagens para selecionar o gerador
        flag = 1;                   // 1 para os jogadores e 0 para o gerador
        int fim = 0;                // 1 finaliza a partida 0 continua 
        qtd = 0;
        contaErros = 0;
        String aux;
        String boneco = "\0";
        boolean acertou = false;
        Generico g1 = new Generico(); // objeto para jogador e gerador 
        lista = new HashMap<>();      // lista dos processos  
        try {
            s = new MulticastSocket(6789);
            s.joinGroup(group);
            byte[] m = new byte[1000];
            messageOut = new DatagramPacket(m, m.length, group, 6789);

            // Condições de começo de jogo
            if (jogadas == 0) {
                String nome = JOptionPane.showInputDialog(null, "--- Jogo da Forca --- \nConfigurando...\nDigite o seu apelido").toLowerCase();
                g1.setNome(nome);
                m = nome.getBytes();
                messageOut = new DatagramPacket(m, m.length, group, 6789);
                s.send(messageOut);
                System.out.println("nome: " + g1.getNome());
            }
            String receive = " ";

            do {
                byte[] buffer = new byte[1000];
                messageIn = new DatagramPacket(buffer, buffer.length);
                s.receive(messageIn);
                receive = new String(messageIn.getData());
                receive = receive.replaceAll("\\W", "");

                if (receive.equals("acabou")) {         //mensagem que o gerador manda quando descobre q eh o gerador
                    flag = 1;
                    if (numMsg == 2) {        //tem tres programas, um vai receber as mensagens dos outros dois pq vai rodar primeiro. O segundo vai receber a mensagem soh do ultimo e o ultimo n vai receber de ninguem, por isso so vai ter ele na lista. No caso deste if, estamos falando do segundo programa.
                        s.receive(messageIn);  //ele tem q receber o nome do gerador ainda, por isso mais um receive.
                        receive = new String(messageIn.getData(), 0, messageIn.getLength());
                        receive = receive.replaceAll("\\W", "");   //tem q adicionar esse na lista ainda
                        System.out.println("Receive: " + receive);

                        s.send(messageOut);        //manda mais uma mensagem pq o ultimo tem que receber o nome dele ainda.
                        id = 1;
                        break;
                    } else if (numMsg == 1) { // o ultimo programa.
                        s.receive(messageIn);
                        receive = new String(messageIn.getData(), 0, messageIn.getLength());
                        receive = receive.replaceAll("\\W", "");   //tem q adicionar esse na lista ainda
                        System.out.println("Receive: " + receive);

                        s.receive(messageIn);  //dois receives pq tem q receber mensagem do gerador e do segundo programa.
                        receive = new String(messageIn.getData(), 0, messageIn.getLength());
                        receive = receive.replaceAll("\\W", "");   //tem q adicionar esse na lista ainda
                        System.out.println("Receive: " + receive);
                        id = 2;
                        break;
                    }
                }
                lista.put(id, receive);        // acrescenta o nome dos jogadores na lista
                numMsg++;
                id++;

                System.out.println("Receive: " + receive);

                if (numMsg == 3) {  //esse eh o gerador recebeu as tres mensagens.
                    flag = 0;
                    //System.out.println("Você é o gerador!!");  
                    for (int i = 0; i < m.length; i++) {
                        m[i] = 0;
                    }
                    aux = "acabou"; //envia acabou para avisar que o gerador ja foi escolhido.
                    m = aux.getBytes();
                    messageOut = new DatagramPacket(m, m.length, group, 6789);
                    s.send(messageOut);

                    for (int i = 0; i < m.length; i++) {
                        m[i] = 0;
                    }
                    m = g1.getNome().getBytes();
                    messageOut = new DatagramPacket(m, m.length, group, 6789);
                    s.send(messageOut);
                    id = 0;
                    break;
                }

            } while (true);
            // Aqui começa deveras o jogo
            do {
                // Se o jogador for o gerador
                if (flag == 0) {
                    byte[] m1 = new byte[1000];
                    String palavra = JOptionPane.showInputDialog(null, g1.getNome() + "\nDigite a palavra a ser descoberta").toLowerCase();
                    g1.setPalavra(palavra);
                    m1 = palavra.getBytes();
                    messageOut = new DatagramPacket(m1, m1.length, group, 6789);
                    s.send(messageOut);                 // envia a palavra para as jogadores
                    for (Integer key : lista.keySet()) {
                        System.out.println(key + " " + lista.get(key));
                    }
                    System.out.println("mensagem do gerador: " + g1.getPalavra());
                // Caso seja o jogador    
                } else {
                    byte[] buffer = new byte[1000];
                    messageIn = new DatagramPacket(buffer, buffer.length);
                    s.receive(messageIn);
                    receive = new String(messageIn.getData());
                    receive = receive.replaceAll("\\W", "");
                    System.out.println(receive);
                    char formaPalavra[] = new char[receive.length()]; 
                    String apresentaPalavra = "\0";
                    for (int y = 0; y < receive.length(); y++) { // forma a palavra com os underlines no popup
                        formaPalavra[y] = '_';
                        apresentaPalavra += formaPalavra[y] + " ";
                        System.out.print(apresentaPalavra);
                    }
                    for (int j = 0; j < (g1.getPalavra().length()+5); j++) //tentativas = qtd de letras + 5 erros
                    {
                        acertou = false;                        // condição de acerto da letra
                        senha = JOptionPane.showInputDialog(null, "Tentativa: " + (j + 1) + " de " + (receive.length()) + "\n" + apresentaPalavra + "\nDigite a letra:");
                        apresentaPalavra = "\0";
                        for (int i = 0; i < receive.length(); i++) {
                            tenta = receive.substring(i, i + 1);
                            if (senha.trim().equals(tenta)) {
                                acertou = true;
                                qtd++;                          // quantidade de letras
                                formaPalavra[i] = senha.charAt(0); //converte de String para char
                                apresentaPalavra += formaPalavra[i] + " ";
                                if (qtd == receive.length()) {    // aqui ainda é só para testes, caso o jogador ganhe
                                    JOptionPane.showMessageDialog(null, "Você Ganhou! Palavra Completa: " + receive + "\nTotal de Tentativas: " + (j + 1));
                                    System.exit(0);
                                }
                            } else {
                                apresentaPalavra += formaPalavra[i] + " ";
                                g1.setErros(formaPalavra);
                            }
                        }
                        // Caso a letra seja errado ele começa o boneco
                        if (acertou == false) {
                            contaErros++;
                            boneco = "\0";
                            switch (contaErros) {
                                case 1:
                                    boneco = "___O \n";
                                    boneco += "|\t \n";
                                    boneco += "|\t \n";
                                    boneco += "|\t \n";
                                    break;
                                case 2:
                                    boneco = "___O \n";
                                    boneco += "|       | \n";
                                    boneco += "|\n";
                                    boneco += "|\n";
                                    break;
                                case 3:
                                    boneco = "___O \n";
                                    boneco += "|      /|\n";
                                    boneco += "|\n";
                                    boneco += "|\n";
                                    break;
                                case 4:
                                    boneco = "___O \n";
                                    boneco += "|      /|\\ \n";
                                    boneco += "|\n";
                                    boneco += "|\n";
                                    break;
                                case 5:
                                    boneco = "___O \n";
                                    boneco += "|      /|\\\n";
                                    boneco += "|      / \n";
                                    boneco += "|\n";
                                    break;
                                case 6:
                                    boneco = "___O \n";
                                    boneco += "|      /|\\\n";
                                    boneco += "|      / \\ \n";
                                    boneco += "|\n";
                                    break;
                            }
                            JOptionPane.showMessageDialog(null, boneco + "\nVocê ERROU " + contaErros + " vez(es)\n" + g1.getErros());
                            if (contaErros >= 6) {
                                JOptionPane.showMessageDialog(null, "PERDEU! FIM DO JOGO");
                                System.exit(0);
                            }
                        }
                    }
                }
            } while (true);
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }
}
