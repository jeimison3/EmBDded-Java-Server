package EmBDded;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import remote.EmBDdedClientKeeper;

public class Estado extends Atributo {
	public String valor;
	public boolean exportar;
	private Connection db;
	private EmBDdedClientKeeper thread;
	
	public Estado(Connection db, EmBDdedClientKeeper thread, Atributo atr) {
		super(atr.atribid, atr.nome, atr.tipo);
		this.thread = thread;
		this.db = db;
		this.getDirection();
		this.valor = this.getDBValor();
	}
	

	public String getValor() {
		return valor;
	}
	
	private String getDBValor() {
		try {
			String query = "SELECT valor FROM estados WHERE atribid = ?;";
			PreparedStatement ps = this.db.prepareStatement(query);
			ps.setInt(1, this.atribid);
			ResultSet ret = ps.executeQuery();
			if(ret.next()) {
				String v = ret.getString("valor");
				ps.close();
				return v;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private boolean getDirection() {
		try {
			String query = "SELECT exportar FROM estados WHERE atribid = ?;";
			PreparedStatement ps = this.db.prepareStatement(query);
			ps.setInt(1, this.atribid);
			ResultSet ret = ps.executeQuery();
			if(ret.next()) {
				this.exportar = ret.getBoolean("exportar");
				ps.close();
				return this.exportar;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.exportar = false;
		return false;
	}
	
	public boolean setExport(boolean toExport) {
		try {
			String query = "REPLACE INTO estados(atribid, exportar) VALUES(?, ?)";
			PreparedStatement ps = this.db.prepareStatement(query);
			ps.setInt(1, this.atribid);
			ps.setBoolean(2, toExport);
			int ret = ps.executeUpdate();
			if(ret > 0) {
				this.exportar = toExport;
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean setValor(String valor) {
		try {
			String query = "REPLACE INTO estados(atribid, exportar, valor) VALUES(?,?,?)";
			PreparedStatement ps = this.db.prepareStatement(query);
			ps.setInt(1, this.atribid);
			ps.setBoolean(2, this.exportar);
			ps.setString(3, valor);
			int ret = ps.executeUpdate();
			if(ret > 0) {
				this.valor = valor;
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
		
	}
	
	public void preformEnvioEstadoToCliente() {
		this.thread.sendMessage(this.toString());
		//this.thread.forcaInterpretaMsg(msg);
	}
	
	@Override
	public String toString() {
		String tmpValor = (this.valor == null ? String.valueOf(EmBDdedMessage.MESSAGE_RESPONSE_ERROR) : this.valor );
		char clientNeedExport = (this.exportar ? (char) 2 : (char) 1 );
		return String.format( "%c%c%c%s%c%s" , EmBDdedMessage.MESSAGE_CLIENT_SET_ESTADO, this.getTipo(), clientNeedExport, this.nome , EmBDdedMessage.MESSAGE_NEXTPARAM , tmpValor );
	}
	
}
