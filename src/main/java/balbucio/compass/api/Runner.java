package balbucio.compass.api;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class Runner {

    private CompassUnit unit;

    private AtomicLong sucess = new AtomicLong(0);
    private AtomicLong fail = new AtomicLong(0);
    private AtomicLong timeouts = new AtomicLong(0);
    private ConcurrentMap<Integer, Long> responseCodes = new ConcurrentHashMap<>();

    public Runner(CompassUnit unit) {

    }
}
