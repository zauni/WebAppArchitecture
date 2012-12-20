package hdm.stuttgart.esell.router;

import hdm.stuttgart.esell.errors.ErrorHandler;
import hdm.stuttgart.esell.errors.ErrorHandler.ErrorCode;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import spark.Request;

public class FileHandler {
	private int petitionId;
	
	private String path = "images/";
	
	/**
	 * Konstruktor
	 * @param petitionId ID eines Kaufgesuchs
	 */
	public FileHandler(int petitionId) {
		this.petitionId = petitionId;
	}
	
	/**
	 * Konstruktor
	 * @param petitionId ID eines Kaufgesuchs
	 * @param path Pfad zum Bilderordner
	 */
	public FileHandler(int petitionId, String path) {
		this.petitionId = petitionId;
		this.path = path;
	}
	
	/**
	 * Holt das Bild eines Kaufgesuchs
	 * @return Bild des Kaufgesuchs
	 * @throws ErrorHandler 
	 */
	public File getImage() throws ErrorHandler {
		File image = new File(getFullPath());
        if(image.exists()) {
        	return image;
        }
        else {
        	throw new ErrorHandler(ErrorCode.ERR);
        }
	}
	
	/**
	 * Speichert das Bild, das in einem Multipart-Request enthalten ist
	 * @param req Der Spark Request
	 * @throws ErrorHandler
	 */
	public void saveImgFromRequest(Request req) throws ErrorHandler {
		try {
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
				throw new ErrorHandler(ErrorCode.ERR);
			}
			
			// Process the uploaded items
			@SuppressWarnings("rawtypes")
			Iterator iter = items.iterator();
			while (iter.hasNext()) {
			    FileItem item = (FileItem) iter.next();

			    if (!item.isFormField()) {
			        File uploadedFile = new File(getFullPath());
			        if(uploadedFile.exists()) {
			        	uploadedFile.delete();
			        }
			        item.write(uploadedFile);
			        
			        break; // es kann nur 1 Bild gespeichert werden
			    }
			}
		}
		catch(Exception e) {
			throw new ErrorHandler(ErrorHandler.ErrorCode.ERR);
		}
	}
	
	/**
	 * Gibt den vollen Pfad zum Bild zurück
	 * @return String
	 */
	public String getFullPath() {
		return path + petitionId + ".jpg";
	}
}
