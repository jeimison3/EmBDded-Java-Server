package EmBDded;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;

import remote.EmBDdedClientKeeper;

public class EmBDdedClient {
	private EmBDdedClientKeeper thread;
	private Connection db;
	public Cliente cliente = null;
	
	
	public EmBDdedClient(EmBDdedClientKeeper runnable, Connection db) {
		this.thread = runnable;
		this.db = db;
		this.cliente = new Cliente(this.db);
	}
	
	
	
	void addAtributo(String nome, String tipo, boolean exportarDIRECAO) {
		if(!this.cliente.haAtributo(nome)) {
			Atributo a = this.cliente.addAtributo(nome, tipo);
			if(exportarDIRECAO) this.cliente.addEstadoExportar(a);
		}
	}
	
	public void forceRecvSelfEstado() {
		ArrayList<String> estads = cliente.EstadosToString();
		for(String std : estads)
			this.thread.sendMessage( std );
	}
	
	
	public void newMessage(EmBDdedMessage msg) throws UnsupportedEncodingException {
		
		// Tratamento de mensagens HIGH-LEVEL do server-side
		
		switch(msg.getType()) {
		case EmBDdedMessage.MESSAGE_CLIENT_NAME:
			System.out.println("Logado como: "+msg.dataStr1);
			this.cliente.setClientname(msg.dataStr1);
			break;
			
		case EmBDdedMessage.MESSAGE_CLIENT_RECV_SELF_ESTADO:{
			Estado e = this.cliente.getEstado(msg.dataStr1);
			this.thread.sendMessage( e.toString() ); // MESSAGE_CLIENT_SET_ESTADO
			break;
		}
		
		case EmBDdedMessage.MESSAGE_CLIENT_PUB_ESTADO:{
			Estado e = this.cliente.getEstado(msg.dataStr1);
			e.setValor(msg.dataStr2);
			break;
		}
		
		case EmBDdedMessage.MESSAGE_CLIENT_PUB_NEW_ATRIBUTO:
			this.addAtributo(msg.dataStr1, msg.dataStr2, msg.dataBool1);
			//System.out.println(msg.dataStr1+" do tipo "+msg.dataStr2);
			break;
			
		case EmBDdedMessage.MESSAGE_CONNECTION_CLOSE:
			System.out.println("Socket> Desconectado.");
			try {
				if(!this.thread.conexao.isInputShutdown()) this.thread.conexao.shutdownInput();
				if(!this.thread.conexao.isOutputShutdown()) this.thread.conexao.shutdownOutput();
				if(!this.thread.conexao.isClosed()) this.thread.conexao.close();
			}catch(IOException excp) {excp.printStackTrace();}
			//this.thread.conexao.
			break;
			
		default:
			System.out.println("Mensagem inesperada: "+((int)msg.getType())+".");			
		}
		
	}

}
