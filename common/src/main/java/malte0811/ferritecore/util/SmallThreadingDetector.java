package malte0811.ferritecore.util;

import malte0811.ferritecore.ducks.SmallThreadDetectable;
import net.minecraft.util.ThreadingDetector;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;

@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
public class SmallThreadingDetector {
    public static void acquire(SmallThreadDetectable obj, String name) {
        byte oldState;
        synchronized (obj) {
            oldState = obj.ferritecore$getState();
            if (oldState == SmallThreadDetectable.UNLOCKED) {
                // Fast path: previously unlocked, everything is fine
                // Performance: Acquire lock, "non-atomic CAS", release lock
                // Vanilla fast path: Acquire lock, atomic CAS (tryAcquire), release lock
                // So this should be at least as fast as the vanilla version
                obj.ferritecore$setState(SmallThreadDetectable.LOCKED);
                return;
                // Anything after this line will only run when we are going to crash, so performance is not a concern
            } else if (oldState == SmallThreadDetectable.LOCKED) {
                // Locking twice => start crash in synchronized block, release lock and wait
                // for release from other thread
                GlobalCrashHandler.startCrash(obj, name);
                obj.ferritecore$setState(SmallThreadDetectable.CRASHING);
            }
        }
        if (oldState == SmallThreadDetectable.LOCKED) {
            // Locking twice
            GlobalCrashHandler.crashAcquire(obj);
        } else {
            // already crashing, probably something like 3 acquires in a row
            // The vanilla detector doesn't explicitly handle this case and will probably produce confusing output,
            // this implementation throws an exception 1 second after the "main" threads have crashed instead.
            GlobalCrashHandler.crashBystander(obj);
        }
    }

    public static void release(SmallThreadDetectable obj) {
        byte oldState;
        synchronized (obj) {
            oldState = obj.ferritecore$getState();
            if (oldState == SmallThreadDetectable.LOCKED) {
                // Fast path, same performance (both here and vanilla) as in acquire
                obj.ferritecore$setState(SmallThreadDetectable.UNLOCKED);
                return;
            }
        }
        if (oldState == SmallThreadDetectable.CRASHING) {
            // Acquire started a crash and is waiting for this thread to also be ready
            GlobalCrashHandler.crashRelease(obj);
        }
        // Release without having acquired before: weird, but vanilla in principle allows it
    }

    /**
     * This code only runs when preparing a threading crash, so none of it needs to be remotely fast
     */
    private static class GlobalCrashHandler {
        private static final Object MONITOR = new Object();
        // SmallThreadDetectable's currently involved in crashes
        // Access to the map needs to be synchronized on MONITOR
        private static final Map<SmallThreadDetectable, CrashingState> ACTIVE_CRASHES = new IdentityHashMap<>();

        private static void startCrash(SmallThreadDetectable owner, String name) {
            synchronized (MONITOR) {
                ACTIVE_CRASHES.put(owner, new CrashingState(name, owner));
            }
        }

        private static void crashAcquire(SmallThreadDetectable owner) {
            var state = getAndWait(owner, ThreadRole.ACQUIRE);
            throw state.mainException;
        }

        private static void crashRelease(SmallThreadDetectable owner) {
            var state = getAndWait(owner, ThreadRole.RELEASE);
            throw state.mainException;
        }

        private static void crashBystander(SmallThreadDetectable owner) {
            var state = getAndWait(owner, ThreadRole.BYSTANDER);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException x) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException(
                    "Bystander to crash of type" + state.name + "on threads " + state.releaseThread + ", " + state.acquireThread
            );
        }

        private static CrashingState getAndWait(SmallThreadDetectable owner, ThreadRole role) {
            CrashingState result;
            synchronized (MONITOR) {
                result = Objects.requireNonNull(ACTIVE_CRASHES.get(owner));
            }
            result.waitUntilReady(role);
            return result;
        }
    }

    /**
     * Data needed to produce the proper crash for race on a single SmallThreadDetectable
     */
    private static class CrashingState {
        final String name;
        final SmallThreadDetectable owner;
        Thread acquireThread;
        Thread releaseThread;
        RuntimeException mainException;

        private CrashingState(String name, SmallThreadDetectable owner) {
            this.name = name;
            this.owner = owner;
        }

        public synchronized void waitUntilReady(ThreadRole role) {
            // Update thread fields with the newly known one (we're synchronized on `this`, so we can just access them
            // as we want)
            if (role == ThreadRole.ACQUIRE) {
                acquireThread = Thread.currentThread();
            } else if (role == ThreadRole.RELEASE) {
                releaseThread = Thread.currentThread();
            }
            // Notify other threads waiting for this crash to be ready
            notifyAll();
            try {
                waitUntilOrCrash(() -> acquireThread != null && releaseThread != null);
                if (role == ThreadRole.ACQUIRE) {
                    mainException = ThreadingDetector.makeThreadingException(name, releaseThread);
                    notifyAll();
                } else {
                    waitUntilOrCrash(() -> mainException != null);
                }
            } catch (InterruptedException x) {
                Thread.currentThread().interrupt();
            }
        }

        private synchronized void waitUntilOrCrash(BooleanSupplier isReady) throws InterruptedException {
            final long maxTotalTime = 10_000;
            final var start = System.currentTimeMillis();
            while (!isReady.getAsBoolean()) {
                if (System.currentTimeMillis() - start > 6 * maxTotalTime) {
                    // Crash without both threads present if we don't manage to "find" them within 60 seconds
                    // Happens e.g. when a release call is just missing, vanilla would hang indefinitely instead
                    // in this case
                    throw new RuntimeException(
                            "Threading detector crash did not find other thread, missing release call?"+
                            " Owner: "+this.owner+" (ID hash: "+System.identityHashCode(this.owner)+")"+
                            ", time: "+System.currentTimeMillis()
                    );
                }
                // Release lock on this for up to 10 seconds, or until the other threads are ready
                this.wait(maxTotalTime);
            }
        }
    }

    private enum ThreadRole {
        ACQUIRE, RELEASE, BYSTANDER
    }
}
