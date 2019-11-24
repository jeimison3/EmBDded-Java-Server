package DB;


import java.sql.*;

public class DBConnection {
	public Connection sql = null;
	private String URL_JDBC;
	
	public DBConnection(String username, String passwd) {
		this.URL_JDBC = "jdbc:mysql://localhost/embdded?user="+username+"&password="+passwd;
	}
	
	public boolean conecta() {
		try {
			this.sql = DriverManager.getConnection(this.URL_JDBC);
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
