package cn.yapeteam.yolbi.command.impl;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.command.Command;
import cn.yapeteam.yolbi.util.misc.LogUtil;

public class Config extends Command {

    public Config() {
        super("Config", "Loads or saves a config.");
    }

    @Override
    public void onCommand(String[] args) {
        if(args.length >= 3) {
            String action = args[1].toLowerCase();
            String configName = args[2];

            switch (action) {
                case "load":
                    boolean success = YolBi.instance.getFileSystem().loadConfig(configName, false);

                    if(success) {
                        LogUtil.addChatMessage("Loaded config " + configName);
                    } else {
                        LogUtil.addChatMessage("Config not found.");
                    }
                    break;
                case "save":
                    YolBi.instance.getFileSystem().saveConfig(configName);

                    LogUtil.addChatMessage("Saved config " + configName);
                    break;
            }
        }
    }
}
