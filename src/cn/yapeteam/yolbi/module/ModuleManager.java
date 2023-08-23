package cn.yapeteam.yolbi.module;

import cn.yapeteam.yolbi.module.impl.combat.*;
import cn.yapeteam.yolbi.module.impl.exploit.Disabler;
import cn.yapeteam.yolbi.module.impl.exploit.StrafeConverter;
import cn.yapeteam.yolbi.module.impl.misc.AnticheatModule;
import cn.yapeteam.yolbi.module.impl.misc.Autoplay;
import cn.yapeteam.yolbi.module.impl.misc.NoteBot;
import cn.yapeteam.yolbi.module.impl.misc.SelfDestruct;
import cn.yapeteam.yolbi.module.impl.movement.*;
import cn.yapeteam.yolbi.module.impl.player.*;
import cn.yapeteam.yolbi.module.impl.visual.*;
import cn.yapeteam.yolbi.module.impl.world.AutoBridge;
import cn.yapeteam.yolbi.module.impl.world.Breaker;
import cn.yapeteam.yolbi.module.impl.world.Scaffold;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class ModuleManager {

    public final List<Module> modules = new ArrayList<>();
    public List<HUDModule> hudModules;

    public ModuleManager() {
        modules.add(new Killaura());
        modules.add(new Velocity());
        modules.add(new TargetStrafe());
        modules.add(new Teams());
        modules.add(new Backtrack());
        modules.add(new TickBase());
        modules.add(new Reach());
        modules.add(new Autoclicker());
        modules.add(new AimAssist());
        modules.add(new DelayRemover());
        modules.add(new WTap());
        modules.add(new Criticals());
        modules.add(new AntiBot());

        modules.add(new Sprint());
        modules.add(new Fly());
        modules.add(new Speed());
        modules.add(new Longjump());
        modules.add(new InventoryMove());
        modules.add(new Noslow());
        modules.add(new Blink());
        modules.add(new Safewalk());
        modules.add(new Step());

        modules.add(new ChestStealer());
        modules.add(new InventoryManager());
        modules.add(new NoFall());
        modules.add(new AntiVoid());
        modules.add(new Timer());
        modules.add(new FastPlace());
        modules.add(new AutoTool());
        modules.add(new Autoplace());
        modules.add(new AutoGapple());
        modules.add(new Scaffold());
        modules.add(new AutoBridge());
        modules.add(new Breaker());

        modules.add(new Watermark());
        modules.add(new ModuleList());
        modules.add(new IngameInfo());
        modules.add(new ClientTheme());
        //modules.add(new ClickGuiModule());
        modules.add(new ClickUI());
        modules.add(new ESP());
        modules.add(new ItemPhysic());
        modules.add(new Chams());
        modules.add(new Animations());
        modules.add(new Rotations());
        modules.add(new TargetHUD());
        modules.add(new Keystrokes());
        modules.add(new Mobends());
        modules.add(new Freelook());
        modules.add(new Ambience());
        modules.add(new Fullbright());
        modules.add(new NameProtect());
        modules.add(new Xray());
        modules.add(new Particles());
        modules.add(new Disabler());
        modules.add(new StrafeConverter());

        modules.add(new AnticheatModule());
        modules.add(new Autoplay());
        modules.add(new SelfDestruct());
        modules.add(new NoteBot());

        hudModules = modules.stream().filter(HUDModule.class::isInstance).map(HUDModule.class::cast).collect(Collectors.toList());
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        return (T) modules.stream().filter(m -> m.getClass().equals(clazz)).findFirst().orElse(null);
    }

    public <T extends Module> T getModuleByName(String name) {
        return (T) modules.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public <T extends Module> T getModuleByNameNoSpace(String name) {
        return (T) modules.stream().filter(m -> m.getName().replace(" ", "").equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<Module> getModulesByCategory(ModuleCategory category) {
        return modules.stream().filter(m -> m.getCategory() == category).collect(Collectors.toList());
    }
}