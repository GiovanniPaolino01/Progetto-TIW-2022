package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import dao.CommentoDAO;
import dao.ImmagineDAO;
import beans.Commento;
import beans.Immagine;
import utils.ConnectionHandler;


@WebServlet("/GoToImagePage")
public class GoToImagePage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

    public GoToImagePage() {
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
  	
  	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
  		
  		String loginpath = getServletContext().getContextPath() + "/Welcome";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("utente") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
		//estraggo dalla request dell'ALBUMPAGE i parametri titolo
		
		String titolo_immagine = null;
		String username = null;
		String percorso = null;
		try {
			titolo_immagine = StringEscapeUtils.escapeJava(request.getParameter("titolo"));
			username = StringEscapeUtils.escapeJava(request.getParameter("username"));
			percorso = request.getParameter("percorso");
			
			if(titolo_immagine==null || username==null || percorso==null) {
				titolo_immagine = (String) request.getAttribute("titolo");
				username = (String) request.getAttribute("username");
				percorso = (String) request.getAttribute("percorso");
			}
			
			System.out.println("titolo: "+ titolo_immagine + "\nUsername: "+ username + "\npercorso: "+ percorso);
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}

		
		Immagine immagine = new Immagine();
		ImmagineDAO immagineDAO = new ImmagineDAO(connection);
		
		try {		
			immagine = immagineDAO.estraiImmagine(titolo_immagine, username);
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Impossibile estrarre l'immagine");
		}
		
		//estraiamo i commenti dal DB
		List<Commento> commenti = new ArrayList<Commento>();
		CommentoDAO commentoDAO = new CommentoDAO(connection);
		
		try {
			commenti = commentoDAO.ottieniCommenti(titolo_immagine, username);
		} catch (SQLException e) {
			System.out.println("errore nel caricare i commenti");
			e.printStackTrace();
		}
		
		String path = null;
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		path = "/WEB-INF/IMAGEPAGE.html";
		ctx.setVariable("immagine", immagine);
		ctx.setVariable("nomealbum", immagine.getTitoloAlbum());
		ctx.setVariable("useralbum", immagine.getUserAlbum());
		ctx.setVariable("percorso", percorso);
		ctx.setVariable("commenti", commenti);
		
		templateEngine.process(path, ctx, response.getWriter());
	}
  	
  	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
  	
  	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
   

}
