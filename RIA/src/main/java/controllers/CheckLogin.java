package controllers;

import java.io.IOException;       
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import beans.Utente;
import dao.UtenteDAO;
import utils.ConnectionHandler;

@WebServlet("/CheckLogin")
@MultipartConfig
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	public CheckLogin() {
		super();
	}

	//crea la servlet
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	/*
	 * crea due stringhe username, pw, assegna a questi valori, i valori inseriti dall'utente nella prima try
	 * controllando che non siano vuoti o nulli
	 * successivamente crea un oggetto UtenteDAO e chiamiamo il metodo al suo interno per che fa una query SQL
	 * controllando all'interno del db se le credenziali sono corrette altrimenti manda errore
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {

		String username = null;
		String pw = null;
		
		username = StringEscapeUtils.escapeJava(request.getParameter("username"));
		pw = StringEscapeUtils.escapeJava(request.getParameter("pw"));
		if (username == null || pw == null || username.isEmpty() || pw.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Le credenziali non possono essere nulle");				//...idem
			return;
		}

		// query db to authenticate for user
		UtenteDAO utenteDao = new UtenteDAO(connection);
		Utente utente = null;
		try {
			utente = utenteDao.checkCredentials(username, pw);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Problema del server, riprova");
			return;
		}
		
		/*
		 * Se le credenziali sono corrette allora reindirizza l'utente alla home page
		 * Se l'utente ritornato dal metodo chiamato da utenteDAO == null allora significa che non c'è corrispondenza nel db
		 * e stampa messaggio di errore e ritorna alla pagina di login
		 * altrimenti va alla home page creando la sessione#
		 */

		String path;
		if (utente == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Credenziali incorrette");   	//idem...

		} else {
			request.getSession().setAttribute("utente", utente);
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(username);					//ciò che il client dovrà leggere come risposta dal server dovrà andare qui, ci sarà una funzione in .js che farà x.responseText ed estrarrà quello scritto tra ""
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