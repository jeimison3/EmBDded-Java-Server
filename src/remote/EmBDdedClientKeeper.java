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

public class EmBDdedClientKeeper implements Runnable {
	private String DedicatedHostIP;
	private EmBDdedClient cliente;
	public Socket conexao;
	private Connection db;
	public DataOutputStream saida;
	public BufferedReader entrada;
	private String bytesResto = "";
	
	public EmBDdedClientKeeper(Socket connection, Connection db) throws IOException {
		this.db = db;
		this.cliente = new EmBDdedClient(connection, this);
		this.conectaSocket(connection);
	}
	
	public boolean itsMe(String addr) {
		//System.out.println("("+addr+")? Sou "+this.DedicatedHostIP);
		return this.DedicatedHostIP.contentEquals(addr);
	}
	
	public void conectaSocket(Socket S) throws UnsupportedEncodingException, IOException {
		this.conexao = S;
		this.DedicatedHostIP = SocketsServer.GetSocketIP(S);
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
			if(!this.conexao.isClosed()) this.conexao.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	private EmBDdedMessage extraiProximaMensagem(String novaEntrada) {
		// Concatena buffer e entrada
		String entradaMsgTotal = this.bytesResto+novaEntrada;
		
		// Busca ENDL
		int endl = entradaMsgTotal.indexOf( EmBDdedMessage.MESSAGE_ENDL );

		// Sem endl, espera próxima iteração
		if(endl == -1) {
			System.out.println("Incompleto: "+entradaMsgTotal);
			this.bytesResto = entradaMsgTotal;
			return null;
		}
		
		String toProcess = entradaMsgTotal.substring(0, endl);
		
		this.bytesResto = entradaMsgTotal.substring(endl+1, entradaMsgTotal.length());
		
		// Retorna mensagem processada em LOW-LEVEL
		return new EmBDdedMessage(toProcess);
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
					
					String newInput ="";	
					if(tam > 0) newInput = String.copyValueOf(reader, 0, tam);

					EmBDdedMessage messg;
					if((messg = extraiProximaMensagem(newInput)) != null) {
						// Cliente interpreta em HIGH-LEVEL
						this.cliente.newMessage(messg);
					}
					
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
