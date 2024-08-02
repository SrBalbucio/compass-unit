import balbucio.compass.api.CompassUnit;
import balbucio.compass.api.route.TestRoute;
import com.github.nidorx.http.HttpRequest;
import java.io.IOException;
import java.time.Duration;

public class ExampleTest {

    public static void main(String[] args) throws InterruptedException, IOException {
        // SERVIDOR DE EXEMPLO
        ExampleServer server = new ExampleServer();
        server.listen(24466);

//        HttpRequest.DEBUG = true;

        CompassUnit compass = new CompassUnit(CompassUnit.TestConfig.builder()
                .url("http://localhost:24466")
                .identifier("example")
                .maxDuration(Duration.ofMinutes(1))
                .amountPerSecond(8)
                .threads(2)
                .increaseDifficulty(true)
                .build());

        compass.testRoute("/ping", new TestRoute()
                .addCreator((req, utils) -> req.method("GET"))
                .onResponse((res, resp) -> resp.statusCode == 200));

        compass.testRoute("/auth", new TestRoute()
                .addCreator((req, utils) -> req.method("POST").contentType(HttpRequest.APPLICATION_JSON).data("user", "").data("password", utils.generateToken(4)))
                .addCreator((req, utils) -> req.method("POST").contentType(HttpRequest.APPLICATION_JSON).data("user", utils.generateNameAsString()).data("password", utils.generateToken(8)))
                .addCreator((req, utils) -> req.method("POST").contentType(HttpRequest.APPLICATION_JSON).data("user", "").data("password", ""))
                .onResponse((req, resp) -> resp.statusCode == 200));

        compass.startTest();
        compass.destroy();
        server.stop();
    }
}
