package balbucio.compass.api;

import balbucio.compass.api.http.RequestCreator;
import balbucio.compass.api.route.TestRoute;
import balbucio.compass.api.task.TestTask;
import balbucio.compass.api.utilities.Utilities;
import lombok.Builder;
import lombok.Data;
import org.ajbrown.namemachine.NameGenerator;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class CompassUnit {

    private Executor executors;
    private Map<String, TestRoute> routes = new ConcurrentHashMap<>();
    private Utilities utilities;

    public CompassUnit() {
        this.utilities = new Utilities();
    }

    public CompassUnit testRoute(String path, TestRoute route) {
        this.routes.put(path, route);
        return this;
    }

    public void startTest(TestConfig config) throws InterruptedException {
        this.executors = (config.threads == -1 ? Executors.newCachedThreadPool() : Executors.newFixedThreadPool(config.threads));

        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis() + config.duration.toMillis();

        for (int i = 0; i < config.threads; i++) {
            executors.execute(new TestTask(this));
        }

        while(endTime > System.currentTimeMillis()){
            Thread.sleep(500L);
        }



    }

    @Builder
    @Data
    public class TestConfig{
        int threads = 1;
        Duration duration = Duration.ofMinutes(2);
    }
}
