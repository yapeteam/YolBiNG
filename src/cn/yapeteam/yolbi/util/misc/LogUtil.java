package cn.yapeteam.yolbi.util.misc;

import net.minecraft.util.ChatComponentText;
import cn.yapeteam.yolbi.Vestige;
import cn.yapeteam.yolbi.util.IMinecraft;

public class LogUtil implements IMinecraft {

    private static final String prefix = "[" + Vestige.instance.name + "]";

    public static void print(Object message) {
        System.out.println(prefix + " " + message);
    }

    public static void addChatMessage(String message) {
        mc.thePlayer.addChatMessage(new ChatComponentText(message));
    }

}