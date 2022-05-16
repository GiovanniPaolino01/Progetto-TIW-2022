package beans;


public class Commento {
	private int id;
	private String testo;
	private String user_commento;
	private String user_immagine;
	private String titolo_immagine;
	
	public Commento(int id, String testo, String user_commento, String user_immagine, String titolo_immagine) {
		this.id = id;
		this.testo = testo;
		this.user_commento = user_commento;
		this.user_immagine = user_immagine;
		this.titolo_immagine = titolo_immagine;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setTesto(String testo) {
		this.testo = testo;
	}
	
	public String getTesto() {
		return this.testo;
	}
	
	public void setUser_commento(String user_commento) {
		this.user_commento = user_commento;
	}
	
	public String getUser_commento() {
		return this.user_commento;
	}
	
	public void setUser_immagine(String user_immagine) {
		this.user_commento = user_immagine;
	}
	
	public String getUser_immagine() {
		return this.user_immagine;
	}
	
	public void setTitolo_immagine(String titolo_immagine) {
		this.titolo_immagine = titolo_immagine;
	}
	
	public String getTitolo_immagine() {
		return this.titolo_immagine;
	}
}