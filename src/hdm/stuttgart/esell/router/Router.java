package hdm.stuttgart.esell.router;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;
import hdm.stuttgart.esell.Model.Petition;
import hdm.stuttgart.esell.Model.User;

import java.lang.reflect.Method;
import java.sql.Date;
import java.text.DateFormat;

import spark.Request;
import spark.Response;
import spark.Route;


public class Router {

	public static final int HTTP_OKAY = 200;
	public static final int HTTP_CREATED = 201;
	public static final int HTTP_SERVER_ERROR = 500;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		/**
		 * CRUD Routen für User:
			(GET /user/:id, POST /user/, PUT /user/:id, DELETE /user/:id)
			CRUD Routen für Kaufgesuche (engl. Petition)
			(GET /petition/:id, POST /petition/, PUT /petition/:id, DELETE /petition/:id)
			
			Routen für Suchen
			
		    Alle Kaufgesuche eines Nutzers: GET /user/:id/petitions
		    Alle Kaufgesuche einer Kategorie: GET /category/:catId
		    Alle Kaufgesuche: GET /petitions/
		    Alle Nutzer: GET /users/
			
			Sonstige Routen
			Registrieren: GET /register/ (Das Formular hätte dann als action die POST /user/ Route)
			Login: POST /login/ (nur POST, da wir davon ausgehen, dass es keine separate Login-Seite gibt)
			Logout: GET /logout/
		 */
		
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
		
		/**
		 * Kaufgesuche
		 */
		// Gibt Informationen eines Kaufgesuchs zurück
		get(new Route("/petition/:id") {
			@Override
			public Object handle(Request req, Response res) {
				try {
					int id = Integer.parseInt(req.params("id"));
					Petition petition = Petition.getPetition(id);

					res.status(Router.HTTP_OKAY);
					return petition.getJson();
				} catch (Exception e) {
					res.status(Router.HTTP_SERVER_ERROR);
					return e.getMessage();
				}
				
			}
		});
		
		// Speichert eine neues Kaufgesuch
		post(new Route("/petition/") {
			@Override
			public Object handle(Request req, Response res) {
				try {
					Petition petition = new Petition(
						Integer.parseInt(req.queryParams("userid")),
						req.queryParams("title"),
						Integer.parseInt(req.queryParams("categoryid")),
						Integer.parseInt(req.queryParams("amount")),
						"Searching" // TODO: Statt dem enum eine Klasse oder Konstanten in die Petition Klasse
					);
					
					petition.insert();
					res.status(Router.HTTP_CREATED);
					return petition.getJson();
				}
				catch(Exception e) {
					res.status(Router.HTTP_SERVER_ERROR);
					return e.getMessage();
				}
			}
		});
		
		// Ändert ein Kaufgesuch
		put(new Route("/petition/:id") {
			@Override
			public Object handle(Request req, Response res) {
				Method method;
				String methodName;
				int id;
				Petition petition;
				DateFormat formatter = DateFormat.getDateInstance(DateFormat.FULL);
				
				// TODO: Date in der Petition diskutieren
				
				try {
					id = Integer.parseInt(req.params("id"));
					petition = Petition.getPetition(id);
				} catch (Exception e) {
					res.status(Router.HTTP_SERVER_ERROR);
					return e.getMessage();
				}
				
				for (String param : req.queryParams()) {
					methodName = "set" + param.substring(0, 1).toUpperCase() + param.substring(1);
					
					try {
						switch (param) {
							case "userid":
							case "title":
							case "description":
							case "categoryid":
							case "state":
								String newValue = req.queryParams(param);
								method = petition.getClass().getMethod(methodName, String.class);
								method.invoke(petition, newValue);
								break;
								
							case "price":
							case "amount":
								int newInt = Integer.parseInt(req.queryParams(param));
								method = petition.getClass().getMethod(methodName, int.class);
								method.invoke(petition, newInt);
								break;
								
							case "creation":
								Date newDate = (Date) formatter.parse(req.queryParams(param));
								method = petition.getClass().getMethod(methodName, Date.class);
								method.invoke(petition, newDate);
								break;
	
							default:
								break;
						}
					} catch (Exception e) {
						res.status(Router.HTTP_SERVER_ERROR);
						return e.getMessage();
					}
				}
				
				try {
					petition.update();
					res.status(Router.HTTP_CREATED);
					return petition.getJson();
				}
				catch(Exception e) {
					res.status(Router.HTTP_SERVER_ERROR);
					return e.getMessage();
				}
			}
		});
		
		// Löscht ein Kaufgesuch
		delete(new Route("/petition/:id") {
			@Override
			public Object handle(Request req, Response res) {
				try {
					int id = Integer.parseInt(req.params("id"));
					Petition petition = Petition.getPetition(id);
					petition.delete();
					
					res.status(Router.HTTP_OKAY);
					return petition.getJson();
				} catch (Exception e) {
					res.status(Router.HTTP_SERVER_ERROR);
					return e.getMessage();
				}
			}
		});
		
	}

}
