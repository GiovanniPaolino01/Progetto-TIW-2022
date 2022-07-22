package controllers;

import java.io.IOException;   
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import dao.AlbumDAO;
import dao.UtenteDAO;
import utils.ConnectionHandler;


@WebServlet("/CheckRegistration")
@MultipartConfig
public class CheckRegistration extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	     
	public CheckRegistration() {
		super();
	}
	
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/*
		 * controlliamo i parametri della request
		 * 1. estrarre i parametri della request
		 * 2. controllare che non siano null or empty e che siano della dimensione giusta
		 * 3. controllare che username sia unico (effettuare la query del db e verificare che il cursore sia prima del primo)
		 * 4. controllare che pw = ripeti pw
		 * 5. creare utente oppure mandare errore
		 */
		boolean isBadRequest = false;
		boolean userEsistente = false;
		boolean lunghezzaCampiEsagerata = false;
		boolean emailScorretta = false;
		String username = null;
		String pw = null;
		String ripeti_pw = null;
		try {
			/*estraggo i parametri dalla request, se sono nulli vengono mandate eccezioni*/
			username = StringEscapeUtils.escapeJava(request.getParameter("username"));
			pw = StringEscapeUtils.escapeJava(request.getParameter("pw"));
			ripeti_pw = StringEscapeUtils.escapeJava(request.getParameter("ripeti_pw"));
			
			/*Controllo la correttezza della mail*/
			
			Pattern p = Pattern.compile(".+@.+\\.[a-z]+", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(username);
			boolean matchFound = m.matches();

			
			
			if(!matchFound) {
				isBadRequest = true;
				emailScorretta = true;
			}
			
			/*Controllo che password e ripeti password siano uguali*/
			if(!pw.equals(ripeti_pw)) {
				isBadRequest = true;
			}
			
			
			/*Controllo che username e password siano al più di 20 caratteri*/
			if(username.length()>20 || pw.length()>20) {
				isBadRequest = true;
				lunghezzaCampiEsagerata = true;
			}
			
			
		} catch (NumberFormatException | NullPointerException e) { /*Le eccezioni vengono lanciate se ci sono campi con formati errati o non compilati*/
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, Impossibile estrarre i parametri della form, provare più tardi");
		}
		
		try { /*Controllo che nel DB non esista già un utente con lo stesso username*/
			UtenteDAO utenteDAO = new UtenteDAO(connection);
			if(false == utenteDAO.controlloUnicitaUsername(username)) {
				isBadRequest = true;
				userEsistente = true;
			}
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, Impossibile controllare se il nome utente esiste già, provare più tardi");
		}
			
		
		/*se isBadRequest è a true vuol dire che i parametri non sono corretti in quanto o si è cercato di inviare la form con dei campi mancanti
		 *  oppure pw e ripeti_pw non sono uguali
		*/
		if(isBadRequest) { /*C'è qualche errore nella compilazione della form */
			/*controllo i boolean in ordine di importanza del tipo di errore per vedere qual è l'errore e stampare a video il giusto messaggio di errore
			 * (il primo per importanza)*/
			if(userEsistente) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Username gia' esistente, scegline un altro.");
			}
			else if(lunghezzaCampiEsagerata) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Username e Password non devono essere piu' lunghe di 20 caratteri!");
			}
			else if(emailScorretta) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Email sintatticamente errata.");
			}
			else {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Password e Ripeti Password non corrispondono.");
			}
			
		}
		else
		{	
			/* Creo Utente in db (ho già controllato prima (righe 93-98) che non ci siano già altri utenti con lo stesso username nel DB)
			 * Se entra nell'else vuol dire che è tutto a posto e posso creare l'utente nel DB senza fare ulteriori controlli.
			 */
			UtenteDAO utenteDAO = new UtenteDAO(connection);
			AlbumDAO albumDAO = new AlbumDAO(connection);
			try {
					utenteDAO.creaUtente(username, pw);
					
					long milliseconds = System.currentTimeMillis();
					Date data = new Date(milliseconds);
					albumDAO.creaAlbum("Nuove_Immagini", username, data);
				
			} catch (SQLException e) { /*la creaUtente può lanciare SQL exception in caso di errore del server che non riesce a fare la insert nel DB, in tal caso devo dare messaggio di internal error server*/
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Internal server error, Impossibile creare l'Utente, riprovare più tardi.");
			}
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println("Registrazione andata a buon fine!");
		}
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}