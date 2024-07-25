package cn.yapeteam.yolbi.command;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.command.impl.Bind;
import cn.yapeteam.yolbi.command.impl.Config;
import cn.yapeteam.yolbi.command.impl.Toggle;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.EventChat;
import cn.yapeteam.yolbi.util.misc.LogUtil;

import java.util.ArrayList;

public class CommandManager {

    public final ArrayList<Command> commands = new ArrayList<>();

    public CommandManager() {
        YolBi.instance.getEventManager().register(this);

        commands.add(new Toggle());
        commands.add(new Bind());
        commands.add(new Config());
    }

    @Listener
    public void onChatSend(EventChat event) {
        String message = event.getMessage();

        if(message.startsWith(".")) {
            event.setCancelled(true);

            String commandName = "";

            for(int i = 0; i < message.length(); i++) {
                if(i > 0) {
                    char c = message.charAt(i);

                    if(c == ' ') {
                        break;
                    } else {
                        commandName += c;
                    }
                }
            }

            Command command = getCommandByName(commandName);

            if(command != null) {
                String commandWithoutDot = message.substring(1);
                String commandParts[] = commandWithoutDot.split(" ");

                command.onCommand(commandParts);
            } else {
                LogUtil.addChatMessage("Command not found.");
            }
        }
    }

    public <T extends Command> T getCommandByName(String name) {
        for(Command command : commands) {
            if(command.getName().equalsIgnoreCase(name)) {
                return (T) command;
            } else if(command.getAliases() != null && command.getAliases().length > 0) {
                for(String alias : command.getAliases()) {
                    if(alias.equalsIgnoreCase(name)) {
                        return (T) command;
                    }
                }
            }
        }

        return null;
    }

}