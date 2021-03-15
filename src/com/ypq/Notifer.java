package com.ypq;

import java.lang.reflect.InvocationTargetException;

public class Notifer {
    private EventHandler eventHandler;

    public Notifer() {
        eventHandler = new EventHandler();
    }

    public EventHandler getEventHandler() {
        return this.eventHandler;
    }

    public void addListener(Object object, String methodName, Object... args) {
        this.getEventHandler().addEvent(object, methodName, args);
    }

    public void notifyX() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        this.getEventHandler().notifyX();
    }
}
