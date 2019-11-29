package remote;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.sql.Connection;

import Classes.Estado;
import DAO.EstadoDAO;
import EmBDded.EmBDdedClient;
import EmBDded.EmBDdedMessage;

// Classe individual de cada conexão socket

public class EmBDdedClientKeeper implements Runnable {
	private String DedicatedHostIP;
	private EmBDdedClient cliente;
	private SocketsServerListener listener;
	public Socket conexao;
	private Connection db;
	public DataOutputStream saida;
	public BufferedReader entrada;
	private String bytesResto = "";
	
	public EmBDdedClientKeeper(Socket connection, Connection db, SocketsServerListener listener) throws IOException {
		this.db = db;
		this.listener = listener;
		this.conectaSocket(connection);
		this.cliente = new EmBDdedClient(this, db);
	}
	
	public String getClientName() {
		return this.cliente.cliente.getClientname();
	}
	
	public boolean itsMe(String addr) {
		return this.DedicatedHostIP.contentEquals(addr);
	}
	
	public void conectaSocket(Socket S) throws UnsupportedEncodingException, IOException {
		this.conexao = S;
		this.DedicatedHostIP = SocketsServer.GetSocketIP(S);
		this.entrada = new BufferedReader( new InputStreamReader(S.getInputStream(), "Cp1252") );
		this.saida = new DataOutputStream(S.getOutputStream());
	}
	
	public void forceUpdateEstados() {
		cliente.forceRecvSelfEstado();
	}
	
	public void forcaInterpretaMsg(EmBDdedMessage msg) {
		try {
			this.cliente.newMessage(msg);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(String msg) {
		try {
			this.saida.write( msg.concat(String.valueOf(EmBDdedMessage.MESSAGE_ENDL)).getBytes("Cp1252") );
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
	
	private EmBDdedMessage extraiProximaMensagem() {
		
		// Busca ENDL
		int endl = this.bytesResto.indexOf( EmBDdedMessage.MESSAGE_ENDL );

		// Sem endl, espera próxima iteração
		if(endl == -1) {
			//System.out.println("Incompleto: "+this.bytesResto);
			return null;
		}
		
		String toProcess = this.bytesResto.substring(0, endl);
		
		
		this.bytesResto = this.bytesResto.substring(endl+1, this.bytesResto.length());
		
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
					
					// Concatena buffer e entrada
					this.bytesResto += newInput;
					
					
					while((messg = extraiProximaMensagem()) != null) {
						
						switch(messg.getType()) {
						case EmBDdedMessage.MESSAGE_SERVER_SET_ESTADO:{
							EmBDdedClientKeeper k = this.listener.getClientByName(messg.dataStr1);
							
							EstadoDAO e = null;
							if(k != null) {
								e = k.cliente.cliente.getEstado(messg.dataStr2);
							}
							if(e == null) {
								System.out.println("ERR: Cliente desconectado ou atributo desconhecido.");
								continue;
							}
							this.listener.addLog("TOOL> Ordem para "+k.getClientName() + " | "+(messg.dataBool1?"IN":"OUT")+" | "+e.nome+"="+messg.dataStr3);
							if(!messg.dataBool1) { // SAIDA
								e.setExport(false);
								e.setValor(messg.dataStr3);
								e.preformEnvioEstadoToCliente();
							} else { // Entrada / EXPORTAR
								e.setExport(true);
								e.preformEnvioEstadoToCliente();
							}
							
							break;
						}
						default:
							// Cliente interpreta em HIGH-LEVEL
							this.cliente.newMessage(messg);
							break;
						}
						
						
						
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
