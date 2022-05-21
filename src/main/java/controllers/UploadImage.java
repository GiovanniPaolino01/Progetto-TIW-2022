package controllers;

import java.io.File; 
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.Utente;
import dao.ImmagineDAO;
import utils.ConnectionHandler;

@WebServlet("/UploadImage")
@MultipartConfig
public class UploadImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
	String folderPath = "";
    
    public UploadImage() {
        super();
    }
    
    public void init() throws ServletException {
    	
    	folderPath = getServletContext().getInitParameter("outputpath");
    	
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
		
		String Nome_Immagine = StringEscapeUtils.escapeJava(request.getParameter("nome_immagine"));
		//String Nome_Immagine = StringEscapeUtils.escapeJava(request.getParameter("img"));
		Utente utente = (Utente) session.getAttribute("utente");
		String username = utente.getUsername();
		long milliseconds = System.currentTimeMillis();
		Date data = new Date(milliseconds);
		String Testo_Descrittivo = null;
		Testo_Descrittivo = StringEscapeUtils.escapeJava(request.getParameter("testo_descrittivo"));
		String titolo_album = "Nuove_Immagini";
		ImmagineDAO immagineDAO = new ImmagineDAO(connection);
		
		Part imagePart = request.getPart("img");
		InputStream imageStream = null;
		String mimeType = null;
		
		// TODO: controllo che sia un tipo png, jpg
		//TODO: da vedere ok di ritorno
		
		if (imagePart == null || imagePart.getSize() <= 0) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing file in request!");
			return;
		}
		
		String imageName = Paths.get(imagePart.getSubmittedFileName()).getFileName().toString();
		String outputPath = folderPath + imageName;
		
		if (imagePart != null) {
			imageStream = imagePart.getInputStream();
			String filename = imagePart.getSubmittedFileName();
			mimeType = getServletContext().getMimeType(filename);
		}
	
		File image = new File(outputPath);
		
		try (InputStream imageContent = imagePart.getInputStream()) {
			/**
			 * copio il file dal file content alla cartella in cui voglio andare
			 */
			Files.copy(imageContent, image.toPath());
			//System.out.println("File saved correctly!");
			
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while saving file");
		}
		
		
		//salvi nel DB l'immagine
		try {
		
			immagineDAO.creaImmagineNelDB(Nome_Immagine, username,  data, Testo_Descrittivo, outputPath, titolo_album, imageStream);
			
			String path;
			path = getServletContext().getContextPath() + "/GoToHome";
			response.sendRedirect(path);
			
			
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile fare upload dell'immagine");
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