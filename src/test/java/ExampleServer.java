import express.Express;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

public class ExampleServer extends Express {

    private Random rand = new Random();

    public ExampleServer(){
        this.get("/ping", (req, resp) -> {

            if(rand.nextBoolean()){
                resp.setStatus(404);
                resp.send();
                return;
            }

            Thread.sleep(rand.nextLong(300L));

            resp.setStatus(200);
            resp.send("Pong!");
        });

        this.post("/auth", (req, resp) -> {
            String body = req.bodyAsString();
            if(body.isEmpty()){
                resp.setStatus(202).send("Enviou sem texto ein pain!");
                return;
            }

            JSONObject json = new JSONObject(body);
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

            Thread.sleep(rand.nextLong(300L));

            resp.setStatus(200);
            resp.send("Ta SAFE!");
        });

        this.addExceptionHandler((e, resp, req) -> {
            e.printStackTrace();
            req.setStatus(500).send("Ocorreu um erro aqui!");
        });
    }
}
