package remote;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.sql.Connection;

import EmBDded.EmBDdedClient;
import EmBDded.EmBDdedMessage;

// Classe individual de cada conexão socket

public class EmBDdedSocket implements Runnable {
	private String DedicatedHostIP;
	private EmBDdedClient cliente;
	public Socket conexao;
	private Connection db;
	public DataOutputStream saida;
	public BufferedReader entrada;
	private String bytesResto = "";
	
	public EmBDdedSocket(Socket connection, Connection db) throws IOException {
		System.out.println("Nova conexão!");
		this.db = db;
		this.cliente = new EmBDdedClient(connection, this);
		this.conectaSocket(connection);
	}
	
	public boolean itsMe(String addr) {
		return this.DedicatedHostIP == addr;
	}
	
	public void conectaSocket(Socket S) throws UnsupportedEncodingException, IOException {
		this.conexao = S;
		this.DedicatedHostIP = SocketsController.GetSocketIP(S);
		this.entrada = new BufferedReader( new InputStreamReader(S.getInputStream(), "Cp1252") );
		this.saida = new DataOutputStream(S.getOutputStream());
	}
	
	public void sendMessage(String msg) {
		try {
			this.saida.write( msg.getBytes("Cp1252") );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void release() {
		try {
			this.conexao.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	@Override
	public void run() {
		char[] reader = new char[4096];
		while(true) {
			// Aguarda enquanto socket desconectado.
			if(this.conexao.isClosed() || !this.conexao.isConnected()) continue;
			
			try {
			int tam = 0;
				while ( ((tam = this.entrada.read(reader)) > 0) // Dados lidos ou linhas a processar:
						|| (this.bytesResto.indexOf( EmBDdedMessage.MESSAGE_ENDL ) > 0) ) {
					
					String a ="";	
					if(tam > 0) a = String.copyValueOf(reader, 0, tam);

					// Concatena buffer e entrada
					String entradaMsgTotal = this.bytesResto+a;
					System.out.println("Processar> "+entradaMsgTotal);
					
					// Busca ENDL
					int endl = entradaMsgTotal.indexOf( EmBDdedMessage.MESSAGE_ENDL );
	
					// Sem endl, espera próxima iteração
					if(endl == -1) {
						System.out.println("Incompleto: "+a);
						this.bytesResto = entradaMsgTotal;
						continue;
					}
					
					String toProcess = entradaMsgTotal.substring(0, endl);
					
					this.bytesResto = entradaMsgTotal.substring(endl+1, entradaMsgTotal.length());

					EmBDdedMessage msg = new EmBDdedMessage(toProcess);
					this.cliente.newMessage(msg);
					
					
				}
				
				
				//this.saida.flush();
				//this.saida.writeChars("Olá! É um teste...");
				//this.saida.flush();
				
				//this.saida.write(teste.toCharArray(), 0, teste.length());// (teste);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}

	

}
