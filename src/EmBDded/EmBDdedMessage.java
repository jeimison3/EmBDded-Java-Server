package EmBDded;




public class EmBDdedMessage {
	
	public static final char MESSAGE_UNKNOWN = (char) 0; // Mensagem desconhecida
	
	public static final char MESSAGE_CLIENT_NAME = (char) 1; // Autenticação com nome de cliente
	
	public static final char MESSAGE_CLIENT_RECV_SELF_ESTADO = (char) 5; // Solicitação de Estado R:[7]
	public static final char MESSAGE_CLIENT_PUB_NEW_ATRIBUTO = (char) 6; // Publicação de Atributo
	
	public static final char MESSAGE_CLIENT_SET_ESTADO = (char) 7; // Ordem de valor para Estado
	public static final char MESSAGE_CLIENT_SET_EXPORT = (char) 8; // Ordem de exportar INPUT para Atributo
	public static final char MESSAGE_CLIENT_PUB_ESTADO = (char) 9; // Publicação de Estado
	
	
	public static final char MESSAGE_CONNECTION_CLOSE = (char) 255; // Finaliza socket no server-side
	
	public static final char MESSAGE_ENDL = (char) 13; // Fim de mensagem
	public static final char MESSAGE_NEXTPARAM = (char) 14; // Pŕoximo parâmetro
	
	public static final char MESSAGE_RESPONSE_ERROR = (char) 16; // Erro na resposta
	
	
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
			
		case EmBDdedMessage.MESSAGE_CLIENT_RECV_SELF_ESTADO:
			this.MessageType = EmBDdedMessage.MESSAGE_CLIENT_RECV_SELF_ESTADO;
			this.dataStr1=this.entrada.substring(1);
			break;
			
		case EmBDdedMessage.MESSAGE_CLIENT_PUB_ESTADO:
			this.MessageType = EmBDdedMessage.MESSAGE_CLIENT_PUB_ESTADO;
			int nxP = this.entrada.indexOf(EmBDdedMessage.MESSAGE_NEXTPARAM);
			this.dataStr1=this.entrada.substring(1, nxP);
			this.dataStr2=this.entrada.substring(nxP+1);
			System.out.println(this.dataStr1+" tem valor: "+this.dataStr2);
			break;
			
		case EmBDdedMessage.MESSAGE_CLIENT_PUB_NEW_ATRIBUTO:
			this.MessageType = EmBDdedMessage.MESSAGE_CLIENT_PUB_NEW_ATRIBUTO;
			String typAtrib = "";
			switch(this.entrada.charAt(1)) {
				case 1: typAtrib = "BOOL"; break;
	            case 2: typAtrib = "INT"; break;
	            case 3: typAtrib = "STR"; break;
	            case 4: typAtrib = "FLOAT"; break;
	            default: typAtrib = "VOID";
			}
			this.dataStr2=typAtrib;
			this.dataBool1 = (this.entrada.charAt(2) == (char) 2); // EXPORT
			this.dataStr1=this.entrada.substring(3);
			
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
