import static spark.Spark.get;
import spark.Request;
import spark.Response;
import spark.Route;

public class SparkTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		get(new Route("/hello/:name") {
			
			@Override
			public Object handle(Request req, Response res) {
				
				return "Hello " + req.params("name");
			}
		});
		
		
	}

}
