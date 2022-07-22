package controllers;

import org.apache.commons.io.FilenameUtils;
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
		
		String Nome_Immagine = null;
		boolean isBadRequest=false;
		try {
			/*estraggo i parametri dalla request, se sono nulli vengono mandate eccezioni*/
			Nome_Immagine = StringEscapeUtils.escapeJava(request.getParameter("nome_immagine"));
			
		} catch (NumberFormatException | NullPointerException e) { /*Le eccezioni vengono lanciate se ci sono campi con formati errati o non compilati*/
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile eseguire l'upload dell'immagine");
		}

		
		try { /*Controllo che nel DB non esista un'immagine con lo stesso nome per quell'utente*/
			ImmagineDAO immagineDAO = new ImmagineDAO(connection);
			Utente utente = (Utente) session.getAttribute("utente");
			String username = utente.getUsername();
			if(false == immagineDAO.controlloUnitcitaImmaginePerUtente(Nome_Immagine, username) || Nome_Immagine ==null) {
				isBadRequest = true;
			}
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile controllare se l'album esiste già");
		}
		
		
		
		if(isBadRequest) {
			String path = "/GoToHome";
			request.setAttribute("errorUpload", "Titolo immagine già presente");
			request.getRequestDispatcher(path).forward(request, response);
			
		}else {
			
			ImmagineDAO immagineDAO = new ImmagineDAO(connection);
			
			
			long milliseconds = System.currentTimeMillis();
			Date data = new Date(milliseconds);
			String Testo_Descrittivo = StringEscapeUtils.escapeJava(request.getParameter("testo_descrittivo"));
			String titolo_album = "Nuove_Immagini";
			Utente utente = (Utente) session.getAttribute("utente");
			String username = utente.getUsername();
			
				Part imagePart = request.getPart("img");
				InputStream imageStream = null;
				//String mimeType = null;
				
				if (imagePart == null || imagePart.getSize() <= 0) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "File mancante");
					return;
				}
				
				String imageName = Paths.get(imagePart.getSubmittedFileName()).getFileName().toString();
				String outputPath = folderPath + imageName;
				String extension = FilenameUtils.getExtension(imageName);
				
				if(extension.equals("jpg") || extension.equals("JPG") || extension.equals("jpeg") || extension.equals("JPEG") || extension.equals("png") || extension.equals("PNG") || extension.equals("webp") || extension.equals("WEBP")) {
					
					if (imagePart != null) {
						imageStream = imagePart.getInputStream();
						//String filename = imagePart.getSubmittedFileName();
						//mimeType = getServletContext().getMimeType(filename);
					}
				
					File image = new File(outputPath);
					
					try /*(InputStream imageContent = imagePart.getInputStream())*/ {
						
						//copio il file dal file content alla cartella di destinazione
						
						Files.copy(imageStream, image.toPath());
					
					} catch (Exception e) {
						e.printStackTrace();
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore nel salvataggio del file");
						return;
					}
					
					
					//salvi nel DB l'immagine
					try {
					
						immagineDAO.creaImmagineNelDB(Nome_Immagine, username,  data, Testo_Descrittivo, outputPath, titolo_album, imageStream);
						
					}catch(SQLException e) {
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile salvare nel DB");
						return;
					}
				}
				else {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Il tipo di file non e' un'immagine, inserisci un'immagine!");
					return;
				}
		    
				String path;
				path = "/GoToHome";
				request.getRequestDispatcher(path).forward(request, response);
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
