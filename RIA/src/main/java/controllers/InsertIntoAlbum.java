package controllers;


import java.io.File; 
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.lang.StringEscapeUtils;

import beans.Utente;
import dao.ImmagineDAO;
import utils.ConnectionHandler;


@WebServlet("/InsertIntoAlbum")
@MultipartConfig
public class InsertIntoAlbum extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
    
    public InsertIntoAlbum() {
        super();
    }
    
    public void init() throws ServletException {	
		connection = ConnectionHandler.getConnection(getServletContext());
	}
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		
		String Nome_Album = StringEscapeUtils.escapeJava(request.getParameter("album"));
		String Nome_Immagine = StringEscapeUtils.escapeJava(request.getParameter("immagine"));
		Utente utente = (Utente) session.getAttribute("utente");
		String username = utente.getUsername();
		
		System.out.println(Nome_Album + Nome_Immagine);
		
		ImmagineDAO immagineDAO = new ImmagineDAO(connection);
		
		try {
			
			immagineDAO.inserisciImmagineInAlbum(Nome_Album, Nome_Immagine, username);			
			
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Impossibile inserire l'immagine nell'Album");
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().print("Immagine aggiunta all'Album correttamente");
		
    }
    
    
    public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


}
