package controllers;

import java.io.IOException;         
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import beans.Utente;
import dao.ImmagineDAO;
import beans.Immagine;
import utils.ConnectionHandler;


@WebServlet("/GetNewImages")
public class GetNewImages extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       

    public GetNewImages() {
        super();
    }
    
  //crea la servlet
  	public void init() throws ServletException {
  		connection = ConnectionHandler.getConnection(getServletContext());
  	}
  	
  	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
  		
  		HttpSession session = request.getSession();
  		
  		Utente utente = (Utente) session.getAttribute("utente");
  		ImmagineDAO immagineDAO = new ImmagineDAO(connection);
  		List<Immagine> nuove_immagini = new ArrayList<Immagine>();
  		
  		
  		try {
			nuove_immagini = immagineDAO.selezionaImmaginiDaAlbum(utente.getUsername(), "Nuove_Immagini");
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Non ci sono nuove immagini");
			return;
		}
  		
  		response.setStatus(HttpServletResponse.SC_OK);
		Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(nuove_immagini);
		
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
  		
  		//redirect alla home page caricando gli album da db
		
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
