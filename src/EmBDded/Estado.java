package EmBDded;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Estado extends Atributo {
	public String valor;
	private Connection db;
	
	public Estado(Connection db, Atributo atr, String valor) {
		super(atr.atribid, atr.nome, atr.tipo);
		this.db = db;
		this.valor = valor;
	}
	
	public Estado(Connection db, String nome, String tipo, String valor) {
		super(nome, tipo);
		this.db = db;
		this.valor = valor;		
	}
	
	public Estado(Connection db, int id, String nome, String tipo, String valor) {
		super(id, nome, tipo);
		this.db = db;
		this.valor = valor;
	}

	public String getValor() {
		return valor;
	}
	
	public String getDBValor() {
		try {
			String query = "SELECT valor FROM estados WHERE atribid = ?;";
			PreparedStatement ps = this.db.prepareStatement(query);
			ps.setInt(1, this.atribid);
			ResultSet ret = ps.executeQuery();
			while(ret.next()) {
				return ret.getString("valor");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean setValor(String valor) {
		try {
			String query = "REPLACE INTO estados(atribid, valor) VALUES(?, ?)";
			PreparedStatement ps = this.db.prepareStatement(query);
			ps.setInt(1, this.atribid);
			ps.setString(2, valor);
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
	
	
}
