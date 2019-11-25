package DB;


import java.sql.*;

public class DBConnection {
	public Connection sql = null;
	private String username;
	private String passwd;
	private String URL_JDBC;
	
	public DBConnection(String username, String passwd) {
		this.username = username;
		this.passwd = passwd;
		this.URL_JDBC = "jdbc:mysql://localhost/embdded";
	}
	
	public boolean conecta() {
		try {
			this.sql = DriverManager.getConnection(this.URL_JDBC, this.username, this.passwd);
		} catch(SQLException e) {
			//e.printStackTrace();
			System.out.println("SQLException: " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		    return false;
		}
		return true;
	}

}
