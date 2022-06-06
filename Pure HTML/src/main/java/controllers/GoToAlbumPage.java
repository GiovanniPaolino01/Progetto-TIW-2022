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
		
		//estraggo dalla request della HOMEPAGE(li ha dalla query SQL) o della Servlet ButtonHandler(li ha dall'ALBUMPAGE che glieli passa) 
		//i parametri titolo e username
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
		}
		AlbumDAO albumDAO = new AlbumDAO(connection);
		Album album = null;
		List<Immagine> immagini = new ArrayList<Immagine>();
		ImmagineDAO immagineDAO = new ImmagineDAO(connection);
		
		//estrae l'album dal DB e poi tutte le immagini relative a quell'album,
		//l'album mi serve per far stampare nella ALBUMPAGE il nome dell'album e poi
		//per passarlo come parametro alla ButtonHandler per poi chiamare la GoToAlbumPage e caricare le immagini
		//passandogli come valori username dell'album e titolo dell'album
		try {
			album = albumDAO.cercaAlbumPerTitolo(titolo_album, username);
			//controlli???
			immagini = immagineDAO.selezionaImmaginiDaAlbum(username, titolo_album);
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		List<Immagine> solo_5_immagini = new ArrayList();
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		int buttonParameter = 0;
		try{
			//buttonParameter = (int) session.getAttribute("parametro");
			buttonParameter = (int) request.getAttribute("parametro");
		}catch(Exception e) {
			//session.setAttribute("parametro", 0);
			request.setAttribute("parametro", 0);
		}
		
		//System.out.println("parametro in goToAlbumPage"+session.getAttribute("parametro"));
		
		
		//carica 5 immagini nella lista solo_5_immagini in base a questa condizione
		for(int i=(0 + 5*buttonParameter) ; i<immagini.size() && (i<5 + 5*buttonParameter); i++) {
			solo_5_immagini.add(immagini.get(i));
		}
		
		//next viene reso visibile solo quando ci sono più di 5 img e non si è all'ultima "pagina" di quell'album
		String next = null;
		if(immagini.size()>5 && (buttonParameter!=(int)Math.ceil(((double)(immagini.size())/5)-1)) ){
			next = "submit";
		}
		else {
			next = "hidden";
		}
		
		//previous viene reso visibile solo quando ci sono più di 5 immagini nell'album e vado nelle "pagine" successive
		String previous = null;
		if(buttonParameter>0) {
			previous = "submit";
		}
		else {
			previous = "hidden";
		}
		
		String path = "/WEB-INF/ALBUMPAGE.html";		
		ctx.setVariable("album", album);
		ctx.setVariable("immagini", immagini);
		ctx.setVariable("solo_5_immagini", solo_5_immagini);
		ctx.setVariable("next", next);
		ctx.setVariable("previous", previous);
		ctx.setVariable("parametro", buttonParameter);
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
