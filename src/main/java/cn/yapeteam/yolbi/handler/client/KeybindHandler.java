package cn.yapeteam.yolbi.handler.client;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventKey;
import cn.yapeteam.yolbi.module.Module;

public class KeybindHandler {

    public KeybindHandler() {
        YolBi.instance.getEventManager().register(this);
    }

    @Listener
    public void onKeyPress(EventKey event) {
        YolBi.instance.getModuleManager().modules.stream().filter(m -> m.getKey() == event.getKey()).forEach(Module::toggle);
    }
}