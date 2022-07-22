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
import beans.Immagine;
import dao.ImmagineDAO;
import utils.ConnectionHandler;


@WebServlet("/GetImage")
public class GetImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	String folderPath = "";
    
    public GetImage() {
        super();
    }
    
    public void init() throws ServletException {
    	
    	folderPath = getServletContext().getInitParameter("outputpath");
    	
		connection = ConnectionHandler.getConnection(getServletContext());
	}
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	//String percorso = request.getParameter("percorso");

    	String titoloAlbum = StringEscapeUtils.escapeJava(request.getParameter("titoloAlbum"));
    	String user_id = StringEscapeUtils.escapeJava(request.getParameter("user_id"));
    	
    	System.out.println("(get image) " + titoloAlbum + " " + user_id);
    	
    	ImmagineDAO immagineDAO = new ImmagineDAO(connection);
    	List<Immagine> immagini = new ArrayList<Immagine>();    	
    	
    	try {
    		immagini = immagineDAO.selezionaImmaginiDaAlbum(user_id, titoloAlbum);
    		//System.out.println(immagini.get(0).getTitolo());
    	}catch(SQLException e) {
    		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Impossibile trovare le immagini dell'album");
			return;
    	}
    	
    	
    	Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(immagini);
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);


    }
    
    public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

    

}
