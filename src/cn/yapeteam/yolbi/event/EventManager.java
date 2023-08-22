package cn.yapeteam.yolbi.event;

import cn.yapeteam.yolbi.YolBi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventManager {

    private final ArrayList<Object> listeningObjects = new ArrayList<>();
    private final CopyOnWriteArrayList<ListeningMethod> listeningMethods = new CopyOnWriteArrayList<>();

    public void register(Object o) {
        if (!listeningObjects.contains(o)) {
            listeningObjects.add(o);
        }

        updateListeningMethods();
    }

    public void unregister(Object o) {
        listeningObjects.remove(o);

        updateListeningMethods();
    }

    private void updateListeningMethods() {
        listeningMethods.clear();
        listeningObjects.forEach(o -> Arrays.stream(o.getClass().getDeclaredMethods()).filter(m -> m.isAnnotationPresent(Listener.class) && m.getParameters().length == 1).forEach(m -> listeningMethods.add(new ListeningMethod(m, o))));
        //for super
        listeningObjects.forEach(o -> Arrays.stream(o.getClass().getSuperclass().getDeclaredMethods()).filter(m -> m.isAnnotationPresent(Listener.class) && m.getParameters().length == 1).forEach(m -> listeningMethods.add(new ListeningMethod(m, o))));
        listeningMethods.sort(Comparator.comparingInt(m -> m.method.getAnnotation(Listener.class).value()));
    }

    public void post(Event e) {
        if (YolBi.instance.isDestructed()) return;

        listeningMethods.forEach(m -> Arrays.stream(m.method.getParameters()).filter(p -> p.getType().equals(e.getClass())).forEach(p -> {
            try {
                m.method.invoke(m.instance, e);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        }));
    }

    private static class ListeningMethod {
        private final Method method;
        private final Object instance;

        private ListeningMethod(Method method, Object instance) {
            method.setAccessible(true);
            this.method = method;
            this.instance = instance;
        }
    }
}