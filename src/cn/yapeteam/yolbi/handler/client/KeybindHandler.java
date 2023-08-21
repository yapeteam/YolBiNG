package cn.yapeteam.yolbi.handler.client;

import cn.yapeteam.yolbi.Vestige;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.KeyPressEvent;

public class KeybindHandler {

    public KeybindHandler() {
        Vestige.instance.getEventManager().register(this);
    }

    @Listener
    public void onKeyPress(KeyPressEvent event) {
        Vestige.instance.getModuleManager().modules.stream().filter(m -> m.getKey() == event.getKey()).forEach(m -> m.toggle());
    }

}