package beans;
import java.sql.Date;

public class Immagine {
	private String titolo;
	private String user_id;
	private Date data;
	private String testo;
	private String percorso;
	
	public Immagine() {
		
	}
	
	public Immagine(String titolo, String user_id, Date data, String testo, String percorso) {
		this.titolo = titolo;
		this.user_id = user_id;
		this.data = data;
		this.testo = testo;
		this.percorso = percorso;
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
	
	public void setData(Date data) {
		this.data = data;
	}
	
	public Date getData() {
		return this.data;
	}
	
	public void setTesto(String testo) {
		this.testo = testo;
	}
	
	public String getTesto() {
		return this.testo;
	}
	
	public void setPercorso(String percorso) {
		this.percorso = percorso;
	}
	
	public String getPercorso() {
		return this.percorso;
	}
}