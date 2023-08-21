package cn.yapeteam.yolbi.command.impl;

import org.lwjgl.input.Keyboard;
import cn.yapeteam.yolbi.Vestige;
import cn.yapeteam.yolbi.command.Command;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.util.misc.LogUtil;

public class Bind extends Command {

    public Bind() {
        super("Bind", "Changes the keybind of the specified module.");
    }

    @Override
    public void onCommand(String[] args) {
        if(args.length >= 3) {
            Module module = Vestige.instance.getModuleManager().getModuleByNameNoSpace(args[1]);

            if(module != null) {
                String keyName = args[2].toUpperCase();

                module.setKey(Keyboard.getKeyIndex(keyName));

                LogUtil.addChatMessage("Bound " + module.getName() + " to " + keyName);
            }
        } else {
            LogUtil.addChatMessage("Usage : .bind module keybind");
        }
    }
}