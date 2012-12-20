package hdm.stuttgart.esell.router;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;
import hdm.stuttgart.esell.Model.User;
import spark.Request;
import spark.Response;
import spark.Route;

import com.google.gson.Gson;

/**
 * CRUD Routen für User:
 * (GET /user/:id, POST /user/, PUT /user/:id, DELETE /user/:id)
 */

public class UserRoutes {
	public UserRoutes() {
		
		// Gibt Informationen eines Users zurück
		get(new Route("/user/:id") {
			
			@Override
			public Object handle(Request req, Response res) {
				try {
					int userId = Integer.parseInt(req.params("id"));
					
					User user = User.getUserByID(userId);
					res.status(Router.HTTP_OKAY);
					return user.getJson();
				} catch (Exception e) {
					res.status(Router.HTTP_SERVER_ERROR);
					return e.getMessage();
				}
				
			}
		});
		
		// Speichert einen neuen Benutzer
		post(new Route("/user") {
			@Override
			public Object handle(Request req, Response res) {
				try {
					Gson gson = new Gson();
					
					User user = gson.fromJson(req.body(), User.class);
					
					user.insert();
					res.status(Router.HTTP_CREATED);
					return user.getJson();
				}
				catch(Exception e) {
					res.status(Router.HTTP_SERVER_ERROR);
					return e.getMessage();
				}
			}
		});
		
		// Ändert einen Nutzer
		put(new Route("/user/:id") {
			@Override
			public Object handle(Request req, Response res) {
				try {
					int userId = Integer.parseInt(req.params("id"));
					User user = User.getUserByID(userId);
				
					Gson gson = new Gson();
					User updateUser = gson.fromJson(req.body(), User.class);
					
					user.setEmail(updateUser.getEmailadress());
					user.setFirstname(updateUser.getFirstname());
					user.setLastname(updateUser.getLastname());
					user.setUsername(updateUser.getUsername());
				
					user.update();
					res.status(Router.HTTP_CREATED);
					return user.getJson();
				}
				catch(Exception e) {
					res.status(Router.HTTP_SERVER_ERROR);
					return e.getMessage();
				}
			}
		});
		
		// Löscht einen Nutzer
		delete(new Route("/user/:id") {
			@Override
			public Object handle(Request req, Response res) {
				try {
					int userId = Integer.parseInt(req.params("id"));
					User user = User.getUserByID(userId);
					user.delete();
					
					res.status(Router.HTTP_OKAY);
					return user.getJson();
				} catch (Exception e) {
					res.status(Router.HTTP_SERVER_ERROR);
					return e.getMessage();
				}
			}
		});
		
	}
}
