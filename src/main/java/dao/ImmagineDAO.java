package dao;

import java.sql.Connection;   
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Album;
import beans.Immagine;

public class ImmagineDAO {
	
private Connection connection;
	
	public ImmagineDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<Immagine> selezionaImmaginiDaAlbum(String user_id, String titolo_album) throws SQLException{
		
		List<Immagine> immagini = new ArrayList<Immagine>();
		
		String query = "SELECT titolo FROM immagine WHERE user_id = ? AND titolo_album = ? AND user_album = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, user_id);
			pstatement.setString(2, titolo_album);
			pstatement.setString(3, user_id);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Immagine immagine = new Immagine();
					immagine.setTitolo(result.getString("titolo"));
					immagini.add(immagine);
				}
			}
		}
		return immagini;
	}

}
