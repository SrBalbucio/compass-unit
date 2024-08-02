package balbucio.compass.api.task;

import balbucio.compass.api.CompassUnit;
import balbucio.compass.api.http.RequestCreator;
import balbucio.compass.api.route.TestRoute;
import balbucio.compass.api.utilities.Utilities;
import com.github.nidorx.http.HttpRequest;
import com.github.nidorx.http.HttpResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public class ConnectionTest implements Runnable {

    @NonNull
    private AtomicLong attempts;
    @NonNull
    private AtomicLong sucess;
    @NonNull
    private AtomicLong fail;
    @NonNull
    private AtomicLong timeouts;
    @NonNull
    private AtomicReference<List<Long>> times;
    @NonNull
    private AtomicReference<Map<Integer, Long>> responseCodes;
    @NonNull
    private CopyOnWriteArrayList<RequestCreator> creators;
    @NonNull
    private Random random;
    @NonNull
    private String path;
    @NonNull
    private Utilities utils;
    @NonNull
    private TestRoute route;
    @NonNull
    CompassUnit.TestConfig config;

    @Override
    public void run() {

        try {
            RequestCreator creator = creators.get(random.nextInt(creators.size())); // resgata um criador aleatório
            HttpRequest connection = creator.createRequest(createConnection(path), utils); // cria um request
            long start = System.currentTimeMillis(); // executa
            HttpResponse response = connection.execute();
            times.get().add((start - System.currentTimeMillis() + 100)); // armazena o tempo entre as respostas
            boolean ok = route.getResponseHandler().onResponse(connection, response); // checa se a resposta é válida
            long n = ok ? sucess.incrementAndGet() : fail.incrementAndGet();

            int responseCode = response.statusCode;
            long rspcount = responseCodes.get().getOrDefault(responseCode, 0L) + 1; // geta e adiciona mais um
            responseCodes.get().put(responseCode, rspcount); // seta a nova resposta
            attempts.incrementAndGet();
        } catch (IOException io) {
            io.printStackTrace();
            fail.incrementAndGet();
            timeouts.incrementAndGet();
        } catch (Exception e) {
            e.printStackTrace();
            fail.incrementAndGet();
        }
    }

    private HttpRequest createConnection(String action) {
        return HttpRequest.build(config.getUrl() + action)
                .timeout(2000);
    }
}
