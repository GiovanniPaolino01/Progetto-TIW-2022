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


@WebServlet("/GoToHome")
public class GoToHome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
       

    public GoToHome() {
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
  		
  		Utente utente = (Utente) session.getAttribute("utente");
  		AlbumDAO albumDAO = new AlbumDAO(connection);
  		ImmagineDAO immagineDAO = new ImmagineDAO(connection)
;  		List<Album> albumsMiei = new ArrayList<Album>();
  		List<Album> albumsNonMiei = new ArrayList<Album>();
  		List<Immagine> nuove_immagini = new ArrayList<Immagine>();
  		
  		try {
			albumsMiei = albumDAO.cercaAlbumPerUtente(utente.getUsername());
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile trovare albumsMiei");
			return;
		}
  		
  		try {
			albumsNonMiei = albumDAO.cercaAlbumNonMiei(utente.getUsername());
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile trovare albumsNonMiei");
			return;
		}
  		
  		try {
			nuove_immagini = immagineDAO.selezionaImmaginiDaAlbum(utente.getUsername(), "Nuove_Immagini");
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile trovare immagini nell'album in Nuove_immagini");
			return;
		}
  		
  		//redirect alla home page caricando gli album da db
		String path = "/WEB-INF/HOMEPAGE.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		if(session.getAttribute("AlbumEsistente") != null && session.getAttribute("AlbumEsistente").equals(true)) {
			ctx.setVariable("errorCreateAlbum", "Album già esistente");
			session.setAttribute("AlbumEsistente", false);
		}
		ctx.setVariable("albumsMiei", albumsMiei);
		ctx.setVariable("albumsNonMiei", albumsNonMiei);
		ctx.setVariable("nuove_immagini", nuove_immagini);
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
