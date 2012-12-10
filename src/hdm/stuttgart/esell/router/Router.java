package hdm.stuttgart.esell.router;


public class Router {

	public static final int HTTP_OKAY = 200;
	public static final int HTTP_CREATED = 201;
	public static final int HTTP_SERVER_ERROR = 500;

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
	}

}
