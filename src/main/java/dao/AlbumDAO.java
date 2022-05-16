package dao;

import java.sql.Connection;   
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Album;

public class AlbumDAO {
	
	private Connection connection;
	
	public AlbumDAO(Connection connection) {
		this.connection = connection;
	}

	public boolean controlloUnicitaAlbumPerUtente(String username, String nome_album) throws SQLException {
		String query = "SELECT * FROM album  WHERE titolo = ? AND user_id = ? ";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, nome_album);
			pstatement.setString(2, username);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // ritorna true se non trova l'album nel db
					return true;
				else {
					return false;
				}
			}
		}
	}
	
	public List<Album> cercaAlbumPerUtente(String username) throws SQLException{
		
		List<Album> albums = new ArrayList<Album>();

		String query = "SELECT * FROM album WHERE user_id = ? AND NOT(titolo = 'Nuove_Immagini') ORDER BY data_creazione DESC";
		
		//preparo i parametri per fare una lettura corretta da db
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, username);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Album album = new Album();
					album.setTitolo(result.getString("titolo"));
					album.setUser_id(result.getString("user_id"));
					album.setData_Creazione(result.getDate("data_creazione"));
					albums.add(album);
				}
			}
		}
		return albums;
	}
	
	public List<Album> cercaAlbumNonMiei(String username) throws SQLException{
		
		List<Album> albums = new ArrayList<Album>();

		String query = "SELECT * FROM album WHERE not(user_id = ?) ORDER BY data_creazione DESC";
		
		//preparo i parametri per fare una lettura corretta da db
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, username);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Album album = new Album();
					album.setTitolo(result.getString("titolo"));
					album.setUser_id(result.getString("user_id"));
					album.setData_Creazione(result.getDate("data_creazione"));
					albums.add(album);
				}
			}
		}
		return albums;
	}
	
	public void creaAlbum(String titolo, String user_id, Date data_creazione) throws SQLException{
		
		String query = "INSERT into album (titolo,user_id,data_creazione) VALUES(?, ?, ?)";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, titolo);
			pstatement.setString(2, user_id);
			pstatement.setDate(3, data_creazione);
			pstatement.executeUpdate();
		}
		
	}	
}