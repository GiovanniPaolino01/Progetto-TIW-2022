package beans;
import java.sql.Date;

public class Album {
	private String titolo;
	private String user_id;
	private Date data_creazione;
	
	public Album() {
		
	}
	
	public Album(String titolo, String user_id, Date data_creazione) {
		this.titolo = titolo;
		this.user_id = user_id;
		this.data_creazione = data_creazione;
	}
	
	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}
	
	public String getTitolo() {
		return this.titolo;
	}
	
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	
	public String getUser_id() {
		return this.user_id;
	}
	
	public void setData_Creazione(Date data_creazione) {
		this.data_creazione = data_creazione;
	}
	
	public Date getData_creazione() {
		return this.data_creazione;
	}
}