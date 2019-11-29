package Classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import EmBDded.EmBDdedMessage;
import remote.EmBDdedClientKeeper;

public class Estado extends Atributo {
	public String valor;
	public boolean exportar;

	
	public Estado(Atributo atr) {
		super(atr.atribid, atr.nome, atr.tipo);
	}
	

	public String getValor() {
		return valor;
	}
	
	@Override
	public String toString() {
		String tmpValor = (this.valor == null ? String.valueOf(EmBDdedMessage.MESSAGE_RESPONSE_ERROR) : this.valor );
		char clientNeedExport = (this.exportar ? (char) 2 : (char) 1 );
		return String.format( "%c%c%c%s%c%s" , EmBDdedMessage.MESSAGE_CLIENT_SET_ESTADO, this.getTipo(), clientNeedExport, this.nome , EmBDdedMessage.MESSAGE_NEXTPARAM , tmpValor );
	}
	
}
