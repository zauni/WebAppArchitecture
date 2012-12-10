package hdm.stuttgart.esell.router;

import static spark.Spark.get;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Routen für Suchen
 * 		
 * Alle Kaufgesuche eines Nutzers: GET /user/:id/petitions
 * Alle Kaufgesuche einer Kategorie: GET /category/:catId
 * Alle Kaufgesuche: GET /petitions/
 * Alle Nutzer: GET /users/
 *
 */

public class SearchRoutes {
	public SearchRoutes() {
		
		get(new Route("/user/:id/petitions") {
			@Override
			public Object handle(Request req, Response res) {
				try {
					//int userId = Integer.parseInt(req.params("id"));
					
					//User user = User.getUser(userId);
					// TODO: PetitionList nutzen!
					//ArrayList<Petition> petitions = user.getPetitions();
					//return petitions.getJson();
					return "";
				} catch (Exception e) {
					res.status(Router.HTTP_SERVER_ERROR);
					return e.getMessage();
				}
			}
		});
		
	}
}
