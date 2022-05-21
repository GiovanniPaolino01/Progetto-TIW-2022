package dao;

import java.io.InputStream;
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
		
		String query = "SELECT titolo,percorso FROM immagine WHERE user_id = ? AND titolo_album = ? AND user_album = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, user_id);
			pstatement.setString(2, titolo_album);
			pstatement.setString(3, user_id);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Immagine immagine = new Immagine();
					immagine.setTitolo(result.getString("titolo"));
					immagine.setPercorso(result.getString("percorso"));
					//System.out.println("percorso bello" + result.getString("percorso"));
					immagini.add(immagine);
				}
			}
		}
		return immagini;
	}
	
	public void creaImmagineNelDB(String titolo, String user_id, Date data, String Testo_Descrittivo, String percorso, String titolo_album, InputStream imageStream) throws SQLException{
		
		String query = "INSERT into immagine (titolo,user_id,data,testo,percorso,titolo_album,user_album,img) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, titolo);
			pstatement.setString(2, user_id);		
			pstatement.setDate(3, data);
			pstatement.setString(4, Testo_Descrittivo);
			pstatement.setString(5, percorso);
			pstatement.setString(6, titolo_album);
			pstatement.setString(7, user_id);
			pstatement.setBlob(8, imageStream);
			pstatement.executeUpdate();
		}
		
	}
	
	public void inserisciImmagineInAlbum(String Nome_Album, String Nome_Immagine, String username) throws SQLException{
		
		String query = "UPDATE immagine SET titolo_album = ? WHERE (titolo = ? AND user_id = ? AND titolo_album = ?)";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, Nome_Album);
			pstatement.setString(2, Nome_Immagine);		
			pstatement.setString(3, username);
			pstatement.setString(4, "Nuove_Immagini");
			pstatement.executeUpdate();
		}
		
	}

}
