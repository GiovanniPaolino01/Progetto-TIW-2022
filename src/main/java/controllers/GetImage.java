package controllers;


import java.io.File;  
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
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


@WebServlet("/GetImage")
public class GetImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
	String folderPath = "";
    
    public GetImage() {
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
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	String percorso = request.getParameter("percorso");

    	
		//System.out.println("filename "+filename+" \tfolderpath "+ folderPath + "\tpercorso:" + percorso + "estensione" + estensione);
		
		File file = new File(percorso); //folderPath inizialized in init
		String filename = file.getName();
		
		file = new File(folderPath, filename); //folderPath inizialized in init
		
		// set headers for browser
		/*response.setHeader("Content-Type", getServletContext().getMimeType(filename));
		response.setHeader("Content-Length", String.valueOf(file.length()));
		
		//TODO: test what happens  if you change inline by  attachment
		//System.out.println("Content-Disposition" + "inline; filename=\"" + file.getName() + "\"");
		response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");*/
																									
		// copy file to output stream
		Files.copy(file.toPath(), response.getOutputStream());

    }
    
    public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

    

}
