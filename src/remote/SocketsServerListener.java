package remote;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.util.ArrayList;

import javax.swing.JTextArea;

class SocketsServerListener implements Runnable {
	private ServerSocket servidor;
	private Connection database;
	private JTextArea logArea;
	
	private ArrayList<EmBDdedClientKeeper> clientes = new ArrayList<EmBDdedClientKeeper>();
	
	public SocketsServerListener(JTextArea logArea, ServerSocket servidor, Connection db) throws SocketException {
		this.logArea = logArea;
		this.database = db;
		this.servidor = servidor;
		this.servidor.setSoTimeout(0); // Sem timeout
	}
	
	public void addLog(String msgLog) {
		this.logArea.append(msgLog+'\n');
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
	
	public EmBDdedClientKeeper getClientByName(String clientname) {
		for(EmBDdedClientKeeper k : this.clientes) 
			if( k.getClientName().contentEquals(clientname) ) {
				return k;
			}
		return null;
	}
	
	public boolean finalizaCliente(Socket s) {
		int idx = getThreadClientId( SocketsServer.GetSocketIP(s) );
		if(idx == -1) return false;
		
		this.clientes.remove(idx);
		return true;
		
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				if(this.servidor.isClosed()) break; // Termina threads
				Socket client = this.servidor.accept();
				// Pra cada conexão, direciona a uma [nova] thread
				
				int idxCli = getThreadClientId(SocketsServer.GetSocketIP(client));
				if(idxCli == -1) { // Nova conexão
					this.logArea.append("Listener> Nova conexão.\n");
					EmBDdedClientKeeper threadSkt = new EmBDdedClientKeeper(client, this.database, this);
					new Thread(threadSkt).start();
					this.clientes.add(threadSkt);
				} else { // Reconexão
					this.logArea.append("Listener> Reconexão.\n");
					this.clientes.get(idxCli).conectaSocket(client);
					//this.clientes.get(idxCli).forceUpdateEstados();
				}
				System.out.println("Listener> Threads: "+this.clientes.size());
				
			} catch (IOException e) {
				e.printStackTrace();
				e.getSuppressed();
			}
		}
	}
	
}


