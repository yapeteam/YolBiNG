package cn.yapeteam.yolbi.util.player;

import cn.yapeteam.yolbi.util.network.PacketUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.network.play.client.C03PacketPlayer;
import cn.yapeteam.yolbi.util.IMinecraft;

public class PlayerUtil implements IMinecraft {

    public static void ncpDamage() {
        for(int i = 0; i < 49; i++) {
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625, mc.thePlayer.posZ, false));
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        }

        PacketUtil.sendPacketNoEvent(new C03PacketPlayer(true));
    }

    public static boolean isBlockBlacklisted(Item item) {
        return item instanceof ItemAnvilBlock || item.getUnlocalizedName().contains("sand") || item.getUnlocalizedName().contains("gravel") || item.getUnlocalizedName().contains("ladder") || item.getUnlocalizedName().contains("tnt") || item.getUnlocalizedName().contains("chest") || item.getUnlocalizedName().contains("web");
    }

}