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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.Utente;
import dao.AlbumDAO;
import utils.ConnectionHandler;


@WebServlet("/CreateAlbum")
public class CreateAlbum extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
       
    
    public CreateAlbum() {
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
		
		String homepath = getServletContext().getContextPath() + "/Welcome";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("utente") == null) {
			response.sendRedirect(homepath);
			return;
		}		
		
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
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile controllare se l'album esiste già");
		}
		
		if(isBadRequest) {
			
			String path;
			request.setAttribute("AlbumEsistente", "Album già esistente");
			path = "/GoToHome";
			//la forward serve per passare gli errori (settati come attributi della request) alla servlet GoToHome che poi verranno visualizzati nella HOMEPAGE
			request.getRequestDispatcher(path).forward(request, response);
			
		}else {
			
			AlbumDAO albumDAO = new AlbumDAO(connection);
			
			
			Utente utente = (Utente) session.getAttribute("utente");
			String username = utente.getUsername();
			long milliseconds = System.currentTimeMillis();
			Date data = new Date(milliseconds);
			try {
				albumDAO.creaAlbum(nome_album,username,data);
				String path;
				path = "/GoToHome";
				//la forward serve per passare gli errori (settati come attributi della request) alla servlet GoToHome che poi verranno visualizzati nella HOMEPAGE
				request.getRequestDispatcher(path).forward(request, response);
				
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile creare l'album");
			}
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
