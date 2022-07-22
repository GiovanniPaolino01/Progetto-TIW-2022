package controllers;

import java.io.IOException;     
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.Utente;
import dao.AlbumDAO;
import utils.ConnectionHandler;


@WebServlet("/SaveOrder")
public class SaveOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    
    public SaveOrder() {
        super();
    }
    
  //crea la servlet
  	public void init() throws ServletException {
  		connection = ConnectionHandler.getConnection(getServletContext());  		
  	}
   
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();

		String lista = request.getParameter("lista");
		System.out.println(lista);
		
		String[] split = lista.split(",");
		System.out.println(split[0]);
		
		
		
		Utente utente = (Utente) session.getAttribute("utente");
		String username = utente.getUsername();
		AlbumDAO albumDAO = new AlbumDAO(connection);
		
		
		try {
			for(int i=0; i<split.length; i++) {
				albumDAO.modificaOrdine(split[i], i, username);
			}
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Impossibile modificare ordine");
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().print("Modifica avvenuta correttamente");
		
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
