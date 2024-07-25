package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import net.minecraft.block.Block;

@ModuleInfo(name = "Xray", category = ModuleCategory.VISUAL)
public class Xray extends Module {
    private static Xray xray;

    public static Xray getInstance() {
        return xray;
    }

    private float oldGamma;

    private final int[] blockIds = {14, 15, 56, 129};

    public Xray() {
        xray = this;
    }

    @Override
    public void onEnable() {
        oldGamma = mc.gameSettings.gammaSetting;

        mc.gameSettings.gammaSetting = 10F;
        mc.gameSettings.ambientOcclusion = 0;

        mc.renderGlobal.loadRenderers();
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = oldGamma;
        mc.gameSettings.ambientOcclusion = 1;

        mc.renderGlobal.loadRenderers();
    }

    public boolean shouldRenderBlock(Block block) {
        for (int id : blockIds) {
            if (block == Block.getBlockById(id)) {
                return true;
            }
        }

        return false;
    }

}
