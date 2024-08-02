package balbucio.compass.api;

import balbucio.compass.api.http.RequestCreator;
import balbucio.compass.api.route.TestRoute;
import balbucio.compass.api.task.ConnectionTest;
import com.github.nidorx.http.HttpRequest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class TestRunner {

    public static CopyOnWriteArraySet<RequestCreator> DEFAULT_CREATORS = new CopyOnWriteArraySet<>();

    static {
        DEFAULT_CREATORS.add((c, u) -> c.method("POST").contentType(HttpRequest.APPLICATION_JSON).data(u.getRandomMap()));
    }


    private File reportFile;
    private CompassUnit unit;
    private CompassUnit.TestConfig config;
    private ScheduledExecutorService executor;
    private Random random;
    private AtomicLong attempts = new AtomicLong(0);
    private AtomicLong sucess = new AtomicLong(0);
    private AtomicLong fail = new AtomicLong(0);
    private AtomicLong timeouts = new AtomicLong(0);
    private AtomicReference<List<Long>> times = new AtomicReference<>(new ArrayList<>());
    private AtomicReference<Map<Integer, Long>> responseCodes = new AtomicReference<>(new HashMap<>());
    private long testDuration;

    public TestRunner(CompassUnit unit, ScheduledExecutorService executor) {
        this.unit = unit;
        this.executor = executor;
        this.config = unit.getConfig();
        this.random = new Random();
        this.reportFile = new File("compass-reports", "compassreport-" + config.identifier + "-" + System.currentTimeMillis() + ".txt");
    }

    public void reset() {
        attempts.set(0);
        sucess.set(0);
        fail.set(0);
        timeouts.set(0);
        times.get().clear();
        responseCodes.get().clear();
        testDuration = 0l;
    }

    public void writeReport(String path) throws IOException {
        reportFile.getParentFile().mkdir();
        reportFile.createNewFile();
        List<Long> tts = times.get();

        AtomicLong avgTime = new AtomicLong();
        tts.forEach(avgTime::addAndGet);

        StringBuilder builder = new StringBuilder(Files.readString(reportFile.toPath(), StandardCharsets.UTF_8));
        builder.append("RESULTADOS DO ").append(path).append(":\n");
        builder.append("      Tentativas: ").append(attempts.get()).append("\n");
        builder.append("      Sucessos: ").append(sucess.get()).append("\n");
        builder.append("      Falhas: ").append(fail.get()).append("\n");
        builder.append("      Timeouts: ").append(timeouts.get()).append("\n");
        builder.append("      Avg. response time: ").append(tts.size() > 0 ? (avgTime.get() / tts.size()) : "-/-").append("ms").append("\n");
        builder.append("      Duração do teste: ").append(testDuration).append("ms").append("\n");
        builder.append("      Códigos de Resposta: ").append("\n");
        responseCodes.get().forEach((code, count) -> builder.append("         - ").append(code).append(": ").append(count).append("\n"));
        builder.append("\n");

        FileWriter writer = new FileWriter(reportFile);
        writer.append(builder);
        writer.flush();
        writer.close();
        System.out.println("The report for " + path + " is now ready.");
    }

    public void run(String path, TestRoute route) throws InterruptedException {
        CopyOnWriteArrayList<RequestCreator> creators = new CopyOnWriteArrayList<>();
        creators.addAll(DEFAULT_CREATORS);
        creators.addAll(route.getCreators());

        List<ScheduledFuture<?>> scheduledFutures = new ArrayList<>();

        for (int i = 0; i < (config.threads * config.amountPerSecond); i++) {
            scheduledFutures.add(executor.scheduleAtFixedRate(
                    new ConnectionTest(attempts, sucess, fail, timeouts, times, responseCodes, creators, random, path, unit.getUtilities(), route, config)
                    , 0, 1, TimeUnit.SECONDS));
        }

        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis() + config.maxDuration.toMillis();
        long lastIncrease = 0L;
        long multiplier = 0;

        while (System.currentTimeMillis() < endTime) {
            StringBuilder builder = new StringBuilder();

            if (System.currentTimeMillis() > lastIncrease && config.increaseDifficulty) {
                multiplier = random.nextInt(config.amountPerSecond);
                lastIncrease = System.currentTimeMillis() + random.nextLong(4000);
            }

            for (long i = 0; i < multiplier; i++) {
                scheduledFutures.add(executor.scheduleAtFixedRate(
                        new ConnectionTest(attempts, sucess, fail, timeouts, times, responseCodes, creators, random, path, unit.getUtilities(), route, config)
                        , 0, 1, TimeUnit.SECONDS));
            }

            long eta = endTime - System.currentTimeMillis();

            String etaHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
                    TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));

            builder.append("\r").append("[").append(path).append("] ")
                    .append(etaHms)
                    .append(" - Attempts: ").append(attempts.get())
                    .append(" Successes: ").append(sucess.get())
                    .append(" Failures: ").append(fail.get())
                    .append(" Timeouts: ").append(timeouts.get())
                    .append(" Multiplier: ").append(multiplier);
            System.out.print(builder.toString());
            Thread.sleep(150L);
        }

        testDuration = (System.currentTimeMillis() - startTime);

        scheduledFutures.forEach(f -> f.cancel(true));

        System.out.print("\r");
        System.out.print("[" + path + "] ROUTE TEST COMPLETED IN " + TimeUnit.MILLISECONDS.toSeconds(testDuration) + "s, EXECUTED " + attempts.get() + " ATTEMPTS!\n");
    }

}
