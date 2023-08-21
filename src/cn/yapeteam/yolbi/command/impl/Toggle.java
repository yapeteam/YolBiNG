package cn.yapeteam.yolbi.command.impl;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.command.Command;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.util.misc.LogUtil;

public class Toggle extends Command {

    public Toggle() {
        super("Toggle", "Turns on or off the specified module.", "t");
    }

    @Override
    public void onCommand(String[] args) {
        if(args.length >= 2) {
            Module module = YolBi.instance.getModuleManager().getModuleByNameNoSpace(args[1]);

            if(module != null) {
                module.toggle();

                LogUtil.addChatMessage((module.isEnabled() ? "Enabled " : "Disabled ") + module.getName());
            }
        } else {
            LogUtil.addChatMessage("Usage : .t/toggle modulename");
        }
    }
}