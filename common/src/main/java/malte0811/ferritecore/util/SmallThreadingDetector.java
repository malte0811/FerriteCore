package malte0811.ferritecore.util;

import com.google.common.base.Preconditions;
import net.minecraft.util.ThreadingDetector;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

public class SmallThreadingDetector {
    private final VarHandle ownerThread;
    private final String name;

    public SmallThreadingDetector(Field ownerThread, String name) throws IllegalAccessException {
        Preconditions.checkArgument(ownerThread.getType() == Thread.class);
        this.ownerThread = MethodHandles.lookup().unreflectVarHandle(ownerThread);
        this.name = name;
    }

    public void acquire(Object obj) {
        Thread currentThread = Thread.currentThread();
        var prevOwner = (Thread) ownerThread.getAndSet(obj, currentThread);
        if (prevOwner == null) {
            return;
        }
        // Tried to acquire a detector that was already owned by a different thread
        throw ThreadingDetector.makeThreadingException(name, prevOwner);
    }

    public void release(Object obj) {
        Thread currentThread = Thread.currentThread();
        var prevOwner = (Thread) ownerThread.getAndSet(obj, null);
        if (prevOwner == currentThread) {
            return;
        }
        // Some other thread tried to acquire while we owned the detector
        throw ThreadingDetector.makeThreadingException(name, prevOwner);
    }
}
