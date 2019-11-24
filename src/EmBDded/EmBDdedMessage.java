package EmBDded;




public class EmBDdedMessage {
	
	public static final char MESSAGE_UNKNOWN = (char) 0;
	
	public static final char MESSAGE_CLIENT_NAME = (char) 1;
	
	public static final char MESSAGE_CLIENT_PUB_NEW_ESTADO = (char) 6;
	
	public static final char MESSAGE_CONNECTION_CLOSE = (char) -1;
	
	public static final char MESSAGE_ENDL = (char) 13;
	
	
	private char MessageType;
	public String dataStr1, dataStr2, dataStr3, dataStr4;
	public int dataInt1, dataInt2, dataInt3, dataInt4;
	public boolean dataBool1, dataBool2, dataBool3, dataBool4;
	
	private String entrada;
	
	public EmBDdedMessage proxima;
	public EmBDdedMessage incompleta;
	
	
	public EmBDdedMessage() {
		
	}
	
	public EmBDdedMessage(String input) {
		this.entrada = input;
		this.processaIn();
	}
	
	public char getType() {
		return this.MessageType;
	}
	
	
	
	private void processaIn() {
		// Tratamento de mensagens LOW-LEVEL do server-side
		char headerOp = this.entrada.charAt(0);
		//System.out.println(((int)headerOp)+">"+this.entrada.substring(1));
		
		switch(headerOp) {
		case EmBDdedMessage.MESSAGE_CLIENT_NAME:
			this.MessageType = EmBDdedMessage.MESSAGE_CLIENT_NAME;
			this.dataStr1 = this.entrada.substring(1);
			break;
			
		case EmBDdedMessage.MESSAGE_CLIENT_PUB_NEW_ESTADO:
			this.MessageType = EmBDdedMessage.MESSAGE_CLIENT_PUB_NEW_ESTADO;
			this.dataStr1=this.entrada.substring(2);
			String typAtrib = "";
			switch(this.entrada.charAt(1)) {
				case 1: typAtrib = "BOOL"; break;
	            case 2: typAtrib = "INT"; break;
	            case 3: typAtrib = "STR"; break;
	            case 4: typAtrib = "FLOAT"; break;
	            default: typAtrib = "VOID";
			}
			this.dataStr2=typAtrib;
			break;
			
		case EmBDdedMessage.MESSAGE_CONNECTION_CLOSE:
			this.MessageType = EmBDdedMessage.MESSAGE_CONNECTION_CLOSE;
			break;
			
		default:
			System.out.println("Mensagem inesperada: "+((int)headerOp)+".");
		}
		
		//System.out.println("Recebido: "+this.entrada);
		
	}

}
