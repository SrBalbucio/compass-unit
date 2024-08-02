import balbucio.compass.api.CompassUnit;
import balbucio.compass.api.route.TestRoute;
import express.Express;
import org.json.JSONObject;
import org.jsoup.Connection;

import java.io.IOException;
import java.time.Duration;
import java.util.Random;

public class ExampleTest {

    public static void main(String[] args) throws InterruptedException, IOException {
        // SERVIDOR DE EXEMPLO

        Express server = new Express();
        Random rand = new Random();

        server.post("/ping", (req, resp) -> {

            if(rand.nextBoolean()){
                resp.send();
                return;
            }

           resp.send("Pong!");
        });

        server.post("/auth", (req, resp) -> {
            JSONObject json = req.bodyAsJson();
            String user = json.getString("user");
            String password = json.getString("password");

            if(rand.nextBoolean()){
                resp.setStatus(202).send("Quer ser minha namorada!");
                return;
            }

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

            Thread.sleep(100L);

            resp.setStatus(200);
            resp.send("Ta SAFE!");
        });

        server.addExceptionHandler((e, resp, req) -> {
            req.setStatus(500).send("Ocorreu um erro aqui!");
        });

        server.listen(24466);

        CompassUnit compass = new CompassUnit(CompassUnit.TestConfig.builder()
                .url("http://localhost:24466")
                .identifier("example")
                .maxDuration(Duration.ofMinutes(1))
                .amountPerSecond(8)
                .threads(2)
                .build());

        compass.testRoute("/ping", new TestRoute()
                .addCreator((con, utils) -> con.method(Connection.Method.POST))
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
                .onResponse((req, resp) -> resp.statusCode() == 200 && !resp.body().isEmpty()));

        compass.startTest();
    }
}
