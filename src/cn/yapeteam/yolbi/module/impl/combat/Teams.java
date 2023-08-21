package cn.yapeteam.yolbi.module.impl.combat;

import net.minecraft.entity.player.EntityPlayer;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;

public class Teams extends Module {

    public Teams() {
        super("Teams", ModuleCategory.COMBAT);
    }

    public boolean canAttack(EntityPlayer entity) {
        if (!this.isEnabled()) return true;

        if(mc.thePlayer.getTeam() != null && entity.getTeam() != null) {
            Character targetColor = entity.getDisplayName().getFormattedText().charAt(1);
            Character playerColor = mc.thePlayer.getDisplayName().getFormattedText().charAt(1);
            if(playerColor.equals(targetColor)) {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }

}
