package com.lab.util.observer;

/**
 * Is told by an {@link Observable} that its data changed.
 */
public interface Observer {
    /**
     * Called by the observable after a change.
     */
    void update();
}
