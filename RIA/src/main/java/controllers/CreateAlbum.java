package controllers;

import java.io.IOException;    
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import beans.Utente;
import dao.UtenteDAO;
import dao.AlbumDAO;
import utils.ConnectionHandler;
import javax.servlet.annotation.MultipartConfig;


@WebServlet("/CreateAlbum")
@MultipartConfig
public class CreateAlbum extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    
    public CreateAlbum() {
        super();
    }
    
    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		

		HttpSession session = request.getSession();
		String nome_album = null;
		boolean isBadRequest = false;
		
		try {
			/*estraggo i parametri dalla request, se sono nulli vengono mandate eccezioni*/
			nome_album = StringEscapeUtils.escapeJava(request.getParameter("nome_album"));
			
		} catch (NumberFormatException | NullPointerException e) { /*Le eccezioni vengono lanciate se ci sono campi con formati errati o non compilati*/
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile creare l'album");
		}

		
		
		try { /*Controllo che nel DB non esista un album con lo stesso nome per quell'utente*/
			AlbumDAO albumDAO = new AlbumDAO(connection);
			Utente utente = (Utente) session.getAttribute("utente");
			String username = utente.getUsername();
			if(false == albumDAO.controlloUnicitaAlbumPerUtente(username, nome_album) || nome_album ==null) {
				isBadRequest = true;
			}
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Impossbile controllare se l'album esiste già");
			return;
		}
		
		if(isBadRequest) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Album gia' esistente");
			return;
		}else {
			
			AlbumDAO albumDAO = new AlbumDAO(connection);
			
			
			Utente utente = (Utente) session.getAttribute("utente");
			String username = utente.getUsername();
			long milliseconds = System.currentTimeMillis();
			Date data = new Date(milliseconds);
			try {
				albumDAO.creaAlbum(nome_album,username,data);
				
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Impossibile creare l'album, Riprova");
				return;
			}
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().print("Album aggiunto correttamente");
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
