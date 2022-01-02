package malte0811.ferritecore.util;

import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

class SmallThreadingDetectorTest {
    private static final SmallThreadingDetector DETECTOR;

    static {
        try {
            DETECTOR = new SmallThreadingDetector(
                    OwnedObject.class.getDeclaredField("owner"), "Test threading detector"
            );
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void setup() {
        SharedConstants.setVersion(new FakeGameVersion());
    }

    @Test
    public void testSynchronized() {
        var obj = new OwnedObject();
        var monitor = new Object();
        final int numThreads = 10;
        var barrier = new CyclicBarrier(numThreads);
        Executable acquireAndRelease = () -> {
            for (int i = 0; i < 10; ++i) {
                synchronized (monitor) {
                    DETECTOR.acquire(obj);
                    DETECTOR.release(obj);
                }
                barrier.await();
            }
        };
        for (int i = 0; i < numThreads; ++i)
            runOnNewThread(acquireAndRelease);
    }

    @Test
    public void testUnsynchronized() throws InterruptedException {
        var obj = new OwnedObject();
        runOnNewThread(() -> DETECTOR.acquire(obj)).join();
        Assertions.assertThrows(ReportedException.class, () -> DETECTOR.acquire(obj));
    }

    @Test
    // This isn't guaranteed to pass, but in practice it always will, and that's good enough
    public void testRace() throws InterruptedException {
        var obj = new OwnedObject();
        AtomicBoolean anyTripped = new AtomicBoolean(false);
        for (int i = 0; i < 10; ++i)
            runOnNewThread(() -> {
                final long start = System.currentTimeMillis();
                while (!anyTripped.get() && System.currentTimeMillis() - start < 1000) {
                    DETECTOR.acquire(obj);
                    DETECTOR.release(obj);
                }
            }, $ -> anyTripped.set(true));
        Thread.sleep(1000);
        Assertions.assertTrue(anyTripped.get());
    }

    private static Thread runOnNewThread(Executable toRun) {
        return runOnNewThread(toRun, Throwable::printStackTrace);
    }

    private static Thread runOnNewThread(Executable toRun, Consumer<Throwable> onXCP) {
        var thread = new Thread(() -> {
            try {
                toRun.execute();
            } catch (Throwable e) {
                onXCP.accept(e);
            }
        });
        thread.start();
        return thread;
    }

    public static class OwnedObject {
        public Thread owner;
    }
}