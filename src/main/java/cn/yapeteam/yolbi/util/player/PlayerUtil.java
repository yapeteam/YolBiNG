package cn.yapeteam.yolbi.util.player;

import cn.yapeteam.yolbi.util.network.PacketUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.network.play.client.C03PacketPlayer;
import cn.yapeteam.yolbi.util.IMinecraft;
import net.minecraft.util.Vec3;

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
    public static double getDistanceToEntity(EntityLivingBase entity) {
        Vec3 playerVec = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

        double yDiff = mc.thePlayer.posY - entity.posY;

        double targetY = yDiff > 0 ? entity.posY + entity.getEyeHeight() : -yDiff < mc.thePlayer.getEyeHeight() ? mc.thePlayer.posY + mc.thePlayer.getEyeHeight() : entity.posY;

        Vec3 targetVec = new Vec3(entity.posX, targetY, entity.posZ);

        return playerVec.distanceTo(targetVec) - 0.3F;
    }
}