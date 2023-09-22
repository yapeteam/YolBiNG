package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import net.minecraft.entity.player.EntityPlayer;

@ModuleInfo(name = "Teams", category = ModuleCategory.COMBAT)
public class Teams extends Module {
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canAttack(EntityPlayer entity) {
        if (!this.isEnabled()) return true;

        if (mc.thePlayer.getTeam() != null && entity.getTeam() != null) {
            Character targetColor = entity.getDisplayName().getFormattedText().charAt(1);
            Character playerColor = mc.thePlayer.getDisplayName().getFormattedText().charAt(1);
            return !playerColor.equals(targetColor);
        } else {
            return false;
        }
    }
}
