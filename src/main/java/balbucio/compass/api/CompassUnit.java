package balbucio.compass.api;

import balbucio.compass.api.route.TestRoute;
import balbucio.compass.api.utilities.Utilities;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.*;

public class CompassUnit {

    private ScheduledExecutorService executor;
    @Getter
    @Setter
    private TestConfig config;
    @Getter
    private Map<String, TestRoute> routes = new ConcurrentHashMap<>();
    @Getter
    private Utilities utilities;

    public CompassUnit(String url) {
        this(TestConfig.builder().url(url).build());
    }

    public CompassUnit(TestConfig config) {
        this.utilities = new Utilities();
        this.config = config;
        this.executor = Executors.newScheduledThreadPool(config.threads);
    }

    public CompassUnit testRoute(String path, TestRoute route) {
        this.routes.put(path, route);
        return this;
    }

    public void startTest() throws InterruptedException, IOException {
        TestRunner runner = new TestRunner(this, executor);
        System.out.println("Initializing test suite.");
        for (String s : routes.keySet()) {
            runner.run(s, routes.get(s));
            runner.writeReport(s);
            runner.reset();
        }
    }

    @Builder
    @Data
    public static class TestConfig{
        String identifier = "test-"+System.currentTimeMillis();
        String url = "http://localhost:8080";
        int threads = 1;
        int amountPerSecond = 4;
        Duration maxDuration = Duration.ofMinutes(3);
    }
}
