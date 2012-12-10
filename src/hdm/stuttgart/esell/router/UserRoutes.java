package hdm.stuttgart.esell.router;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;
import hdm.stuttgart.esell.Model.User;

import java.lang.reflect.Method;

import spark.Request;
import spark.Response;
import spark.Route;

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
				int userId = Integer.parseInt(req.params("id"));
				
				try {
					User user = User.getUser(userId);
					res.status(Router.HTTP_OKAY);
					return user.getJson();
				} catch (Exception e) {
					res.status(Router.HTTP_SERVER_ERROR);
					return e.getMessage();
				}
				
			}
		});
		
		// Speichert einen neuen Benutzer
		post(new Route("/user/") {
			@Override
			public Object handle(Request req, Response res) {
				try {
					User user = new User(
						req.queryParams("firstname"),
						req.queryParams("lastname"),
						req.queryParams("email"),
						req.queryParams("password")
					);
					
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
				Method method;
				String methodName;
				User user;
				
				try {
					int userId = Integer.parseInt(req.params("id"));
					user = User.getUser(userId);
				} catch (Exception e) {
					res.status(Router.HTTP_SERVER_ERROR);
					return e.getMessage();
				}
				
				for (String param : req.queryParams()) {
					methodName = "set" + param.substring(0, 1).toUpperCase() + param.substring(1);
					String newValue = req.queryParams(param);
					
					try {
						method = user.getClass().getMethod(methodName, String.class);
						method.invoke(user, newValue);
					} catch (Exception e) {
						res.status(Router.HTTP_SERVER_ERROR);
						return e.getMessage();
					}
				}
				
				try {
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
					User user = User.getUser(userId);
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
