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

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.Utente;
import dao.UtenteDAO;
import utils.ConnectionHandler;

@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public CheckLogin() {
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

	/*
	 * crea due stringhe username, pw, assegna a questi valori, i valori inseriti dall'utente nella prima try
	 * controllando che non siano vuoti o nulli
	 * successivamente crea un oggetto UtenteDAO e chiamiamo il metodo al suo interno per che fa una query SQL
	 * controllando all'interno del db se le credenziali sono corrette altrimenti manda errore
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// obtain and escape params
		String username = null;
		String pw = null;
		try {
			username = StringEscapeUtils.escapeJava(request.getParameter("username"));
			pw = StringEscapeUtils.escapeJava(request.getParameter("pw"));
			if (username == null || pw == null || username.isEmpty() || pw.isEmpty()) {
				throw new Exception("Missing or empty credential value");
			}

		} catch (Exception e) {
			// for debugging only e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credential value");
			return;
		}

		// query db to authenticate for user
		UtenteDAO utenteDao = new UtenteDAO(connection);
		Utente utente = null;
		try {
			utente = utenteDao.checkCredentials(username, pw);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not Possible to check credentials");
			return;
		}

		// If the user exists, add info to the session and go to home page, otherwise
		// show login page with error message
		
		/*
		 * Se le credenziali sono corrette allora reindirizza l'utente alla home page
		 * Se l'utente ritornato dal metodo chiamato da utenteDAO == null allora significa che non c'è corrispondenza nel db
		 * e stampa messaggio di errore e ritorna alla pagina di login
		 * altrimenti va alla home page creando la sessione#
		 */

		String path;
		if (utente == null) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorCredentials", "Username o password non corretti");
			//path= getServletContext().getContextPath() + "/Welcome";
			//response.sendRedirect(path);
			
			path = "/WEB-INF/LOGIN_REGISTRAZIONE.html";
			templateEngine.process(path, ctx, response.getWriter());
		} else {
			/*
			 * #creo la sessione chiamando la request, il metodo getSession e settando l'attributo
			 * "utente" con l'oggetto utente
			 */
			
			
			/*
			 * in questo modo facciamo il reindirizzamento alla socket GoToHome che ha il compito di estrarre le info dal db
			 */
			request.getSession().setAttribute("utente", utente);
			path = getServletContext().getContextPath() + "/GoToHome";
			response.sendRedirect(path);
			
			
			
			/*
			 * in questo modo andiamo direttamente alla pagina che vogliamo, senza passare per il reindirizzamento alla socket gotohome
			 */
			/*path = "/WEB-INF/HOMEPAGE.html";
			request.getSession().setAttribute("utente", utente);
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("utente", utente);
			templateEngine.process(path, ctx, response.getWriter());*/
		}

	}
	/*
	 * distrugge la servlet
	 */
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}