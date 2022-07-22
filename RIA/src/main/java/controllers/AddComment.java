package controllers;

import java.io.IOException;    
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import beans.Utente;
import dao.CommentoDAO;
import utils.ConnectionHandler;


@WebServlet("/AddComment")
public class AddComment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    
    public AddComment() {
        super();
    }
    
  //crea la servlet
  	public void init() throws ServletException {
  		connection = ConnectionHandler.getConnection(getServletContext());  		
  	}
   
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		String testo_commento = null;
		String userimmagine = null;
		String titoloimmagine = null;
		
		try {
			/*estraggo i parametri dalla request, se sono nulli vengono mandate eccezioni*/
			testo_commento = StringEscapeUtils.escapeJava(request.getParameter("testo_commento"));
			userimmagine = StringEscapeUtils.escapeJava(request.getParameter("user_immagine"));
			titoloimmagine = StringEscapeUtils.escapeJava(request.getParameter("titolo_immagine"));
			
			System.out.println("testo: "+ testo_commento);
			System.out.println("utente: "+ userimmagine);
			System.out.println("titoloImmagine: "+ titoloimmagine);
			
			
		} catch (NumberFormatException | NullPointerException e) { /*Le eccezioni vengono lanciate se ci sono campi con formati errati o non compilati*/
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Errore caricamento commento");
			return;
		
		}
		
		Utente utente = (Utente) session.getAttribute("utente");
		String username = utente.getUsername();
		CommentoDAO commentoDAO = new CommentoDAO(connection);
		
		try {
			commentoDAO.faiCommento(testo_commento, userimmagine, titoloimmagine, username);
			
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Impossibile aggiungere il commento");
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().print("Commento aggiunto correttamente");
		
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
