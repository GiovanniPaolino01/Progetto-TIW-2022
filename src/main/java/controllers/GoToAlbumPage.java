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

import beans.Utente;
import dao.UtenteDAO;
import dao.AlbumDAO;
import dao.ImmagineDAO;
import beans.Album;
import beans.Immagine;
import utils.ConnectionHandler;
/**
 * Servlet implementation class GoToAlbumPage
 */
@WebServlet("/GoToAlbumPage")
public class GoToAlbumPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
       

    public GoToAlbumPage() {
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
		
		String titolo_album = null;
		String username = null;
		try {
			titolo_album = StringEscapeUtils.escapeJava(request.getParameter("titolo"));
			username = StringEscapeUtils.escapeJava(request.getParameter("username"));
			//System.out.println("titolo: "+ titolo_album + "\nUsername: "+ username);
		} catch (NumberFormatException | NullPointerException e) {
			// only for debugging e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}
		
		if (username == null) {
			Utente utente = (Utente) session.getAttribute("utente");
			username = utente.getUsername();
			//System.out.println("Attenzione! ho preso l'user di sessione!");
			//System.out.println("titolo: "+ titolo_album + "\nUsername: "+ username);
		}
		AlbumDAO albumDAO = new AlbumDAO(connection);
		Album album = null;
		List<Immagine> immagini = new ArrayList<Immagine>();
		ImmagineDAO immagineDAO = new ImmagineDAO(connection);
		
		try {
			album = albumDAO.cercaAlbumPerTitolo(titolo_album, username);
			//controlli???
			immagini = immagineDAO.selezionaImmaginiDaAlbum(username, titolo_album);
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		String path = "/WEB-INF/ALBUMPAGE.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("album", album);
		ctx.setVariable("immagini", immagini);
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
