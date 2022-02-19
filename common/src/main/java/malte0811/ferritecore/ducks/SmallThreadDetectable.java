package malte0811.ferritecore.ducks;

/**
 * An object that can be acquired and released similar to the vanilla ThreadingDetector.
 * Methods must only be called while synchronized on this object.
 */
public interface SmallThreadDetectable {
    byte UNLOCKED = 0;
    byte LOCKED = 1;
    byte CRASHING = 2;

    byte ferritecore$getState();

    void ferritecore$setState(byte newState);
}
