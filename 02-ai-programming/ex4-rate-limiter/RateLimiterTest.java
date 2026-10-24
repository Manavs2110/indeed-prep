import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

public class RateLimiterTest {
    static int passed = 0, failed = 0;

    static void check(String name, Object expected, Object actual) {
        if (Objects.equals(expected, actual)) { passed++; }
        else { failed++; System.out.println("  FAIL " + name + "\n       expected: " + expected + "\n       actual:   " + actual); }
    }

    interface Level { void run() throws Exception; }

    static void level(int n, Level body) {
        System.out.println("Level " + n);
        int before = failed;
        try { body.run(); }
        catch (UnsupportedOperationException e) { failed++; System.out.println("  not implemented yet"); }
        catch (Exception e) { failed++; System.out.println("  ERROR " + e); e.printStackTrace(System.out); }
        System.out.println(failed == before ? "  ok" : "  has failures");
    }

    static List<Boolean> calls(SlidingWindowLimiter l, String c, long... ts) {
        List<Boolean> out = new ArrayList<>();
        for (long t : ts) out.add(l.allow(c, t));
        return out;
    }

    static List<Boolean> calls(TokenBucketLimiter l, String c, long... ts) {
        List<Boolean> out = new ArrayList<>();
        for (long t : ts) out.add(l.allow(c, t));
        return out;
    }

    /** Runs threads x callsPerThread allow() calls spread over clients at one timestamp; returns allowed count per client. */
    static Map<String, Integer> hammer(BiPredicate<String, Long> allow, int clients) throws Exception {
        int threads = 16, callsPerThread = 2_000;
        ConcurrentHashMap<String, AtomicInteger> allowed = new ConcurrentHashMap<>();
        CountDownLatch go = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        List<Future<?>> fs = new ArrayList<>();
        for (int t = 0; t < threads; t++) {
            final int seed = t;
            fs.add(pool.submit(() -> {
                go.await();
                for (int i = 0; i < callsPerThread; i++) {
                    String c = "c" + ((i + seed) % clients);
                    if (allow.test(c, 1_000L)) allowed.computeIfAbsent(c, k -> new AtomicInteger()).incrementAndGet();
                }
                return null;
            }));
        }
        go.countDown();
        for (Future<?> f : fs) f.get();
        pool.shutdown();
        Map<String, Integer> out = new TreeMap<>();
        for (int i = 0; i < clients; i++) out.put("c" + i, allowed.getOrDefault("c" + i, new AtomicInteger()).get());
        return out;
    }

    public static void main(String[] args) {
        int max = args.length > 0 ? Integer.parseInt(args[0]) : 4;

        if (max >= 1) level(1, () -> {
            SlidingWindowLimiter l = new SlidingWindowLimiter(3, 1000);
            check("first 3 allowed, 4th denied", List.of(true, true, true, false), calls(l, "a", 0, 100, 200, 300));
            check("other client independent", List.of(true), calls(l, "b", 300));
            check("still denied inside window", List.of(false), calls(l, "a", 999));
            check("oldest drops out at ts - window", List.of(true, false), calls(l, "a", 1000, 1000));
            check("two more slide out", List.of(true, true, false), calls(l, "a", 1100, 1200, 1200));
            check("denied requests don't count", List.of(true, true, true), calls(l, "a", 5000, 5001, 5002));
        });

        if (max >= 2) level(2, () -> {
            SlidingWindowLimiter l = new SlidingWindowLimiter(2, 1000);
            l.setLimit("vip", 4);
            check("vip gets 4", List.of(true, true, true, true, false), calls(l, "vip", 0, 1, 2, 3, 4));
            check("default unchanged", List.of(true, true, false), calls(l, "std", 0, 1, 2));
            l.setLimit("std", 3);
            check("raised limit counts existing requests", List.of(true, false), calls(l, "std", 3, 4));
            l.setLimit("vip", 1);
            check("lowered limit", List.of(false, true, false), calls(l, "vip", 500, 1004, 1005));
        });

        if (max >= 3) level(3, () -> {
            TokenBucketLimiter l = new TokenBucketLimiter(2, 1.0);
            check("starts full", List.of(true, true, false), calls(l, "a", 0, 0, 0));
            check("half a token is not enough", List.of(false), calls(l, "a", 500));
            check("refill accumulates", List.of(true, false), calls(l, "a", 1000, 1000));
            check("refill caps at capacity", List.of(true, true, false), calls(l, "a", 10_000, 10_000, 10_000));
            check("clients independent", List.of(true), calls(l, "b", 10_000));
            TokenBucketLimiter fast = new TokenBucketLimiter(5, 10.0); // 1 token per 100 ms
            check("fractional refill", List.of(true, true, true, true, true, false, true, false),
                    calls(fast, "x", 0, 0, 0, 0, 0, 50, 100, 150));
        });

        if (max >= 4) level(4, () -> {
            for (int round = 0; round < 5; round++) {
                SlidingWindowLimiter sw = new SlidingWindowLimiter(100, 10_000);
                Map<String, Integer> r1 = hammer(sw::allow, 4);
                check("sliding window exact under contention (round " + round + ")",
                        Map.of("c0", 100, "c1", 100, "c2", 100, "c3", 100), r1);
                TokenBucketLimiter tb = new TokenBucketLimiter(50, 1.0);
                Map<String, Integer> r2 = hammer(tb::allow, 4);
                check("token bucket exact under contention (round " + round + ")",
                        Map.of("c0", 50, "c1", 50, "c2", 50, "c3", 50), r2);
            }
        });

        System.out.println("\n" + passed + " passed, " + failed + " failed");
        if (failed > 0) System.exit(1);
    }
}
