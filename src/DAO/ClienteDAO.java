package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Classes.Atributo;
import Classes.Cliente;
import Classes.Estado;
import remote.EmBDdedClientKeeper;

public class ClienteDAO extends Cliente {
	

	private Connection db;
	private ArrayList<EstadoDAO> estados = new ArrayList<EstadoDAO>();
	private ArrayList<Atributo> atributos = new ArrayList<Atributo>();
	private EmBDdedClientKeeper thread;
	
	public ClienteDAO(Connection db, EmBDdedClientKeeper thread) {
		this.thread = thread;
		this.db = db;
	}
	
	@Override
	public void setClientname(String clientname) {
		this.clientname = clientname;
		try {
			this.retriveMainData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean haAtributo(String nome) {
		for(Atributo a : this.atributos) 
			if(a.nome.contentEquals(nome)) return true;
		return false;
	}
	
	public Atributo addAtributo(String nome, String tipo) {
		try {
			//System.out.println("FK> "+this.clientid);
			String query = "INSERT INTO atributos(atribid,clientid,nome,tipo) VALUES(NULL, ?, ?, ?)";
			PreparedStatement ps = this.db.prepareStatement(query);
			ps.setInt(1, this.clientid);
			ps.setString(2, nome);
			ps.setString(3, tipo);
			int ret = ps.executeUpdate();
			if(ret > 0) {
				Atributo a = new Atributo(nome, tipo);
				atributos.add(a);
				return a;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return null;
	}
	
	public EstadoDAO getEstado(String nome) {
		for(EstadoDAO e : this.estados) {
			if(e.nome.contentEquals(nome)) return e;
		}
		Atributo a = this.getAtributo(nome);
		if(a != null) {
			return new EstadoDAO( this.db, this.thread, a );
		}
		return null;
	}
	
	public Atributo getAtributo(String nome) {
		for(Atributo e : this.atributos) {
			//System.out.println(e.nome + " == "+ nome + "?");
			if(e.nome.contentEquals(nome)) return e;
		}
		return null;
	}
	
	
	public void addEstadoExportar(Atributo atr) {
		EstadoDAO e = new EstadoDAO(this.db, this.thread, atr);
		e.setExport(true);
		estados.add(e);
	}
	
	public boolean registraCliente() {
		try {
			String query = "INSERT INTO clientes(clientid, clientname) VALUES(NULL, ?);";
			PreparedStatement ps = this.db.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, this.clientname);
			int ret = ps.executeUpdate();
			ResultSet kys = ps.getGeneratedKeys();
			if(ret > 0) {
				kys.next();
				this.clientid =  kys.getInt(1);
				System.out.println("NewCli ID: "+this.clientid);
				kys.close();
				ps.close();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public ArrayList<String> EstadosToString(){
		ArrayList<String> str = new ArrayList<String>();
		for(EstadoDAO e : this.estados) {
			str.add(e.toString());
		}
		return str;
	}



	public boolean retriveMainData() throws SQLException {
		if(this.clientid == -1) {
			if(this.db == null) System.out.println("DB> Problema: DB nulo?!");
			// ===================================================
			
			String query = "SELECT clientid FROM clientes WHERE clientname = ?;";
			PreparedStatement ps = this.db.prepareStatement(query);
			
			ps.setString(1, this.clientname);
			
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				this.clientid = rs.getInt("clientid");
			} else { 
				ps.close();
				return this.registraCliente();
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
			
			ps.close();
			// ===================================================
			this.estados.clear();
			
			for(Atributo a : this.atributos) {
				this.estados.add( new EstadoDAO(this.db, this.thread, a) );
			}
			
			
			System.out.printf("id: %d | %d atributos | %d estados\n", this.clientid, this.atributos.size(), this.estados.size());
			
			return true;
		}
		return false;
	}

}
