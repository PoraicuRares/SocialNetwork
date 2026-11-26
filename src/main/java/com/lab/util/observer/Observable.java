package com.lab.util.observer;

/**
 * An object that tells its observers when its data changes.
 */
public interface Observable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
}
