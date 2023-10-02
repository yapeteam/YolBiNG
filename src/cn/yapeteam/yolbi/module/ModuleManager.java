package cn.yapeteam.yolbi.module;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventKey;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.event.impl.network.EventChat;
import cn.yapeteam.yolbi.event.impl.network.EventFinalPacketSend;
import cn.yapeteam.yolbi.event.impl.network.EventPacketReceive;
import cn.yapeteam.yolbi.event.impl.network.EventPacketSend;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.event.impl.render.EventRender3D;
import cn.yapeteam.yolbi.util.misc.StringUtil;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class ModuleManager {

    public final List<Module> modules = new ArrayList<>();
    public List<HUDModule> hudModules;
    private final File scriptDir, externalDir;

    public ModuleManager() {
        Reflections reflections = new Reflections("cn.yapeteam.yolbi.module");
        for (Class<? extends Module> aClass : reflections.getSubTypesOf(Module.class))
            registerModule(aClass);
        try {
            Field classes = ClassLoader.class.getDeclaredField("classes");
            classes.setAccessible(true);
            Vector<Class<?>> list = (Vector<Class<?>>) classes.get(Minecraft.class.getClassLoader());
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < list.size(); i++) {
                Class<?> aClass = list.get(i);
                if (aClass.getName().startsWith("cn.yapeteam.yolbi.module"))
                    registerModule((Class<? extends Module>) aClass);
            }
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }

        externalDir = new File(YolBi.instance.getFileSystem().getYolbiDir(), "externals");
        if (!externalDir.exists()) {
            if (!externalDir.mkdir())
                System.err.println("Failed to create externalDir.");
        } else loadExternalModules();

        scriptDir = new File(YolBi.instance.getFileSystem().getYolbiDir(), "scripts");
        if (!scriptDir.exists()) {
            if (!scriptDir.mkdir())
                System.err.println("Failed to create scriptDir.");
        } else loadScriptModules();

        modules.sort((m1, m2) -> -Integer.compare(m2.getName().charAt(0), m1.getName().charAt(0)));
        hudModules = modules.stream().filter(HUDModule.class::isInstance).map(HUDModule.class::cast).collect(Collectors.toList());
    }

    private void registerModule(@NotNull Class<? extends Module> aClass) {
        if (aClass.getAnnotation(Deprecated.class) == null && aClass.getAnnotation(ModuleInfo.class) != null && modules.stream().noneMatch(module -> module.getClass() == aClass)) {
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
                e.printStackTrace();
            }
        }
    }

    private void loadExternalModules() {
        try {
            Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            defineClass.setAccessible(true);
            for (File file : Arrays.stream(Objects.requireNonNull(externalDir.listFiles())).filter(f -> f.getName().endsWith(".class")).collect(Collectors.toList())) {
                try {
                    byte[] classBytes = Files.readAllBytes(file.toPath());
                    Class<?> clazz = (Class<?>) defineClass.invoke(Minecraft.class.getClassLoader(), null, classBytes, 0, classBytes.length);
                    if (clazz.isAnnotationPresent(ModuleInfo.class))
                        registerModule((Class<? extends Module>) clazz);
                } catch (IOException | IllegalAccessException | InvocationTargetException e) {
                    System.err.println("Failed to load ExternalModule: " + file.getName());
                }
            }
        } catch (NoSuchMethodException e) {
            System.err.println("Failed to get Method 'defineClass' from java/lang/ClassLoader");
        }
    }

    private void loadScriptModules() {
        List<File> files = Arrays.stream(Objects.requireNonNull(scriptDir.listFiles())).filter(f -> f.getName().endsWith(".spt")).collect(Collectors.toList());
        for (File file : files) {
            try {
                ScriptModule module = new ScriptModule(StringUtil.readString(Files.newInputStream(file.toPath()))) {
                    {
                        getScript().runBlock("init");
                    }

                    @Listener
                    private void onKey(EventKey e) {
                        getScript().runBlock("onKey");
                        getScript().getObjectsPool().put("event", e);
                    }

                    @Listener
                    private void onTick(EventTick e) {
                        getScript().runBlock("onTick");
                        getScript().getObjectsPool().put("event", e);
                    }

                    @Listener
                    private void onChat(EventChat e) {
                        getScript().runBlock("onChat");
                        getScript().getObjectsPool().put("event", e);
                    }

                    @Listener
                    private void onFinalPacketSend(EventFinalPacketSend e) {
                        getScript().runBlock("onFinalPacketSend");
                        getScript().getObjectsPool().put("event", e);
                    }

                    @Listener
                    private void onPacketReceive(EventPacketReceive e) {
                        getScript().runBlock("onPacketReceive");
                        getScript().getObjectsPool().put("event", e);
                    }

                    @Listener
                    private void onPacketSend(EventPacketSend e) {
                        getScript().runBlock("onPacketSend");
                        getScript().getObjectsPool().put("event", e);
                    }

                    @Listener
                    private void onMotion(EventRender2D e) {
                        getScript().runBlock("onMotion");
                        getScript().getObjectsPool().put("event", e);
                    }

                    @Listener
                    private void onRender2D(EventRender2D e) {
                        getScript().runBlock("onRender2D");
                        getScript().getObjectsPool().put("event", e);
                    }

                    @Listener
                    private void onRender3D(EventRender3D e) {
                        getScript().runBlock("onRender2D");
                        getScript().getObjectsPool().put("event", e);
                    }
                };
                if (modules.stream().anyMatch(m -> m.getName().equals(file.getName().replace(".spt", "")))) {
                    System.err.println("Name duplicate: " + file.getName() + ", this script will not be loaded.");
                    continue;
                }
                module.setName(file.getName().replace(".spt", ""));
                module.setCategory(ModuleCategory.SCRIPT);
                module.listenType = EventListenType.AUTOMATIC;
                modules.add(module);
            } catch (IOException e) {
                System.err.println("Failed to load Script: " + file.getName());
            }
        }
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