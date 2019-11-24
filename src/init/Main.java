package init;

import DB.DBConnection;
import remote.SocketsController;

public class Main {
	
	static int porta = 8000;

	public static void main(String[] args) {
		
		System.out.print("Iniciando socket...");
		SocketsController controlador = new SocketsController(porta);
		if(controlador.iniciar()) System.out.println("OK");
		else System.out.println("FALHA");
		
		System.out.print("Conectando com DB...");
		DBConnection mysql = new DBConnection("root", "");
		while(!mysql.conecta()) {
			System.out.print("FALHA\nTentando novamente...");
		}
		System.out.println("OK");
		
	}

}
