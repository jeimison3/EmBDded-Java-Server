package EmBDded;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Cliente {
	private int clientid = -1;
	private String clientname = "";
	private Connection db;
	private ArrayList<Estado> estados = new ArrayList<Estado>();
	private ArrayList<Atributo> atributos = new ArrayList<Atributo>();
	
	public Cliente(Connection db) {
		this.db = db;
	}
	
	public String getClientname() {
		return clientname;
	}
	
	public boolean haAtributo(String nome) {
		for(Atributo a : this.atributos) 
			if(a.nome.contentEquals(nome)) return true;
		return false;
	}
	
	public boolean addAtributo(String nome, String tipo) {
		try {
			System.out.println("FK> "+this.clientid);
			String query = "INSERT INTO atributos(atribid,clientid,nome,tipo) VALUES(NULL, ?, ?, ?)";
			PreparedStatement ps = this.db.prepareStatement(query);
			ps.setInt(1, this.clientid);
			ps.setString(2, nome);
			ps.setString(3, tipo);
			int ret = ps.executeUpdate();
			if(ret > 0) {
				atributos.add(new Atributo(nome, tipo));
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return false;
	}
	
	public void addEstado(Atributo atr, String valor) {
		estados.add(new Estado(this.db, atr, valor));
	}


	public void setClientname(String clientname) {
		this.clientname = clientname;
		try {
			this.retriveMainData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean registraCliente() {
		try {
			String query = "INSERT INTO clientes(clientid, clientname) VALUES(NULL, ?);";
			PreparedStatement ps = this.db.prepareStatement(query);
			ps.setString(1, this.clientname);
			int ret = ps.executeUpdate();
			if(ret > 0) {
				this.clientid = ps.getGeneratedKeys().getInt("clientid");
				System.out.println("NewCli ID: "+this.clientid);
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}



	public boolean retriveMainData() throws SQLException {
		if(this.clientid == -1) {
			if(this.db == null) System.out.println("DB> Problema: DB nulo?!");
			// ===================================================
			
			String query = "SELECT clientid FROM clientes WHERE clientname = ?;";
			PreparedStatement ps = this.db.prepareStatement(query);
			
			ps.setString(1, this.clientname);
			
			ResultSet rs = ps.executeQuery();
			int resu = rs.getFetchSize();
			
			if( resu == 0 ) { ps.close(); return this.registraCliente(); }
			else if(resu > 1) { System.out.println("Problema no banco: Múltiplos nomes iguais."); }
				
			while(rs.next()) {
				this.clientid = rs.getInt("clientid");
			}
			
			// ===================================================
			
			this.atributos.clear();
			
			query = "SELECT atribid,nome,tipo FROM atributos WHERE clientid = ?;";
			ps = this.db.prepareStatement(query);
			ps.setInt(1, this.clientid);
			
			rs = ps.executeQuery();
			while(rs.next()) {
				this.atributos.add(new Atributo(rs.getInt("atribid"), rs.getString("nome"), rs.getString("tipo")));
			}
			
			// ===================================================
			
			this.estados.clear();
			
			query = "";
			for(int i = 0; i < this.atributos.size(); i++) {
				query+= this.atributos.get(i).atribid+(i<this.atributos.size()-2 ? "," : "" );
			}
			
			query = "SELECT atribid,valor FROM estados WHERE atribid IN ( "+query+" );";
			ps = this.db.prepareStatement(query);
			
			rs = ps.executeQuery();
			while(rs.next()) {
				for(Atributo a : this.atributos) {
					if(a.atribid == rs.getInt("atribid"))
						estados.add(new Estado(this.db, a, rs.getString("valor")));
				}
			}
			
			// ===================================================
			
			ps.close();
			
			System.out.printf("id: %d | %d atributos | %d estados\n", this.clientid, this.atributos.size(), this.estados.size());
			
			return true;
		}
		return false;
	}
	
}

	
