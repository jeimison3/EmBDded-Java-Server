package remote;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.util.ArrayList;

public class SocketsController {
	private int porta;
	
	public ServerSocket servidor;
	private Connection mysql = null;
	
	public SocketsController(int port) {
		this.porta = port;
	}
	
	public static String GetSocketIP(Socket s) {
		return ((InetSocketAddress)s.getRemoteSocketAddress() ).getAddress().toString().split("/")[1];
	}
	
	public void setDatabase(Connection db) {
		this.mysql = db;
	}
	
	public boolean iniciar() {
		try {
			// Cria servidor
			this.servidor = new ServerSocket(this.porta);
			
			// Cria thread receptora
			Runnable receptor = new SocketListener(this.servidor, this.mysql);
			new Thread(receptor).start();
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}

class SocketListener implements Runnable {
	private ServerSocket servidor;
	private Connection database;
	
	private ArrayList<EmBDdedSocket> clientes = new ArrayList<EmBDdedSocket>();
	
	public SocketListener(ServerSocket servidor, Connection db) throws SocketException {
		this.database = db;
		this.servidor = servidor;
		this.servidor.setSoTimeout(0); // Sem timeout
	}
	
	public int getThreadClientId(String addr) {
		int ret = -1;
		for(int i=0; i < this.clientes.size(); i++) 
			if( this.clientes.get(i).itsMe(addr) ) {
				ret = i;
				break;
			}
		return ret;
	}
	
	public boolean clienteDesconectado(Socket s) {
		int idx = getThreadClientId( SocketsController.GetSocketIP(s) );
		if(idx == -1) return false;
		
		this.clientes.remove(idx);
		return true;
		
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Socket client = this.servidor.accept();
				// Pra cada nova conexão, direciona a uma thread
				
				int idxCli = getThreadClientId(SocketsController.GetSocketIP(client));
				if(idxCli == -1) { // Nova conexão
					EmBDdedSocket threadSkt = new EmBDdedSocket(client, this.database);
					new Thread(threadSkt).start();
					this.clientes.add(threadSkt);
				} else { // Reconexão
					this.clientes.get(idxCli).conectaSocket(client);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				e.getSuppressed();
			}
		}
	}
	
}

