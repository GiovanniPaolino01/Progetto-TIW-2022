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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import utils.ConnectionHandler;

@WebServlet("/ButtonsHandler")
public class ButtonsHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
   
    public ButtonsHandler() {
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
		
		//serve a vedere in che "pagina" con i gruppi di 5 immagini sono
		int parametro = Integer.parseInt(request.getParameter("parametro"));
		
		//mi dice se la chiamata è stata fatta dal bottone next o previous
		String tipo = request.getParameter("tipo");
		
		//serve per controllare il tipo del bottone ed effettuare il giusto comportamento legato al bottone,
		//cioè andare avanti o indietro
		if(tipo != null && tipo.equals("successivo")) {
			parametro=parametro+1;
		}
		else if (tipo != null && tipo.equals("precedente")) {
			parametro=parametro -1;
		}
		
		String path = "/GoToAlbumPage";
		request.setAttribute("parametro",parametro);
		//forward invia la richiesta di questa Servlet con gli stessi parametri alla GoToAlbumPage
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
