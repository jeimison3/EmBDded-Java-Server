package init;

import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JTextArea;

import DB.DBConnection;
import remote.SocketsServer;

public class ServicesInit {
	
	public DBConnection mysql = null;
	public SocketsServer controlador = null;
	protected int porta;
	private JTextArea logArea;

	public ServicesInit(int porta) {
		this.porta = porta;
	}
	
	public boolean start(JTextArea logArea) {
		this.logArea = logArea;
		logArea.append("Conectando com DB...");
		this.mysql = new DBConnection("root", "");
		int cont = 0;
		while(!mysql.conecta()) {
			logArea.append("BD: FALHA\nTentando novamente...");
			if(cont++ == 2) {
				return false;
			}
		}
		logArea.append("OK\n");
		
		logArea.append("Iniciando socket...");
		this.controlador = new SocketsServer(logArea, this.porta, this.mysql.sql);
		if(controlador.iniciar()) logArea.append("OK\n");
		else logArea.append("FALHA\n");
		
		return true;
		
	}
	
	public void parar() {
		try {
			this.mysql.sql.close();
			this.controlador.parar();
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

}
