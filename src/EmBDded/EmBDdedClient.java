package EmBDded;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;

import remote.EmBDdedClientKeeper;

public class EmBDdedClient {
	private Socket socket;
	private EmBDdedClientKeeper thread;
	private Connection db;
	public Cliente cliente = null;
	
	
	public EmBDdedClient(Socket socket, EmBDdedClientKeeper runnable, Connection db) {
		this.socket = socket;
		this.thread = runnable;
		this.db = db;
		this.cliente = new Cliente(this.db);
	}
	
	void addAtributo(String nome, String tipo) {
		if(!this.cliente.haAtributo(nome))
			this.cliente.addAtributo(nome, tipo);
	}
	
	
	public void newMessage(EmBDdedMessage msg) throws UnsupportedEncodingException {
		
		// Tratamento de mensagens HIGH-LEVEL do server-side
		
		switch(msg.getType()) {
		case EmBDdedMessage.MESSAGE_CLIENT_NAME:
			System.out.println("Logado como: "+msg.dataStr1);
			this.cliente.setClientname(msg.dataStr1);
			break;
		case EmBDdedMessage.MESSAGE_CLIENT_PUB_NEW_ESTADO:
			this.addAtributo(msg.dataStr1, msg.dataStr2);
			//System.out.println(msg.dataStr1+" do tipo "+msg.dataStr2);
			break;
		case EmBDdedMessage.MESSAGE_CONNECTION_CLOSE:
			System.out.println("Socket> Desconectado.");
			try {
				if(!this.socket.isInputShutdown()) this.socket.shutdownInput();
				if(!this.socket.isOutputShutdown()) this.socket.shutdownOutput();
				if(!this.socket.isClosed()) this.socket.close();
			}catch(IOException a) {a.printStackTrace();}
			//this.thread.conexao.
			break;
			
		default:
			System.out.println("Mensagem inesperada: "+((int)msg.getType())+".");			
		}
		
	}

}
