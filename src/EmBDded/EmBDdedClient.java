package EmBDded;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import remote.EmBDdedClientKeeper;

public class EmBDdedClient {
	private Socket socket;
	private EmBDdedClientKeeper thread;
	public String clientName;
	
	public EmBDdedClient(Socket socket, EmBDdedClientKeeper runnable) {
		this.socket = socket;
		this.thread = runnable;
	}
	
	public void newMessage(EmBDdedMessage msg) throws UnsupportedEncodingException {
		
		// Tratamento de mensagens HIGH-LEVEL do server-side
		
		switch(msg.getType()) {
		case EmBDdedMessage.MESSAGE_CLIENT_NAME:
			// DB aqui..
			System.out.println("Logado como: "+msg.dataStr1);
			this.clientName = msg.dataStr1;
			
			this.thread.sendMessage("OLAAAAAAAAAA " + this.clientName);
			//this.thread.release();
			
			break;
		case EmBDdedMessage.MESSAGE_CLIENT_PUB_NEW_ESTADO:
			// DB aqui
			System.out.println(msg.dataStr1+" do tipo "+msg.dataStr2);
			break;
		case EmBDdedMessage.MESSAGE_CONNECTION_CLOSE:
			System.out.println("Socket> Desconectado.");
			try {
				if(!this.socket.isInputShutdown()) this.socket.shutdownInput();
				if(!this.socket.isOutputShutdown()) this.socket.shutdownOutput();
				if(!this.socket.isClosed()) this.socket.close();
			}catch(IOException a) {a.printStackTrace();}
			
			break;
			
		default:
			System.out.println("Mensagem inesperada: "+((int)msg.getType())+".");			
		}
		
	}

}
