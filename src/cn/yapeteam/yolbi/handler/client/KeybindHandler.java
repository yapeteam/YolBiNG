package cn.yapeteam.yolbi.handler.client;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.KeyPressEvent;

public class KeybindHandler {

    public KeybindHandler() {
        YolBi.instance.getEventManager().register(this);
    }

    @Listener
    public void onKeyPress(KeyPressEvent event) {
        YolBi.instance.getModuleManager().modules.stream().filter(m -> m.getKey() == event.getKey()).forEach(m -> m.toggle());
    }

}