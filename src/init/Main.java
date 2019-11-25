package init;

import DB.DBConnection;
import remote.SocketsServer;

public class Main {
	
	static int porta = 8000;

	public static void main(String[] args) {
		
		System.out.print("Conectando com DB...");
		DBConnection mysql = new DBConnection("root", "");
		while(!mysql.conecta()) {
			System.out.print("FALHA\nTentando novamente...");
		}
		System.out.println("OK");
		
		System.out.print("Iniciando socket...");
		SocketsServer controlador = new SocketsServer(porta, mysql.sql);
		if(controlador.iniciar()) System.out.println("OK");
		else System.out.println("FALHA");
		
		
		
	}

}
