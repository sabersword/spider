package com.ypq;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class EventHandler {
    private List<Event> eventList;

    public EventHandler() {
        eventList = new ArrayList<Event>();
    }

    public void addEvent(Object object, String methodName, Object... args) {
        eventList.add(new Event(object, methodName, args));
    }

    public void notifyX() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for (Event e : eventList) {
            e.invoke();
        }
    }
}
