package dao;

import java.sql.Connection;     
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import beans.Commento;

public class CommentoDAO {
	
	private Connection connection;
	
	public CommentoDAO(Connection connection) {
		this.connection = connection;
	}

	public void faiCommento(String testo, String user_id, String titolo_immagine, String user_commento) throws SQLException{
		
		String query = "INSERT into commento (testo,user_immagine,titolo_immagine,user_commento) VALUES(?, ?, ?, ?)";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, testo);
			pstatement.setString(2, user_id);
			pstatement.setString(3, titolo_immagine);
			pstatement.setString(4, user_commento);
			pstatement.executeUpdate();
		}
		
	}
	
	public List<Commento> ottieniCommenti(String titolo_immagine, String user) throws SQLException{
		
		List<Commento> commenti = new ArrayList<Commento>();

		String query = "SELECT * FROM commento WHERE titolo_immagine = ? AND user_immagine = ?";
		
		//preparo i parametri per fare una lettura corretta da db
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, titolo_immagine);
			pstatement.setString(2, user);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Commento commento = new Commento();
					commento.setTesto(result.getString("testo"));
					commento.setUser_commento(result.getString("user_commento"));
					commenti.add(commento);
				}
			}
		}
		return commenti;
		
	}
}