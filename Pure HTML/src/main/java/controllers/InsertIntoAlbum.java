package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.Utente;
import dao.ImmagineDAO;
import utils.ConnectionHandler;


@WebServlet("/InsertIntoAlbum")
public class InsertIntoAlbum extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
	String folderPath = "";
    
    public InsertIntoAlbum() {
        super();
    }
    
    public void init() throws ServletException {
        	
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	String homepath = getServletContext().getContextPath() + "/GoToHome";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("utente") == null) {
			response.sendRedirect(homepath);
			return;
		}
		
		String Nome_Album = StringEscapeUtils.escapeJava(request.getParameter("album"));
		String Nome_Immagine = StringEscapeUtils.escapeJava(request.getParameter("immagine"));
		Utente utente = (Utente) session.getAttribute("utente");
		String username = utente.getUsername();
		
		ImmagineDAO immagineDAO = new ImmagineDAO(connection);
		
		try {
			
			immagineDAO.inserisciImmagineInAlbum(Nome_Album, Nome_Immagine, username);
			
			String path;
			path = getServletContext().getContextPath() + "/GoToHome";
			response.sendRedirect(path);
			
			
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile inserire l'immagine nell'album");
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
