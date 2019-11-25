package remote;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.util.ArrayList;

public class SocketsServer {
	private int porta;
	
	public ServerSocket servidor;
	private Connection mysql = null;
	
	public SocketsServer(int port, Connection db) {
		this.porta = port;
		this.mysql = db;
	}
	
	public static String GetSocketIP(Socket s) {
		return ((InetSocketAddress)s.getRemoteSocketAddress() ).getAddress().toString().split("/")[1];
	}
	
	
	public boolean iniciar() {
		try {
			// Cria servidor
			this.servidor = new ServerSocket(this.porta);
			
			// Cria thread receptora
			Runnable receptor = new SocketsServerListener(this.servidor, this.mysql);
			new Thread(receptor).start();
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}

