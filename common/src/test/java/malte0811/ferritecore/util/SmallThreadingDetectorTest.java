package malte0811.ferritecore.util;

import malte0811.ferritecore.ducks.SmallThreadDetectable;
import net.minecraft.SharedConstants;
import net.minecraft.util.ThreadingDetector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

class SmallThreadingDetectorTest {
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
                    SmallThreadingDetector.acquire(obj, "test");
                    SmallThreadingDetector.release(obj);
                }
                barrier.await();
            }
        };
        for (int i = 0; i < numThreads; ++i)
            runOnNewThread(acquireAndRelease);
    }

    @Test
    public void testHandoff() throws InterruptedException {
        var obj = new OwnedObject();
        runOnNewThread(() -> SmallThreadingDetector.acquire(obj, "test")).join();
        SmallThreadingDetector.release(obj);
    }

    @Test
    // This isn't guaranteed to pass, but in practice it always will, and that's good enough
    public void testRace() throws InterruptedException {
        var obj = new OwnedObject();
        AtomicBoolean anyTripped = new AtomicBoolean(false);
        List<Thread> threads = new ArrayList<>(10);
        for (int i = 0; i < 10; ++i)
            threads.add(runOnNewThread(() -> {
                final long start = System.currentTimeMillis();
                while (!anyTripped.get() && System.currentTimeMillis() - start < 1000) {
                    SmallThreadingDetector.acquire(obj, "test");
                    SmallThreadingDetector.release(obj);
                }
            }, $ -> anyTripped.set(true)));
        Thread.sleep(1000);
        for (var thread : threads)
            thread.join();
        Assertions.assertTrue(anyTripped.get());
    }

    @Test
    public void testReleaseNoAcquire() {
        var obj = new OwnedObject();
        SmallThreadingDetector.release(obj);

        ThreadingDetector detec = new ThreadingDetector("test");
        detec.checkAndUnlock();
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

    public static class OwnedObject implements SmallThreadDetectable {
        private byte state = 0;

        @Override
        public byte ferritecore$getState() {
            return state;
        }

        @Override
        public void ferritecore$setState(byte newState) {
            state = newState;
        }
    }
}