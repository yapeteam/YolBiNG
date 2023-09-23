package cn.yapeteam.yolbi.module;

import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class ModuleManager {

    public final List<Module> modules = new ArrayList<>();
    public List<HUDModule> hudModules;

    public ModuleManager() {
        Reflections reflections = new Reflections(getClass().getPackage().getName());
        for (Class<? extends Module> aClass : reflections.getSubTypesOf(Module.class)) {
            if (aClass.getAnnotation(Deprecated.class) == null && aClass.getAnnotation(ModuleInfo.class) != null) {
                try {
                    Module module = aClass.getConstructor().newInstance();
                    ModuleInfo info = aClass.getAnnotation(ModuleInfo.class);
                    if (module.getName() == null)
                        module.setName(info.name());
                    if (module.getCategory() == null)
                        module.setCategory(info.category());
                    if (module.getKey() == 0)
                        module.setKey(info.key());
                    if (module.listenType == null)
                        module.listenType = EventListenType.AUTOMATIC;
                    modules.add(module);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    System.err.println("Failed to load Module: " + aClass.getSimpleName());
                }
            }
        }
        modules.sort((m1, m2) -> -Integer.compare(m2.getName().charAt(0), m1.getName().charAt(0)));
        hudModules = modules.stream().filter(HUDModule.class::isInstance).map(HUDModule.class::cast).collect(Collectors.toList());
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        return (T) modules.stream().filter(m -> m.getClass().equals(clazz)).findFirst().orElse(null);
    }

    public <T extends Module> T getModuleByName(String name) {
        return (T) modules.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public <T extends Module> T getModuleByNameNoSpace(String name) {
        return (T) modules.stream().filter(m -> m.getName().replace(" ", "_").equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<Module> getModulesByCategory(ModuleCategory category) {
        return modules.stream().filter(m -> m.getCategory() == category).collect(Collectors.toList());
    }
}