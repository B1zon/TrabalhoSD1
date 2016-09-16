/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Socket;

/**
 *
 * @author Gabriel Garcia - Eduardo Nardi
 */
public class Generico {

    private String nome;
    private String palavra;
    private String letra;
    private int pontuação;
    private char[] erros;
    private String [] lista;

    public String getNome() {
        return nome;
    }

    public String getPalavra() {
        return palavra;
    }

    public String getLetra() {
        return letra;
    }

    public int getPontuação() {
        return pontuação;
    }

    public char[] getErros() {
        return erros;
    }

    public String[] getLista() {
        return lista;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setPalavra(String palavra) {
        this.palavra = palavra;
    }

    public void setLetra(String letra) {
        this.letra = letra;
    }

    public void setPontuação(int pontuação) {
        this.pontuação = pontuação;
    }

    public void setErros(char[] erros) {
        this.erros = erros;
    }    
}
