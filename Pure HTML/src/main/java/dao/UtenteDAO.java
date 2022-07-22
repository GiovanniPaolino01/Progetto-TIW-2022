package dao;

import java.sql.Connection;    
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import beans.Utente;

public class UtenteDAO {
	
	private Connection connection;

	//crea la connessione con il db
	public UtenteDAO(Connection connection) {
		this.connection = connection;
	}
	
	/*
	 * controlla la validità delle credenziali nel db
	 */
	public Utente checkCredentials(String username, String pw) throws SQLException {
		
		String query = "SELECT username, password FROM utente  WHERE username = ? AND password =?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, username);
			pstatement.setString(2, pw);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // ritorna true se non trova l'utente nel db
					return null;
				else {
					result.next();
					Utente utente = new Utente();
					utente.setUsername(result.getString("username"));
					return utente;
				}
			}
		}
	}
	
	public boolean controlloUnicitaUsername(String username) throws SQLException {
		String query = "SELECT username FROM utente  WHERE username = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, username);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // ritorna true se non trova l'utente nel db
					return true;
				else {
					return false;
				}
			}
		}
	}
	
	public void creaUtente(String username, String pw) throws SQLException {

		String query = "INSERT into utente (username, password) VALUES(?, ?)";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, username);
			pstatement.setString(2, pw);
			pstatement.executeUpdate();
		}
	}
}
