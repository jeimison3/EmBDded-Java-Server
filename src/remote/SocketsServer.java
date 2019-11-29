package remote;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.util.ArrayList;

import javax.swing.JTextArea;

public class SocketsServer {
	private int porta;
	
	public ServerSocket servidor;
	private Connection mysql = null;
	private JTextArea logArea;
	
	public SocketsServer(JTextArea logArea, int port, Connection db) {
		this.logArea = logArea;
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
			SocketsServerListener receptor = new SocketsServerListener(this.logArea, this.servidor, this.mysql);
			new Thread(receptor).start();
			logArea.append("Thread receptora iniciada.\n");
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void parar() throws IOException {
		if(!this.servidor.isClosed()) this.servidor.close();
	}
	
}

