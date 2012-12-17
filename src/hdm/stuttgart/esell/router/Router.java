package hdm.stuttgart.esell.router;

import static spark.Spark.post;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import spark.Request;
import spark.Response;
import spark.Route;


public class Router {

	public static final int HTTP_OKAY = 200;
	public static final int HTTP_CREATED = 201;
	public static final int HTTP_SERVER_ERROR = 500;
	
	public static Logger LOG = Logger.getLogger(Router.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		/**
		 * 
			
			Sonstige Routen
			Registrieren: GET /register/ (Das Formular hätte dann als action die POST /user/ Route)
			Login: POST /login/ (nur POST, da wir davon ausgehen, dass es keine separate Login-Seite gibt)
			Logout: GET /logout/
		 */
		
		// CRUD Benutzer
		new UserRoutes();
		
		// CRUD Petition
		new PetitionRoutes();
		
		// Sucherouten
		new SearchRoutes();
		
		post(new Route("/pic") {
			@Override
			public Object handle(Request req, Response res) {
				StringBuffer buf = new StringBuffer();
				HttpServletRequest raw = req.raw();
				
				// Create a factory for disk-based file items
				FileItemFactory factory = new DiskFileItemFactory();

				// Create a new file upload handler
				ServletFileUpload upload = new ServletFileUpload(factory);

				// Parse the request
				@SuppressWarnings("rawtypes")
				List items;
				try {
					 items = upload.parseRequest(raw);
				} catch (FileUploadException e) {
					res.status(Router.HTTP_SERVER_ERROR);
					return e.getMessage();
				}
				
				// Process the uploaded items
				@SuppressWarnings("rawtypes")
				Iterator iter = items.iterator();
				while (iter.hasNext()) {
				    FileItem item = (FileItem) iter.next();

				    if (item.isFormField()) {
				        String name = item.getFieldName();
				        String value = item.getString();
				        buf.append(" | Item ist ein Feld: " + name + ": " + value);
				    } else {
				    	String fieldName = item.getFieldName();
				        String fileName = item.getName();
				        String contentType = item.getContentType();
				        boolean isInMemory = item.isInMemory();
				        long sizeInBytes = item.getSize();
				        buf.append(" | Item ist eine Datei! field: " + fieldName + ", value: " + fileName + ", typ: " + contentType + ", size: "+ 
				        		 sizeInBytes + ", in mem: " + isInMemory);
				    }
				}
				
				return buf.toString();
			}
		});
	}

}
