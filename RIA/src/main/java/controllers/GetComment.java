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
import org.apache.commons.lang.StringEscapeUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.CommentoDAO;
import beans.Commento;
import utils.ConnectionHandler;


@WebServlet("/GetComment")
public class GetComment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    public GetComment() {
        super();
    }
    
    //crea la servlet
  	public void init() throws ServletException {
  		connection = ConnectionHandler.getConnection(getServletContext());
  	}
  	
  	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String titolo_immagine = null;
		String user_immagine = null;
		try {
			titolo_immagine = StringEscapeUtils.escapeJava(request.getParameter("titoloImmagine"));
			user_immagine = StringEscapeUtils.escapeJava(request.getParameter("userImmagine"));

		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("titoloImmagine non valido");
			return;
		}

		List<Commento> commenti = new ArrayList<Commento>();
		CommentoDAO commentoDAO = new CommentoDAO(connection);
		
		try {
			commenti = commentoDAO.ottieniCommenti(titolo_immagine,user_immagine);
			
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Impossibile trovare i commenti");
			return;
		}
		System.out.println("(GetComment) titolo_immagine: "+titolo_immagine + " " + user_immagine);
		//System.out.println("(GetComment) comm: ");
		Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(commenti);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);		

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
