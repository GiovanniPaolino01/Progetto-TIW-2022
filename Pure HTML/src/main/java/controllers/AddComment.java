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
import dao.CommentoDAO;
import utils.ConnectionHandler;


@WebServlet("/AddComment")
public class AddComment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
    
    public AddComment() {
        super();
    }
    
  //crea la servlet
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
		
		String homepath = getServletContext().getContextPath() + "/Welcome";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("utente") == null) {
			response.sendRedirect(homepath);
			return;
		}	
		
		String testo_commento = null;
		String userimmagine = null;
		String titoloimmagine = null;
		String percorso = null;
		
		try {
			/*estraggo i parametri dalla request, se sono nulli vengono mandate eccezioni*/
			testo_commento = StringEscapeUtils.escapeJava(request.getParameter("testo_commento"));
			userimmagine = StringEscapeUtils.escapeJava(request.getParameter("user_immagine"));
			titoloimmagine = StringEscapeUtils.escapeJava(request.getParameter("titolo_immagine"));
			percorso = StringEscapeUtils.escapeJava(request.getParameter("percorso"));
			
		} catch (NumberFormatException | NullPointerException e) { /*Le eccezioni vengono lanciate se ci sono campi con formati errati o non compilati*/
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore caricamento commento");
		}
		
		Utente utente = (Utente) session.getAttribute("utente");
		String username = utente.getUsername();
		CommentoDAO commentoDAO = new CommentoDAO(connection);
		
		try {
			commentoDAO.faiCommento(testo_commento, userimmagine, titoloimmagine, username);
			
		} catch (SQLException e) {
			//System.out.println("errore nel fare il commento");
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile aggiungere il commento");
		}
		
		String path = "/GoToImagePage";
		request.setAttribute("titolo", titoloimmagine);
		request.setAttribute("username", userimmagine);
		request.setAttribute("percorso", percorso);
		request.getRequestDispatcher(path).forward(request, response);
		
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
