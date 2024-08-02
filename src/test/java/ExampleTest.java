import balbucio.compass.api.CompassUnit;
import balbucio.compass.api.route.TestRoute;
import express.Express;
import org.json.JSONObject;
import org.jsoup.Connection;

public class ExampleTest {

    public static void main(String[] args) {
        // SERVIDOR DE EXEMPLO

        Express server = new Express();

        server.get("/ping", (req, resp) -> {
           resp.send("Pong!");
        });

        server.get("/auth", (req, resp) -> {
            JSONObject json = req.bodyAsJson();
            String user = json.getString("user");
            String password = json.getString("password");

            if(user.isEmpty()){
                resp.setStatus(404);
                resp.send("Invalid user");
                return;
            }

            if(password.length() < 8){
                resp.setStatus(403);
                resp.send("Invalid password");
                return;
            }

            resp.setStatus(200);
            resp.send("Ta SAFE!");
        });


        CompassUnit compass = new CompassUnit();

        compass.testRoute("/ping", new TestRoute()
                .addCreator((con, utils) -> con.method(Connection.Method.GET))
                .onResponse((res, resp) -> resp.body().equalsIgnoreCase("Pong!")));

        compass.testRoute("/auth", new TestRoute()
                .addCreator((con, utils) -> con.requestBody(new JSONObject()
                        .put("user", utils.generateNameAsString())
                        .put("password", utils.generateToken(8))
                        .toString()).method(Connection.Method.POST))
                .addCreator((con, utils) -> con.requestBody(new JSONObject()
                        .put("user", "")
                        .put("password", utils.generateToken(8))
                        .toString()).method(Connection.Method.POST))
                .addCreator((con, utils) -> con.requestBody(new JSONObject()
                        .put("user", "")
                        .put("password", "")
                        .toString()).method(Connection.Method.POST))
                .onResponse((req, resp) -> resp.statusCode() != 200));

        compass.startTest();
    }
}
