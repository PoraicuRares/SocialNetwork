package com.lab.util.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps the list of observers, so that a class becomes {@link Observable} only by extending this one
 */
public abstract class AbstractObservable implements Observable{
    private final List<Observer> observers = new ArrayList<>();
    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for(Observer observer : observers){
            observer.update();
        }
    }
}
